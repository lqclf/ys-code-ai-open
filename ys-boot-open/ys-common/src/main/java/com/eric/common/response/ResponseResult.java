package com.eric.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;

/**
 * 统一公共响应体
 * @author liuQ
 * @date 2025-06-16 14:48:49
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ResponseResult<T> implements Serializable {

	private static final ResponseCode DEFAULT_SUCCESS = ResponseCode.SUCCESS;
	private static final ResponseCode DEFAULT_FAILURE = ResponseCode.ERROR;

	/**
	 * 状态码
	 */
	private Integer code;

	/**
	 * 返回信息
	 */
	private String msg;

	/**
	 * 响应数据 (泛型支持)
	 */
	private T data;

	/**
	 * 时间戳 (ISO-8601格式)
	 */
	private final String timestamp = Instant.now().toString();

	// ==================== 静态工厂方法 ====================

	public static <T> ResponseResult<T> success() {
		return success(DEFAULT_SUCCESS.getMsg(), null);
	}

	public static <T> ResponseResult<T> success(String msg) {
		return success(msg, null);
	}

	public static <T> ResponseResult<T> success(T data) {
		return success(DEFAULT_SUCCESS.getMsg(), data);
	}

	public static <T> ResponseResult<T> success(String msg, T data) {
		return new ResponseResult<T>()
				.setCode(DEFAULT_SUCCESS.getCode())
				.setMsg(msg)
				.setData(data);
	}

	public static <T> ResponseResult<T> fail() {
		return fail(DEFAULT_FAILURE.getMsg());
	}

	public static <T> ResponseResult<T> fail(String msg) {
		return fail(msg, null);
	}

	public static <T> ResponseResult<T> fail(String msg, T errorData) {
		return new ResponseResult<T>()
				.setCode(DEFAULT_FAILURE.getCode())
				.setMsg(msg)
				.setData(errorData);
	}

	public static <T> ResponseResult<T> result(ResponseCode code) {
		return result(code, code.getMsg(), null);
	}

	public static <T> ResponseResult<T> result(ResponseCode code, String msg) {
		return result(code, msg, null);
	}

	public static <T> ResponseResult<T> result(ResponseCode code, T data) {
		return result(code, code.getMsg(), data);
	}

	public static <T> ResponseResult<T> result(ResponseCode code, String msg, T data) {
		return new ResponseResult<T>()
				.setCode(code.getCode())
				.setMsg(msg)
				.setData(data);
	}

	/**
	 * 获取数据 (带安全转换)
	 */
	@SuppressWarnings("unchecked")
	public <R> R getDataAs(Class<R> clazz) {
		if (clazz.isInstance(data)) {
			return (R) data;
		}
		return null;
	}
}