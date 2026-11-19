package com.eric.module.system.excel.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.eric.excel.module.dto.BaseExcelDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门导入专用DTO
 * 所有字段使用String类型，避免EasyExcel类型转换异常
 * @ClassName:   SysDepartImportDTO
 * @author:      Eric
 * @date:        2026-05-18
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "SysDepartImportDTO", description = "部门导入专用DTO")
public class SysDepartImportDTO extends BaseExcelDTO {

    /**
     * 部门名称
     */
    @ExcelProperty("部门名称")
    @Schema(description = "部门名称")
    private String name;

    /**
     * 部门简称
     */
    @ExcelProperty("部门简称")
    @Schema(description = "部门简称")
    private String shortName;

    /**
     * 部门编码
     */
    @ExcelProperty("部门编码")
    @Schema(description = "部门编码")
    private String code;

    /**
     * 上级部门名称
     */
    @ExcelProperty("上级部门")
    @Schema(description = "上级部门")
    private String parentName;

    /**
     * 部门分类（文本）
     */
    @ExcelProperty("部门分类")
    @Schema(description = "部门分类")
    private String categoryStr;

    /**
     * 部门性质（文本）
     */
    @ExcelProperty("部门性质")
    @Schema(description = "部门性质")
    private String deptTypeStr;

    /**
     * 部门层级（文本）
     */
    @ExcelProperty("部门层级")
    @Schema(description = "部门层级")
    private String levelTypeStr;

    /**
     * 所属地区
     */
    @ExcelProperty("所属地区")
    @Schema(description = "所属地区")
    private String area;

    /**
     * 部门邮箱
     */
    @ExcelProperty("部门邮箱")
    @Schema(description = "部门邮箱")
    private String email;

    /**
     * 邮政编码
     */
    @ExcelProperty("邮政编码")
    @Schema(description = "邮政编码")
    private String zipCode;

    /**
     * 详细地址
     */
    @ExcelProperty("详细地址")
    @Schema(description = "详细地址")
    private String address;

    /**
     * 传真号码
     */
    @ExcelProperty("传真号码")
    @Schema(description = "传真号码")
    private String fax;

    /**
     * 备注
     */
    @ExcelProperty("备注")
    @Schema(description = "备注")
    private String remark;

    /**
     * 联系人
     */
    @ExcelProperty("联系人")
    @Schema(description = "联系人")
    private String linkMan;

    /**
     * 联系电话
     */
    @ExcelProperty("联系电话")
    @Schema(description = "联系电话")
    private String linkPhone;

    /**
     * 部门负责人名称
     */
    @ExcelProperty("部门负责人")
    @Schema(description = "部门负责人")
    private String leaderName;

    /**
     * 显示顺序
     */
    @ExcelProperty("显示顺序")
    @Schema(description = "显示顺序")
    private String seq;

    /**
     * 图标
     */
    @ExcelProperty("图标")
    @Schema(description = "图标")
    private String icon;

    /**
     * 状态（文本）
     */
    @ExcelProperty("状态")
    @Schema(description = "状态")
    private String statusStr;
}
