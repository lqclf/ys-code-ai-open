package com.eric.module.system.dto;

import com.eric.common.module.domain.BaseEntityDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户基础表-DTO层
 * @ClassName:   SysUserDTO
 * @author:      liuQ
 * @date:        2025-07-23 12:29:26
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUserDTO extends BaseEntityDTO {

     /**
     * 登录名
     */
	private String loginName;
     /**
     * 密码
     */
	private String password;
    /**
     * 密码加密密钥
     */
    private String hexKey;
     /**
     * 真实姓名
     */
	private String userName;
     /**
     * 编号
     */
	private String code;
     /**
     * 所属组织机构id
     */
	private String departId ;
     /**
     * 所属组织机构名称
     */
	private String departName;
     /**
     * 用户类型（0：游客，1：普通用户 2：会员，3：管理员（中后台））
     */
	private Integer userType;
     /**
     * 手机号码
     */
	private String phone;
     /**
     * 邮箱
     */
	private String email;
     /**
     * 第三方类型（字典）
     */
	private Integer thirdType;
     /**
     * 第三方登录账号
     */
	private String thirdAccount;
     /**
     * 备注
     */
	private String remark;
     /**
     * 状态 0.禁用 1.正常
     */
	private Integer status;
     /**
     * 锁定状态 0.已锁定 1.正常
     */
	private Integer isLock;
     /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;
     /**
     * 更新人id
     */
	private String updateBy;
     /**
     * 更新人名称
     */
	private String updateName;
     /**
     * 最后登录ip
     */
	private String loginIp;
     /**
     * 最后登录时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime loginTime;
     /**
     * 密码最后更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime passwordUpdateTime;

    /**
     * 排除部门id
     */
    private String excludeDeptId;

    /**
     * 排除角色id
     */
    private String excludeRoleId;

    /**
     * 角色id（用于查询过滤）
     */
    private String roleId;
    
    /**
     * 角色id集合
     */
    private List<String> roleIds;

}
