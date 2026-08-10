package com.eric.common.annotation.log;

import java.lang.annotation.*;

/**
 * 自定义操作日志注解
 * <p>
 * 标注在 Controller 方法上，由日志切面（OperationAspect）拦截，
 * 自动记录操作模块、操作类型、操作描述、请求参数、耗时等信息。
 * </p>
 *
 * @ClassName:  MyLog
 * @author:     Eric
 * @date:       2025-07-22 11:01:51
 * @Copyright   ERIC 微信公众号：忆笙的技术杂货库
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyLog {

    /**
     * 操作模块，例如：用户管理、角色管理
     *
     * @return 操作模块名称
     */
    String operModul() default "";

    /**
     * 操作类型，建议取值 {@link com.eric.common.constant.LogConst} 中的常量
     *
     * @return 操作类型
     */
    String operType() default "";

    /**
     * 操作描述，例如：新增用户
     *
     * @return 操作描述
     */
    String operDesc() default "";

}
