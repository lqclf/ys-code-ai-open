package com.eric.excel.utils;

import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.eric.excel.config.StyleConfig;
import com.eric.excel.config.WriteConfig;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.util.StringUtils;

/**
 * 样式构建工具类
 * 提供统一的样式构建方法，供各种策略类使用
 *
 * @ClassName: StyleBuilder
 * @author: liuQ
 * @date: 2025-02-28
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
public class StyleBuilder {

    /**
     * 构建表头单元格样式
     *
     * @param config 写入配置
     * @return 表头样式
     */
    public static WriteCellStyle buildHeadWriteCellStyle(WriteConfig config) {
        StyleConfig styleConfig = config.getHeaderStyle();
        if (styleConfig == null) {
            styleConfig = WriteConfig.defaultHeaderStyle();
        }
        return buildWriteCellStyle(styleConfig);
    }

    /**
     * 构建内容单元格样式
     *
     * @param config 写入配置
     * @return 内容样式
     */
    public static WriteCellStyle buildContentWriteCellStyle(WriteConfig config) {
        StyleConfig styleConfig = config.getContentStyle();
        if (styleConfig == null) {
            styleConfig = WriteConfig.defaultContentStyle();
        }
        return buildWriteCellStyle(styleConfig);
    }

    /**
     * 构建单元格样式
     *
     * @param styleConfig 样式配置
     * @return 单元格样式
     */
    public static WriteCellStyle buildWriteCellStyle(StyleConfig styleConfig) {
        WriteCellStyle writeCellStyle = new WriteCellStyle();

        WriteFont font = buildWriteFont(styleConfig);
        writeCellStyle.setWriteFont(font);

        if (styleConfig.getHorizontalAlignment() != null) {
            writeCellStyle.setHorizontalAlignment(
                    HorizontalAlignment.valueOf(styleConfig.getHorizontalAlignment().name()));
        }

        if (styleConfig.getVerticalAlignment() != null) {
            writeCellStyle.setVerticalAlignment(
                    VerticalAlignment.valueOf(styleConfig.getVerticalAlignment().name()));
        }

        if (styleConfig.getBorderStyle() != null) {
            BorderStyle borderStyle = BorderStyle.valueOf(styleConfig.getBorderStyle().name());
            writeCellStyle.setBorderLeft(borderStyle);
            writeCellStyle.setBorderRight(borderStyle);
            writeCellStyle.setBorderTop(borderStyle);
            writeCellStyle.setBorderBottom(borderStyle);
        }

        if (StringUtils.hasText(styleConfig.getBorderColor())) {
            Short colorIndex = convertColorToIndex(styleConfig.getBorderColor());
            if (colorIndex != null) {
                writeCellStyle.setLeftBorderColor(colorIndex);
                writeCellStyle.setRightBorderColor(colorIndex);
                writeCellStyle.setTopBorderColor(colorIndex);
                writeCellStyle.setBottomBorderColor(colorIndex);
            }
        }

        if (StringUtils.hasText(styleConfig.getBackgroundColor())) {
            Short colorIndex = convertColorToIndex(styleConfig.getBackgroundColor());
            if (colorIndex != null) {
                writeCellStyle.setFillForegroundColor(colorIndex);
            }
        }

        if (styleConfig.getWrapText() != null) {
            writeCellStyle.setWrapped(styleConfig.getWrapText());
        }

        if (styleConfig.getRotation() != null) {
            writeCellStyle.setRotation(styleConfig.getRotation());
        }

        return writeCellStyle;
    }

    /**
     * 构建字体样式
     *
     * @param styleConfig 样式配置
     * @return 字体样式
     */
    public static WriteFont buildWriteFont(StyleConfig styleConfig) {
        WriteFont font = new WriteFont();

        if (StringUtils.hasText(styleConfig.getFontName())) {
            font.setFontName(styleConfig.getFontName());
        }

        if (styleConfig.getFontSize() != null) {
            font.setFontHeightInPoints(styleConfig.getFontSize());
        }

        if (styleConfig.getFontBold() != null) {
            font.setBold(styleConfig.getFontBold());
        }

        if (StringUtils.hasText(styleConfig.getFontColor())) {
            Short colorIndex = convertColorToIndex(styleConfig.getFontColor());
            if (colorIndex != null) {
                font.setColor(colorIndex);
            }
        }

        return font;
    }

    /**
     * 将十六进制颜色转换为颜色索引
     * 使用POI标准颜色索引
     *
     * @param hexColor 十六进制颜色值
     * @return 颜色索引
     */
    public static Short convertColorToIndex(String hexColor) {
        if (!StringUtils.hasText(hexColor)) {
            return null;
        }

        String cleanHex = hexColor.replace("#", "").toLowerCase();

        switch (cleanHex) {
            // 基础颜色 (索引 8-15)
            case "000000":
                return IndexedColors.BLACK.getIndex();
            case "ffffff":
                return IndexedColors.WHITE.getIndex();
            case "ff0000":
                return IndexedColors.RED.getIndex();
            case "00ff00":
                return IndexedColors.BRIGHT_GREEN.getIndex();  // BRIGHT_GREEN 对应 00ff00
            case "0000ff":
                return IndexedColors.BLUE.getIndex();
            case "ffff00":
                return IndexedColors.YELLOW.getIndex();
            case "ff00ff":
                return IndexedColors.PINK.getIndex();
            case "00ffff":
                return IndexedColors.TURQUOISE.getIndex();     // TURQUOISE 对应 00ffff

            // 深色系列 (索引 16-21)
            case "800000":  // Maroon
                return IndexedColors.MAROON.getIndex();
            case "008000":  // Green
                return IndexedColors.GREEN.getIndex();
            case "000080":  // Navy
                return IndexedColors.DARK_BLUE.getIndex();     // Navy 映射到 DarkBlue
            case "808000":  // Olive
                return IndexedColors.DARK_YELLOW.getIndex();   // Olive 映射到 DarkYellow
            case "800080":  // Purple
                return IndexedColors.VIOLET.getIndex();        // Purple 映射到 Violet
            case "008080":  // Teal
                return IndexedColors.TEAL.getIndex();

            // 灰色系列 (索引 22-23, 55, 63)
            case "c0c0c0":  // Silver
            case "d3d3d3":  // LightGray
                return IndexedColors.GREY_25_PERCENT.getIndex();
            case "808080":  // Gray
                return IndexedColors.GREY_50_PERCENT.getIndex();
            case "666666":  // DimGray
                return IndexedColors.GREY_40_PERCENT.getIndex();
            case "404040":  // DarkGray
                return IndexedColors.GREY_80_PERCENT.getIndex();
            case "f0f0f0":  // Very Light Gray
                return IndexedColors.GREY_25_PERCENT.getIndex(); // 近似色

            // 蓝色系列 (索引 24, 30-31, 40, 44, 48)
            case "6495ed":  // CornflowerBlue
                return IndexedColors.CORNFLOWER_BLUE.getIndex();
            case "4169e1":  // RoyalBlue
                return IndexedColors.ROYAL_BLUE.getIndex();
            case "99ccff":  // LightCornflowerBlue
                return IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex();
            case "87ceeb":  // SkyBlue
                return IndexedColors.SKY_BLUE.getIndex();
            case "add8e6":  // LightBlue
                return IndexedColors.LIGHT_BLUE.getIndex();
            case "b0c4de":  // LightSteelBlue
                return IndexedColors.PALE_BLUE.getIndex();     // 近似色

            // 暖色系列 (索引 25-27, 29, 45, 47, 52-53)
            case "8b0000":  // DarkRed
                return IndexedColors.DARK_RED.getIndex();
            case "fffacd":  // LemonChiffon
                return IndexedColors.LEMON_CHIFFON.getIndex();
            case "da70d6":  // Orchid
                return IndexedColors.ORCHID.getIndex();
            case "ff7f50":  // Coral
                return IndexedColors.CORAL.getIndex();
            case "ffc0cb":  // Pink
                return IndexedColors.ROSE.getIndex();
            case "d2b48c":  // Tan
                return IndexedColors.TAN.getIndex();
            case "ffb6c1":  // LightPink
            case "ffa07a":  // LightSalmon
                return IndexedColors.LIGHT_ORANGE.getIndex();
            case "ffa500":  // Orange
                return IndexedColors.ORANGE.getIndex();
            case "f4a460":  // SandyBrown
                return IndexedColors.LIGHT_ORANGE.getIndex();  // 近似色

            // 绿色系列 (索引 41-42, 49-50, 56-59)
            case "40e0d0":  // Turquoise
                return IndexedColors.LIGHT_TURQUOISE.getIndex();
            case "90ee90":  // LightGreen
                return IndexedColors.LIGHT_GREEN.getIndex();
            case "98fb98":  // PaleGreen
                return IndexedColors.LIGHT_GREEN.getIndex();   // 近似色
            case "f0e68c":  // Khaki
                return IndexedColors.LIGHT_YELLOW.getIndex();
            case "ffd700":  // Gold
                return IndexedColors.GOLD.getIndex();
            case "669999":  // BlueGrey
                return IndexedColors.BLUE_GREY.getIndex();
            case "008b8b":  // DarkCyan
                return IndexedColors.DARK_TEAL.getIndex();
            case "2e8b57":  // SeaGreen
                return IndexedColors.SEA_GREEN.getIndex();
            case "006400":  // DarkGreen
                return IndexedColors.DARK_GREEN.getIndex();
            case "556b2f":  // DarkOliveGreen
                return IndexedColors.OLIVE_GREEN.getIndex();

            // 其他颜色 (索引 60-62)
            case "a52a2a":  // Brown
                return IndexedColors.BROWN.getIndex();
            case "dda0dd":  // Plum
                return IndexedColors.PLUM.getIndex();
            case "4b0082":  // Indigo
                return IndexedColors.INDIGO.getIndex();

            // 特殊颜色 (索引 64)
            case "automatic":
            case "default":
                return IndexedColors.AUTOMATIC.getIndex();

            // 默认返回黑色
            default:
                return IndexedColors.BLACK.getIndex();
        }
    }

}
