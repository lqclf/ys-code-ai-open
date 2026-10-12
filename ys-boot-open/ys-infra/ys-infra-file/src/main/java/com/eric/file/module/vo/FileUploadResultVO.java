package com.eric.file.module.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传结果VO
 * @ClassName: FileUploadResultVO
 * @author: Eric
 * @date: 2025-09-12
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "FileUploadResultVO", description = "文件上传结果")
public class FileUploadResultVO {

    @Schema(description = "文件ID")
    private String fileId;

    @Schema(description = "文件唯一标识")
    private String fileKey;

    @Schema(description = "原始文件名")
    private String originalFilename;

    @Schema(description = "存储文件名")
    private String fileName;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "文件大小描述")
    private String fileSizeStr;

    @Schema(description = "文件类型")
    private String fileType;

    @Schema(description = "文件MD5值")
    private String fileMd5;

    @Schema(description = "文件访问URL")
    private String fileUrl;

    @Schema(description = "缩略图URL")
    private String thumbnailUrl;

    @Schema(description = "存储类型")
    private String storageType;

    @Schema(description = "上传状态")
    private Integer uploadStatus;

    @Schema(description = "上传状态描述")
    private String uploadStatusDesc;

    @Schema(description = "是否存在重复文件")
    private Boolean isDuplicate;

    @Schema(description = "重复文件信息")
    private String duplicateInfo;

    @Schema(description = "业务ID")
    private String businessId;
}