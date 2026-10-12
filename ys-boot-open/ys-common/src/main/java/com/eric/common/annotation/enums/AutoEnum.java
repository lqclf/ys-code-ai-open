package com.eric.common.annotation.enums;

import java.lang.annotation.*;

/**
 *  <p> 自定义枚举注解 -用于接口方法，自定义开启枚举注解</p>
 * @ClassName:  AutoEnum     
 * @author:     Eric 
 * @date:       2025-07-29 09:49:48
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoEnum {

    /**
     * 备用
     * @return
     */
    String value() default "";
}
