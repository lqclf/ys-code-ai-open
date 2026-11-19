package com.eric.excel.context;

import com.eric.excel.result.ErrorRow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据处理上下文
 * 用于在 processData 和 persist 之间传递数据
 * 支持数据分类：新增、更新、删除、自定义操作等
 *
 * @param <R> 原始数据类型（从Excel读取的DTO，如 CustomerImportDTO）
 * @param <T> 业务数据类型（转换后的DTO，如 CrmCustomerDTO）
 * @ClassName:   ProcessContext
 * @author:      liuQ
 * @date:        2025-02-27
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessContext<R, T> {

    /**
     * 原始数据列表（从Excel读取的原始数据）
     */
    @Builder.Default
    private List<R> originalData = new ArrayList<>();

    /**
     * 处理后的数据列表（经过校验、清洗后的原始数据）
     */
    @Builder.Default
    private List<R> processedData = new ArrayList<>();

    /**
     * 需要新增的数据列表
     */
    @Builder.Default
    private List<T> insertList = new ArrayList<>();

    /**
     * 需要更新的数据列表
     */
    @Builder.Default
    private List<T> updateList = new ArrayList<>();

    /**
     * 需要删除的数据列表
     */
    @Builder.Default
    private List<T> deleteList = new ArrayList<>();

    /**
     * 需要自定义操作的数据列表
     */
    @Builder.Default
    private List<T> customList = new ArrayList<>();

    /**
     * 处理失败的数据列表
     */
    @Builder.Default
    private List<ErrorRow<R>> errorList = new ArrayList<>();

    /**
     * 处理统计信息
     * 可以存储任意统计信息，如：总条数、成功条数、失败条数等
     */
    @Builder.Default
    private Map<String, Object> statistics = new HashMap<>();

    /**
     * 获取总数据量
     *
     * @return 原始数据列表大小
     */
    public int getTotalCount() {
        return originalData != null ? originalData.size() : 0;
    }

    /**
     * 获取成功处理的数据量
     *
     * @return 处理后数据列表大小
     */
    public int getSuccessCount() {
        return processedData != null ? processedData.size() : 0;
    }

    /**
     * 获取失败的数据量
     *
     * @return 错误列表大小
     */
    public int getFailCount() {
        return errorList != null ? errorList.size() : 0;
    }

    /**
     * 添加统计信息
     *
     * @param key   统计项名称
     * @param value 统计值
     */
    public void addStatistic(String key, Object value) {
        if (statistics == null) {
            statistics = new HashMap<>();
        }
        statistics.put(key, value);
    }

    /**
     * 获取统计信息
     *
     * @param key 统计项名称
     * @return 统计值
     */
    public Object getStatistic(String key) {
        return statistics != null ? statistics.get(key) : null;
    }

    /**
     * 添加错误记录
     *
     * @param rowNum   行号
     * @param data     原始数据
     * @param errorMsg 错误信息
     */
    public void addError(int rowNum, R data, String errorMsg) {
        if (errorList == null) {
            errorList = new ArrayList<>();
        }
        ErrorRow<R> errorRow = new ErrorRow<>();
        errorRow.setRowNum(rowNum);
        errorRow.setOriginalData(data);
        errorRow.setErrorMsg(errorMsg);
        errorList.add(errorRow);
    }

    /**
     * 检查是否有错误
     *
     * @return true 如果有错误记录
     */
    public boolean hasErrors() {
        return errorList != null && !errorList.isEmpty();
    }

    /**
     * 检查是否有新增数据
     *
     * @return true 如果有新增数据
     */
    public boolean hasInsertData() {
        return insertList != null && !insertList.isEmpty();
    }

    /**
     * 检查是否有更新数据
     *
     * @return true 如果有更新数据
     */
    public boolean hasUpdateData() {
        return updateList != null && !updateList.isEmpty();
    }

    /**
     * 检查是否有删除数据
     *
     * @return true 如果有删除数据
     */
    public boolean hasDeleteData() {
        return deleteList != null && !deleteList.isEmpty();
    }

    /**
     * 检查是否有自定义操作数据
     *
     * @return true 如果有自定义操作数据
     */
    public boolean hasCustomData() {
        return customList != null && !customList.isEmpty();
    }
}
