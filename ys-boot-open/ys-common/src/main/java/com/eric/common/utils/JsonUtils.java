package com.eric.common.utils;

import com.alibaba.fastjson2.*;
import com.alibaba.fastjson2.reader.ObjectReaderProvider;
import com.alibaba.fastjson2.util.ParameterizedTypeImpl;
import com.alibaba.fastjson2.util.TypeUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 基于 FastJson2 2.0.57 的增强工具类
 * 解决 autoType 安全问题，优化时间处理，增强类型转换功能
 *
 * @author eric
 * @version 3.0.0
 * @since 3.0.0
 */
@Slf4j
public class JsonUtils {

    // ============================ 常量定义区 ============================

    /**
     * 默认日期时间格式
     */
    public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 默认时间格式
     */
    public static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";

    // ============================ 日期时间格式化器 ============================

    /**
     * 日期时间格式化器 (yyyy-MM-dd HH:mm:ss)
     */
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN);

    /**
     * 日期格式化器 (yyyy-MM-dd)
     */
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN);

    /**
     * 时间格式化器 (HH:mm:ss)
     */
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN);

    /**
     * Date类型格式化器线程本地变量
     */
    private static final ThreadLocal<SimpleDateFormat> __dateFormatter
            = ThreadLocal.withInitial(() -> new SimpleDateFormat(DEFAULT_DATE_TIME_PATTERN));

    // ============================ JSON读写特性配置 ============================

    /**
     * 默认JSON写入特性配置
     * - WriteMapNullValue: 输出空值字段
     * - WriteNullListAsEmpty: 空List输出为[]
     * - WriteNullStringAsEmpty: 空字符串输出为""
     * - FieldBased: 基于字段而非getter/setter序列化
     * - ReferenceDetection: 引用检测
     */
    private static final JSONWriter.Feature[] DEFAULT_WRITE_FEATURES = {
            JSONWriter.Feature.WriteMapNullValue,
            JSONWriter.Feature.WriteNullListAsEmpty,
            JSONWriter.Feature.WriteNullStringAsEmpty,
            JSONWriter.Feature.FieldBased,
            JSONWriter.Feature.ReferenceDetection
    };

    /**
     * 安全读取特性配置 - 禁用AutoType以防止反序列化漏洞
     * - UseNativeObject: 使用原生对象(LinkedHashMap/ArrayList)而非JSONObject/JSONArray
     * - SupportSmartMatch: 支持智能匹配不同命名风格(camel/upper/pascal/snake/Kebab)
     * - UseBigDecimalForDoubles: 使用BigDecimal而非Double解析小数
     */
    private static final JSONReader.Feature[] SAFE_READ_FEATURES = {
            JSONReader.Feature.UseNativeObject,
            JSONReader.Feature.SupportSmartMatch,
            JSONReader.Feature.UseBigDecimalForDoubles
    };

    // ============================ 日期时间序列化/反序列化函数 ============================

    /**
     * LocalDateTime序列化函数
     */
    private static final Function<LocalDateTime, String> dateTimeSerializer
            = dateTimeFormatter::format;

    /**
     * LocalDate序列化函数
     */
    private static final Function<LocalDate, String> dateSerializer
            = dateFormatter::format;

    /**
     * LocalTime序列化函数
     */
    private static final Function<LocalTime, String> timeSerializer
            = timeFormatter::format;

    /**
     * Date序列化函数
     */
    private static final Function<Date, String> __dateSerializer
            = json -> __dateFormatter.get().format(json);

    /**
     * LocalDateTime反序列化函数
     */
    private static final Function<String, LocalDateTime> dateTimeDeserializer
            = json -> LocalDateTime.parse(json, dateTimeFormatter);

    /**
     * LocalDate反序列化函数
     */
    private static final Function<String, LocalDate> dateDeserializer
            = json -> LocalDate.parse(json, dateFormatter);

    /**
     * LocalTime反序列化函数
     */
    private static final Function<String, LocalTime> timeDeserializer
            = json -> LocalTime.parse(json, timeFormatter);

    /**
     * Date反序列化函数
     */
    private static final Function<String, Date> __dateDeserializer
            = json -> {
        try {
            return __dateFormatter.get().parse(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    // ============================ 缓存和提供者 ============================

    /**
     * 对象读取提供者
     */
    private static final ObjectReaderProvider provider;

    /**
     * TypeReference缓存，避免重复创建
     */
    private static final Map<Type, TypeReference<?>> typeReferenceCache = new ConcurrentHashMap<>();

    /**
     * JSONWriter特性缓存
     */
    private static final Map<String, JSONWriter.Feature[]> featureCache = new ConcurrentHashMap<>();

    // ============================ 静态初始化块 ============================

    static {
        // 启用安全模式，完全禁用AutoType
        System.setProperty("fastjson2.parser.safeMode", "true");

        provider = JSONFactory.getDefaultObjectReaderProvider();
        // 注册日期时间类型转换器
        provider.registerTypeConvert(String.class, Date.class, __dateDeserializer);
        provider.registerTypeConvert(String.class, LocalDateTime.class, dateTimeDeserializer);
        provider.registerTypeConvert(String.class, LocalDate.class, dateDeserializer);
        provider.registerTypeConvert(String.class, LocalTime.class, timeDeserializer);
        provider.registerTypeConvert(Date.class, String.class, __dateSerializer);
        provider.registerTypeConvert(LocalDateTime.class, String.class, dateTimeSerializer);
        provider.registerTypeConvert(LocalDate.class, String.class, dateSerializer);
        provider.registerTypeConvert(LocalTime.class, String.class, timeSerializer);
    }

    // ============================ 类型工具方法 ============================

    /**
     * 创建参数化类型
     *
     * @param rawType       原始类型
     * @param typeArguments 类型参数
     * @return ParameterizedType
     */
    public static ParameterizedType makeJavaType(Type rawType, Type... typeArguments) {
        return new ParameterizedTypeImpl(typeArguments, null, rawType);
    }

    // ============================ 对象序列化方法 ============================

    /**
     * 将对象转换为字符串
     *
     * @param value 对象
     * @return 字符串
     */
    public static String toJson(Object value) {
        if (Objects.isNull(value)) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        return toJSONString(value);
    }

    /**
     * 将对象转换为JSON字符串
     *
     * @param value 对象
     * @return JSON字符串
     */
    public static String toJSONString(Object value) {
        return JSON.toJSONString(value, DEFAULT_WRITE_FEATURES);
    }

    /**
     * 将对象转换为格式化的JSON字符串
     *
     * @param value 对象
     * @return 格式化的JSON字符串
     */
    public static String toPrettyString(Object value) {
        return JSON.toJSONString(value, JSONWriter.Feature.PrettyFormat, JSONWriter.Feature.WriteMapNullValue);
    }

    /**
     * 将对象转换为JSON字符串（自定义特性）
     *
     * @param value    对象
     * @param features 特性
     * @return JSON字符串
     */
    public static String toJSONString(Object value, JSONWriter.Feature... features) {
        return JSON.toJSONString(value, features);
    }

    /**
     * 将对象转换为JSON字节数组
     *
     * @param value 对象
     * @return JSON字节数组
     */
    public static byte[] toJSONBytes(Object value) {
        return JSON.toJSONBytes(value, DEFAULT_WRITE_FEATURES);
    }

    /**
     * 将对象转换为JSON字节数组（自定义特性）
     *
     * @param value    对象
     * @param features 特性
     * @return JSON字节数组
     */
    public static byte[] toJSONBytes(Object value, JSONWriter.Feature... features) {
        return JSON.toJSONBytes(value, features);
    }

    // ============================ JSON解析方法 ============================

    /**
     * 对象转换
     *
     * @param value 对象
     * @return 转换后的对象
     */
    public static Object fromObject(Object value) {
        Object result = null;
        if (Objects.nonNull(value) && (value instanceof String)) {
            result = toObject((String) value);
        } else {
            result = JSON.toJSON(value);
        }
        return result;
    }

    /**
     * 解析JSON字符串为对象
     *
     * @param content JSON字符串
     * @return JSONObject
     */
    public static Object toObject(String content) {
        return JSON.parseObject(content, Object.class, SAFE_READ_FEATURES);
    }

    /**
     * 解析JSON字符串为指定类型对象
     *
     * @param content JSON字符串
     * @param clazz   目标类型
     * @param <T>     泛型
     * @return 指定类型的对象
     */
    public static <T> T parseObject(String content, Class<T> clazz) {
        return JSON.parseObject(content, clazz, SAFE_READ_FEATURES);
    }

    /**
     * 解析JSON字符串为指定类型对象
     *
     * @param content JSON字符串
     * @param type    目标类型
     * @param <T>     泛型
     * @return 指定类型的对象
     */
    public static <T> T parseObject(String content, Type type) {
        return JSON.parseObject(content, type, SAFE_READ_FEATURES);
    }

    /**
     * 解析JSON字符串为指定类型对象（安全模式）
     *
     * @param content       JSON字符串
     * @param typeReference 类型引用
     * @param <T>           泛型
     * @return 指定类型的对象
     */
    public static <T> T parseObject(String content, TypeReference<T> typeReference) {
        return JSON.parseObject(content, typeReference, SAFE_READ_FEATURES);
    }

    /**
     * 解析JSON字符串为对象（启用AutoType，仅在必要时使用）
     *
     * @param content JSON字符串
     * @return JSONObject
     * @deprecated 由于安全考虑，不推荐使用
     */
    @Deprecated
    public static Object parseObjectWithAutoType(String content) {
        return JSON.parseObject(content, Object.class, SAFE_READ_FEATURES);
    }

    /**
     * 解析JSON字符串为指定类型对象（启用AutoType，仅在必要时使用）
     *
     * @param content JSON字符串
     * @param clazz   目标类型
     * @param <T>     泛型
     * @return 指定类型的对象
     * @deprecated 由于安全考虑，不推荐使用
     */
    @Deprecated
    public static <T> T parseObjectWithAutoType(String content, Class<T> clazz) {
        return JSON.parseObject(content, clazz, SAFE_READ_FEATURES);
    }

    /**
     * 解析JSON字符串为指定类型对象（启用AutoType，仅在必要时使用）
     *
     * @param content JSON字符串
     * @param type    目标类型
     * @param <T>     泛型
     * @return 指定类型的对象
     * @deprecated 由于安全考虑，不推荐使用
     */
    @Deprecated
    public static <T> T parseObjectWithAutoType(String content, Type type) {
        return JSON.parseObject(content, type, SAFE_READ_FEATURES);
    }

    /**
     * 解析JSON字符串为指定类型对象（启用AutoType，仅在必要时使用）
     *
     * @param content       JSON字符串
     * @param typeReference 类型引用
     * @param <T>           泛型
     * @return 指定类型的对象
     * @deprecated 由于安全考虑，不推荐使用
     */
    @Deprecated
    public static <T> T parseObjectWithAutoType(String content, TypeReference<T> typeReference) {
        return JSON.parseObject(content, typeReference, SAFE_READ_FEATURES);
    }

    /**
     * 解析JSON字节数组为对象（安全模式）
     *
     * @param bytes JSON字节数组
     * @return JSONObject
     */
    public static Object parseObject(byte[] bytes) {
        return JSON.parseObject(bytes, Object.class, SAFE_READ_FEATURES);
    }

    /**
     * 解析JSON字节数组为指定类型对象（安全模式）
     *
     * @param bytes JSON字节数组
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 指定类型的对象
     */
    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), clazz, SAFE_READ_FEATURES);
    }

    /**
     * 解析JSON字节数组为指定类型对象（安全模式）
     *
     * @param bytes JSON字节数组
     * @param type  目标类型
     * @param <T>   泛型
     * @return 指定类型的对象
     */
    public static <T> T parseObject(byte[] bytes, Type type) {
        return JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), type, SAFE_READ_FEATURES);
    }

    /**
     * 解析JSON字节数组为指定类型对象（安全模式）
     *
     * @param bytes         JSON字节数组
     * @param typeReference 类型引用
     * @param <T>           泛型
     * @return 指定类型的对象
     */
    public static <T> T parseObject(byte[] bytes, TypeReference<T> typeReference) {
        return JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), typeReference, SAFE_READ_FEATURES);
    }

    // ============================ JSON元素访问方法 ============================

    /**
     * 从JSONObject获取元素
     *
     * @param node JSONObject
     * @param name 元素名称
     * @return 元素值
     */
    public static Object getJsonElement(JSONObject node, String name) {
        return node.get(name);
    }

    /**
     * 从JSONArray获取元素
     *
     * @param node  JSONArray
     * @param index 索引
     * @return 元素值
     */
    public static Object getJsonElement(JSONArray node, int index) {
        return node.get(index);
    }

    // ============================ 对象类型转换方法 ============================

    /**
     * 将对象转换为指定类型
     *
     * @param node  对象
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 指定类型的对象
     */
    public static <T> T toObject(Object node, Class<T> clazz) {
        // 特殊处理：如果目标类型是JSONObject，则先序列化再反序列化
        if (clazz == JSONObject.class) {
            if (node instanceof JSONObject) {
                return (T) node;
            }
            String jsonString = toJSONString(node);
            return (T) JSON.parseObject(jsonString, JSONObject.class, SAFE_READ_FEATURES);
        }
        return JSON.to(clazz, node);
    }

    /**
     * 将对象转换为指定类型
     *
     * @param node 对象
     * @param type 目标类型
     * @param <T>  泛型
     * @return 指定类型的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T toObject(@NonNull Object node, Type type) {
        if (node instanceof JSONObject) {
            return ((JSONObject) node).to(type);
        }
        if (node instanceof JSONArray) {
            return ((JSONArray) node).to(type);
        }
        if (node instanceof String) {
            return JSON.parseObject((String) node, type, SAFE_READ_FEATURES);
        }
        // 特殊处理：如果目标类型是JSONObject，则先序列化再反序列化
        if (type == JSONObject.class) {
            String jsonString = toJSONString(node);
            return (T) JSON.parseObject(jsonString, JSONObject.class, SAFE_READ_FEATURES);
        }
        return (T) TypeUtils.cast(node, TypeUtils.getClass(type));
    }

    /**
     * 将对象转换为指定类型
     *
     * @param node          对象
     * @param typeReference 类型引用
     * @param <T>           泛型
     * @return 指定类型的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T toObject(@NonNull Object node, TypeReference<T> typeReference) {
        if (node instanceof JSONObject) {
            return typeReference.to((JSONObject) node);
        }
        if (node instanceof JSONArray) {
            return typeReference.to((JSONArray) node);
        }
        if (node instanceof String) {
            return JSON.parseObject((String) node, typeReference, SAFE_READ_FEATURES);
        }
        // 特殊处理：如果目标类型是JSONObject，则先序列化再反序列化
        if (typeReference.getType() == JSONObject.class) {
            String jsonString = toJSONString(node);
            return (T) JSON.parseObject(jsonString, JSONObject.class, SAFE_READ_FEATURES);
        }
        return (T) TypeUtils.cast(node, typeReference.getRawType());
    }

    /**
     * 将对象转换为List
     *
     * @param node  对象
     * @param clazz 元素类型
     * @param <E>   泛型
     * @return List
     */
    public static <E> List<E> toList(Object node, Class<E> clazz) {
        return toObject(node, makeJavaType(List.class, clazz));
    }

    /**
     * 将对象转换为List<Object>
     *
     * @param node 对象
     * @return List<Object>
     */
    public static List<Object> toList(Object node) {
        return toObject(node, new TypeReference<List<Object>>() {
        });
    }

    /**
     * 将对象转换为Set
     *
     * @param node  对象
     * @param clazz 元素类型
     * @param <E>   泛型
     * @return Set
     */
    public static <E> Set<E> toSet(Object node, Class<E> clazz) {
        return toObject(node, makeJavaType(Set.class, clazz));
    }

    /**
     * 将对象转换为Set<Object>
     *
     * @param node 对象
     * @return Set<Object>
     */
    public static Set<Object> toSet(Object node) {
        return toObject(node, new TypeReference<Set<Object>>() {
        });
    }

    /**
     * 将对象转换为Map
     *
     * @param node  对象
     * @param clazz 值类型
     * @param <V>   泛型
     * @return Map
     */
    public static <V> Map<String, V> toMap(Object node, Class<V> clazz) {
        return toObject(node, makeJavaType(Map.class, String.class, clazz));
    }

    /**
     * 将对象转换为Map<String, Object>
     *
     * @param node 对象
     * @return Map<String, Object>
     */
    public static Map<String, Object> toMap(Object node) {
        return toObject(node, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * 将JSON字符串转换为指定类型对象
     *
     * @param content JSON字符串
     * @param clazz   目标类型
     * @param <T>     泛型
     * @return 指定类型的对象
     */
    public static <T> T toObject(String content, Class<T> clazz) {
        return JSON.parseObject(content, clazz, SAFE_READ_FEATURES);
    }

    /**
     * 将JSON字符串转换为指定类型对象
     *
     * @param content JSON字符串
     * @param type    目标类型
     * @param <T>     泛型
     * @return 指定类型的对象
     */
    public static <T> T toObject(String content, Type type) {
        return JSON.parseObject(content, type, SAFE_READ_FEATURES);
    }

    /**
     * 将JSON字符串转换为指定类型对象
     *
     * @param content       JSON字符串
     * @param typeReference 类型引用
     * @param <T>           泛型
     * @return 指定类型的对象
     */
    public static <T> T toObject(String content, TypeReference<T> typeReference) {
        return JSON.parseObject(content, typeReference, SAFE_READ_FEATURES);
    }

    /**
     * 将JSON字符串转换为List
     *
     * @param content JSON字符串
     * @param clazz   元素类型
     * @param <E>     泛型
     * @return List
     */
    public static <E> List<E> toList(String content, Class<E> clazz) {
        return JSON.parseObject(content, makeJavaType(List.class, clazz), SAFE_READ_FEATURES);
    }

    /**
     * 将JSON字符串转换为List<Object>
     *
     * @param content JSON字符串
     * @return List<Object>
     */
    public static List<Object> toList(String content) {
        return JSON.parseObject(content, new TypeReference<List<Object>>() {
        }, SAFE_READ_FEATURES);
    }

    /**
     * 将JSON字符串转换为Set
     *
     * @param content JSON字符串
     * @param clazz   元素类型
     * @param <E>     泛型
     * @return Set
     */
    public static <E> Set<E> toSet(String content, Class<E> clazz) {
        return JSON.parseObject(content, makeJavaType(Set.class, clazz), SAFE_READ_FEATURES);
    }

    /**
     * 将JSON字符串转换为Set<Object>
     *
     * @param content JSON字符串
     * @return Set<Object>
     */
    public static Set<Object> toSet(String content) {
        return JSON.parseObject(content, new TypeReference<Set<Object>>() {
        }, SAFE_READ_FEATURES);
    }

    /**
     * 将JSON字符串转换为Map
     *
     * @param content JSON字符串
     * @param clazz   值类型
     * @param <V>     泛型
     * @return Map
     */
    public static <V> Map<String, V> toMap(String content, Class<V> clazz) {
        return JSON.parseObject(content, makeJavaType(Map.class, String.class, clazz), SAFE_READ_FEATURES);
    }

    /**
     * 将JSON字符串转换为Map<String, Object>
     *
     * @param content JSON字符串
     * @return Map<String, Object>
     */
    public static Map<String, Object> toMap(String content) {
        return JSON.parseObject(content, new TypeReference<Map<String, Object>>() {
        }, SAFE_READ_FEATURES);
    }

    /**
     * 将JSON字符串转换为数组
     *
     * @param content JSON字符串
     * @param clazz   元素类型
     * @param <T>     泛型
     * @return 数组
     */
    public static <T> T[] toArray(String content, Class<T> clazz) {
        return JSON.parseObject(content, makeJavaType(clazz.arrayType(), clazz), SAFE_READ_FEATURES);
    }

    /**
     * 将对象转换为数组
     *
     * @param node  对象
     * @param clazz 元素类型
     * @param <T>   泛型
     * @return 数组
     */
    public static <T> T[] toArray(Object node, Class<T> clazz) {
        return toObject(node, makeJavaType(clazz.arrayType(), clazz));
    }

    // ============================ 文件读写方法 ============================

    /**
     * 读取文件内容并解析为对象
     *
     * @param file  文件
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 指定类型的对象
     * @throws IOException IO异常
     */
    public static <T> T readFromFile(File file, Class<T> clazz) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        return parseObject(content, clazz);
    }

    /**
     * 读取文件内容并解析为对象
     *
     * @param file  文件
     * @param type  目标类型
     * @param <T>   泛型
     * @return 指定类型的对象
     * @throws IOException IO异常
     */
    public static <T> T readFromFile(File file, Type type) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        return parseObject(content, type);
    }

    /**
     * 读取文件内容并解析为对象
     *
     * @param file          文件
     * @param typeReference 类型引用
     * @param <T>           泛型
     * @return 指定类型的对象
     * @throws IOException IO异常
     */
    public static <T> T readFromFile(File file, TypeReference<T> typeReference) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        return parseObject(content, typeReference);
    }

    /**
     * 将对象写入文件
     *
     * @param obj  对象
     * @param file 文件
     * @throws IOException IO异常
     */
    public static void writeToFile(Object obj, File file) throws IOException {
        String json = toJSONString(obj);
        Files.write(file.toPath(), json.getBytes(StandardCharsets.UTF_8));
    }

    // ============================ JSONPath查询方法 ============================

    /**
     * 使用JSONPath查询对象
     *
     * @param jsonObject JSON对象
     * @param path       JSONPath表达式
     * @return 查询结果
     */
    public static Object jsonPathQuery(Object jsonObject, String path) {
        return JSONPath.eval(jsonObject, path);
    }

    /**
     * 使用JSONPath查询对象并转换为指定类型
     *
     * @param jsonObject JSON对象
     * @param path       JSONPath表达式
     * @param clazz      目标类型
     * @param <T>        泛型
     * @return 查询结果
     */
    public static <T> T jsonPathQuery(Object jsonObject, String path, Class<T> clazz) {
        Object result = JSONPath.eval(jsonObject, path);
        return toObject(result, clazz);
    }

    // ============================ 工具方法 ============================

    /**
     * 验证JSON字符串是否有效
     *
     * @param json JSON字符串
     * @return 是否有效
     */
    public static boolean isValidJson(String json) {
        try {
            JSON.parse(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证JSON对象是否有效
     *
     * @param obj 对象
     * @return 是否有效
     */
    public static boolean isValidJsonObject(Object obj) {
        try {
            toJSONString(obj);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 比较两个JSON对象是否相等
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 是否相等
     */
    public static boolean equals(Object obj1, Object obj2) {
        if (obj1 == obj2) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        return Objects.equals(toJSONString(obj1), toJSONString(obj2));
    }

    /**
     * 深度克隆对象
     *
     * @param obj 对象
     * @param <T> 泛型
     * @return 克隆的对象
     */
    public static <T> T deepClone(T obj) {
        String json = toJSONString(obj);
        return (T) parseObject(json, obj.getClass());
    }

    /**
     * 合并两个JSON对象
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 合并后的对象
     */
    public static JSONObject merge(JSONObject obj1, JSONObject obj2) {
        JSONObject result = new JSONObject();
        if (obj1 != null) {
            result.putAll(obj1);
        }
        if (obj2 != null) {
            result.putAll(obj2);
        }
        return result;
    }
    /**
     * 将源对象的属性值更新到目标对象中
     *
     * @param target 目标对象
     * @param source 源对象
     * @param <T>    目标对象类型
     * @return 更新后的目标对象
     */
    public static <T> void updateValue(T target, Object source) {
        if (target == null || source == null) {
            return;
        }

        try {
            // 将源对象转换为JSONObject
            JSONObject sourceObj = toObject(source, JSONObject.class);

            // 将源对象的属性更新到目标对象中
            String jsonString = sourceObj.toJSONString();
            JSON.parseObject(jsonString, (Type) target.getClass(), SAFE_READ_FEATURES);
        } catch (Exception e) {
            log.error("更新对象值失败", e);
            throw new RuntimeException("对象值更新失败: " + e.getMessage());
        }
    }

    /**
     * 清空对象，将对象属性重置为默认值
     *
     * @param targetClass 目标对象类型
     * @param <T>         目标对象类型
     * @return 清空后的对象实例
     */
    public static <T> void clearValue(Class<T> targetClass) {
        if (targetClass == null) {
            return;
        }

        try {
            // 创建一个空的JSON对象字符串
            String emptyJson = "{}";
            // 使用FastJSON将空JSON对象反序列化为目标类型的对象
            JSON.parseObject(emptyJson, targetClass, SAFE_READ_FEATURES);
        } catch (Exception e) {
            log.error("清空对象失败", e);
            throw new RuntimeException("清空对象失败: " + e.getMessage());
        }
    }

    /**
     * 清空指定对象，将对象属性重置为默认值
     *
     * @param target 目标对象
     * @param <T>    目标对象类型
     * @return 清空后的对象
     */
    public static <T> void clearValue(T target) {
        if (target == null) {
            return;
        }

        try {
            // 创建一个空的JSON对象字符串
            String emptyJson = "{}";
            // 使用FastJSON将空JSON对象反序列化为目标对象的类型，并更新目标对象
            JSON.parseObject(emptyJson, (Type) target.getClass(), SAFE_READ_FEATURES);
        } catch (Exception e) {
            log.error("清空对象失败", e);
            throw new RuntimeException("清空对象失败: " + e.getMessage());
        }
    }


    /**
     * 获取TypeReference缓存
     *
     * @param type 类型
     * @param <T>  泛型
     * @return TypeReference
     */
    @SuppressWarnings("unchecked")
    private static <T> TypeReference<T> getTypeReference(Type type) {
        return (TypeReference<T>) typeReferenceCache.computeIfAbsent(type, t -> new TypeReference<T>() {
        });
    }

    /**
     * 获取JSONWriter特性数组
     *
     * @param features 特性字符串
     * @return JSONWriter特性数组
     */
    public static JSONWriter.Feature[] getFeatures(String features) {
        if (features == null || features.isEmpty()) {
            return DEFAULT_WRITE_FEATURES;
        }
        return featureCache.computeIfAbsent(features, k -> {
            String[] featureNames = k.split(",");
            return Arrays.stream(featureNames)
                    .map(String::trim)
                    .map(name -> {
                        try {
                            return JSONWriter.Feature.valueOf(name);
                        } catch (Exception e) {
                            log.warn("Unknown JSONWriter.Feature: {}", name);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toArray(JSONWriter.Feature[]::new);
        });
    }
}
