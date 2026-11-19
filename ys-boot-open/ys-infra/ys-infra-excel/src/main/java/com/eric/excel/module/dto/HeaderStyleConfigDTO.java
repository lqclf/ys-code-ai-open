package com.eric.excel.module.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Excel表头样式配置DTO
 * 用于配置Excel导出时表头的样式参数
 *
 * @ClassName: HeaderStyleConfigDTO
 * @author: liuQ
 * @date: 2025-02-28
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeaderStyleConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字体名称
     */
    private String fontName;

    /**
     * 字体大小
     */
    private Integer fontSize;

    /**
     * 字体颜色（十六进制，如：#000000）
     */
    private String fontColor;

    /**
     * 是否加粗
     */
    private Boolean fontBold;

    /**
     * 背景色（十六进制，如：#FFFFFF）
     */
    private String backgroundColor;

    /**
     * 水平对齐方式（left、center、right）
     */
    private String horizontalAlign;

    /**
     * 垂直对齐方式（top、center、bottom）
     */
    private String verticalAlign;

    /**
     * 边框样式（none、thin、medium、thick）
     */
    private String borderStyle;

    /**
     * 边框颜色（十六进制，如：#000000）
     */
    private String borderColor;
}
