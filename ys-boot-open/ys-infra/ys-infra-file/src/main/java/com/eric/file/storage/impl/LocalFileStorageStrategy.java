package com.eric.file.storage.impl;

import com.eric.file.properties.FileUploadProperties;
import com.eric.file.storage.FileStorageStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

/**
 * 本地文件存储实现
 * @ClassName: LocalFileStorageStrategy
 * @author: Eric
 * @date: 2025-09-12
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "file.upload.local.enabled", havingValue = "true", matchIfMissing = true)
public class LocalFileStorageStrategy implements FileStorageStrategy {

    /** 文件上传配置属性 */
    private final FileUploadProperties properties;

    /**
     * 上传文件到本地存储（不带元数据）
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
     * 上传文件到本地存储（带元数据，本地存储忽略元数据）
     * @param inputStream 文件输入流
     * @param filePath 存储路径
     * @param contentType 内容类型
     * @param metadata 文件元数据（本地存储忽略）
     * @return 文件URL，失败返回null
     */
    @Override
    public String uploadFile(InputStream inputStream, String filePath, String contentType, Map<String, String> metadata) {
        try {
            String fullPath = getFullPath(filePath);
            Path targetPath = Paths.get(fullPath);
            
            // 创建目录
            Files.createDirectories(targetPath.getParent());
            
            // 保存文件
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            return getFileUrl(filePath);
        } catch (IOException e) {
            log.error("本地文件上传失败: {}", filePath, e);
            return null;
        }
    }

    /**
     * 从本地存储下载文件
     * @param filePath 文件存储路径
     * @return 文件输入流，失败返回null
     */
    @Override
    public InputStream downloadFile(String filePath) {
        try {
            String fullPath = getFullPath(filePath);
            Path path = Paths.get(fullPath);
            
            if (!Files.exists(path)) {
                log.warn("文件不存在: {}", fullPath);
                return null;
            }
            
            return Files.newInputStream(path);
        } catch (IOException e) {
            log.error("本地文件下载失败: {}", filePath, e);
            return null;
        }
    }

    /**
     * 删除本地存储的文件
     * @param filePath 文件存储路径
     * @return true-删除成功，false-删除失败
     */
    @Override
    public boolean deleteFile(String filePath) {
        try {
            String fullPath = getFullPath(filePath);
            Path path = Paths.get(fullPath);
            
            boolean deleted = Files.deleteIfExists(path);
            if (deleted) {
                log.info("文件删除成功: {}", fullPath);
            } else {
                log.warn("文件不存在，无需删除: {}", fullPath);
            }
            return true;
        } catch (IOException e) {
            log.error("本地文件删除失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 检查本地文件是否存在
     * @param filePath 文件存储路径
     * @return true-存在，false-不存在
     */
    @Override
    public boolean fileExists(String filePath) {
        String fullPath = getFullPath(filePath);
        return Files.exists(Paths.get(fullPath));
    }

    /**
     * 获取本地文件大小
     * @param filePath 文件存储路径
     * @return 文件大小（字节），失败返回0
     */
    @Override
    public long getFileSize(String filePath) {
        try {
            String fullPath = getFullPath(filePath);
            Path path = Paths.get(fullPath);
            
            if (!Files.exists(path)) {
                return 0;
            }
            
            return Files.size(path);
        } catch (IOException e) {
            log.error("获取文件大小失败: {}", filePath, e);
            return 0;
        }
    }

    /**
     * 获取本地文件的访问URL
     *
     * @param filePath 文件存储路径
     * @return 文件URL
     */
    @Override
    public String getFileUrl(String filePath) {
        // 本地存储直接返回 URL 路径（由网关/静态资源映射提供）
        return "/files/" + filePath.replace("\\", "/");
    }

    /**
     * 获取本地文件的临时访问URL（本地存储暂不支持，返回普通URL）
     * @param filePath 文件存储路径
     * @param expireMinutes 过期时间（分钟），本地存储忽略
     * @return 文件URL
     */
    @Override
    public String getTemporaryUrl(String filePath, int expireMinutes) {
        // 本地存储暂不支持临时URL，返回普通URL
        // 可以结合Redis生成临时访问令牌
        return getFileUrl(filePath);
    }

    /**
     * 复制本地文件
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return true-复制成功，false-复制失败
     */
    @Override
    public boolean copyFile(String sourcePath, String targetPath) {
        try {
            String sourceFullPath = getFullPath(sourcePath);
            String targetFullPath = getFullPath(targetPath);
            
            Path source = Paths.get(sourceFullPath);
            Path target = Paths.get(targetFullPath);
            
            if (!Files.exists(source)) {
                log.warn("源文件不存在: {}", sourceFullPath);
                return false;
            }
            
            // 创建目标目录
            Files.createDirectories(target.getParent());
            
            // 复制文件
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("文件复制成功: {} -> {}", sourceFullPath, targetFullPath);
            return true;
        } catch (IOException e) {
            log.error("本地文件复制失败: {} -> {}", sourcePath, targetPath, e);
            return false;
        }
    }

    /**
     * 移动本地文件
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return true-移动成功，false-移动失败
     */
    @Override
    public boolean moveFile(String sourcePath, String targetPath) {
        try {
            String sourceFullPath = getFullPath(sourcePath);
            String targetFullPath = getFullPath(targetPath);
            
            Path source = Paths.get(sourceFullPath);
            Path target = Paths.get(targetFullPath);
            
            if (!Files.exists(source)) {
                log.warn("源文件不存在: {}", sourceFullPath);
                return false;
            }
            
            // 创建目标目录
            Files.createDirectories(target.getParent());
            
            // 移动文件
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("文件移动成功: {} -> {}", sourceFullPath, targetFullPath);
            return true;
        } catch (IOException e) {
            log.error("本地文件移动失败: {} -> {}", sourcePath, targetPath, e);
            return false;
        }
    }

    /**
     * 获取存储类型标识
     * @return 存储类型字符串
     */
    @Override
    public String getStorageType() {
        return "local";
    }

    /**
     * 获取文件完整路径
     *
     * @param filePath 相对路径
     * @return 完整路径
     */
    private String getFullPath(String filePath) {
        String basePath = properties.getLocal().getPath();
        if (basePath == null || basePath.isEmpty()) {
            basePath = "/data/files";
        }
        return Paths.get(basePath, filePath).toString();
    }
}
