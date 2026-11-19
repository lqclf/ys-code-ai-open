package com.eric.excel.module.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 导入辅助字段dto实体
 * 所有辅助字段使用 @ExcelIgnore 注解，避免 EasyExcel 尝试从 Excel 读取这些字段
 * @ClassName:  BaseExcelDTO
 * @author:     liuQ
 * @date:       2025-06-16 14:52:21
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Data
public class BaseExcelDTO implements Serializable {

    // ==================== 导入辅助字段 ====================

    /**
     * 行号（用于导入时标记错误位置）
     */
    @ExcelIgnore
    @Schema(description = "行号")
    private Integer rowNum;

    /**
     * 错误信息（用于标记导入错误）
     */
    @ExcelIgnore
    @Schema(description = "错误信息")
    private String errorMsg;

    /**
     * 是否有效（用于导入预览）
     */
    @ExcelIgnore
    @Schema(description = "是否有效")
    private Boolean valid;

    /**
     * 导出时选中的字段列表
     */
    @ExcelIgnore
    @Schema(description = "导出时选中的字段列表")
    private List<String> selectedFields;
}
