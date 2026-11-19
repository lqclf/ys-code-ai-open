package com.eric.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.eric.excel.config.ReadConfig;
import com.eric.excel.enums.ErrorHandler;
import com.eric.excel.exception.ExcelException;
import com.eric.excel.result.ErrorRow;
import com.eric.excel.result.ValidationResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 通用读取监听器
 * 用于处理Excel读取过程中的数据解析、校验和批量处理
 *
 * @ClassName: CommonReadListener
 * @author: liuQ
 * @date: 2025-02-26
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 * @param <T> 数据类型
 */
@Slf4j
public class CommonReadListener<T> extends AnalysisEventListener<T> {

    /**
     * 读取配置
     */
    private final ReadConfig<T> config;

    /**
     * 成功数据列表
     */
    private final List<T> successData;

    /**
     * 错误行数据列表
     */
    private final List<ErrorRow<T>> errorRows;

    /**
     * 总行数计数器
     */
    private final AtomicInteger totalCount;

    /**
     * 批次数据缓存
     */
    private final List<T> batchData;

    /**
     * 构造函数
     *
     * @param config      读取配置
     * @param successData 成功数据列表
     * @param errorRows   错误行数据列表
     * @param totalCount  总行数计数器
     */
    public CommonReadListener(ReadConfig<T> config, List<T> successData,
                              List<ErrorRow<T>> errorRows, AtomicInteger totalCount) {
        this.config = config;
        this.successData = successData;
        this.errorRows = errorRows;
        this.totalCount = totalCount;
        this.batchData = new ArrayList<>();
    }

    /**
     * 每解析一行数据时调用
     *
     * @param data    解析后的数据对象
     * @param context 分析上下文
     */
    @Override
    public void invoke(T data, AnalysisContext context) {
        totalCount.incrementAndGet();
        Integer rowIndex = context.readRowHolder().getRowIndex();

        try {
            if (config.getValidator() != null) {
                ValidationResult validationResult = config.getValidator().validate(data, rowIndex);
                if (!validationResult.isValid()) {
                    ErrorRow<T> errorRow = ErrorRow.<T>builder()
                            .rowNum(rowIndex + 1)
                            .originalData(data)
                            .errorMsg(validationResult.getErrorMsg())
                            .errorField(validationResult.getErrorField())
                            .build();
                    errorRows.add(errorRow);

                    if (config.getErrorHandler() == ErrorHandler.STOP_ON_ERROR) {
                        throw new ExcelException("数据校验失败: " + validationResult.getErrorMsg());
                    }
                    return;
                }
            }

            successData.add(data);
            batchData.add(data);

            if (config.getBatchDataHandler() != null &&
                    batchData.size() >= config.getBatchSize()) {
                config.getBatchDataHandler().accept(new ArrayList<>(batchData));
                batchData.clear();
            }

        } catch (Exception e) {
            ErrorRow<T> errorRow = ErrorRow.<T>builder()
                    .rowNum(rowIndex + 1)
                    .originalData(data)
                    .errorMsg(e.getMessage())
                    .build();
            errorRows.add(errorRow);

            if (config.getErrorHandler() == ErrorHandler.STOP_ON_ERROR) {
                throw e;
            }
        }
    }

    /**
     * 所有数据解析完成后调用
     *
     * @param context 分析上下文
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (config.getBatchDataHandler() != null && !batchData.isEmpty()) {
            config.getBatchDataHandler().accept(new ArrayList<>(batchData));
            batchData.clear();
        }
    }

    /**
     * 解析发生异常时调用
     *
     * @param exception 异常对象
     * @param context   分析上下文
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        Integer rowIndex = context.readRowHolder().getRowIndex();
        log.error("第{}行解析异常", rowIndex + 1, exception);

        ErrorRow<T> errorRow = ErrorRow.<T>builder()
                .rowNum(rowIndex + 1)
                .errorMsg(exception.getMessage())
                .build();
        errorRows.add(errorRow);

        if (config.getErrorHandler() == ErrorHandler.STOP_ON_ERROR) {
            throw new ExcelException("解析异常: " + exception.getMessage(), exception);
        }
    }
}
