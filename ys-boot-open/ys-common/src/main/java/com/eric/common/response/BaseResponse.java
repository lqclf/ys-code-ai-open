package com.eric.common.response;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 统一响应注解<br/>
 *               添加注解后，统一响应体才能生效
 * @ClassName:     BaseResponse     
 * @author:        liuQ
 * @date:          2024-03-06 10:26:35 
 * @Copyright 	   ERIC
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface BaseResponse {

}