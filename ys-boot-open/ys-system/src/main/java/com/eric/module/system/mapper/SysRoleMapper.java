package com.eric.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eric.module.system.entity.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色信息表 Mapper 接口
 * @ClassName:  SysRoleMapper    
 * @author:     liuQ
 * @date:       2025-07-23 14:38:08
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 获取角色权限id集合
     * @param roleId
     * @return
     */
    List<String> getRoleMenusIds(String roleId);

    /**
     * 根据角色id删除菜单
     * @param ids
     */
    void deleteMenuByRoleId(@Param("ids") List<String> ids);

    /**
     * 根据角色id添加菜单
     * @param id
     * @param menuIds
     */
    void insertRoleMenus(@Param("id") String id, @Param("menuIds") List<String> menuIds);
}

