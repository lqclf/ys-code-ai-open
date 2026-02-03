package com.eric.module.system.vo;

import lombok.Data;

import java.util.List;

/**
 *  用户集合to角色vo
 * @ClassName:  UserListRoleVo
 * @author:     liuQ 
 * @date:       2025-07-23 15:00:47
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Data
public class UserListRoleVo {

    private List<String> userIds;
    /**
     * 角色id
     */
    private String roleId;
}
