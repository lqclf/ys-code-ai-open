package com.eric.module.system.vo;

import lombok.Data;

/**
 *  用户角色vo
 * @ClassName:  UserRoleVo     
 * @author:     liuQ 
 * @date:       2025-07-23 15:00:47
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Data
public class UserRoleVo {

    private String userId;
    /**
     * 角色id
     */
    private String roleId;
    /**
     * 角色编码
     */
    private String roleCode;
}
