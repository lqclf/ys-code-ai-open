package com.eric.module.system.service;

import com.eric.common.module.service.BaseService;
import com.eric.module.system.dto.SysUserDTO;
import com.eric.module.system.entity.SysUser;
import com.eric.module.system.vo.UserRoleVo;

import java.util.List;

/**
 * 用户基础表 服务类
 * @ClassName:  SysUserService     
 * @author:     liuQ
 * @date:       2025-07-23 12:29:26
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
public interface SysUserService extends BaseService<SysUserDTO, SysUser> {

    /**
     * 获取用户角色列表
     * @param userId
     * @return
     */
    List<UserRoleVo> getUserRoleList(String userId);

    /**
     * 获取用户权限列表
     * @param userId
     * @return
     */
    List<String> getUserPermissionList(String userId);

    /**
     * 根据用户名获取用户信息
     * @param loginName
     * @return
     */
    SysUser getUserByLoginName(String loginName);

    /**
     * 根据手机号码获取用户信息
     * @param phone
     * @return
     */
    SysUser getUserByPhone(String phone);
    /**
     * 根据邮箱获取用户信息
     * @param phone
     * @return
     */
    SysUser getUserByEmail(String email);
    
    /**
     * 将用户分配到指定部门
     * @param deptId 部门ID
     * @param userIds 用户ID列表
     * @return 是否分配成功
     */
    Boolean assignUsersToDept(String deptId, List<String> userIds);
    
    /**
     * 将用户从指定部门移除
     * @param deptId 部门ID
     * @param userIds 用户ID列表
     * @return 是否移除成功
     */
    Boolean removeUsersFromDept(String deptId, List<String> userIds);

    /**
     * 冻结/解冻用户
     * @param userId 用户ID
     * @param isLock  0表示冻结 1表示正常
     * @return 是否冻结成功
     */
    Boolean freezeOrUnUser(String userId, Integer isLock);

    /**
     * 修改用户密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    Boolean changePassword(String userId, String oldPassword, String newPassword);

    /**
     * 重置用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 是否重置成功
     */
    Boolean resetPassword(String userId, String newPassword);

    /**
     * 根据用户id集合获取所有用户
     */
    List<SysUser> getUsersByIds(List<String> userIds);

    /**
     * 批量分配角色
     * @param roleId
     * @param userIds
     * @return
     */
    Boolean addRoleUsers(String roleId, List<String> userIds);
    /**
     * 批量移除角色
     * @param roleId
     * @param userIds
     * @return
     */
    Boolean removeRoleUsers(String roleId, List<String> userIds);

    /**
     * 获取所有用户用于系统校验（忽略数据权限）
     * 
     * 使用场景：需要获取表中全部数据进行系统校验
     * 
     * @return 所有用户列表
     */
    List<SysUser> getAllUsers();
}