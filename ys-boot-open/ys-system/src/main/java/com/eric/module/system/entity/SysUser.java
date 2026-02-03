package com.eric.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eric.common.module.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户基础表-实体映射类
 * @ClassName:   SysUser
 * @author:      liuQ
 * @date:        2025-07-23 12:29:26
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@TableName("sys_user")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUser extends BaseEntity {

    /**
     * 登录名
     */
    @TableField("login_name")
    private String loginName;
    /**
     * 密码
     */
    @TableField("password")
    private String password;
    /**
     * 密码加密密钥
     */
    @TableField("hex_key")
    private String hexKey;
    /**
     * 真实姓名
     */
    @TableField("user_name")
    private String userName;
    /**
     * 编号
     */
    @TableField("code")
    private String code;
    /**
     * 所属组织机构id
     */
    @TableField("depart_id")
    private String departId;
    /**
     * 所属组织机构名称
     */
    @TableField("depart_name")
    private String departName;
    /**
     * 用户类型（0：游客，1：普通用户 2：会员，3：管理员（中后台））
     */
    @TableField("user_type")
    private Integer userType;
    /**
     * 手机号码
     */
    @TableField("phone")
    private String phone;
    /**
     * 邮箱
     */
    @TableField("email")
    private String email;
    /**
     * 第三方类型（字典）
     */
    @TableField("third_type")
    private Integer thirdType;
    /**
     * 第三方登录账号
     */
    @TableField("third_account")
    private String thirdAccount;
    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
    /**
     * 状态 0.禁用 1.正常
     */
    @TableField("status")
    private Integer status;
    /**
     * 锁定状态 0.已锁定 1.正常
     */
    @TableField("is_lock")
    private Integer isLock;
    /**
     * 更新时间
     */
    @TableField("update_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    /**
     * 更新人id
     */
    @TableField("update_by")
    private String updateBy;
    /**
     * 更新人名称
     */
    @TableField("update_name")
    private String updateName;
    /**
     * 最后登录ip
     */
    @TableField("login_ip")
    private String loginIp;
    /**
     * 最后登录时间
     */
    @TableField("login_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;
    /**
     * 密码最后更新时间
     */
    @TableField("password_update_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime passwordUpdateTime;


    @TableField(exist = false)
    private List<SysRole> roleList;
}