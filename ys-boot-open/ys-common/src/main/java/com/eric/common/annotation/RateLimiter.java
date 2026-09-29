package com.eric.common.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解
 * 基于Redis实现令牌桶/滑动窗口限流算法
 * 用于防止接口被恶意刷请求或滥用
 * @ClassName:   RateLimiter
 * @author:      Eric
 * @date:        2026-04-16 10:00:00
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 限流key
     * 支持SpEL表达式，如：#userId、#ip
     * 留空则使用方法全限定名
     */
    String key() default "";

    /**
     * 限流时间窗口（秒）
     * 默认60秒
     */
    int time() default 60;

    /**
     * 时间窗口内允许的最大请求数
     * 默认100次
     */
    int count() default 100;

    /**
     * 限流策略
     * SLIDING_WINDOW: 滑动窗口（默认）
     * FIXED_WINDOW: 固定窗口
     * TOKEN_BUCKET: 令牌桶
     */
    LimitType limitType() default LimitType.SLIDING_WINDOW;

    /**
     * 限流维度
     * USER: 按用户限流（默认）
     * IP: 按IP限流
     * GLOBAL: 全局限流（所有用户共享）
     */
    LimitDimension dimension() default LimitDimension.USER;

    /**
     * 自定义提示消息
     * 限流触发时返回给前端的消息
     */
    String message() default "请求过于频繁，请稍后再试";

    /**
     * 是否抛出异常
     * true: 抛出异常由全局异常处理器处理
     * false: 直接返回响应，不抛异常
     */
    boolean throwException() default true;

    /**
     * 限流类型枚举
     */
    enum LimitType {
        /**
         * 滑动窗口算法
         * 更平滑的限流效果
         */
        SLIDING_WINDOW,

        /**
         * 固定窗口算法
         * 简单但可能在窗口边界处产生突发
         */
        FIXED_WINDOW,

        /**
         * 令牌桶算法
         * 允许一定程度的突发请求
         */
        TOKEN_BUCKET
    }

    /**
     * 限流维度枚举
     */
    enum LimitDimension {
        /**
         * 按用户限流
         * 基于登录用户ID
         */
        USER,

        /**
         * 按IP限流
         * 基于客户端IP地址
         */
        IP,

        /**
         * 全局限流
         * 所有请求共享限流配额
         */
        GLOBAL
    }
}
