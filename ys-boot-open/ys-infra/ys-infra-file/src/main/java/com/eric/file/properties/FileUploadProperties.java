package com.eric.file.properties;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置类（简化版）
 * <p>
 * 仅支持 local（本地）和 minio 两种存储方式
 * </p>
 *
 * @author: Eric
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {

    /**
     * 文件存储类型（local / minio）
     * <p>默认 local</p>
     */
    private String storageType = "local";

    /**
     * 本地存储配置
     */
    private LocalStorage local = new LocalStorage();

    /**
     * MinIO 配置
     */
    private MinioStorage minio = new MinioStorage();

    /**
     * 文件上传大小限制（字节），默认 50MB
     */
    private Long maxFileSize = 50 * 1024 * 1024L;

    @Data
    public static class LocalStorage {
        /**
         * 本地存储路径
         */
        private String path = "/data/files";
        /**
         * 是否启用该存储策略
         */
        private Boolean enabled = true;
    }

    @Data
    public static class MinioStorage {
        /**
         * MinIO 服务地址
         */
        private String endpoint;
        /**
         * 访问密钥
         */
        private String accessKey;
        /**
         * 密钥
         */
        private String secretKey;
        /**
         * 桶名称
         */
        private String bucketName = "files";
        /**
         * 是否启用 SSL
         */
        private Boolean secure = false;
        /**
         * 是否启用该存储策略
         */
        private Boolean enabled = false;
    }

    /**
     * 初始化方法，在 Windows 系统上自动转换路径格式
     */
    @PostConstruct
    public void init() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            if (local != null && local.getPath() != null
                    && !local.getPath().startsWith("C:")
                    && !local.getPath().startsWith("D:")
                    && !local.getPath().startsWith("E:")
                    && !local.getPath().startsWith("F:")) {
                local.setPath("E:" + local.getPath().replace("/", "\\"));
                log.info("转换 local.path 为: {}", local.getPath());
            }
        }
    }
}
