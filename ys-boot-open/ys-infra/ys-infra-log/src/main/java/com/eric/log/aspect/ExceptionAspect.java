package com.eric.log.aspect;

import com.eric.common.enums.system.LogLevelEnum;
import com.eric.common.enums.system.LogTypeEnum;
import com.eric.log.module.dto.SysLoggerDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 异常日志切面
 * <p>
 * 拦截 Controller 层抛出的异常并记录异常日志。
 * 切点采用注解匹配而非硬编码包路径，从而与具体业务模块解耦。
 * </p>
 *
 * @ClassName:  ExceptionAspect
 * @author:     Eric
 * @date:       2025-01-17
 * @Copyright   ERIC 微信公众号：忆笙的技术杂货库
 */
@Aspect
@Component
@Slf4j
public class ExceptionAspect extends BaseLogAspect {

    /**
     * 异常日志切点：拦截所有标注 @RestController 或 @Controller 的类的方法
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) || "
            + "@within(org.springframework.stereotype.Controller)")
    public void exceptionLogAspect() {
    }

    /**
     * 异常抛出通知，在 Controller 层方法抛出异常时记录异常日志
     *
     * @param joinPoint 切点连接点
     * @param e         抛出的异常对象
     */
    @AfterThrowing(throwing = "e", pointcut = "exceptionLogAspect()")
    public void doAfter(JoinPoint joinPoint, Throwable e) {
        HttpServletRequest request = null;
        try {
            // 获取当前请求对象
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
                request = servletRequestAttributes.getRequest();
            }

            // 构建日志上下文（追踪ID、请求对象、用户信息）
            buildLogContext(request, joinPoint);
            saveExceptionLog(request, joinPoint, e);
        } catch (Exception ex) {
            // 异常日志记录失败不影响主业务流程
            log.error("异常日志记录失败: {}", ex.getMessage(), ex);
        } finally {
            // 清理日志上下文，避免 ThreadLocal 内存泄漏
            clearLogContext();
        }
    }

    /**
     * 构建并保存异常日志
     *
     * @param request   HTTP请求对象
     * @param joinPoint 切点连接点
     * @param e         抛出的异常对象
     */
    private void saveExceptionLog(HttpServletRequest request, JoinPoint joinPoint, Throwable e) {
        try {
            SysLoggerDTO logger = createLoggerBase();

            logger.setLogType(LogTypeEnum.EXCEPTION.key);
            logger.setModul("操作异常");
            logger.setType("异常");
            logger.setReqParam(getRequestParamsJson(request));
            logger.setMethod(getMethodName(joinPoint));

            // 记录异常类名与截断后的堆栈信息
            logger.setExcName(e.getClass().getName());
            logger.setExcMsg(formatStackTrace(e));
            logger.setRespParam("");

            setLogLevel(logger, LogLevelEnum.ERROR);

            saveLog(logger);
        } catch (Exception ex) {
            log.error("保存异常日志失败: {}", ex.getMessage(), ex);
        }
    }
}
