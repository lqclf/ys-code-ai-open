package com.eric.common.enums.common;

import com.eric.common.enums.BaseEnum;

/**
 * 用户类型枚举
 * @ClassName:  UserTypeEnum
 * @author:     liuQ 
 * @date:       2025-07-24 16:17:32
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
public enum UserTypeEnum implements BaseEnum {

    USER_VISITOR(0, "游客"),
    USER_NORMAL(1, "普通用户"),
    USER_MEMBER(2, "会员"),
    USER_ADMIN(3, "管理员"),
    ;

    public final Integer key;
    public final String value;

    UserTypeEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 判断用户类型是否存在
     * @param userType
     * @return
     */
    public static boolean isExist(Integer userType) {
        for (UserTypeEnum value : values()) {
            if (value.key.equals(userType)) {
                return true;
            }
        }
        return false;
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
