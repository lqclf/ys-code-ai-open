package com.eric.excel.strategy;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import com.eric.excel.config.WriteConfig;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 列宽自适应策略
 * 根据列标题和实际数据内容自动计算列宽，支持CJK字符宽度补偿
 *
 * @ClassName: ColumnWidthStyleStrategy
 * @author: liuQ
 * @date: 2025-02-26
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
public class ColumnWidthStyleStrategy extends AbstractColumnWidthStyleStrategy {

    /** CJK字符宽度倍数（中文字符占2个英文字符宽度） */
    private static final int CHAR_WIDTH_CJK = 2;
    /** 普通字符宽度倍数 */
    private static final int CHAR_WIDTH_NORMAL = 1;
    /** 列宽内边距字符数 */
    private static final int PADDING_CHARS = 4;
    /** 最小列宽（字符数） */
    private static final int MIN_COLUMN_WIDTH = 20;
    /** 最大列宽（字符数） */
    private static final int MAX_COLUMN_WIDTH = 200;

    /** 写入配置 */
    private final WriteConfig config;
    /** 列宽缓存（key为列索引，value为最大字符宽度） */
    private final Map<Integer, Integer> columnWidthMap = new HashMap<>();

    /**
     * 构造函数
     *
     * @param config 写入配置
     */
    public ColumnWidthStyleStrategy(WriteConfig config) {
        this.config = config;
    }

    /**
     * 设置列宽
     * 根据列标题和实际内容数据动态计算最优列宽
     *
     * @param writeSheetHolder 写入Sheet持有者
     * @param cellDataList     单元格数据列表
     * @param cell             当前单元格
     * @param head             表头信息
     * @param relativeRowIndex 相对行索引
     * @param isHead           是否为表头
     */
    @Override
    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList,
                                  Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        Sheet sheet = writeSheetHolder.getSheet();
        int columnIndex = cell.getColumnIndex();

        Integer configuredWidth = null;
        if (config.getColumnWidths() != null && head != null
                && config.getColumnWidths().containsKey(head.getFieldName())) {
            configuredWidth = config.getColumnWidths().get(head.getFieldName());
        }

        if (configuredWidth != null && configuredWidth > 0) {
            sheet.setColumnWidth(columnIndex, configuredWidth * 256);
            return;
        }

        if (!Boolean.TRUE.equals(config.getAutoAdjustColumnWidth())) {
            if (config.getDefaultColumnWidth() != null && config.getDefaultColumnWidth() > 0) {
                sheet.setColumnWidth(columnIndex, config.getDefaultColumnWidth() * 256);
            }
            return;
        }

        int contentWidth = computeContentCharWidth(cellDataList, cell, head, isHead);

        Integer currentMax = columnWidthMap.get(columnIndex);
        if (currentMax == null || contentWidth > currentMax) {
            columnWidthMap.put(columnIndex, contentWidth);
            int finalWidth = Math.max(MIN_COLUMN_WIDTH, Math.min(contentWidth + PADDING_CHARS, MAX_COLUMN_WIDTH));
            sheet.setColumnWidth(columnIndex, finalWidth * 256);
        }
    }

    /**
     * 计算单元格内容的字符宽度
     * CJK字符按2倍宽度计算，普通字符按1倍宽度计算
     *
     * @param cellDataList 单元格数据列表
     * @param cell         当前单元格
     * @param head         表头信息
     * @param isHead       是否为表头
     * @return 字符宽度
     */
    private int computeContentCharWidth(List<WriteCellData<?>> cellDataList, Cell cell, Head head, Boolean isHead) {
        String cellValue = extractCellValue(cell, cellDataList, head, isHead);
        if (cellValue == null || cellValue.isEmpty()) {
            return 0;
        }

        int width = 0;
        for (int i = 0; i < cellValue.length(); i++) {
            char c = cellValue.charAt(i);
            if (isCjkCharacter(c)) {
                width += CHAR_WIDTH_CJK;
            } else {
                width += CHAR_WIDTH_NORMAL;
            }
        }
        return width;
    }

    /**
     * 提取单元格的文本值
     */
    private String extractCellValue(Cell cell, List<WriteCellData<?>> cellDataList, Head head, Boolean isHead) {
        if (isHead != null && isHead && head != null) {
            List<String> headNameList = head.getHeadNameList();
            if (headNameList != null && !headNameList.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String name : headNameList) {
                    sb.append(name);
                }
                return sb.toString();
            }
            return null;
        }

        if (cell != null) {
            try {
                String stringValue = cell.getStringCellValue();
                if (stringValue != null && !stringValue.isEmpty()) {
                    return stringValue;
                }
            } catch (Exception ignored) {
            }

            try {
                double numericValue = cell.getNumericCellValue();
                return String.valueOf(numericValue);
            } catch (Exception ignored) {
            }
        }

        if (cellDataList != null && !cellDataList.isEmpty()) {
            WriteCellData<?> writeCellData = cellDataList.get(0);
            if (writeCellData != null) {
                Object data = writeCellData.getData();
                if (data instanceof String) {
                    return (String) data;
                }
                if (data != null) {
                    return data.toString();
                }
                if (writeCellData.getStringValue() != null) {
                    return writeCellData.getStringValue();
                }
            }
        }

        return null;
    }

    /**
     * 判断是否为 CJK 字符（中日韩统一表意文字及相关符号）
     * 使用 Unicode 码点范围直接判断，避免 UnicodeBlock.of() 创建对象开销
     */
    private boolean isCjkCharacter(char c) {
        return (c >= 0x1100 && c <= 0x11FF)     // Hangul Jamo
                || (c >= 0x2E80 && c <= 0x2FDF)     // CJK Radicals Supplement, Kangxi Radicals
                || (c >= 0x3000 && c <= 0x312F)     // CJK Symbols and Punctuation, Bopomofo
                || (c >= 0x3130 && c <= 0x318F)     // Hangul Compatibility Jamo
                || (c >= 0x3190 && c <= 0x31BF)     // Kanbun, Bopomofo Extended
                || (c >= 0x31F0 && c <= 0x31FF)     // Katakana Phonetic Extensions
                || (c >= 0x3400 && c <= 0x4DBF)     // CJK Unified Ideographs Extension A
                || (c >= 0x4E00 && c <= 0x9FFF)     // CJK Unified Ideographs
                || (c >= 0xAC00 && c <= 0xD7AF)     // Hangul Syllables
                || (c >= 0xF900 && c <= 0xFAFF)     // CJK Compatibility Ideographs
                || (c >= 0xFE30 && c <= 0xFE4F)     // CJK Compatibility Forms
                || (c >= 0xFF00 && c <= 0xFFEF);    // Halfwidth and Fullwidth Forms
    }
}