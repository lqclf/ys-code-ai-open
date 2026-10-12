package com.eric.file.module.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eric.common.annotation.enums.EnumTranslate;
import com.eric.common.enums.fileupload.FileStorageTypeEnum;
import com.eric.common.enums.fileupload.FileTypeEnum;
import com.eric.common.enums.fileupload.FileUploadStatusEnum;
import com.eric.common.module.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 文件管理-实体映射类，对应sys_fileinfo表，存储文件的基本信息
 * @ClassName:   SysFileinfo
 * @author:      Eric
 * @date:        2025-09-11 15:29:29
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@TableName("sys_fileinfo")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "SysFileinfo", description = "文件管理")
public class SysFileinfo extends BaseEntity {

    /** 业务id */
    @TableField("business_id")
    @Schema(description = "业务id")
    private String businessId;
    /** 文件唯一标识(MD5+时间戳) */
    @TableField("file_key")
    @Schema(description = "文件唯一标识(MD5+时间戳)")
    private String fileKey;
    /** 文件名 */
    @TableField("file_name")
    @Schema(description = "文件名")
    private String fileName;
    /** 文件哈希值(SHA-256) */
    @TableField("file_hash")
    @Schema(description = "文件哈希值(SHA-256)")
    private String fileHash;
    /** 真实文件名 */
    @TableField("original_filename")
    @Schema(description = "真实文件名")
    private String originalFilename;
    /** 文件种类 */
    @TableField("category")
    @Schema(description = "文件种类")
    private String category;
    /** 文件存储路径 */
    @TableField("file_path")
    @Schema(description = "文件存储路径")
    private String filePath;
    /** 临时路径 */
    @TableField("temp_path")
    @Schema(description = "临时路径")
    private String tempPath;
    /** 文件大小 */
    @TableField("file_size")
    @Schema(description = "文件大小")
    private Long fileSize;
    /** 文件大小描述 */
    @TableField("file_size_str")
    @Schema(description = "文件大小描述")
    private String fileSizeStr;
    /** 文件字节大小 */
    @TableField("byte_size")
    @Schema(description = "文件字节大小")
    private Long byteSize;
    /** 后缀名 */
    @TableField("suffix")
    @Schema(description = "后缀名")
    private String suffix;
    /** 文件类型 */
    @TableField("file_type")
    @Schema(description = "文件类型")
    @EnumTranslate(enumClass = FileTypeEnum.class)
    private String fileType;
    /** 文件MD5值 */
    @TableField("file_md5")
    @Schema(description = "文件MD5值")
    private String fileMd5;
    /** 存储类型(0:本地,1:MinIO) */
    @TableField("storage_type")
    @Schema(description = "存储类型(0:本地,1:MinIO)")
    @EnumTranslate(enumClass = FileStorageTypeEnum.class)
    private String storageType;
    /** MinIO桶名 */
    @TableField("bucket_name")
    @Schema(description = "MinIO桶名")
    private String bucketName;
    /** 是否是目录 */
    @TableField("is_directory")
    @Schema(description = "是否是目录")
    private Integer isDirectory;
    /** 父目录ID */
    @TableField("parent_id")
    @Schema(description = "父目录ID")
    private Long parentId;
    /** 分片信息(JSON格式) */
    @TableField("chunk_info")
    @Schema(description = "分片信息(JSON格式)")
    private String chunkInfo;
    /** 上传状态(0:上传中,1:上传完成,2:上传失败) */
    @TableField("upload_status")
    @Schema(description = "上传状态(0:上传中,1:上传完成,2:上传失败)")
    @EnumTranslate(enumClass = FileUploadStatusEnum.class)
    private Integer uploadStatus;
    /** 下载次数 */
    @TableField("download_count")
    @Schema(description = "下载次数")
    private Long downloadCount;
    /** 目录结构路径 */
    @TableField("path_hierarchy")
    @Schema(description = "目录结构路径")
    private String pathHierarchy;
    /** 缩略图路径 */
    @TableField("thumbnail_path")
    @Schema(description = "缩略图路径")
    private String thumbnailPath;
    /** 文件扩展属性(JSON格式) */
    @TableField("extra_info")
    @Schema(description = "文件扩展属性(JSON格式)")
    private String extraInfo;
}
