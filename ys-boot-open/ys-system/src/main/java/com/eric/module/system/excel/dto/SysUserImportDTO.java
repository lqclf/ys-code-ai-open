package com.eric.module.system.excel.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.eric.excel.module.dto.BaseExcelDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户导入专用DTO
 * 所有字段使用String类型，避免EasyExcel类型转换异常
 * @ClassName:   SysUserImportDTO
 * @author:      Eric
 * @date:        2026-05-18
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "SysUserImportDTO", description = "用户导入专用DTO")
public class SysUserImportDTO extends BaseExcelDTO {

    /**
     * 登录名
     */
    @ExcelProperty("登录名")
    @Schema(description = "登录名")
    private String loginName;

    /**
     * 密码
     */
    @ExcelProperty("密码")
    @Schema(description = "密码")
    private String password;

    /**
     * 真实姓名
     */
    @ExcelProperty("真实姓名")
    @Schema(description = "真实姓名")
    private String userName;

    /**
     * 编号
     */
    @ExcelProperty("编号")
    @Schema(description = "编号")
    private String code;

    /**
     * 所属组织机构名称
     */
    @ExcelProperty("部门名称")
    @Schema(description = "部门名称")
    private String departName;

    /**
     * 岗位名称
     */
    @ExcelProperty("岗位")
    @Schema(description = "岗位")
    private String positionName;

    /**
     * 用户类型（文本）
     */
    @ExcelProperty("用户类型")
    @Schema(description = "用户类型")
    private String userTypeStr;

    /**
     * 手机号码
     */
    @ExcelProperty("手机号码")
    @Schema(description = "手机号码")
    private String phone;

    /**
     * 邮箱
     */
    @ExcelProperty("邮箱")
    @Schema(description = "邮箱")
    private String email;

    /**
     * 第三方类型
     */
    @ExcelProperty("第三方类型")
    @Schema(description = "第三方类型")
    private String thirdType;

    /**
     * 第三方登录账号
     */
    @ExcelProperty("第三方登录账号")
    @Schema(description = "第三方登录账号")
    private String thirdAccount;

    /**
     * 备注
     */
    @ExcelProperty("备注")
    @Schema(description = "备注")
    private String remark;

    /**
     * 状态（文本）
     */
    @ExcelProperty("状态")
    @Schema(description = "状态")
    private String statusStr;

    /**
     * 锁定状态（文本）
     */
    @ExcelProperty("锁定状态")
    @Schema(description = "锁定状态")
    private String isLockStr;
}
