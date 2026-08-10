package com.eric.log.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.eric.common.annotation.log.LoginLog;
import com.eric.common.annotation.log.LogoutLog;
import com.eric.common.constant.GlobalConstant;
import com.eric.common.enums.system.LogLevelEnum;
import com.eric.common.enums.system.LogTypeEnum;
import com.eric.common.module.domain.vo.LoginUserVo;
import com.eric.common.response.ResponseCode;
import com.eric.common.response.ResponseResult;
import com.eric.log.module.dto.SysLoggerDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 登录日志切面
 * <p>
 * 负责记录用户登录、登出操作日志。由于登出接口执行后会话即失效，
 * 因此在方法执行前先将用户信息暂存于 ThreadLocal，方法返回后再用于日志记录。
 * </p>
 *
 * @ClassName:  LoginAspect
 * @author:     Eric
 * @date:       2025-01-17
 * @Copyright   ERIC 微信公众号：忆笙的技术杂货库
 */
@Aspect
@Component
@Slf4j
public class LoginAspect extends BaseLogAspect {

    /**
     * 登出前的用户信息暂存区
     * <p>
     * 使用 ThreadLocal 而非实例字段，避免并发场景下多个请求相互覆盖用户信息。
     * </p>
     */
    private static final ThreadLocal<LoginUserVo> LOGOUT_USER = new ThreadLocal<>();

    /**
     * 登录方法切点：拦截带有 {@link LoginLog} 注解的方法
     */
    @Pointcut("@annotation(com.eric.common.annotation.log.LoginLog)")
    public void loginPointCut() {
    }

    /**
     * 登出方法切点：拦截带有 {@link LogoutLog} 注解的方法
     */
    @Pointcut("@annotation(com.eric.common.annotation.log.LogoutLog)")
    public void logoutPointCut() {
    }

    /**
     * 登录或登出组合切点
     */
    @Pointcut("loginPointCut() || logoutPointCut()")
    public void loginOrLogoutPointCut() {
    }

    /**
     * 登出前置通知
     * <p>
     * 登出接口执行后会话即被销毁，因此需要在执行前抓取用户信息。
     * </p>
     *
     * @param joinPoint 切点连接点
     */
    @Before("logoutPointCut()")
    public void doBefore(JoinPoint joinPoint) {
        try {
            if (StpUtil.isLogin()) {
                LOGOUT_USER.set((LoginUserVo) StpUtil.getSession().get(GlobalConstant.SESSION_USERINFO));
            }
        } catch (Exception e) {
            // 获取登出用户信息失败不影响主业务流程
            log.warn("获取登出用户信息失败: {}", e.getMessage());
        }
    }

    /**
     * 登录或登出后置通知，仅在操作成功时记录日志
     *
     * @param joinPoint 切点连接点
     * @param response  方法返回值
     */
    @AfterReturning(returning = "response", pointcut = "loginOrLogoutPointCut()")
    public void doAfter(JoinPoint joinPoint, Object response) {
        try {
            if (response == null) {
                return;
            }
            // 仅记录业务执行成功的登录/登出行为
            if (!isSuccess(response)) {
                return;
            }

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            // 获取当前请求对象
            HttpServletRequest request = null;
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
                request = servletRequestAttributes.getRequest();
            }

            if (method.getAnnotation(LoginLog.class) != null) {
                saveLoginLogoutLog(request, response, LogTypeEnum.LOGIN);
            } else if (method.getAnnotation(LogoutLog.class) != null) {
                saveLoginLogoutLog(request, response, LogTypeEnum.LOGOUT);
            }
        } catch (Exception e) {
            log.warn("记录登录/登出日志失败", e);
        } finally {
            // 无论成功与否都要清理，避免线程复用导致数据串号
            LOGOUT_USER.remove();
        }
    }

    /**
     * 判断响应结果是否为成功
     *
     * @param response 方法返回值
     * @return true 表示业务执行成功
     */
    private boolean isSuccess(Object response) {
        if (response instanceof ResponseResult<?> result) {
            return result.getCode() != null && result.getCode().equals(ResponseCode.SUCCESS.getCode());
        }
        // 非统一响应体默认视为成功
        return true;
    }

    /**
     * 构建并保存登录或登出日志
     *
     * @param request  HTTP请求对象
     * @param response 响应对象
     * @param logType  日志类型（登录或登出）
     */
    private void saveLoginLogoutLog(HttpServletRequest request, Object response, LogTypeEnum logType) {
        try {
            // 构建日志上下文（追踪ID、请求对象、用户信息）
            buildLogContext(request, null);

            SysLoggerDTO logger = createLoggerBase();
            logger.setLogType(logType.key);
            logger.setModul("系统" + logType.value);
            logger.setType(logType.value);
            logger.setReqParam(getRequestParamsJson(request));
            logger.setRespParam(getResponseJson(response));

            // 登录场景从当前会话取用户，登出场景取前置通知暂存的用户
            LoginUserVo currentUser = LogTypeEnum.LOGIN.equals(logType) ? getUserInfo() : LOGOUT_USER.get();
            if (currentUser != null) {
                logger.setUserId(currentUser.getId());
                logger.setUserName(currentUser.getUserName());
                logger.setLoginName(currentUser.getLoginName());
                logger.setRemark(currentUser.getUserName() + logType.value + "成功！");
            } else {
                logger.setRemark(logType.value + "成功！");
            }

            logger.setMethod("");
            logger.setVersion("1.0.0");
            logger.setExcName("");
            logger.setExcMsg("");
            setLogLevel(logger, LogLevelEnum.INFO);

            saveLog(logger);
        } catch (Exception e) {
            // 日志记录失败不影响主业务流程
            log.error("保存登录/登出日志失败: {}", e.getMessage(), e);
        } finally {
            clearLogContext();
        }
    }
}
