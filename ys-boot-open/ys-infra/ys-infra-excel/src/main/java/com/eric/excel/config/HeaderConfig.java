package com.eric.excel.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;

/**
 * 表头配置类
 * @ClassName: HeaderConfig
 * @author: liuQ
 * @date: 2025-02-26
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeaderConfig {
    /**
     * 字段名（对应数据对象的属性名）
     */
    private String fieldName;

    /**
     * 表头标题
     */
    private String title;

    /**
     * 表头层级（用于多级表头，从0开始）
     */
    @Builder.Default
    private Integer level = 0;

    /**
     * 父表头标题（用于多级表头）
     */
    private String parentTitle;

    /**
     * 列宽
     */
    private Integer width;

    /**
     * 数据格式（如：日期格式、数字格式等）
     */
    private String dataFormat;

    /**
     * 转换器（数据转换函数）
     */
    private Function<Object, Object> converter;

    /**
     * 排序号
     */
    @Builder.Default
    private Integer sort = 0;
}
