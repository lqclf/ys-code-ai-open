package com.eric.log.module.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统日志查询/传输对象
 * <p>
 * 用于日志分页查询条件的传递，其中 logTypes 支持一次查询多种日志类型
 * （例如登录日志页签需要同时查询登录与登出两种类型）。
 * </p>
 *
 * @ClassName:  SysLoggerDTO
 * @author:     Eric
 * @Copyright   ERIC 微信公众号：忆笙的技术杂货库
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysLoggerDTO {

    /** 主键id */
    private String id;

    /** 日志类型 0登录 1登出 2业务操作 3异常 */
    private Integer logType;

    /** 日志类型集合，支持一次查询多种类型 */
    private List<String> logTypes;

    /** 功能模块 */
    private String modul;

    /** 操作类型 */
    private String type;

    /** 操作描述 */
    private String remark;

    /** 请求参数 */
    private String reqParam;

    /** 返回参数 */
    private String respParam;

    /** 操作用户ID */
    private String userId;

    /** 操作用户登录名 */
    private String loginName;

    /** 操作用户姓名 */
    private String userName;

    /** 操作方法 */
    private String method;

    /** 请求URL */
    private String url;

    /** 请求IP */
    private String ip;

    /** 请求IP物理地址 */
    private String address;

    /** 操作系统 */
    private String win;

    /** 浏览器的信息 */
    private String userAgent;

    /** 操作版本号 */
    private String version;

    /** 请求追踪ID */
    private String traceId;

    /** 日志级别 0DEBUG 1INFO 2WARN 3ERROR 4FATAL */
    private Integer logLevel;

    /** 操作时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime logTime;

    /** 异常名称 */
    private String excName;

    /** 异常信息（堆栈） */
    private String excMsg;
}
