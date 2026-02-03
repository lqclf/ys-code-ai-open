package com.eric.module.system.service;

import com.eric.common.module.service.BaseService;
import com.eric.module.system.dto.SysRoleDTO;
import com.eric.module.system.entity.SysRole;

import java.util.List;

/**
 * 角色信息表 服务类
 * @ClassName:  SysRoleService     
 * @author:     liuQ
 * @date:       2025-07-23 14:38:08
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
public interface SysRoleService extends BaseService<SysRoleDTO, SysRole> {

    /**
     * 获取角色权限id集合
     * @param roleId
     * @return
     */
    List<String> getRoleMenusIds(String roleId);

    /**
     * 更新角色权限
     * @param id
     * @param menuIds
     * @return
     */
    Boolean updateRoleMenus(String id, List<String> menuIds);

    /**
     * 获取角色列表
     * @return
     */
    List<SysRole> getRoleList();
}
