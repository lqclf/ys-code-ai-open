package com.eric.file.storage.impl;

import com.eric.common.exception.fileupload.MinioException;
import com.eric.file.properties.FileUploadProperties;
import com.eric.file.storage.FileStorageStrategy;
import com.eric.file.utils.MinioUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;

/**
 * MinIO文件存储实现
 * @ClassName: MinioFileStorageStrategy
 * @author: Eric
 * @date: 2025-12-12
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "file.upload.minio.enabled", havingValue = "true")
public class MinioFileStorageStrategy implements FileStorageStrategy {

    /** 文件上传配置属性 */
    private final FileUploadProperties properties;
    /** MinIO工具类 */
    private final MinioUtils minioUtils;

    /**
     * 获取默认存储桶名称
     * @return 存储桶名称
     */
    private String getDefaultBucketName() {
        FileUploadProperties.MinioStorage minioStorage = properties.getMinio();
        if (minioStorage == null) {
            return "files";
        }
        String bucketName = minioStorage.getBucketName();
        return bucketName == null || bucketName.isEmpty() ? "files" : bucketName;
    }

    /**
     * 上传文件到MinIO（不带元数据）
     * @param inputStream 文件输入流
     * @param filePath 存储路径
     * @param contentType 内容类型
     * @return 文件URL，失败返回null
     */
    @Override
    public String uploadFile(InputStream inputStream, String filePath, String contentType) {
        return uploadFile(inputStream, filePath, contentType, null);
    }

    /**
     * 上传文件到MinIO（带元数据）
     * @param inputStream 文件输入流
     * @param filePath 存储路径
     * @param contentType 内容类型
     * @param metadata 文件元数据
     * @return 文件URL，失败返回null
     */
    @Override
    public String uploadFile(InputStream inputStream, String filePath, String contentType, Map<String, String> metadata) {
        try {
            String bucketName = getDefaultBucketName();
            return minioUtils.uploadFile(bucketName, filePath, inputStream, contentType, metadata);
        } catch (MinioException e) {
            log.error("MinIO文件上传失败: {}", filePath, e);
            return null;
        } catch (Exception e) {
            log.error("文件上传失败: {}", filePath, e);
            return null;
        }
    }

    /**
     * 从MinIO下载文件
     * @param filePath 文件存储路径
     * @return 文件输入流，失败返回null
     */
    @Override
    public InputStream downloadFile(String filePath) {
        try {
            String bucketName = getDefaultBucketName();
            return minioUtils.downloadFile(bucketName, filePath);
        } catch (MinioException e) {
            log.error("MinIO文件下载失败: {}", filePath, e);
            return null;
        } catch (Exception e) {
            log.error("文件下载失败: {}", filePath, e);
            return null;
        }
    }

    /**
     * 删除MinIO上的文件
     * @param filePath 文件存储路径
     * @return true-删除成功，false-删除失败
     */
    @Override
    public boolean deleteFile(String filePath) {
        try {
            String bucketName = getDefaultBucketName();
            minioUtils.deleteFile(bucketName, filePath);
            return true;
        } catch (MinioException e) {
            log.error("MinIO文件删除失败: {}", filePath, e);
            return false;
        } catch (Exception e) {
            log.error("文件删除失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 检查MinIO上文件是否存在
     * @param filePath 文件存储路径
     * @return true-存在，false-不存在
     */
    @Override
    public boolean fileExists(String filePath) {
        try {
            String bucketName = getDefaultBucketName();
            return minioUtils.fileExists(bucketName, filePath);
        } catch (MinioException e) {
            log.error("MinIO文件存在性检查失败: {}", filePath, e);
            return false;
        } catch (Exception e) {
            log.error("文件存在性检查失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 获取MinIO文件大小
     * @param filePath 文件存储路径
     * @return 文件大小（字节），失败返回0
     */
    @Override
    public long getFileSize(String filePath) {
        try {
            String bucketName = getDefaultBucketName();
            return minioUtils.getFileSize(bucketName, filePath);
        } catch (MinioException e) {
            log.error("MinIO获取文件大小失败: {}", filePath, e);
            return 0;
        } catch (Exception e) {
            log.error("获取文件大小失败: {}", filePath, e);
            return 0;
        }
    }

    /**
     * 获取MinIO文件的永久访问URL
     * @param filePath 文件存储路径
     * @return 文件URL，失败返回null
     */
    @Override
    public String getFileUrl(String filePath) {
        try {
            String bucketName = getDefaultBucketName();
            return minioUtils.getFileUrl(bucketName, filePath);
        } catch (MinioException e) {
            log.error("MinIO获取文件URL失败: {}", filePath, e);
            return null;
        } catch (Exception e) {
            log.error("获取文件URL失败: {}", filePath, e);
            return null;
        }
    }

    /**
     * 获取MinIO文件的临时访问URL
     * @param filePath 文件存储路径
     * @param expireMinutes 过期时间（分钟）
     * @return 临时URL，失败返回null
     */
    @Override
    public String getTemporaryUrl(String filePath, int expireMinutes) {
        try {
            String bucketName = getDefaultBucketName();
            // 转换为秒
            int expireSeconds = expireMinutes * 60;
            return minioUtils.getTemporaryUrl(bucketName, filePath, expireSeconds);
        } catch (MinioException e) {
            log.error("MinIO获取临时URL失败: {}", filePath, e);
            return null;
        } catch (Exception e) {
            log.error("获取临时URL失败: {}", filePath, e);
            return null;
        }
    }

    /**
     * 复制MinIO文件
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return true-复制成功，false-复制失败
     */
    @Override
    public boolean copyFile(String sourcePath, String targetPath) {
        try {
            String bucketName = getDefaultBucketName();
            minioUtils.copyFile(bucketName, sourcePath, bucketName, targetPath);
            return true;
        } catch (MinioException e) {
            log.error("MinIO文件复制失败: {} -> {}", sourcePath, targetPath, e);
            return false;
        } catch (Exception e) {
            log.error("文件复制失败: {} -> {}", sourcePath, targetPath, e);
            return false;
        }
    }

    /**
     * 移动MinIO文件
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return true-移动成功，false-移动失败
     */
    @Override
    public boolean moveFile(String sourcePath, String targetPath) {
        try {
            String bucketName = getDefaultBucketName();
            minioUtils.moveFile(bucketName, sourcePath, bucketName, targetPath);
            return true;
        } catch (MinioException e) {
            log.error("MinIO文件移动失败: {} -> {}", sourcePath, targetPath, e);
            return false;
        } catch (Exception e) {
            log.error("文件移动失败: {} -> {}", sourcePath, targetPath, e);
            return false;
        }
    }

    /**
     * 获取存储类型标识
     * @return 存储类型字符串
     */
    @Override
    public String getStorageType() {
        return "minio";
    }

    /**
     * 创建目录
     * @param directoryName 目录名称
     * @return true-成功，false-失败
     */
    public boolean createDirectory(String directoryName) {
        try {
            String bucketName = getDefaultBucketName();
            minioUtils.createDirectory(bucketName, directoryName);
            return true;
        } catch (MinioException e) {
            log.error("MinIO目录创建失败: {}", directoryName, e);
            return false;
        } catch (Exception e) {
            log.error("目录创建失败: {}", directoryName, e);
            return false;
        }
    }
}
