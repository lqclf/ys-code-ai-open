package com.eric.excel.module.service;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.eric.excel.config.HeaderConfig;
import com.eric.excel.config.StyleConfig;
import com.eric.excel.config.WriteConfig;
import com.eric.excel.core.ExportHandler;
import com.eric.excel.utils.EasyExcelUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Excel导出服务
 * 封装通用的导出逻辑
 * @ClassName:  ExcelExportService
 * @author:     liuQ
 * @date:       2026-02-28 23:57:17
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
@Service
public class ExcelExportService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 从 Map 查询参数执行导出（统一控制器专用）
     *
     * @param queryMap       Map 查询参数
     * @param selectedFields 选中的字段列表
     * @param fileType       文件类型
     * @param handler        导出处理器
     * @param response       HTTP响应
     */
    public <T, Q> void exportFromMaps(Map<String, String> queryMap, List<String> selectedFields,
                                       String fileType, ExportHandler<T, Q> handler,
                                       HttpServletResponse response) {
        Q queryParams = objectMapper.convertValue(queryMap, handler.getQueryClass());
        export(queryParams, selectedFields, fileType, handler, response);
    }

    /**
     * 执行导出（带列过滤）
     * 流程：查询数据 → 字段过滤 → 后处理 → 写入响应流
     *
     * @param queryParams    查询参数
     * @param selectedFields 选中的字段列表（null表示导出全部字段，包含"selectedFields"表示需要序号列）
     * @param handler        导出处理器
     * @param response       HTTP响应
     */
    public <T, Q> void export(Q queryParams, List<String> selectedFields,
                              ExportHandler<T, Q> handler, HttpServletResponse response) {
        export(queryParams, selectedFields, "xlsx", handler, response);
    }

    /**
     * 执行导出（带列过滤和文件类型）
     * 流程：查询数据 → 字段过滤 → 后处理 → 写入响应流
     * 序号列通过 selectedFields 中是否包含 "selectedFields" 来判断
     *
     * @param queryParams    查询参数
     * @param selectedFields 选中的字段列表（null表示导出全部字段，包含"selectedFields"表示需要序号列）
     * @param fileType       文件类型（xlsx、xls、csv）
     * @param handler        导出处理器
     * @param response       HTTP响应
     */
    public <T, Q> void export(Q queryParams, List<String> selectedFields, String fileType,
                              ExportHandler<T, Q> handler, HttpServletResponse response) {
        export(queryParams, selectedFields, fileType, null, null, handler, response);
    }

    /**
     * 执行导出（带列过滤、文件类型和样式配置）
     * 流程：查询数据 → 字段过滤 → 后处理 → 写入响应流
     * 序号列通过 selectedFields 中是否包含 "selectedFields" 来判断
     *
     * @param queryParams    查询参数
     * @param selectedFields 选中的字段列表（null表示导出全部字段，包含"selectedFields"表示需要序号列）
     * @param fileType       文件类型（xlsx、xls、csv、json）
     * @param headerStyle    表头样式配置
     * @param contentStyle   内容样式配置
     * @param handler        导出处理器
     * @param response       HTTP响应
     */
    public <T, Q> void export(Q queryParams, List<String> selectedFields, String fileType,
                              StyleConfig headerStyle, StyleConfig contentStyle,
                              ExportHandler<T, Q> handler, HttpServletResponse response) {
        long startTime = System.currentTimeMillis();

        boolean includeSeq = selectedFields != null && selectedFields.contains("selectedFields");

        List<String> includeFields = selectedFields;
        if (includeSeq) {
            includeFields = selectedFields.stream()
                    .filter(f -> !"selectedFields".equals(f))
                    .collect(Collectors.toList());
            if (includeFields.isEmpty()) {
                includeFields = null;
            }
        }

        try {
            List<T> dataList = handler.queryData(queryParams);

            List<String> handlerIncludeFields = handler.getIncludeColumns(includeFields);
            if (!CollectionUtils.isEmpty(handlerIncludeFields)) {
                log.info("将导出字段：{}", handlerIncludeFields);
            }

            handler.afterFilter(dataList, handlerIncludeFields);

            String fileName = handler.getFileName();
            writeToResponse(dataList, handlerIncludeFields, fileType, includeSeq, 
                    headerStyle, contentStyle, handler, fileName, response);

            long costTime = System.currentTimeMillis() - startTime;
            log.info("导出完成，数据量：{}，文件名：{}，文件类型：{}，耗时：{}ms",
                    dataList.size(), fileName, fileType, costTime);

        } catch (Exception e) {
            log.error("导出失败", e);
            throw new RuntimeException("导出失败：" + e.getMessage(), e);
        }
    }

    /**
     * 执行导出（带列过滤、文件类型、样式配置和自定义数据转换）
     * 适用于JSON格式等需要自定义序列化场景
     *
     * @param queryParams    查询参数
     * @param selectedFields 选中的字段列表（null表示导出全部字段，包含"selectedFields"表示需要序号列）
     * @param fileType       文件类型（xlsx、xls、csv、json）
     * @param headerStyle    表头样式配置
     * @param contentStyle   内容样式配置
     * @param handler        导出处理器
     * @param dataConverter  数据转换器（用于自定义格式转换，可为null）
     * @param response       HTTP响应
     */
    public <T, Q> void export(Q queryParams, List<String> selectedFields, String fileType,
                              StyleConfig headerStyle, StyleConfig contentStyle,
                              ExportHandler<T, Q> handler,
                              DataConverter<T> dataConverter, HttpServletResponse response) {
        long startTime = System.currentTimeMillis();

        boolean includeSeq = selectedFields != null && selectedFields.contains("selectedFields");

        List<String> includeFields = selectedFields;
        if (includeSeq) {
            includeFields = selectedFields.stream()
                    .filter(f -> !"selectedFields".equals(f))
                    .collect(Collectors.toList());
            if (includeFields.isEmpty()) {
                includeFields = null;
            }
        }

        try {
            List<T> dataList = handler.queryData(queryParams);
            log.debug("查询到数据量：{}", dataList.size());

            List<String> handlerIncludeFields = handler.getIncludeColumns(includeFields);
            if (!CollectionUtils.isEmpty(handlerIncludeFields)) {
                log.debug("将导出字段：{}", handlerIncludeFields);
            }

            handler.afterFilter(dataList, handlerIncludeFields);

            String fileName = handler.getFileName();

            // JSON格式使用自定义转换器
            if ("json".equalsIgnoreCase(fileType) && dataConverter != null) {
                writeToResponseAsJson(dataList, fileName, dataConverter, response);
            } else {
                writeToResponse(dataList, handlerIncludeFields, fileType, includeSeq,
                        headerStyle, contentStyle, handler, fileName, response);
            }

            long costTime = System.currentTimeMillis() - startTime;
            log.info("导出完成，数据量：{}，文件名：{}，文件类型：{}，耗时：{}ms",
                    dataList.size(), fileName, fileType, costTime);

        } catch (Exception e) {
            log.error("导出失败", e);
            throw new RuntimeException("导出失败：" + e.getMessage(), e);
        }
    }

    /**
     * 执行导出（导出全部字段）
     *
     * @param queryParams 查询参数
     * @param handler     导出处理器
     * @param response    HTTP响应
     */
    public <T, Q> void export(Q queryParams, ExportHandler<T, Q> handler, HttpServletResponse response) {
        export(queryParams, null, handler, response);
    }

    /**
     * 写入响应流
     * 支持列过滤、文件类型和序号列
     * 当指定了 includeFields 时，使用动态表头模式，按前端指定的字段顺序导出
     */
    private <T, Q> void writeToResponse(List<T> dataList, List<String> includeFields,
                                        String fileType, boolean includeSeq,
                                        StyleConfig headerStyle, StyleConfig contentStyle,
                                        ExportHandler<T, Q> handler, String fileName,
                                        HttpServletResponse response) throws IOException {
        // JSON格式直接序列化输出，不经过EasyExcel
        if ("json".equalsIgnoreCase(fileType)) {
            writeJsonToResponse(dataList, includeFields, includeSeq, handler, fileName, response);
            return;
        }

        ExcelTypeEnum excelType = parseExcelType(fileType);
        String extension = getFileExtension(fileType);

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setContentType(getContentType(fileType));
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + encodedFileName + extension);
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

        StyleConfig finalHeaderStyle = headerStyle != null ? headerStyle : handler.getExportStyleConfig();
        StyleConfig finalContentStyle = contentStyle != null ? contentStyle : handler.getExportStyleConfig();
        List<HeaderConfig> customHeaders = handler.getExportHeaderConfig();
        Map<String, Integer> columnWidths = handler.getColumnWidths();

        // CSV格式不支持POI样式策略（SeqColumnStyleStrategy等），需要将序号列直接添加到数据中
        boolean csvWithSeq = excelType == ExcelTypeEnum.CSV && includeSeq;
        if (csvWithSeq) {
            includeSeq = false;
        }

        WriteConfig.WriteConfigBuilder configBuilder = WriteConfig.builder()
                .sheetName(handler.getSheetName())
                .excelType(excelType);

        if (finalHeaderStyle != null) {
            configBuilder.headerStyle(finalHeaderStyle);
        }
        if (finalContentStyle != null) {
            configBuilder.contentStyle(finalContentStyle);
        }

        if (columnWidths != null && !columnWidths.isEmpty()) {
            configBuilder.columnWidths(columnWidths);
            configBuilder.autoAdjustColumnWidth(false);
        }

        if (includeSeq) {
            configBuilder.includeSeq(true);
        }

        WriteConfig config = configBuilder.build();

        OutputStream outputStream = response.getOutputStream();

        // 当指定了 includeFields 且未配置自定义表头时，使用动态表头模式按前端顺序导出
        if (!CollectionUtils.isEmpty(includeFields) && (customHeaders == null || customHeaders.isEmpty())) {
            List<HeaderConfig> dynamicHeaders = buildDynamicHeaders(includeFields, handler.getDtoClass(), columnWidths);
            List<Map<String, Object>> mapData = convertToOrderedMapList(dataList, includeFields);
            // CSV格式：将序号列直接添加到表头和数据中
            if (csvWithSeq) {
                addSeqColumnToData(dynamicHeaders, mapData);
            }
            EasyExcelUtils.writeWithDynamicHeader(outputStream, dynamicHeaders, mapData, config);
        } else if (customHeaders != null && !customHeaders.isEmpty()) {
            List<Map<String, Object>> mapData = convertToMapList(dataList);
            // CSV格式：将序号列直接添加到表头和数据中
            if (csvWithSeq) {
                addSeqColumnToData(customHeaders, mapData);
            }
            EasyExcelUtils.writeWithDynamicHeader(outputStream, customHeaders, mapData, config);
        } else {
            EasyExcelUtils.write(outputStream, dataList, handler.getDtoClass(), config);
        }
    }

    /**
     * 为CSV格式添加序号列到表头和数据中
     * CSV格式不支持POI样式策略（SeqColumnStyleStrategy），因此需要将序号列直接添加到数据中
     *
     * @param headers 表头配置列表（会被修改）
     * @param mapData 数据列表（会被修改）
     */
    private void addSeqColumnToData(List<HeaderConfig> headers, List<Map<String, Object>> mapData) {
        // 在表头最前面添加序号列
        HeaderConfig seqHeader = HeaderConfig.builder()
                .fieldName("_seq_")
                .title("序号")
                .sort(-1)
                .build();
        headers.add(0, seqHeader);

        // 在每行数据最前面添加序号
        for (int i = 0; i < mapData.size(); i++) {
            Map<String, Object> row = new java.util.LinkedHashMap<>();
            row.put("_seq_", i + 1);
            row.putAll(mapData.get(i));
            mapData.set(i, row);
        }
    }

    /**
     * 以JSON格式写入响应流（不经过EasyExcel）
     * JSON格式无需Excel样式处理，直接使用Jackson序列化输出
     * 使用Jackson convertValue替代MethodHandle，支持继承字段和日期格式化
     *
     * @param dataList    数据列表
     * @param includeFields 选中的字段列表
     * @param includeSeq  是否包含序号列
     * @param handler     导出处理器
     * @param fileName    文件名
     * @param response    HTTP响应
     */
    private <T, Q> void writeJsonToResponse(List<T> dataList, List<String> includeFields, boolean includeSeq,
                                              ExportHandler<T, Q> handler, String fileName,
                                              HttpServletResponse response) throws IOException {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + encodedFileName + ".json");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

        // 使用Jackson convertValue将DTO转换为Map，比MethodHandle更可靠
        // 优势：支持继承字段、@JsonFormat日期格式化、null值处理
        ObjectMapper jsonMapper = com.eric.excel.core.JsonExportHandler.getObjectMapper();
        List<Map<String, Object>> mapData = new ArrayList<>();
        for (T dto : dataList) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = jsonMapper.convertValue(dto, java.util.LinkedHashMap.class);
            if (!CollectionUtils.isEmpty(includeFields)) {
                // 按指定字段顺序过滤，只保留前端选中的字段
                Map<String, Object> filteredMap = new java.util.LinkedHashMap<>();
                for (String field : includeFields) {
                    if (map.containsKey(field)) {
                        filteredMap.put(field, map.get(field));
                    }
                }
                map = filteredMap;
            }
            mapData.add(map);
        }

        // 添加序号列
        if (includeSeq) {
            for (int i = 0; i < mapData.size(); i++) {
                Map<String, Object> row = new java.util.LinkedHashMap<>();
                row.put("序号", i + 1);
                row.putAll(mapData.get(i));
                mapData.set(i, row);
            }
        }

        // 构建JSON结构并序列化输出
        Map<String, Object> wrapper = new java.util.LinkedHashMap<>();
        wrapper.put("fileName", fileName);
        wrapper.put("totalCount", mapData.size());
        wrapper.put("data", mapData);

        byte[] jsonBytes = jsonMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsBytes(wrapper);

        // 使用OutputStream写入，避免与filter/interceptor的getOutputStream冲突
        response.getOutputStream().write(jsonBytes);
        response.getOutputStream().flush();
    }

    /**
     * 根据前端指定的字段列表构建动态表头
     * 从 DTO 类的 @ExcelProperty 注解中提取标题
     * @param includeFields 前端指定的字段顺序列表
     * @param dtoClass DTO 类
     * @param columnWidths 列宽配置
     * @return 动态表头配置列表
     */
    private <T> List<HeaderConfig> buildDynamicHeaders(List<String> includeFields, Class<T> dtoClass,
                                                        Map<String, Integer> columnWidths) {
        List<HeaderConfig> headers = new ArrayList<>();
        // 缓存字段的 @ExcelProperty 标题，避免重复反射
        Map<String, String> fieldTitleMap = new java.util.HashMap<>();
        List<Field> allFields = getAllFields(dtoClass);
        for (Field field : allFields) {
            com.alibaba.excel.annotation.ExcelProperty excelProperty =
                    field.getAnnotation(com.alibaba.excel.annotation.ExcelProperty.class);
            if (excelProperty != null && excelProperty.value().length > 0) {
                fieldTitleMap.put(field.getName(), excelProperty.value()[0]);
            }
        }

        int sort = 0;
        for (String fieldName : includeFields) {
            String title = fieldTitleMap.getOrDefault(fieldName, fieldName);
            HeaderConfig.HeaderConfigBuilder headerBuilder = HeaderConfig.builder()
                    .fieldName(fieldName)
                    .title(title)
                    .sort(sort++);
            if (columnWidths != null && columnWidths.containsKey(fieldName)) {
                headerBuilder.width(columnWidths.get(fieldName));
            }
            headers.add(headerBuilder.build());
        }
        return headers;
    }

    /**
     * 将对象列表转换为按指定字段顺序的 Map 列表
     * 确保导出数据的列顺序与前端指定的 includeFields 完全一致
     * @param dataList 数据列表
     * @param includeFields 前端指定的字段顺序列表
     * @return 按顺序排列的 Map 列表
     */
    /**
     * 将DTO列表转换为有序的Map列表（用于动态表头导出）
     * 使用MethodHandle替代Field反射获取，大幅提升大数据量导出性能
     */
    /** MethodHandle缓存，用于DTO字段getter方法的快速反射调用 */
    private static final ConcurrentHashMap<String, MethodHandle> GETTER_MH_CACHE = new ConcurrentHashMap<>();

    private <T> List<Map<String, Object>> convertToOrderedMapList(List<T> dataList, List<String> includeFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(dataList) || includeFields == null || includeFields.isEmpty()) {
            return result;
        }

        Class<?> clazz = dataList.get(0).getClass();

        for (String fieldName : includeFields) {
            String cacheKey = clazz.getName() + "." + fieldName;
            GETTER_MH_CACHE.computeIfAbsent(cacheKey, k -> {
                try {
                    // 支持继承字段：遍历父类查找
                    Field f = findField(clazz, fieldName);
                    if (f == null) {
                        log.warn("字段不存在：{}，将导出空值", fieldName);
                        return null;
                    }
                    f.setAccessible(true);
                    return MethodHandles.lookup().unreflectGetter(f);
                } catch (Exception e) {
                    log.warn("字段获取失败：{}，将导出空值", fieldName);
                    return null;
                }
            });
        }

        for (T dto : dataList) {
            Map<String, Object> map = new java.util.LinkedHashMap<>();
            for (String fieldName : includeFields) {
                String cacheKey = clazz.getName() + "." + fieldName;
                MethodHandle mh = GETTER_MH_CACHE.get(cacheKey);
                if (mh != null) {
                    try {
                        map.put(fieldName, mh.invoke(dto));
                    } catch (Throwable e) {
                        map.put(fieldName, null);
                    }
                } else {
                    map.put(fieldName, null);
                }
            }
            result.add(map);
        }
        return result;
    }

    /**
     * 解析文件类型
     *
     * @param fileType 文件类型字符串
     * @return ExcelTypeEnum
     */
    private ExcelTypeEnum parseExcelType(String fileType) {
        if (!org.springframework.util.StringUtils.hasText(fileType)) {
            return ExcelTypeEnum.XLSX;
        }
        switch (fileType.toLowerCase()) {
            case "xls":
                return ExcelTypeEnum.XLS;
            case "csv":
                return ExcelTypeEnum.CSV;
            case "json":
                return ExcelTypeEnum.CSV; // JSON不使用Excel类型，此处返回默认值
            case "xlsx":
            default:
                return ExcelTypeEnum.XLSX;
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param fileType 文件类型
     * @return 文件扩展名
     */
    private String getFileExtension(String fileType) {
        if (!org.springframework.util.StringUtils.hasText(fileType)) {
            return ".xlsx";
        }
        switch (fileType.toLowerCase()) {
            case "xls":
                return ".xls";
            case "csv":
                return ".csv";
            case "json":
                return ".json";
            case "xlsx":
            default:
                return ".xlsx";
        }
    }

    /**
     * 获取Content-Type
     *
     * @param fileType 文件类型
     * @return Content-Type
     */
    private String getContentType(String fileType) {
        if (!org.springframework.util.StringUtils.hasText(fileType)) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        switch (fileType.toLowerCase()) {
            case "xls":
                return "application/vnd.ms-excel";
            case "csv":
                return "text/csv";
            case "json":
                return "application/json";
            case "xlsx":
            default:
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
    }

    /**
     * 将对象列表转换为Map列表（用于动态表头导出）
     * 支持继承字段（遍历父类）
     */
    private <T> List<Map<String, Object>> convertToMapList(List<T> dataList) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (T dto : dataList) {
            Map<String, Object> map = new java.util.LinkedHashMap<>();
            List<Field> allFields = getAllFields(dto.getClass());
            for (Field field : allFields) {
                try {
                    field.setAccessible(true);
                    map.put(field.getName(), field.get(dto));
                } catch (IllegalAccessException e) {
                    log.warn("获取字段值失败：{}", field.getName());
                }
            }
            result.add(map);
        }
        return result;
    }

    /**
     * 获取类及其父类的所有声明字段
     * @param clazz 类
     * @return 所有字段列表（包含父类）
     */
    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                fields.add(field);
            }
            current = current.getSuperclass();
        }
        return fields;
    }

    /**
     * 在类及其父类中查找指定名称的字段
     * @param clazz 类
     * @param fieldName 字段名
     * @return 字段对象，未找到返回null
     */
    private Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 以JSON格式写入响应流
     *
     * @param dataList      数据列表
     * @param fileName      文件名
     * @param dataConverter 数据转换器
     * @param response      HTTP响应
     */
    private <T> void writeToResponseAsJson(List<T> dataList, String fileName,
                                            DataConverter<T> dataConverter,
                                            HttpServletResponse response) throws IOException {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + encodedFileName + ".json");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

        // 使用转换器转换为Map列表
        List<Map<String, Object>> jsonData = dataConverter.convert(dataList);
        byte[] jsonBytes = com.eric.excel.core.JsonExportHandler.getObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsBytes(jsonData);

        // 使用OutputStream写入，避免与filter/interceptor的getOutputStream冲突
        response.getOutputStream().write(jsonBytes);
        response.getOutputStream().flush();
    }

    /**
     * 数据转换器接口
     * 用于将实体数据转换为自定义格式（如JSON结构）
     * @ClassName:   DataConverter
     * @author:      liuQ
     * @date:        2026-04-16 10:00:00
     * @Copyright    ERIC 微信公众号：Eric的技术杂货库
     */
    @FunctionalInterface
    public interface DataConverter<T> {
        /**
         * 转换数据
         *
         * @param dataList 原始数据列表
         * @return 转换后的数据
         */
        List<Map<String, Object>> convert(List<T> dataList);
    }
}
