package com.eric.file.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件安全验证器（简化版）
 *
 * @author: Eric
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
public class FileSecurityValidator {

    /**
     * 文件安全异常（内部使用）
     */
    public static class FileSecurityException extends RuntimeException {
        public FileSecurityException(String message) {
            super(message);
        }
    }

    private static final Set<String> DANGEROUS_PATTERNS = new HashSet<>();
    private static final Set<String> ALLOWED_MIME_TYPES = new HashSet<>();
    
    static {
        initDangerousPatterns();
        initAllowedMimeTypes();
    }
    
    /**
     * 需要进行内容安全检查的文件类型
     * 这些类型可能包含脚本代码或可执行内容
     */
    private static final Set<String> CONTENT_CHECK_REQUIRED_EXTENSIONS = new HashSet<>(Arrays.asList(
        "html", "htm", "xhtml", "xml", "jsp", "asp", "aspx", "php", "js", "json", "svg", "txt", "md", "log"
    ));
    
    /**
     * 内容检查的最大文件大小（10MB），防止超大文件导致性能问题
     */
    private static final long MAX_CONTENT_CHECK_SIZE = 10 * 1024 * 1024;
    
    /**
     * 不需要进行内容安全检查的文件类型
     * 这些类型是二进制文件或已知的文档格式
     */
    private static final Set<String> CONTENT_CHECK_EXEMPT_EXTENSIONS = new HashSet<>(Arrays.asList(
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "ico", "tif", "tiff",
        "mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "m4v", "3gp",
        "mp3", "wav", "flac", "aac", "ogg", "wma", "m4a", "opus", "aiff",
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
        "zip", "rar", "7z", "tar", "gz", "bz2", "xz", "lzma", "tgz"
    ));
    
    /**
     * 初始化危险模式
     * 这些模式只在需要内容检查的文件类型中使用
     */
    private static void initDangerousPatterns() {
        DANGEROUS_PATTERNS.add("<script");
        DANGEROUS_PATTERNS.add("<?php");
        DANGEROUS_PATTERNS.add("<%@");
        DANGEROUS_PATTERNS.add("<%=");
        DANGEROUS_PATTERNS.add("<jsp:");
        DANGEROUS_PATTERNS.add("<asp:");
        DANGEROUS_PATTERNS.add("javascript:");
        DANGEROUS_PATTERNS.add("vbscript:");
        DANGEROUS_PATTERNS.add("onload=");
        DANGEROUS_PATTERNS.add("onerror=");
        DANGEROUS_PATTERNS.add("onclick=");
        DANGEROUS_PATTERNS.add("eval(");
        DANGEROUS_PATTERNS.add("document.cookie");
        DANGEROUS_PATTERNS.add("window.location");
        DANGEROUS_PATTERNS.add("document.write");
    }
    
    /**
     * 初始化允许的MIME类型
     */
    private static void initAllowedMimeTypes() {
        // 图片类型
        ALLOWED_MIME_TYPES.add("image/jpeg");
        ALLOWED_MIME_TYPES.add("image/png");
        ALLOWED_MIME_TYPES.add("image/gif");
        ALLOWED_MIME_TYPES.add("image/bmp");
        ALLOWED_MIME_TYPES.add("image/webp");
        ALLOWED_MIME_TYPES.add("image/svg+xml");
        ALLOWED_MIME_TYPES.add("image/tiff");
        ALLOWED_MIME_TYPES.add("image/x-icon");
        
        // 文档类型
        ALLOWED_MIME_TYPES.add("application/pdf");
        ALLOWED_MIME_TYPES.add("application/msword");
        ALLOWED_MIME_TYPES.add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        ALLOWED_MIME_TYPES.add("application/vnd.ms-excel");
        ALLOWED_MIME_TYPES.add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        ALLOWED_MIME_TYPES.add("application/vnd.ms-powerpoint");
        ALLOWED_MIME_TYPES.add("application/vnd.openxmlformats-officedocument.presentationml.presentation");
        ALLOWED_MIME_TYPES.add("application/vnd.ms-access");
        ALLOWED_MIME_TYPES.add("application/x-sqlite3");
        
        // 文本类型
        ALLOWED_MIME_TYPES.add("text/plain");
        ALLOWED_MIME_TYPES.add("text/csv");
        ALLOWED_MIME_TYPES.add("text/markdown");
        ALLOWED_MIME_TYPES.add("text/html");
        ALLOWED_MIME_TYPES.add("text/xml");
        ALLOWED_MIME_TYPES.add("application/json");
        
        // 视频类型
        ALLOWED_MIME_TYPES.add("video/mp4");
        ALLOWED_MIME_TYPES.add("video/avi");
        ALLOWED_MIME_TYPES.add("video/mkv");
        ALLOWED_MIME_TYPES.add("video/quicktime");
        ALLOWED_MIME_TYPES.add("video/x-ms-wmv");
        ALLOWED_MIME_TYPES.add("video/x-flv");
        ALLOWED_MIME_TYPES.add("video/webm");
        ALLOWED_MIME_TYPES.add("video/x-matroska");
        ALLOWED_MIME_TYPES.add("video/x-msvideo");
        
        // 音频类型
        ALLOWED_MIME_TYPES.add("audio/mpeg");
        ALLOWED_MIME_TYPES.add("audio/mp3");
        ALLOWED_MIME_TYPES.add("audio/wav");
        ALLOWED_MIME_TYPES.add("audio/flac");
        ALLOWED_MIME_TYPES.add("audio/aac");
        ALLOWED_MIME_TYPES.add("audio/ogg");
        ALLOWED_MIME_TYPES.add("audio/wma");
        ALLOWED_MIME_TYPES.add("audio/aiff");
        ALLOWED_MIME_TYPES.add("audio/x-aiff");
        ALLOWED_MIME_TYPES.add("audio/x-flac");
        
        // 压缩文件类型
        ALLOWED_MIME_TYPES.add("application/zip");
        ALLOWED_MIME_TYPES.add("application/x-zip-compressed");
        ALLOWED_MIME_TYPES.add("application/x-rar-compressed");
        ALLOWED_MIME_TYPES.add("application/vnd.rar");
        ALLOWED_MIME_TYPES.add("application/x-7z-compressed");
        ALLOWED_MIME_TYPES.add("application/x-tar");
        ALLOWED_MIME_TYPES.add("application/gzip");
        ALLOWED_MIME_TYPES.add("application/x-gzip");
        ALLOWED_MIME_TYPES.add("application/x-bzip2");
        ALLOWED_MIME_TYPES.add("application/x-xz");
        ALLOWED_MIME_TYPES.add("application/x-compress");
        ALLOWED_MIME_TYPES.add("application/x-archive");
        ALLOWED_MIME_TYPES.add("application/x-lzh");
        ALLOWED_MIME_TYPES.add("application/x-ace");
        ALLOWED_MIME_TYPES.add("application/x-cab");
        ALLOWED_MIME_TYPES.add("application/x-tgz");
        ALLOWED_MIME_TYPES.add("application/x-gtar");

        //代码文件类型
        ALLOWED_MIME_TYPES.add("text/x-c");
        ALLOWED_MIME_TYPES.add("text/x-java");
        ALLOWED_MIME_TYPES.add("text/x-python");
        ALLOWED_MIME_TYPES.add("text/x-ruby");
        ALLOWED_MIME_TYPES.add("text/x-perl");
        ALLOWED_MIME_TYPES.add("text/x-php");
        ALLOWED_MIME_TYPES.add("text/x-javascript");
        ALLOWED_MIME_TYPES.add("text/x-sql");
        ALLOWED_MIME_TYPES.add("text/x-sh");
        ALLOWED_MIME_TYPES.add("text/x-csharp");
        ALLOWED_MIME_TYPES.add("text/x-vb");
        ALLOWED_MIME_TYPES.add("text/x-c++");
        ALLOWED_MIME_TYPES.add("text/x-c++src");
        ALLOWED_MIME_TYPES.add("text/x-c++hdr");
        ALLOWED_MIME_TYPES.add("text/x-makefile");
        ALLOWED_MIME_TYPES.add("text/x-make");


        // 其他类型
        ALLOWED_MIME_TYPES.add("application/octet-stream");
        ALLOWED_MIME_TYPES.add("application/x-iso9660-image");
        ALLOWED_MIME_TYPES.add("application/x-diskimage");
        ALLOWED_MIME_TYPES.add("application/x-virtualbox-vhd");
        ALLOWED_MIME_TYPES.add("application/ova");
        ALLOWED_MIME_TYPES.add("application/xml");
        ALLOWED_MIME_TYPES.add("application/yaml");
        ALLOWED_MIME_TYPES.add("application/x-yaml");
        ALLOWED_MIME_TYPES.add("text/x-yaml");
        ALLOWED_MIME_TYPES.add("text/x-ini");
        ALLOWED_MIME_TYPES.add("text/x-log");
    }
    
    // 危险扩展名列表
    private static final Set<String> DANGEROUS_EXTENSIONS = new HashSet<>(Arrays.asList(
        "exe", "bat", "cmd", "com", "pif", "scr", "vbs", "js", "jar", "jsp", "php", "asp", "aspx",
        "sh", "ps1", "vbs", "wsf", "msi", "dll", "so", "dylib", "app", "deb", "rpm", "dmg"
    ));
    
    // 允许的扩展名列表
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
        // 图片
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "ico", "tif", "tiff",
        // 文档
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "csv", "md", "rtf", "odt", "ods", "odp",
        // 视频
        "mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "m4v", "3gp", "f4v", "asf", "rm", "rmvb",
        // 音频
        "mp3", "wav", "flac", "aac", "ogg", "wma", "m4a", "opus", "aiff", "au", "ra", "amr",
        // 压缩包
        "zip", "rar", "7z", "tar", "gz", "bz2", "xz", "lzma", "z", "arj", "cab", "lha", "ace", "tgz",
        // 代码文件（已移除危险扩展名：js、jsp、php、asp、sh、bat、ps1、vbs、wsf、msi、dll、so）
        "java", "py", "ts", "html", "css", "scss", "sass", "less", "c", "cpp", "cxx", "h", "hpp", "sql", "pl", "rb",
        "hxx", "m", "mm", "go", "vue", "tsx",

        // 其他
        "iso", "img", "vhd", "ova", "sqlite", "db", "log", "xml", "json", "yaml", "yml", "ini", "conf", "cfg"
    ));
    
    /**
     * 验证文件
     */
    public static void validateFile(MultipartFile file) throws FileSecurityException {
        if (file == null || file.isEmpty()) {
            throw new FileSecurityException("文件不能为空");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            throw new FileSecurityException("文件名不能为空");
        }
        
        String extension = getFileExtension(filename);
        
        validateExtension(extension);
        validateMimeType(file, extension);
        validateFilename(filename);
        validateFileContent(file);
    }
    
    /**
     * 验证扩展名
     */
    public static void validateExtension(String extension) throws FileSecurityException {
        if (extension == null || extension.isEmpty()) {
            throw new FileSecurityException("文件扩展名不能为空");
        }
        
        extension = extension.toLowerCase();
        
        if (DANGEROUS_EXTENSIONS.contains(extension)) {
            throw new FileSecurityException("不允许的文件类型: " + extension);
        }
        
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new FileSecurityException("不支持的文件类型: " + extension);
        }
    }
    
    /**
     * 验证MIME类型
     */
    public static void validateMimeType(MultipartFile file, String extension) throws FileSecurityException {
        String contentType = file.getContentType();
        
        if (contentType == null || contentType.isEmpty()) {
            throw new FileSecurityException("无法确定文件MIME类型");
        }
        
        if (!ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new FileSecurityException("不允许的MIME类型: " + contentType);
        }
        
        if (!isMimeTypeMatchExtension(contentType, extension)) {
            throw new FileSecurityException("MIME类型与文件扩展名不匹配: " + contentType + " vs " + extension);
        }
    }
    
    /**
     * 验证文件名
     */
    public static void validateFilename(String filename) throws FileSecurityException {
        if (filename == null || filename.trim().isEmpty()) {
            throw new FileSecurityException("文件名不能为空");
        }
        
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new FileSecurityException("文件名包含非法字符");
        }
        
        String dangerousChars = "<>:\"|?*";
        for (char c : dangerousChars.toCharArray()) {
            if (filename.indexOf(c) != -1) {
                throw new FileSecurityException("文件名包含非法字符: " + c);
            }
        }
        
        if (filename.length() > 255) {
            throw new FileSecurityException("文件名过长，最大255个字符");
        }
    }
    
    /**
     * 验证文件内容
     * 根据文件类型进行差异化检查：
     * - 对于可能包含脚本代码的文件类型（HTML、XML、JSP等），进行严格的内容检查（检查全部内容）
     * - 对于二进制文件或已知文档格式（图片、视频、PDF、Office文档等），跳过内容检查
     */
    public static void validateFileContent(MultipartFile file) throws FileSecurityException {
        String filename = file.getOriginalFilename();
        String extension = getFileExtension(filename);
        
        if (extension == null || extension.isEmpty()) {
            log.warn("文件扩展名为空，跳过内容检查: {}", filename);
            return;
        }
        
        extension = extension.toLowerCase();
        
        if (CONTENT_CHECK_EXEMPT_EXTENSIONS.contains(extension)) {
            log.debug("文件类型豁免内容检查: {}", extension);
            return;
        }
        
        if (!CONTENT_CHECK_REQUIRED_EXTENSIONS.contains(extension)) {
            log.debug("文件类型不在检查列表中，跳过内容检查: {}", extension);
            return;
        }
        
        // 超过最大文件大小的文本文件跳过内容检查，避免性能问题
        if (file.getSize() > MAX_CONTENT_CHECK_SIZE) {
            log.warn("文件超过内容检查最大大小限制({}MB)，跳过内容检查: {}", MAX_CONTENT_CHECK_SIZE / 1024 / 1024, filename);
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineCount = 0;
            
            // 检查全部内容，不再限制行数
            while ((line = reader.readLine()) != null) {
                lineCount++;
                
                for (String pattern : DANGEROUS_PATTERNS) {
                    if (line.toLowerCase().contains(pattern.toLowerCase())) {
                        throw new FileSecurityException(
                            "文件包含可疑内容: " + pattern + " (第" + lineCount + "行)"
                        );
                    }
                }
            }
        } catch (IOException e) {
            log.error("读取文件内容失败: {}", file.getOriginalFilename(), e);
        }
    }
    
    /**
     * 获取文件扩展名
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
     * 检查MIME类型是否与扩展名匹配
     */
    private static boolean isMimeTypeMatchExtension(String mimeType, String extension) {
        String lowerExtension = extension.toLowerCase();
        String lowerMimeType = mimeType.toLowerCase();
        
        // 图片类型
        if (lowerMimeType.startsWith("image/")) {
            return Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "ico", "tif", "tiff").contains(lowerExtension);
        }
        
        // 视频类型
        if (lowerMimeType.startsWith("video/")) {
            return Arrays.asList("mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "m4v", "3gp", "f4v", "asf", "rm", "rmvb").contains(lowerExtension);
        }
        
        // 音频类型
        if (lowerMimeType.startsWith("audio/")) {
            return Arrays.asList("mp3", "wav", "flac", "aac", "ogg", "wma", "m4a", "opus", "aiff", "au", "ra", "amr").contains(lowerExtension);
        }
        
        // 文档类型
        if (lowerMimeType.equals("application/pdf")) {
            return "pdf".equals(lowerExtension);
        }
        
        if (lowerMimeType.contains("word")) {
            return Arrays.asList("doc", "docx").contains(lowerExtension);
        }
        
        if (lowerMimeType.contains("excel")) {
            return Arrays.asList("xls", "xlsx").contains(lowerExtension);
        }
        
        if (lowerMimeType.contains("powerpoint")) {
            return Arrays.asList("ppt", "pptx").contains(lowerExtension);
        }
        
        if (lowerMimeType.contains("access")) {
            return Arrays.asList("mdb", "accdb").contains(lowerExtension);
        }
        
        if (lowerMimeType.contains("sqlite")) {
            return Arrays.asList("sqlite", "db", "sqlite3").contains(lowerExtension);
        }
        
        // 压缩包类型
        if (lowerMimeType.contains("zip") || lowerMimeType.contains("rar") || 
            lowerMimeType.contains("tar") || lowerMimeType.contains("gzip") ||
            lowerMimeType.contains("bzip2")) {
            return Arrays.asList("zip", "rar", "7z", "tar", "gz", "bz2", "xz", "lzma", "z", "arj", "cab", "lha", "ace", "tgz").contains(lowerExtension);
        }
        
        // 文本类型
        if (lowerMimeType.startsWith("text/") || lowerMimeType.equals("application/json") || 
            lowerMimeType.equals("application/xml") || lowerMimeType.equals("text/html")) {
            return Arrays.asList("txt", "csv", "md", "rtf", "odt", "ods", "odp", "xml", "json", "yaml", "yml", "ini", "conf", "cfg", "log").contains(lowerExtension);
        }
        
        // 其他类型
        if (lowerMimeType.contains("java-archive")) {
            return "jar".equals(lowerExtension);
        }
        
        if (lowerMimeType.contains("android.package-archive")) {
            return "apk".equals(lowerExtension);
        }
        
        // 特殊处理：application/octet-stream 类型
        // 这个类型很通用，允许所有允许的扩展名
        if (lowerMimeType.equals("application/octet-stream")) {
            return ALLOWED_EXTENSIONS.contains(lowerExtension);
        }
        
        // 特殊处理：Office 2007+ 格式的 MIME 类型
        // Office 文档可能被识别为 application/zip 或 application/x-zip-compressed
        if ((lowerMimeType.contains("zip") || lowerMimeType.equals("application/vnd.openxmlformats-officedocument")) && 
            ("docx".equals(lowerExtension) || "xlsx".equals(lowerExtension) || "pptx".equals(lowerExtension))) {
            return true;
        }
        
        return true;
    }
    
    /**
     * 清理文件名，移除不安全字符
     */
    public static String sanitizeFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "unnamed_file";
        }
        
        String sanitized = filename.replaceAll("[<>:\"|?*\\\\/]", "_");
        sanitized = sanitized.replaceAll("_{2,}", "_");
        sanitized = sanitized.trim().replaceAll("^_+|_+$", "");
        
        if (sanitized.isEmpty()) {
            return "unnamed_file";
        }
        
        return sanitized;
    }
}