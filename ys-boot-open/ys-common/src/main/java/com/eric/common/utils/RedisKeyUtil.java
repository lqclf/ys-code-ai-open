package com.eric.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Redis缓存Key统一管理工具类
 * 提供统一的key生成方法，自动添加项目顶级目录前缀
 * @ClassName:  RedisKeyUtil
 * @author:     Eric
 * @date:       2025-04-27
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
@Component
public class RedisKeyUtil {

    /**
     * 项目名称，从配置文件读取
     */
    @Value("${spring.application.name:ysstokly}")
    private String projectName;

    /**
     * 部署环境，从配置文件读取
     */
    @Value("${spring.profiles.active:dev}")
    private String environment;

    /**
     * Redis key分隔符
     */
    private static final String KEY_SEPARATOR = ":";

    /**
     * 获取项目顶级目录key前缀
     * 格式：${project_name}:${environment}:
     * @return 项目顶级目录key前缀
     */
    public String getProjectPrefix() {
        return projectName + KEY_SEPARATOR + environment + KEY_SEPARATOR;
    }

    /**
     * 构建完整的Redis key
     * 格式：${project_name}:${environment}:${original_key}
     * @param originalKey 原始key
     * @return 完整的Redis key
     * @throws IllegalArgumentException 如果originalKey为null或空字符串
     */
    public String buildKey(String originalKey) {
        Assert.hasText(originalKey, "Original key must not be empty");
        return getProjectPrefix() + originalKey;
    }

    /**
     * 构建完整的Redis key（支持多个部分）
     * 格式：${project_name}:${environment}:${part1}:${part2}:...
     * @param parts key的各个部分
     * @return 完整的Redis key
     * @throws IllegalArgumentException 如果parts为null或空数组
     */
    public String buildKey(String... parts) {
        Assert.notEmpty(parts, "Key parts must not be empty");
        
        StringBuilder keyBuilder = new StringBuilder(getProjectPrefix());
        for (int i = 0; i < parts.length; i++) {
            Assert.hasText(parts[i], "Key part must not be empty");
            keyBuilder.append(parts[i]);
            if (i < parts.length - 1) {
                keyBuilder.append(KEY_SEPARATOR);
            }
        }
        
        return keyBuilder.toString();
    }

    /**
     * 构建带通配符的Redis key模式
     * 格式：${project_name}:${environment}:${pattern}
     * @param pattern key模式（如："login:token:*"）
     * @return 完整的Redis key模式
     * @throws IllegalArgumentException 如果pattern为null或空字符串
     */
    public String buildPattern(String pattern) {
        Assert.hasText(pattern, "Pattern must not be empty");
        return getProjectPrefix() + pattern;
    }

    /**
     * 从完整key中提取原始key（去除项目前缀）
     * @param fullKey 完整的Redis key
     * @return 原始key
     * @throws IllegalArgumentException 如果fullKey为null或空字符串
     */
    public String extractOriginalKey(String fullKey) {
        Assert.hasText(fullKey, "Full key must not be empty");
        
        String prefix = getProjectPrefix();
        if (fullKey.startsWith(prefix)) {
            return fullKey.substring(prefix.length());
        }
        
        log.warn("Key [{}] does not start with project prefix [{}]", fullKey, prefix);
        return fullKey;
    }

    /**
     * 检查key是否包含项目前缀
     * @param key Redis key
     * @return true-包含项目前缀，false-不包含项目前缀
     */
    public boolean hasProjectPrefix(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        return key.startsWith(getProjectPrefix());
    }

    /**
     * 获取项目名称
     * @return 项目名称
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * 获取部署环境
     * @return 部署环境
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * 获取完整的项目前缀（不带末尾分隔符）
     * 格式：${project_name}:${environment}
     * @return 项目前缀
     */
    public String getProjectPrefixWithoutSeparator() {
        return projectName + KEY_SEPARATOR + environment;
    }
}
