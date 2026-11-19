package com.eric.excel.module.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.eric.common.response.ResponseResult;
import com.eric.excel.core.ExportHandler;
import com.eric.excel.module.service.ExcelExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统一导出控制器
 * 提供数据导出执行、导出类型查询功能
 * @ClassName:   ExportController
 * @author:      liuQ
 * @date:        2026-05-22 10:00:00
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Tag(name = "统一导出")
@Slf4j
@RestController
@RequestMapping("/excel/export")
public class ExportController {

    @Autowired
    private ExcelExportService excelExportService;

    private static final Map<String, ExportHandler<?, ?>> HANDLER_REGISTRY = new ConcurrentHashMap<>();

    public static void registerHandler(String type, ExportHandler<?, ?> handler) {
        HANDLER_REGISTRY.put(type, handler);
        //log.info("注册导出处理器，类型：{}，处理器：{}", type, handler.getClass().getSimpleName());
    }

    public static void unregisterHandler(String type) {
        HANDLER_REGISTRY.remove(type);
        log.info("取消注册导出处理器，类型：{}", type);
    }

    /**
     * 获取 Handler 并进行权限校验
     */
    private ExportHandler<?, ?> getHandler(String exportType) {
        ExportHandler<?, ?> handler = HANDLER_REGISTRY.get(exportType);
        if (handler == null) {
            return null;
        }
        String permissionCode = handler.getPermissionCode();
        if (permissionCode != null && !permissionCode.isEmpty()) {
            StpUtil.checkPermission(permissionCode);
        }
        return handler;
    }

    /**
     * 执行导出
     */
    @Operation(summary = "执行导出", description = "根据查询条件执行数据导出")
    @GetMapping("/execute")
    public void execute(@RequestParam("exportType") String exportType,
                        @RequestParam Map<String, String> allParams,
                        HttpServletResponse response) {
        log.info("导出请求，类型：{}", exportType);

        ExportHandler<?, ?> handler = getHandler(exportType);
        if (handler == null) {
            throw new RuntimeException("不支持的导出类型：" + exportType);
        }

        String selectedFieldsStr = allParams.remove("selectedFields");
        List<String> selectedFields = StringUtils.isNotBlank(selectedFieldsStr)
                ? Arrays.asList(selectedFieldsStr.split(","))
                : null;

        String fileType = allParams.remove("fileType");
        if (StringUtils.isBlank(fileType)) {
            fileType = "xlsx";
        }

        allParams.remove("exportType");
        handler.beforeExport(allParams);

        try {
            excelExportService.exportFromMaps(allParams, selectedFields, fileType, handler, response);
        } catch (Exception e) {
            log.error("导出失败", e);
            throw new RuntimeException("导出失败：" + e.getMessage(), e);
        }
    }

    /**
     * 获取支持的导出类型列表
     */
    @Operation(summary = "获取导出类型", description = "获取系统支持的导出类型列表")
    @GetMapping("/types")
    public ResponseResult<List<String>> getExportTypes() {
        return ResponseResult.success(List.copyOf(HANDLER_REGISTRY.keySet()));
    }
}