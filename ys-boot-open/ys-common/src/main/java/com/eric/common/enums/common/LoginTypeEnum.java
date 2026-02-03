package com.eric.common.enums.common;

import com.eric.common.enums.BaseEnum;

/**
 *  登录类型
 * @ClassName:  LoginTypeEnum     
 * @author:     liuQ 
 * @date:       2025-07-24 15:52:11
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
public enum LoginTypeEnum implements BaseEnum {

    USER_LOGIN(1, "账号登录"),
    USER_PHONE(2, "手机号登录"),
    USER_EMAIL(3, "邮箱登录"),
    USER_CODE(4, "用户验证码"),
    THIRD_PART(5, "第三方登录"),
    ;

    public final Integer key;
    public final String value;

    LoginTypeEnum(Integer key, String value) {
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
