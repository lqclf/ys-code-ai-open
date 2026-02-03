package com.eric.module.system.vo;

import lombok.Data;

import java.util.List;

/**
 * 修改角色权限Vo
 * @ClassName:   UpdateRoleMenuVo
 * @author:  liuQ 
 * @date:    2025-08-13 18:36:36
 * @Copyright ERIC 微信公众号：Eric技术杂货库
 */
@Data
public class UpdateRoleMenuVO {

    public String id;

    public List<String> roleIds;
}