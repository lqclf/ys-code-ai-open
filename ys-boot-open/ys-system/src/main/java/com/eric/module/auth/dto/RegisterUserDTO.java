package com.eric.module.auth.dto;

import lombok.Data;

/**
 *  注册用户信息
 * @ClassName:  RegisterUserDTO     
 * @author:     liuQ 
 * @date:       2025-07-28 14:36:30
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Data
public class RegisterUserDTO {

    /**
     * 验证码
     */
    private String smscode;

    /**
     * 登录名
     */
    private String loginName;
    /**
     * 密码
     */
    private String password;
    /**
     * 确认密码
     */
    private String realPassword;
    /**
     * 用户类型（0：游客，1：普通用户 2：会员，3：管理员（中后台））
     */
    private Integer userType;
    /**
     * 真实姓名
     */
    private String userName;
    /**
     * 手机号码
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
}
