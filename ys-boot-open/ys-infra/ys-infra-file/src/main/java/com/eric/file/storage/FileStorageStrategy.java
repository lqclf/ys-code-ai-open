package com.eric.file.storage;

import java.io.InputStream;
import java.util.Map;

/**
 * 文件存储策略接口
 * @ClassName: FileStorageStrategy
 * @author: Eric
 * @date: 2025-09-12
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
public interface FileStorageStrategy {

    /**
     * 上传文件
     * @param inputStream 文件输入流
     * @param filePath 文件路径
     * @param contentType 文件类型
     * @return 访问URL
     */
    String uploadFile(InputStream inputStream, String filePath, String contentType);

    /**
     * 上传文件（带元数据）
     * @param inputStream 文件输入流
     * @param filePath 文件路径
     * @param contentType 文件类型
     * @param metadata 元数据
     * @return 访问URL
     */
    String uploadFile(InputStream inputStream, String filePath, String contentType, Map<String, String> metadata);

    /**
     * 下载文件
     * @param filePath 文件路径
     * @return 文件输入流
     */
    InputStream downloadFile(String filePath);

    /**
     * 删除文件
     * @param filePath 文件路径
     * @return true-成功，false-失败
     */
    boolean deleteFile(String filePath);

    /**
     * 检查文件是否存在
     * @param filePath 文件路径
     * @return true-存在，false-不存在
     */
    boolean fileExists(String filePath);

    /**
     * 获取文件大小
     * @param filePath 文件路径
     * @return 文件大小（字节）
     */
    long getFileSize(String filePath);

    /**
     * 获取文件访问URL
     * @param filePath 文件路径
     * @return 访问URL
     */
    String getFileUrl(String filePath);

    /**
     * 获取临时访问URL（带过期时间）
     * @param filePath 文件路径
     * @param expireMinutes 过期时间（分钟）
     * @return 临时访问URL
     */
    String getTemporaryUrl(String filePath, int expireMinutes);

    /**
     * 复制文件
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return true-成功，false-失败
     */
    boolean copyFile(String sourcePath, String targetPath);

    /**
     * 移动文件
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return true-成功，false-失败
     */
    boolean moveFile(String sourcePath, String targetPath);

    /**
     * 获取存储类型
     * @return 存储类型
     */
    String getStorageType();
}