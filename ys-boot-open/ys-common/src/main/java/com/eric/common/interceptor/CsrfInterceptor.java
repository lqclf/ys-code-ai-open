package com.eric.common.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.eric.common.response.ResponseCode;
import com.eric.common.response.ResponseResult;
import com.eric.common.security.CsrfTokenValidator;
import com.eric.common.utils.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

/**
 * CSRF防护拦截器
 * 对HTTP请求进行CSRF令牌验证
 * 仅对修改状态的方法（POST/PUT/DELETE）进行验证
 * @ClassName:   CsrfInterceptor
 * @author:      Eric
 * @date:        2026-04-16 10:00:00
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
@Component
public class CsrfInterceptor implements HandlerInterceptor {

    /**
     * CSRF令牌请求头名
     */
    private static final String CSRF_HEADER_NAME = "X-CSRF-Token";

    /**
     * CSRF令牌请求参数名
     */
    private static final String CSRF_PARAM_NAME = "csrfToken";

    /**
     * 需要验证的HTTP方法
     */
    private static final List<String> PROTECTED_METHODS = Arrays.asList("POST", "PUT", "DELETE", "PATCH");

    /**
     * 排除路径列表
     */
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/logout",
            "/api/auth/captcha",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/doc.html"
    );

    @Autowired
    private CsrfTokenValidator csrfTokenValidator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String requestUri = request.getRequestURI();
        String method = request.getMethod().toUpperCase();

        // 跳过排除路径
        if (isExcludePath(requestUri)) {
            return true;
        }

        // 仅对修改状态的方法进行CSRF验证
        if (!PROTECTED_METHODS.contains(method)) {
            return true;
        }

        // 获取用户ID（从Sa-Token或JWT中获取）
        String userId = getUserId(request);
        if (userId == null) {
            // 未登录用户由其他拦截器处理
            return true;
        }

        // 获取CSRF令牌
        String csrfToken = extractCsrfToken(request);

        // 验证令牌
        if (csrfToken == null || csrfToken.isEmpty()) {
            log.warn("CSRF验证失败：缺少令牌，URI：{}，用户：{}", requestUri, userId);
            sendCsrfErrorResponse(response, "缺少CSRF令牌");
            return false;
        }

        // 执行验证（使用一次性验证）
        boolean isValid = csrfTokenValidator.validateAndConsume(userId, csrfToken);

        if (!isValid) {
            log.warn("CSRF验证失败：令牌无效，URI：{}，用户：{}", requestUri, userId);
            sendCsrfErrorResponse(response, "CSRF令牌无效或已过期");
            return false;
        }

        log.debug("CSRF验证通过，URI：{}，用户：{}", requestUri, userId);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        // 无需处理
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        // 无需处理
    }

    /**
     * 从请求中提取CSRF令牌
     * 优先级：请求头 > 请求参数
     *
     * @param request HTTP请求
     * @return CSRF令牌
     */
    private String extractCsrfToken(HttpServletRequest request) {
        // 优先从请求头获取
        String token = request.getHeader(CSRF_HEADER_NAME);
        if (token != null && !token.isEmpty()) {
            return token;
        }

        // 从请求参数获取
        return request.getParameter(CSRF_PARAM_NAME);
    }

    /**
     * 获取用户ID
     * 从Sa-Token或其他认证机制中获取
     *
     * @param request HTTP请求
     * @return 用户ID
     */
    private String getUserId(HttpServletRequest request) {
        // 尝试从Sa-Token获取（如果引入了Sa-Token）
        try {
            StpUtil.checkLogin();
            return StpUtil.getLoginIdAsString();
        } catch (Exception e) {
            // 如果Sa-Token不可用，从请求属性中获取
            return (String) request.getAttribute("userId");
        }
    }

    /**
     * 判断是否为排除路径
     *
     * @param path 请求路径
     * @return 是否排除
     */
    private boolean isExcludePath(String path) {
        if (EXCLUDE_PATHS == null || EXCLUDE_PATHS.isEmpty()) {
            return false;
        }
        for (String pattern : EXCLUDE_PATHS) {
            if (pattern.endsWith("/**")) {
                String prefix = pattern.substring(0, pattern.length() - 3);
                if (path.startsWith(prefix)) {
                    return true;
                }
            } else if (pattern.equals(path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 发送CSRF错误响应
     *
     * @param response HTTP响应
     * @param message  错误消息
     */
    private void sendCsrfErrorResponse(HttpServletResponse response, String message) {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setHeader("X-CSRF-Error", "true");

        try {
            ResponseResult<String> errorResult = ResponseResult.result(
                ResponseCode.FORBIDDEN != null ? ResponseCode.FORBIDDEN : ResponseCode.ERROR, 
                message
            );
            response.getWriter().write(JsonUtils.toJson(errorResult));
        } catch (Exception e) {
            log.error("发送CSRF错误响应失败", e);
        }
    }
}
