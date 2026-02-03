package com.eric.common.enums.common;

import com.eric.common.enums.BaseEnum;

/**
 * <p> 删除状态枚举类 </p>
 * @ClassName:  IsDeleteEnum
 * @author:     liuQ
 * @date:       2025-07-23 16:19:15
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
public enum IsDeleteEnum implements BaseEnum {

    DELETED(false, "已删除"),
    NOT_DELETED(true, "正常");

    public final Boolean key;
    public final String value;

    IsDeleteEnum(Boolean key, String value) {
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
