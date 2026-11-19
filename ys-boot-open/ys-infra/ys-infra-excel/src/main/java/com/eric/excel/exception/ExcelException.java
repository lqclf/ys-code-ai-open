package com.eric.excel.exception;

/**
 * Excel操作异常
 * @ClassName: ExcelException
 * @author: liuQ
 * @date: 2025-02-26
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
public class ExcelException extends RuntimeException {

    /**
     * 构造异常对象
     * @param message 异常消息
     */
    public ExcelException(String message) {
        super(message);
    }

    /**
     * 构造异常对象
     * @param message 异常消息
     * @param cause 原始异常
     */
    public ExcelException(String message, Throwable cause) {
        super(message, cause);
    }
}
