package com.eric.common.constant;

/**
 * 文件上传相关常量
 * @ClassName: FileUploadConstants
 * @author: Eric
 * @date: 2025-09-12
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
public class FileUploadConstants {

    /**
     * 默认最大文件大小 10MB
     */
    public static final long DEFAULT_MAX_FILE_SIZE = 10 * 1024 * 1024L;

    /**
     * 分片上传默认分片大小 2MB
     */
    public static final long DEFAULT_CHUNK_SIZE = 2 * 1024 * 1024L;

    /**
     * 允许的图片类型
     */
    public static final String[] ALLOWED_IMAGE_EXTENSIONS = {
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "ico"
    };

    /**
     * 允许的文档类型
     */
    public static final String[] ALLOWED_DOCUMENT_EXTENSIONS = {
        "doc", "docx", "pdf", "txt", "md", "xls", "xlsx", "ppt", "pptx", "csv"
    };

    /**
     * 允许的视频类型
     */
    public static final String[] ALLOWED_VIDEO_EXTENSIONS = {
        "mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "m4v"
    };

    /**
     * 允许的音频类型
     */
    public static final String[] ALLOWED_AUDIO_EXTENSIONS = {
        "mp3", "wav", "flac", "aac", "ogg", "wma", "m4a"
    };

    /**
     * 允许的压缩包类型
     */
    public static final String[] ALLOWED_ARCHIVE_EXTENSIONS = {
        "zip", "rar", "7z", "tar", "gz", "bz2"
    };

    /**
     * 默认允许的所有文件类型
     */
    public static final String[] DEFAULT_ALLOWED_EXTENSIONS = {
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "ico",
        "doc", "docx", "pdf", "txt", "md", "xls", "xlsx", "ppt", "pptx", "csv",
        "mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "m4v",
        "mp3", "wav", "flac", "aac", "ogg", "wma", "m4a",
        "zip", "rar", "7z", "tar", "gz", "bz2"
    };

    /**
     * 危险文件扩展名黑名单
     */
    public static final String[] DANGEROUS_EXTENSIONS = {
        "exe", "bat", "cmd", "com", "pif", "scr", "vbs", "js", "jar", "jsp", "php", "asp", "aspx"
    };

    /**
     * 文件上传临时目录
     */
    public static final String TEMP_UPLOAD_DIR = "temp";

    /**
     * 文件存储根目录
     */
    public static final String FILE_STORAGE_ROOT = "files";

    /**
     * 缩略图目录
     */
    public static final String THUMBNAIL_DIR = "thumbnails";

    /**
     * 分片文件目录
     */
    public static final String CHUNK_DIR = "chunks";

    /**
     * 默认缩略图宽度
     */
    public static final int DEFAULT_THUMBNAIL_WIDTH = 200;

    /**
     * 默认缩略图高度
     */
    public static final int DEFAULT_THUMBNAIL_HEIGHT = 200;

    /**
     * 文件上传进度缓存前缀
     */
    public static final String UPLOAD_PROGRESS_PREFIX = "upload:progress:";

    /**
     * 分片上传缓存前缀
     */
    public static final String CHUNK_UPLOAD_PREFIX = "upload:chunk:";

    /**
     * 文件去重缓存前缀
     */
    public static final String FILE_DUPLICATE_PREFIX = "file:duplicate:";

    /**
     * 上传临时令牌缓存前缀
     */
    public static final String UPLOAD_TOKEN_PREFIX = "upload:token:";

    /**
     * 临时令牌过期时间(小时)
     */
    public static final int UPLOAD_TOKEN_EXPIRE_HOURS = 2;

    /**
     * 文件下载临时链接过期时间(分钟)
     */
    public static final int DOWNLOAD_LINK_EXPIRE_MINUTES = 30;

}