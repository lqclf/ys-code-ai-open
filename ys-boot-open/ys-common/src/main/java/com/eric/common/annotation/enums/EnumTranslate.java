package com.eric.common.annotation.enums;

import com.eric.common.enums.BaseEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumTranslate {

    /**
     * 枚举类
     * @return
     */
    Class<? extends BaseEnum> enumClass();

    /**
     * 翻译自定义字段后缀
     * @return
     */
    String enumText() default "Text";
}

