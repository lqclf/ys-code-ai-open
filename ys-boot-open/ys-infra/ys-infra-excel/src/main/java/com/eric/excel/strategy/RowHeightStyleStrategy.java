package com.eric.excel.strategy;

import com.alibaba.excel.write.style.row.AbstractRowHeightStyleStrategy;
import com.eric.excel.config.WriteConfig;
import org.apache.poi.ss.usermodel.Row;

/**
 * 行高样式策略
 * 用于自定义Excel导出时的行高设置
 * 支持自动换行时行高自适应
 *
 * @ClassName: RowHeightStyleStrategy
 * @author: liuQ
 * @date: 2025-02-26
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
public class RowHeightStyleStrategy extends AbstractRowHeightStyleStrategy {

    /** 写入配置 */
    private final WriteConfig config;

    /**
     * 构造函数
     *
     * @param config 写入配置
     */
    public RowHeightStyleStrategy(WriteConfig config) {
        this.config = config;
    }

    /**
     * 设置表头行高
     *
     * @param row             当前行
     * @param relativeRowIndex 相对行索引
     */
    @Override
    protected void setHeadColumnHeight(Row row, int relativeRowIndex) {
        if (config.getHeaderRowHeight() != null && config.getHeaderRowHeight() > 0) {
            row.setHeightInPoints(config.getHeaderRowHeight());
        }
    }

    /**
     * 设置内容行高
     * 当启用自动换行时，使用自适应高度（-1），否则使用配置的固定行高
     *
     * @param row             当前行
     * @param relativeRowIndex 相对行索引
     */
    @Override
    protected void setContentColumnHeight(Row row, int relativeRowIndex) {
        boolean wrapText = config.getContentStyle() != null
                && Boolean.TRUE.equals(config.getContentStyle().getWrapText());
        if (wrapText) {
            row.setHeight((short) -1);
        } else if (config.getDefaultRowHeight() != null && config.getDefaultRowHeight() > 0) {
            row.setHeightInPoints(config.getDefaultRowHeight());
        }
    }
}