package com.eric.enums.aspect;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.common.annotation.enums.EnumTranslate;
import com.eric.common.enums.BaseEnum;
import com.eric.common.response.ResponseResult;
import com.eric.common.utils.JsonUtils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 枚举翻译切面，自动处理带有@EnumTranslate注解的字段
 *
 * <p>功能说明：
 * 1. 拦截带有@AutoEnum注解的方法
 * 2. 自动转换返回值中的枚举字段
 * 3. 支持ResponseResult、Page、List、Set、Map包装</p>
 *
 * @ClassName:  EnumAspect
 * @author:     Eric
 * @date:       2025-07-30 10:59:32
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Aspect
@Component
@Slf4j
@Order(1)
public class EnumAspect {

    /** 字段注解缓存：类 -> 需要翻译的字段列表 */
    private static final Map<Class<?>, List<Field>> TRANSLATE_FIELD_CACHE = new ConcurrentHashMap<>(64);

    /** 枚举值缓存：枚举类 -> (枚举key -> 枚举描述) 的映射 */
    private static final Map<Class<? extends BaseEnum>, Map<Object, String>> ENUM_VALUE_CACHE = new ConcurrentHashMap<>(64);

    /** 不可序列化类型集合：这些类型不需要进行枚举翻译处理 */
    private static final Set<Class<?>> NON_SERIALIZABLE_TYPES = new HashSet<>(Arrays.asList(
            String.class,           // 字符串类型
            Number.class,           // 数字类型
            Boolean.class,          // 布尔类型
            ModelAndView.class,     // Spring MVC视图对象
            ServletRequest.class,   // Servlet请求对象
            ServletResponse.class,  // Servlet响应对象
            HttpSession.class,      // HTTP会话对象
            MultipartFile.class,    // 上传文件对象
            ServletContext.class    // Servlet上下文对象
    ));

    /**
     * 定义切入点：拦截带有@AutoEnum注解的方法
     * 该切入点用于匹配所有使用了@AutoEnum注解的控制器方法
     */
    @Pointcut("@annotation(com.eric.common.annotation.enums.AutoEnum)")
    public void enumPoinCut() {}

    /**
     * 拦截返回值并执行枚举转换（优化版）
     * 在目标方法执行完成后，对返回结果进行后置处理，实现枚举值的自动翻译
     * 增加性能监控和异常处理
     * @param joinPoint 切入点信息，包含方法调用的相关信息
     * @return 方法执行后的返回结果
     * @throws Throwable 处理过程中可能抛出的异常
     */
    @Around("enumPoinCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 先执行目标方法，获取原始返回结果
        Object result = joinPoint.proceed();
        long startTime = System.currentTimeMillis();
        try {
            // 对返回结果进行枚举翻译处理
            translateEnum(result);
        } catch (Exception e) {
            log.error("枚举翻译异常：method={}", joinPoint.getSignature().toShortString(), e);
        }
        return result;
    }

    /**
     * 核心处理方法：根据返回结果类型执行对应的翻译逻辑（优化版）
     * 支持多种返回值类型：
     * 1. ResponseResult<?> - 标准响应结果包装类
     * 2. Page<?> - 分页数据
     * 3. List<?> - 列表数据
     * 4. Set<?> - 集合数据
     * 5. Map<?, ?> - 字典数据
     * 6. 数组 - 数组数据
     * @param result 原始返回结果
     * @throws Exception 处理异常
     */
    public void translateEnum(Object result) throws Exception {
        if (result == null) {
            return;
        }

        if (result instanceof ResponseResult) {
            // 处理标准响应结果包装类：提取data字段进行翻译
            ResponseResult<?> resultWrapper = (ResponseResult<?>) result;
            Object data = resultWrapper.getData();

            if (isSerializable(data)) {
                // 对可序列化的数据执行枚举翻译
                Object translatedData = translateData(data);
                if (translatedData != data) {
                    // 翻译后数据与原数据不同，需要更新响应结果中的data字段
                    ((ResponseResult) result).setData(translatedData);
                }
            }
        } else {
            // 处理非ResponseResult包装的直接返回结果（如List、Set、Map、数组）
            Object translatedResult = translateData(result);
            if (translatedResult != result) {
                // 翻译后数据与原数据不同，需要将翻译结果回写到原集合中
                if (result instanceof List) {
                    // 列表类型：清空原列表并添加翻译后的元素
                    ((List<?>) result).clear();
                    ((List) result).addAll((List) translatedResult);
                } else if (result instanceof Set) {
                    // 集合类型：清空原集合并添加翻译后的元素
                    ((Set<?>) result).clear();
                    ((Set) result).addAll((Set) translatedResult);
                } else if (result instanceof Map) {
                    // Map类型：清空原Map并添加翻译后的键值对
                    ((Map<?, ?>) result).clear();
                    ((Map) result).putAll((Map) translatedResult);
                } else if (result.getClass().isArray()) {
                    // 数组类型：通过System.arraycopy将翻译后的数据复制回原数组
                    Object[] array = (Object[]) result;
                    Object[] translatedArray = (Object[]) translatedResult;
                    System.arraycopy(translatedArray, 0, array, 0, Math.min(array.length, translatedArray.length));
                }
            }
        }
    }

    /**
     * 统一数据翻译处理方法
     * @param data 原始数据
     * @return 翻译后的数据
     */
    private Object translateData(Object data) {
        if (data == null) {
            return null;
        }

        if (data instanceof Page) {
            // 分页数据：翻译records中的每条记录，保留分页信息
            Page<?> pageData = (Page<?>) data;
            List<JSONObject> translatedRecords = pageData.getRecords().stream()
                    .map(this::translateToJSONObject)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            ((Page) data).setRecords(translatedRecords);
            return data;
        } else if (data instanceof List) {
            // 列表数据：逐条翻译并返回新列表
            List<?> listData = (List<?>) data;
            return listData.stream()
                    .map(this::translateToJSONObject)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else if (data instanceof Set) {
            // 集合数据：逐条翻译并返回新集合
            Set<?> setData = (Set<?>) data;
            return setData.stream()
                    .map(this::translateToJSONObject)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } else if (data instanceof Map) {
            // Map数据：翻译每个value值，保留key不变
            Map<String, ?> mapData = (Map<String, ?>) data;
            return mapData.entrySet().stream()
                    .map(entry -> {
                        JSONObject jsonObject = translateToJSONObject(entry.getValue());
                        if (jsonObject != null) {
                            return new AbstractMap.SimpleEntry<>(String.valueOf(entry.getKey()), jsonObject);
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } else if (data.getClass().isArray()) {
            // 数组数据：逐个元素翻译并返回新数组
            Object[] arrayData = (Object[]) data;
            Object[] translatedArray = new Object[arrayData.length];
            for (int i = 0; i < arrayData.length; i++) {
                translatedArray[i] = translateToJSONObject(arrayData[i]);
            }
            return translatedArray;
        } else {
            // 其他类型（如单个实体对象）不进行翻译处理，直接返回原对象
            return data;
        }
    }


    /**
     * 将实体类转换为JSON对象并添加翻译字段（优化版）
     * 使用FastJsonUtils将原始对象转换为JSONObject，然后添加枚举翻译字段
     * 增加空值检查和异常处理
     * @param obj 原始对象
     * @return 带有翻译字段的JSON对象
     */
    private JSONObject translateToJSONObject(Object obj) {
        if (obj == null) {
            return null;
        }

        try {
            // 将原始对象转换为JSONObject，便于动态添加翻译字段
            JSONObject item = JsonUtils.toObject(obj, JSONObject.class);
            if (item != null) {
                // 为JSON对象中带有@EnumTranslate注解的字段添加翻译值
                addTranslateFields(item, obj.getClass());
            }
            return item;
        } catch (Exception e) {
            log.error("JSON转换失败：objType={}", obj.getClass().getName(), e);
            return null;
        }
    }

    /**
     * 为JSON对象添加翻译字段
     * 遍历类中所有带有@EnumTranslate注解的字段，为每个字段添加对应的翻译字段
     * @param item JSON对象
     * @param clazz 原始对象的类
     */
    private void addTranslateFields(JSONObject item, Class<?> clazz) {
        // 获取类中所有需要翻译的字段（使用缓存提高性能）
        getTranslateFields(clazz).forEach(field -> {
            try {
                processField(item, field);
            } catch (Exception e) {
                log.error("字段[{}]翻译失败", field.getName(), e);
            }
        });
    }

    /**
     * 处理单个字段的翻译逻辑（优化版）
     * 根据字段上的@EnumTranslate注解，查找对应的枚举值并添加翻译字段
     * 增加字段名冲突检测和类型安全检查
     * @param item JSON对象
     * @param field 需要翻译的字段
     * @throws Exception 处理过程中可能抛出的异常
     */
    private void processField(JSONObject item, Field field) throws Exception {
        String fieldName = field.getName();
        // 获取JSON对象中该字段对应的值
        Object valueNode = item.get(fieldName);

        if (valueNode == null) {
            // 字段值为空，无需翻译
            return;
        }

        EnumTranslate annotation = field.getAnnotation(EnumTranslate.class);
        if (annotation == null) {
            // 字段没有@EnumTranslate注解，跳过
            return;
        }

        // 构造翻译字段名：原字段名 + "_" + 注解中指定的enumText后缀
        String translateFieldName = fieldName + "_" + annotation.enumText();

        if (item.containsKey(translateFieldName)) {
            // 翻译字段名与已有字段冲突，跳过翻译避免覆盖
            log.warn("翻译字段名冲突：字段{}已存在，跳过翻译", translateFieldName);
            return;
        }

        try {
            // 根据注解指定的枚举类和字段值，查找对应的枚举描述
            String desc = translate(annotation.enumClass(), valueNode);
            if (desc != null) {
                // 将翻译结果作为新字段添加到JSON对象中
                item.put(translateFieldName, desc);
            }
        } catch (Exception e) {
            log.error("字段[{}]翻译失败：value={}", fieldName, valueNode, e);
        }
    }

    /**
     * 枚举值翻译核心方法（优化版）
     * 使用缓存机制提高性能，O(1)复杂度查找
     * @param enumClass 枚举类，必须实现BaseEnum接口
     * @param value 枚举值
     * @return 枚举描述，如果未找到则返回null
     */
    public static String translate(Class<? extends BaseEnum> enumClass, Object value) {
        if (value == null || enumClass == null) return null;

        try {
            // 从缓存中获取枚举映射，若不存在则通过反射构建并缓存
            Map<Object, String> enumMap = ENUM_VALUE_CACHE.computeIfAbsent(enumClass, clazz -> {
                Map<Object, String> map = new HashMap<>();
                // 遍历枚举类的所有常量，构建 key -> 描述 的映射
                for (BaseEnum e : clazz.getEnumConstants()) {
                    Object enumKey = e.getKey();
                    if (enumKey != null) {
                        map.put(enumKey, e.getValue());
                    }
                }
                return Collections.unmodifiableMap(map);
            });

            // 根据枚举key查找对应的描述值
            return enumMap.get(value);
        } catch (Exception e) {
            log.error("枚举翻译失败：enumClass={}, value={}", enumClass.getName(), value, e);
            return null;
        }
    }

    /**
     * 获取需要翻译的字段列表（使用缓存）
     * 通过反射获取类及其父类中所有带有@EnumTranslate注解的字段
     * @param clazz 需要处理的类
     * @return 需要翻译的字段列表
     */
    private List<Field> getTranslateFields(Class<?> clazz) {
        // 使用computeIfAbsent方法实现缓存机制，避免重复反射操作
        return TRANSLATE_FIELD_CACHE.computeIfAbsent(clazz, key -> {
            List<Field> fields = new ArrayList<>();
            Class<?> targetClass = clazz;

            // 遍历类的继承链，查找所有带有@EnumTranslate注解的字段
            while (targetClass != null && !targetClass.getName().startsWith("java.")) {
                for (Field field : targetClass.getDeclaredFields()) {
                    // 如果字段上有@EnumTranslate注解，则加入列表
                    if (field.isAnnotationPresent(EnumTranslate.class)) {
                        field.setAccessible(true);
                        fields.add(field);
                    }
                }
                targetClass = targetClass.getSuperclass();
            }
            // 返回不可变列表，保证缓存数据的安全性
            return Collections.unmodifiableList(fields);
        });
    }

    /**
     * 判断对象是否需要序列化处理（优化版）
     * 排除一些不需要处理的类型，如基本类型、Web相关类型等
     * 增加递归深度限制，防止栈溢出
     * @param obj 待判断的对象
     * @return true表示需要序列化处理，false表示不需要
     */
    private boolean isSerializable(Object obj) {
        return isSerializable(obj, 0, 10);
    }

    /**
     * 判断对象是否需要序列化处理（带递归深度限制）
     * @param obj 待判断的对象
     * @param currentDepth 当前递归深度
     * @param maxDepth 最大递归深度
     * @return true表示需要序列化处理，false表示不需要
     */
    private boolean isSerializable(Object obj, int currentDepth, int maxDepth) {
        if (obj == null) return false;

        // 递归深度超过限制，停止处理防止栈溢出
        if (currentDepth > maxDepth) {
            log.warn("递归深度超过限制：currentDepth={}, maxDepth={}", currentDepth, maxDepth);
            return false;
        }

        Class<?> clazz = obj.getClass();

        // 检查是否属于不可序列化类型（基本类型、Web相关类型等）
        if (NON_SERIALIZABLE_TYPES.stream().anyMatch(type -> type.isInstance(obj))) {
            return false;
        }

        // 排除代理类和动态生成的类（CGLIB代理、JDK动态代理、Hibernate代理）
        if (isProxyOrDynamicClass(obj)) {
            return false;
        }

        if (clazz.isArray()) {
            // 文件上传数组类型不需要翻译
            if (obj instanceof MultipartFile[]) {
                return false;
            }
            // 递归检查数组中的元素是否需要翻译
            return Arrays.stream((Object[]) obj).anyMatch(item -> isSerializable(item, currentDepth + 1, maxDepth));
        }

        if (obj instanceof Collection) {
            Collection<?> collection = (Collection<?>) obj;
            // 空集合无需翻译
            if (collection.isEmpty()) {
                return false;
            }
            // 递归检查集合中的元素是否需要翻译
            return collection.stream().anyMatch(item -> isSerializable(item, currentDepth + 1, maxDepth));
        }

        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            // 空Map无需翻译
            if (map.isEmpty()) {
                return false;
            }
            // 递归检查Map中的值是否需要翻译
            return map.values().stream().anyMatch(item -> isSerializable(item, currentDepth + 1, maxDepth));
        }

        // 其他对象类型默认需要翻译处理
        return true;
    }

    /**
     * 判断是否为代理类或动态生成的类
     * 排除Spring CGLIB代理类、JDK动态代理类和Hibernate代理类
     * @param obj 待判断的对象
     * @return true表示是代理类或动态生成的类，false表示不是
     */
    private boolean isProxyOrDynamicClass(Object obj) {
        Class<?> clazz = obj.getClass();
        // 检查三种代理类型：JDK动态代理、Spring CGLIB代理、Hibernate代理
        return Proxy.isProxyClass(clazz) ||
                clazz.getName().contains("$$EnhancerBySpringCGLIB$$") ||
                clazz.getName().contains("org.hibernate.proxy");
    }

    /**
     * 清理字段注解缓存
     * 用于在类结构发生变化时清理缓存，确保使用最新的字段信息
     * @param clazz 需要清理缓存的类
     */
    public static void clearFieldCache(Class<?> clazz) {
        if (clazz != null) {
            TRANSLATE_FIELD_CACHE.remove(clazz);
            log.info("已清理字段注解缓存：{}", clazz.getName());
        }
    }

    /**
     * 清理所有字段注解缓存
     * 用于在系统重启或类结构大规模变更时清理所有缓存
     */
    public static void clearAllFieldCache() {
        int size = TRANSLATE_FIELD_CACHE.size();
        TRANSLATE_FIELD_CACHE.clear();
        log.info("已清理所有字段注解缓存，共{}个类", size);
    }

    /**
     * 清理枚举值缓存
     * 用于在枚举类发生变化时清理缓存，确保使用最新的枚举值
     * @param enumClass 需要清理缓存的枚举类
     */
    public static void clearEnumCache(Class<? extends BaseEnum> enumClass) {
        if (enumClass != null) {
            ENUM_VALUE_CACHE.remove(enumClass);
            log.info("已清理枚举值缓存：{}", enumClass.getName());
        }
    }

    /**
     * 清理所有枚举值缓存
     * 用于在系统重启或枚举类大规模变更时清理所有缓存
     */
    public static void clearAllEnumCache() {
        int size = ENUM_VALUE_CACHE.size();
        ENUM_VALUE_CACHE.clear();
        log.info("已清理所有枚举值缓存，共{}个枚举类", size);
    }

    /**
     * 清理所有缓存
     * 包括字段注解缓存和枚举值缓存
     */
    public static void clearAllCache() {
        clearAllFieldCache();
        clearAllEnumCache();
    }

    /**
     * 获取缓存统计信息
     * @return 缓存统计信息的JSON字符串
     */
    public static String getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        // 字段注解缓存数量
        stats.put("fieldCacheSize", TRANSLATE_FIELD_CACHE.size());
        // 枚举值缓存数量
        stats.put("enumCacheSize", ENUM_VALUE_CACHE.size());
        // 已缓存的类名列表
        stats.put("fieldCacheKeys", TRANSLATE_FIELD_CACHE.keySet().stream()
                .map(Class::getName)
                .collect(Collectors.toList()));
        // 已缓存的枚举类名列表
        stats.put("enumCacheKeys", ENUM_VALUE_CACHE.keySet().stream()
                .map(Class::getName)
                .collect(Collectors.toList()));
        return JsonUtils.toJSONString(stats);
    }
}
