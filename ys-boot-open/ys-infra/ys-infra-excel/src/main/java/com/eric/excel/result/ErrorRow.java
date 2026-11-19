package com.eric.excel.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel错误行数据类
 * 封装Excel读取过程中发生错误的行信息，包含行号、原始数据、错误详情等
 *
 * @ClassName: ErrorRow
 * @author: liuQ
 * @date: 2025-02-26
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 * @param <T> 原始数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorRow<T> {

    /**
     * 行号（从1开始）
     */
    private Integer rowNum;

    /**
     * 原始数据对象
     */
    private T originalData;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 错误字段名
     */
    private String errorField;

    /**
     * 单元格错误信息列表（用于多字段错误场景）
     */
    @Builder.Default
    private List<CellError> cellErrors = new ArrayList<>();

    /**
     * 添加单元格错误信息
     * @param fieldName 字段名
     * @param errorMsg 错误信息
     * @param originalValue 原始值
     */
    public void addCellError(String fieldName, String errorMsg, Object originalValue) {
        cellErrors.add(new CellError(fieldName, errorMsg, originalValue));
    }
}
