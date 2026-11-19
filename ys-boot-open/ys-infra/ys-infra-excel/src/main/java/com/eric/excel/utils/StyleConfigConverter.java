package com.eric.excel.utils;

import com.alibaba.excel.enums.poi.BorderStyleEnum;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import com.eric.excel.config.StyleConfig;
import com.eric.excel.module.dto.DataStyleConfigDTO;
import com.eric.excel.module.dto.ExcelStyleConfigDTO;
import com.eric.excel.module.dto.HeaderStyleConfigDTO;
import org.springframework.util.StringUtils;

/**
 * 样式配置转换工具类
 * 将前端传递的DTO转换为后端使用的StyleConfig
 *
 * @ClassName: StyleConfigConverter
 * @author: liuQ
 * @date: 2025-02-28
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
public class StyleConfigConverter {

    /**
     * 将DTO转换为StyleConfig（用于表头）
     *
     * @param dto 前端样式配置DTO
     * @return StyleConfig
     */
    public static StyleConfig convertToHeaderStyleConfig(ExcelStyleConfigDTO dto) {
        if (dto == null || dto.getHeaderStyle() == null) {
            return null;
        }

        HeaderStyleConfigDTO headerDto = dto.getHeaderStyle();
        StyleConfig.StyleConfigBuilder builder = StyleConfig.builder();

        // 字体配置
        if (StringUtils.hasText(headerDto.getFontName())) {
            builder.fontName(headerDto.getFontName());
        }
        if (headerDto.getFontSize() != null) {
            builder.fontSize(headerDto.getFontSize().shortValue());
        }
        if (StringUtils.hasText(headerDto.getFontColor())) {
            // 移除可能存在的#前缀
            builder.fontColor(headerDto.getFontColor().replace("#", ""));
        }
        if (headerDto.getFontBold() != null) {
            builder.fontBold(headerDto.getFontBold());
        }

        // 表头背景色
        if (StringUtils.hasText(headerDto.getBackgroundColor())) {
            builder.backgroundColor(headerDto.getBackgroundColor().replace("#", ""));
            builder.fillPatternType(FillPatternTypeEnum.SOLID_FOREGROUND);
        }

        // 表头对齐方式
        if (StringUtils.hasText(headerDto.getHorizontalAlign())) {
            builder.horizontalAlignment(convertHorizontalAlign(headerDto.getHorizontalAlign()));
        }
        if (StringUtils.hasText(headerDto.getVerticalAlign())) {
            builder.verticalAlignment(convertVerticalAlign(headerDto.getVerticalAlign()));
        }

        // 边框配置
        if (StringUtils.hasText(headerDto.getBorderStyle())) {
            builder.borderStyle(convertBorderStyle(headerDto.getBorderStyle()));
        }
        if (StringUtils.hasText(headerDto.getBorderColor())) {
            builder.borderColor(headerDto.getBorderColor().replace("#", ""));
        }

        return builder.build();
    }

    /**
     * 将DTO转换为StyleConfig（用于内容/数据行）
     *
     * @param dto 前端样式配置DTO
     * @return StyleConfig
     */
    public static StyleConfig convertToContentStyleConfig(ExcelStyleConfigDTO dto) {
        if (dto == null || dto.getDataStyle() == null) {
            return null;
        }

        DataStyleConfigDTO dataDto = dto.getDataStyle();
        StyleConfig.StyleConfigBuilder builder = StyleConfig.builder();

        // 字体配置
        if (StringUtils.hasText(dataDto.getFontName())) {
            builder.fontName(dataDto.getFontName());
        }
        if (dataDto.getFontSize() != null) {
            builder.fontSize(dataDto.getFontSize().shortValue());
        }
        if (StringUtils.hasText(dataDto.getFontColor())) {
            builder.fontColor(dataDto.getFontColor().replace("#", ""));
        }
        if (dataDto.getFontBold() != null) {
            builder.fontBold(dataDto.getFontBold());
        }

        // 内容对齐方式
        if (StringUtils.hasText(dataDto.getHorizontalAlign())) {
            builder.horizontalAlignment(convertHorizontalAlign(dataDto.getHorizontalAlign()));
        }
        if (StringUtils.hasText(dataDto.getVerticalAlign())) {
            builder.verticalAlignment(convertVerticalAlign(dataDto.getVerticalAlign()));
        }

        // 边框配置
        if (StringUtils.hasText(dataDto.getBorderStyle())) {
            builder.borderStyle(convertBorderStyle(dataDto.getBorderStyle()));
        }
        if (StringUtils.hasText(dataDto.getBorderColor())) {
            builder.borderColor(dataDto.getBorderColor().replace("#", ""));
        }

        // 自动换行
        if (dataDto.getWrapText() != null) {
            builder.wrapText(dataDto.getWrapText());
        }

        return builder.build();
    }

    /**
     * 转换水平对齐方式
     *
     * @param align 对齐方式字符串
     * @return HorizontalAlignmentEnum
     */
    private static HorizontalAlignmentEnum convertHorizontalAlign(String align) {
        if (!StringUtils.hasText(align)) {
            return HorizontalAlignmentEnum.CENTER;
        }
        switch (align.toLowerCase()) {
            case "left":
                return HorizontalAlignmentEnum.LEFT;
            case "right":
                return HorizontalAlignmentEnum.RIGHT;
            case "center":
            default:
                return HorizontalAlignmentEnum.CENTER;
        }
    }

    /**
     * 转换垂直对齐方式
     *
     * @param align 对齐方式字符串
     * @return VerticalAlignmentEnum
     */
    private static VerticalAlignmentEnum convertVerticalAlign(String align) {
        if (!StringUtils.hasText(align)) {
            return VerticalAlignmentEnum.CENTER;
        }
        switch (align.toLowerCase()) {
            case "top":
                return VerticalAlignmentEnum.TOP;
            case "bottom":
                return VerticalAlignmentEnum.BOTTOM;
            case "center":
            default:
                return VerticalAlignmentEnum.CENTER;
        }
    }

    /**
     * 转换边框样式
     *
     * @param style 边框样式字符串
     * @return BorderStyleEnum
     */
    private static BorderStyleEnum convertBorderStyle(String style) {
        if (!StringUtils.hasText(style)) {
            return BorderStyleEnum.THIN;
        }
        switch (style.toLowerCase()) {
            case "none":
                return BorderStyleEnum.NONE;
            case "medium":
                return BorderStyleEnum.MEDIUM;
            case "thick":
                return BorderStyleEnum.THICK;
            case "thin":
            default:
                return BorderStyleEnum.THIN;
        }
    }
}
