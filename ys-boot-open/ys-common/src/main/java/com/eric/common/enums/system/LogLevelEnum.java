package com.eric.common.enums.system;

import com.eric.common.enums.BaseEnum;
import com.eric.common.module.domain.vo.EnumVo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 日志级别枚举
 * <p>
 * 定义系统日志的级别类型，用于标识日志的重要程度：
 * <ul>
 *     <li>DEBUG: 调试信息，用于开发和调试阶段</li>
 *     <li>INFO: 一般信息，记录系统正常运行时的关键信息</li>
 *     <li>WARN: 警告信息，记录潜在问题但不影响系统运行</li>
 *     <li>ERROR: 错误信息，记录系统运行中的错误</li>
 *     <li>FATAL: 致命错误，记录导致系统无法运行的严重错误</li>
 * </ul>
 * </p>
 *
 * @ClassName:  LogLevelEnum
 * @author:     Eric
 * @date:       2025-07-29
 * @Copyright   ERIC 微信公众号：忆笙的技术杂货库
 */
public enum LogLevelEnum implements BaseEnum {

    /** 调试级别 */
    DEBUG(0, "DEBUG"),

    /** 信息级别 */
    INFO(1, "INFO"),

    /** 警告级别 */
    WARN(2, "WARN"),

    /** 错误级别 */
    ERROR(3, "ERROR"),

    /** 致命错误级别 */
    FATAL(4, "FATAL");

    /** 日志级别的数值编码，用于数据库存储和比较 */
    public final Integer key;

    /** 日志级别的描述，用于显示 */
    public final String value;

    /**
     * 构造函数
     *
     * @param key   日志级别的数值编码
     * @param value 日志级别的描述
     */
    LogLevelEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 获取日志级别的数值编码
     *
     * @return 日志级别的数值编码
     */
    @Override
    public Object getKey() {
        return key;
    }

    /**
     * 获取日志级别的描述
     *
     * @return 日志级别的描述
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * 获取日志级别的下拉列表数据，用于前端下拉框展示
     *
     * @return 日志级别的 EnumVo 列表
     */
    public static List<EnumVo> getSelectList() {
        return Arrays.stream(LogLevelEnum.values())
                .map(e -> new EnumVo(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
