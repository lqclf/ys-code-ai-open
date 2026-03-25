package com.eric.config.exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.eric.common.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一处理系统中的异常情况，返回标准化的错误响应
 *
 * @author Eric
 * @date 2025-03-25
 * @Copyright ERIC 微信公众号：Eric技术杂货库
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 Sa-Token 未登录异常
     * 当用户未登录或 token 无效时触发
     *
     * @param e NotLoginException 异常对象
     * @return 统一响应结果，包含 401 状态码和错误信息
     */
    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseResult<Void> handleNotLoginException(NotLoginException e) {
        log.warn("用户未登录或token无效: {}", e.getMessage());
        
        // 根据异常类型返回具体的错误信息
        String message;
        switch (e.getType()) {
            case NotLoginException.NOT_TOKEN:
                message = "未提供token，请先登录";
                break;
            case NotLoginException.INVALID_TOKEN:
                message = "token无效，请重新登录";
                break;
            case NotLoginException.TOKEN_TIMEOUT:
                message = "token已过期，请重新登录";
                break;
            case NotLoginException.BE_REPLACED:
                message = "账号已在其他地方登录，您已被登出";
                break;
            case NotLoginException.KICK_OUT:
                message = "账号已被踢下线";
                break;
            default:
                message = "登录已失效，请重新登录";
                break;
        }
        
        return ResponseResult.fail(message);
    }

    /**
     * 处理其他所有未捕获的异常
     *
     * @param e Exception 异常对象
     * @return 统一响应结果，包含 500 状态码和错误信息
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult<Void> handleException(Exception e) {
        log.error("系统发生异常: ", e);
        return ResponseResult.fail("系统繁忙，请稍后重试");
    }
}
