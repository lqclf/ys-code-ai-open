package com.eric.common.enums.common;

import com.eric.common.enums.BaseEnum;

/**
 * 状态枚举
 * @ClassName:  StatusEnum     
 * @author:     liuQ 
 * @date:       2025-07-28 14:53:02
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
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
    @Override
    public Object getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }
}
