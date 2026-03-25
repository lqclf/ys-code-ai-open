package com.eric.config.satoken;

import cn.dev33.satoken.stp.StpInterface;
import com.eric.module.system.service.SysUserService;
import com.eric.module.system.vo.UserRoleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * @ClassName:  StpInterfaceImpl
 * @author:     Eric
 * @date:       2025-08-03 10:14:32
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private SysUserService userService;


    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return  userService.getUserPermissionList(String.valueOf(loginId));
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<UserRoleVo> roles = userService.getUserRoleList(String.valueOf(loginId));
        return roles.stream().map(UserRoleVo::getRoleCode).toList();
    }
}
