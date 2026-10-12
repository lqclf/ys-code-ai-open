package com.eric.common.exception.fileupload;

import com.eric.common.exception.BaseException;
import com.eric.common.response.ResponseCode;
import lombok.Getter;
import lombok.Setter;

/**
 * MinIO存储异常类
 * @ClassName: MinioException
 * @author: Eric
 * @date: 2025-12-12
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Getter
@Setter
public class MinioException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 桶名
     */
    private String bucketName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 构造方法
     * @param message 错误消息
     * @param code 错误码
     */
    public MinioException(String message, ResponseCode code) {
        super(message, code);
    }

    /**
     * 构造方法
     * @param message 错误消息
     * @param code 错误码
     * @param bucketName 桶名
     */
    public MinioException(String message, ResponseCode code, String bucketName) {
        super(message, code);
        this.bucketName = bucketName;
    }

    /**
     * 构造方法
     * @param message 错误消息
     * @param code 错误码
     * @param bucketName 桶名
     * @param filePath 文件路径
     */
    public MinioException(String message, ResponseCode code, String bucketName, String filePath) {
        super(message, code);
        this.bucketName = bucketName;
        this.filePath = filePath;
    }

    /**
     * 构造方法
     * @param cause 原始异常
     * @param message 错误消息
     * @param code 错误码
     */
    public MinioException(Throwable cause, String message, ResponseCode code) {
        super(cause, message, code);
    }

    /**
     * 构造方法
     * @param cause 原始异常
     * @param message 错误消息
     * @param code 错误码
     * @param bucketName 桶名
     * @param filePath 文件路径
     */
    public MinioException(Throwable cause, String message, ResponseCode code, String bucketName, String filePath) {
        super(cause, message, code);
        this.bucketName = bucketName;
        this.filePath = filePath;
    }
}
