package com.eric.excel.handler;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 基于反射的行号处理器实现
 * 通过反射调用DTO的getter/setter方法
 *
 * @param <T> DTO类型
 * @author Eric
 * @date 2025-02-27
 */
@Slf4j
public class ReflectRowNumHandler<T> implements RowNumHandler<T> {

    private final String rowNumField;
    private final String validField;
    private final String errorMsgField;

    // 方法缓存，避免重复反射
    private final ConcurrentMap<String, Method> methodCache = new ConcurrentHashMap<>();

    public ReflectRowNumHandler(String rowNumField, String validField, String errorMsgField) {
        this.rowNumField = rowNumField;
        this.validField = validField;
        this.errorMsgField = errorMsgField;
    }

    @Override
    public void setRowNum(T dto, int rowNum) {
        try {
            Method setter = getSetterMethod(dto.getClass(), rowNumField, Integer.class);
            if (setter != null) {
                setter.invoke(dto, rowNum);
            }
        } catch (Exception e) {
            log.debug("设置行号失败: {}", e.getMessage());
        }
    }

    @Override
    public int getRowNum(T dto) {
        try {
            Method getter = getGetterMethod(dto.getClass(), rowNumField);
            if (getter != null) {
                Object value = getter.invoke(dto);
                return value != null ? (Integer) value : 0;
            }
        } catch (Exception e) {
            log.debug("获取行号失败: {}", e.getMessage());
        }
        return 0;
    }

    @Override
    public void setValid(T dto, boolean valid) {
        try {
            Method setter = getSetterMethod(dto.getClass(), validField, Boolean.class);
            if (setter != null) {
                setter.invoke(dto, valid);
            }
        } catch (Exception e) {
            log.debug("设置有效性失败: {}", e.getMessage());
        }
    }

    /**
     * 获取有效性标记
     * @param dto 数据对象
     * @return 是否有效，未设置则返回true
     */
    @Override
    public boolean getValid(T dto) {
        try {
            Method getter = getGetterMethod(dto.getClass(), validField);
            if (getter != null) {
                Object value = getter.invoke(dto);
                return value != null ? (Boolean) value : true;
            }
        } catch (Exception e) {
            log.debug("获取有效性失败: {}", e.getMessage());
        }
        return true;
    }

    @Override
    public void setErrorMsg(T dto, String errorMsg) {
        try {
            Method setter = getSetterMethod(dto.getClass(), errorMsgField, String.class);
            if (setter != null) {
                setter.invoke(dto, errorMsg);
            }
        } catch (Exception e) {
            log.debug("设置错误信息失败: {}", e.getMessage());
        }
    }

    /**
     * 获取错误信息
     * @param dto 数据对象
     * @return 错误信息，未设置则返回null
     */
    @Override
    public String getErrorMsg(T dto) {
        try {
            Method getter = getGetterMethod(dto.getClass(), errorMsgField);
            if (getter != null) {
                Object value = getter.invoke(dto);
                return value != null ? (String) value : null;
            }
        } catch (Exception e) {
            log.debug("获取错误信息失败: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> dtoClass) {
        // 检查是否有对应的setter方法
        return getSetterMethod(dtoClass, rowNumField, Integer.class) != null ||
               getSetterMethod(dtoClass, validField, Boolean.class) != null ||
               getSetterMethod(dtoClass, errorMsgField, String.class) != null;
    }

    /**
     * 获取setter方法（带缓存）
     */
    private Method getSetterMethod(Class<?> clazz, String fieldName, Class<?> paramType) {
        String cacheKey = clazz.getName() + ".set" + capitalize(fieldName);
        return methodCache.computeIfAbsent(cacheKey, k -> {
            try {
                String methodName = "set" + capitalize(fieldName);
                return clazz.getMethod(methodName, paramType);
            } catch (NoSuchMethodException e) {
                log.debug("未找到setter方法: {}.set{}", clazz.getSimpleName(), capitalize(fieldName));
                return null;
            }
        });
    }

    /**
     * 获取getter方法（带缓存）
     */
    private Method getGetterMethod(Class<?> clazz, String fieldName) {
        String cacheKey = clazz.getName() + ".get" + capitalize(fieldName);
        return methodCache.computeIfAbsent(cacheKey, k -> {
            try {
                String methodName = "get" + capitalize(fieldName);
                return clazz.getMethod(methodName);
            } catch (NoSuchMethodException e) {
                log.debug("未找到getter方法: {}.get{}", clazz.getSimpleName(), capitalize(fieldName));
                return null;
            }
        });
    }

    /**
     * 首字母大写
     * @param str 原始字符串
     * @return 首字母大写后的字符串
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
