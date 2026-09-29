package com.eric.common.aspect;

import com.eric.common.annotation.RateLimiter;
import com.eric.common.exception.BaseException;
import com.eric.common.response.ResponseCode;
import com.eric.common.utils.IpUtils;
import com.eric.common.utils.RedisUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 接口限流切面
 * 基于Redis实现限流控制，支持多种限流策略
 * 防止接口被恶意刷请求或滥用
 * @ClassName:   RateLimiterAspect
 * @author:      Eric
 * @date:        2026-04-16 10:00:00
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
@Aspect
@Component
public class RateLimiterAspect {

    /**
     * Redis限流键前缀
     */
    private static final String RATE_LIMIT_PREFIX = "rate:limit:";

    /**
     * SpEL表达式解析器
     */
    private final SpelExpressionParser spelParser = new SpelExpressionParser();

    /**
     * 参数名称发现器
     */
    private final ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 限流切点
     * 拦截带有@RateLimiter注解的方法
     */
    @Around("@annotation(rateLimiter)")
    public Object around(ProceedingJoinPoint point, RateLimiter rateLimiter) throws Throwable {
        // 获取限流键
        String limitKey = buildLimitKey(point, rateLimiter);

        // 根据限流类型执行不同策略
        boolean allowed = switch (rateLimiter.limitType()) {
            case SLIDING_WINDOW -> slidingWindowLimit(limitKey, rateLimiter.time(), rateLimiter.count());
            case FIXED_WINDOW -> fixedWindowLimit(limitKey, rateLimiter.time(), rateLimiter.count());
            case TOKEN_BUCKET -> tokenBucketLimit(limitKey, rateLimiter.time(), rateLimiter.count());
        };

        if (!allowed) {
            log.warn("限流拦截，键：{}，类型：{}，维度：{}，最大次数：{}",
                    limitKey, rateLimiter.limitType(), rateLimiter.dimension(), rateLimiter.count());

            if (rateLimiter.throwException()) {
                throw new BaseException(rateLimiter.message(), ResponseCode.FORBIDDEN);
            } else {
                return com.eric.common.response.ResponseResult.fail(rateLimiter.message());
            }
        }

        log.debug("限流检查通过，键：{}", limitKey);
        return point.proceed();
    }

    /**
     * 构建限流键
     * 格式：rate:limit:{维度}:{具体标识}
     *
     * @param point       切点
     * @param rateLimiter 限流注解
     * @return 限流键
     */
    private String buildLimitKey(ProceedingJoinPoint point, RateLimiter rateLimiter) {
        StringBuilder keyBuilder = new StringBuilder(RATE_LIMIT_PREFIX);

        // 添加维度前缀
        keyBuilder.append(rateLimiter.dimension().name().toLowerCase()).append(":");

        // 添加具体标识
        String customKey = rateLimiter.key();
        if (customKey != null && !customKey.isEmpty()) {
            // 支持SpEL表达式
            String resolvedKey = parseSpelExpression(point, customKey);
            keyBuilder.append(resolvedKey);
        } else {
            // 默认使用方法全限定名
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            keyBuilder.append(method.getDeclaringClass().getName())
                    .append(".")
                    .append(method.getName());
        }

        return keyBuilder.toString();
    }

    /**
     * 滑动窗口限流算法
     * 使用Redis ZSET实现，更平滑的限流效果
     *
     * @param key     限流键
     * @param time    时间窗口（秒）
     * @param count   最大请求数
     * @return 是否允许请求
     */
    private boolean slidingWindowLimit(String key, int time, int count) {
        long now = System.currentTimeMillis();
        long windowStart = now - time * 1000L;

        // 移除窗口外的记录
        redisUtils.zRemoveRangeByScore(key, 0, windowStart);

        // 获取当前窗口内请求数
        Long currentCount = redisUtils.zCount(key, windowStart, now);

        if (currentCount != null && currentCount >= count) {
            return false;
        }

        // 添加当前请求记录
        redisUtils.zAdd(key, String.valueOf(now), now);
        // 设置过期时间
        redisUtils.expire(key, time + 1, TimeUnit.SECONDS);

        return true;
    }

    /**
     * 固定窗口限流算法
     * 简单但可能在窗口边界处产生突发请求
     *
     * @param key     限流键
     * @param time    时间窗口（秒）
     * @param count   最大请求数
     * @return 是否允许请求
     */
    private boolean fixedWindowLimit(String key, int time, int count) {
        // 获取当前计数
        Long currentCount = redisUtils.get(key);

        if (currentCount == null) {
            // 第一次请求，设置计数和过期时间
            redisUtils.set(key, 1, time, TimeUnit.SECONDS);
            return true;
        }

        if (currentCount >= count) {
            return false;
        }

        // 递增计数
        redisUtils.increment(key, 1);
        return true;
    }

    /**
     * 令牌桶限流算法
     * 允许一定程度的突发请求
     *
     * @param key     限流键
     * @param time    时间窗口（秒）
     * @param count   最大令牌数
     * @return 是否允许请求
     */
    private boolean tokenBucketLimit(String key, int time, int count) {
        String tokenKey = key + ":tokens";
        String lastTimeKey = key + ":lastTime";

        Long tokens = redisUtils.get(tokenKey);
        Long lastTime = redisUtils.get(lastTimeKey);
        long now = System.currentTimeMillis();

        if (tokens == null) {
            // 初始化令牌桶
            redisUtils.set(tokenKey, count - 1, time, TimeUnit.SECONDS);
            redisUtils.set(lastTimeKey, now, time, TimeUnit.SECONDS);
            return true;
        }

        // 计算新增令牌数
        long timeDiff = (now - lastTime) / 1000;
        long newTokens = timeDiff * count / time;

        if (newTokens > 0) {
            tokens = Math.min(tokens + newTokens, count);
            redisUtils.set(tokenKey, tokens, time, TimeUnit.SECONDS);
            redisUtils.set(lastTimeKey, now, time, TimeUnit.SECONDS);
        }

        if (tokens <= 0) {
            return false;
        }

        // 消费令牌
        redisUtils.decrement(tokenKey, 1);
        return true;
    }

    /**
     * 解析SpEL表达式
     * 支持从方法参数中提取变量
     *
     * @param point      切点
     * @param expression SpEL表达式
     * @return 解析后的值
     */
    private String parseSpelExpression(ProceedingJoinPoint point, String expression) {
        // 如果不是SpEL表达式，直接返回
        if (!expression.contains("#") && !expression.contains("T(")) {
            return expression;
        }

        try {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();

            // 获取参数名称
            String[] parameterNames = discoverer.getParameterNames(method);
            Object[] args = point.getArgs();

            // 创建评估上下文
            EvaluationContext context = new StandardEvaluationContext();
            if (parameterNames != null) {
                for (int i = 0; i < parameterNames.length; i++) {
                    context.setVariable(parameterNames[i], args[i]);
                }
            }

            // 添加特殊变量
            // #ip 当前请求IP
            context.setVariable("ip", IpUtils.getIp());

            // 解析表达式
            Expression expr = spelParser.parseExpression(expression);
            Object value = expr.getValue(context);

            return value != null ? value.toString() : expression;
        } catch (Exception e) {
            log.warn("SpEL表达式解析失败：{}", expression, e);
            return expression;
        }
    }

    /**
     * 获取当前HTTP请求
     *
     * @return HttpServletRequest
     */
    private HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
