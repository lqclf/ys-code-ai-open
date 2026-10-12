package com.eric.file.constant;

/**
 * 文件上传相关常量（简化版）
 * <p>仅保留基础的文件类型与大小限制常量</p>
 *
 * @author: Eric
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
public class FileUploadConstants {

    /** 默认最大文件大小 50MB */
    public static final long DEFAULT_MAX_FILE_SIZE = 50 * 1024 * 1024L;

    /** 允许的图片类型 */
    public static final String[] ALLOWED_IMAGE_EXTENSIONS = {
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "ico"
    };

    /** 允许的文档类型 */
    public static final String[] ALLOWED_DOCUMENT_EXTENSIONS = {
            "doc", "docx", "pdf", "txt", "md", "xls", "xlsx", "ppt", "pptx", "csv"
    };

    /** 允许的视频类型 */
    public static final String[] ALLOWED_VIDEO_EXTENSIONS = {
            "mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "m4v"
    };

    /** 允许的音频类型 */
    public static final String[] ALLOWED_AUDIO_EXTENSIONS = {
            "mp3", "wav", "flac", "aac", "ogg", "wma", "m4a"
    };

    /** 允许的压缩包类型 */
    public static final String[] ALLOWED_ARCHIVE_EXTENSIONS = {
            "zip", "rar", "7z", "tar", "gz", "bz2"
    };

    /** 默认允许的所有文件类型 */
    public static final String[] DEFAULT_ALLOWED_EXTENSIONS = {
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "ico",
            "doc", "docx", "pdf", "txt", "md", "xls", "xlsx", "ppt", "pptx", "csv",
            "mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "m4v",
            "mp3", "wav", "flac", "aac", "ogg", "wma", "m4a",
            "zip", "rar", "7z", "tar", "gz", "bz2"
    };

    /** 危险文件扩展名黑名单 */
    public static final String[] DANGEROUS_EXTENSIONS = {
            "exe", "bat", "cmd", "com", "pif", "scr", "vbs", "js", "jar", "jsp", "php", "asp", "aspx"
    };
}
