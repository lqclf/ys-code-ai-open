package com.eric.excel.handler;

import com.eric.excel.config.HeaderConfig;
import com.eric.excel.config.StyleConfig;
import com.eric.excel.core.ExportHandler;
import com.eric.excel.module.dto.ExcelStyleConfigDTO;
import com.eric.excel.module.service.ExcelExportService;
import com.eric.excel.utils.StyleConfigConverter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 导出处理器抽象类
 * 提供通用的导出流程实现，子类只需实现特定的业务逻辑
 * @ClassName:  AbstractExportHandler
 * @author:     liuQ
 * @param <T> DTO类型
 * @param <Q> 查询参数类型
 * @date:       2026-02-27 14:19:45
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
public abstract class AbstractExportHandler<T, Q> implements ExportHandler<T, Q> {

    /** Excel导出服务 */
    @Autowired
    protected ExcelExportService excelExportService;

    /**
     * 获取DTO类
     * 子类必须实现此方法，用于EasyExcel读取Excel注解
     */
    @Override
    public abstract Class<T> getDtoClass();

    /**
     * 执行导出（带列过滤）
     * 序号列通过 selectedFields 中是否包含 "selectedFields" 来判断
     *
     * @param queryParams 查询参数
     * @param selectedFields 选中的字段列表（null表示导出全部字段，包含"selectedFields"表示需要序号列）
     * @param response HTTP响应
     */
    @Override
    public void export(Q queryParams, List<String> selectedFields, HttpServletResponse response) {
        excelExportService.export(queryParams, selectedFields, this, response);
    }

    /**
     * 执行导出（带列过滤和文件类型）
     * 序号列通过 selectedFields 中是否包含 "selectedFields" 来判断
     *
     * @param queryParams 查询参数
     * @param selectedFields 选中的字段列表（null表示导出全部字段，包含"selectedFields"表示需要序号列）
     * @param fileType 文件类型（xlsx、xls、csv）
     * @param response HTTP响应
     */
    public void export(Q queryParams, List<String> selectedFields, String fileType, HttpServletResponse response) {
        excelExportService.export(queryParams, selectedFields, fileType, this, response);
    }

    /**
     * 执行导出（带列过滤、文件类型和样式配置）
     * 序号列通过 selectedFields 中是否包含 "selectedFields" 来判断
     *
     * @param queryParams 查询参数
     * @param selectedFields 选中的字段列表（null表示导出全部字段，包含"selectedFields"表示需要序号列）
     * @param fileType 文件类型（xlsx、xls、csv）
     * @param styleConfigDTO 前端样式配置DTO
     * @param response HTTP响应
     */
    public void export(Q queryParams, List<String> selectedFields, String fileType,
                       ExcelStyleConfigDTO styleConfigDTO, HttpServletResponse response) {
        StyleConfig headerStyle = StyleConfigConverter.convertToHeaderStyleConfig(styleConfigDTO);
        StyleConfig contentStyle = StyleConfigConverter.convertToContentStyleConfig(styleConfigDTO);
        excelExportService.export(queryParams, selectedFields, fileType, headerStyle, contentStyle, this, response);
    }

    /**
     * 执行导出（导出全部字段）
     *
     * @param queryParams 查询参数
     * @param response HTTP响应
     */
    @Override
    public void export(Q queryParams, HttpServletResponse response) {
        export(queryParams, null, response);
    }

    /**
     * 查询数据
     * 子类必须实现此方法
     */
    @Override
    public abstract List<T> queryData(Q queryParams);

    /**
     * 字段过滤后处理
     * 子类可重写此方法实现自定义后处理逻辑
     */
    @Override
    public void afterFilter(List<T> dataList, List<String> selectedFields) {
    }

    /**
     * 获取导出样式配置
     * 子类可重写此方法自定义导出样式
     */
    @Override
    public StyleConfig getExportStyleConfig() {
        return null;
    }

    /**
     * 获取导出表头配置
     * 子类可重写此方法自定义表头
     */
    @Override
    public List<HeaderConfig> getExportHeaderConfig() {
        return null;
    }

    /**
     * 获取文件名
     * 子类可重写此方法自定义文件名
     */
    @Override
    public String getFileName() {
        return "导出数据";
    }

    /**
     * 获取Sheet名称
     * 子类可重写此方法自定义Sheet名称
     */
    @Override
    public String getSheetName() {
        return "Sheet1";
    }

    /**
     * 获取列宽配置
     * 子类可重写此方法自定义列宽
     */
    @Override
    public Map<String, Integer> getColumnWidths() {
        return null;
    }

    /**
     * 获取需要导出的字段列表
     * 子类可重写此方法自定义字段过滤逻辑
     *
     * @param selectedFields 前端选中的字段列表
     * @return 实际需要导出的字段列表
     */
    @Override
    public List<String> getIncludeColumns(List<String> selectedFields) {
        return selectedFields;
    }
}
