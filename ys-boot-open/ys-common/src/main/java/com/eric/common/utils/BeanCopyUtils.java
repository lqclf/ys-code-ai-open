package com.eric.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean 对象拷贝工具类
 * <p>
 * 提供对象属性拷贝、列表批量拷贝等常用功能，支持 Spring BeanUtils 的便捷封装。
 *
 * @author liuQ
 * @date 2025-06-18
 */
public class BeanCopyUtils {

    private static final Logger logger = LoggerFactory.getLogger(BeanCopyUtils.class);

    /**
     * 单个对象拷贝
     * <p>
     * 将源对象的属性拷贝到目标类的新实例中。若源对象或目标类为 null，或实例化/拷贝失败，则返回 null。
     *
     * @param source      源对象
     * @param targetClass 目标类类型
     * @return 拷贝后的目标对象实例
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     */
    public static <S, T> T copyObj(S source, Class<T> targetClass) {
        if (source == null || targetClass == null) {
            return null;
        }

        T target = null;
        try {
            // 使用 Spring 提供的工具类创建实例，避免直接调用 newInstance()
            target = BeanUtils.instantiate(targetClass);
            // 属性拷贝：源对象 -> 目标对象
            BeanUtils.copyProperties(source, target);
        } catch (Exception e) {
            // 记录错误日志，避免异常传播
            logger.error("对象拷贝失败，源类型: {}，目标类型: {}", source.getClass().getSimpleName(), targetClass.getSimpleName(), e);
        }

        return target;
    }

    /**
     * 批量拷贝对象列表
     * <p>
     * 将源对象列表中的每个元素拷贝为目标类的新实例。若源列表为 null，则返回 null。
     *
     * @param sourceList  源对象列表
     * @param targetClass 目标类类型
     * @return 拷贝后的目标对象列表
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     */
    public static <S, T> List<T> copyList(List<S> sourceList, Class<T> targetClass) {
        if (sourceList == null) {
            return null;
        }

        List<T> targetList = new ArrayList<>(sourceList.size());
        for (S source : sourceList) {
            T target = copyObj(source, targetClass);
            targetList.add(target);
        }

        return targetList;
    }

    /**
     * 单个对象的属性拷贝（目标对象已存在）
     * <p>
     * 将源对象的属性拷贝到已存在的目标对象中。若任一对象为 null，则不执行拷贝。
     *
     * @param source 源对象
     * @param target 目标对象
     * @param <S>    源对象类型
     * @param <T>    目标对象类型
     */
    public static <S, T> void copyProperties(S source, T target) {
        if (source == null || target == null) {
            return;
        }

        try {
            BeanUtils.copyProperties(source, target);
        } catch (Exception e) {
            logger.error("属性拷贝失败，源类型: {}，目标类型: {}", source.getClass().getSimpleName(), target.getClass().getSimpleName(), e);
        }
    }

}