package com.eric.listener;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import com.eric.common.utils.SpringContextUtils;
import com.eric.module.system.dto.SysUserDTO;
import com.eric.module.system.service.SysUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 
 * @ClassName:   MySaTokenListener     
 * @author:  liuQ 
 * @date:    2025-07-26 22:41:51
 * @Copyright ERIC 微信公众号：Eric技术杂货库
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MySaTokenListener implements SaTokenListener {

    @Autowired
    private SysUserService userService;

    @Value("${sa-token.timeout}")
    private Integer timeout;

    /** 每次登录时触发 */
    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginParameter loginModel) {

        // 获取用户信息
        SysUserDTO user = userService.get(String.valueOf(loginId));
        
        // 获取请求信息
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
                /*HttpServletRequest request = ObjectUtil.isNotEmpty(loginModel.getExtraData())
            ? (HttpServletRequest) loginModel.getExtraData().get("request") 
            : null;
*/
        log.info("用户 {} 登录系统，token: {}", user.getLoginName(), tokenValue);
    }

    /** 每次注销时触发 */
    @Override
    public void doLogout(String loginType, Object loginId, String tokenValue) {
        log.info("用户token {} 注销", tokenValue);
    }

    /** 每次被踢下线时触发 */
    @Override
    public void doKickout(String loginType, Object loginId, String tokenValue) {
        log.info("用户token {} 被踢下线", tokenValue);
    }

    /** 每次被顶下线时触发 */
    @Override
    public void doReplaced(String loginType, Object loginId, String tokenValue) {
        log.info("用户token {} 被顶下线", tokenValue);
    }

    /** 每次被封禁时触发 */
    @Override
    public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {
        log.info("用户 {} 被封禁，服务: {}, 等级: {}, 解封时间: {}", loginId, service, level, disableTime);
    }

    /** 每次被解封时触发 */
    @Override
    public void doUntieDisable(String loginType, Object loginId, String service) {
        log.info("用户 {} 被解封，服务: {}", loginId, service);
    }

    /** 每次二级认证时触发 */
    @Override
    public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {
        log.info("用户token {} 开启二级认证，服务: {}, 认证时间: {}", tokenValue, service, safeTime);
    }

    /** 每次退出二级认证时触发 */
    @Override
    public void doCloseSafe(String loginType, String tokenValue, String service) {
        log.info("用户token {} 退出二级认证，服务: {}", tokenValue, service);
    }

    /** 每次创建Session时触发 */
    @Override
    public void doCreateSession(String id) {
        log.info("创建Session: {}", id);
    }

    /** 每次注销Session时触发 */
    @Override
    public void doLogoutSession(String id) {
        log.info("注销Session: {}", id);
    }

    /** 每次Token续期时触发 */
    @Override
    public void doRenewTimeout(String tokenValue, Object loginId, String var3, long timeout) {
        log.info("用户token {} 续期，用户ID: {}, 超时时间: {}", tokenValue, loginId, timeout);
    }
}
