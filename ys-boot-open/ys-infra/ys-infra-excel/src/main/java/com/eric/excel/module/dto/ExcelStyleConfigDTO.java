package com.eric.excel.module.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Excel样式配置DTO
 * 用于接收前端传递的样式配置参数，包含表头样式和数据样式
 *
 * @ClassName: ExcelStyleConfigDTO
 * @author: liuQ
 * @date: 2025-02-28
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelStyleConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表头样式配置
     */
    private HeaderStyleConfigDTO headerStyle;

    /**
     * 数据样式配置
     */
    private DataStyleConfigDTO dataStyle;
}
