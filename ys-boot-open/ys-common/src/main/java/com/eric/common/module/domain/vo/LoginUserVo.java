package com.eric.common.module.domain.vo;


import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 登录用户信息
 * @ClassName:  LoginUserVo     
 * @author:     liuQ 
 * @date:       2025-07-23 09:58:32
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Data
public class LoginUserVo {

    private String id;

    private String loginName;

    private String userName;

    private String nickname;

    private String code;

    private String orgId;

    private String orgName;

    private Integer userType;

    private String phone;

    private String email;

    private Integer thirdType;

    private String thirdAccount;

    private String remark;

    private Integer status;

    private Integer isLock;

    private String token;

    private List<String> permissions;

    private Set<String> roles;
}
