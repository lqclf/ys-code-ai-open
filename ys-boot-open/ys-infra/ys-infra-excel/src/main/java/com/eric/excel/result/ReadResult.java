package com.eric.excel.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel读取结果类
 * 封装Excel读取操作的返回结果，包含成功数据、错误行、统计信息等
 *
 * @ClassName: ReadResult
 * @author: liuQ
 * @date: 2025-02-26
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 * @param <T> 数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadResult<T> {

    /**
     * 成功数据列表
     */
    @Builder.Default
    private List<T> successData = new ArrayList<>();

    /**
     * 错误行数据列表
     */
    @Builder.Default
    private List<ErrorRow<T>> errorRows = new ArrayList<>();

    /**
     * 总行数
     */
    private Integer totalCount;

    /**
     * 成功数
     */
    private Integer successCount;

    /**
     * 错误数
     */
    private Integer errorCount;

    /**
     * 读取耗时（毫秒）
     */
    private Long costTime;

    /**
     * 判断是否全部成功
     * @return true-全部成功，false-存在错误
     */
    public boolean isAllSuccess() {
        return errorCount == null || errorCount == 0;
    }

    /**
     * 获取成功率（百分比）
     * @return 成功率，如99.5表示99.5%
     */
    public double getSuccessRate() {
        if (totalCount == null || totalCount == 0) {
            return 0.0;
        }
        int success = successCount != null ? successCount : 0;
        return (double) success / totalCount * 100;
    }
}
