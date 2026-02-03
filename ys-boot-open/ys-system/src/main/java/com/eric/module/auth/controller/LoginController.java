package com.eric.module.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eric.common.constant.GlobalConstant;
import com.eric.common.enums.common.IsDeleteEnum;
import com.eric.common.enums.common.LoginTypeEnum;
import com.eric.common.enums.common.StatusEnum;
import com.eric.common.enums.common.UserTypeEnum;
import com.eric.common.module.domain.vo.LoginUserVo;
import com.eric.common.response.ResponseResult;
import com.eric.common.utils.BeanCopyUtils;
import com.eric.common.utils.IpUtils;
import com.eric.module.auth.dto.LoginUserDTO;
import com.eric.module.auth.dto.RegisterUserDTO;
import com.eric.module.system.dto.SysUserDTO;
import com.eric.module.system.entity.SysUser;
import com.eric.module.system.service.SysUserService;
import com.eric.module.system.vo.UserRoleVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @ClassName:  LoginController     
 * @author:     liuQ 
 * @date:       2025-07-24 15:38:56
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@RestController
@RequestMapping("/auth")
@Slf4j
public class LoginController {
    @Autowired
    private SysUserService userService;
    /**
     * 博客前台web页面登录
     * @param o
     * @return
     */
    @PostMapping("/login")
    public ResponseResult<LoginUserVo> login(@RequestBody LoginUserDTO o){
        if (o.getType() == null) {
            return ResponseResult.fail("登录类型不能为空");
        }
        //【1】 校验用户是否存在且有效
        LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<>();
        if (LoginTypeEnum.USER_LOGIN.getKey().equals(o.getType())) {    // 账号密码登录
            query.eq(SysUser::getLoginName, o.getLoginName());
        } else if(LoginTypeEnum.USER_PHONE.getKey().equals(o.getType())){
            query.eq(SysUser::getPhone, o.getPhone());
        } else if(LoginTypeEnum.USER_EMAIL.getKey().equals(o.getType())){
            query.eq(SysUser::getEmail, o.getEmail());
        } else {
            //......
        }
        query.eq(SysUser::getIsDelete, IsDeleteEnum.NOT_DELETED.getKey());
        SysUser sysUser = userService.getOne(query);

        if(sysUser == null) {
            return ResponseResult.fail("用户登录失败，用户不存在！");
        }
        if(sysUser.getIsLock() == 0){
            return ResponseResult.fail("账号已被锁定,请联系管理员!");
        }
        if(sysUser.getStatus() == 0){
            return ResponseResult.fail("账号已被禁用,请联系管理员!");
        }

        // 【2】校验用户密码
        if (!sysUser.getPassword().equals(o.getPassword())) {
            return ResponseResult.fail("用户名或密码错误！");
        }

        //【3】生成token
        // 执行登录
        Boolean remember = o.getRemember();
        StpUtil.login(sysUser.getId(), remember != null && remember);
        String token = StpUtil.getTokenValue();

        LoginUserVo loginUser = BeanCopyUtils.copyObj(sysUser, LoginUserVo.class);
        loginUser.setToken(token);
        // 批量获取角色和权限信息
        List<UserRoleVo> roles = userService.getUserRoleList(loginUser.getId());
        //获取roles中roleCode属性集合
        Set<String> roleCodes = roles.stream().map(UserRoleVo::getRoleCode).collect(Collectors.toSet());
        loginUser.setRoles(roleCodes);
        List<String> permissions = userService.getUserPermissionList(loginUser.getId());
        loginUser.setPermissions(permissions);
        StpUtil.getSession().set(GlobalConstant.SESSION_USERINFO, loginUser);

        //更新用户表最后登录时间
        sysUser.setLoginTime(LocalDateTime.now());
        sysUser.setLoginIp(IpUtils.getIp());
        userService.updateById(sysUser);
        return ResponseResult.success("登录成功！", loginUser);
    }


    /**
     * 退出登录
     * @param request
     * @param response
     * @return
     */
    @GetMapping(value = "/logout")
    public ResponseResult<Object> logout() {
        String userId = StpUtil.getLoginIdAsString();
        SysUser sysUser = userService.getById(userId);

        if(sysUser!=null) {
            log.info("用户:  {},退出成功！ ", sysUser.getLoginName());
            //调用shiro的logout
            StpUtil.logout();
            return ResponseResult.success("退出登录成功！");
        }else {
            return ResponseResult.fail("Token无效!");
        }
    }

    /**
     *  用户注册
     * @param o
     * @return
     */
    @PostMapping("/regester")
    public ResponseResult<String> regester(RegisterUserDTO o){
        //@TODO 验证码校验

        // 判断注册用户的账号是否已存在
        if(StringUtils.isNotBlank(o.getLoginName())) {
            SysUser user = userService.getUserByLoginName(o.getLoginName());
            if (user != null) {
                return ResponseResult.fail("用户已注册！");
            }
        }

        if(StringUtils.isNotBlank(o.getPhone())) {
            SysUser user = userService.getUserByPhone(o.getPhone());
            if (user != null) {
                return ResponseResult.fail("手机号已注册！");
            }
        }

        if(StringUtils.isNotBlank(o.getEmail())) {
            SysUser user = userService.getUserByEmail(o.getEmail());
            if (user != null) {
                return ResponseResult.fail("邮箱已注册！");
            }
        }

        SysUserDTO userDTO = BeanCopyUtils.copyObj(o, SysUserDTO.class);
        String password = o.getRealPassword();
        userDTO.setPassword(password);   // 密码
        userDTO.setCode(IdUtil.simpleUUID());   // 编号

        Integer userType = o.getUserType();
        // 判断userType是否在UserTypeEnum枚举中
        if (userType != null && UserTypeEnum.isExist(userType)) {
            userDTO.setUserType(userType);
        } else {
            userDTO.setUserType(UserTypeEnum.USER_NORMAL.key); // 默认普通用户
        }

        userDTO.setStatus(StatusEnum.NORMAL.key);  // 状态 0.禁用 1.正常
        userDTO.setIsLock(1);                      // 锁定状态 0.已锁定 1.正常
        userDTO.setCreateTime(LocalDateTime.now()); // 设置创建时间
        userService.add(userDTO);

        return ResponseResult.success("注册成功！");
    }
    /**
     * 获取登录用户信息
     * @param id
     * @return
     */
    @GetMapping("/getLoginUser")
    public ResponseResult<LoginUserVo> getLoginUser() {
        LoginUserVo user = (LoginUserVo) StpUtil.getSession().get(GlobalConstant.SESSION_USERINFO);
        return ResponseResult.success(user);
    }
}
