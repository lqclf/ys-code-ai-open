package com.eric.excel.config;

import com.alibaba.excel.enums.poi.BorderStyleEnum;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 样式配置类
 * @ClassName: StyleConfig
 * @author: liuQ
 * @date: 2025-02-26
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StyleConfig {

    /**
     * 字体名称
     */
    @Builder.Default
    private String fontName = "微软雅黑";

    /**
     * 字体大小
     */
    @Builder.Default
    private Short fontSize = 10;

    /**
     * 是否粗体
     */
    @Builder.Default
    private Boolean fontBold = false;

    /**
     * 字体颜色（十六进制，如：FF0000表示红色）
     */
    @Builder.Default
    private String fontColor = "000000";

    /**
     * 水平对齐方式
     */
    @Builder.Default
    private HorizontalAlignmentEnum horizontalAlignment = HorizontalAlignmentEnum.CENTER;

    /**
     * 垂直对齐方式
     */
    @Builder.Default
    private VerticalAlignmentEnum verticalAlignment = VerticalAlignmentEnum.CENTER;

    /**
     * 边框样式
     */
    @Builder.Default
    private BorderStyleEnum borderStyle = BorderStyleEnum.THIN;

    /**
     * 边框颜色（十六进制）
     */
    @Builder.Default
    private String borderColor = "000000";

    /**
     * 背景颜色（十六进制）
     */
    private String backgroundColor;

    /**
     * 填充模式
     */
    @Builder.Default
    private FillPatternTypeEnum fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND;

    /**
     * 是否自动换行
     */
    @Builder.Default
    private Boolean wrapText = true;

    /**
     * 旋转角度（-90到90）
     */
    private Short rotation;
}
