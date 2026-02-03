package com.eric.common.utils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import net.lingala.zip4j.progress.ProgressMonitor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Zip工具类
 * 功能全面的ZIP压缩工具类
 * 支持功能：
 * 1. 标准ZIP压缩/解压
 * 2. 加密ZIP压缩/解压（AES-256加密）
 * 3. 分卷压缩（ZIP文件分割）
 * 4. 压缩进度监控
 * 5. 多种压缩级别（存储、快速、标准、最佳）
 * 6. 支持中文文件名
 * 7. 内存压缩/解压
 * 8. 自动创建目录结构
 * 9. 批量文件/目录压缩
 * 10. ZIP文件内容查看
 * 使用示例：
 * // 压缩文件夹
 * ZipUtils.compressDirectory("/path/to/source", "/path/to/destination.zip");
 * // 加密压缩
 * ZipUtils.compressDirectory("/path/to/source", "/path/to/encrypted.zip", "password");
 * // 解压
 * ZipUtils.extract("/path/to/archive.zip", "/path/to/destination");
 * // 加密解压
 * ZipUtils.extract("/path/to/encrypted.zip", "/path/to/destination", "password");
 *
 * @ClassName:  ZipUtils
 * @author:     liuQ
 * @date:       2025-06-16 15:11:09
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
public class ZipUtils {

    // 默认压缩参数
    private static final CompressionLevel DEFAULT_COMPRESSION_LEVEL = CompressionLevel.NORMAL;
    private static final CompressionMethod DEFAULT_COMPRESSION_METHOD = CompressionMethod.DEFLATE;
    private static final EncryptionMethod DEFAULT_ENCRYPTION_METHOD = EncryptionMethod.AES;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final long DEFAULT_SPLIT_SIZE = 1024 * 1024 * 100; // 100MB分卷大小

    // 压缩进度状态
    public enum ProgressState {
        STARTED, PROCESSING, COMPLETED, CANCELLED, ERROR
    }

    /**
     * 压缩目录到ZIP文件（无加密）
     *
     * @param sourcePath 源目录路径
     * @param zipFilePath 目标ZIP文件路径
     * @throws ZipException 压缩失败时抛出
     */
    public static void compressDirectory(String sourcePath, String zipFilePath) throws ZipException {
        compressDirectory(sourcePath, zipFilePath, null, DEFAULT_COMPRESSION_LEVEL, null);
    }

    /**
     * 压缩目录到ZIP文件（带密码）
     *
     * @param sourcePath 源目录路径
     * @param zipFilePath 目标ZIP文件路径
     * @param password ZIP文件密码
     * @throws ZipException 压缩失败时抛出
     */
    public static void compressDirectory(String sourcePath, String zipFilePath, String password) throws ZipException {
        compressDirectory(sourcePath, zipFilePath, password, DEFAULT_COMPRESSION_LEVEL, null);
    }

    /**
     * 压缩目录到ZIP文件（带进度监听）
     *
     * @param sourcePath 源目录路径
     * @param zipFilePath 目标ZIP文件路径
     * @param password ZIP文件密码
     * @param progressListener 进度监听器
     * @throws ZipException 压缩失败时抛出
     */
    public static void compressDirectory(String sourcePath, String zipFilePath, String password,
                                         Consumer<ProgressState> progressListener) throws ZipException {
        compressDirectory(sourcePath, zipFilePath, password, DEFAULT_COMPRESSION_LEVEL, progressListener);
    }

    /**
     * 压缩目录到ZIP文件（完整参数）
     *
     * @param sourcePath 源目录路径
     * @param zipFilePath 目标ZIP文件路径
     * @param password ZIP文件密码
     * @param compressionLevel 压缩级别
     * @param progressListener 进度监听器
     * @throws ZipException 压缩失败时抛出
     */
    public static void compressDirectory(String sourcePath, String zipFilePath, String password,
                                         CompressionLevel compressionLevel,
                                         Consumer<ProgressState> progressListener) throws ZipException {
        File sourceDir = new File(sourcePath);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new IllegalArgumentException("Source path must be an existing directory");
        }

        ZipFile zipFile = new ZipFile(zipFilePath);
        zipFile.setCharset(DEFAULT_CHARSET);

        if (password != null && !password.isEmpty()) {
            zipFile.setPassword(password.toCharArray());
        }

        ZipParameters parameters = createZipParameters(compressionLevel, password != null);

        if (progressListener != null) {
            progressListener.accept(ProgressState.STARTED);
        }

        // 添加目录到ZIP
        zipFile.addFolder(sourceDir, parameters);

        if (progressListener != null) {
            progressListener.accept(ProgressState.COMPLETED);
        }
    }

    /**
     * 压缩多个文件到ZIP文件
     *
     * @param filePaths 文件路径列表
     * @param zipFilePath 目标ZIP文件路径
     * @param password ZIP文件密码
     * @throws ZipException 压缩失败时抛出
     */
    public static void compressFiles(List<String> filePaths, String zipFilePath, String password) throws ZipException {
        List<File> filesToAdd = new ArrayList<>();
        for (String path : filePaths) {
            File file = new File(path);
            if (!file.exists()) {
                throw new IllegalArgumentException("File does not exist: " + path);
            }
            filesToAdd.add(file);
        }

        ZipFile zipFile = new ZipFile(zipFilePath);
        zipFile.setCharset(DEFAULT_CHARSET);

        if (password != null && !password.isEmpty()) {
            zipFile.setPassword(password.toCharArray());
        }

        ZipParameters parameters = createZipParameters(DEFAULT_COMPRESSION_LEVEL, password != null);
        zipFile.addFiles(filesToAdd, parameters);
    }

    /**
     * 创建分卷压缩文件
     *
     * @param sourcePath 源目录路径
     * @param zipFilePath 目标ZIP文件路径
     * @param password ZIP文件密码
     * @param splitSize 分卷大小（字节）
     * @throws ZipException 压缩失败时抛出
     */
    public static void createSplitZip(String sourcePath, String zipFilePath, String password, long splitSize)
            throws ZipException {
        File sourceDir = new File(sourcePath);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new IllegalArgumentException("Source path must be an existing directory");
        }

        ZipFile zipFile = new ZipFile(zipFilePath);
        zipFile.setCharset(DEFAULT_CHARSET);

        if (password != null && !password.isEmpty()) {
            zipFile.setPassword(password.toCharArray());
        }

        ZipParameters parameters = createZipParameters(DEFAULT_COMPRESSION_LEVEL, password != null);

        // 设置分卷大小
        zipFile.createSplitZipFileFromFolder(sourceDir, parameters, true, splitSize);
    }

    /**
     * 解压ZIP文件（无密码）
     *
     * @param zipFilePath ZIP文件路径
     * @param destPath 解压目标路径
     * @throws ZipException 解压失败时抛出
     */
    public static void extract(String zipFilePath, String destPath) throws ZipException {
        extract(zipFilePath, destPath, null, null);
    }

    /**
     * 解压ZIP文件（带密码）
     *
     * @param zipFilePath ZIP文件路径
     * @param destPath 解压目标路径
     * @param password ZIP文件密码
     * @throws ZipException 解压失败时抛出
     */
    public static void extract(String zipFilePath, String destPath, String password) throws ZipException {
        extract(zipFilePath, destPath, password, null);
    }

    /**
     * 解压ZIP文件（带进度监听）
     *
     * @param zipFilePath ZIP文件路径
     * @param destPath 解压目标路径
     * @param password ZIP文件密码
     * @param progressListener 进度监听器
     * @throws ZipException 解压失败时抛出
     */
    public static void extract(String zipFilePath, String destPath, String password,
                               Consumer<ProgressState> progressListener) throws ZipException {
        File zipFile = new File(zipFilePath);
        if (!zipFile.exists()) {
            throw new IllegalArgumentException("Zip file does not exist");
        }

        File destDir = new File(destPath);
        if (!destDir.exists() && !destDir.mkdirs()) {
            throw new IllegalArgumentException("Failed to create destination directory");
        }

        ZipFile zip = new ZipFile(zipFile);
        zip.setCharset(DEFAULT_CHARSET);

        if (password != null && !password.isEmpty()) {
            if (zip.isEncrypted()) {
                zip.setPassword(password.toCharArray());
            }
        }

        if (progressListener != null) {
            progressListener.accept(ProgressState.STARTED);
        }

        // 解压文件
        zip.extractAll(destPath);

        if (progressListener != null) {
            progressListener.accept(ProgressState.COMPLETED);
        }
    }

    /**
     * 解压ZIP文件中的特定文件
     *
     * @param zipFilePath ZIP文件路径
     * @param destPath 解压目标路径
     * @param fileNameInZip ZIP中的文件名
     * @param password ZIP文件密码
     * @throws ZipException 解压失败时抛出
     */
    public static void extractFile(String zipFilePath, String destPath,
                                   String fileNameInZip, String password) throws ZipException {
        File zipFile = new File(zipFilePath);
        if (!zipFile.exists()) {
            throw new IllegalArgumentException("Zip file does not exist");
        }

        File destDir = new File(destPath);
        if (!destDir.exists() && !destDir.mkdirs()) {
            throw new IllegalArgumentException("Failed to create destination directory");
        }

        ZipFile zip = new ZipFile(zipFile);
        zip.setCharset(DEFAULT_CHARSET);

        if (password != null && !password.isEmpty()) {
            if (zip.isEncrypted()) {
                zip.setPassword(password.toCharArray());
            }
        }

        // 确保目标目录存在
        File outputFile = new File(destDir, FilenameUtils.getName(fileNameInZip));
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        zip.extractFile(fileNameInZip, destPath, outputFile.getName());
    }

    /**
     * 在内存中压缩字节数组
     *
     * @param data 要压缩的数据
     * @param fileName 压缩后的文件名
     * @return 压缩后的字节数组
     * @throws IOException 压缩失败时抛出
     */
    public static byte[] compressInMemory(byte[] data, String fileName) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            ZipEntry entry = new ZipEntry(fileName);
            zos.putNextEntry(entry);
            zos.write(data);
            zos.closeEntry();

            zos.finish();
            return baos.toByteArray();
        }
    }

    /**
     * 在内存中解压字节数组
     *
     * @param zipData ZIP文件字节数组
     * @return 解压后的字节数组列表
     * @throws IOException 解压失败时抛出
     */
    public static List<byte[]> extractInMemory(byte[] zipData) throws IOException {
        List<byte[]> extractedData = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipData))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                    extractedData.add(baos.toByteArray());
                }
                zis.closeEntry();
            }
        }

        return extractedData;
    }

    /**
     * 获取ZIP文件中的文件列表
     *
     * @param zipFilePath ZIP文件路径
     * @return ZIP中的文件列表
     * @throws ZipException 读取失败时抛出
     */
    public static List<String> listFiles(String zipFilePath) throws ZipException {
        return listFiles(zipFilePath, null);
    }

    /**
     * 获取加密ZIP文件中的文件列表
     *
     * @param zipFilePath ZIP文件路径
     * @param password ZIP文件密码
     * @return ZIP中的文件列表
     * @throws ZipException 读取失败时抛出
     */
    public static List<String> listFiles(String zipFilePath, String password) throws ZipException {
        File zipFile = new File(zipFilePath);
        if (!zipFile.exists()) {
            throw new IllegalArgumentException("Zip file does not exist");
        }

        ZipFile zip = new ZipFile(zipFile);
        zip.setCharset(DEFAULT_CHARSET);

        if (password != null && !password.isEmpty()) {
            if (zip.isEncrypted()) {
                zip.setPassword(password.toCharArray());
            }
        }

        List<FileHeader> headers = zip.getFileHeaders();
        List<String> fileList = new ArrayList<>();

        for (FileHeader header : headers) {
            fileList.add(header.getFileName());
        }

        return fileList;
    }

    /**
     * 检查ZIP文件是否加密
     *
     * @param zipFilePath ZIP文件路径
     * @return 是否加密
     * @throws ZipException 读取失败时抛出
     */
    public static boolean isEncrypted(String zipFilePath) throws ZipException {
        File zipFile = new File(zipFilePath);
        if (!zipFile.exists()) {
            throw new IllegalArgumentException("Zip file does not exist");
        }

        ZipFile zip = new ZipFile(zipFile);
        return zip.isEncrypted();
    }

    /**
     * 添加文件到现有ZIP文件
     *
     * @param zipFilePath ZIP文件路径
     * @param fileToAdd 要添加的文件路径
     * @param password ZIP文件密码
     * @throws ZipException 添加失败时抛出
     */
    public static void addFileToZip(String zipFilePath, String fileToAdd, String password) throws ZipException {
        File zipFile = new File(zipFilePath);
        if (!zipFile.exists()) {
            throw new IllegalArgumentException("Zip file does not exist");
        }

        File file = new File(fileToAdd);
        if (!file.exists()) {
            throw new IllegalArgumentException("File to add does not exist");
        }

        ZipFile zip = new ZipFile(zipFile);
        zip.setCharset(DEFAULT_CHARSET);

        if (password != null && !password.isEmpty()) {
            if (zip.isEncrypted()) {
                zip.setPassword(password.toCharArray());
            }
        }

        ZipParameters parameters = createZipParameters(DEFAULT_COMPRESSION_LEVEL, zip.isEncrypted());
        zip.addFile(file, parameters);
    }

    /**
     * 从ZIP文件中删除文件
     *
     * @param zipFilePath ZIP文件路径
     * @param fileToRemove ZIP中要删除的文件名
     * @throws ZipException 删除失败时抛出
     */
    public static void removeFileFromZip(String zipFilePath, String fileToRemove) throws ZipException {
        File zipFile = new File(zipFilePath);
        if (!zipFile.exists()) {
            throw new IllegalArgumentException("Zip file does not exist");
        }

        ZipFile zip = new ZipFile(zipFile);
        zip.removeFile(fileToRemove);
    }

    /**
     * 压缩目录（使用Java原生API，适合小文件）
     *
     * @param sourceDirPath 源目录路径
     * @param zipFilePath 目标ZIP文件路径
     * @throws IOException 压缩失败时抛出
     */
    public static void compressWithJavaApi(String sourceDirPath, String zipFilePath) throws IOException {
        Path sourceDir = Paths.get(sourceDirPath);
        if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
            throw new IllegalArgumentException("Source path must be an existing directory");
        }

        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path relativePath = sourceDir.relativize(file);
                    ZipEntry zipEntry = new ZipEntry(relativePath.toString().replace("\\", "/"));
                    zos.putNextEntry(zipEntry);
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (!sourceDir.equals(dir)) {
                        Path relativePath = sourceDir.relativize(dir);
                        ZipEntry zipEntry = new ZipEntry(relativePath.toString().replace("\\", "/") + "/");
                        zos.putNextEntry(zipEntry);
                        zos.closeEntry();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            zos.finish();
        }
    }

    /**
     * 计算压缩比
     *
     * @param zipFilePath ZIP文件路径
     * @return 压缩比（原始大小/压缩后大小）
     * @throws ZipException 计算失败时抛出
     */
    public static double calculateCompressionRatio(String zipFilePath) throws ZipException {
        File zipFile = new File(zipFilePath);
        if (!zipFile.exists()) {
            throw new IllegalArgumentException("Zip file does not exist");
        }

        ZipFile zip = new ZipFile(zipFile);
        List<FileHeader> headers = zip.getFileHeaders();

        long compressedSize = zipFile.length();
        long uncompressedSize = 0;

        for (FileHeader header : headers) {
            uncompressedSize += header.getUncompressedSize();
        }

        if (compressedSize == 0) return 0;
        return (double) uncompressedSize / compressedSize;
    }

    /**
     * 创建ZIP参数对象
     *
     * @param compressionLevel 压缩级别
     * @param encrypt 是否加密
     * @return ZIP参数对象
     */
    private static ZipParameters createZipParameters(CompressionLevel compressionLevel, boolean encrypt) {
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(DEFAULT_COMPRESSION_METHOD);
        parameters.setCompressionLevel(compressionLevel);

        if (encrypt) {
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(DEFAULT_ENCRYPTION_METHOD);
        }

        return parameters;
    }

    /**
     * 测试ZIP文件完整性
     *
     * @param zipFilePath ZIP文件路径
     * @return 文件是否完整
     */
    public static boolean testZipIntegrity(String zipFilePath) {
        File zipFile = new File(zipFilePath);
        if (!zipFile.exists()) {
            return false;
        }

        try (ZipFile zip = new ZipFile(zipFile)) {
            return zip.isValidZipFile();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 获取ZIP文件注释
     *
     * @param zipFilePath ZIP文件路径
     * @return ZIP文件注释
     * @throws ZipException 读取失败时抛出
     */
    public static String getZipComment(String zipFilePath) throws ZipException {
        File zipFile = new File(zipFilePath);
        if (!zipFile.exists()) {
            throw new IllegalArgumentException("Zip file does not exist");
        }

        ZipFile zip = new ZipFile(zipFile);
        return zip.getComment();
    }

    /**
     * 设置ZIP文件注释
     *
     * @param zipFilePath ZIP文件路径
     * @param comment 注释内容
     * @throws ZipException 设置失败时抛出
     */
    public static void setZipComment(String zipFilePath, String comment) throws ZipException {
        File zipFile = new File(zipFilePath);
        if (!zipFile.exists()) {
            throw new IllegalArgumentException("Zip file does not exist");
        }

        ZipFile zip = new ZipFile(zipFile);
        zip.setComment(comment);
    }

    /**
     * 合并分卷ZIP文件
     *
     * @param splitZipFiles 分卷文件列表
     * @param outputZipPath 合并后的ZIP文件路径
     * @throws IOException 合并失败时抛出
     */
    public static void mergeSplitZipFiles(List<String> splitZipFiles, String outputZipPath) throws IOException {
        if (splitZipFiles == null || splitZipFiles.isEmpty()) {
            throw new IllegalArgumentException("No split files provided");
        }

        // 确保文件按顺序排列
        Collections.sort(splitZipFiles);

        try (FileOutputStream fos = new FileOutputStream(outputZipPath);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            for (String part : splitZipFiles) {
                File partFile = new File(part);
                if (!partFile.exists()) {
                    throw new FileNotFoundException("Split file not found: " + part);
                }

                try (FileInputStream fis = new FileInputStream(partFile);
                     BufferedInputStream bis = new BufferedInputStream(fis)) {
                    IOUtils.copy(bis, bos);
                }
            }
        }
    }

    /**
     * 监控ZIP操作进度
     *
     * @param zipFile ZIP文件对象
     * @param progressListener 进度监听器
     */
    private static void monitorProgress(ZipFile zipFile, Consumer<ProgressState> progressListener) {
        if (progressListener == null) return;

        new Thread(() -> {
            ProgressMonitor progressMonitor = zipFile.getProgressMonitor();
            progressListener.accept(ProgressState.STARTED);

            while (progressMonitor.getState() == ProgressMonitor.State.BUSY) {
                try {
                    int percentDone = progressMonitor.getPercentDone();
                    // 可以在这里添加进度百分比回调
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    progressListener.accept(ProgressState.CANCELLED);
                    return;
                }
            }

            if (progressMonitor.getResult() == ProgressMonitor.Result.SUCCESS) {
                progressListener.accept(ProgressState.COMPLETED);
            } else if (progressMonitor.getResult() == ProgressMonitor.Result.ERROR) {
                progressListener.accept(ProgressState.ERROR);
            } else if (progressMonitor.getResult() == ProgressMonitor.Result.CANCELLED) {
                progressListener.accept(ProgressState.CANCELLED);
            }
        }).start();
    }

    /**
     * 计算目录大小
     *
     * @param path 目录路径
     * @return 目录大小（字节）
     * @throws IOException 计算失败时抛出
     */
    public static long calculateDirectorySize(String path) throws IOException {
        Path dir = Paths.get(path);
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Path must be an existing directory");
        }

        AtomicLong size = new AtomicLong(0);

        Files.walk(dir)
                .filter(Files::isRegularFile)
                .forEach(p -> {
                    try {
                        size.addAndGet(Files.size(p));
                    } catch (IOException e) {
                        // 忽略无法访问的文件
                    }
                });

        return size.get();
    }
}
