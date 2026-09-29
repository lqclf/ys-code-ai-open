package com.eric.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * CSRF令牌验证器
 * 验证请求中的CSRF令牌是否有效
 * @ClassName:   CsrfTokenValidator
 * @author:      Eric
 * @date:        2026-04-16 10:00:00
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Component
public class CsrfTokenValidator {

    /**
     * Redis键前缀
     */
    private static final String CSRF_TOKEN_PREFIX = "csrf:token:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 验证CSRF令牌
     * 比较请求中的令牌与Redis中存储的令牌是否一致
     *
     * @param userId      用户ID
     * @param requestToken 请求中的令牌
     * @return 是否验证通过
     */
    public boolean validate(String userId, String requestToken) {
        if (userId == null || requestToken == null) {
            return false;
        }

        String redisKey = CSRF_TOKEN_PREFIX + userId;
        Object storedToken = redisTemplate.opsForValue().get(redisKey);

        if (storedToken == null) {
            return false;
        }

        // 使用常量时间比较防止时序攻击
        return constantTimeEquals(requestToken, storedToken.toString());
    }

    /**
     * 验证并消耗令牌（一次性令牌）
     * 验证成功后删除令牌，防止重放攻击
     *
     * @param userId      用户ID
     * @param requestToken 请求中的令牌
     * @return 是否验证通过
     */
    public boolean validateAndConsume(String userId, String requestToken) {
        if (!validate(userId, requestToken)) {
            return false;
        }

        // 验证成功后删除令牌
        String redisKey = CSRF_TOKEN_PREFIX + userId;
        redisTemplate.delete(redisKey);
        return true;
    }

    /**
     * 常量时间比较
     * 防止时序攻击（Timing Attack）
     *
     * @param a 字符串a
     * @param b 字符串b
     * @return 是否相等
     */
    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
