package com.eric.excel.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据持久化结果
 * 统一封装批量保存操作的结果，包含成功/失败数量和失败明细
 *
 * @ClassName:   PersistResult
 * @author:      liuQ
 * @date:        2025-02-27
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersistResult {

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
     * 添加失败明细
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
     * 添加成功计数
     */
    public void incrementSuccess() {
        successCount++;
    }

    /**
     * 是否全部成功
     */
    public boolean isAllSuccess() {
        return failCount == 0;
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
