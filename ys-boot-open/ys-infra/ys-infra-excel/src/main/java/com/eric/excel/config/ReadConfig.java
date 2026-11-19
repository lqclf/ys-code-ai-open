package com.eric.excel.config;

import com.eric.excel.enums.ErrorHandler;
import com.eric.excel.validator.DataValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Consumer;

/**
 * 读取配置类
 * @ClassName: ReadConfig
 * @author: liuQ
 * @date: 2025-02-26
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadConfig<T> {

    /**
     * 默认批处理大小
     */
    private static final int DEFAULT_BATCH_SIZE = 1000;

    /**
     * Sheet索引（从0开始）
     */
    @Builder.Default
    private Integer sheetIndex = 0;

    /**
     * Sheet名称（优先于sheetIndex）
     */
    private String sheetName;

    /**
     * 表头行数（从1开始计数）
     */
    @Builder.Default
    private Integer headRowNumber = 1;

    /**
     * 起始数据行索引（从0开始，包含表头）
     */
    private Integer startRowIndex;

    /**
     * 是否去除单元格值首尾空格
     */
    @Builder.Default
    private Boolean trimCellValue = true;

    /**
     * 批处理大小（用于大数据量分批处理）
     */
    @Builder.Default
    private Integer batchSize = DEFAULT_BATCH_SIZE;

    /**
     * 数据校验器
     */
    private DataValidator<T> validator;

    /**
     * 错误处理器
     */
    @Builder.Default
    private ErrorHandler errorHandler = ErrorHandler.CONTINUE;

    /**
     * 数据批次处理器（用于大数据量分批处理）
     */
    private Consumer<List<T>> batchDataHandler;

    /**
     * 是否自动关闭输入流
     */
    @Builder.Default
    private Boolean autoCloseStream = true;

    /**
     * 字符编码
     */
    @Builder.Default
    private String charset = "UTF-8";
}
