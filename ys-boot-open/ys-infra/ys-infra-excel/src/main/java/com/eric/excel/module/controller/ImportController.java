package com.eric.excel.module.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.eric.common.response.ResponseResult;
import com.eric.excel.core.ImportHandler;
import com.eric.excel.module.service.ExcelImportService;
import com.eric.excel.result.ImportResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统一导入控制器
 * 提供数据导入与错误数据下载功能
 * 简化后不再支持预览阶段：上传文件 → 校验 → 直接执行导入
 * @ClassName:   ImportController
 * @author:      liuQ
 * @date:        2026-04-16 10:00:00
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Tag(name = "统一导入")
@Slf4j
@RestController
@RequestMapping("/excel/import")
public class ImportController {

    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Autowired
    private ExcelImportService excelImportService;

    private static final Map<String, ImportHandler<?, ?>> HANDLER_REGISTRY = new ConcurrentHashMap<>();

    public static void registerHandler(String type, ImportHandler<?, ?> handler) {
        HANDLER_REGISTRY.put(type, handler);
    }

    public static void unregisterHandler(String type) {
        HANDLER_REGISTRY.remove(type);
        log.info("取消注册导入处理器，类型：{}", type);
    }

    private ImportHandler<?, ?> getHandler(String importType) {
        ImportHandler<?, ?> handler = HANDLER_REGISTRY.get(importType);
        if (handler == null) {
            return null;
        }
        String permissionCode = handler.getPermissionCode();
        if (permissionCode != null && !permissionCode.isEmpty()) {
            StpUtil.checkPermission(permissionCode);
        }
        return handler;
    }

    private Map<String, String> parseExtraParams(Map<String, String> allParams) {
        Map<String, String> extraParams = new HashMap<>(allParams);
        extraParams.remove("importType");
        return extraParams;
    }

    /**
     * 执行导入：上传文件 → 校验 → 直接执行导入
     * 校验通过的数据将立即写入数据库；失败明细通过返回结果的 failDetails 提供下载
     */
    @Operation(summary = "执行导入", description = "上传Excel文件，校验后立即执行导入，返回导入结果")
    @PostMapping("/execute")
    public ResponseResult<ImportResult> execute(@RequestParam("file") MultipartFile file,
                                                @RequestParam("importType") String importType,
                                                @RequestParam Map<String, String> allParams) {
        log.info("导入请求，文件：{}，类型：{}", file.getOriginalFilename(), importType);

        ImportHandler<?, ?> handler = getHandler(importType);
        if (handler == null) {
            return ResponseResult.fail("不支持的导入类型：" + importType);
        }

        if (file.isEmpty()) {
            return ResponseResult.fail("上传文件不能为空");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return ResponseResult.fail("请上传Excel文件（.xlsx或.xls格式）");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseResult.fail("文件大小不能超过10MB");
        }

        try {
            Map<String, String> extraParams = parseExtraParams(allParams);
            handler.beforeImport(extraParams);
            ImportResult importResult = excelImportService.importFromFile(file, handler);

            log.info("导入完成，成功：{}，失败：{}，耗时：{}ms",
                    importResult.getSuccessCount(), importResult.getFailCount(), importResult.getCostTime());

            return ResponseResult.success(importResult);
        } catch (Exception e) {
            log.error("导入失败", e);
            return ResponseResult.fail("导入失败：" + e.getMessage());
        }
    }

    /**
     * 下载错误数据
     * 错误数据通过请求体传递（来自上一次导入的 failDetails）
     */
    @Operation(summary = "下载错误数据", description = "下载校验失败的导入错误数据")
    @PostMapping("/error/download")
    public void downloadErrorData(@RequestParam("importType") String importType,
                                   @RequestBody List<Object> invalidList,
                                   HttpServletResponse response) {
        log.info("错误数据下载请求，类型：{}，数量：{}", importType, invalidList.size());

        ImportHandler<?, ?> handler = getHandler(importType);
        if (handler == null) {
            throw new RuntimeException("不支持的导入类型：" + importType);
        }

        if (invalidList == null || invalidList.isEmpty()) {
            throw new RuntimeException("没有错误数据可下载");
        }

        try {
            excelImportService.downloadErrorData(
                    invalidList,
                    handler.getDtoClass(),
                    handler.getFileName() + "-错误数据",
                    response
            );
        } catch (Exception e) {
            log.error("下载错误数据失败", e);
            throw new RuntimeException("下载错误数据失败：" + e.getMessage(), e);
        }
    }

    @Operation(summary = "获取导入类型", description = "获取系统支持的导入类型列表")
    @GetMapping("/types")
    public ResponseResult<List<String>> getImportTypes() {
        return ResponseResult.success(List.copyOf(HANDLER_REGISTRY.keySet()));
    }
}
