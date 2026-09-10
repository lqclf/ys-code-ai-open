package com.eric.dict.aspect;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.common.annotation.dict.DictTranslate;
import com.eric.common.response.ResponseResult;
import com.eric.common.utils.JsonUtils;
import com.eric.dict.module.entity.SysDictDetail;
import com.eric.dict.module.service.SysDictDetailService;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * 字典切面处理
 * @ClassName:  DictAspect     
 * @author:     liuQ 
 * @date:       2025-07-29 09:33:32
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Aspect
@Component
@Slf4j
@Order(1)
public class DictAspect {

    /**
     * 字典详情服务，用于从数据库查询字典数据
     * 使用static修饰，供静态方法 refreshDictCache 使用
     */
    private static SysDictDetailService dictDetailService;

    /**
     * 字典缓存读写锁，用于保证多线程环境下字典缓存操作的线程安全性
     * 采用读写锁机制，支持多读单写，提高并发性能
     */
    private static final ReadWriteLock DICT_CACHE_LOCK = new ReentrantReadWriteLock();

    /**
     * 字典缓存映射表
     * Key: 字典编码（dictCode）
     * Value: 字典项映射（字典项code -> 字典项name）
     * 使用ConcurrentHashMap保证线程安全，初始容量64
     */
    private static final Map<String, Map<String, String>> DICT_CACHE = new ConcurrentHashMap<>(64);

    /**
     * 字典缓存时间戳映射表
     * Key: 字典编码（dictCode）
     * Value: 缓存更新时间戳（毫秒）
     * 用于记录每个字典类型的最后更新时间，便于监控和调试
     */
    private static final Map<String, Long> DICT_CACHE_TIMESTAMP = new ConcurrentHashMap<>(64);

    /**
     * 字典缓存版本映射表
     * Key: 字典编码（dictCode）
     * Value: 缓存版本号（递增）
     * 用于检测字典数据是否发生变化，支持增量更新
     */
    private static final Map<String, Long> DICT_CACHE_VERSION = new ConcurrentHashMap<>(64);

    /**
     * 缓存刷新间隔时间（毫秒）
     * 默认5分钟刷新一次字典缓存，确保数据时效性
     */
    private static final long CACHE_REFRESH_INTERVAL = 5 * 60 * 1000;

    /**
     * 缓存初始化标志
     * 使用volatile关键字保证多线程环境下的可见性
     * true表示缓存已初始化完成，false表示未初始化
     */
    private static volatile boolean cacheInitialized = false;

    /**
     * 初始化尝试标志
     * 使用volatile关键字保证多线程环境下的可见性
     * true表示已经尝试过初始化（无论成功或失败），false表示未尝试过
     * 用于避免在初始化失败后重复尝试
     */
    private static volatile boolean initAttempted = false;

    /**
     * 初始化锁对象
     * 用于确保缓存初始化操作的线程安全性
     * 防止多个线程同时触发初始化操作
     */
    private static final Object INIT_LOCK = new Object();

    /**
     * 刷新锁对象
     * 用于确保缓存刷新操作的线程安全性
     * 防止多个线程同时触发刷新操作
     */
    private static final Object REFRESH_LOCK = new Object();

    /**
     * 翻译字段缓存
     * Key: 类的Class对象
     * Value: 该类中所有需要翻译的字段列表（带有@DictTranslate注解的字段）
     * 使用缓存避免每次都通过反射获取字段，提高性能
     */
    private static final Map<Class<?>, List<Field>> TRANSLATE_FIELD_CACHE = new ConcurrentHashMap<>(64);

    /**
     * 不需要序列化的类型集合
     * 包含基本类型包装类、Web相关类型等，这些类型不需要进行字典翻译处理
     * 用于快速过滤掉不需要处理的对象类型
     */
    private static final Set<Class<?>> NON_SERIALIZABLE_TYPES = new HashSet<>(Arrays.asList(
            String.class, Number.class, Boolean.class,
            ModelAndView.class, ServletRequest.class, ServletResponse.class,
            HttpSession.class, MultipartFile.class, ServletContext.class
    ));

    /**
     * 初始化方法，在Bean构造完成后自动执行
     * 使用@PostConstruct注解标记，Spring容器会在Bean初始化阶段调用此方法
     * 主要功能：注入静态依赖、加载字典数据到缓存中，为后续的字典翻译做准备
     */
    @Autowired
    public void init(SysDictDetailService service) {
        dictDetailService = service;
        log.info("字典切面初始化完成，缓存将在首次使用时加载");
    }

    /**
     * 销毁方法，在Bean销毁前自动执行
     * 使用@PreDestroy注解标记，Spring容器会在Bean销毁阶段调用此方法
     * 主要功能：清理字典缓存资源，释放内存
     */
    @PreDestroy
    public void destroy() {
        try {
            // 获取写锁，确保清理操作的原子性
            DICT_CACHE_LOCK.writeLock().lock();
            // 清空字典缓存
            DICT_CACHE.clear();
            // 清空时间戳缓存
            DICT_CACHE_TIMESTAMP.clear();
            // 清空版本号缓存
            DICT_CACHE_VERSION.clear();
            log.info("字典缓存已清理");
        } finally {
            // 确保锁被释放，避免死锁
            DICT_CACHE_LOCK.writeLock().unlock();
        }
    }

    /**
     * 定时刷新缓存方法
     * 使用@Scheduled注解标记，按照固定间隔定时执行
     * fixedDelay: 上一次执行完成后延迟指定时间再执行下一次
     * 主要功能：定期刷新字典缓存，确保缓存数据的时效性
     */
    /*@Scheduled(fixedDelay = CACHE_REFRESH_INTERVAL)
    public void scheduledRefreshCache() {
        if (cacheInitialized) {
            log.debug("开始定时刷新字典缓存...");
            // 设置忽略数据权限标志，防止定时任务线程触发数据权限拦截器导致SaToken上下文异常
            com.eric.common.config.datapermission.DataPermissionContextHolder.setIgnore(true);
            try {
                refreshDictCache();
            } finally {
                // 清理忽略标志，防止影响其他线程
                com.eric.common.config.datapermission.DataPermissionContextHolder.clear();
            }
        }
    }*/

    /**
     * 确保字典缓存已初始化
     * 采用延迟初始化策略，在首次使用时才加载字典缓存
     * 避免在Spring容器初始化阶段访问数据库，防止与SaToken上下文初始化冲突
     * 使用双重检查锁定机制确保线程安全
     * 标记为public static，供外部工具类（如DictTranslateUtil）在非Controller场景下主动触发缓存加载
     */
    public static void ensureCacheInitialized() {
        // 双重检查：先检查是否已初始化或已尝试过初始化
        if (!cacheInitialized && !initAttempted) {
            // 使用同步块确保只有一个线程执行初始化
            synchronized (INIT_LOCK) {
                // 再次检查，防止在等待锁期间其他线程已经完成初始化
                if (!cacheInitialized && !initAttempted) {
                    // 标记已尝试初始化，避免重复尝试
                    initAttempted = true;
                    try {
                        // 执行缓存刷新逻辑，从数据库加载字典数据
                        refreshDictCache();
                        // 标记缓存已初始化完成
                        cacheInitialized = true;
                    } catch (Exception e) {
                        // 初始化失败时记录错误日志，并重置尝试标志
                        log.error("字典缓存初始化失败", e);
                        initAttempted = false;
                    }
                }
            }
        }
    }

    /**
     * 刷新字典缓存的核心方法
     * 从数据库重新加载所有字典数据，并更新到缓存中
     * 使用写锁保证整个刷新过程的原子性和线程安全
     * 标记为public static，供外部工具类在非Controller场景下主动刷新缓存
     */
    public static void refreshDictCache() {
        try {
            // 获取写锁，防止在刷新过程中有其他线程读取或修改缓存
            DICT_CACHE_LOCK.writeLock().lock();
            
            // 临时存储从数据库查询到的字典数据
            // Key: 字典编码, Value: 该字典下的所有字典项列表
            Map<String, List<SysDictDetail>> dictMap = new HashMap<>();
            
            // 从数据库查询所有字典详情数据
            for (SysDictDetail dictDetail : dictDetailService.list()) {
                // 校验字典数据的完整性，确保必要字段不为空
                if (dictDetail.getDictCode() != null && dictDetail.getCode() != null && dictDetail.getName() != null) {
                    // 按字典编码分组存储字典项
                    dictMap.computeIfAbsent(dictDetail.getDictCode(), k -> new ArrayList<>())
                           .add(dictDetail);
                }
            }

            // 构建新的缓存映射
            Map<String, Map<String, String>> newCache = new ConcurrentHashMap<>(dictMap.size());
            Map<String, Long> newTimestamp = new ConcurrentHashMap<>(dictMap.size());
            Map<String, Long> newVersion = new ConcurrentHashMap<>(dictMap.size());
            long currentTime = System.currentTimeMillis();

            // 遍历所有字典类型，构建code到name的映射关系
            dictMap.forEach((dictCode, details) -> {
                // 为每个字典类型创建一个code->name的映射
                Map<String, String> codeToNameMap = new ConcurrentHashMap<>(details.size());
                details.forEach(detail -> {
                    // 再次校验数据完整性
                    if (detail.getCode() != null && detail.getName() != null) {
                        codeToNameMap.put(detail.getCode(), detail.getName());
                    }
                });
                // 将构建好的映射放入新缓存
                newCache.put(dictCode, codeToNameMap);
                // 记录该字典类型的缓存更新时间
                newTimestamp.put(dictCode, currentTime);
                // 递增版本号
                Long currentVersion = DICT_CACHE_VERSION.getOrDefault(dictCode, 0L);
                newVersion.put(dictCode, currentVersion + 1);
            });

            // 采用先清空再添加的方式更新缓存，确保数据一致性
            DICT_CACHE.clear();
            DICT_CACHE.putAll(newCache);
            DICT_CACHE_TIMESTAMP.clear();
            DICT_CACHE_TIMESTAMP.putAll(newTimestamp);
            DICT_CACHE_VERSION.clear();
            DICT_CACHE_VERSION.putAll(newVersion);

            log.debug("字典缓存刷新完成，共 {} 个字典类型", DICT_CACHE.size());
        } catch (Exception e) {
            log.error("刷新字典缓存失败", e);
        } finally {
            // 确保锁被释放，避免死锁
            DICT_CACHE_LOCK.writeLock().unlock();
        }
    }

    /**
     * 从缓存中获取指定字典类型的映射数据
     * @param dictCode 字典编码
     * @return 字典项映射（code -> name），如果不存在则返回null
     */
    private Map<String, String> getDictFromCache(String dictCode) {
        try {
            DICT_CACHE_LOCK.readLock().lock();
            return DICT_CACHE.get(dictCode);
        } finally {
            DICT_CACHE_LOCK.readLock().unlock();
        }
    }

    /**
     * 供外部工具类调用的字典翻译静态方法
     * 直接从内存缓存读取，无Spring上下文依赖
     * 适用于导出处理器等非Controller层场景
     * @param dictCode 字典编码
     * @param code 字典项编码
     * @return 翻译后的名称，未找到返回空字符串
     */
    public static String getDictName(String dictCode, String code) {
        if (dictCode == null || code == null || code.isEmpty()) {
            return "";
        }
        try {
            DICT_CACHE_LOCK.readLock().lock();
            Map<String, String> codeMap = DICT_CACHE.get(dictCode);
            if (codeMap == null) {
                return "";
            }
            return codeMap.getOrDefault(code, "");
        } finally {
            DICT_CACHE_LOCK.readLock().unlock();
        }
    }

    /**
     * 供外部工具类调用的字典映射获取静态方法
     * 返回指定字典编码的完整code→name映射表，用于批量翻译场景
     * @param dictCode 字典编码
     * @return 字典项映射，不存在或未初始化返回空Map
     */
    public static Map<String, String> getDictMap(String dictCode) {
        if (dictCode == null) {
            return Collections.emptyMap();
        }
        try {
            DICT_CACHE_LOCK.readLock().lock();
            Map<String, String> codeMap = DICT_CACHE.get(dictCode);
            if (codeMap == null) {
                return Collections.emptyMap();
            }
            return codeMap;
        } finally {
            DICT_CACHE_LOCK.readLock().unlock();
        }
    }

    /**
     * 检查字典缓存是否已初始化
     * @return true-已初始化，false-未初始化
     */
    public static boolean isCacheInitialized() {
        return cacheInitialized;
    }

    /**
     * 从缓存中获取指定字典类型的更新时间戳
     * @param dictCode 字典编码
     * @return 缓存更新时间戳（毫秒），如果不存在则返回null
     */
    private Long getDictCacheTimestamp(String dictCode) {
        try {
            // 获取读锁，允许多个线程同时读取
            DICT_CACHE_LOCK.readLock().lock();
            return DICT_CACHE_TIMESTAMP.get(dictCode);
        } finally {
            // 确保锁被释放
            DICT_CACHE_LOCK.readLock().unlock();
        }
    }
    /**
     * 注解位置-切入点
     * 定义切面拦截的位置，拦截所有带有@AutoDict注解的方法
     * 目前只支持通过注解开启字典翻译功能
     * 如需支持其他方式，可以使用 || 运算符追加其他切入点表达式
     */
    @Pointcut("@annotation(com.eric.common.annotation.dict.AutoDict)")
    public void dictPoinCut() {}

    /**
     * 环绕通知方法，拦截返回值并执行字典转换
     * 在目标方法执行完成后，对返回结果进行后置处理，实现字典值的自动翻译
     * 使用@Around而不是@AfterReturning，确保在脱敏处理之前执行字典翻译
     * @param point 切入点信息，包含方法调用的相关信息
     * @return 处理后的返回结果
     * @throws Throwable 执行过程中可能抛出的异常
     */
    @Around("dictPoinCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();
        translateDict(result);
        return result;
    }


    /**
     * 核心处理方法：根据返回结果类型执行对应的翻译逻辑
     * 支持多种返回值类型的字典翻译：
     * 1. ResponseResult<?> - 标准响应结果包装类，翻译其中的data字段
     * 2. Page<?> - 分页数据，翻译分页结果中的每条记录
     * 3. List<?> - 列表数据，翻译列表中的每个元素
     * 4. Set<?> - 集合数据，翻译集合中的每个元素
     * 5. Map<?, ?> - 字典数据，翻译Map中每个值
     * @param result 原始返回结果
     * @throws Exception 处理异常
     */
    public void translateDict(Object result) {
        // 处理ResponseResult包装类型
        if (result instanceof ResponseResult) {
            ResponseResult<?> resultWrapper = (ResponseResult<?>) result;
            Object data = resultWrapper.getData();

            // 只处理可序列化的数据
            if (isSerializable(data)) {
                Object translatedData = translateData(data);
                if (translatedData != data) {
                    ((ResponseResult) result).setData(translatedData);
                }
            }
        } else {
            // 处理直接返回的数据结构
            Object translatedResult = translateData(result);
            if (translatedResult != result) {
                // 对于直接返回的数据结构，需要特殊处理
                // 由于Java的引用传递特性，需要修改原对象的内容
                if (result instanceof List) {
                    ((List<?>) result).clear();
                    ((List<?>) result).addAll((List) translatedResult);
                } else if (result instanceof Set) {
                    ((Set<?>) result).clear();
                    ((Set<?>) result).addAll((Set) translatedResult);
                } else if (result instanceof Map) {
                    ((Map<?, ?>) result).clear();
                    ((Map<?, ?>) result).putAll((Map) translatedResult);
                }
            }
        }
    }

    /**
     * 统一数据翻译处理方法
     * 根据不同的数据类型调用相应的翻译逻辑
     * 在首次调用时会触发字典缓存的延迟初始化
     * @param data 原始数据
     * @return 翻译后的数据
     */
    private Object translateData(Object data) {
        // 确保字典缓存已初始化（延迟初始化策略）
        ensureCacheInitialized();

        // 检查缓存是否已初始化，如果未初始化则跳过翻译
        if (!cacheInitialized) {
            log.warn("字典缓存未初始化，跳过翻译");
            return data;
        }

        if (data instanceof Page) {
            Page<?> pageData = (Page<?>) data;
            // 将分页结果中的每条记录转换为带翻译字段的JSONObject
            List<JSONObject> translatedRecords = pageData.getRecords().stream()
                    .map(this::translateToJSONObject)
                    .collect(Collectors.toList());
            ((Page) data).setRecords(translatedRecords);
            return data;
        } else if (data instanceof List) {
            // 处理列表数据类型
            List<?> listData = (List<?>) data;
            // 将列表中的每个元素转换为带翻译字段的JSONObject
            return listData.stream()
                    .map(this::translateToJSONObject)
                    .collect(Collectors.toList());
        } else if (data instanceof Set) {
            // 处理集合数据类型
            Set<?> setData = (Set<?>) data;
            // 将集合中的每个元素转换为带翻译字段的JSONObject
            return setData.stream()
                    .map(this::translateToJSONObject)
                    .collect(Collectors.toSet());
        } else if (data instanceof Map) {
            // 处理字典数据类型
            Map<?, ?> mapData = (Map<?, ?>) data;
            // 将Map中的每个值转换为带翻译字段的JSONObject
            return mapData.entrySet().stream()
                    .map(entry -> {
                        JSONObject jsonObject = translateToJSONObject(entry.getValue());
                        return new AbstractMap.SimpleEntry<>(String.valueOf(entry.getKey()), jsonObject);
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } else {
            // 处理单个对象：检查是否含有@DictTranslate注解字段，有则进行翻译
            if (data != null && hasTranslateFields(data.getClass())) {
                return translateToJSONObject(data);
            }
            return data;
        }
    }

    /**
     * 将实体类转换为JSON对象并添加翻译字段
     * 使用FastJsonUtils将原始对象转换为JSONObject，然后添加字典翻译字段
     * @param obj 原始对象
     * @return 带有翻译字段的JSON对象
     */
    private JSONObject translateToJSONObject(Object obj) {
        // 空值检查，返回空的JSONObject
        if (obj == null) {
            return new JSONObject();
        }
        try {
            // 使用JsonUtils将对象转换为JSONObject
            JSONObject item = JsonUtils.toObject(obj, JSONObject.class);
            // 如果转换成功，添加翻译字段
            if (item != null) {
                addTranslateFields(item, obj.getClass());
            }
            return item;
        } catch (Exception e) {
            // 转换失败时记录错误日志，返回空的JSONObject
            log.error("JSON转换失败，对象类型：{}", obj.getClass().getName(), e);
            return new JSONObject();
        }
    }

    /**
     * 为JSON对象添加翻译字段
     * 遍历类中所有带有@DictTranslate注解的字段，为每个字段添加对应的翻译字段
     * @param item JSON对象
     * @param clazz 原始对象的类
     */
    private void addTranslateFields(JSONObject item, Class<?> clazz) {
        // 空值检查
        if (item == null) {
            return;
        }
        // 获取类中所有需要翻译的字段（使用缓存提高性能）
        getTranslateFields(clazz).forEach(field -> {
            try {
                // 处理单个字段的翻译逻辑
                processField(item, field);
            } catch (Exception e) {
                // 翻译失败时记录错误日志，但不影响其他字段的翻译
                log.error("字段[{}]翻译失败", field.getName(), e);
            }
        });
    }

    /**
     * 处理单个字段的翻译逻辑
     * 根据字段上的@DictTranslate注解，查找对应的字典值并添加翻译字段
     * @param item JSON对象
     * @param field 需要翻译的字段
     * @throws Exception 处理过程中可能抛出的异常
     */
    private void processField(JSONObject item, Field field) throws Exception {
        // 获取字段名称
        String fieldName = field.getName();
        // 获取字段在JSON中的值
        Object valueNode = item.get(fieldName);
        // 如果字段值为空，则跳过处理
        if (valueNode == null) {
            return;
        }

        // 获取字段上的@DictTranslate注解
        DictTranslate annotation = field.getAnnotation(DictTranslate.class);
        if (annotation == null) {
            return;
        }

        // 获取字典编码
        String dictCode = annotation.dictCode();
        // 校验字典编码是否为空
        if (dictCode == null || dictCode.trim().isEmpty()) {
            log.warn("字段[{}]的dictCode为空，跳过翻译", fieldName);
            return;
        }

        // 从缓存中获取字典数据
        Map<String, String> dictMap = getDictFromCache(dictCode);
        if (dictMap == null || dictMap.isEmpty()) {
            // 字典不存在或为空时，获取缓存时间戳并记录日志
            Long timestamp = getDictCacheTimestamp(dictCode);
            if (timestamp == null) {
                log.warn("字典[{}]不存在于缓存中", dictCode);
            } else {
                log.debug("字典[{}]缓存时间为：{}", dictCode, new Date(timestamp));
            }
            return;
        }

        // 根据字段值查找对应的字典描述
        String desc = dictMap.get(String.valueOf(valueNode));
        // 如果找到对应的描述，则添加翻译字段到JSON对象中
        if (desc != null) {
            item.put(fieldName + "_" + annotation.dictText(), desc);
        }
    }

    /**
     * 获取需要翻译的字段列表（使用缓存）
     * 通过反射获取类及其父类中所有带有@DictTranslate注解的字段
     * 使用缓存机制避免重复反射操作，提高性能
     * @param clazz 需要处理的类
     * @return 需要翻译的字段列表（不可变列表）
     */
    private List<Field> getTranslateFields(Class<?> clazz) {
        // 空值检查
        if (clazz == null) {
            return Collections.emptyList();
        }
        // 使用computeIfAbsent方法实现缓存机制，避免重复反射操作
        return TRANSLATE_FIELD_CACHE.computeIfAbsent(clazz, key -> {
            List<Field> fields = new ArrayList<>();
            Class<?> targetClass = clazz;

            // 遍历类的继承链，查找所有带有@DictTranslate注解的字段
            while (targetClass != null && !targetClass.getName().startsWith("java.")) {
                try {
                    // 遍历当前类声明的所有字段
                    for (Field field : targetClass.getDeclaredFields()) {
                        // 检查字段是否有@DictTranslate注解
                        if (field.isAnnotationPresent(DictTranslate.class)) {
                            // 获取字段的修饰符
                            int modifiers = field.getModifiers();
                            // 只处理非静态、非final的字段
                            if (!Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers)) {
                                // 设置字段可访问，以便获取字段值
                                field.setAccessible(true);
                                // 添加到字段列表
                                fields.add(field);
                            }
                        }
                    }
                } catch (SecurityException e) {
                    // 如果无法访问类的字段，记录警告日志
                    log.warn("无法访问类[{}]的字段", targetClass.getName(), e);
                }
                // 继续处理父类
                targetClass = targetClass.getSuperclass();
            }
            // 返回不可变列表，保证缓存数据的安全性
            return Collections.unmodifiableList(fields);
        });
    }

    /**
     * 判断类（包括其父类）中是否包含带有@DictTranslate注解的字段
     * 复用TRANSLATE_FIELD_CACHE缓存机制，避免重复反射操作
     * @param clazz 需要检查的类
     * @return true表示存在需要翻译的字段，false表示不存在
     */
    private boolean hasTranslateFields(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        List<Field> fields = getTranslateFields(clazz);
        return !fields.isEmpty();
    }

    /**
     * 判断对象是否需要序列化处理
     * 排除一些不需要处理的类型，如基本类型、Web相关类型等
     * @param obj 待判断的对象
     * @return true表示需要序列化处理，false表示不需要
     */
    private boolean isSerializable(Object obj) {
        // null值不需要处理
        if (obj == null) return false;

        Class<?> clazz = obj.getClass();

        // 检查是否为不可序列化的类型
        if (NON_SERIALIZABLE_TYPES.stream().anyMatch(type -> type.isInstance(obj))) {
            return false;
        }

        // 检查是否为代理类或动态生成的类
        if (isProxyOrDynamicClass(obj)) {
            return false;
        }

        // 处理数组类型：递归检查数组元素
        if (clazz.isArray()) {
            // 特殊处理MultipartFile数组
            if (obj instanceof MultipartFile[]) {
                return false;
            }
            // 递归检查数组中的每个元素
            return Arrays.stream((Object[]) obj).anyMatch(this::isSerializable);
        }

        // 处理集合类型：递归检查集合中的每个元素
        if (obj instanceof Collection) {
            Collection<?> collection = (Collection<?>) obj;
            // 空集合不需要处理
            if (collection.isEmpty()) {
                return false;
            }
            // 递归检查集合中的每个元素
            return collection.stream().anyMatch(this::isSerializable);
        }

        // 处理Map类型：递归检查Map中每个值
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            // 空Map不需要处理
            if (map.isEmpty()) {
                return false;
            }
            // 递归检查Map中每个值
            return map.values().stream().anyMatch(this::isSerializable);
        }

        // 其他类型默认需要处理
        return true;
    }

    /**
     * 判断是否为代理类或动态生成的类
     * 排除Spring CGLIB代理类、JDK动态代理类和Hibernate代理类
     * 这些代理类通常不需要进行字典翻译处理
     * @param obj 待判断的对象
     * @return true表示是代理类或动态生成的类，false表示不是
     */
    private boolean isProxyOrDynamicClass(Object obj) {
        Class<?> clazz = obj.getClass();
        // 检查是否为JDK动态代理类
        // 检查是否为Spring CGLIB代理类（类名包含$$EnhancerBySpringCGLIB$$）
        // 检查是否为Hibernate代理类（类名包含org.hibernate.proxy）
        return Proxy.isProxyClass(clazz) ||
                clazz.getName().contains("$$EnhancerBySpringCGLIB$$") ||
                clazz.getName().contains("org.hibernate.proxy");
    }

    /**
     * 手动刷新指定字典类型的缓存
     * 用于字典数据变更后立即更新缓存，避免等待定时刷新
     * 支持单个字典类型刷新，提高更新效率
     * @param dictCode 字典编码，如果为null或空则刷新所有字典
     */
    public void refreshDictCacheByCode(String dictCode) {
        if (dictCode == null || dictCode.trim().isEmpty()) {
            log.info("手动刷新所有字典缓存...");
            refreshDictCache();
            return;
        }

        synchronized (REFRESH_LOCK) {
            try {
                DICT_CACHE_LOCK.writeLock().lock();
                log.info("手动刷新字典缓存，字典编码：{}", dictCode);

                // 从数据库查询指定字典类型的数据
                List<SysDictDetail> details = dictDetailService.list(
                    new LambdaQueryWrapper<SysDictDetail>()
                        .eq(SysDictDetail::getDictCode, dictCode)
                );

                if (details == null || details.isEmpty()) {
                    log.warn("字典[{}]在数据库中不存在", dictCode);
                    DICT_CACHE.remove(dictCode);
                    DICT_CACHE_TIMESTAMP.remove(dictCode);
                    DICT_CACHE_VERSION.remove(dictCode);
                    return;
                }

                // 构建新的字典映射
                Map<String, String> codeToNameMap = new ConcurrentHashMap<>(details.size());
                details.forEach(detail -> {
                    if (detail.getCode() != null && detail.getName() != null) {
                        codeToNameMap.put(detail.getCode(), detail.getName());
                    }
                });

                // 更新缓存
                DICT_CACHE.put(dictCode, codeToNameMap);
                DICT_CACHE_TIMESTAMP.put(dictCode, System.currentTimeMillis());
                
                // 递增版本号
                Long currentVersion = DICT_CACHE_VERSION.getOrDefault(dictCode, 0L);
                DICT_CACHE_VERSION.put(dictCode, currentVersion + 1);

                log.info("字典[{}]缓存刷新完成，共 {} 个字典项，版本号：{}", 
                    dictCode, codeToNameMap.size(), DICT_CACHE_VERSION.get(dictCode));
            } catch (Exception e) {
                log.error("手动刷新字典[{}]缓存失败", dictCode, e);
            } finally {
                DICT_CACHE_LOCK.writeLock().unlock();
            }
        }
    }

    /**
     * 批量刷新多个字典类型的缓存
     * 用于字典批量变更后同时更新多个字典缓存
     * @param dictCodes 字典编码列表
     */
    public void refreshDictCacheByCodes(List<String> dictCodes) {
        if (dictCodes == null || dictCodes.isEmpty()) {
            log.warn("字典编码列表为空，跳过批量刷新");
            return;
        }

        log.info("批量刷新字典缓存，共 {} 个字典类型", dictCodes.size());
        dictCodes.forEach(this::refreshDictCacheByCode);
    }

    /**
     * 清空字典缓存
     * 用于系统维护或缓存异常时强制清空缓存
     * 清空后会在下次使用时自动重新加载
     */
    public void clearDictCache() {
        synchronized (REFRESH_LOCK) {
            try {
                DICT_CACHE_LOCK.writeLock().lock();
                log.info("清空字典缓存...");
                
                int sizeBefore = DICT_CACHE.size();
                DICT_CACHE.clear();
                DICT_CACHE_TIMESTAMP.clear();
                DICT_CACHE_VERSION.clear();
                
                log.info("字典缓存已清空，清空前共 {} 个字典类型", sizeBefore);
            } catch (Exception e) {
                log.error("清空字典缓存失败", e);
            } finally {
                DICT_CACHE_LOCK.writeLock().unlock();
            }
        }
    }

    /**
     * 获取字典缓存统计信息
     * 用于监控和调试字典缓存状态
     * @return 缓存统计信息Map
     */
    public Map<String, Object> getDictCacheStats() {
        try {
            DICT_CACHE_LOCK.readLock().lock();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("cacheInitialized", cacheInitialized);
            stats.put("dictTypeCount", DICT_CACHE.size());
            stats.put("totalDictItemCount", DICT_CACHE.values().stream()
                .mapToInt(Map::size)
                .sum());
            
            // 添加每个字典类型的详细信息
            Map<String, Object> dictDetails = new HashMap<>();
            DICT_CACHE.forEach((dictCode, codeToNameMap) -> {
                Map<String, Object> detail = new HashMap<>();
                detail.put("itemCount", codeToNameMap.size());
                detail.put("timestamp", DICT_CACHE_TIMESTAMP.get(dictCode));
                detail.put("version", DICT_CACHE_VERSION.get(dictCode));
                dictDetails.put(dictCode, detail);
            });
            stats.put("dictDetails", dictDetails);
            
            return stats;
        } finally {
            DICT_CACHE_LOCK.readLock().unlock();
        }
    }

    /**
     * 检查指定字典类型是否在缓存中
     * @param dictCode 字典编码
     * @return true表示存在，false表示不存在
     */
    public boolean isDictInCache(String dictCode) {
        try {
            DICT_CACHE_LOCK.readLock().lock();
            return DICT_CACHE.containsKey(dictCode);
        } finally {
            DICT_CACHE_LOCK.readLock().unlock();
        }
    }

    /**
     * 获取指定字典类型的缓存版本号
     * @param dictCode 字典编码
     * @return 版本号，如果不存在则返回null
     */
    public Long getDictCacheVersion(String dictCode) {
        try {
            DICT_CACHE_LOCK.readLock().lock();
            return DICT_CACHE_VERSION.get(dictCode);
        } finally {
            DICT_CACHE_LOCK.readLock().unlock();
        }
    }

}
