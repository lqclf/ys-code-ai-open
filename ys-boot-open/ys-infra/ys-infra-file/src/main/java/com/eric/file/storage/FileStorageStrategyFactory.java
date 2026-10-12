package com.eric.file.storage;

import com.eric.common.exception.BaseException;
import com.eric.common.response.ResponseCode;
import com.eric.file.properties.FileUploadProperties;
import com.eric.file.storage.impl.LocalFileStorageStrategy;
import com.eric.file.storage.impl.MinioFileStorageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 文件存储策略工厂（简化版）
 * <p>仅支持 local 和 minio 两种存储方式</p>
 *
 * @author: Eric
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
@Component
public class FileStorageStrategyFactory {

    @Autowired
    private FileUploadProperties properties;

    @Autowired(required = false)
    private LocalFileStorageStrategy localFileStorageStrategy;

    @Autowired(required = false)
    private MinioFileStorageStrategy minioFileStorageStrategy;

    /**
     * 获取当前配置的存储策略
     */
    public FileStorageStrategy getStrategy() {
        return getStrategy(null);
    }

    /**
     * 根据存储类型获取存储策略
     * <p>优先级：DTO传入 > yml配置 > local(默认)</p>
     *
     * @param storageType 存储类型
     * @return 文件存储策略
     */
    public FileStorageStrategy getStrategy(String storageType) {
        String finalStorageType = determineStorageType(storageType);
        log.info("使用存储类型: {}", finalStorageType);
        return getStrategyByType(finalStorageType);
    }

    /**
     * 确定最终使用的存储类型
     */
    private String determineStorageType(String frontendStorageType) {
        if (frontendStorageType != null && !frontendStorageType.trim().isEmpty()) {
            return frontendStorageType;
        }
        String configStorageType = properties.getStorageType();
        if (configStorageType != null && !configStorageType.trim().isEmpty()) {
            return configStorageType;
        }
        return "local";
    }

    /**
     * 根据存储类型获取对应的存储策略实现
     */
    private FileStorageStrategy getStrategyByType(String storageType) {
        FileStorageStrategy strategy;
        switch (storageType.toLowerCase()) {
            case "local" -> strategy = localFileStorageStrategy;
            case "minio" -> strategy = minioFileStorageStrategy;
            default -> {
                log.error("未知的存储类型: {}", storageType);
                throw new BaseException("存储策略类型[" + storageType + "]不支持", ResponseCode.ERROR);
            }
        }
        if (strategy == null) {
            log.error("存储策略[{}]未启用或未配置", storageType);
            throw new BaseException("存储策略[" + storageType + "]未启用或未配置，请检查配置文件", ResponseCode.ERROR);
        }
        return strategy;
    }
}
