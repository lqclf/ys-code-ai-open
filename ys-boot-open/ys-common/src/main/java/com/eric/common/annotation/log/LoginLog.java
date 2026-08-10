package com.eric.common.annotation.log;

import java.lang.annotation.*;

/**
 * 登录日志注解
 * <p>
 * 用于标记需要记录登录日志的方法，由 LoginAspect 拦截处理。
 * </p>
 *
 * @ClassName:  LoginLog
 * @author:     Eric
 * @date:       2025-01-21
 * @Copyright   ERIC 微信公众号：忆笙的技术杂货库
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginLog {

    /**
     * 登录描述，用于记录登录操作的描述信息
     *
     * @return 登录描述
     */
    String value() default "用户登录";
}
