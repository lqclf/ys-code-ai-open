package com.eric.excel.module.service;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.eric.excel.config.ExcelImportConfig;
import com.eric.excel.config.WriteConfig;
import com.eric.excel.context.PersistContext;
import com.eric.excel.context.ProcessContext;
import com.eric.excel.core.ImportHandler;
import com.eric.excel.handler.ReflectRowNumHandler;
import com.eric.excel.handler.RowNumHandler;
import com.eric.excel.result.ImportResult;
import com.eric.excel.result.ReadResult;
import com.eric.excel.result.ValidationResult;
import com.eric.excel.utils.EasyExcelUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Excel导入服务
 * 封装通用的导入逻辑：读取文件 → 校验 → 持久化
 * 简化后不再支持预览阶段，校验通过的数据直接执行导入
 * @ClassName:  ExcelImportService
 * @author:     liuQ
 * @date:       2026-02-28 23:56:51
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
@Service
public class ExcelImportService {

    @Autowired
    private ExcelImportConfig importConfig;

    private final Map<Class<?>, RowNumHandler<?>> rowNumHandlerCache = new ConcurrentHashMap<>();

    /**
     * 解析Excel文件并执行导入
     * 流程：读取Excel → 逐条校验（仅保留有效数据）→ 数据处理 → 持久化 → 返回导入结果
     * 失败明细通过 ImportResult.failDetails 返回，前端可直接用于下载错误数据
     *
     * @param file    Excel文件
     * @param handler 导入处理器
     * @return 导入结果
     */
    public <R, T> ImportResult importFromFile(MultipartFile file, ImportHandler<R, T> handler) {
        long startTime = System.currentTimeMillis();
        log.info("开始导入，文件名：{}，处理器：{}", file.getOriginalFilename(), handler.getClass().getSimpleName());

        List<R> dataList;
        try (InputStream is = file.getInputStream()) {
            ReadResult<R> readResult = EasyExcelUtils.read(is, handler.getDtoClass());
            dataList = readResult.getSuccessData();
        } catch (IOException e) {
            log.error("读取Excel文件失败", e);
            throw new RuntimeException("读取Excel文件失败：" + e.getMessage(), e);
        }

        if (dataList == null || dataList.isEmpty()) {
            log.warn("Excel文件中无有效数据");
            return ImportResult.builder()
                    .successCount(0)
                    .failCount(0)
                    .costTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        // 数据校验：仅保留校验通过的数据，无效数据直接写入失败明细
        List<R> validList = filterValidData(dataList, handler);

        if (validList.isEmpty()) {
            long costTime = System.currentTimeMillis() - startTime;
            ImportResult result = ImportResult.builder()
                    .successCount(0)
                    .failCount(0)
                    .costTime(costTime)
                    .build();
            log.warn("导入完成，校验通过数据为空，共 {} 条无效数据，耗时：{}ms", dataList.size(), costTime);
            return result;
        }

        // 执行持久化导入
        ImportResult result = importData(validList, handler);
        result.setCostTime(System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * 执行导入
     * 流程：数据处理（分类） → 持久化 → 返回导入结果
     *
     * @param dataList 数据列表
     * @param handler  导入处理器
     * @return 导入结果
     */
    public <R, T> ImportResult importData(List<R> dataList, ImportHandler<R, T> handler) {
        long startTime = System.currentTimeMillis();
        log.info("开始执行导入，数据量：{}，处理器：{}", dataList.size(), handler.getClass().getSimpleName());

        ProcessContext<R, T> processContext;
        try {
            processContext = handler.processData(dataList);
            if (processContext == null) {
                processContext = new ProcessContext<>();
                processContext.setOriginalData(dataList);
                processContext.setProcessedData(dataList);
            }
        } catch (Exception e) {
            log.error("数据处理失败", e);
            throw new RuntimeException("数据处理失败：" + e.getMessage(), e);
        }

        PersistContext persistContext;
        try {
            persistContext = handler.persist(processContext);
            if (persistContext == null) {
                persistContext = new PersistContext();
            }
        } catch (Exception e) {
            log.error("数据持久化失败", e);
            throw new RuntimeException("数据持久化失败：" + e.getMessage(), e);
        }

        long costTime = System.currentTimeMillis() - startTime;

        int totalSuccess = persistContext.getInsertCount()
                + persistContext.getUpdateCount()
                + persistContext.getDeleteCount()
                + persistContext.getCustomCount();

        ImportResult result = ImportResult.builder()
                .successCount(totalSuccess)
                .failCount(persistContext.getFailCount())
                .costTime(costTime)
                .build();

        if (persistContext.getFailDetails() != null) {
            for (PersistContext.FailDetail detail : persistContext.getFailDetails()) {
                result.addFailDetail(detail.getRowNum(), detail.getData(), detail.getErrorMsg());
            }
        }

        if (processContext.hasErrors()) {
            processContext.getErrorList().forEach(errorRow ->
                    result.addFailDetail(errorRow.getRowNum(), errorRow.getOriginalData(), errorRow.getErrorMsg())
            );
        }

        log.info("导入执行完成，成功：{}（插入：{}，更新：{}，删除：{}，自定义：{}），失败：{}，耗时：{}ms",
                totalSuccess,
                persistContext.getInsertCount(),
                persistContext.getUpdateCount(),
                persistContext.getDeleteCount(),
                persistContext.getCustomCount(),
                result.getFailCount(),
                costTime);

        return result;
    }

    /**
     * 下载导入错误数据
     *
     * @param invalidList 无效数据列表
     * @param dtoClass    DTO类
     * @param fileName    文件名
     * @param response    HTTP响应
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void downloadErrorData(List<?> invalidList, Class<?> dtoClass, String fileName, HttpServletResponse response) {
        try {
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + encodedFileName);
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

            WriteConfig config = WriteConfig.builder()
                    .sheetName("错误数据")
                    .excelType(ExcelTypeEnum.XLSX)
                    .build();

            EasyExcelUtils.write(response.getOutputStream(), (List) invalidList, (Class) dtoClass, config);

            log.info("错误数据下载完成，文件名：{}，数据量：{}", fileName, invalidList.size());

        } catch (IOException e) {
            log.error("下载错误数据失败", e);
            throw new RuntimeException("下载错误数据失败：" + e.getMessage(), e);
        }
    }

    /**
     * 校验并过滤有效数据
     * 校验失败的数据行不会进入导入流程，但其行号与错误信息可用于返回给前端
     *
     * @param dataList 原始数据列表
     * @param handler  导入处理器
     * @return 校验通过的数据列表
     */
    private <R, T> List<R> filterValidData(List<R> dataList, ImportHandler<R, T> handler) {
        RowNumHandler<R> rowNumHandler = getRowNumHandler(handler.getDtoClass());
        Map<String, Object> validateContext = handler.prepareValidate(dataList);
        List<R> validList = new ArrayList<>(dataList.size());

        for (int i = 0; i < dataList.size(); i++) {
            R dto = dataList.get(i);
            int rowNum = importConfig.calculateRowNum(i);

            if (importConfig.isAutoSetRowNum()) {
                rowNumHandler.setRowNum(dto, rowNum);
            }

            ValidationResult validationResult;
            try {
                validationResult = handler.validate(dto, rowNum, validateContext);
            } catch (Exception e) {
                log.warn("数据校验异常，行号：{}，原因：{}", rowNum, e.getMessage());
                continue;
            }

            if (validationResult != null && validationResult.isValid()) {
                validList.add(dto);
            }
        }
        return validList;
    }

    /**
     * 获取行号处理器
     * @param dtoClass DTO类
     * @return 行号处理器实例
     */
    @SuppressWarnings("unchecked")
    private <T> RowNumHandler<T> getRowNumHandler(Class<T> dtoClass) {
        return (RowNumHandler<T>) rowNumHandlerCache.computeIfAbsent(dtoClass, k ->
                new ReflectRowNumHandler<>(
                        importConfig.getRowNumField(),
                        importConfig.getValidField(),
                        importConfig.getErrorMsgField()
                )
        );
    }
}
