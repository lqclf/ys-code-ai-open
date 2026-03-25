package com.eric.common.constant;


/**
 * 全局常量配置
 */
public class GlobalConstant {
	// SM4密钥
	public static final String SM4_SECRETKEY = "SM4_secretKey";
	// session中保存用户信息
	public static final String SESSION_USERINFO = "session_userinfo";

	/** 登录用户Token令牌缓存KEY前缀 */
	public static final String PREFIX_USER_TOKEN  = "prefix_user_token:";

	// 前端传入的token的key
	public static final String X_ACCESS_TOKEN = "X-Access-Token";

	/**
	 * 登录失败，用于记录失败次数的key
	 */
	public static final String LOGIN_FAIL = "LOGIN_FAIL_";












	//************************************quart 定时任务**********************************************/
	/**
	 * 定时任务组名
	 */
	public static final String QZ_JOB_GROUP_NAME = "JOB_GROUP_NAME";
	public static final String QZ_TRIGGER_GROUP_NAME = "TRIGGER_GROUP_NAME";

}
