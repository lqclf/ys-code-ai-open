package com.eric.common.response;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 返回状态码
 *
 * @ClassName: ResponseCode
 * @author: liuQ
 * @date: 2024-03-06 10:27:13
 * @Copyright ERIC
 */
@Getter
public enum ResponseCode {

    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),
    /**
     * 对象创建成功
     */
    CREATED(201, "对象创建成功"),
    /**
     * 请求已经被接受
     */
    ACCEPTED(202, "请求已经被接受"),
    /**
     * 操作已经执行成功，但是没有返回数据
     */
    NO_CONTENT(204, "操作已经执行成功，但是没有返回数据"),
    /**
     * 资源已被移除
     */
    MOVED_PERM(301, "资源已被移除"),
    /**
     * 重定向
     */
    SEE_OTHER(303, "重定向"),
    /**
     * 资源没有被修改
     */
    NOT_MODIFIED(304, "资源没有被修改"),
    /**
     * 参数列表错误（缺少，格式不匹配）
     */
    BAD_REQUEST(400, "参数列表错误（缺少，格式不匹配）"),
    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),
    /**
     * 访问受限，授权过期
     */
    FORBIDDEN(403, "访问受限，授权过期"),
    /**
     * 资源，服务未找到
     */
    NOT_FOUND(404, "资源，服务未找！"),
    /**
     * 不允许的http方法
     */
    BAD_METHOD(405, "不允许的http方法"),
    /**
     * 资源冲突，或者资源被锁
     */
    CONFLICT(409, "资源冲突，或者资源被锁"),
    /**
     * 不支持的数据，媒体类型
     */
    UNSUPPORTED_TYPE(415, "不支持的数据，媒体类型"),
    /**
     * 系统内部错误
     */
    ERROR(500, "系统内部错误"),
    /**
     * 接口未实现
     */
    NOT_IMPLEMENTED(501, "接口未实现"),
    /**
     * 系统警告消息
     */
    WARN(601, "系统警告消息");


    /**
     * 错误码
     */
    private final int code;
    /**
     * 错误描述信息
     */
    private final String msg;

    ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCodeValue() {
        return this.code + "";
    }

    /**
     * 转为Map集合数据
     *
     * @return 枚举对象Map集合
     */
    public static Map<Integer, String> toMap() {
        Map<Integer, String> map = new HashMap<>(32);
        for (ResponseCode value : ResponseCode.values()) {
            map.put(value.getCode(), value.getMsg());
        }
        return map;
    }

    /**
     * 转为List集合数据
     *
     * @return 枚举对象List集合
     */
    public static List<Map<String, String>> toList() {
        List<Map<String, String>> list = new ArrayList<>(32);
        Map<String, String> map = null;
        for (ResponseCode item : ResponseCode.values()) {
            map = new HashMap<>();
            map.put("code", item.getCodeValue());
            map.put("msg", item.getMsg());
            list.add(map);
        }
        map = null;
        return list;
    }
}
