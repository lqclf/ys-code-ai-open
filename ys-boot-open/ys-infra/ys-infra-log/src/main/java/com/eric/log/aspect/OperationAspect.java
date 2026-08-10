package com.eric.log.aspect;

import com.eric.common.annotation.log.MyLog;
import com.eric.common.constant.LogConst;
import com.eric.common.enums.system.LogLevelEnum;
import com.eric.common.enums.system.LogTypeEnum;
import com.eric.log.config.LogContextHolder;
import com.eric.log.module.dto.SysLoggerDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 操作日志切面
 * <p>
 * 负责记录带有 {@link MyLog} 注解的方法的业务操作日志。
 * 日志对象构建完成后放入缓存队列，由 LogCacheManager 异步批量落库，
 * 因此此处无需再叠加线程池异步。
 * </p>
 *
 * @ClassName:  OperationAspect
 * @author:     Eric
 * @date:       2025-01-17
 * @Copyright   ERIC 微信公众号：忆笙的技术杂货库
 */
@Aspect
@Component
@Slf4j
public class OperationAspect extends BaseLogAspect {

    /**
     * 操作日志切点：拦截所有带有 {@link MyLog} 注解的方法
     */
    @Pointcut("@annotation(com.eric.common.annotation.log.MyLog)")
    public void operLogPointCut() {
    }

    /**
     * 操作日志后置通知，在目标方法执行成功后记录操作日志
     *
     * @param joinPoint 切点连接点
     * @param response  方法返回值
     */
    @AfterReturning(returning = "response", pointcut = "operLogPointCut()")
    public void doAfter(JoinPoint joinPoint, Object response) {
        HttpServletRequest request = null;
        try {
            // 获取当前请求对象
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
                request = servletRequestAttributes.getRequest();
            }

            // 构建日志上下文（追踪ID、请求对象、用户信息）
            buildLogContext(request, joinPoint);

            // 获取方法签名和 @MyLog 注解
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            MyLog myLog = method.getAnnotation(MyLog.class);

            if (myLog != null) {
                saveOperationLog(joinPoint, response, myLog);
            }
        } catch (Exception e) {
            // 操作日志记录失败不影响主业务流程
            log.error("操作日志记录失败: {}", e.getMessage(), e);
        } finally {
            // 清理日志上下文，避免 ThreadLocal 内存泄漏
            clearLogContext();
        }
    }

    /**
     * 构建并保存操作日志
     *
     * @param joinPoint 切点连接点
     * @param response  方法返回值
     * @param myLog     MyLog 注解对象
     */
    private void saveOperationLog(JoinPoint joinPoint, Object response, MyLog myLog) {
        try {
            SysLoggerDTO logger = createLoggerBase();

            logger.setLogType(LogTypeEnum.OPERATION.key);
            logger.setModul(myLog.operModul());
            logger.setType(myLog.operType());
            logger.setRemark(myLog.operDesc());
            logger.setMethod(getMethodName(joinPoint));
            logger.setReqParam(getRequestParamsJson(LogContextHolder.getRequest()));
            logger.setRespParam(getResponseJson(response));

            // 操作日志无异常信息
            logger.setExcName("");
            logger.setExcMsg("");

            // 按操作类型区分日志级别：高危操作提升为 WARN，便于筛选审计
            String operType = myLog.operType();
            if (LogConst.DELETE.equals(operType) || LogConst.EXPORT.equals(operType)) {
                setLogLevel(logger, LogLevelEnum.WARN);
            } else if (LogConst.ADD.equals(operType) || LogConst.UPDATE.equals(operType)) {
                setLogLevel(logger, LogLevelEnum.INFO);
            } else {
                setLogLevel(logger, LogLevelEnum.DEBUG);
            }

            saveLog(logger);
        } catch (Exception e) {
            log.error("保存操作日志失败: {}", e.getMessage(), e);
        }
    }
}
