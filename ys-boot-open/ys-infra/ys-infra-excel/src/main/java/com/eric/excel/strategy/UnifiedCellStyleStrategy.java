package com.eric.excel.strategy;

import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.eric.excel.config.WriteConfig;
import com.eric.excel.utils.StyleBuilder;

/**
 * 统一单元格样式策略
 * 基于EasyExcel的HorizontalCellStyleStrategy实现
 * 支持表头样式和内容样式的自定义配置
 *
 * @ClassName: UnifiedCellStyleStrategy
 * @author: liuQ
 * @date: 2025-02-28
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
public class UnifiedCellStyleStrategy extends HorizontalCellStyleStrategy {

    /**
     * 构造函数
     *
     * @param config 写入配置
     */
    public UnifiedCellStyleStrategy(WriteConfig config) {
        super(StyleBuilder.buildHeadWriteCellStyle(config), StyleBuilder.buildContentWriteCellStyle(config));
    }
}
