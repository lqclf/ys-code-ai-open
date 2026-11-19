package com.eric.excel.strategy;

import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.handler.context.RowWriteHandlerContext;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.eric.excel.config.WriteConfig;
import com.eric.excel.utils.StyleBuilder;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.StringUtils;

/**
 * 序号列样式策略
 * 继承HorizontalCellStyleStrategy并组合RowWriteHandler，确保序号列与其他列样式完全一致
 *
 * <p>实现原理：
 * 1. RowWriteHandler在afterRowDispose中处理每一行，将原有单元格右移，在第一列插入序号单元格
 * 2. 手动为序号列设置样式（使用与HorizontalCellStyleStrategy相同的样式）
 * 3. 样式一致性：序号列的样式与其他列完全一致
 *
 * <p>功能说明：
 * 1. 在每一行的第一列插入序号列
 * 2. 表头行显示"序号"标题
 * 3. 数据行显示行号（从1开始）
 * 4. 样式与其他列完全一致
 *
 * @ClassName: SeqColumnStyleStrategy
 * @author: liuQ
 * @date: 2025-02-28
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
public class SeqColumnStyleStrategy extends HorizontalCellStyleStrategy implements RowWriteHandler {

    private final String seqColumnTitle;
    private final WriteCellStyle headWriteCellStyle;
    private final WriteCellStyle contentWriteCellStyle;

    private CellStyle cachedHeadCellStyle;
    private CellStyle cachedContentCellStyle;

    private static final int SEQ_COLUMN_WIDTH = 2500;

    /**
     * 构造函数
     *
     * @param config 写入配置
     */
    public SeqColumnStyleStrategy(WriteConfig config) {
        super(StyleBuilder.buildHeadWriteCellStyle(config), StyleBuilder.buildContentWriteCellStyle(config));
        this.seqColumnTitle = StringUtils.hasText(config.getSeqColumnTitle()) ? config.getSeqColumnTitle() : "序号";
        this.headWriteCellStyle = StyleBuilder.buildHeadWriteCellStyle(config);
        this.contentWriteCellStyle = StyleBuilder.buildContentWriteCellStyle(config);
    }

    @Override
    public void afterRowDispose(RowWriteHandlerContext context) {
        if (context.getRow() == null) {
            return;
        }

        Row row = context.getRow();

        // 仅支持POI XSSF/XLS Row，CSV等非POI格式（如CsvRow）不支持单元格移位操作
        if (!(row instanceof org.apache.poi.xssf.usermodel.XSSFRow)
                && !(row instanceof org.apache.poi.hssf.usermodel.HSSFRow)) {
            return;
        }

        Sheet sheet = row.getSheet();
        Workbook workbook = sheet.getWorkbook();
        boolean isHead = Boolean.TRUE.equals(context.getHead());

        // 将行的所有单元格向右移动一列
        shiftRowCellsRight(row);

        // 在第一列创建序号单元格
        String seqValue = isHead ? seqColumnTitle : String.valueOf(row.getRowNum());
        Cell seqCell = row.createCell(0);
        seqCell.setCellValue(seqValue);

        // 为序号列设置样式（与其他列保持一致）
        CellStyle cellStyle = isHead ? getOrCreateHeadCellStyle(workbook) : getOrCreateContentCellStyle(workbook);
        seqCell.setCellStyle(cellStyle);

        // 设置列宽
        sheet.setColumnWidth(0, SEQ_COLUMN_WIDTH);
    }

    /**
     * 获取或创建表头单元格样式
     */
    private CellStyle getOrCreateHeadCellStyle(Workbook workbook) {
        if (cachedHeadCellStyle == null) {
            cachedHeadCellStyle = createCellStyle(workbook, headWriteCellStyle);
        }
        return cachedHeadCellStyle;
    }

    /**
     * 获取或创建内容单元格样式
     * @param workbook 工作簿
     * @return 内容CellStyle
     */
    private CellStyle getOrCreateContentCellStyle(Workbook workbook) {
        if (cachedContentCellStyle == null) {
            cachedContentCellStyle = createCellStyle(workbook, contentWriteCellStyle);
        }
        return cachedContentCellStyle;
    }

    /**
     * 根据WriteCellStyle创建POI CellStyle
     */
    private CellStyle createCellStyle(Workbook workbook, WriteCellStyle writeCellStyle) {
        CellStyle cellStyle = workbook.createCellStyle();
        
        // 设置字体
        if (writeCellStyle.getWriteFont() != null) {
            org.apache.poi.ss.usermodel.Font poiFont = createFont(workbook, writeCellStyle.getWriteFont());
            cellStyle.setFont(poiFont);
        }
        
        // 设置水平对齐
        if (writeCellStyle.getHorizontalAlignment() != null) {
            cellStyle.setAlignment(writeCellStyle.getHorizontalAlignment());
        }
        
        // 设置垂直对齐
        if (writeCellStyle.getVerticalAlignment() != null) {
            cellStyle.setVerticalAlignment(writeCellStyle.getVerticalAlignment());
        }
        
        // 设置边框
        if (writeCellStyle.getBorderLeft() != null) {
            cellStyle.setBorderLeft(writeCellStyle.getBorderLeft());
        }
        if (writeCellStyle.getBorderRight() != null) {
            cellStyle.setBorderRight(writeCellStyle.getBorderRight());
        }
        if (writeCellStyle.getBorderTop() != null) {
            cellStyle.setBorderTop(writeCellStyle.getBorderTop());
        }
        if (writeCellStyle.getBorderBottom() != null) {
            cellStyle.setBorderBottom(writeCellStyle.getBorderBottom());
        }
        
        // 设置边框颜色
        if (writeCellStyle.getLeftBorderColor() != null) {
            cellStyle.setLeftBorderColor(writeCellStyle.getLeftBorderColor());
        }
        if (writeCellStyle.getRightBorderColor() != null) {
            cellStyle.setRightBorderColor(writeCellStyle.getRightBorderColor());
        }
        if (writeCellStyle.getTopBorderColor() != null) {
            cellStyle.setTopBorderColor(writeCellStyle.getTopBorderColor());
        }
        if (writeCellStyle.getBottomBorderColor() != null) {
            cellStyle.setBottomBorderColor(writeCellStyle.getBottomBorderColor());
        }
        
        // 设置背景色
        if (writeCellStyle.getFillForegroundColor() != null) {
            cellStyle.setFillForegroundColor(writeCellStyle.getFillForegroundColor());
            cellStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        }
        
        // 设置自动换行
        if (writeCellStyle.getWrapped() != null) {
            cellStyle.setWrapText(writeCellStyle.getWrapped());
        }
        
        // 设置旋转角度
        if (writeCellStyle.getRotation() != null) {
            cellStyle.setRotation(writeCellStyle.getRotation());
        }
        
        return cellStyle;
    }
    
    /**
     * 创建POI字体
     */
    private org.apache.poi.ss.usermodel.Font createFont(Workbook workbook, com.alibaba.excel.write.metadata.style.WriteFont writeFont) {
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        
        if (writeFont.getFontName() != null) {
            font.setFontName(writeFont.getFontName());
        }
        if (writeFont.getFontHeightInPoints() != null) {
            font.setFontHeightInPoints(writeFont.getFontHeightInPoints());
        }
        if (writeFont.getBold() != null) {
            font.setBold(writeFont.getBold());
        }
        if (writeFont.getColor() != null) {
            font.setColor(writeFont.getColor());
        }
        
        return font;
    }

    /**
     * 将行的所有单元格向右移动一列
     */
    private void shiftRowCellsRight(Row row) {
        short lastCellNum = row.getLastCellNum();
        if (lastCellNum <= 0) {
            return;
        }

        for (int i = lastCellNum - 1; i >= 0; i--) {
            Cell oldCell = row.getCell(i);
            if (oldCell != null) {
                Cell newCell = row.createCell(i + 1);
                copyCell(oldCell, newCell);
                row.removeCell(oldCell);
            }
        }
    }

    /**
     * 复制单元格内容（包括样式和值）
     * @param source 源单元格
     * @param target 目标单元格
     */
    private void copyCell(Cell source, Cell target) {
        target.setCellStyle(source.getCellStyle());
        switch (source.getCellType()) {
            case STRING:
                target.setCellValue(source.getStringCellValue());
                break;
            case NUMERIC:
                target.setCellValue(source.getNumericCellValue());
                break;
            case BOOLEAN:
                target.setCellValue(source.getBooleanCellValue());
                break;
            case FORMULA:
                target.setCellFormula(source.getCellFormula());
                break;
            case BLANK:
                target.setBlank();
                break;
            default:
                break;
        }
    }
}
