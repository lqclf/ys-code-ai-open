package com.eric.excel.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 持久化上下文类
 * 用于记录Excel数据导入过程中的操作状态和统计信息
 *
 * @ClassName: PersistContext
 * @author: liuQ
 * @date: 2025-02-26
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersistContext {

    /**
     * 操作类型
     */
    private DataOperation operation;

    /**
     * 插入数量
     */
    @Builder.Default
    private Integer insertCount = 0;

    /**
     * 更新数量
     */
    @Builder.Default
    private Integer updateCount = 0;

    /**
     * 删除数量
     */
    @Builder.Default
    private Integer deleteCount = 0;

    /**
     * 自定义操作数量
     */
    @Builder.Default
    private Integer customCount = 0;

    /**
     * 失败数量
     */
    @Builder.Default
    private Integer failCount = 0;

    /**
     * 失败详情列表
     */
    @Builder.Default
    private List<FailDetail> failDetails = new ArrayList<>();

    /**
     * 总记录数
     */
    private Integer totalCount;

    /**
     * 处理耗时（毫秒）
     */
    private Long costTime;

    /**
     * 获取总成功数
     *
     * @return 总成功数
     */
    public Integer getTotalSuccessCount() {
        return (insertCount != null ? insertCount : 0) +
               (updateCount != null ? updateCount : 0) +
               (deleteCount != null ? deleteCount : 0) +
               (customCount != null ? customCount : 0);
    }

    /**
     * 是否全部成功
     *
     * @return true-全部成功，false-存在失败
     */
    public boolean isAllSuccess() {
        return failCount == null || failCount == 0;
    }

    /**
     * 获取成功率
     *
     * @return 成功率百分比
     */
    public double getSuccessRate() {
        if (totalCount == null || totalCount == 0) {
            return 0.0;
        }
        int successCount = totalCount - (failCount != null ? failCount : 0);
        return (double) successCount / totalCount * 100;
    }

    /**
     * 增加插入计数
     */
    public void incrementInsertCount() {
        this.insertCount = (this.insertCount != null ? this.insertCount : 0) + 1;
    }

    /**
     * 增加更新计数
     */
    public void incrementUpdateCount() {
        this.updateCount = (this.updateCount != null ? this.updateCount : 0) + 1;
    }

    /**
     * 增加删除计数
     */
    public void incrementDeleteCount() {
        this.deleteCount = (this.deleteCount != null ? this.deleteCount : 0) + 1;
    }

    /**
     * 增加自定义操作计数
     */
    public void incrementCustomCount() {
        this.customCount = (this.customCount != null ? this.customCount : 0) + 1;
    }

    /**
     * 增加失败计数
     */
    public void incrementFailCount() {
        this.failCount = (this.failCount != null ? this.failCount : 0) + 1;
    }

    /**
     * 添加失败详情
     *
     * @param failDetail 失败详情
     */
    public void addFailDetail(FailDetail failDetail) {
        if (this.failDetails == null) {
            this.failDetails = new ArrayList<>();
        }
        this.failDetails.add(failDetail);
        incrementFailCount();
    }

    /**
     * 失败详情内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailDetail {

        /**
         * 行号
         */
        private Integer rowNum;

        /**
         * 数据对象
         */
        private Object data;

        /**
         * 错误信息
         */
        private String errorMsg;

        /**
         * 错误字段
         */
        private String errorField;

        /**
         * 原始值
         */
        private Object originalValue;
    }
}
