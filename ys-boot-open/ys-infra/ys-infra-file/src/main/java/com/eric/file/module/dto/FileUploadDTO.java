package com.eric.file.module.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件上传请求DTO，用于接收文件上传时的业务参数
 * @ClassName: FileUploadDTO
 * @author: Eric
 * @date: 2025-09-12
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@Schema(name = "FileUploadDTO", description = "文件上传请求")
public class FileUploadDTO {

    /** 业务ID */
    @Schema(description = "业务ID")
    private String businessId;

    /** 文件分类 */
    @Schema(description = "文件分类", example = "image")
    private String category;

    /** 文件描述 */
    @Schema(description = "文件描述")
    private String description;

    /** 是否公开访问 */
    @Schema(description = "是否公开访问", example = "true")
    private Boolean isPublic = true;

    /** 存储类型 (local, minio) */
    @Schema(description = "存储类型 (local, minio)", example = "local")
    private String storageType;

    /** 是否生成缩略图 */
    @Schema(description = "是否生成缩略图", example = "true")
    private Boolean generateThumbnail = false;

    /** 缩略图宽度 */
    @Schema(description = "缩略图宽度", example = "200")
    private Integer thumbnailWidth;

    /** 缩略图高度 */
    @Schema(description = "缩略图高度", example = "200")
    private Integer thumbnailHeight;

    /** 是否启用去重 */
    @Schema(description = "是否启用去重", example = "true")
    private Boolean enableDeduplication = true;

    /** 扩展属性(JSON格式) */
    @Schema(description = "扩展属性(JSON格式)")
    private String extraInfo;
}
