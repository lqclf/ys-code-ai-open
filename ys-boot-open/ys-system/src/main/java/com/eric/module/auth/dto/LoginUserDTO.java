package com.eric.module.auth.dto;

import lombok.Data;

/**
 *  登录表单
 * @ClassName:  LoginUserDTO     
 * @author:     liuQ 
 * @date:       2025-07-24 15:42:55
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Data
public class LoginUserDTO {
    /**
     * 登录名
     */
    private String loginName;
    /**
     * 密码
     */
    private String password;
    /**
     * 手机号码
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 登录类型 登录名、手机号、邮箱、二维码等等
     */
    private Integer type;
    /**
     * 验证码
     */
    private String captcha;

    /**
     * 记住我 true/false
     */
    private Boolean remember;
}
