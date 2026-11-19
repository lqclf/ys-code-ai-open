package com.eric.excel.core;

import com.eric.excel.config.HeaderConfig;
import com.eric.excel.config.StyleConfig;
import com.eric.excel.module.dto.ExcelStyleConfigDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * 导出处理器接口
 * 定义Excel数据导出的标准流程，包括数据查询、字段过滤、样式配置等
 *
 * @ClassName:   ExportHandler
 * @author:      liuQ
 * @param <T> DTO类型
 * @param <Q> 查询参数类型
 * @date:        2025-02-27
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
public interface ExportHandler<T, Q> {

    /**
     * 获取DTO类
     */
    Class<T> getDtoClass();

    /**
     * 执行导出（带列过滤）
     *
     * @param queryParams 查询参数
     * @param selectedFields 选中的字段列表（null表示导出全部字段）
     * @param response HTTP响应
     */
    void export(Q queryParams, List<String> selectedFields, HttpServletResponse response);

    /**
     * 执行导出（带列过滤和样式配置）
     *
     * @param queryParams 查询参数
     * @param selectedFields 选中的字段列表（null表示导出全部字段）
     * @param fileType 文件类型（xlsx、xls、csv）
     * @param styleConfigDTO 前端样式配置
     * @param response HTTP响应
     */
    default void export(Q queryParams, List<String> selectedFields, String fileType,
                        ExcelStyleConfigDTO styleConfigDTO, HttpServletResponse response) {
        export(queryParams, selectedFields, response);
    }

    /**
     * 执行导出（导出全部字段）
     *
     * @param queryParams 查询参数
     * @param response HTTP响应
     */
    default void export(Q queryParams, HttpServletResponse response) {
        export(queryParams, null, response);
    }

    /**
     * 查询数据
     *
     * @param queryParams 查询参数
     * @return 数据列表
     */
    List<T> queryData(Q queryParams);

    /**
     * 字段过滤后处理
     * 默认实现：不做处理
     *
     * @param dataList 数据列表
     * @param selectedFields 选中字段
     */
    default void afterFilter(List<T> dataList, List<String> selectedFields) {
    }

    /**
     * 获取导出样式配置
     * 默认实现：返回null，使用默认样式
     *
     * @return 样式配置
     */
    default StyleConfig getExportStyleConfig() {
        return null;
    }

    /**
     * 获取导出表头配置
     * 默认实现：返回null，使用注解定义的表头
     *
     * @return 表头配置列表
     */
    default List<HeaderConfig> getExportHeaderConfig() {
        return null;
    }

    /**
     * 获取文件名
     * 默认实现：返回"导出数据"
     *
     * @return 文件名（不含扩展名）
     */
    default String getFileName() {
        return "导出数据";
    }

    /**
     * 获取Sheet名称
     * 默认实现：返回"Sheet1"
     *
     * @return Sheet名称
     */
    default String getSheetName() {
        return "Sheet1";
    }

    /**
     * 获取列宽配置
     * 默认实现：返回null，使用自动列宽
     *
     * @return 列宽配置（key为字段名，value为宽度）
     */
    default Map<String, Integer> getColumnWidths() {
        return null;
    }

    /**
     * 获取需要导出的字段列表
     * 默认实现：返回null，表示导出全部字段
     *
     * @param selectedFields 前端选中的字段列表
     * @return 实际需要导出的字段列表
     */
    default List<String> getIncludeColumns(List<String> selectedFields) {
        return selectedFields;
    }

    /**
     * 获取查询参数DTO的Class类型
     * 用于统一导出控制器中 Map → Q 的类型转换
     *
     * @return 查询参数DTO的Class，默认返回null需要子类重写
     */
    default Class<Q> getQueryClass() {
        return null;
    }

    /**
     * 获取导出所需的权限标识
     * 由统一 Controller 在调用前进行校验
     *
     * @return 权限标识，如 "system:user:export"，返回null表示不需要权限校验
     */
    default String getPermissionCode() {
        return null;
    }

    /**
     * 导出前置准备（接收业务特有参数）
     * 在导出前由统一控制器调用
     *
     * @param params 额外参数（来自前端请求）
     */
    default void beforeExport(Map<String, String> params) {
    }
}
