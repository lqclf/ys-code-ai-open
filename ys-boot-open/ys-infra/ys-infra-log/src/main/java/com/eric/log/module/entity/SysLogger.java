package com.eric.log.module.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 系统日志实体
 * <p>
 * 对应数据库表 sys_logger，统一承载登录、登出、业务操作、异常四类日志数据。
 * </p>
 *
 * @ClassName:  SysLogger
 * @author:     Eric
 * @Copyright   ERIC 微信公众号：忆笙的技术杂货库
 */
@TableName("sys_logger")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysLogger {

    /** 主键id */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /** 日志类型 0登录 1登出 2业务操作 3异常 */
    @TableField("log_type")
    private Integer logType;

    /** 功能模块 */
    @TableField("modul")
    private String modul;

    /** 操作类型 */
    @TableField("type")
    private String type;

    /** 操作描述 */
    @TableField("remark")
    private String remark;

    /** 请求参数 */
    @TableField("req_param")
    private String reqParam;

    /** 返回参数 */
    @TableField("resp_param")
    private String respParam;

    /** 操作用户ID */
    @TableField("user_id")
    private String userId;

    /** 操作用户登录名 */
    @TableField("login_name")
    private String loginName;

    /** 操作用户姓名 */
    @TableField("user_name")
    private String userName;

    /** 操作方法 */
    @TableField("method")
    private String method;

    /** 请求URL */
    @TableField("url")
    private String url;

    /** 请求IP */
    @TableField("ip")
    private String ip;

    /** 请求IP物理地址 */
    @TableField("address")
    private String address;

    /** 操作系统 */
    @TableField("win")
    private String win;

    /** 浏览器的信息 */
    @TableField("user_agent")
    private String userAgent;

    /** 操作版本号 */
    @TableField("version")
    private String version;

    /** 请求追踪ID，用于链路追踪 */
    @TableField("trace_id")
    private String traceId;

    /** 日志级别 0DEBUG 1INFO 2WARN 3ERROR 4FATAL */
    @TableField("log_level")
    private Integer logLevel;

    /** 操作时间 */
    @TableField("log_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime logTime;

    /** 异常名称 */
    @TableField("exc_name")
    private String excName;

    /** 异常信息（堆栈） */
    @TableField("exc_msg")
    private String excMsg;
}
