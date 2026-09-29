package com.eric.common.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 高性能Redis工具类
 * 自动为所有key添加项目顶级目录前缀
 * @ClassName:  RedisUtils
 * @author:     Eric
 * @date:       2025-06-16 21:57:30
 * @Copyright   ERIC 微信公众号：Eric技术杂货库
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisKeyUtil redisKeyUtil;

    /**
     * 是否启用项目前缀（默认启用）
     * -- GETTER --
     *  获取是否启用项目前缀
     *
     * @return true-启用，false-禁用

     */
    @Getter
    private boolean enableProjectPrefix = true;

    /**
     * 构建完整的Redis key（自动添加项目前缀）
     * @param key 原始key
     * @return 完整的Redis key
     */
    private String buildKey(String key) {
        if (!enableProjectPrefix) {
            return key;
        }

        if (redisKeyUtil.hasProjectPrefix(key)) {
            return key;
        }

        return redisKeyUtil.buildKey(key);
    }

    /**
     * 批量构建完整的Redis key
     * @param keys 原始key集合
     * @return 完整的Redis key集合
     */
    private Collection<String> buildKeys(Collection<String> keys) {
        if (!enableProjectPrefix) {
            return keys;
        }

        return keys.stream()
                .map(this::buildKey)
                .collect(Collectors.toList());
    }

    /**
     * 构建带通配符的key模式
     * @param pattern 原始模式
     * @return 完整的key模式
     */
    private String buildPattern(String pattern) {
        if (!enableProjectPrefix) {
            return pattern;
        }

        if (redisKeyUtil.hasProjectPrefix(pattern)) {
            return pattern;
        }

        return redisKeyUtil.buildPattern(pattern);
    }

    // region ===================== Key-Value操作 =====================

    /**
     * 设置键值对（永不过期）
     *
     * @param key   键，不允许为 null 或空字符串
     * @param value 值，允许为 null，但 Redis 中 null 值不会被存储
     * @throws IllegalArgumentException 如果 key 为 null 或空字符串
     */
    public void set(String key, Object value) {
        validateKey(key);
        String fullKey = buildKey(key);
        redisTemplate.opsForValue().set(fullKey, value);
    }

    /**
     * 设置键值对并指定过期时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间数值，必须大于 0
     * @param unit    过期时间单位，不允许为 null
     * @throws IllegalArgumentException 如果 key 为 null，timeout <= 0，unit 为 null
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        validateKey(key);
        Assert.notNull(unit, "TimeUnit must not be null");
        Assert.isTrue(timeout > 0, "Timeout must be greater than 0");
        String fullKey = buildKey(key);
        redisTemplate.opsForValue().set(fullKey, value, timeout, unit);
    }

    /**
     * 设置键值对（仅当键不存在时），并指定过期时间
     *
     * @param key     键，不允许为 null 或空字符串
     * @param value   值，允许为 null
     * @param timeout 过期时间数值，必须大于 0
     * @param unit    过期时间单位，不允许为 null
     * @return 是否设置成功（true 表示键不存在且设置成功，false 表示键已存在）
     * @throws IllegalArgumentException 如果 key 为 null，timeout <= 0，unit 为 null
     */
    public boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
        validateKey(key);
        Assert.notNull(unit, "TimeUnit must not be null");
        Assert.isTrue(timeout > 0, "Timeout must be greater than 0");
        String fullKey = buildKey(key);
        return redisTemplate.opsForValue().setIfAbsent(fullKey, value, timeout, unit);
    }

    /**
     * 获取键对应的值
     *
     * @param key 键，不允许为 null
     * @param <T> 返回值类型
     * @return 值，如果键不存在则返回 null
     * @throws IllegalArgumentException 如果 key 为 null
     */
    @Nullable
    public <T> T get(String key) {
        validateKey(key);
        String fullKey = buildKey(key);
        return (T) redisTemplate.opsForValue().get(fullKey);
    }

    /**
     * 删除指定键
     *
     * @param key 键，不允许为 null
     * @return 是否删除成功，键不存在时返回 false
     * @throws IllegalArgumentException 如果 key 为 null
     */
    public Boolean delete(String key) {
        validateKey(key);
        String fullKey = buildKey(key);
        return redisTemplate.delete(fullKey);
    }

    /**
     * 批量删除键
     *
     * @param keys 键集合，不允许为 null 或空集合
     * @return 成功删除的键数量
     * @throws IllegalArgumentException 如果 keys 为 null 或空集合
     */
    public Long delete(Collection<String> keys) {
        Assert.notEmpty(keys, "Keys collection must not be empty");
        Collection<String> fullKeys = buildKeys(keys);
        return redisTemplate.delete(fullKeys);
    }

    /**
     * 设置键的过期时间
     *
     * @param key     键，不允许为 null
     * @param timeout 过期时间数值，必须大于 0
     * @param unit    时间单位，不允许为 null
     * @throws IllegalArgumentException 如果 key 为 null，timeout <= 0，unit 为 null
     */
    public void expire(String key, long timeout, TimeUnit unit) {
        validateKey(key);
        Assert.notNull(unit, "TimeUnit must not be null");
        Assert.isTrue(timeout > 0, "Timeout must be greater than 0");
        String fullKey = buildKey(key);
        redisTemplate.expire(fullKey, timeout, unit);
    }

    /**
     * 获取键的剩余过期时间
     *
     * @param key  键，不允许为 null
     * @param unit 时间单位，不允许为 null
     * @return 剩余过期时间，单位与参数一致；如果键不存在或无过期时间则返回 -1
     * @throws IllegalArgumentException 如果 key 为 null，unit 为 null
     */
    public Long getExpire(String key, TimeUnit unit) {
        validateKey(key);
        Assert.notNull(unit, "TimeUnit must not be null");
        String fullKey = buildKey(key);
        return redisTemplate.getExpire(fullKey, unit);
    }

    /**
     * 检查键是否存在
     *
     * @param key 键，不允许为 null
     * @return 键是否存在
     * @throws IllegalArgumentException 如果 key 为 null
     */
    public Boolean hasKey(String key) {
        validateKey(key);
        String fullKey = buildKey(key);
        return redisTemplate.hasKey(fullKey);
    }

    /**
     * 对键的值进行增量操作（仅适用于数字类型）
     *
     * @param key   键，不允许为 null
     * @param delta 增量值，必须大于 0
     * @throws IllegalArgumentException 如果 key 为 null，delta <= 0
     */
    @Nullable
    public void increment(String key, long delta) {
        validateKey(key);
        Assert.isTrue(delta > 0, "Delta must be greater than 0");
        String fullKey = buildKey(key);
        redisTemplate.opsForValue().increment(fullKey, delta);
    }

    /**
     * 对键的值进行减量操作（仅适用于数字类型）
     *
     * @param key   键，不允许为 null
     * @param delta 减量值，必须大于 0
     * @throws IllegalArgumentException 如果 key 为 null，delta <= 0
     */
    public void decrement(String key, long delta) {
        validateKey(key);
        Assert.isTrue(delta > 0, "Delta must be greater than 0");
        String fullKey = buildKey(key);
        redisTemplate.opsForValue().decrement(fullKey, delta);
    }

    /**
     * 获取字符串值的长度（以字节为单位）
     *
     * @param key 键，不允许为 null
     * @return 值的长度，如果键不存在则返回 null
     * @throws IllegalArgumentException 如果 key 为 null
     */
    @Nullable
    public Long size(String key) {
        validateKey(key);
        String fullKey = buildKey(key);
        return redisTemplate.opsForValue().size(fullKey);
    }
    // endregion


    // region ===================== BitMap操作 =====================

    /**
     * 设置位图中指定偏移位的值
     *
     * @param key     键，不允许为 null
     * @param offset  偏移量，必须 >= 0
     * @param value   位值（true 表示 1，false 表示 0）
     * @throws IllegalArgumentException 如果 key 为 null，offset < 0
     */
    public void setBit(String key, long offset, boolean value) {
        validateKey(key);
        String fullKey = buildKey(key);
        Assert.isTrue(offset >= 0, "Offset must be >= 0");
        redisTemplate.opsForValue().setBit(fullKey, offset, value);
    }

    /**
     * 获取位图中指定偏移位的值
     *
     * @param key    键，不允许为 null
     * @param offset 偏移量，必须 >= 0
     * @return 位值（true 表示 1，false 表示 0），如果键不存在则返回 null
     * @throws IllegalArgumentException 如果 key 为 null，offset < 0
     */
    @Nullable
    public Boolean getBit(String key, long offset) {
        validateKey(key);
        String fullKey = buildKey(key);
        Assert.isTrue(offset >= 0, "Offset must be >= 0");
        return redisTemplate.opsForValue().getBit(fullKey, offset);
    }

    /**
     * 统计位图中值为 1 的位的数量
     *
     * @param key   键，不允许为 null
     * @param start 起始偏移量（包含）
     * @param end   结束偏移量（包含）
     * @return 值为 1 的位的数量，如果键不存在则返回 0
     * @throws IllegalArgumentException 如果 key 为 null，start < 0，end < 0，start > end
     */
    public Long bitCount(String key, long start, long end) {
        validateKey(key);
        String fullKey = buildKey(key);
        Assert.isTrue(start >= 0 && end >= 0, "Start and end must be >= 0");
        Assert.isTrue(start <= end, "Start must be <= end");
        return redisTemplate.execute((RedisCallback<Long>) connection ->
                connection.bitCount(fullKey.getBytes(StandardCharsets.UTF_8), start, end));
    }
    // endregion

    // region ===================== Hash操作 =====================

    /**
     * 设置哈希表中指定字段的值并设置整个键的过期时间
     *
     * @param key     键，不允许为 null
     * @param hashKey 哈希字段名，不允许为 null 或空字符串
     * @param value   字段值，允许为 null
     * @param timeout 过期时间数值，必须 > 0
     * @param unit    时间单位，不允许为 null
     * @throws IllegalArgumentException 如果 key 或 hashKey 为 null 或空字符串，timeout <= 0，unit 为 null
     * @note 此方法非原子操作，若 key 不存在，expire 可能失败
     */
    public void hSetWithExpire(String key, String hashKey, Object value, long timeout, TimeUnit unit) {
        hSet(key, hashKey, value);
        expire(key, timeout, unit);
    }

    /**
     * 检查哈希表中是否存在指定字段
     *
     * @param key    键，不允许为 null
     * @param hashKey 字段名，不允许为 null 或空字符串
     * @return 字段是否存在
     * @throws IllegalArgumentException 如果 key 或 hashKey 为 null 或空字符串
     */
    public Boolean hHasKey(String key, String hashKey) {
        validateKey(key);
        String fullKey = buildKey(key);
        Assert.hasText(hashKey, "Hash key must not be empty");
        return redisTemplate.opsForHash().hasKey(fullKey, hashKey);
    }

    /**
     * 对哈希表字段值进行增量操作（仅适用于数字类型）
     *
     * @param key     键，不允许为 null
     * @param hashKey 字段名，不允许为 null 或空字符串
     * @param delta   增量值，必须 > 0
     * @return 操作后的新值，如果字段不存在则返回 null
     * @throws IllegalArgumentException 如果 key 或 hashKey 为 null 或空字符串，delta <= 0
     */
    @Nullable
    public Long hIncr(String key, String hashKey, long delta) {
        validateKey(key);
        Assert.hasText(hashKey, "Hash key must not be empty");
        String fullKey = buildKey(key);
        Assert.isTrue(delta > 0, "Delta must be greater than 0");
        return redisTemplate.opsForHash().increment(fullKey, hashKey, delta);
    }

    /**
     * 对哈希表字段值进行减量操作（仅适用于数字类型）
     *
     * @param key     键，不允许为 null
     * @param hashKey 字段名，不允许为 null 或空字符串
     * @param delta   减量值，必须 > 0
     * @return 操作后的新值，如果字段不存在则返回 null
     * @throws IllegalArgumentException 如果 key 或 hashKey 为 null 或空字符串，delta <= 0
     */
    @Nullable
    public Long hDecr(String key, String hashKey, long delta) {
        validateKey(key);
        Assert.hasText(hashKey, "Hash key must not be empty");
        String fullKey = buildKey(key);
        Assert.isTrue(delta > 0, "Delta must be greater than 0");
        return redisTemplate.opsForHash().increment(fullKey, hashKey, -delta);
    }
    /**
     * 获取哈希表中指定字段的值
     *
     * @param key    键，不允许为 null
     * @param hashKey 哈希字段名，不允许为 null 或空字符串
     * @param <T>    返回值类型
     * @return 字段值，如果字段不存在则返回 null
     * @throws IllegalArgumentException 如果 key 或 hashKey 为 null 或空字符串
     */
    @Nullable
    public <T> T hGet(String key, String hashKey) {
        validateKey(key);
        Assert.hasText(hashKey, "Hash key must not be empty");
        String fullKey = buildKey(key);
        return (T) redisTemplate.opsForHash().get(fullKey, hashKey);
    }

    /**
     * 设置哈希表中指定字段的值
     *
     * @param key     键，不允许为 null
     * @param hashKey 哈希字段名，不允许为 null 或空字符串
     * @param value   字段值，允许为 null
     * @throws IllegalArgumentException 如果 key 或 hashKey 为 null 或空字符串
     */
    public void hSet(String key, String hashKey, Object value) {
        validateKey(key);
        Assert.hasText(hashKey, "Hash key must not be empty");
        String fullKey = buildKey(key);
        redisTemplate.opsForHash().put(fullKey, hashKey, value);
    }

    /**
     * 获取整个哈希表
     *
     * @param key 键，不允许为 null
     * @return 哈希表的所有字段和值，如果键不存在则返回空 Map
     * @throws IllegalArgumentException 如果 key 为 null
     */
    public Map<Object, Object> hGetAll(String key) {
        validateKey(key);
        String fullKey = buildKey(key);
        return Optional.ofNullable(redisTemplate.opsForHash().entries(fullKey))
                .orElse(Collections.emptyMap());
    }

    /**
     * 批量设置哈希表的字段值
     *
     * @param key  键，不允许为 null
     * @param map  字段-值映射表，不允许为 null 或空
     * @throws IllegalArgumentException 如果 key 为 null，map 为 null 或空
     */
    public void hSetAll(String key, Map<?, ?> map) {
        validateKey(key);
        Assert.notEmpty(map, "Map must not be empty");
        String fullKey = buildKey(key);
        redisTemplate.opsForHash().putAll(fullKey, map);
    }

    /**
     * 删除哈希表中的一个或多个字段
     *
     * @param key      键，不允许为 null
     * @param hashKeys 要删除的字段名数组，不允许为 null 或空数组
     * @return 成功删除的字段数量
     * @throws IllegalArgumentException 如果 key 为 null，hashKeys 为 null 或空数组
     */
    public Long hDel(String key, Object... hashKeys) {
        validateKey(key);
        Assert.notEmpty(hashKeys, "Hash keys must not be empty");
        String fullKey = buildKey(key);
        return redisTemplate.opsForHash().delete(fullKey, hashKeys);
    }

    // endregion


    // region ===================== List操作 =====================

    /**
     * 向列表头部添加一个元素（左推）
     *
     * @param key   键，不允许为 null
     * @param value 元素值，允许为 null
     * @return 操作后列表的长度
     * @throws IllegalArgumentException 如果 key 为 null
     */
    public Long lPush(String key, Object value) {
        validateKey(key);
        String fullKey = buildKey(key);
        return redisTemplate.opsForList().leftPush(fullKey, value);
    }

    /**
     * 向列表头部批量添加元素（左推）
     *
     * @param key    键，不允许为 null
     * @param values 元素值数组，不允许为 null 或空数组
     * @return 操作后列表的长度
     * @throws IllegalArgumentException 如果 key 为 null，values 为 null 或空数组
     */
    public Long lPushAll(String key, Object... values) {
        validateKey(key);
        String fullKey = buildKey(key);
        Assert.notEmpty(values, "Values array must not be empty");
        return redisTemplate.opsForList().leftPushAll(fullKey, values);
    }

    /**
     * 从列表头部弹出一个元素（左弹）
     *
     * @param key 键，不允许为 null
     * @param <T> 元素类型
     * @return 弹出的元素，如果列表为空则返回 null
     * @throws IllegalArgumentException 如果 key 为 null
     */
    @Nullable
    public <T> T lPop(String key) {
        validateKey(key);
        String fullKey = buildKey(key);
        return (T) redisTemplate.opsForList().leftPop(fullKey);
    }

    /**
     * 从列表尾部弹出一个元素（右弹）
     *
     * @param key 键，不允许为 null
     * @param <T> 元素类型
     * @return 弹出的元素，如果列表为空则返回 null
     * @throws IllegalArgumentException 如果 key 为 null
     */
    @Nullable
    public <T> T rPop(String key) {
        validateKey(key);
        String fullKey = buildKey(key);
        return (T) redisTemplate.opsForList().rightPop(fullKey);
    }

    /**
     * 获取列表指定范围内的元素
     *
     * @param key   键，不允许为 null
     * @param start 起始索引（包含）
     * @param end   结束索引（包含）
     * @param <T>   元素类型
     * @return 元素列表，如果键不存在则返回空列表
     * @throws IllegalArgumentException 如果 key 为 null，start > end
     */
    @NonNull
    public <T> List<T> lRange(String key, long start, long end) {
        validateKey(key);
        String fullKey = buildKey(key);
        Assert.isTrue(start <= end, "Start must be <= end");
        return Optional.ofNullable((List<T>) redisTemplate.opsForList().range(fullKey, start, end))
                .orElse(Collections.emptyList());
    }

    /**
     * 获取列表长度
     *
     * @param key 键，不允许为 null
     * @return 列表长度，如果键不存在则返回 0
     * @throws IllegalArgumentException 如果 key 为 null
     */
    public Long lSize(String key) {
        validateKey(key);
        String fullKey = buildKey(key);
        return redisTemplate.opsForList().size(fullKey);
    }
    // endregion


    // region ===================== Set操作 =====================

    /**
     * 向集合添加一个或多个元素
     *
     * @param key    键，不允许为 null
     * @param values 元素值数组，不允许为 null 或空数组
     * @return 成功添加的元素数量（不包含已存在的元素）
     * @throws IllegalArgumentException 如果 key 为 null，values 为 null 或空数组
     */
    public Long sAdd(String key, Object... values) {
        validateKey(key);
        Assert.notEmpty(values, "Values array must not be empty");
        String fullKey = buildKey(key);
        return redisTemplate.opsForSet().add(fullKey, values);
    }
    /**
     * 检查元素是否在集合中
     *
     * @param key   键，不允许为 null
     * @param value 元素值，不允许为 null
     * @return 元素是否存在
     * @throws IllegalArgumentException 如果 key 或 value 为 null
     */
    public Boolean sIsMember(String key, Object value) {
        validateKey(key);
        String fullKey = buildKey(key);
        Assert.notNull(value, "Value must not be null");
        return redisTemplate.opsForSet().isMember(fullKey, value);
    }
    /**
     * 获取集合的所有元素
     *
     * @param key 键，不允许为 null
     * @param <T> 元素类型
     * @return 元素集合，如果键不存在则返回空集合
     * @throws IllegalArgumentException 如果 key 为 null
     */
    @NonNull
    public <T> Set<T> sMembers(String key) {
        validateKey(key);
        String fullKey = buildKey(key);
        return Optional.ofNullable((Set<T>) redisTemplate.opsForSet().members(fullKey))
                .orElse(Collections.emptySet());
    }

    /**
     * 从集合中移除一个或多个元素
     *
     * @param key    键，不允许为 null
     * @param values 元素值数组，不允许为 null 或空数组
     * @return 成功移除的元素数量
     * @throws IllegalArgumentException 如果 key 为 null，values 为 null 或空数组
     */
    public Long sRemove(String key, Object... values) {
        validateKey(key);
        Assert.notEmpty(values, "Values array must not be empty");
        String fullKey = buildKey(key);
        return redisTemplate.opsForSet().remove(fullKey, values);
    }
    /**
     * 获取集合大小
     *
     * @param key 键，不允许为 null
     * @return 集合大小，如果键不存在则返回 0
     * @throws IllegalArgumentException 如果 key 为 null
     */
    public Long sSize(String key) {
        validateKey(key);
        String fullKey = buildKey(key);
        return redisTemplate.opsForSet().size(fullKey);
    }
    // endregion


    // region ===================== ZSet操作 =====================

    /**
     * 向有序集合添加一个元素
     *
     * @param key   键，不允许为 null
     * @param value 元素值，不允许为 null
     * @param score 元素分数，必须 >= 0
     * @throws IllegalArgumentException 如果 key 为 null，value 为 null，score < 0
     */
    public void zAdd(String key, Object value, double score) {
        validateKey(key);
        String fullKey = buildKey(key);
        Assert.notNull(value, "Value must not be null");
        Assert.isTrue(score >= 0, "Score must be >= 0");
        redisTemplate.opsForZSet().add(fullKey, value, score);
    }

    /**
     * 批量向有序集合添加元素
     *
     * @param key    键，不允许为 null
     * @param tuples 元素-分数对集合，不允许为 null 或空
     * @return 成功添加的元素数量
     * @throws IllegalArgumentException 如果 key 为 null，tuples 为 null 或空
     */
    public Long zAdd(String key, Set<ZSetOperations.TypedTuple<Object>> tuples) {
        validateKey(key);
        String fullKey = buildKey(key);
        Assert.notEmpty(tuples, "Tuples must not be empty");
        return redisTemplate.opsForZSet().add(fullKey, tuples);
    }

    /**
     * 获取有序集合中指定分数范围内的元素（按升序排列）
     *
     * @param key  键，不允许为 null
     * @param min  最小分数（包含）
     * @param max  最大分数（包含）
     * @param <T>  元素类型
     * @return 元素集合，如果键不存在则返回空集合
     * @throws IllegalArgumentException 如果 key 为 null，min > max
     */
    @NonNull
    public <T> Set<T> zRangeByScore(String key, double min, double max) {
        validateKey(key);
        String fullKey = buildKey(key);
        Assert.isTrue(min <= max, "Min must be <= max");
        return Optional.ofNullable((Set<T>) redisTemplate.opsForZSet().rangeByScore(fullKey, min, max))
                .orElse(Collections.emptySet());
    }

    /**
     * 获取有序集合中指定排名范围内的元素（按升序排列）
     *
     * @param key   键，不允许为 null
     * @param start 起始排名（0-based，包含）
     * @param end   结束排名（0-based，包含）
     * @param <T>   元素类型
     * @return 元素集合，如果键不存在则返回空集合
     * @throws IllegalArgumentException 如果 key 为 null，start > end
     */
    @NonNull
    public <T> Set<T> zRange(String key, long start, long end) {
        validateKey(key);
        String fullKey = buildKey(key);
        Assert.isTrue(start <= end, "Start must be <= end");
        return Optional.ofNullable((Set<T>) redisTemplate.opsForZSet().range(fullKey, start, end))
                .orElse(Collections.emptySet());
    }

    /**
     * 获取有序集合中指定元素的排名（按升序排列）
     *
     * @param key   键，不允许为 null
     * @param value 元素值，不允许为 null
     * @return 排名（0-based），如果元素不存在则返回 null
     * @throws IllegalArgumentException 如果 key 或 value 为 null
     */
    @Nullable
    public Long zRank(String key, Object value) {
        validateKey(key);
        String fullKey = buildKey(key);
        Assert.notNull(value, "Value must not be null");
        return redisTemplate.opsForZSet().rank(fullKey, value);
    }

    /**
     * 获取有序集合中元素的数量
     *
     * @param key 键，不允许为 null
     * @return 元素数量，如果键不存在则返回 0
     * @throws IllegalArgumentException 如果 key 为 null
     */
    public Long zSize(String key) {
        validateKey(key);
        String fullKey = buildKey(key);
        return redisTemplate.opsForZSet().size(fullKey);
    }

    /**
     * 移除有序集合中指定分数范围内的元素
     *
     * @param key 键，不允许为 null
     * @param min 最小分数（包含）
     * @param max 最大分数（包含）
     * @throws IllegalArgumentException 如果 key 为 null
     */
    public void zRemoveRangeByScore(String key, double min, double max) {
        validateKey(key);
        String fullKey = buildKey(key);
        redisTemplate.opsForZSet().removeRangeByScore(fullKey, min, max);
    }

    /**
     * 统计有序集合中指定分数范围内的元素数量
     *
     * @param key  键，不允许为 null
     * @param min  最小分数（包含）
     * @param max  最大分数（包含）
     * @return 元素数量，如果键不存在则返回 0
     * @throws IllegalArgumentException 如果 key 为 null
     */
    public Long zCount(String key, double min, double max) {
        validateKey(key);
        String fullKey = buildKey(key);
        return redisTemplate.opsForZSet().count(fullKey, min, max);
    }
    // endregion

    // region ===================== 高级操作 =====================

    /**
     * 根据模式匹配获取键集合
     * 注意：此方法会自动添加项目前缀到pattern中
     *
     * @param pattern 匹配模式（如："login:token:*"）
     * @return 匹配的键集合，不会返回 null，无匹配时返回空集合
     * @throws IllegalArgumentException 如果 pattern 为 null 或空字符串
     */
    @NonNull
    public Set<String> keys(String pattern) {
        Assert.hasText(pattern, "Pattern must not be empty");
        String fullPattern = buildPattern(pattern);
        Set<String> rawKeys = Optional.ofNullable(redisTemplate.keys(fullPattern))
                .orElse(Collections.emptySet());
        return stripProjectPrefix(rawKeys);
    }

    /**
     * 扫描匹配指定模式的键（使用SCAN命令，避免阻塞Redis）
     * 注意：此方法会自动添加项目前缀到pattern中
     *
     * @param pattern 匹配模式（如："login:token:*"）
     * @return 匹配的键集合，不会返回 null，无匹配时返回空集合
     * @throws IllegalArgumentException 如果 pattern 为 null 或空字符串
     */
    @NonNull
    public Set<String> scan(String pattern) {
        Assert.hasText(pattern, "Pattern must not be empty");
        String fullPattern = buildPattern(pattern);
        Set<String> keys = new HashSet<>();
        ScanOptions options = ScanOptions.scanOptions().match(fullPattern).count(100).build();
        try {
            redisTemplate.execute((RedisCallback<Void>) connection -> {
                try (Cursor<byte[]> cursor = connection.keyCommands().scan(options)) {
                    while (cursor.hasNext()) {
                        keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
                    }
                }
                return null;
            });
        } catch (Exception e) {
            log.error("扫描Redis key失败", e);
        }
        return stripProjectPrefix(keys);
    }
    /**
     * 获取键存储的数据类型
     *
     * @param key 键，不允许为 null
     * @return 数据类型字符串（NONE, STRING, LIST, SET, ZSET, HASH）
     * @throws IllegalArgumentException 如果 key 为 null
     */
    public String type(String key) {
        validateKey(key);
        String fullKey = buildKey(key);
        return Optional.of(redisTemplate.type(fullKey))
                .map(DataType::name)
                .orElse("NONE");
    }

    /**
     * 清空整个 Redis 数据库（慎用！）
     */
    public void flushAll() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }
    /**
     * 扫描匹配指定模式的键（用于大数据量场景）
     * 注意：此方法会自动添加项目前缀到pattern中
     *
     * @param pattern 匹配模式（如："login:token:*"）
     * @return 匹配的键集合
     * @throws IllegalArgumentException 如果 pattern 为 null 或空字符串
     */
    @NonNull
    public Set<String> scanKeys(String pattern) {
        Assert.hasText(pattern, "Pattern must not be empty");
        String fullPattern = buildPattern(pattern);
        Set<String> keys = new HashSet<>();
        ScanOptions options = ScanOptions.scanOptions().match(fullPattern).build();
        try {
            redisTemplate.execute((RedisCallback<Void>) connection -> {
                try (Cursor<byte[]> cursor = connection.keyCommands().scan(options)) {
                    while (cursor.hasNext()) {
                        keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
                    }
                }
                return null;
            });
        } catch (Exception e) {
            log.error("扫描Redis key失败", e);
        }
        return stripProjectPrefix(keys);
    }

    /**
     * 扫描匹配指定模式的键（用于大数据量场景）增强
     * 注意：此方法会自动添加项目前缀到pattern中
     *
     * @param pattern 匹配模式（如："login:token:*"）
     * @param count 扫描的键数量
     * @return 匹配的键集合
     */
    @NonNull
    public Set<String> scanKeysOptimized(String pattern, int count) {
        Assert.hasText(pattern, "Pattern must not be empty");
        String fullPattern = buildPattern(pattern);
        Set<String> keys = new HashSet<>();
        ScanOptions options = ScanOptions.scanOptions()
                .match(fullPattern)
                .count(count)
                .build();

        try {
            redisTemplate.execute((RedisCallback<Void>) connection -> {
                try (Cursor<byte[]> cursor = connection.keyCommands().scan(options)) {
                    while (cursor.hasNext()) {
                        keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
                        if (keys.size() >= 1000) {
                            break;
                        }
                    }
                }
                return null;
            });
        } catch (Exception e) {
            log.error("扫描Redis key失败，回退到keys方法", e);
            return keys(pattern);
        }
        return stripProjectPrefix(keys);
    }

    // endregion

    // region ===================== 辅助方法 =====================

    /**
     * 验证键是否有效（非 null 且非空字符串）
     *
     * @param key 键
     * @throws IllegalArgumentException 如果 key 为 null 或空字符串
     */
    private void validateKey(String key) {
        Assert.notNull(key, "Key must not be null");
        Assert.hasText(key, "Key must not be empty");
    }

    private Set<String> stripProjectPrefix(Set<String> keys) {
        if (!enableProjectPrefix || keys == null || keys.isEmpty()) {
            return keys;
        }
        return keys.stream()
                .map(redisKeyUtil::extractOriginalKey)
                .collect(Collectors.toSet());
    }

    /**
     * 设置是否启用项目前缀
     * @param enable true-启用，false-禁用
     */
    public void setEnableProjectPrefix(boolean enable) {
        this.enableProjectPrefix = enable;
        log.info("Redis项目前缀功能已{}", enable ? "启用" : "禁用");
    }

    // endregion
}