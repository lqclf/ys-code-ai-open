package com.eric.module.system.excel.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.eric.excel.module.dto.BaseExcelDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色导入专用DTO
 * 所有字段使用String类型，避免EasyExcel类型转换异常
 * @ClassName:   SysRoleImportDTO
 * @author:      Eric
 * @date:        2026-05-18
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "SysRoleImportDTO", description = "角色导入专用DTO")
public class SysRoleImportDTO extends BaseExcelDTO {

    /**
     * 角色编码
     */
    @ExcelProperty("角色编码")
    @Schema(description = "角色编码")
    private String code;

    /**
     * 角色名称
     */
    @ExcelProperty("角色名称")
    @Schema(description = "角色名称")
    private String name;

    /**
     * 备注
     */
    @ExcelProperty("备注")
    @Schema(description = "备注")
    private String remark;

    /**
     * 排序
     */
    @ExcelProperty("排序")
    @Schema(description = "排序")
    private String seq;

    /**
     * 状态（文本）
     */
    @ExcelProperty("状态")
    @Schema(description = "状态")
    private String statusStr;
}
