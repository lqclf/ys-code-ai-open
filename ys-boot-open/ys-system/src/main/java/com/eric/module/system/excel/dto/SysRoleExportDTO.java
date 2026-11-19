package com.eric.module.system.excel.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.eric.excel.module.dto.BaseExcelDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 角色导出专用DTO
 * @ClassName:   SysRoleExportDTO
 * @author:      Eric
 * @date:        2026-05-18
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "SysRoleExportDTO", description = "角色导出专用DTO")
public class SysRoleExportDTO extends BaseExcelDTO {

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
    private Integer seq;

    /**
     * 状态（文本）
     */
    @ExcelProperty("状态")
    @Schema(description = "状态")
    private String statusStr;

    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    @Schema(description = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 创建人名称
     */
    @ExcelProperty("创建人")
    @Schema(description = "创建人")
    private String createName;
}
