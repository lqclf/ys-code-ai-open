package com.eric.excel.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Excel写入结果类
 * 封装Excel写入操作的返回结果，包含写入状态、行数、耗时等信息
 *
 * @ClassName: WriteResult
 * @author: liuQ
 * @date: 2025-02-26
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WriteResult {

    /**
     * 是否成功
     */
    @Builder.Default
    private boolean success = true;

    /**
     * 写入行数
     */
    private Integer rowCount;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 耗时（毫秒）
     */
    private Long costTime;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建成功结果
     * @param rowCount 写入行数
     * @param costTime 耗时（毫秒）
     * @return 成功的写入结果
     */
    public static WriteResult success(int rowCount, long costTime) {
        return WriteResult.builder()
                .success(true)
                .rowCount(rowCount)
                .costTime(costTime)
                .build();
    }

    /**
     * 创建失败结果
     * @param errorMsg 错误信息
     * @return 失败的写入结果
     */
    public static WriteResult fail(String errorMsg) {
        return WriteResult.builder()
                .success(false)
                .errorMsg(errorMsg)
                .build();
    }
}
