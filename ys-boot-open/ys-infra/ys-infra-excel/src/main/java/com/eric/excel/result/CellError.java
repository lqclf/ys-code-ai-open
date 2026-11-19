package com.eric.excel.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Excel单元格错误类
 * 封装单个单元格的错误信息，包含字段名、错误信息和原始值
 *
 * @ClassName: CellError
 * @author: liuQ
 * @date: 2025-02-26
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CellError {

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 原始值
     */
    private Object originalValue;
}
