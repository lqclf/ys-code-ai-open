package com.eric.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eric.module.system.entity.SysMenu;
import com.eric.module.system.entity.SysUser;
import com.eric.module.system.vo.UserRoleVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户基础表 Mapper 接口
 * @ClassName:  SysUserMapper    
 * @author:     liuQ
 * @date:       2025-07-23 12:29:26
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 获取指定用户所有角色信息
     * @param userId
     * @return
     */
    List<UserRoleVo> getUserRoleList(String userId);

    /**
     * 获取所有用户的角色信息
     * @param userIds 用户ID列表
     * @return
     */
    List<UserRoleVo> getAllUsersRoleList(@Param("userIds") List<String> userIds);

    //获取指定用户菜单列表
    List<SysMenu> getUserMenuList(String userId);

    /**
     * 添加用户角色
     * @param userId
     * @param roleIds
     */
    void addUserRole(String userId, List<String> roleIds);
    /**
     * 删除用户所有角色
     * @param userId
     */
    void removeUserRole(String userId);

    /**
     * 批量添加用户到角色
     * @param roleId
     * @param userIds
     */
    void addRoleToUsers(String roleId, List<String> userIds);

    /**
     * 批量移除用户指定角色
     * @param roleId
     * @param userIds
     */
    void removeRoleUsers(String roleId, List<String> userIds);

    /**
     * 获取指定角色的所有用户ID列表
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    List<String> getUserIdsByRoleId(@Param("roleId") String roleId);
}

