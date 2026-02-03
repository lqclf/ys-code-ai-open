package com.eric.common.utils;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 图片处理工具类
 * @ClassName: ImageUtils
 * @author: liuQ
 * @date: 2025-09-12
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
public class ImageUtils {

    /**
     * 生成缩略图
     * @param sourceFile 源文件路径
     * @param targetFile 目标文件路径
     * @param width 缩略图宽度
     * @param height 缩略图高度
     * @return true-成功，false-失败
     */
    public static boolean generateThumbnail(String sourceFile, String targetFile, int width, int height) {
        try {
            BufferedImage sourceImage = ImageIO.read(new File(sourceFile));
            if (sourceImage == null) {
                log.error("无法读取图片文件: {}", sourceFile);
                return false;
            }

            BufferedImage thumbnail = createThumbnail(sourceImage, width, height);
            
            // 创建目标目录
            String targetDir = new File(targetFile).getParent();
            if (!Files.exists(Paths.get(targetDir))) {
                Files.createDirectories(Paths.get(targetDir));
            }
            
            // 获取文件扩展名
            String extension = FilenameUtils.getExtension(targetFile);
            if (extension.isEmpty()) {
                extension = "jpg";
            }
            
            return ImageIO.write(thumbnail, extension, new File(targetFile));
        } catch (IOException e) {
            log.error("生成缩略图失败: {} -> {}", sourceFile, targetFile, e);
            return false;
        }
    }

    /**
     * 生成缩略图
     * @param inputStream 输入流
     * @param outputStream 输出流
     * @param width 缩略图宽度
     * @param height 缩略图高度
     * @param format 图片格式
     * @return true-成功，false-失败
     */
    public static boolean generateThumbnail(InputStream inputStream, OutputStream outputStream, 
                                          int width, int height, String format) {
        try {
            BufferedImage sourceImage = ImageIO.read(inputStream);
            if (sourceImage == null) {
                log.error("无法读取图片输入流");
                return false;
            }

            BufferedImage thumbnail = createThumbnail(sourceImage, width, height);
            return ImageIO.write(thumbnail, format, outputStream);
        } catch (IOException e) {
            log.error("生成缩略图失败", e);
            return false;
        }
    }

    /**
     * 创建缩略图
     * @param sourceImage 源图片
     * @param width 缩略图宽度
     * @param height 缩略图高度
     * @return 缩略图
     */
    private static BufferedImage createThumbnail(BufferedImage sourceImage, int width, int height) {
        int sourceWidth = sourceImage.getWidth();
        int sourceHeight = sourceImage.getHeight();
        
        // 计算缩放比例，保持长宽比
        double scaleX = (double) width / sourceWidth;
        double scaleY = (double) height / sourceHeight;
        double scale = Math.min(scaleX, scaleY);
        
        int targetWidth = (int) (sourceWidth * scale);
        int targetHeight = (int) (sourceHeight * scale);
        
        BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = thumbnail.createGraphics();
        
        // 设置渲染质量
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 填充白色背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        
        // 计算居中位置
        int x = (width - targetWidth) / 2;
        int y = (height - targetHeight) / 2;
        
        // 绘制缩放后的图片
        g2d.drawImage(sourceImage, x, y, targetWidth, targetHeight, null);
        g2d.dispose();
        
        return thumbnail;
    }

    /**
     * 压缩图片质量
     * @param sourceFile 源文件路径
     * @param targetFile 目标文件路径
     * @param quality 压缩质量 (0.0-1.0)
     * @return true-成功，false-失败
     */
    public static boolean compressImage(String sourceFile, String targetFile, float quality) {
        try {
            BufferedImage sourceImage = ImageIO.read(new File(sourceFile));
            if (sourceImage == null) {
                log.error("无法读取图片文件: {}", sourceFile);
                return false;
            }

            // 创建目标目录
            String targetDir = new File(targetFile).getParent();
            if (!Files.exists(Paths.get(targetDir))) {
                Files.createDirectories(Paths.get(targetDir));
            }

            // 使用JPEG格式压缩
            return ImageIO.write(sourceImage, "jpg", new File(targetFile));
        } catch (IOException e) {
            log.error("压缩图片失败: {} -> {}", sourceFile, targetFile, e);
            return false;
        }
    }

    /**
     * 检查是否为图片文件
     * @param filename 文件名
     * @return true-是图片，false-不是图片
     */
    public static boolean isImageFile(String filename) {
        String extension =  FilenameUtils.getExtension(filename);
        if (extension.isEmpty()) {
            return false;
        }
        
        String[] imageExtensions = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
        for (String ext : imageExtensions) {
            if (ext.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取图片尺寸信息
     * @param filePath 图片文件路径
     * @return 图片尺寸数组 [width, height]，失败返回null
     */
    public static int[] getImageDimensions(String filePath) {
        try {
            BufferedImage image = ImageIO.read(new File(filePath));
            if (image == null) {
                return null;
            }
            return new int[]{image.getWidth(), image.getHeight()};
        } catch (IOException e) {
            log.error("获取图片尺寸失败: {}", filePath, e);
            return null;
        }
    }

    /**
     * 获取图片尺寸信息
     * @param inputStream 图片输入流
     * @return 图片尺寸数组 [width, height]，失败返回null
     */
    public static int[] getImageDimensions(InputStream inputStream) {
        try {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                return null;
            }
            return new int[]{image.getWidth(), image.getHeight()};
        } catch (IOException e) {
            log.error("获取图片尺寸失败", e);
            return null;
        }
    }
}