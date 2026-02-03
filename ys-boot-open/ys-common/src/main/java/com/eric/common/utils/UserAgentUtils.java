package com.eric.common.utils;

/**
 * 用户代理工具类
 * @ClassName:  UserAgentUtils
 * @author:     liuQ
 * @date:       2025-06-18 14:35:41
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
public class UserAgentUtils {

    /**
     * 获取操作系统信息
     */
    public static String getOs(String userAgent) {
        if (userAgent == null) return "未知";

        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("windows")) {
            return "Windows";
        } else if (userAgent.contains("mac")) {
            return "Mac OS";
        } else if (userAgent.contains("linux")) {
            return "Linux";
        } else if (userAgent.contains("android")) {
            return "Android";
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
            return "iOS";
        }
        return "未知";
    }

    /**
     * 获取浏览器信息
     */
    public static String getBrowser(String userAgent) {
        if (userAgent == null) return "未知";

        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("edge")) {
            return "Edge";
        } else if (userAgent.contains("chrome")) {
            return "Chrome";
        } else if (userAgent.contains("firefox")) {
            return "Firefox";
        } else if (userAgent.contains("safari")) {
            return "Safari";
        } else if (userAgent.contains("opera")) {
            return "Opera";
        } else if (userAgent.contains("msie") || userAgent.contains("trident")) {
            return "IE";
        }
        return "未知";
    }
}
