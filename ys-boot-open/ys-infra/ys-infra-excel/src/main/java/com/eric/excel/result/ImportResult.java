package com.eric.excel.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 导入结果
 * 封装导入操作的完整结果信息
 *
 * @author Eric
 * @date 2025-02-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResult {

    /**
     * 成功数量
     */
    @Builder.Default
    private int successCount = 0;

    /**
     * 失败数量
     */
    @Builder.Default
    private int failCount = 0;

    /**
     * 失败明细列表
     */
    @Builder.Default
    private List<FailDetail> failDetails = new ArrayList<>();

    /**
     * 导入耗时（毫秒）
     */
    private Long costTime;

    /**
     * 是否全部成功
     */
    public boolean isAllSuccess() {
        return failCount == 0;
    }

    /**
     * 获取成功率
     */
    public double getSuccessRate() {
        int total = successCount + failCount;
        if (total == 0) {
            return 0.0;
        }
        return (double) successCount / total * 100;
    }

    /**
     * 添加失败明细
     * @param rowNum    行号
     * @param data      数据对象
     * @param errorMsg  错误信息
     */
    public void addFailDetail(int rowNum, Object data, String errorMsg) {
        failDetails.add(FailDetail.builder()
                .rowNum(rowNum)
                .data(data)
                .errorMsg(errorMsg)
                .build());
        failCount++;
    }

    /**
     * 合并另一个结果
     */
    public void merge(ImportResult other) {
        this.successCount += other.successCount;
        this.failCount += other.failCount;
        this.failDetails.addAll(other.failDetails);
    }

    /**
     * 失败明细内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailDetail {
        /**
         * 行号
         */
        private int rowNum;

        /**
         * 数据对象
         */
        private Object data;

        /**
         * 错误信息
         */
        private String errorMsg;
    }
}
