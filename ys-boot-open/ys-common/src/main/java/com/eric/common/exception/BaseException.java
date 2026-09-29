package com.eric.common.exception;


import com.eric.common.response.ResponseCode;

/**
 * 业务异常类，继承运行时异常，确保事务正常回滚
 * @ClassName:     BaseException     
 * @author:        Eric
 * @date:          2024-03-06 10:26:49 
 * @Copyright 	   ERIC
 */
public class BaseException extends RuntimeException {

	private ResponseCode code;
	
	private String errorMsg;

	public BaseException(ResponseCode code) {
		super(code.getMsg());
		this.code = code;
	}
	
	public BaseException(String errorMsg, ResponseCode code) {
		super(errorMsg);
		this.errorMsg = errorMsg;
		this.code = code;
	}

	public BaseException(Throwable cause, ResponseCode code) {
		super(code.getMsg(), cause);
		this.code = code;
	}
	
	public BaseException(Throwable cause, String errorMsg, ResponseCode code) {
		super(errorMsg, cause);
		this.errorMsg = errorMsg;
		this.code = code;
	}

	public ResponseCode getCode() {
		return code;
	}

	public void setCode(ResponseCode code) {
		this.code = code;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseException other = (BaseException) obj;
		if (code != other.code)
			return false;
		return true;
	}

}
