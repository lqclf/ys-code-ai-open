package com.eric.excel.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * JSON导出处理器
 * 将数据导出为JSON格式文件
 * JSON格式特点：结构化、易于解析、适合API集成和前后端交互
 * @ClassName:   JsonExportHandler
 * @author:      liuQ
 * @date:        2026-04-16 10:00:00
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
public class JsonExportHandler<T> {

    /**
     * ObjectMapper实例（线程安全）
     */
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        // 注册Java 8时间模块
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        // 禁用时间戳格式（使用ISO-8601格式）
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * 导出JSON文件到响应流
     *
     * @param dataList   数据列表
     * @param fileName   文件名（不含扩展名）
     * @param response   HTTP响应
     */
    public void exportToJson(List<T> dataList, String fileName, HttpServletResponse response) {
        exportToJsonWithPrettyFormat(dataList, fileName, false, response);
    }

    /**
     * 导出JSON文件到响应流（支持格式化）
     *
     * @param dataList     数据列表
     * @param fileName     文件名（不含扩展名）
     * @param prettyFormat 是否格式化输出
     * @param response     HTTP响应
     */
    public void exportToJsonWithPrettyFormat(List<T> dataList, String fileName,
                                              boolean prettyFormat, HttpServletResponse response) {
        long startTime = System.currentTimeMillis();
        log.info("开始导出JSON，文件名：{}，数据量：{}", fileName, dataList != null ? dataList.size() : 0);

        try {
            // 设置响应头
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + encodedFileName + ".json");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

            // 序列化数据
            String jsonContent = serializeToJson(dataList, prettyFormat);

            // 写入响应流
            try (PrintWriter writer = response.getWriter()) {
                writer.write(jsonContent);
                writer.flush();
            }

            long costTime = System.currentTimeMillis() - startTime;
            log.info("JSON导出完成，文件名：{}，数据量：{}，耗时：{}ms",
                    fileName, dataList != null ? dataList.size() : 0, costTime);

        } catch (IOException e) {
            log.error("导出JSON失败，文件名：{}", fileName, e);
            throw new RuntimeException("导出JSON失败：" + e.getMessage(), e);
        }
    }

    /**
     * 导出JSON文件到输出流
     *
     * @param dataList   数据列表
     * @param fileName   文件名
     * @param outputStream 输出流
     */
    public void exportToJsonStream(List<T> dataList, String fileName, OutputStream outputStream) {
        exportToJsonStreamWithPrettyFormat(dataList, false, outputStream);
    }

    /**
     * 导出JSON文件到输出流（支持格式化）
     *
     * @param dataList     数据列表
     * @param prettyFormat 是否格式化输出
     * @param outputStream 输出流
     */
    public void exportToJsonStreamWithPrettyFormat(List<T> dataList, boolean prettyFormat,
                                                    OutputStream outputStream) {
        long startTime = System.currentTimeMillis();
        log.info("开始导出JSON到流，数据量：{}", dataList != null ? dataList.size() : 0);

        try {
            // 序列化数据
            String jsonContent = serializeToJson(dataList, prettyFormat);

            // 写入输出流
            outputStream.write(jsonContent.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

            long costTime = System.currentTimeMillis() - startTime;
            log.info("JSON流导出完成，数据量：{}，耗时：{}ms",
                    dataList != null ? dataList.size() : 0, costTime);

        } catch (IOException e) {
            log.error("导出JSON到流失败", e);
            throw new RuntimeException("导出JSON到流失败：" + e.getMessage(), e);
        }
    }

    /**
     * 将Map数据导出为JSON
     * 适用于动态列场景
     *
     * @param dataList   Map数据列表
     * @param fileName   文件名
     * @param response   HTTP响应
     */
    public void exportJsonWithDynamicData(List<Map<String, Object>> dataList,
                                           String fileName, HttpServletResponse response) {
        exportJsonWithDynamicDataAndFormat(dataList, fileName, false, response);
    }

    /**
     * 将Map数据导出为JSON（支持格式化）
     *
     * @param dataList     Map数据列表
     * @param fileName     文件名
     * @param prettyFormat 是否格式化输出
     * @param response     HTTP响应
     */
    public void exportJsonWithDynamicDataAndFormat(List<Map<String, Object>> dataList,
                                                    String fileName, boolean prettyFormat,
                                                    HttpServletResponse response) {
        long startTime = System.currentTimeMillis();
        log.info("开始导出动态JSON，文件名：{}，数据量：{}", fileName, dataList != null ? dataList.size() : 0);

        try {
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + encodedFileName + ".json");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

            // 构建标准JSON结构
            Map<String, Object> wrapper = Map.of(
                    "fileName", fileName,
                    "totalCount", dataList != null ? dataList.size() : 0,
                    "data", dataList
            );

            String jsonContent = prettyFormat ?
                    OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(wrapper) :
                    OBJECT_MAPPER.writeValueAsString(wrapper);

            try (PrintWriter writer = response.getWriter()) {
                writer.write(jsonContent);
                writer.flush();
            }

            long costTime = System.currentTimeMillis() - startTime;
            log.info("动态JSON导出完成，文件名：{}，数据量：{}，耗时：{}ms",
                    fileName, dataList != null ? dataList.size() : 0, costTime);

        } catch (IOException e) {
            log.error("导出动态JSON失败，文件名：{}", fileName, e);
            throw new RuntimeException("导出动态JSON失败：" + e.getMessage(), e);
        }
    }

    /**
     * 序列化数据为JSON字符串
     *
     * @param dataList     数据列表
     * @param prettyFormat 是否格式化
     * @return JSON字符串
     */
    private String serializeToJson(List<T> dataList, boolean prettyFormat) {
        try {
            return prettyFormat ?
                    OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(dataList) :
                    OBJECT_MAPPER.writeValueAsString(dataList);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败", e);
            throw new RuntimeException("JSON序列化失败：" + e.getMessage(), e);
        }
    }

    /**
     * 获取JSON格式类型标识
     *
     * @return 格式类型字符串
     */
    public static String getFormatType() {
        return "json";
    }

    /**
     * 获取JSON MIME类型
     *
     * @return MIME类型
     */
    public static String getMimeType() {
        return "application/json";
    }

    /**
     * 获取JSON文件扩展名
     *
     * @return 文件扩展名
     */
    public static String getFileExtension() {
        return ".json";
    }

    /**
     * 获取ObjectMapper实例
     * 用于外部自定义序列化需求
     *
     * @return ObjectMapper实例
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}
