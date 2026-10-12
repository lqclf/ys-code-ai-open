package com.eric.file.utils;

import com.eric.common.exception.fileupload.MinioException;
import com.eric.common.response.ResponseCode;
import com.eric.file.properties.FileUploadProperties;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * MinIO工具类
 * @ClassName: MinioUtils
 * @author: Eric
 * @date: 2025-12-12
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "file.upload.minio.enabled", havingValue = "true")
public class MinioUtils implements InitializingBean {

    @Autowired
    private FileUploadProperties properties;

    private MinioClient minioClient;

    /**
     * 分片大小，默认5MB
     */
    private static final long DEFAULT_PART_SIZE = 5 * 1024 * 1024;

    /**
     * 并发线程数，默认5个
     */
    private static final int DEFAULT_CONCURRENCY = 5;

    /**
     * 重试次数，默认3次
     */
    private static final int DEFAULT_RETRY_COUNT = 3;

    /**
     * 重试间隔，默认1秒
     */
    private static final long DEFAULT_RETRY_INTERVAL = 1000;

    /**
     * 连接超时，默认30秒
     */
    private static final long DEFAULT_CONNECT_TIMEOUT = 30000;

    /**
     * 读取超时，默认30秒
     */
    private static final long DEFAULT_READ_TIMEOUT = 30000;

    /**
     * 写入超时，默认30秒
     */
    private static final long DEFAULT_WRITE_TIMEOUT = 30000;

    /**
     * 执行器服务，用于分片上传
     */
    private final ExecutorService executorService = Executors.newFixedThreadPool(DEFAULT_CONCURRENCY);

    /**
     * 初始化MinIO客户端
     * @throws Exception 初始化异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            FileUploadProperties.MinioStorage minioStorage = properties.getMinio();
            if (minioStorage == null) {
                throw new MinioException("MinIO配置不能为空", ResponseCode.ERROR);
            }

            // 验证必填配置
            if (minioStorage.getEndpoint() == null || minioStorage.getEndpoint().isEmpty()) {
                throw new MinioException("MinIO服务地址不能为空", ResponseCode.ERROR);
            }
            if (minioStorage.getAccessKey() == null || minioStorage.getAccessKey().isEmpty()) {
                throw new MinioException("MinIO访问密钥不能为空", ResponseCode.ERROR);
            }
            if (minioStorage.getSecretKey() == null || minioStorage.getSecretKey().isEmpty()) {
                throw new MinioException("MinIO密钥不能为空", ResponseCode.ERROR);
            }

            // 创建MinIO客户端
            minioClient = new MinioClient.Builder()
                    .endpoint(minioStorage.getEndpoint())
                    .credentials(minioStorage.getAccessKey(), minioStorage.getSecretKey())
                    .build();

            // 检查连接
            checkConnection();

            // 自动创建存储桶
            autoCreateBucket();

            log.info("MinIO客户端初始化成功，服务地址: {}", minioStorage.getEndpoint());
        } catch (Exception e) {
            throw new MinioException(e, "MinIO客户端初始化失败", ResponseCode.ERROR);
        }
    }

    /**
     * 创建HTTP客户端配置
     * @return HTTP客户端配置
     */
    private okhttp3.OkHttpClient createHttpClient() {
        return new okhttp3.OkHttpClient.Builder()
                .connectTimeout(Duration.ofMillis(DEFAULT_CONNECT_TIMEOUT))
                .readTimeout(Duration.ofMillis(DEFAULT_READ_TIMEOUT))
                .writeTimeout(Duration.ofMillis(DEFAULT_WRITE_TIMEOUT))
                .retryOnConnectionFailure(true)
                .build();
    }

    /**
     * 检查MinIO连接
     * @throws Exception 连接异常
     */
    private void checkConnection() throws Exception {
        try {
            minioClient.listBuckets();
        } catch (Exception e) {
            throw new MinioException(e, "MinIO连接失败，请检查服务地址和凭证", ResponseCode.ERROR);
        }
    }

    /**
     * 自动创建存储桶
     * @throws Exception 创建异常
     */
    private void autoCreateBucket() throws Exception {
        FileUploadProperties.MinioStorage minioStorage = properties.getMinio();
        String bucketName = minioStorage.getBucketName();
        if (bucketName == null || bucketName.isEmpty()) {
            bucketName = "files";
        }

        // 检查存储桶是否存在，不存在则创建
        if (!bucketExists(bucketName)) {
            createBucket(bucketName);
            log.info("自动创建MinIO存储桶成功: {}", bucketName);
        }
    }

    // ============================== 存储桶管理 ==============================

    /**
     * 创建存储桶
     * @param bucketName 存储桶名称
     * @throws Exception 创建异常
     */
    public void createBucket(String bucketName) throws Exception {
        createBucket(bucketName, null, null);
    }

    /**
     * 创建存储桶
     * @param bucketName 存储桶名称
     * @param region 区域
     * @param isPublic 是否公开
     * @throws Exception 创建异常
     */
    public void createBucket(String bucketName, String region, Boolean isPublic) throws Exception {
        try {
            if (bucketExists(bucketName)) {
                throw new MinioException("存储桶已存在: " + bucketName, ResponseCode.ERROR);
            }

            // 创建存储桶
            MakeBucketArgs makeBucketArgs = new MakeBucketArgs.Builder()
                    .bucket(bucketName)
                    .build();

            minioClient.makeBucket(makeBucketArgs);

            // 设置访问策略
            if (Boolean.TRUE.equals(isPublic)) {
                setBucketPublic(bucketName);
            }

            log.info("创建存储桶成功: {}", bucketName);
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e, "创建存储桶失败: " + bucketName, ResponseCode.ERROR);
        }
    }

    /**
     * 删除存储桶
     * @param bucketName 存储桶名称
     * @throws Exception 删除异常
     */
    public void deleteBucket(String bucketName) throws Exception {
        try {
            if (!bucketExists(bucketName)) {
                throw new MinioException("存储桶不存在: " + bucketName, ResponseCode.ERROR);
            }

            // 检查存储桶是否为空
            Iterable<Result<Item>> results = minioClient.listObjects(new ListObjectsArgs.Builder().bucket(bucketName).build());
            if (results.iterator().hasNext()) {
                throw new MinioException("存储桶非空，无法删除: " + bucketName, ResponseCode.ERROR);
            }

            // 删除存储桶
            minioClient.removeBucket(new RemoveBucketArgs.Builder().bucket(bucketName).build());

            log.info("删除存储桶成功: {}", bucketName);
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e, "删除存储桶失败: " + bucketName, ResponseCode.ERROR);
        }
    }

    /**
     * 检查存储桶是否存在
     * @param bucketName 存储桶名称
     * @return true-存在，false-不存在
     * @throws Exception 检查异常
     */
    public boolean bucketExists(String bucketName) throws Exception {
        try {
            return minioClient.bucketExists(new BucketExistsArgs.Builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new MinioException(e, "检查存储桶存在性失败: " + bucketName, ResponseCode.ERROR);
        }
    }

    /**
     * 列出所有存储桶
     * @return 存储桶列表
     * @throws Exception 列出异常
     */
    public List<Bucket> listBuckets() throws Exception {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            throw new MinioException(e, "列出存储桶失败", ResponseCode.ERROR);
        }
    }

    /**
     * 设置存储桶为公开访问
     * @param bucketName 存储桶名称
     * @throws Exception 设置异常
     */
    public void setBucketPublic(String bucketName) throws Exception {
        try {
            if (!bucketExists(bucketName)) {
                throw new MinioException("存储桶不存在: " + bucketName, ResponseCode.ERROR);
            }

            // 设置公开访问策略
            String policyJson = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::\"+bucketName+\"/*\"]}]}";
            minioClient.setBucketPolicy(new SetBucketPolicyArgs.Builder()
                    .bucket(bucketName)
                    .config(policyJson)
                    .build());

            log.info("设置存储桶为公开访问成功: {}", bucketName);
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e, "设置存储桶访问策略失败: " + bucketName, ResponseCode.ERROR);
        }
    }

    /**
     * 设置存储桶为私有访问
     * @param bucketName 存储桶名称
     * @throws Exception 设置异常
     */
    public void setBucketPrivate(String bucketName) throws Exception {
        try {
            if (!bucketExists(bucketName)) {
                throw new MinioException("存储桶不存在: " + bucketName, ResponseCode.ERROR);
            }

            // 删除访问策略，默认即为私有
            minioClient.setBucketPolicy(new SetBucketPolicyArgs.Builder()
                    .bucket(bucketName)
                    .config("{}")
                    .build());

            log.info("设置存储桶为私有访问成功: {}", bucketName);
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e, "设置存储桶访问策略失败: " + bucketName, ResponseCode.ERROR);
        }
    }

    // ============================== 文件基础操作 ==============================

    /**
     * 上传文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param inputStream 文件输入流
     * @param contentType 文件类型
     * @return 文件访问URL
     * @throws Exception 上传异常
     */
    public String uploadFile(String bucketName, String objectName, InputStream inputStream, String contentType) throws Exception {
        return uploadFile(bucketName, objectName, inputStream, contentType, null);
    }

    /**
     * 上传文件（带元数据）
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param inputStream 文件输入流
     * @param contentType 文件类型
     * @param metadata 元数据
     * @return 文件访问URL
     * @throws Exception 上传异常
     */
    public String uploadFile(String bucketName, String objectName, InputStream inputStream, String contentType, Map<String, String> metadata) throws Exception {
        try {
            if (!bucketExists(bucketName)) {
                throw new MinioException("存储桶不存在: " + bucketName, ResponseCode.ERROR);
            }

            // 自动检测文件类型
            if (contentType == null || contentType.isEmpty()) {
                contentType = URLConnection.guessContentTypeFromName(objectName);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
            }

            // 构建PutObjectArgs
            PutObjectArgs.Builder builder = new PutObjectArgs.Builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, -1, DEFAULT_PART_SIZE)
                    .contentType(contentType);

            // 添加元数据
            if (metadata != null && !metadata.isEmpty()) {
                builder.userMetadata(metadata);
            }

            // 上传文件
            minioClient.putObject(builder.build());

            // 获取文件URL
            String fileUrl = getFileUrl(bucketName, objectName);
            return fileUrl;
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e, "文件上传失败: " + objectName, ResponseCode.ERROR, bucketName, objectName);
        }
    }

    /**
     * 下载文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 文件输入流
     * @throws Exception 下载异常
     */
    public InputStream downloadFile(String bucketName, String objectName) throws Exception {
        try {
            if (!bucketExists(bucketName)) {
                throw new MinioException("存储桶不存在: " + bucketName, ResponseCode.ERROR);
            }

            if (!fileExists(bucketName, objectName)) {
                throw new MinioException("文件不存在: " + objectName, ResponseCode.ERROR, bucketName, objectName);
            }

            // 下载文件
            GetObjectResponse response = minioClient.getObject(new GetObjectArgs.Builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());

            return response;
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e, "文件下载失败: " + objectName, ResponseCode.ERROR, bucketName, objectName);
        }
    }

    /**
     * 下载文件到本地
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param localFilePath 本地文件路径
     * @throws Exception 下载异常
     */
    public void downloadFileToLocal(String bucketName, String objectName, String localFilePath) throws Exception {
        try (InputStream inputStream = downloadFile(bucketName, objectName);
             FileOutputStream outputStream = new FileOutputStream(localFilePath)) {
            IOUtils.copy(inputStream, outputStream);
            log.info("文件下载到本地成功: {} -> {}", objectName, localFilePath);
        }
    }

    /**
     * 删除文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @throws Exception 删除异常
     */
    public void deleteFile(String bucketName, String objectName) throws Exception {
        try {
            if (!bucketExists(bucketName)) {
                throw new MinioException("存储桶不存在: " + bucketName, ResponseCode.ERROR);
            }

            // 删除文件
            minioClient.removeObject(new RemoveObjectArgs.Builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());

        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e, "文件删除失败: " + objectName, ResponseCode.ERROR, bucketName, objectName);
        }
    }

    /**
     * 批量删除文件
     * @param bucketName 存储桶名称
     * @param objectNames 对象名称列表
     * @throws Exception 删除异常
     */
    public void deleteFiles(String bucketName, List<String> objectNames) throws Exception {
        try {
            if (!bucketExists(bucketName)) {
                throw new MinioException("存储桶不存在: " + bucketName, ResponseCode.ERROR);
            }

            // 批量删除文件
            List<DeleteObject> deleteObjects = new ArrayList<>();
            for (String objectName : objectNames) {
                deleteObjects.add(new DeleteObject(objectName));
            }
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(new RemoveObjectsArgs.Builder()
                    .bucket(bucketName)
                    .objects(deleteObjects)
                    .build());

            // 处理删除结果
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                log.error("文件删除失败: {}，错误代码: {}，错误消息: {}", error.objectName(), error.code(), error.message());
            }

            log.info("批量删除文件成功，删除数量: {}", objectNames.size());
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e, "批量删除文件失败", ResponseCode.ERROR, bucketName, null);
        }
    }

    /**
     * 复制文件
     * @param sourceBucket 源存储桶
     * @param sourceObject 源对象
     * @param targetBucket 目标存储桶
     * @param targetObject 目标对象
     * @throws Exception 复制异常
     */
    public void copyFile(String sourceBucket, String sourceObject, String targetBucket, String targetObject) throws Exception {
        try {
            if (!bucketExists(sourceBucket)) {
                throw new MinioException("源存储桶不存在: " + sourceBucket, ResponseCode.ERROR);
            }

            if (!bucketExists(targetBucket)) {
                throw new MinioException("目标存储桶不存在: " + targetBucket, ResponseCode.ERROR);
            }

            if (!fileExists(sourceBucket, sourceObject)) {
                throw new MinioException("源文件不存在: " + sourceObject, ResponseCode.ERROR, sourceBucket, sourceObject);
            }

            // 复制文件
            minioClient.copyObject(new CopyObjectArgs.Builder()
                    .bucket(targetBucket)
                    .object(targetObject)
                    .source(new CopySource.Builder()
                            .bucket(sourceBucket)
                            .object(sourceObject)
                            .build())
                    .build());

            log.info("文件复制成功: {} -> {}", sourceObject, targetObject);
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e, "文件复制失败: " + sourceObject + " -> " + targetObject, ResponseCode.ERROR);
        }
    }

    /**
     * 移动文件
     * @param sourceBucket 源存储桶
     * @param sourceObject 源对象
     * @param targetBucket 目标存储桶
     * @param targetObject 目标对象
     * @throws Exception 移动异常
     */
    public void moveFile(String sourceBucket, String sourceObject, String targetBucket, String targetObject) throws Exception {
        try {
            // 复制文件
            copyFile(sourceBucket, sourceObject, targetBucket, targetObject);

            // 删除源文件
            deleteFile(sourceBucket, sourceObject);

            log.info("文件移动成功: {} -> {}", sourceObject, targetObject);
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e, "文件移动失败: " + sourceObject + " -> " + targetObject, ResponseCode.ERROR);
        }
    }

    /**
     * 重命名文件
     * @param bucketName 存储桶名称
     * @param oldObjectName 旧对象名称
     * @param newObjectName 新对象名称
     * @throws Exception 重命名异常
     */
    public void renameFile(String bucketName, String oldObjectName, String newObjectName) throws Exception {
        moveFile(bucketName, oldObjectName, bucketName, newObjectName);
    }

    /**
     * 检查文件是否存在
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return true-存在，false-不存在
     * @throws Exception 检查异常
     */
    public boolean fileExists(String bucketName, String objectName) throws Exception {
        try {
            if (!bucketExists(bucketName)) {
                throw new MinioException("存储桶不存在: " + bucketName, ResponseCode.ERROR);
            }

            minioClient.statObject(new StatObjectArgs.Builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            return true;
        } catch (io.minio.errors.ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            }
            throw e;
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e, "检查文件存在性失败: " + objectName, ResponseCode.ERROR, bucketName, objectName);
        }
    }

    /**
     * 获取文件大小
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 文件大小（字节）
     * @throws Exception 获取异常
     */
    public long getFileSize(String bucketName, String objectName) throws Exception {
        try {
            if (!bucketExists(bucketName)) {
                throw new MinioException("存储桶不存在: " + bucketName, ResponseCode.ERROR);
            }

            StatObjectResponse response = minioClient.statObject(new StatObjectArgs.Builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            return response.size();
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e, "获取文件大小失败: " + objectName, ResponseCode.ERROR, bucketName, objectName);
        }
    }

    // ============================== 目录管理 ==============================

    /**
     * 创建目录
     * @param bucketName 存储桶名称
     * @param directoryName 目录名称
     * @throws Exception 创建异常
     */
    public void createDirectory(String bucketName, String directoryName) throws Exception {
        try {
            if (!bucketExists(bucketName)) {
                throw new MinioException("存储桶不存在: " + bucketName, ResponseCode.ERROR);
            }

            // 确保目录名称以斜杠结尾
            if (!directoryName.endsWith("/")) {
                directoryName += "/";
            }

            // 创建空目录（上传一个空对象）
            minioClient.putObject(new PutObjectArgs.Builder()
                    .bucket(bucketName)
                    .object(directoryName)
                    .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                    .contentType("application/octet-stream")
                    .build());

            log.info("目录创建成功: {}", directoryName);
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e, "创建目录失败: " + directoryName, ResponseCode.ERROR, bucketName, directoryName);
        }
    }

    /**
     * 删除目录
     * @param bucketName 存储桶名称
     * @param directoryName 目录名称
     * @param recursive 是否递归删除
     * @throws Exception 删除异常
     */
    public void deleteDirectory(String bucketName, String directoryName, boolean recursive) throws Exception {
        try {
            if (!bucketExists(bucketName)) {
                throw new MinioException("存储桶不存在: " + bucketName, ResponseCode.ERROR);
            }

            // 确保目录名称以斜杠结尾
            if (!directoryName.endsWith("/")) {
                directoryName += "/";
            }

            if (recursive) {
                // 递归删除目录下所有文件
                List<String> objectNames = listObjects(bucketName, directoryName, true);
                if (!objectNames.isEmpty()) {
                    deleteFiles(bucketName, objectNames);
                }
            } else {
                // 非递归删除，只删除目录本身
                deleteFile(bucketName, directoryName);
            }

            log.info("目录删除成功: {}", directoryName);
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e, "删除目录失败: " + directoryName, ResponseCode.ERROR, bucketName, directoryName);
        }
    }

    /**
     * 列出目录下的文件
     * @param bucketName 存储桶名称
     * @param prefix 前缀（目录名称）
     * @param recursive 是否递归
     * @return 文件列表
     * @throws Exception 列出异常
     */
    public List<String> listObjects(String bucketName, String prefix, boolean recursive) throws Exception {
        try {
            if (!bucketExists(bucketName)) {
                throw new MinioException("存储桶不存在: " + bucketName, ResponseCode.ERROR);
            }

            List<String> objectNames = new ArrayList<>();
            Iterable<Result<Item>> results = minioClient.listObjects(new ListObjectsArgs.Builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .recursive(recursive)
                    .build());

            // 遍历结果
            for (Result<Item> result : results) {
                Item item = result.get();
                objectNames.add(item.objectName());
            }

            log.info("列出目录文件成功: {}，文件数量: {}", prefix, objectNames.size());
            return objectNames;
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e, "列出目录文件失败: " + prefix, ResponseCode.ERROR, bucketName, prefix);
        }
    }

    // ============================== 高级功能 ==============================

    /**
     * 获取文件URL
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 文件访问URL
     * @throws Exception 获取异常
     */
    public String getFileUrl(String bucketName, String objectName) throws Exception {
        try {
            if (!bucketExists(bucketName)) {
                throw new MinioException("存储桶不存在: " + bucketName, ResponseCode.ERROR);
            }

            if (!fileExists(bucketName, objectName)) {
                throw new MinioException("文件不存在: " + objectName, ResponseCode.ERROR, bucketName, objectName);
            }

            // 获取文件URL
            String fileUrl = minioClient.getPresignedObjectUrl(new GetPresignedObjectUrlArgs.Builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .method(Method.GET)
                    .expiry(7 * 24 * 60 * 60) // 7天过期
                    .build());

            return fileUrl;
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e, "获取文件URL失败: " + objectName, ResponseCode.ERROR, bucketName, objectName);
        }
    }

    /**
     * 获取临时访问URL
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expireSeconds 过期时间（秒）
     * @return 临时访问URL
     * @throws Exception 获取异常
     */
    public String getTemporaryUrl(String bucketName, String objectName, int expireSeconds) throws Exception {
        try {
            if (!bucketExists(bucketName)) {
                throw new MinioException("存储桶不存在: " + bucketName, ResponseCode.ERROR);
            }

            if (!fileExists(bucketName, objectName)) {
                throw new MinioException("文件不存在: " + objectName, ResponseCode.ERROR, bucketName, objectName);
            }

            // 获取临时URL
            String temporaryUrl = minioClient.getPresignedObjectUrl(new GetPresignedObjectUrlArgs.Builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .method(Method.GET)
                    .expiry(expireSeconds)
                    .build());

            log.info("获取临时访问URL成功: {}，过期时间: {}秒", objectName, expireSeconds);
            return temporaryUrl;
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e, "获取临时访问URL失败: " + objectName, ResponseCode.ERROR, bucketName, objectName);
        }
    }

    /**
     * 获取文件元数据
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 元数据
     * @throws Exception 获取异常
     */
    public Map<String, String> getFileMetadata(String bucketName, String objectName) throws Exception {
        try {
            if (!bucketExists(bucketName)) {
                throw new MinioException("存储桶不存在: " + bucketName, ResponseCode.ERROR);
            }

            if (!fileExists(bucketName, objectName)) {
                throw new MinioException("文件不存在: " + objectName, ResponseCode.ERROR, bucketName, objectName);
            }

            // 获取文件元数据
            StatObjectResponse response = minioClient.statObject(new StatObjectArgs.Builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());

            // 返回元数据
            Map<String, String> metadata = new HashMap<>();
            metadata.put("contentType", response.contentType());
            metadata.put("contentLength", String.valueOf(response.size()));
            metadata.put("etag", response.etag());
            metadata.put("lastModified", response.lastModified().toString());
            metadata.putAll(response.userMetadata());

            log.info("获取文件元数据成功: {}", objectName);
            return metadata;
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e, "获取文件元数据失败: " + objectName, ResponseCode.ERROR, bucketName, objectName);
        }
    }

    /**
     * 关闭资源
     * @throws Exception 关闭异常
     */
    public void close() throws Exception {
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * 限制输入流大小的包装类
     */
    private static class BoundedInputStream extends InputStream {
        private final InputStream delegate;
        private final long maxBytes;
        private long bytesRead = 0;

        public BoundedInputStream(InputStream delegate, long maxBytes) {
            this.delegate = delegate;
            this.maxBytes = maxBytes;
        }

        @Override
        public int read() throws IOException {
            if (bytesRead >= maxBytes) {
                return -1;
            }
            int result = delegate.read();
            if (result != -1) {
                bytesRead++;
            }
            return result;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (bytesRead >= maxBytes) {
                return -1;
            }
            long remaining = maxBytes - bytesRead;
            int bytesToRead = (int) Math.min(len, remaining);
            int result = delegate.read(b, off, bytesToRead);
            if (result != -1) {
                bytesRead += result;
            }
            return result;
        }

        @Override
        public void close() throws IOException {
            delegate.close();
        }
    }
}
