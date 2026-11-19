package com.eric.common.enums.common;

import com.eric.common.enums.BaseEnum;

/**
 * 状态枚举
 * @ClassName:  StatusEnum     
 * @author:     liuQ 
 * @date:       2025-07-28 14:53:02
 * @Copyright   ERIC 微信公众号：忆笙的技术杂货库
 */
public enum StatusEnum implements BaseEnum {

    DISABLED(0, "禁用"),
    NORMAL(1, "正常");

    public final Integer key;
    public final String value;

    StatusEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }
    /**
     * 根据key获取枚举值
     * @return
     */
    public static StatusEnum getByKey(Integer key) {
        for (StatusEnum value : values()) {
            if (value.key.equals(key)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public Object getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }
}
