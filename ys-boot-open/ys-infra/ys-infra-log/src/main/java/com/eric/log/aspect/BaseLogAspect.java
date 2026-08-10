package com.eric.log.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.eric.common.constant.GlobalConstant;
import com.eric.common.enums.system.LogLevelEnum;
import com.eric.common.module.domain.vo.LoginUserVo;
import com.eric.common.utils.IpUtils;
import com.eric.common.utils.JsonUtils;
import com.eric.log.config.LogCacheManager;
import com.eric.log.config.LogContextHolder;
import com.eric.log.module.dto.SysLoggerDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 日志切面基类
 * <p>
 * 抽取登录、操作、异常三类日志切面的公共能力，包括上下文构建、用户信息获取、
 * 参数序列化、日志对象初始化、异常堆栈格式化以及日志入队等，减少代码重复。
 * </p>
 *
 * @ClassName:  BaseLogAspect
 * @author:     Eric
 * @Copyright   ERIC 微信公众号：忆笙的技术杂货库
 */
@Slf4j
public abstract class BaseLogAspect {

    /** 日志缓存管理器，负责日志的异步批量落库 */
    @Autowired
    protected LogCacheManager logCacheManager;

    /** 异常堆栈最大记录行数，避免单条日志过大 */
    private static final int MAX_STACK_TRACE_LINES = 20;

    /**
     * 构建日志上下文信息
     * <p>
     * 在目标方法执行前初始化日志上下文，包括追踪ID、请求对象和用户信息。
     * </p>
     *
     * @param request   当前HTTP请求
     * @param joinPoint 切点对象
     */
    protected void buildLogContext(HttpServletRequest request, JoinPoint joinPoint) {
        try {
            // 生成并设置追踪ID，用于链路追踪和日志关联
            LogContextHolder.setTraceId(LogContextHolder.generateTraceId());
            // 设置请求对象到上下文，便于后续获取请求信息
            LogContextHolder.setRequest(request);

            // 尝试获取当前登录用户信息
            LoginUserVo loginUser = getUserInfo();
            if (loginUser != null) {
                LogContextHolder.setUserInfo(loginUser);
            }
        } catch (Exception e) {
            log.error("构建日志上下文失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 登录用户信息，未登录时返回 null
     */
    protected LoginUserVo getUserInfo() {
        try {
            if (StpUtil.isLogin()) {
                return (LoginUserVo) StpUtil.getSession().get(GlobalConstant.SESSION_USERINFO);
            }
        } catch (Exception e) {
            log.debug("获取用户信息失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取方法的全限定名称（类名.方法名）
     *
     * @param joinPoint 切点对象
     * @return 方法全限定名，获取失败时返回空串
     */
    protected String getMethodName(JoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            return joinPoint.getTarget().getClass().getName() + "." + method.getName();
        } catch (Exception e) {
            log.warn("获取方法名失败: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 将请求参数序列化为 JSON 字符串
     *
     * @param request 当前HTTP请求
     * @return 请求参数的JSON字符串
     */
    protected String getRequestParamsJson(HttpServletRequest request) {
        if (request == null) {
            return "{}";
        }
        try {
            return JsonUtils.toJson(request.getParameterMap());
        } catch (Exception e) {
            log.warn("序列化请求参数失败: {}", e.getMessage());
            return "{}";
        }
    }

    /**
     * 将响应数据序列化为 JSON 字符串
     *
     * @param response 响应对象
     * @return 响应数据的JSON字符串
     */
    protected String getResponseJson(Object response) {
        try {
            return JsonUtils.toJson(response);
        } catch (Exception e) {
            log.warn("序列化响应数据失败: {}", e.getMessage());
            return "{}";
        }
    }

    /**
     * 创建日志基础对象
     * <p>
     * 从上下文中获取用户信息、请求信息等，构建日志对象的公共字段。
     * </p>
     *
     * @return 已填充公共字段的日志DTO
     */
    protected SysLoggerDTO createLoggerBase() {
        SysLoggerDTO logger = new SysLoggerDTO();

        // 从上下文中设置用户信息
        logger.setUserId(LogContextHolder.getUserId());
        logger.setUserName(LogContextHolder.getUserName());
        logger.setLoginName(LogContextHolder.getLoginName());
        // 设置追踪ID，用于链路追踪
        logger.setTraceId(LogContextHolder.getTraceId());

        // 从上下文中获取请求对象，填充请求相关信息
        HttpServletRequest request = LogContextHolder.getRequest();
        if (request != null) {
            logger.setUrl(request.getRequestURI());
            logger.setUserAgent(request.getHeader("User-Agent"));
            // 获取客户端真实IP并解析归属地
            String ip = IpUtils.getIp();
            logger.setIp(ip);
            logger.setAddress(IpUtils.getIp2region(ip));
        }

        // 服务端操作系统信息
        logger.setWin(System.getProperty("os.name"));
        logger.setLogTime(LocalDateTime.now());
        // 默认日志级别为 INFO
        logger.setLogLevel(LogLevelEnum.INFO.key);

        return logger;
    }

    /**
     * 设置日志级别
     *
     * @param logger 日志对象
     * @param level  日志级别枚举
     */
    protected void setLogLevel(SysLoggerDTO logger, LogLevelEnum level) {
        if (logger != null && level != null) {
            logger.setLogLevel(level.key);
        }
    }

    /**
     * 将日志放入缓存队列，由缓存管理器异步批量落库
     *
     * @param logger 日志对象
     */
    protected void saveLog(SysLoggerDTO logger) {
        if (logger == null) {
            return;
        }
        try {
            logCacheManager.addLog(logger);
        } catch (Exception e) {
            log.error("保存日志失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 清理日志上下文，防止 ThreadLocal 内存泄漏
     */
    protected void clearLogContext() {
        try {
            LogContextHolder.clear();
        } catch (Exception e) {
            log.warn("清理日志上下文失败: {}", e.getMessage());
        }
    }

    /**
     * 格式化异常堆栈信息，限制最大行数避免日志过大
     *
     * @param e 异常对象
     * @return 格式化后的堆栈字符串
     */
    protected String formatStackTrace(Throwable e) {
        if (e == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(e.getClass().getName()).append(": ").append(e.getMessage()).append("\n");
        StackTraceElement[] elements = e.getStackTrace();
        for (int i = 0; i < elements.length && i < MAX_STACK_TRACE_LINES; i++) {
            sb.append("\t").append(elements[i]).append("\n");
        }
        if (elements.length > MAX_STACK_TRACE_LINES) {
            sb.append("\t... and ").append(elements.length - MAX_STACK_TRACE_LINES).append(" more\n");
        }
        return sb.toString();
    }
}
