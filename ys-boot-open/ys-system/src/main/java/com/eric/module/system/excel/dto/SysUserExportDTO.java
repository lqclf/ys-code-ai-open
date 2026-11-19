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
 * 用户导出专用DTO
 * @ClassName:   SysUserExportDTO
 * @author:      Eric
 * @date:        2026-05-18
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "SysUserExportDTO", description = "用户导出专用DTO")
public class SysUserExportDTO extends BaseExcelDTO {

    /**
     * 登录名
     */
    @ExcelProperty("登录名")
    @Schema(description = "登录名")
    private String loginName;

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
     * 部门名称
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

    /**
     * 最后登录ip
     */
    @ExcelProperty("最后登录IP")
    @Schema(description = "最后登录IP")
    private String loginIp;

    /**
     * 最后登录时间
     */
    @ExcelProperty("最后登录时间")
    @Schema(description = "最后登录时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;

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
