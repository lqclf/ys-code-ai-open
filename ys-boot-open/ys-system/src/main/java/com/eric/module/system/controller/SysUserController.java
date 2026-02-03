package com.eric.module.system.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.common.module.controller.BaseController;
import com.eric.common.module.domain.query.PageQuery;
import com.eric.common.response.ResponseResult;
import com.eric.module.system.dto.SysUserDTO;
import com.eric.module.system.entity.SysUser;
import com.eric.module.system.service.SysUserService;
import com.eric.module.system.vo.UserDeptVo;
import com.eric.module.system.vo.UserListRoleVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户基础表-控制层
 * @ClassName:   SysUserController
 * @author:      liuQ
 * @date:        2025-07-23 12:29:26
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController<SysUser, SysUserService> {
    /**
     * 分页查询
     * @param query
     * @return
     */
    @GetMapping("/list")
    //@DataPermission(tableName = "sys_user", departIdField = "depart_id", userIdField = "create_by")
    public ResponseResult<Page<SysUser>> list(SysUserDTO dto, PageQuery page) {
        return ResponseResult.success(service.pageList(dto, page));
    }
    /**
     * 添加
     * @param o
     * @return
     */
    @PostMapping("/add")
    //@SaCheckPermission("system:user:add")
    public ResponseResult<String> add(SysUserDTO o){
        return ResponseResult.success("新增成功！", service.add(o));
    }

    /**
     * 编辑
     * @param o
     * @return
     */
    @PutMapping("/update")
    //@SaCheckPermission("system:user:edit")
    public ResponseResult<Boolean> edit(SysUserDTO o){
        return ResponseResult.success(service.update(o));
    }
    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping("/delete/{ids}")
    //@SaCheckPermission("system:user:delete")
    public ResponseResult<Boolean> delete(@PathVariable List<String> ids){
        return ResponseResult.success(service.delete(ids));
    }
    
    /**
     * 添加用户到部门
     * @param deptId 部门ID
     * @param userIds 用户ID列表
     * @return 操作结果
     */
    @PostMapping("/assignUsersToDept")
    //@SaCheckPermission("system:user:edit")
    public ResponseResult<Boolean> assignUsersToDept(@RequestBody UserDeptVo vo) {
        return ResponseResult.success(service.assignUsersToDept(vo.getDeptId(), vo.getUserIds()));
    }
    
    /**
     * 从部门中移除用户
     * @param deptId 部门ID
     * @param userIds 用户ID列表
     * @return 操作结果
     */
    @PostMapping("/removeUsersFromDept")
    //@SaCheckPermission("system:user:edit")
    public ResponseResult<Boolean> removeUsersFromDept(@RequestBody UserDeptVo vo) {
        return ResponseResult.success(service.removeUsersFromDept(vo.getDeptId(), vo.getUserIds()));
    }
    /**
     * 添加用户到角色
     * @param roleId 角色ID
     * @param userIds 用户ID列表
     * @return 操作结果
     */
    @PostMapping("/addRoleUsers")
    //@SaCheckPermission("system:user:edit")
    public ResponseResult<Boolean> addRoleUsers(@RequestBody UserListRoleVo vo) {
        return ResponseResult.success(service.addRoleUsers(vo.getRoleId(), vo.getUserIds()));
    }

    @PostMapping("/removeRoleUsers")
    //@SaCheckPermission("system:user:edit")
    public ResponseResult<Boolean> removeRoleUsers(@RequestBody UserListRoleVo vo) {
        return ResponseResult.success(service.removeRoleUsers(vo.getRoleId(), vo.getUserIds()));
    }

    /**
     * 获取对象信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult<SysUserDTO> get(@PathVariable("id") String id) {
        return ResponseResult.success(service.get(id));
    }
    
    /**
     * 冻结用户
     * @param userId 用户ID
     * @return 操作结果
     */
    @PutMapping("/freeze/{userId}")
    //@SaCheckPermission("system:user:edit")
    public ResponseResult<Boolean> freezeUser(@PathVariable String userId) {
        return ResponseResult.success(service.freezeOrUnUser(userId, 0));
    }
    
    /**
     * 解冻用户
     * @param userId 用户ID
     * @return 操作结果
     */
    @PutMapping("/unfreeze/{userId}")
    //@SaCheckPermission("system:user:edit")
    public ResponseResult<Boolean> unfreezeUser(@PathVariable String userId) {
        return ResponseResult.success(service.freezeOrUnUser(userId, 1));
    }
    
    /**
     * 修改用户密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 操作结果
     */
    @PutMapping("/changePassword")
    //@SaCheckPermission("system:user:edit")
    public ResponseResult<Boolean> changePassword(@RequestParam String userId, 
                                                  @RequestParam String oldPassword, 
                                                  @RequestParam String newPassword) {
        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
            return ResponseResult.fail("密码不能为空");
        }
        if (oldPassword.equals(newPassword)) {
            return ResponseResult.fail("新密码不能与旧密码相同");
        }
        return ResponseResult.success(service.changePassword(userId, oldPassword, newPassword));
    }
    
    /**
     * 重置用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 操作结果
     */
    @PutMapping("/resetPassword")
    //@SaCheckPermission("system:user:edit")
    public ResponseResult<Boolean> resetPassword(@RequestParam String userId, 
                                                 @RequestParam String newPassword) {
        if (StringUtils.isBlank(newPassword)) {
            return ResponseResult.fail("密码不能为空");
        }
        return ResponseResult.success(service.resetPassword(userId, newPassword));
    }

    /**
     * 根据用户id集合获取所有用户
     */
    @GetMapping("/getUsersByIds")
    public ResponseResult<List<SysUser>> getUsersByIds(@RequestParam List<String> userIds) {
        return ResponseResult.success(service.getUsersByIds(userIds));
    }
}