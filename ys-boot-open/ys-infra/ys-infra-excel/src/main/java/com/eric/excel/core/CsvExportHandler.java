package com.eric.excel.core;

import com.eric.excel.config.WriteConfig;
import com.eric.excel.utils.EasyExcelUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * CSV导出处理器
 * 将数据导出为CSV格式文件
 * CSV格式特点：纯文本、体积小、兼容性好、适合大数据量导出
 * @ClassName:   CsvExportHandler
 * @author:      liuQ
 * @date:        2026-04-16 10:00:00
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
public class CsvExportHandler<T> {

    /**
     * 导出CSV文件到响应流
     *
     * @param dataList   数据列表
     * @param dtoClass   DTO类
     * @param fileName   文件名（不含扩展名）
     * @param response   HTTP响应
     */
    public void exportToCsv(List<T> dataList, Class<T> dtoClass, String fileName, HttpServletResponse response) {
        exportToCsvWithConfig(dataList, dtoClass, fileName, WriteConfig.builder().build(), response);
    }

    /**
     * 导出CSV文件到响应流（带配置）
     *
     * @param dataList   数据列表
     * @param dtoClass   DTO类
     * @param fileName   文件名（不含扩展名）
     * @param config     写入配置
     * @param response   HTTP响应
     */
    public void exportToCsvWithConfig(List<T> dataList, Class<T> dtoClass, String fileName, 
                                       WriteConfig config, HttpServletResponse response) {
        long startTime = System.currentTimeMillis();
        log.info("开始导出CSV，文件名：{}，数据量：{}", fileName, dataList != null ? dataList.size() : 0);

        try {
            // 设置响应头
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            response.setContentType("text/csv");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + encodedFileName + ".csv");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

            // 执行导出
            try (OutputStream outputStream = response.getOutputStream()) {
                EasyExcelUtils.write(outputStream, dataList, dtoClass, config);
            }

            long costTime = System.currentTimeMillis() - startTime;
            log.info("CSV导出完成，文件名：{}，数据量：{}，耗时：{}ms",
                    fileName, dataList != null ? dataList.size() : 0, costTime);

        } catch (IOException e) {
            log.error("导出CSV失败，文件名：{}", fileName, e);
            throw new RuntimeException("导出CSV失败：" + e.getMessage(), e);
        }
    }

    /**
     * 将Map数据导出为CSV
     * 适用于动态列场景
     *
     * @param dataList   Map数据列表
     * @param headers    表头配置
     * @param fileName   文件名
     * @param response   HTTP响应
     */
    public void exportCsvWithDynamicHeader(List<Map<String, Object>> dataList, 
                                            List<com.eric.excel.config.HeaderConfig> headers,
                                            String fileName, HttpServletResponse response) {
        long startTime = System.currentTimeMillis();
        log.info("开始导出动态CSV，文件名：{}，数据量：{}", fileName, dataList != null ? dataList.size() : 0);

        try {
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            response.setContentType("text/csv");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + encodedFileName + ".csv");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

            WriteConfig config = WriteConfig.builder().build();

            try (OutputStream outputStream = response.getOutputStream()) {
                EasyExcelUtils.writeWithDynamicHeader(outputStream, headers, dataList, config);
            }

            long costTime = System.currentTimeMillis() - startTime;
            log.info("动态CSV导出完成，文件名：{}，数据量：{}，耗时：{}ms",
                    fileName, dataList != null ? dataList.size() : 0, costTime);

        } catch (IOException e) {
            log.error("导出动态CSV失败，文件名：{}", fileName, e);
            throw new RuntimeException("导出动态CSV失败：" + e.getMessage(), e);
        }
    }

    /**
     * 获取CSV格式类型标识
     *
     * @return 格式类型字符串
     */
    public static String getFormatType() {
        return "csv";
    }

    /**
     * 获取CSV MIME类型
     *
     * @return MIME类型
     */
    public static String getMimeType() {
        return "text/csv";
    }

    /**
     * 获取CSV文件扩展名
     *
     * @return 文件扩展名
     */
    public static String getFileExtension() {
        return ".csv";
    }
}
