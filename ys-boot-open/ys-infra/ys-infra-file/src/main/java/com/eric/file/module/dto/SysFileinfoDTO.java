package com.eric.file.module.dto;

import com.eric.common.module.domain.BaseEntityDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件管理-DTO层，用于文件信息的参数传递和查询
 * @ClassName:   SysFileinfoDTO
 * @author:      Eric
 * @date:        2025-09-11 15:29:29
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "SysFileinfoDTO", description = "文件管理")
public class SysFileinfoDTO extends BaseEntityDTO {

    /** 业务id */
    @Schema(description = "业务id")
    private String businessId;
    /** 文件唯一标识(MD5+时间戳) */
    @Schema(description = "文件唯一标识(MD5+时间戳)")
    private String fileKey;
    /** 文件名 */
    @Schema(description = "文件名")
    private String fileName;
    /** 文件哈希值(SHA-256) */
    @Schema(description = "文件哈希值(SHA-256)")
    private String fileHash;
    /** 真实文件名 */
    @Schema(description = "真实文件名")
    private String originalFilename;
    /** 文件种类 */
    @Schema(description = "文件种类")
    private String category;
    /** 文件存储路径 */
    @Schema(description = "文件存储路径")
    private String filePath;
    /** 临时路径 */
    @Schema(description = "临时路径")
    private String tempPath;
    /** 文件大小 */
    @Schema(description = "文件大小")
    private Long fileSize;
    /** 文件大小描述 */
    @Schema(description = "文件大小描述")
    private String fileSizeStr;
    /** 文件字节大小 */
    @Schema(description = "文件字节大小")
    private Long byteSize;
    /** 后缀名 */
    @Schema(description = "后缀名")
    private String suffix;
    /** 文件类型 */
    @Schema(description = "文件类型")
    private String fileType;
    /** 文件MD5值 */
    @Schema(description = "文件MD5值")
    private String fileMd5;
    /** 存储类型(0:本地,1:MinIO) */
    @Schema(description = "存储类型(0:本地,1:MinIO)")
    private String storageType;
    /** MinIO桶名 */
    @Schema(description = "MinIO桶名")
    private String bucketName;
    /** 是否是目录 */
    @Schema(description = "是否是目录")
    private Integer isDirectory;
    /** 父目录ID */
    @Schema(description = "父目录ID")
    private Long parentId;
    /** 分片信息(JSON格式) */
    @Schema(description = "分片信息(JSON格式)")
    private String chunkInfo;
    /** 上传状态(0:上传中,1:上传完成,2:上传失败) */
    @Schema(description = "上传状态(0:上传中,1:上传完成,2:上传失败)")
    private Integer uploadStatus;
    /** 下载次数 */
    @Schema(description = "下载次数")
    private Long downloadCount;
    /** 目录结构路径 */
    @Schema(description = "目录结构路径")
    private String pathHierarchy;
    /** 缩略图路径 */
    @Schema(description = "缩略图路径")
    private String thumbnailPath;
    /** 文件扩展属性(JSON格式) */
    @Schema(description = "文件扩展属性(JSON格式)")
    private String extraInfo;
}
