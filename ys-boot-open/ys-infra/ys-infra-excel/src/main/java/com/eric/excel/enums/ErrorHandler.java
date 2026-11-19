package com.eric.excel.enums;

/**
 * 错误处理策略枚举
 * @ClassName: ErrorHandler
 * @author: liuQ
 * @date: 2025-02-26
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
public enum ErrorHandler {
    /**
     * 继续处理（记录错误，继续读取）
     */
    CONTINUE,
    /**
     * 遇到错误立即停止
     */
    STOP_ON_ERROR,
    /**
     * 跳过错误行
     */
    SKIP
}
