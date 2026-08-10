package com.eric.log.config;

import com.eric.common.module.domain.vo.LoginUserVo;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 日志上下文持有者
 * <p>
 * 用于管理请求级别的日志上下文信息，功能说明：
 * <ol>
 *     <li>存储和管理请求级别的日志上下文信息</li>
 *     <li>使用 ThreadLocal 确保线程安全</li>
 *     <li>支持追踪ID、用户信息、请求对象等上下文信息</li>
 *     <li>集成 MDC（Mapped Diagnostic Context）用于日志追踪</li>
 * </ol>
 * <b>注意：</b>必须在请求处理完成后调用 {@link #clear()}，否则可能导致内存泄漏。
 * </p>
 *
 * @ClassName:  LogContextHolder
 * @author:     Eric
 * @date:       2025-01-17
 * @Copyright   ERIC 微信公众号：忆笙的技术杂货库
 */
public class LogContextHolder {

    /** 追踪ID的MDC键名，用于在日志中标识请求链路 */
    private static final String TRACE_ID_KEY = "traceId";

    /** 用户ID的键名 */
    private static final String USER_ID_KEY = "userId";

    /** 用户名的键名 */
    private static final String USER_NAME_KEY = "userName";

    /** 登录名的键名 */
    private static final String LOGIN_NAME_KEY = "loginName";

    /** 请求对象的键名 */
    private static final String REQUEST_KEY = "request";

    /** 上下文存储，使用 ThreadLocal 保证线程安全 */
    private static final ThreadLocal<Map<String, Object>> CONTEXT = ThreadLocal.withInitial(HashMap::new);

    /** 匿名用户标识，当用户信息不存在时使用 */
    private static final String ANONYMOUS_USER = "Anonymous";

    /**
     * 私有构造函数，防止实例化
     */
    private LogContextHolder() {
    }

    /**
     * 设置追踪ID，同时写入 MDC 与 ThreadLocal 上下文
     *
     * @param traceId 追踪ID，为 null 时不设置
     */
    public static void setTraceId(String traceId) {
        if (traceId != null) {
            MDC.put(TRACE_ID_KEY, traceId);
            CONTEXT.get().put(TRACE_ID_KEY, traceId);
        }
    }

    /**
     * 获取追踪ID，不存在时自动生成
     *
     * @return 追踪ID，永远不会返回 null
     */
    public static String getTraceId() {
        String traceId = (String) CONTEXT.get().get(TRACE_ID_KEY);
        if (traceId == null) {
            traceId = generateTraceId();
            setTraceId(traceId);
        }
        return traceId;
    }

    /**
     * 生成 32 位追踪ID（UUID 去除横杠）
     *
     * @return 32位的追踪ID字符串
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 设置当前登录用户信息
     *
     * @param loginUser 登录用户信息对象，为 null 时不设置
     */
    public static void setUserInfo(LoginUserVo loginUser) {
        if (loginUser != null) {
            CONTEXT.get().put(USER_ID_KEY, loginUser.getId());
            CONTEXT.get().put(USER_NAME_KEY, loginUser.getUserName());
            CONTEXT.get().put(LOGIN_NAME_KEY, loginUser.getLoginName());
        }
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID，不存在时返回 null
     */
    public static String getUserId() {
        return (String) CONTEXT.get().get(USER_ID_KEY);
    }

    /**
     * 获取当前登录用户姓名
     *
     * @return 用户名，不存在时返回 "Anonymous"
     */
    public static String getUserName() {
        String userName = (String) CONTEXT.get().get(USER_NAME_KEY);
        return userName != null ? userName : ANONYMOUS_USER;
    }

    /**
     * 获取当前登录用户登录名
     *
     * @return 登录名，不存在时返回 "Anonymous"
     */
    public static String getLoginName() {
        String loginName = (String) CONTEXT.get().get(LOGIN_NAME_KEY);
        return loginName != null ? loginName : ANONYMOUS_USER;
    }

    /**
     * 设置 HTTP 请求对象
     *
     * @param request HTTP请求对象，为 null 时不设置
     */
    public static void setRequest(HttpServletRequest request) {
        if (request != null) {
            CONTEXT.get().put(REQUEST_KEY, request);
        }
    }

    /**
     * 获取 HTTP 请求对象
     *
     * @return HTTP请求对象，不存在时返回 null
     */
    public static HttpServletRequest getRequest() {
        return (HttpServletRequest) CONTEXT.get().get(REQUEST_KEY);
    }

    /**
     * 清空日志上下文，防止内存泄漏与上下文污染
     * <p>
     * 建议在 finally 块中调用，确保一定会执行。
     * </p>
     */
    public static void clear() {
        MDC.remove(TRACE_ID_KEY);
        MDC.remove(USER_ID_KEY);
        MDC.remove(USER_NAME_KEY);
        MDC.remove(LOGIN_NAME_KEY);
        CONTEXT.remove();
    }

    /**
     * 获取上下文信息的副本，避免外部直接修改
     *
     * @return 上下文信息的副本
     */
    public static Map<String, Object> getContext() {
        return new HashMap<>(CONTEXT.get());
    }
}
