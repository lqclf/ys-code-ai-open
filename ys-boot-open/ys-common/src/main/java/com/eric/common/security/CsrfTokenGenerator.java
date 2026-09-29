package com.eric.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * CSRF令牌生成器
 * 生成并管理跨站请求伪造防护令牌
 * 令牌存储在Redis中，支持过期时间设置
 * @ClassName:   CsrfTokenGenerator
 * @author:      Eric
 * @date:        2026-04-16 10:00:00
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Component
public class CsrfTokenGenerator {

    /**
     * Redis键前缀
     */
    private static final String CSRF_TOKEN_PREFIX = "csrf:token:";

    /**
     * 令牌默认过期时间（分钟）
     */
    private static final long DEFAULT_TOKEN_EXPIRE_MINUTES = 30;

    /**
     * 安全随机数生成器
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * 令牌长度（字节）
     */
    private static final int TOKEN_LENGTH = 32;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 生成CSRF令牌
     * 生成随机令牌并存储到Redis中
     *
     * @param userId 用户ID
     * @return CSRF令牌字符串
     */
    public String generateToken(String userId) {
        return generateToken(userId, DEFAULT_TOKEN_EXPIRE_MINUTES);
    }

    /**
     * 生成CSRF令牌（自定义过期时间）
     *
     * @param userId       用户ID
     * @param expireMinutes 过期时间（分钟）
     * @return CSRF令牌字符串
     */
    public String generateToken(String userId, long expireMinutes) {
        // 生成随机令牌
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        SECURE_RANDOM.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

        // 存储到Redis
        String redisKey = CSRF_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(redisKey, token, expireMinutes, TimeUnit.MINUTES);

        return token;
    }

    /**
     * 刷新CSRF令牌
     * 删除旧令牌并生成新令牌
     *
     * @param userId 用户ID
     * @return 新令牌字符串
     */
    public String refreshToken(String userId) {
        // 删除旧令牌
        invalidateToken(userId);
        // 生成新令牌
        return generateToken(userId);
    }

    /**
     * 使令牌失效
     * 用于用户退出登录或令牌泄露时
     *
     * @param userId 用户ID
     */
    public void invalidateToken(String userId) {
        String redisKey = CSRF_TOKEN_PREFIX + userId;
        redisTemplate.delete(redisKey);
    }

    /**
     * 检查令牌是否存在
     *
     * @param userId 用户ID
     * @return 是否存在
     */
    public boolean hasToken(String userId) {
        String redisKey = CSRF_TOKEN_PREFIX + userId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }

    /**
     * 获取Redis键前缀
     * 用于测试和调试
     *
     * @return Redis键前缀
     */
    public static String getRedisKeyPrefix() {
        return CSRF_TOKEN_PREFIX;
    }

    /**
     * 获取默认过期时间
     *
     * @return 默认过期时间（分钟）
     */
    public static long getDefaultExpireMinutes() {
        return DEFAULT_TOKEN_EXPIRE_MINUTES;
    }
}
