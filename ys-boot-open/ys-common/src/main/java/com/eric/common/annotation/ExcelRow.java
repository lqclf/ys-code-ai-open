package com.eric.common.annotation;

import java.lang.annotation.*;

/**
 * @Description:  ExcelRow注解
 * @ClassName:  ExcelRow
 * @author:     Eric
 * @date:       2025-07-22 10:59:39
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Target(ElementType.FIELD) // 注解放置的目标位置,METHOD是可注解在方法级别上
@Retention(RetentionPolicy.RUNTIME) // 注解在哪个阶段执行
@Documented
public @interface ExcelRow {
	
	int row() default 1; // 列数 >=1
}
