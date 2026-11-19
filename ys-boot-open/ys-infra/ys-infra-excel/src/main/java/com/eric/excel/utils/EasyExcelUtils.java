package com.eric.excel.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.eric.excel.config.HeaderConfig;
import com.eric.excel.config.ReadConfig;
import com.eric.excel.config.WriteConfig;
import com.eric.excel.exception.ExcelException;
import com.eric.excel.listener.CommonReadListener;
import com.eric.excel.result.ErrorRow;
import com.eric.excel.result.ReadResult;
import com.eric.excel.result.WriteResult;
import com.eric.excel.strategy.ColumnWidthStyleStrategy;
import com.eric.excel.strategy.RowHeightStyleStrategy;
import com.eric.excel.strategy.SeqColumnStyleStrategy;
import com.eric.excel.strategy.UnifiedCellStyleStrategy;
import com.eric.excel.validator.DataValidator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * EasyExcel工具类 - 基于EasyExcel 4.0.3
 * 
 * <p>功能概述：
 * 提供功能强大、高度可定制的Excel读取（导入）与写入（导出）功能。
 * 支持多种使用场景：基础读写、动态表头、多Sheet、大数据量分批处理、Web导出等。
 * 
 * <p>核心特性：
 * <ul>
 *   <li><b>读取功能</b>：支持基础读取、指定Sheet、数据校验、异步读取、分批读取</li>
 *   <li><b>写入功能</b>：支持基础写入、动态表头、多Sheet、大数据量分批写入、模板填充</li>
 *   <li><b>样式控制</b>：基于EasyExcel的HorizontalCellStyleStrategy实现统一样式控制</li>
 *   <li><b>序号列支持</b>：通过SeqColumnStyleStrategy实现自动序号列，样式与其他列一致</li>
 *   <li><b>线程安全</b>：使用线程池支持异步操作，所有方法均为静态方法，工具类线程安全</li>
 * </ul>
 * 
 * <p>样式策略说明：
 * <ul>
 *   <li>使用EasyExcel内置的HorizontalCellStyleStrategy实现样式控制</li>
 *   <li>序号列使用SeqColumnStyleStrategy实现，继承自HorizontalCellStyleStrategy，确保样式一致</li>
 *   <li>列宽使用ColumnWidthStyleStrategy实现</li>
 *   <li>行高使用RowHeightStyleStrategy实现</li>
 * </ul>
 * 
 * <p>使用示例：
 * <pre>
 * // 基础读取
 * ReadResult&lt;UserDTO&gt; result = EasyExcelUtils.read(inputStream, UserDTO.class);
 * 
 * // 基础写入
 * WriteResult result = EasyExcelUtils.write(outputStream, dataList, UserDTO.class);
 * 
 * // Web导出
 * EasyExcelUtils.exportToWeb(response, dataList, UserDTO.class, "用户数据", null);
 * </pre>
 *
 * @ClassName: EasyExcelUtils
 * @author: liuQ
 * @date: 2025-02-26
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
public class EasyExcelUtils {

    /**
     * 线程池（用于异步操作）
     * 
     * <p>线程池配置：
     * <ul>
     *   <li>核心线程数：CPU核心数 * 2</li>
     *   <li>线程名称前缀：EasyExcelUtils-Async-</li>
     *   <li>守护线程：是（避免阻止JVM退出）</li>
     * </ul>
     */
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 2,
            r -> {
                Thread t = new Thread(r, "EasyExcelUtils-Async-" + System.currentTimeMillis());
                t.setDaemon(true);
                return t;
            }
    );

    // ==================== 通用读取方法 ====================

    /**
     * 基础读取Excel方法
     * 
     * <p>功能说明：
     * 读取Excel文件，将数据映射到指定类型的对象列表中。
     * 支持自定义读取配置，包括Sheet选择、表头行号、数据校验等。
     * 
     * <p>执行流程：
     * <ol>
     *   <li>创建ExcelReaderBuilder并配置参数</li>
     *   <li>构建CommonReadListener监听读取过程</li>
     *   <li>读取指定Sheet的数据</li>
     *   <li>返回包含成功数据、错误行、统计信息的ReadResult</li>
     * </ol>
     *
     * @param is     输入流（Excel文件流）
     * @param clazz  数据类型Class（用于EasyExcel映射数据）
     * @param config 读取配置（可为null，使用默认配置）
     * @param <T>    数据类型
     * @return 读取结果（包含成功数据、错误行、统计信息）
     * @throws ExcelException 读取失败时抛出
     */
    public static <T> ReadResult<T> read(InputStream is, Class<T> clazz, ReadConfig<T> config) {
        long startTime = System.currentTimeMillis();

        // 使用默认配置（如果未提供）
        if (config == null) {
            config = ReadConfig.<T>builder().build();
        }

        // 初始化数据容器
        List<T> successData = new ArrayList<>();
        List<ErrorRow<T>> errorRows = new ArrayList<>();
        AtomicInteger totalCount = new AtomicInteger(0);

        // 创建读取监听器
        CommonReadListener<T> listener = new CommonReadListener<>(config, successData, errorRows, totalCount);

        try {
            // 构建ExcelReader
            ExcelReaderBuilder readerBuilder = EasyExcel.read(is, clazz, listener);

            // 配置字符集
            if (StringUtils.hasText(config.getCharset())) {
                readerBuilder.charset(java.nio.charset.Charset.forName(config.getCharset()));
            }

            // 配置读取选项
            readerBuilder.ignoreEmptyRow(true);
            readerBuilder.autoCloseStream(config.getAutoCloseStream());

            // 构建并执行读取
            ExcelReader excelReader = readerBuilder.build();

            // 确定表头行号
            Integer headRowNumber = config.getHeadRowNumber();
            if (headRowNumber == null || headRowNumber <= 0) {
                headRowNumber = 1;
            }

            // 构建ReadSheet（根据Sheet名称或索引）
            ReadSheet readSheet;
            if (StringUtils.hasText(config.getSheetName())) {
                readSheet = EasyExcel.readSheet(config.getSheetName())
                        .headRowNumber(headRowNumber)
                        .build();
            } else {
                readSheet = EasyExcel.readSheet(config.getSheetIndex())
                        .headRowNumber(headRowNumber)
                        .build();
            }

            // 执行读取
            excelReader.read(readSheet);
            excelReader.finish();

        } catch (Exception e) {
            log.error("读取Excel失败", e);
            throw new ExcelException("读取Excel失败: " + e.getMessage(), e);
        } finally {
            // 关闭输入流（如果配置为自动关闭）
            if (config.getAutoCloseStream()) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.warn("关闭输入流失败", e);
                }
            }
        }

        long costTime = System.currentTimeMillis() - startTime;

        // 构建并返回读取结果
        return ReadResult.<T>builder()
                .successData(successData)
                .errorRows(errorRows)
                .totalCount(totalCount.get())
                .successCount(successData.size())
                .errorCount(errorRows.size())
                .costTime(costTime)
                .build();
    }

    /**
     * 简化版读取方法（使用默认配置）
     *
     * @param is    输入流
     * @param clazz 数据类型Class
     * @param <T>   数据类型
     * @return 读取结果
     * @see #read(InputStream, Class, ReadConfig)
     */
    public static <T> ReadResult<T> read(InputStream is, Class<T> clazz) {
        return read(is, clazz, null);
    }

    /**
     * 读取指定Sheet（通过名称）
     *
     * @param is        输入流
     * @param clazz     数据类型Class
     * @param sheetName Sheet名称
     * @param <T>       数据类型
     * @return 读取结果
     * @see #read(InputStream, Class, ReadConfig)
     */
    public static <T> ReadResult<T> readSheet(InputStream is, Class<T> clazz, String sheetName) {
        ReadConfig<T> config = ReadConfig.<T>builder()
                .sheetName(sheetName)
                .build();
        return read(is, clazz, config);
    }

    /**
     * 读取指定Sheet（通过索引）
     *
     * @param is         输入流
     * @param clazz      数据类型Class
     * @param sheetIndex Sheet索引（从0开始）
     * @param <T>        数据类型
     * @return 读取结果
     * @see #read(InputStream, Class, ReadConfig)
     */
    public static <T> ReadResult<T> readSheet(InputStream is, Class<T> clazz, Integer sheetIndex) {
        ReadConfig<T> config = ReadConfig.<T>builder()
                .sheetIndex(sheetIndex)
                .build();
        return read(is, clazz, config);
    }

    /**
     * 带数据校验的读取
     * 
     * <p>在读取过程中对每个数据进行校验，校验失败的数据会被记录到错误行中。
     *
     * @param is        输入流
     * @param clazz     数据类型Class
     * @param validator 数据校验器
     * @param <T>       数据类型
     * @return 读取结果
     * @see #read(InputStream, Class, ReadConfig)
     */
    public static <T> ReadResult<T> readWithValidation(InputStream is, Class<T> clazz, DataValidator<T> validator) {
        ReadConfig<T> config = ReadConfig.<T>builder()
                .validator(validator)
                .build();
        return read(is, clazz, config);
    }

    /**
     * 异步读取Excel
     * 
     * <p>在后台线程中执行读取操作，不会阻塞当前线程。
     * 读取完成后通过回调函数处理结果。
     *
     * @param is       输入流
     * @param clazz    数据类型Class
     * @param config   读取配置
     * @param callback 回调函数（读取完成后执行，可为null）
     * @param <T>      数据类型
     * @return CompletableFuture（可用于等待操作完成或链式调用）
     */
    public static <T> CompletableFuture<ReadResult<T>> readAsync(InputStream is, Class<T> clazz,
                                                                  ReadConfig<T> config,
                                                                  Consumer<ReadResult<T>> callback) {
        return CompletableFuture.supplyAsync(() -> {
            ReadResult<T> result = read(is, clazz, config);
            if (callback != null) {
                callback.accept(result);
            }
            return result;
        }, EXECUTOR_SERVICE);
    }

    /**
     * 分批读取Excel（用于大数据量）
     * 
     * <p>适用于大数据量场景，避免一次性加载所有数据到内存。
     * 每读取一批数据就调用batchDataHandler处理，处理完后释放内存。
     *
     * @param is               输入流
     * @param clazz            数据类型Class
     * @param batchSize        批次大小
     * @param batchDataHandler 批次数据处理器（每读取一批数据调用一次）
     * @param <T>              数据类型
     */
    public static <T> void readInBatches(InputStream is, Class<T> clazz, Integer batchSize,
                                         Consumer<List<T>> batchDataHandler) {
        ReadConfig<T> config = ReadConfig.<T>builder()
                .batchSize(batchSize)
                .batchDataHandler(batchDataHandler)
                .build();
        read(is, clazz, config);
    }

    // ==================== 通用写入方法 ====================

    /**
     * 基础写入Excel方法
     * 
     * <p>功能说明：
     * 将数据列表写入Excel文件。
     * 支持自定义写入配置，包括Sheet名称、文件类型、样式、列过滤等。
     * 
     * <p>执行流程：
     * <ol>
     *   <li>创建ExcelWriterBuilder并配置参数</li>
     *   <li>注册样式处理器（样式、列宽、行高）</li>
     *   <li>构建ExcelWriter并写入数据</li>
     *   <li>返回包含写入统计的WriteResult</li>
     * </ol>
     *
     * @param os     输出流（Excel文件流）
     * @param data   数据列表
     * @param clazz  数据类型Class（用于EasyExcel获取字段注解）
     * @param config 写入配置（可为null，使用默认配置）
     * @param <T>    数据类型
     * @return 写入结果（包含数据量、耗时等信息）
     */
    public static <T> WriteResult write(OutputStream os, List<T> data, Class<T> clazz, WriteConfig config) {
        long startTime = System.currentTimeMillis();

        // 使用默认配置（如果未提供）
        if (config == null) {
            config = WriteConfig.builder().build();
        }

        // 空数据直接返回成功
        if (CollectionUtils.isEmpty(data)) {
            return WriteResult.success(0, 0L);
        }

        ExcelWriter excelWriter = null;
        try {
            // 构建ExcelWriter
            ExcelWriterBuilder writerBuilder = EasyExcel.write(os, clazz);

            // 配置Excel类型
            if (config.getExcelType() != null) {
                writerBuilder.excelType(config.getExcelType());
            }

            // 配置模板（如果指定）
            if (StringUtils.hasText(config.getTemplatePath())) {
                writerBuilder.withTemplate(config.getTemplatePath());
            }

            // 配置列过滤
            if (config.getIncludeColumnFieldNames() != null && !config.getIncludeColumnFieldNames().isEmpty()) {
                writerBuilder.includeColumnFieldNames(config.getIncludeColumnFieldNames());
            }

            // 注册样式处理器
            registerStyleHandlers(writerBuilder, config);

            // 构建并执行写入
            excelWriter = writerBuilder.build();
            WriteSheet writeSheet = EasyExcel.writerSheet(config.getSheetName()).build();
            excelWriter.write(data, writeSheet);
            excelWriter.finish();

            long costTime = System.currentTimeMillis() - startTime;
            return WriteResult.success(data.size(), costTime);

        } catch (Exception e) {
            log.error("写入Excel失败", e);
            return WriteResult.fail("写入Excel失败: " + e.getMessage());
        } finally {
            // 确保资源释放
            if (excelWriter != null) {
                excelWriter.finish();
            }
            // 关闭输出流（如果配置为自动关闭）
            if (config.getAutoCloseStream()) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.warn("关闭输出流失败", e);
                }
            }
        }
    }

    /**
     * 简化版写入方法（使用默认配置）
     *
     * @param os    输出流
     * @param data  数据列表
     * @param clazz 数据类型Class
     * @param <T>   数据类型
     * @return 写入结果
     * @see #write(OutputStream, List, Class, WriteConfig)
     */
    public static <T> WriteResult write(OutputStream os, List<T> data, Class<T> clazz) {
        return write(os, data, clazz, null);
    }

    /**
     * 动态表头写入
     * 
     * <p>适用于表头需要动态生成的场景，如用户自定义导出列。
     * 通过HeaderConfig配置每个列的标题、字段名、排序等。
     *
     * @param os      输出流
     * @param headers 表头配置列表
     * @param data    数据列表（Map形式，key为字段名）
     * @param config  写入配置
     * @see com.eric.excel.config.HeaderConfig
     */
    public static void writeWithDynamicHeader(OutputStream os, List<HeaderConfig> headers,
                                              List<Map<String, Object>> data, WriteConfig config) {
        long startTime = System.currentTimeMillis();

        // 使用默认配置（如果未提供）
        if (config == null) {
            config = WriteConfig.builder().build();
        }

        // 校验表头配置
        if (CollectionUtils.isEmpty(headers)) {
            WriteResult.fail("表头配置不能为空");
            return;
        }

        ExcelWriter excelWriter = null;
        try {
            // 构建ExcelWriter（不指定Class，使用动态表头）
            ExcelWriterBuilder writerBuilder = EasyExcel.write(os);

            // 配置Excel类型
            if (config.getExcelType() != null) {
                writerBuilder.excelType(config.getExcelType());
            }

            // 注册样式处理器
            registerStyleHandlers(writerBuilder, config);

            // 设置动态表头
            writerBuilder.head(buildDynamicHead(headers));

            // 构建并执行写入
            excelWriter = writerBuilder.build();
            WriteSheet writeSheet = EasyExcel.writerSheet(config.getSheetName()).build();

            // 转换数据格式并写入
            List<List<Object>> dataList = convertToListList(data, headers);
            excelWriter.write(dataList, writeSheet);
            excelWriter.finish();

            long costTime = System.currentTimeMillis() - startTime;
            WriteResult.success(dataList.size(), costTime);

        } catch (Exception e) {
            log.error("写入Excel失败", e);
            WriteResult.fail("写入Excel失败: " + e.getMessage());
        } finally {
            // 确保资源释放
            if (excelWriter != null) {
                excelWriter.finish();
            }
            // 关闭输出流（如果配置为自动关闭）
            if (config.getAutoCloseStream()) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.warn("关闭输出流失败", e);
                }
            }
        }
    }

    /**
     * 多Sheet写入
     * 
     * <p>适用于需要将数据写入多个Sheet的场景。
     * 每个Sheet可以有不同的数据列表，但数据类型必须相同。
     *
     * @param os            输出流
     * @param sheetDataMap  Sheet数据Map（key为Sheet名称，value为数据列表）
     * @param clazz         数据类型Class
     * @param config        写入配置
     * @param <T>           数据类型
     * @return 写入结果
     */
    public static <T> WriteResult writeMultiSheet(OutputStream os, Map<String, List<T>> sheetDataMap,
                                                   Class<T> clazz, WriteConfig config) {
        long startTime = System.currentTimeMillis();

        // 空数据直接返回成功
        if (CollectionUtils.isEmpty(sheetDataMap)) {
            return WriteResult.success(0, 0L);
        }

        ExcelWriter excelWriter = null;
        try {
            // 构建ExcelWriter
            ExcelWriterBuilder writerBuilder = EasyExcel.write(os, clazz);

            // 配置Excel类型
            if (config != null && config.getExcelType() != null) {
                writerBuilder.excelType(config.getExcelType());
            }

            // 注册样式处理器
            if (config != null) {
                registerStyleHandlers(writerBuilder, config);
            }

            // 构建ExcelWriter
            excelWriter = writerBuilder.build();

            // 遍历写入每个Sheet
            int totalRowCount = 0;
            int sheetIndex = 0;
            for (Map.Entry<String, List<T>> entry : sheetDataMap.entrySet()) {
                String sheetName = entry.getKey();
                List<T> data = entry.getValue();

                if (!CollectionUtils.isEmpty(data)) {
                    WriteSheet writeSheet = EasyExcel.writerSheet(sheetIndex++, sheetName).build();
                    excelWriter.write(data, writeSheet);
                    totalRowCount += data.size();
                }
            }

            excelWriter.finish();

            long costTime = System.currentTimeMillis() - startTime;
            return WriteResult.success(totalRowCount, costTime);

        } catch (Exception e) {
            log.error("写入Excel失败", e);
            return WriteResult.fail("写入Excel失败: " + e.getMessage());
        } finally {
            // 确保资源释放
            if (excelWriter != null) {
                excelWriter.finish();
            }
            // 关闭输出流（如果配置为自动关闭）
            if (config != null && config.getAutoCloseStream()) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.warn("关闭输出流失败", e);
                }
            }
        }
    }

    /**
     * 大数据量写入（分批写入）
     * 
     * <p>适用于超大数据量场景，避免一次性加载所有数据到内存。
     * 通过Supplier分批提供数据，每批数据写入后立即释放内存。
     *
     * @param os            输出流
     * @param dataSupplier  数据提供者（分批提供数据，返回空列表表示数据已读完）
     * @param clazz         数据类型Class
     * @param config        写入配置
     * @param <T>           数据类型
     * @return 写入结果
     */
    public static <T> WriteResult writeLargeData(OutputStream os, Supplier<List<T>> dataSupplier,
                                                  Class<T> clazz, WriteConfig config) {
        long startTime = System.currentTimeMillis();

        // 使用默认配置（如果未提供）
        if (config == null) {
            config = WriteConfig.builder().build();
        }

        ExcelWriter excelWriter = null;
        try {
            // 构建ExcelWriter
            ExcelWriterBuilder writerBuilder = EasyExcel.write(os, clazz);

            // 配置Excel类型
            if (config.getExcelType() != null) {
                writerBuilder.excelType(config.getExcelType());
            }

            // 注册样式处理器
            registerStyleHandlers(writerBuilder, config);

            // 构建ExcelWriter
            excelWriter = writerBuilder.build();
            WriteSheet writeSheet = EasyExcel.writerSheet(config.getSheetName()).build();

            // 分批读取并写入数据
            int totalRowCount = 0;
            List<T> batch;
            while (!(batch = dataSupplier.get()).isEmpty()) {
                excelWriter.write(batch, writeSheet);
                totalRowCount += batch.size();
            }

            excelWriter.finish();

            long costTime = System.currentTimeMillis() - startTime;
            return WriteResult.success(totalRowCount, costTime);

        } catch (Exception e) {
            log.error("写入Excel失败", e);
            return WriteResult.fail("写入Excel失败: " + e.getMessage());
        } finally {
            // 确保资源释放
            if (excelWriter != null) {
                excelWriter.finish();
            }
            // 关闭输出流（如果配置为自动关闭）
            if (config.getAutoCloseStream()) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.warn("关闭输出流失败", e);
                }
            }
        }
    }

    /**
     * 使用模板填充写入
     * 
     * <p>适用于需要基于模板生成Excel的场景。
     * 模板中可以使用占位符，通过dataMap填充数据。
     *
     * @param os           输出流
     * @param templatePath 模板文件路径（classpath路径）
     * @param data         填充数据（Map形式，key为占位符名称）
     * @param config       写入配置
     * @return 写入结果
     */
    public static WriteResult writeWithTemplate(OutputStream os, String templatePath,
                                                 Map<String, Object> data, WriteConfig config) {
        long startTime = System.currentTimeMillis();

        // 校验模板路径
        if (!StringUtils.hasText(templatePath)) {
            return WriteResult.fail("模板路径不能为空");
        }

        // 使用默认配置（如果未提供）
        if (config == null) {
            config = WriteConfig.builder().build();
        }

        ExcelWriter excelWriter = null;
        try {
            // 构建ExcelWriter（使用模板）
            ExcelWriterBuilder writerBuilder = EasyExcel.write(os);
            writerBuilder.withTemplate(templatePath);

            // 配置Excel类型
            if (config.getExcelType() != null) {
                writerBuilder.excelType(config.getExcelType());
            }

            // 构建并执行填充
            excelWriter = writerBuilder.build();
            WriteSheet writeSheet = EasyExcel.writerSheet(config.getSheetName()).build();
            excelWriter.fill(data, writeSheet);
            excelWriter.finish();

            long costTime = System.currentTimeMillis() - startTime;
            return WriteResult.success(1, costTime);

        } catch (Exception e) {
            log.error("模板填充写入失败", e);
            return WriteResult.fail("模板填充写入失败: " + e.getMessage());
        } finally {
            // 确保资源释放
            if (excelWriter != null) {
                excelWriter.finish();
            }
            // 关闭输出流（如果配置为自动关闭）
            if (config.getAutoCloseStream()) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.warn("关闭输出流失败", e);
                }
            }
        }
    }

    /**
     * Web端导出Excel（响应到浏览器下载）
     * 
     * <p>适用于Web应用导出Excel并直接下载到浏览器的场景。
     * 自动设置响应头，包括Content-Type、Content-Disposition等。
     *
     * @param response HttpServletResponse
     * @param data     数据列表
     * @param clazz    数据类型Class
     * @param fileName 文件名（不含扩展名）
     * @param config   写入配置
     * @param <T>      数据类型
     * @throws ExcelException 导出失败时抛出
     */
    public static <T> void exportToWeb(HttpServletResponse response, List<T> data, Class<T> clazz,
                                        String fileName, WriteConfig config) {
        try {
            // 使用默认配置（如果未提供）
            if (config == null) {
                config = WriteConfig.builder().build();
            }

            // 编码文件名（处理中文文件名）
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            // 根据Excel类型设置Content-Type
            String contentType = config.getExcelType() == ExcelTypeEnum.XLS
                    ? "application/vnd.ms-excel"
                    : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

            // 设置响应头
            response.setContentType(contentType);
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + encodedFileName +
                    (config.getExcelType() == ExcelTypeEnum.XLS ? ".xls" : ".xlsx"));
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

            // 执行写入
            write(response.getOutputStream(), data, clazz, config);

        } catch (IOException e) {
            log.error("Web导出Excel失败", e);
            throw new ExcelException("Web导出Excel失败: " + e.getMessage(), e);
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 获取Excel所有Sheet名称
     *
     * @param is 输入流
     * @return Sheet名称列表
     */
    public static List<String> getSheetNames(InputStream is) {
        List<String> sheetNames = new ArrayList<>();
        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(is).build();
            List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
            for (ReadSheet sheet : sheets) {
                sheetNames.add(sheet.getSheetName());
            }
        } finally {
            // 确保资源释放
            if (excelReader != null) {
                excelReader.finish();
            }
            try {
                is.close();
            } catch (IOException e) {
                log.warn("关闭输入流失败", e);
            }
        }
        return sheetNames;
    }

    /**
     * 获取Sheet数据行数（不含表头）
     *
     * @param is        输入流
     * @param sheetName Sheet名称
     * @return 数据行数
     */
    public static int getSheetRowCount(InputStream is, String sheetName) {
        AtomicInteger rowCount = new AtomicInteger(0);
        EasyExcel.read(is, new com.alibaba.excel.event.AnalysisEventListener<Map<Integer, String>>() {
            @Override
            public void invoke(Map<Integer, String> data, com.alibaba.excel.context.AnalysisContext context) {
                rowCount.incrementAndGet();
            }

            @Override
            public void doAfterAllAnalysed(com.alibaba.excel.context.AnalysisContext context) {
            }
        }).sheet(sheetName).doRead();
        return rowCount.get();
    }

    /**
     * 验证Excel文件有效性
     *
     * @param is 输入流
     * @return 是否有效
     */
    public static boolean validateExcelFile(InputStream is) {
        try (is) {
            try {
                ExcelReader excelReader = EasyExcel.read(is).build();
                List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
                boolean valid = !CollectionUtils.isEmpty(sheets);
                excelReader.finish();
                return valid;
            } catch (Exception e) {
                return false;
            }
        } catch (IOException e) {
            log.warn("关闭输入流失败", e);
        }
        return false;
    }

    /**
     * 获取Excel类型
     *
     * @param fileName 文件名
     * @return Excel类型
     */
    public static ExcelTypeEnum getExcelType(String fileName) {
        if (fileName == null) {
            return ExcelTypeEnum.XLSX;
        }
        String lowerCase = fileName.toLowerCase();
        if (lowerCase.endsWith(".xls")) {
            return ExcelTypeEnum.XLS;
        } else if (lowerCase.endsWith(".xlsx")) {
            return ExcelTypeEnum.XLSX;
        } else if (lowerCase.endsWith(".csv")) {
            return ExcelTypeEnum.CSV;
        }
        return ExcelTypeEnum.XLSX;
    }

    // ==================== 私有方法 ====================

    /**
     * 注册样式处理器
     * 
     * <p>根据配置注册不同的样式处理器：
     * <ul>
     *   <li>需要序号列时：使用SeqColumnStyleStrategy（继承自HorizontalCellStyleStrategy，同时处理序号和样式）</li>
     *   <li>不需要序号列时：使用UnifiedCellStyleStrategy（仅处理样式）</li>
     *   <li>自动调整列宽时：注册ColumnWidthStyleStrategy</li>
     *   <li>始终注册RowHeightStyleStrategy处理行高</li>
     * </ul>
     *
     * @param writerBuilder 写入构建器
     * @param config        写入配置
     */
    private static void registerStyleHandlers(ExcelWriterBuilder writerBuilder, WriteConfig config) {
        // CSV格式不支持样式设置，跳过所有POI样式处理器
        // CsvRow/CsvSheet不支持单元格移位、列宽、行高等POI操作
        if (config.getExcelType() == ExcelTypeEnum.CSV) {
            return;
        }

        // 根据是否需要序号列选择不同的样式策略
        if (Boolean.TRUE.equals(config.getIncludeSeq())) {
            // 需要序号列：使用SeqColumnStyleStrategy，它同时处理序号和样式
            writerBuilder.registerWriteHandler(new SeqColumnStyleStrategy(config));
        } else {
            // 不需要序号列：使用UnifiedCellStyleStrategy，仅处理样式
            writerBuilder.registerWriteHandler(new UnifiedCellStyleStrategy(config));
        }

        // 注册列宽处理器（如果启用自动调整列宽）
        if (config.getAutoAdjustColumnWidth()) {
            writerBuilder.registerWriteHandler(new ColumnWidthStyleStrategy(config));
        }

        // 注册行高处理器
        writerBuilder.registerWriteHandler(new RowHeightStyleStrategy(config));
    }

    /**
     * 构建动态表头
     * 
     * <p>根据HeaderConfig列表构建EasyExcel需要的表头格式。
     * 支持多级表头（通过parentTitle设置父级标题）。
     *
     * @param headers 表头配置列表
     * @return 表头列表（EasyExcel格式）
     */
    private static List<List<String>> buildDynamicHead(List<HeaderConfig> headers) {
        // 按层级分组
        Map<Integer, List<HeaderConfig>> levelMap = new HashMap<>();
        for (HeaderConfig header : headers) {
            levelMap.computeIfAbsent(header.getLevel(), k -> new ArrayList<>()).add(header);
        }

        // 获取最大层级
        int maxLevel = levelMap.keySet().stream().max(Integer::compareTo).orElse(0);

        // 构建表头
        List<List<String>> head = new ArrayList<>();
        for (int i = 0; i <= maxLevel; i++) {
            List<HeaderConfig> levelHeaders = levelMap.getOrDefault(i, new ArrayList<>());
            levelHeaders.sort(Comparator.comparingInt(HeaderConfig::getSort));

            for (HeaderConfig header : levelHeaders) {
                List<String> headNames = new ArrayList<>();
                // 如果有父级标题，先添加父级
                if (StringUtils.hasText(header.getParentTitle())) {
                    headNames.add(header.getParentTitle());
                }
                headNames.add(header.getTitle());
                head.add(headNames);
            }
        }

        return head;
    }

    /**
     * 将Map数据转换为List<List<Object>>
     * 
     * <p>用于动态表头写入时的数据格式转换。
     * 根据headers的顺序从data中提取对应的值。
     *
     * @param data    Map数据列表
     * @param headers 表头配置（决定列的顺序）
     * @return List<List<Object>>（EasyExcel需要的格式）
     */
    private static List<List<Object>> convertToListList(List<Map<String, Object>> data, List<HeaderConfig> headers) {
        List<List<Object>> result = new ArrayList<>();

        // 按排序号排序
        headers.sort(Comparator.comparingInt(HeaderConfig::getSort));

        // 转换每一行数据
        for (Map<String, Object> rowData : data) {
            List<Object> row = new ArrayList<>();
            for (HeaderConfig header : headers) {
                Object value = rowData.get(header.getFieldName());

                // 应用值转换器（如果有）
                if (header.getConverter() != null && value != null) {
                    value = header.getConverter().apply(value);
                }

                row.add(value);
            }
            result.add(row);
        }

        return result;
    }

    // ==================== 关闭资源 ====================

    /**
     * 关闭线程池
     * 
     * <p>在应用关闭时调用，释放线程池资源。
     * 建议在Spring的销毁方法中调用。
     */
    public static void shutdown() {
        EXECUTOR_SERVICE.shutdown();
    }
}
