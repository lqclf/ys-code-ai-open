package com.eric.common.enums.system;

import com.eric.common.enums.BaseEnum;
import com.eric.common.module.domain.vo.EnumVo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 日志类型枚举
 * <p>
 * 定义系统日志的类型分类，用于标识日志的来源和用途：
 * <ul>
 *     <li>LOGIN: 登录日志，记录用户登录操作，包括登录时间、IP、登录状态等</li>
 *     <li>LOGOUT: 登出日志，记录用户登出操作</li>
 *     <li>OPERATION: 业务操作日志，记录用户的增删改查等业务操作</li>
 *     <li>EXCEPTION: 异常日志，记录系统运行中的异常信息及堆栈</li>
 * </ul>
 * </p>
 *
 * @ClassName:  LogTypeEnum
 * @author:     Eric
 * @date:       2025-07-29 08:54:14
 * @Copyright   ERIC 微信公众号：忆笙的技术杂货库
 */
public enum LogTypeEnum implements BaseEnum {

    /** 登录日志类型 */
    LOGIN(0, "登录"),

    /** 登出日志类型 */
    LOGOUT(1, "登出"),

    /** 业务操作日志类型 */
    OPERATION(2, "业务操作"),

    /** 异常日志类型 */
    EXCEPTION(3, "异常");

    /** 日志类型的数值编码，用于数据库存储和比较 */
    public final Integer key;

    /** 日志类型的描述，用于显示 */
    public final String value;

    /**
     * 构造函数
     *
     * @param key   日志类型的数值编码
     * @param value 日志类型的描述
     */
    LogTypeEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 获取日志类型的数值编码
     *
     * @return 日志类型的数值编码
     */
    @Override
    public Object getKey() {
        return key;
    }

    /**
     * 获取日志类型的描述
     *
     * @return 日志类型的描述
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * 获取日志类型的下拉列表数据，用于前端下拉框展示
     *
     * @return 日志类型的 EnumVo 列表
     */
    public static List<EnumVo> getSelectList() {
        return Arrays.stream(LogTypeEnum.values())
                .map(e -> new EnumVo(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
