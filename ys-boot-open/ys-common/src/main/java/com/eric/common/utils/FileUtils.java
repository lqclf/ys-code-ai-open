package com.eric.common.utils;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.zip.*;

/**
 * 文件操作工具类
 * 功能全面的文件处理工具类，提供文件和目录的各种操作功能
 * 
 * 主要功能模块：
 * 1. 文件基础操作：创建、删除、复制、移动、重命名
 * 2. 文件内容处理：文本读写、二进制读写、大文件分块处理
 * 3. 文件属性管理：权限设置、元数据获取、文件校验
 * 4. 文件搜索与过滤：多条件搜索、正则匹配、递归搜索
 * 5. 文件压缩与解压：ZIP、GZIP格式支持，密码保护
 * 6. 目录操作增强：目录遍历、大小计算、结构同步
 * 7. IO流处理：流包装、缓冲优化、自动资源管理
 * 8. 异常处理与日志：完善的异常处理和日志记录
 * 9. 性能优化：缓存机制、NIO支持、线程安全
 * 
 * 使用示例：
 * // 创建文件
 * FileUtils.createFile("test.txt");
 * 
 * // 写入文本内容
 * FileUtils.writeTextFile("test.txt", "Hello World", StandardCharsets.UTF_8);
 * 
 * // 读取文本内容
 * String content = FileUtils.readTextFile("test.txt", StandardCharsets.UTF_8);
 * 
 * // 复制文件
 * FileUtils.copyFile("source.txt", "dest.txt");
 * 
 * // 计算文件MD5
 * String md5 = FileUtils.calculateMD5("test.txt");
 * 
 * // 搜索文件
 * List<File> files = FileUtils.searchFiles("/path/to/dir", "*.txt");
 * 
 * // 压缩目录
 * FileUtils.zipDirectory("/path/to/dir", "/path/to/archive.zip");
 * 
 * // 解压文件
 * FileUtils.unzipFile("/path/to/archive.zip", "/path/to/dest");
 * 
 * @ClassName:  FileUtils
 * @author:     liuQ
 * @date:       2025-01-21
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    // ==================== 常量定义 ====================

    /** 默认缓冲区大小：8KB */
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    /** 大文件缓冲区大小：1MB */
    private static final int LARGE_FILE_BUFFER_SIZE = 1024 * 1024;

    /** 默认字符编码：UTF-8 */
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /** 文件属性缓存 */
    private static final Map<String, BasicFileAttributes> ATTRIBUTES_CACHE = new ConcurrentHashMap<>();

    /** 文件大小缓存 */
    private static final Map<String, Long> SIZE_CACHE = new ConcurrentHashMap<>();

    // ==================== 文件基础操作 ====================

    /**
     * 创建文件（包括父目录）
     * 如果文件已存在，则不执行任何操作
     *
     * @param filePath 文件路径
     * @throws IOException 如果创建文件失败
     */
    public static void createFile(String filePath) throws IOException {
        createFile(filePath, false);
    }

    /**
     * 创建文件（包括父目录）
     *
     * @param filePath 文件路径
     * @param overwrite 是否覆盖已存在的文件
     * @return 创建的文件对象
     * @throws IOException 如果创建文件失败
     */
    public static File createFile(String filePath, boolean overwrite) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        File file = new File(filePath);

        if (file.exists()) {
            if (overwrite) {
                deleteFile(file);
            } else {
                return file;
            }
        }

        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            createDirectory(parentDir.getAbsolutePath());
        }

        if (!file.createNewFile()) {
            throw new IOException("创建文件失败: " + filePath);
        }

        logger.debug("创建文件成功: {}", filePath);
        return file;
    }

    /**
     * 创建目录（包括父目录）
     *
     * @param dirPath 目录路径
     * @throws IOException 如果创建目录失败
     */
    public static void createDirectory(String dirPath) throws IOException {
        Objects.requireNonNull(dirPath, "目录路径不能为空");
        File dir = new File(dirPath);

        if (dir.exists()) {
            if (!dir.isDirectory()) {
                throw new IOException("路径已存在但不是目录: " + dirPath);
            }
            return;
        }

        if (!dir.mkdirs()) {
            throw new IOException("创建目录失败: " + dirPath);
        }

        logger.debug("创建目录成功: {}", dirPath);
    }

    /**
     * 创建临时文件
     * 临时文件会在JVM退出时自动删除
     *
     * @param prefix 文件名前缀
     * @param suffix 文件名后缀
     * @return 创建的临时文件对象
     * @throws IOException 如果创建临时文件失败
     */
    public static File createTempFile(String prefix, String suffix) throws IOException {
        return createTempFile(prefix, suffix, null);
    }

    /**
     * 创建临时文件（指定目录）
     *
     * @param prefix 文件名前缀
     * @param suffix 文件名后缀
     * @param directory 临时文件所在目录，如果为null则使用系统默认临时目录
     * @return 创建的临时文件对象
     * @throws IOException 如果创建临时文件失败
     */
    public static File createTempFile(String prefix, String suffix, File directory) throws IOException {
        File tempFile = File.createTempFile(prefix, suffix, directory);
        tempFile.deleteOnExit();
        logger.debug("创建临时文件成功: {}", tempFile.getAbsolutePath());
        return tempFile;
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 删除是否成功
     */
    public static boolean deleteFile(String filePath) {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        return deleteFile(new File(filePath));
    }

    /**
     * 删除文件
     *
     * @param file 文件对象
     * @return 删除是否成功
     */
    public static boolean deleteFile(File file) {
        Objects.requireNonNull(file, "文件对象不能为空");
        if (!file.exists()) {
            return false;
        }

        boolean result = file.delete();
        if (result) {
            clearCache(file.getAbsolutePath());
            logger.debug("删除文件成功: {}", file.getAbsolutePath());
        }
        return result;
    }

    /**
     * 递归删除目录及其所有内容
     *
     * @param dirPath 目录路径
     * @return 删除是否成功
     */
    public static boolean deleteDirectory(String dirPath) {
        Objects.requireNonNull(dirPath, "目录路径不能为空");
        return deleteDirectory(new File(dirPath));
    }

    /**
     * 递归删除目录及其所有内容
     *
     * @param directory 目录对象
     * @return 删除是否成功
     */
    public static boolean deleteDirectory(File directory) {
        Objects.requireNonNull(directory, "目录对象不能为空");
        if (!directory.exists()) {
            return false;
        }

        if (!directory.isDirectory()) {
            return deleteFile(directory);
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    deleteFile(file);
                }
            }
        }

        boolean result = directory.delete();
        if (result) {
            clearCache(directory.getAbsolutePath());
            logger.debug("删除目录成功: {}", directory.getAbsolutePath());
        }
        return result;
    }

    /**
     * 复制文件
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return 复制是否成功
     * @throws IOException 如果复制失败
     */
    public static boolean copyFile(String sourcePath, String targetPath) throws IOException {
        Objects.requireNonNull(sourcePath, "源文件路径不能为空");
        Objects.requireNonNull(targetPath, "目标文件路径不能为空");
        return copyFile(new File(sourcePath), new File(targetPath));
    }

    /**
     * 复制文件
     *
     * @param sourceFile 源文件对象
     * @param targetFile 目标文件对象
     * @return 复制是否成功
     * @throws IOException 如果复制失败
     */
    public static boolean copyFile(File sourceFile, File targetFile) throws IOException {
        Objects.requireNonNull(sourceFile, "源文件对象不能为空");
        Objects.requireNonNull(targetFile, "目标文件对象不能为空");

        if (!sourceFile.exists()) {
            throw new FileNotFoundException("源文件不存在: " + sourceFile.getAbsolutePath());
        }

        if (sourceFile.isDirectory()) {
            return copyDirectory(sourceFile, targetFile);
        }

        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        logger.debug("复制文件成功: {} -> {}", sourceFile.getAbsolutePath(), targetFile.getAbsolutePath());
        return true;
    }

    /**
     * 使用NIO复制文件（性能更好，适合大文件）
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return 复制是否成功
     * @throws IOException 如果复制失败
     */
    public static boolean copyFileNIO(String sourcePath, String targetPath) throws IOException {
        Objects.requireNonNull(sourcePath, "源文件路径不能为空");
        Objects.requireNonNull(targetPath, "目标文件路径不能为空");
        return copyFileNIO(Paths.get(sourcePath), Paths.get(targetPath));
    }

    /**
     * 使用NIO复制文件（性能更好，适合大文件）
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return 复制是否成功
     * @throws IOException 如果复制失败
     */
    public static boolean copyFileNIO(Path sourcePath, Path targetPath) throws IOException {
        Objects.requireNonNull(sourcePath, "源文件路径不能为空");
        Objects.requireNonNull(targetPath, "目标文件路径不能为空");

        if (!Files.exists(sourcePath)) {
            throw new FileNotFoundException("源文件不存在: " + sourcePath);
        }

        if (Files.isDirectory(sourcePath)) {
            return copyDirectoryNIO(sourcePath, targetPath);
        }

        Files.createDirectories(targetPath.getParent());
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

        logger.debug("使用NIO复制文件成功: {} -> {}", sourcePath, targetPath);
        return true;
    }

    /**
     * 递归复制目录
     *
     * @param sourceDir 源目录
     * @param targetDir 目标目录
     * @return 复制是否成功
     * @throws IOException 如果复制失败
     */
    public static boolean copyDirectory(File sourceDir, File targetDir) throws IOException {
        Objects.requireNonNull(sourceDir, "源目录不能为空");
        Objects.requireNonNull(targetDir, "目标目录不能为空");

        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new IOException("源目录不存在或不是目录: " + sourceDir.getAbsolutePath());
        }

        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        File[] files = sourceDir.listFiles();
        if (files != null) {
            for (File file : files) {
                File targetFile = new File(targetDir, file.getName());
                if (file.isDirectory()) {
                    copyDirectory(file, targetFile);
                } else {
                    copyFile(file, targetFile);
                }
            }
        }

        logger.debug("复制目录成功: {} -> {}", sourceDir.getAbsolutePath(), targetDir.getAbsolutePath());
        return true;
    }

    /**
     * 使用NIO递归复制目录
     *
     * @param sourceDir 源目录路径
     * @param targetDir 目标目录路径
     * @return 复制是否成功
     * @throws IOException 如果复制失败
     */
    public static boolean copyDirectoryNIO(Path sourceDir, Path targetDir) throws IOException {
        Objects.requireNonNull(sourceDir, "源目录路径不能为空");
        Objects.requireNonNull(targetDir, "目标目录路径不能为空");

        if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
            throw new IOException("源目录不存在或不是目录: " + sourceDir);
        }

        Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path target = targetDir.resolve(sourceDir.relativize(dir));
                Files.createDirectories(target);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path target = targetDir.resolve(sourceDir.relativize(file));
                Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });

        logger.debug("使用NIO复制目录成功: {} -> {}", sourceDir, targetDir);
        return true;
    }

    /**
     * 移动文件
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return 移动是否成功
     * @throws IOException 如果移动失败
     */
    public static boolean moveFile(String sourcePath, String targetPath) throws IOException {
        Objects.requireNonNull(sourcePath, "源文件路径不能为空");
        Objects.requireNonNull(targetPath, "目标文件路径不能为空");
        return moveFile(new File(sourcePath), new File(targetPath));
    }

    /**
     * 移动文件
     *
     * @param sourceFile 源文件对象
     * @param targetFile 目标文件对象
     * @return 移动是否成功
     * @throws IOException 如果移动失败
     */
    public static boolean moveFile(File sourceFile, File targetFile) throws IOException {
        Objects.requireNonNull(sourceFile, "源文件对象不能为空");
        Objects.requireNonNull(targetFile, "目标文件对象不能为空");

        if (!sourceFile.exists()) {
            throw new FileNotFoundException("源文件不存在: " + sourceFile.getAbsolutePath());
        }

        if (!sourceFile.renameTo(targetFile)) {
            throw new IOException("移动文件失败: " + sourceFile.getAbsolutePath() + " -> " + targetFile.getAbsolutePath());
        }

        clearCache(sourceFile.getAbsolutePath());
        logger.debug("移动文件成功: {} -> {}", sourceFile.getAbsolutePath(), targetFile.getAbsolutePath());
        return true;
    }

    /**
     * 使用NIO移动文件
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return 移动是否成功
     * @throws IOException 如果移动失败
     */
    public static boolean moveFileNIO(String sourcePath, String targetPath) throws IOException {
        Objects.requireNonNull(sourcePath, "源文件路径不能为空");
        Objects.requireNonNull(targetPath, "目标文件路径不能为空");
        return moveFileNIO(Paths.get(sourcePath), Paths.get(targetPath));
    }

    /**
     * 使用NIO移动文件
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return 移动是否成功
     * @throws IOException 如果移动失败
     */
    public static boolean moveFileNIO(Path sourcePath, Path targetPath) throws IOException {
        Objects.requireNonNull(sourcePath, "源文件路径不能为空");
        Objects.requireNonNull(targetPath, "目标文件路径不能为空");

        if (!Files.exists(sourcePath)) {
            throw new FileNotFoundException("源文件不存在: " + sourcePath);
        }

        Files.createDirectories(targetPath.getParent());
        Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

        clearCache(sourcePath.toString());
        logger.debug("使用NIO移动文件成功: {} -> {}", sourcePath, targetPath);
        return true;
    }

    /**
     * 重命名文件
     *
     * @param filePath 文件路径
     * @param newName 新文件名
     * @return 重命名是否成功
     * @throws IOException 如果重命名失败
     */
    public static boolean renameFile(String filePath, String newName) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        Objects.requireNonNull(newName, "新文件名不能为空");

        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        File newFile = new File(file.getParent(), newName);
        return moveFile(file, newFile);
    }

    // ==================== 文件内容处理 ====================

    /**
     * 写入文本文件（使用默认UTF-8编码）
     *
     * @param filePath 文件路径
     * @param content 文件内容
     * @throws IOException 如果写入失败
     */
    public static void writeTextFile(String filePath, String content) throws IOException {
        writeTextFile(filePath, content, DEFAULT_CHARSET, false);
    }

    /**
     * 写入文本文件（指定字符编码）
     *
     * @param filePath 文件路径
     * @param content 文件内容
     * @param charset 字符编码
     * @throws IOException 如果写入失败
     */
    public static void writeTextFile(String filePath, String content, Charset charset) throws IOException {
        writeTextFile(filePath, content, charset, false);
    }

    /**
     * 写入文本文件
     *
     * @param filePath 文件路径
     * @param content 文件内容
     * @param charset 字符编码
     * @param append 是否追加内容
     * @throws IOException 如果写入失败
     */
    public static void writeTextFile(String filePath, String content, Charset charset, boolean append) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        Objects.requireNonNull(content, "文件内容不能为空");
        Objects.requireNonNull(charset, "字符编码不能为空");

        File file = new File(filePath);
        if (!file.exists()) {
            createFile(filePath);
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file, append), charset))) {
            writer.write(content);
        }

        clearCache(filePath);
        logger.debug("写入文本文件成功: {}", filePath);
    }

    /**
     * 读取文本文件（使用默认UTF-8编码）
     *
     * @param filePath 文件路径
     * @return 文件内容
     * @throws IOException 如果读取失败
     */
    public static String readTextFile(String filePath) throws IOException {
        return readTextFile(filePath, DEFAULT_CHARSET);
    }

    /**
     * 读取文本文件（指定字符编码）
     *
     * @param filePath 文件路径
     * @param charset 字符编码
     * @return 文件内容
     * @throws IOException 如果读取失败
     */
    public static String readTextFile(String filePath, Charset charset) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        Objects.requireNonNull(charset, "字符编码不能为空");

        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), charset))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (!firstLine) {
                    content.append(System.lineSeparator());
                }
                content.append(line);
                firstLine = false;
            }
        }

        logger.debug("读取文本文件成功: {}", filePath);
        return content.toString();
    }

    /**
     * 按行读取文本文件
     *
     * @param filePath 文件路径
     * @return 文件行列表
     * @throws IOException 如果读取失败
     */
    public static List<String> readTextFileLines(String filePath) throws IOException {
        return readTextFileLines(filePath, DEFAULT_CHARSET);
    }

    /**
     * 按行读取文本文件（指定字符编码）
     *
     * @param filePath 文件路径
     * @param charset 字符编码
     * @return 文件行列表
     * @throws IOException 如果读取失败
     */
    public static List<String> readTextFileLines(String filePath, Charset charset) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        Objects.requireNonNull(charset, "字符编码不能为空");

        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        logger.debug("读取文本文件行成功: {}", filePath);
        return lines;
    }

    /**
     * 写入二进制文件
     *
     * @param filePath 文件路径
     * @param data 二进制数据
     * @throws IOException 如果写入失败
     */
    public static void writeBinaryFile(String filePath, byte[] data) throws IOException {
        writeBinaryFile(filePath, data, false);
    }

    /**
     * 写入二进制文件
     *
     * @param filePath 文件路径
     * @param data 二进制数据
     * @param append 是否追加
     * @throws IOException 如果写入失败
     */
    public static void writeBinaryFile(String filePath, byte[] data, boolean append) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        Objects.requireNonNull(data, "二进制数据不能为空");

        File file = new File(filePath);
        if (!file.exists()) {
            createFile(filePath);
        }

        try (BufferedOutputStream out = new BufferedOutputStream(
                new FileOutputStream(file, append))) {
            out.write(data);
        }

        clearCache(filePath);
        logger.debug("写入二进制文件成功: {}", filePath);
    }

    /**
     * 读取二进制文件
     *
     * @param filePath 文件路径
     * @return 二进制数据
     * @throws IOException 如果读取失败
     */
    public static byte[] readBinaryFile(String filePath) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");

        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
        }

        logger.debug("读取二进制文件成功: {}", filePath);
        return baos.toByteArray();
    }

    /**
     * 分块读取大文件
     * 适合处理大文件，避免内存溢出
     *
     * @param filePath 文件路径
     * @param chunkSize 每块大小（字节）
     * @param consumer 每块数据的消费者
     * @throws IOException 如果读取失败
     */
    public static void readLargeFileInChunks(String filePath, int chunkSize, Consumer<byte[]> consumer) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        Objects.requireNonNull(consumer, "消费者不能为空");

        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[chunkSize];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                if (bytesRead < buffer.length) {
                    byte[] chunk = new byte[bytesRead];
                    System.arraycopy(buffer, 0, chunk, 0, bytesRead);
                    consumer.accept(chunk);
                } else {
                    consumer.accept(buffer);
                }
            }
        }

        logger.debug("分块读取大文件成功: {}", filePath);
    }

    /**
     * 使用NIO分块读取大文件
     * 性能更好，适合超大文件
     *
     * @param filePath 文件路径
     * @param chunkSize 每块大小（字节）
     * @param consumer 每块数据的消费者
     * @throws IOException 如果读取失败
     */
    public static void readLargeFileInChunksNIO(String filePath, int chunkSize, Consumer<ByteBuffer> consumer) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        Objects.requireNonNull(consumer, "消费者不能为空");

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(chunkSize);
            while (channel.read(buffer) != -1) {
                buffer.flip();
                consumer.accept(buffer);
                buffer.clear();
            }
        }

        logger.debug("使用NIO分块读取大文件成功: {}", filePath);
    }

    /**
     * 分块写入大文件
     * 适合处理大文件，避免内存溢出
     *
     * @param filePath 文件路径
     * @param chunkSize 每块大小（字节）
     * @param supplier 数据块提供者，返回null表示结束
     * @throws IOException 如果写入失败
     */
    public static void writeLargeFileInChunks(String filePath, int chunkSize, java.util.function.Supplier<byte[]> supplier) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        Objects.requireNonNull(supplier, "数据提供者不能为空");

        File file = new File(filePath);
        if (!file.exists()) {
            createFile(filePath);
        }

        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            byte[] chunk;
            while ((chunk = supplier.get()) != null) {
                out.write(chunk, 0, Math.min(chunk.length, chunkSize));
            }
        }

        clearCache(filePath);
        logger.debug("分块写入大文件成功: {}", filePath);
    }

    /**
     * 使用NIO分块写入大文件
     * 性能更好，适合超大文件
     *
     * @param filePath 文件路径
     * @param chunkSize 每块大小（字节）
     * @param supplier 数据块提供者，返回null表示结束
     * @throws IOException 如果写入失败
     */
    public static void writeLargeFileInChunksNIO(String filePath, java.util.function.Supplier<ByteBuffer> supplier) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        Objects.requireNonNull(supplier, "数据提供者不能为空");

        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());

        try (FileChannel channel = FileChannel.open(path, 
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            ByteBuffer buffer;
            while ((buffer = supplier.get()) != null) {
                channel.write(buffer);
            }
        }

        clearCache(filePath);
        logger.debug("使用NIO分块写入大文件成功: {}", filePath);
    }

    // ==================== 文件属性管理 ====================

    /**
     * 获取文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小（字节）
     * @throws IOException 如果获取失败
     */
    public static long getFileSize(String filePath) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");

        Long cachedSize = SIZE_CACHE.get(filePath);
        if (cachedSize != null) {
            return cachedSize;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        long size = file.length();
        SIZE_CACHE.put(filePath, size);
        return size;
    }

    /**
     * 获取目录大小（递归计算）
     *
     * @param dirPath 目录路径
     * @return 目录大小（字节）
     * @throws IOException 如果计算失败
     */
    public static long getDirectorySize(String dirPath) throws IOException {
        Objects.requireNonNull(dirPath, "目录路径不能为空");

        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("目录不存在或不是目录: " + dirPath);
        }

        long size = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    size += getDirectorySize(file.getAbsolutePath());
                } else {
                    size += file.length();
                }
            }
        }

        return size;
    }

    /**
     * 使用NIO获取文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小（字节）
     * @throws IOException 如果获取失败
     */
    public static long getFileSizeNIO(String filePath) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        return Files.size(path);
    }

    /**
     * 获取文件扩展名
     *
     * @param filePath 文件路径
     * @return 文件扩展名（不包含点），如果没有扩展名则返回空字符串
     */
    public static String getFileExtension(String filePath) {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        return FilenameUtils.getExtension(filePath);
    }

    /**
     * 获取文件名（不包含路径和扩展名）
     *
     * @param filePath 文件路径
     * @return 文件名（不包含扩展名）
     */
    public static String getFileName(String filePath) {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        return FilenameUtils.getBaseName(filePath);
    }

    /**
     * 获取完整文件名（包含扩展名）
     *
     * @param filePath 文件路径
     * @return 完整文件名
     */
    public static String getFullFileName(String filePath) {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        return FilenameUtils.getName(filePath);
    }

    /**
     * 获取文件创建时间
     *
     * @param filePath 文件路径
     * @return 文件创建时间
     * @throws IOException 如果获取失败
     */
    public static Date getFileCreationTime(String filePath) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        return new Date(attrs.creationTime().toMillis());
    }

    /**
     * 获取文件最后修改时间
     *
     * @param filePath 文件路径
     * @return 文件最后修改时间
     * @throws IOException 如果获取失败
     */
    public static Date getFileLastModifiedTime(String filePath) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");

        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        return new Date(file.lastModified());
    }

    /**
     * 设置文件最后修改时间
     *
     * @param filePath 文件路径
     * @param time 修改时间
     * @return 设置是否成功
     */
    public static boolean setFileLastModifiedTime(String filePath, Date time) {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        Objects.requireNonNull(time, "修改时间不能为空");

        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }

        boolean result = file.setLastModified(time.getTime());
        if (result) {
            clearCache(filePath);
        }
        return result;
    }

    /**
     * 判断文件是否可读
     *
     * @param filePath 文件路径
     * @return 是否可读
     */
    public static boolean isFileReadable(String filePath) {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        File file = new File(filePath);
        return file.exists() && file.canRead();
    }

    /**
     * 判断文件是否可写
     *
     * @param filePath 文件路径
     * @return 是否可写
     */
    public static boolean isFileWritable(String filePath) {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        File file = new File(filePath);
        return file.exists() && file.canWrite();
    }

    /**
     * 判断文件是否可执行
     *
     * @param filePath 文件路径
     * @return 是否可执行
     */
    public static boolean isFileExecutable(String filePath) {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        File file = new File(filePath);
        return file.exists() && file.canExecute();
    }

    /**
     * 设置文件权限（仅支持Unix-like系统）
     *
     * @param filePath 文件路径
     * @param readable 是否可读
     * @param writable 是否可写
     * @param executable 是否可执行
     * @return 设置是否成功
     */
    public static boolean setFilePermissions(String filePath, boolean readable, boolean writable, boolean executable) {
        Objects.requireNonNull(filePath, "文件路径不能为空");

        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }

        boolean result = file.setReadable(readable);
        result = result && file.setWritable(writable);
        result = result && file.setExecutable(executable);

        if (result) {
            logger.debug("设置文件权限成功: {}", filePath);
        }
        return result;
    }

    /**
     * 使用NIO设置文件权限
     *
     * @param filePath 文件路径
     * @param permissions 权限集合
     * @return 设置是否成功
     * @throws IOException 如果设置失败
     */
    public static boolean setFilePermissionsNIO(String filePath, Set<PosixFilePermission> permissions) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        Objects.requireNonNull(permissions, "权限集合不能为空");

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        try {
            Files.setPosixFilePermissions(path, permissions);
            logger.debug("使用NIO设置文件权限成功: {}", filePath);
            return true;
        } catch (UnsupportedOperationException e) {
            logger.warn("当前系统不支持POSIX文件权限: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取文件权限（仅支持Unix-like系统）
     *
     * @param filePath 文件路径
     * @return 权限集合
     * @throws IOException 如果获取失败
     */
    public static Set<PosixFilePermission> getFilePermissions(String filePath) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        try {
            return Files.getPosixFilePermissions(path);
        } catch (UnsupportedOperationException e) {
            logger.warn("当前系统不支持POSIX文件权限: {}", e.getMessage());
            return new HashSet<>();
        }
    }

    /**
     * 计算文件的MD5哈希值
     *
     * @param filePath 文件路径
     * @return MD5哈希值（32位十六进制字符串）
     * @throws IOException 如果计算失败
     */
    public static String calculateMD5(String filePath) throws IOException {
        return calculateFileHash(filePath, "MD5");
    }

    /**
     * 计算文件的SHA-256哈希值
     *
     * @param filePath 文件路径
     * @return SHA-256哈希值（64位十六进制字符串）
     * @throws IOException 如果计算失败
     */
    public static String calculateSHA256(String filePath) throws IOException {
        return calculateFileHash(filePath, "SHA-256");
    }

    /**
     * 计算文件的SHA-1哈希值
     *
     * @param filePath 文件路径
     * @return SHA-1哈希值（40位十六进制字符串）
     * @throws IOException 如果计算失败
     */
    public static String calculateSHA1(String filePath) throws IOException {
        return calculateFileHash(filePath, "SHA-1");
    }

    /**
     * 计算文件的哈希值
     *
     * @param filePath 文件路径
     * @param algorithm 哈希算法（MD5、SHA-1、SHA-256等）
     * @return 哈希值（十六进制字符串）
     * @throws IOException 如果计算失败
     */
    public static String calculateFileHash(String filePath, String algorithm) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        Objects.requireNonNull(algorithm, "哈希算法不能为空");

        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            try (FileInputStream fis = new FileInputStream(file);
                 BufferedInputStream bis = new BufferedInputStream(fis)) {
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }

            byte[] hashBytes = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            logger.debug("计算文件{}哈希值成功: {}", algorithm, filePath);
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("不支持的哈希算法: " + algorithm, e);
        }
    }

    /**
     * 比较两个文件的内容是否相同
     *
     * @param filePath1 第一个文件路径
     * @param filePath2 第二个文件路径
     * @return 文件内容是否相同
     * @throws IOException 如果比较失败
     */
    public static boolean compareFiles(String filePath1, String filePath2) throws IOException {
        Objects.requireNonNull(filePath1, "第一个文件路径不能为空");
        Objects.requireNonNull(filePath2, "第二个文件路径不能为空");

        File file1 = new File(filePath1);
        File file2 = new File(filePath2);

        if (!file1.exists() || !file2.exists()) {
            return false;
        }

        if (file1.length() != file2.length()) {
            return false;
        }

        try (FileInputStream fis1 = new FileInputStream(file1);
             FileInputStream fis2 = new FileInputStream(file2)) {
            int data1, data2;
            while ((data1 = fis1.read()) != -1) {
                data2 = fis2.read();
                if (data1 != data2) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 比较两个文件的哈希值是否相同
     *
     * @param filePath1 第一个文件路径
     * @param filePath2 第二个文件路径
     * @param algorithm 哈希算法
     * @return 文件哈希值是否相同
     * @throws IOException 如果比较失败
     */
    public static boolean compareFilesByHash(String filePath1, String filePath2, String algorithm) throws IOException {
        String hash1 = calculateFileHash(filePath1, algorithm);
        String hash2 = calculateFileHash(filePath2, algorithm);
        return hash1.equals(hash2);
    }

    // ==================== 文件搜索与过滤 ====================

    /**
     * 搜索文件（支持通配符）
     *
     * @param dirPath 搜索目录
     * @param pattern 文件名模式（支持通配符*和?）
     * @return 匹配的文件列表
     */
    public static List<File> searchFiles(String dirPath, String pattern) {
        Objects.requireNonNull(dirPath, "目录路径不能为空");
        Objects.requireNonNull(pattern, "文件名模式不能为空");

        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return Collections.emptyList();
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        List<File> result = new ArrayList<>();
        for (File file : files) {
            if (FilenameUtils.wildcardMatch(file.getName(), pattern)) {
                result.add(file);
            }
            if (file.isDirectory()) {
                result.addAll(searchFiles(file.getAbsolutePath(), pattern));
            }
        }

        return result;
    }

    /**
     * 使用正则表达式搜索文件
     *
     * @param dirPath 搜索目录
     * @param regex 正则表达式
     * @return 匹配的文件列表
     */
    public static List<File> searchFilesByRegex(String dirPath, String regex) {
        Objects.requireNonNull(dirPath, "目录路径不能为空");
        Objects.requireNonNull(regex, "正则表达式不能为空");

        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return Collections.emptyList();
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        List<File> result = new ArrayList<>();
        for (File file : files) {
            if (file.getName().matches(regex)) {
                result.add(file);
            }
            if (file.isDirectory()) {
                result.addAll(searchFilesByRegex(file.getAbsolutePath(), regex));
            }
        }

        return result;
    }

    /**
     * 按文件扩展名搜索文件
     *
     * @param dirPath 搜索目录
     * @param extension 文件扩展名（不包含点）
     * @return 匹配的文件列表
     */
    public static List<File> searchFilesByExtension(String dirPath, String extension) {
        Objects.requireNonNull(dirPath, "目录路径不能为空");
        Objects.requireNonNull(extension, "文件扩展名不能为空");

        return searchFiles(dirPath, "*." + extension);
    }

    /**
     * 按文件大小范围搜索文件
     *
     * @param dirPath 搜索目录
     * @param minSize 最小文件大小（字节），null表示无限制
     * @param maxSize 最大文件大小（字节），null表示无限制
     * @return 匹配的文件列表
     */
    public static List<File> searchFilesBySize(String dirPath, Long minSize, Long maxSize) {
        Objects.requireNonNull(dirPath, "目录路径不能为空");

        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return Collections.emptyList();
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        List<File> result = new ArrayList<>();
        for (File file : files) {
            if (file.isFile()) {
                long size = file.length();
                boolean match = minSize == null || size >= minSize;
                if (maxSize != null && size > maxSize) {
                    match = false;
                }
                if (match) {
                    result.add(file);
                }
            }
            if (file.isDirectory()) {
                result.addAll(searchFilesBySize(file.getAbsolutePath(), minSize, maxSize));
            }
        }

        return result;
    }

    /**
     * 按修改日期范围搜索文件
     *
     * @param dirPath 搜索目录
     * @param startDate 开始日期，null表示无限制
     * @param endDate 结束日期，null表示无限制
     * @return 匹配的文件列表
     */
    public static List<File> searchFilesByDate(String dirPath, Date startDate, Date endDate) {
        Objects.requireNonNull(dirPath, "目录路径不能为空");

        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return Collections.emptyList();
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        List<File> result = new ArrayList<>();
        for (File file : files) {
            if (file.isFile()) {
                long modifiedTime = file.lastModified();
                boolean match = startDate == null || modifiedTime >= startDate.getTime();
                if (endDate != null && modifiedTime > endDate.getTime()) {
                    match = false;
                }
                if (match) {
                    result.add(file);
                }
            }
            if (file.isDirectory()) {
                result.addAll(searchFilesByDate(file.getAbsolutePath(), startDate, endDate));
            }
        }

        return result;
    }

    /**
     * 使用自定义谓词搜索文件
     *
     * @param dirPath 搜索目录
     * @param predicate 文件过滤谓词
     * @return 匹配的文件列表
     */
    public static List<File> searchFilesByPredicate(String dirPath, Predicate<File> predicate) {
        Objects.requireNonNull(dirPath, "目录路径不能为空");
        Objects.requireNonNull(predicate, "过滤谓词不能为空");

        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return Collections.emptyList();
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        List<File> result = new ArrayList<>();
        for (File file : files) {
            if (predicate.test(file)) {
                result.add(file);
            }
            if (file.isDirectory()) {
                result.addAll(searchFilesByPredicate(file.getAbsolutePath(), predicate));
            }
        }

        return result;
    }

    /**
     * 使用NIO搜索文件（性能更好）
     *
     * @param dirPath 搜索目录
     * @param pattern 文件名模式（支持通配符）
     * @return 匹配的文件路径列表
     * @throws IOException 如果搜索失败
     */
    public static List<Path> searchFilesNIO(String dirPath, String pattern) throws IOException {
        Objects.requireNonNull(dirPath, "目录路径不能为空");
        Objects.requireNonNull(pattern, "文件名模式不能为空");

        Path startPath = Paths.get(dirPath);
        if (!Files.exists(startPath) || !Files.isDirectory(startPath)) {
            return Collections.emptyList();
        }

        final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        List<Path> result = new ArrayList<>();

        Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                Path fileName = file.getFileName();
                if (fileName != null && matcher.matches(fileName)) {
                    result.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return result;
    }

    // ==================== 文件压缩与解压 ====================

    /**
     * 压缩文件为ZIP格式
     *
     * @param filePath 要压缩的文件路径
     * @param zipFilePath ZIP文件路径
     * @throws IOException 如果压缩失败
     */
    public static void zipFile(String filePath, String zipFilePath) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        Objects.requireNonNull(zipFilePath, "ZIP文件路径不能为空");

        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            if (file.isDirectory()) {
                zipDirectory(file, file.getName(), zos);
            } else {
                zipFile(file, file.getName(), zos);
            }
        }

        logger.debug("压缩文件成功: {} -> {}", filePath, zipFilePath);
    }

    /**
     * 压缩文件到ZIP输出流
     *
     * @param file 要压缩的文件
     * @param entryName ZIP条目名称
     * @param zos ZIP输出流
     * @throws IOException 如果压缩失败
     */
    private static void zipFile(File file, String entryName, ZipOutputStream zos) throws IOException {
        ZipEntry entry = new ZipEntry(entryName);
        zos.putNextEntry(entry);

        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                zos.write(buffer, 0, bytesRead);
            }
        }

        zos.closeEntry();
    }

    /**
     * 压缩目录到ZIP输出流
     *
     * @param directory 要压缩的目录
     * @param basePath 基础路径
     * @param zos ZIP输出流
     * @throws IOException 如果压缩失败
     */
    private static void zipDirectory(File directory, String basePath, ZipOutputStream zos) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String entryName = basePath + "/" + file.getName();
                if (file.isDirectory()) {
                    zipDirectory(file, entryName, zos);
                } else {
                    zipFile(file, entryName, zos);
                }
            }
        }
    }

    /**
     * 压缩目录为ZIP格式
     *
     * @param dirPath 要压缩的目录路径
     * @param zipFilePath ZIP文件路径
     * @throws IOException 如果压缩失败
     */
    public static void zipDirectory(String dirPath, String zipFilePath) throws IOException {
        Objects.requireNonNull(dirPath, "目录路径不能为空");
        Objects.requireNonNull(zipFilePath, "ZIP文件路径不能为空");

        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("目录不存在或不是目录: " + dirPath);
        }

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            zipDirectory(dir, dir.getName(), zos);
        }

        logger.debug("压缩目录成功: {} -> {}", dirPath, zipFilePath);
    }

    /**
     * 解压ZIP文件
     *
     * @param zipFilePath ZIP文件路径
     * @param destPath 解压目标路径
     * @throws IOException 如果解压失败
     */
    public static void unzipFile(String zipFilePath, String destPath) throws IOException {
        Objects.requireNonNull(zipFilePath, "ZIP文件路径不能为空");
        Objects.requireNonNull(destPath, "解压目标路径不能为空");

        File zipFile = new File(zipFilePath);
        if (!zipFile.exists()) {
            throw new FileNotFoundException("ZIP文件不存在: " + zipFilePath);
        }

        File destDir = new File(destPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File destFile = new File(destDir, entry.getName());

                if (entry.isDirectory()) {
                    destFile.mkdirs();
                } else {
                    File parentDir = destFile.getParentFile();
                    if (parentDir != null && !parentDir.exists()) {
                        parentDir.mkdirs();
                    }

                    try (FileOutputStream fos = new FileOutputStream(destFile);
                         BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                        int bytesRead;
                        while ((bytesRead = zis.read(buffer)) != -1) {
                            bos.write(buffer, 0, bytesRead);
                        }
                    }
                }

                zis.closeEntry();
            }
        }

        logger.debug("解压ZIP文件成功: {} -> {}", zipFilePath, destPath);
    }

    /**
     * 使用GZIP压缩文件
     *
     * @param filePath 要压缩的文件路径
     * @param gzipFilePath GZIP文件路径
     * @throws IOException 如果压缩失败
     */
    public static void gzipFile(String filePath, String gzipFilePath) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        Objects.requireNonNull(gzipFilePath, "GZIP文件路径不能为空");

        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis);
             FileOutputStream fos = new FileOutputStream(gzipFilePath);
             GZIPOutputStream gzos = new GZIPOutputStream(fos)) {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                gzos.write(buffer, 0, bytesRead);
            }
        }

        logger.debug("GZIP压缩文件成功: {} -> {}", filePath, gzipFilePath);
    }

    /**
     * 解压GZIP文件
     *
     * @param gzipFilePath GZIP文件路径
     * @param destFilePath 解压目标文件路径
     * @throws IOException 如果解压失败
     */
    public static void ungzipFile(String gzipFilePath, String destFilePath) throws IOException {
        Objects.requireNonNull(gzipFilePath, "GZIP文件路径不能为空");
        Objects.requireNonNull(destFilePath, "解压目标文件路径不能为空");

        File gzipFile = new File(gzipFilePath);
        if (!gzipFile.exists()) {
            throw new FileNotFoundException("GZIP文件不存在: " + gzipFilePath);
        }

        try (FileInputStream fis = new FileInputStream(gzipFile);
             GZIPInputStream gzis = new GZIPInputStream(fis);
             FileOutputStream fos = new FileOutputStream(destFilePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = gzis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
        }

        logger.debug("解压GZIP文件成功: {} -> {}", gzipFilePath, destFilePath);
    }

    /**
     * 压缩字节数组为GZIP格式
     *
     * @param data 要压缩的数据
     * @return 压缩后的数据
     * @throws IOException 如果压缩失败
     */
    public static byte[] gzipCompress(byte[] data) throws IOException {
        Objects.requireNonNull(data, "数据不能为空");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
            gzos.write(data);
        }
        return baos.toByteArray();
    }

    /**
     * 解压GZIP字节数组
     *
     * @param compressedData 压缩的数据
     * @return 解压后的数据
     * @throws IOException 如果解压失败
     */
    public static byte[] gzipDecompress(byte[] compressedData) throws IOException {
        Objects.requireNonNull(compressedData, "压缩数据不能为空");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
             GZIPInputStream gzis = new GZIPInputStream(bais)) {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = gzis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
        }
        return baos.toByteArray();
    }

    // ==================== 目录操作增强 ====================

    /**
     * 深度优先遍历目录
     *
     * @param dirPath 目录路径
     * @param consumer 文件消费者
     * @throws IOException 如果遍历失败
     */
    public static void traverseDirectoryDFS(String dirPath, Consumer<File> consumer) throws IOException {
        Objects.requireNonNull(dirPath, "目录路径不能为空");
        Objects.requireNonNull(consumer, "文件消费者不能为空");

        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("目录不存在或不是目录: " + dirPath);
        }

        Stack<File> stack = new Stack<>();
        stack.push(dir);

        while (!stack.isEmpty()) {
            File current = stack.pop();
            consumer.accept(current);

            if (current.isDirectory()) {
                File[] files = current.listFiles();
                if (files != null) {
                    for (File file : files) {
                        stack.push(file);
                    }
                }
            }
        }

        logger.debug("深度优先遍历目录成功: {}", dirPath);
    }

    /**
     * 广度优先遍历目录
     *
     * @param dirPath 目录路径
     * @param consumer 文件消费者
     * @throws IOException 如果遍历失败
     */
    public static void traverseDirectoryBFS(String dirPath, Consumer<File> consumer) throws IOException {
        Objects.requireNonNull(dirPath, "目录路径不能为空");
        Objects.requireNonNull(consumer, "文件消费者不能为空");

        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("目录不存在或不是目录: " + dirPath);
        }

        Queue<File> queue = new LinkedList<>();
        queue.add(dir);

        while (!queue.isEmpty()) {
            File current = queue.poll();
            consumer.accept(current);

            if (current.isDirectory()) {
                File[] files = current.listFiles();
                if (files != null) {
                    for (File file : files) {
                        queue.add(file);
                    }
                }
            }
        }

        logger.debug("广度优先遍历目录成功: {}", dirPath);
    }

    /**
     * 使用NIO遍历目录（支持深度优先）
     *
     * @param dirPath 目录路径
     * @param consumer 文件消费者
     * @throws IOException 如果遍历失败
     */
    public static void traverseDirectoryNIO(String dirPath, Consumer<Path> consumer) throws IOException {
        Objects.requireNonNull(dirPath, "目录路径不能为空");
        Objects.requireNonNull(consumer, "文件消费者不能为空");

        Path startPath = Paths.get(dirPath);
        if (!Files.exists(startPath) || !Files.isDirectory(startPath)) {
            throw new IOException("目录不存在或不是目录: " + dirPath);
        }

        Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                consumer.accept(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                consumer.accept(dir);
                return FileVisitResult.CONTINUE;
            }
        });

        logger.debug("使用NIO遍历目录成功: {}", dirPath);
    }

    /**
     * 获取目录树结构
     *
     * @param dirPath 目录路径
     * @return 目录树结构字符串
     * @throws IOException 如果获取失败
     */
    public static String getDirectoryTree(String dirPath) throws IOException {
        Objects.requireNonNull(dirPath, "目录路径不能为空");

        StringBuilder tree = new StringBuilder();
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("目录不存在或不是目录: " + dirPath);
        }

        buildDirectoryTree(dir, "", tree);
        return tree.toString();
    }

    /**
     * 构建目录树结构
     *
     * @param dir 目录
     * @param prefix 前缀
     * @param tree 树结构字符串构建器
     */
    private static void buildDirectoryTree(File dir, String prefix, StringBuilder tree) {
        tree.append(prefix).append(dir.getName()).append("\n");

        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                boolean isLast = (i == files.length - 1);
                String newPrefix = prefix + (isLast ? "    " : "│   ");
                String connector = isLast ? "└── " : "├── ";

                tree.append(prefix).append(connector).append(file.getName()).append("\n");

                if (file.isDirectory()) {
                    buildDirectoryTree(file, newPrefix, tree);
                }
            }
        }
    }

    /**
     * 比较两个目录的差异
     *
     * @param dirPath1 第一个目录路径
     * @param dirPath2 第二个目录路径
     * @return 差异信息
     * @throws IOException 如果比较失败
     */
    public static String compareDirectories(String dirPath1, String dirPath2) throws IOException {
        Objects.requireNonNull(dirPath1, "第一个目录路径不能为空");
        Objects.requireNonNull(dirPath2, "第二个目录路径不能为空");

        File dir1 = new File(dirPath1);
        File dir2 = new File(dirPath2);

        if (!dir1.exists() || !dir1.isDirectory()) {
            throw new IOException("第一个目录不存在或不是目录: " + dirPath1);
        }
        if (!dir2.exists() || !dir2.isDirectory()) {
            throw new IOException("第二个目录不存在或不是目录: " + dirPath2);
        }

        StringBuilder diff = new StringBuilder();
        Map<String, File> files1 = getFileMap(dir1);
        Map<String, File> files2 = getFileMap(dir2);

        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(files1.keySet());
        allFiles.addAll(files2.keySet());

        for (String relativePath : allFiles) {
            File file1 = files1.get(relativePath);
            File file2 = files2.get(relativePath);

            if (file1 == null) {
                diff.append("仅在第二个目录中存在: ").append(relativePath).append("\n");
            } else if (file2 == null) {
                diff.append("仅在第一个目录中存在: ").append(relativePath).append("\n");
            } else {
                if (file1.isDirectory() != file2.isDirectory()) {
                    diff.append("类型不同: ").append(relativePath).append("\n");
                } else if (!file1.isDirectory() && file1.length() != file2.length()) {
                    diff.append("大小不同: ").append(relativePath)
                        .append(" (").append(file1.length()).append(" vs ").append(file2.length()).append(")\n");
                }
            }
        }

        logger.debug("比较目录差异成功: {} vs {}", dirPath1, dirPath2);
        return diff.toString();
    }

    /**
     * 获取目录文件映射
     *
     * @param dir 目录
     * @return 文件映射（相对路径 -> 文件对象）
     */
    private static Map<String, File> getFileMap(File dir) {
        Map<String, File> fileMap = new HashMap<>();
        buildFileMap(dir, dir.getName(), fileMap);
        return fileMap;
    }

    /**
     * 构建文件映射
     *
     * @param dir 目录
     * @param basePath 基础路径
     * @param fileMap 文件映射
     */
    private static void buildFileMap(File dir, String basePath, Map<String, File> fileMap) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                String relativePath = basePath + "/" + file.getName();
                fileMap.put(relativePath, file);
                if (file.isDirectory()) {
                    buildFileMap(file, relativePath, fileMap);
                }
            }
        }
    }

    /**
     * 同步两个目录
     * 将源目录的内容同步到目标目录
     *
     * @param sourceDirPath 源目录路径
     * @param targetDirPath 目标目录路径
     * @throws IOException 如果同步失败
     */
    public static void syncDirectories(String sourceDirPath, String targetDirPath) throws IOException {
        Objects.requireNonNull(sourceDirPath, "源目录路径不能为空");
        Objects.requireNonNull(targetDirPath, "目标目录路径不能为空");

        File sourceDir = new File(sourceDirPath);
        File targetDir = new File(targetDirPath);

        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new IOException("源目录不存在或不是目录: " + sourceDirPath);
        }

        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        Map<String, File> sourceFiles = getFileMap(sourceDir);

        for (Map.Entry<String, File> entry : sourceFiles.entrySet()) {
            String relativePath = entry.getKey();
            File sourceFile = entry.getValue();
            File targetFile = new File(targetDir, relativePath.substring(sourceDir.getName().length() + 1));

            if (sourceFile.isDirectory()) {
                if (!targetFile.exists()) {
                    targetFile.mkdirs();
                }
            } else {
                if (!targetFile.exists() || sourceFile.length() != targetFile.length()) {
                    copyFile(sourceFile, targetFile);
                }
            }
        }

        logger.debug("同步目录成功: {} -> {}", sourceDirPath, targetDirPath);
    }

    // ==================== IO流处理 ====================

    /**
     * 关闭IO流（忽略异常）
     *
     * @param closeable 可关闭的对象
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                logger.warn("关闭IO流失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 关闭多个IO流（忽略异常）
     *
     * @param closeables 可关闭的对象数组
     */
    public static void closeQuietly(Closeable... closeables) {
        if (closeables != null) {
            for (Closeable closeable : closeables) {
                closeQuietly(closeable);
            }
        }
    }

    /**
     * 创建缓冲输入流
     *
     * @param inputStream 输入流
     * @return 缓冲输入流
     */
    public static BufferedInputStream createBufferedInputStream(InputStream inputStream) {
        return new BufferedInputStream(inputStream, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 创建缓冲输出流
     *
     * @param outputStream 输出流
     * @return 缓冲输出流
     */
    public static BufferedOutputStream createBufferedOutputStream(OutputStream outputStream) {
        return new BufferedOutputStream(outputStream, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 创建缓冲读取器
     *
     * @param reader 读取器
     * @return 缓冲读取器
     */
    public static BufferedReader createBufferedReader(Reader reader) {
        return new BufferedReader(reader, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 创建缓冲写入器
     *
     * @param writer 写入器
     * @return 缓冲写入器
     */
    public static BufferedWriter createBufferedWriter(Writer writer) {
        return new BufferedWriter(writer, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 字节流转字符流
     *
     * @param inputStream 输入流
     * @param charset 字符编码
     * @return 字符读取器
     */
    public static InputStreamReader byteToChar(InputStream inputStream, Charset charset) {
        return new InputStreamReader(inputStream, charset);
    }

    /**
     * 字符流转字节流
     *
     * @param outputStream 输出流
     * @param charset 字符编码
     * @return 输出流写入器
     */
    public static OutputStreamWriter charToByte(OutputStream outputStream, Charset charset) {
        return new OutputStreamWriter(outputStream, charset);
    }

    /**
     * 复制输入流到输出流
     *
     * @param inputStream 输入流
     * @param outputStream 输出流
     * @return 复制的字节数
     * @throws IOException 如果复制失败
     */
    public static long copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        Objects.requireNonNull(inputStream, "输入流不能为空");
        Objects.requireNonNull(outputStream, "输出流不能为空");

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long totalBytes = 0;
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            totalBytes += bytesRead;
        }
        return totalBytes;
    }

    /**
     * 复制输入流到输出流（使用NIO）
     *
     * @param inputStream 输入流
     * @param outputStream 输出流
     * @return 复制的字节数
     * @throws IOException 如果复制失败
     */
    public static long copyStreamNIO(InputStream inputStream, OutputStream outputStream) throws IOException {
        Objects.requireNonNull(inputStream, "输入流不能为空");
        Objects.requireNonNull(outputStream, "输出流不能为空");

        long totalBytes = 0;
        byte[] buffer = new byte[LARGE_FILE_BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            totalBytes += bytesRead;
        }
        return totalBytes;
    }

    /**
     * 读取输入流为字节数组
     *
     * @param inputStream 输入流
     * @return 字节数组
     * @throws IOException 如果读取失败
     */
    public static byte[] readStreamToBytes(InputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream, "输入流不能为空");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copyStream(inputStream, baos);
        return baos.toByteArray();
    }

    /**
     * 读取输入流为字符串
     *
     * @param inputStream 输入流
     * @param charset 字符编码
     * @return 字符串
     * @throws IOException 如果读取失败
     */
    public static String readStreamToString(InputStream inputStream, Charset charset) throws IOException {
        Objects.requireNonNull(inputStream, "输入流不能为空");
        Objects.requireNonNull(charset, "字符编码不能为空");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            StringBuilder content = new StringBuilder();
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (!firstLine) {
                    content.append(System.lineSeparator());
                }
                content.append(line);
                firstLine = false;
            }
            return content.toString();
        }
    }

    /**
     * 写入字符串到输出流
     *
     * @param outputStream 输出流
     * @param content 字符串内容
     * @param charset 字符编码
     * @throws IOException 如果写入失败
     */
    public static void writeStringToStream(OutputStream outputStream, String content, Charset charset) throws IOException {
        Objects.requireNonNull(outputStream, "输出流不能为空");
        Objects.requireNonNull(content, "内容不能为空");
        Objects.requireNonNull(charset, "字符编码不能为空");

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, charset))) {
            writer.write(content);
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 清除缓存
     *
     * @param filePath 文件路径
     */
    private static void clearCache(String filePath) {
        ATTRIBUTES_CACHE.remove(filePath);
        SIZE_CACHE.remove(filePath);
    }

    /**
     * 格式化文件大小
     *
     * @param size 文件大小（字节）
     * @return 格式化后的字符串
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }

    /**
     * 判断文件是否为隐藏文件
     *
     * @param filePath 文件路径
     * @return 是否为隐藏文件
     */
    public static boolean isHidden(String filePath) {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        File file = new File(filePath);
        return file.isHidden();
    }

    /**
     * 获取文件的MIME类型
     *
     * @param filePath 文件路径
     * @return MIME类型
     */
    public static String getMimeType(String filePath) {
        Objects.requireNonNull(filePath, "文件路径不能为空");

        String extension = getFileExtension(filePath).toLowerCase();
        switch (extension) {
            case "txt":
                return "text/plain";
            case "html":
            case "htm":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "json":
                return "application/json";
            case "xml":
                return "application/xml";
            case "pdf":
                return "application/pdf";
            case "zip":
                return "application/zip";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "svg":
                return "image/svg+xml";
            case "mp3":
                return "audio/mpeg";
            case "mp4":
                return "video/mp4";
            case "avi":
                return "video/x-msvideo";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 获取文件相对路径
     *
     * @param basePath 基础路径
     * @param filePath 文件路径
     * @return 相对路径
     */
    public static String getRelativePath(String basePath, String filePath) {
        Objects.requireNonNull(basePath, "基础路径不能为空");
        Objects.requireNonNull(filePath, "文件路径不能为空");

        Path base = Paths.get(basePath).normalize();
        Path file = Paths.get(filePath).normalize();
        return base.relativize(file).toString();
    }

    /**
     * 获取文件绝对路径
     *
     * @param filePath 文件路径
     * @return 绝对路径
     */
    public static String getAbsolutePath(String filePath) {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        return new File(filePath).getAbsolutePath();
    }

    /**
     * 规范化文件路径
     *
     * @param filePath 文件路径
     * @return 规范化后的路径
     */
    public static String normalizePath(String filePath) {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        return Paths.get(filePath).normalize().toString();
    }

    /**
     * 获取临时文件路径
     *
     * @param prefix 文件名前缀
     * @param suffix 文件名后缀
     * @return 临时文件路径
     * @throws IOException 如果创建失败
     */
    public static String getTempFilePath(String prefix, String suffix) throws IOException {
        File tempFile = File.createTempFile(prefix, suffix);
        return tempFile.getAbsolutePath();
    }

    /**
     * 获取系统临时目录
     *
     * @return 临时目录路径
     */
    public static String getTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * 获取用户主目录
     *
     * @return 用户主目录路径
     */
    public static String getUserHomeDirectory() {
        return System.getProperty("user.home");
    }

    /**
     * 获取当前工作目录
     *
     * @return 当前工作目录路径
     */
    public static String getCurrentWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    /**
     * 获取文件系统根目录列表
     *
     * @return 根目录列表
     */
    public static File[] getRootDirectories() {
        return File.listRoots();
    }

    /**
     * 获取磁盘可用空间
     *
     * @param path 路径
     * @return 可用空间（字节）
     */
    public static long getFreeDiskSpace(String path) {
        Objects.requireNonNull(path, "路径不能为空");
        File file = new File(path);
        return file.getFreeSpace();
    }

    /**
     * 获取磁盘总空间
     *
     * @param path 路径
     * @return 总空间（字节）
     */
    public static long getTotalDiskSpace(String path) {
        Objects.requireNonNull(path, "路径不能为空");
        File file = new File(path);
        return file.getTotalSpace();
    }

    /**
     * 获取磁盘可用空间（百分比）
     *
     * @param path 路径
     * @return 可用空间百分比
     */
    public static double getFreeDiskSpacePercentage(String path) {
        long totalSpace = getTotalDiskSpace(path);
        long freeSpace = getFreeDiskSpace(path);
        return (freeSpace * 100.0) / totalSpace;
    }

    /**
     * 检查磁盘空间是否足够
     *
     * @param path 路径
     * @param requiredSpace 需要的空间（字节）
     * @return 是否足够
     */
    public static boolean hasEnoughDiskSpace(String path, long requiredSpace) {
        long freeSpace = getFreeDiskSpace(path);
        return freeSpace >= requiredSpace;
    }

    /**
     * 创建文件锁
     *
     * @param filePath 文件路径
     * @return 文件锁
     * @throws IOException 如果创建失败
     */
    public static FileLock createFileLock(String filePath) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE);
        return channel.lock();
    }

    /**
     * 尝试创建文件锁（非阻塞）
     *
     * @param filePath 文件路径
     * @return 文件锁，如果无法获取则返回null
     * @throws IOException 如果创建失败
     */
    public static FileLock tryFileLock(String filePath) throws IOException {
        Objects.requireNonNull(filePath, "文件路径不能为空");

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE);
        return channel.tryLock();
    }

    /**
     * 监控文件变化（使用NIO）
     *
     * @param dirPath 目录路径
     * @param listener 文件变化监听器
     * @throws IOException 如果监控失败
     */
    public static void watchDirectory(String dirPath, FileChangeListener listener) throws IOException {
        Objects.requireNonNull(dirPath, "目录路径不能为空");
        Objects.requireNonNull(listener, "监听器不能为空");

        Path path = Paths.get(dirPath);
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            throw new IOException("目录不存在或不是目录: " + dirPath);
        }

        WatchService watchService = FileSystems.getDefault().newWatchService();
        path.register(watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE,
            StandardWatchEventKinds.ENTRY_MODIFY);

        Thread watchThread = new Thread(() -> {
            try {
                WatchKey key;
                while ((key = watchService.take()) != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        Path changedPath = (Path) event.context();
                        Path fullPath = path.resolve(changedPath);

                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            listener.onFileCreated(fullPath);
                        } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                            listener.onFileDeleted(fullPath);
                        } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                            listener.onFileModified(fullPath);
                        }
                    }
                    key.reset();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("文件监控被中断: {}", e.getMessage());
            }
        });

        watchThread.setDaemon(true);
        watchThread.start();

        logger.debug("开始监控目录: {}", dirPath);
    }

    /**
     * 文件变化监听器接口
     */
    public interface FileChangeListener {
        void onFileCreated(Path filePath);
        void onFileDeleted(Path filePath);
        void onFileModified(Path filePath);
    }

    /**
     * 清理所有缓存
     */
    public static void clearAllCache() {
        ATTRIBUTES_CACHE.clear();
        SIZE_CACHE.clear();
        logger.debug("清理所有缓存成功");
    }

    /**
     * 获取缓存统计信息
     *
     * @return 缓存统计信息字符串
     */
    public static String getCacheStatistics() {
        return String.format("属性缓存: %d, 大小缓存: %d",
            ATTRIBUTES_CACHE.size(), SIZE_CACHE.size());
    }
}