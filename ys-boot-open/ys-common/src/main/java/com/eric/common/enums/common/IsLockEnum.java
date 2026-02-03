package com.eric.common.enums.common;

import com.eric.common.enums.BaseEnum;

/**
 * <p> 用户锁定状态枚举类 </p>
 * @ClassName:  IsLockEnum
 * @author:     liuQ
 * @date:       2025-07-23 16:19:15
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
public enum IsLockEnum implements BaseEnum {

    LOCK(0, "已锁定"),
    NORMAL(1, "正常");

    public final Integer key;
    public final String value;

    IsLockEnum(Integer key, String value) {
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
