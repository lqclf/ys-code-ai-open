package com.eric.file.utils;

import com.eric.common.constant.FileUploadConstants;
import com.eric.common.enums.fileupload.FileTypeEnum;
import com.eric.file.validator.FileSecurityValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

/**
 * 文件上传工具类（简化版）
 * <p>仅保留文件名处理、文件大小格式化、扩展名与MIME类型识别等基础工具方法</p>
 *
 * @author: Eric
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
public class FileUploadUtils {

    /**
     * 生成唯一文件名
     *
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    public static String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return timestamp + "_" + uuid + (extension.isEmpty() ? "" : "." + extension);
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名（不包含点）
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 获取不带扩展名的文件名
     */
    public static String getFileNameWithoutExtension(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return filename;
        }
        return filename.substring(0, lastDotIndex);
    }

    /**
     * 校验文件类型是否允许
     */
    public static boolean isAllowedFileType(String filename, String[] allowedTypes) {
        if (allowedTypes == null || allowedTypes.length == 0) {
            return true;
        }
        String extension = getFileExtension(filename);
        if (extension.isEmpty()) {
            return false;
        }
        return Arrays.stream(allowedTypes).anyMatch(type -> type.equalsIgnoreCase(extension));
    }

    /**
     * 是否为危险文件类型
     */
    public static boolean isDangerousFileType(String filename) {
        String extension = getFileExtension(filename);
        if (extension.isEmpty()) {
            return false;
        }
        return Arrays.stream(FileUploadConstants.DANGEROUS_EXTENSIONS)
                .anyMatch(type -> type.equalsIgnoreCase(extension));
    }

    /**
     * 校验文件大小
     */
    public static boolean isValidFileSize(long fileSize, long maxSize) {
        return fileSize > 0 && fileSize <= maxSize;
    }

    /**
     * 格式化文件大小
     */
    public static String formatFileSize(long size) {
        if (size <= 0) {
            return "0 B";
        }
        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        DecimalFormat df = new DecimalFormat("#,##0.#");
        return df.format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * 生成文件存储路径
     *
     * @param basePath 基础路径
     * @param category 文件分类
     * @param filename 文件名
     * @return 文件存储路径
     */
    public static String generateStoragePath(String basePath, String category, String filename) {
        LocalDateTime now = LocalDateTime.now();
        String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return Paths.get(basePath, category, datePath, filename)
                .toString()
                .replace("\\", "/");
    }

    /**
     * 创建目录（如不存在）
     */
    public static void createDirectories(String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            log.error("创建目录失败: {}", dirPath, e);
        }
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String filePath) {
        try {
            return Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            log.error("删除文件失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 检查文件是否存在
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * 获取文件MIME类型
     */
    public static String getContentType(String filename) {
        String extension = getFileExtension(filename);
        if (extension.isEmpty()) {
            return "application/octet-stream";
        }
        return switch (extension) {
            // 图片类型
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            case "ico" -> "image/x-icon";

            // 文档类型
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt" -> "application/vnd.ms-powerpoint";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt" -> "text/plain";
            case "csv" -> "text/csv";
            case "md" -> "text/markdown";

            // 视频类型
            case "mp4" -> "video/mp4";
            case "avi" -> "video/x-msvideo";
            case "mkv" -> "video/x-matroska";
            case "mov" -> "video/quicktime";
            case "wmv" -> "video/x-ms-wmv";
            case "flv" -> "video/x-flv";
            case "webm" -> "video/webm";

            // 音频类型
            case "mp3" -> "audio/mpeg";
            case "wav" -> "audio/wav";
            case "flac" -> "audio/flac";
            case "aac" -> "audio/aac";
            case "ogg" -> "audio/ogg";
            case "wma" -> "audio/x-ms-wma";

            // 压缩包类型
            case "zip" -> "application/zip";
            case "rar" -> "application/x-rar-compressed";
            case "7z" -> "application/x-7z-compressed";
            case "tar" -> "application/x-tar";
            case "gz" -> "application/gzip";

            default -> "application/octet-stream";
        };
    }

    /**
     * 获取文件类型枚举
     */
    public static FileTypeEnum getFileType(String filename) {
        String extension = getFileExtension(filename);
        return FileTypeEnum.getByExtension(extension);
    }

    /**
     * 校验文件名是否安全（不含路径遍历与特殊字符）
     */
    public static boolean isSecureFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return false;
        }
        String dangerousChars = "<>:\"|?*";
        for (char c : dangerousChars.toCharArray()) {
            if (filename.indexOf(c) != -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * 清理文件名，移除不安全字符
     */
    public static String sanitizeFilename(String filename) {
        return FileSecurityValidator.sanitizeFilename(filename);
    }
}
