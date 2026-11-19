package com.eric.excel.config;

import com.alibaba.excel.enums.poi.BorderStyleEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 写入配置类
 * @ClassName: WriteConfig
 * @author: liuQ
 * @date: 2025-02-26
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WriteConfig {

    /**
     * 默认Sheet名称
     */
    private static final String DEFAULT_SHEET_NAME = "Sheet1";

    /**
     * Sheet名称
     */
    @Builder.Default
    private String sheetName = DEFAULT_SHEET_NAME;

    /**
     * 表头配置列表
     */
    private List<HeaderConfig> headers;

    /**
     * 表头样式
     */
    @Builder.Default
    private StyleConfig headerStyle = defaultHeaderStyle();

    /**
     * 内容样式
     */
    @Builder.Default
    private StyleConfig contentStyle = defaultContentStyle();

    /**
     * 列级样式（key为字段名）
     */
    @Builder.Default
    private Map<String, StyleConfig> columnStyles = new HashMap<>();

    /**
     * 默认列宽
     */
    @Builder.Default
    private Integer defaultColumnWidth = 40;

    /**
     * 指定列宽（key为字段名，value为宽度）
     */
    @Builder.Default
    private Map<String, Integer> columnWidths = new HashMap<>();

    /**
     * 是否自动调整列宽
     */
    @Builder.Default
    private Boolean autoAdjustColumnWidth = true;

    /**
     * 默认行高
     */
    @Builder.Default
    private Short defaultRowHeight = 20;

    /**
     * 表头行高
     */
    @Builder.Default
    private Short headerRowHeight = 30;

    /**
     * 是否写入表头
     */
    @Builder.Default
    private Boolean writeHeader = true;

    /**
     * 是否自动关闭输出流
     */
    @Builder.Default
    private Boolean autoCloseStream = true;

    /**
     * Excel类型
     */
    @Builder.Default
    private ExcelTypeEnum excelType = ExcelTypeEnum.XLSX;

    /**
     * 模板文件路径（用于模板填充）
     */
    private String templatePath;

    /**
     * 数据格式化配置
     */
    @Builder.Default
    private Map<String, String> dataFormat = new HashMap<>();

    /**
     * 需要导出的字段列表（用于列过滤）
     * 如果为null或空，则导出全部字段
     */
    private List<String> includeColumnFieldNames;

    /**
     * 是否包含序号列
     */
    @Builder.Default
    private Boolean includeSeq = false;

    /**
     * 序号列标题
     */
    @Builder.Default
    private String seqColumnTitle = "序号";

    /**
     * 获取默认表头样式
     */
    public static StyleConfig defaultHeaderStyle() {
        return StyleConfig.builder()
                .fontName("微软雅黑")
                .fontSize((short) 12)
                .fontBold(true)
                .fontColor("000000")
                .horizontalAlignment(HorizontalAlignmentEnum.CENTER)
                .verticalAlignment(VerticalAlignmentEnum.CENTER)
                .backgroundColor("d3d3d3")
                .borderStyle(BorderStyleEnum.THIN)
                .borderColor("000000")
                .build();
    }

    /**
     * 获取默认内容样式
     */
    public static StyleConfig defaultContentStyle() {
        return StyleConfig.builder()
                .fontName("微软雅黑")
                .fontSize((short) 10)
                .fontBold(false)
                .fontColor("000000")
                .horizontalAlignment(HorizontalAlignmentEnum.CENTER)
                .verticalAlignment(VerticalAlignmentEnum.CENTER)
                .borderStyle(BorderStyleEnum.THIN)
                .borderColor("000000")
                .wrapText(true)
                .build();
    }
}
