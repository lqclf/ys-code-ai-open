package com.eric.common.filter;

import com.eric.common.wrapper.XssHttpServletRequestWrapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * XSS防护过滤器
 * 对HTTP请求参数和请求体进行XSS攻击过滤
 * 防止恶意脚本注入到系统中
 * @ClassName:   XssFilter
 * @author:      Eric
 * @date:        2026-04-16 10:00:00
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
public class XssFilter implements Filter {

    /**
     * 默认排除路径
     */
    private static final List<String> DEFAULT_EXCLUDE_PATHS = Arrays.asList(
            "/favicon.ico",
            "/static/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/doc.html"
    );

    /**
     * 排除路径列表
     */
    private List<String> excludePaths;

    /**
     * 是否启用XSS过滤
     */
    private boolean enabled = true;

    /**
     * 路径匹配器
     */
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public XssFilter() {
        this.excludePaths = DEFAULT_EXCLUDE_PATHS;
    }

    public XssFilter(List<String> excludePaths) {
        this.excludePaths = excludePaths != null ? excludePaths : DEFAULT_EXCLUDE_PATHS;
    }

    public XssFilter(boolean enabled, List<String> excludePaths) {
        this.enabled = enabled;
        this.excludePaths = excludePaths != null ? excludePaths : DEFAULT_EXCLUDE_PATHS;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("XSS过滤器初始化完成，启用状态：{}，排除路径数：{}", enabled, excludePaths.size());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestUri = httpRequest.getRequestURI();

        // 检查是否启用XSS过滤
        if (!enabled) {
            chain.doFilter(request, response);
            return;
        }

        // 检查是否为排除路径
        if (isExcludePath(requestUri)) {
            chain.doFilter(request, response);
            return;
        }

        // 仅对包含用户输入的请求方法进行XSS过滤
        String method = httpRequest.getMethod().toUpperCase();
        if (!isProtectedMethod(method)) {
            chain.doFilter(request, response);
            return;
        }

        log.debug("对请求执行XSS过滤，URI：{}，方法：{}", requestUri, method);

        // 使用XSS包装器包装请求
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(httpRequest);
        chain.doFilter(xssRequest, response);
    }

    @Override
    public void destroy() {
        log.info("XSS过滤器销毁");
    }

    /**
     * 判断是否为需要保护的方法
     * POST/PUT/PATCH方法通常包含用户输入数据
     *
     * @param method HTTP方法
     * @return 是否需要保护
     */
    private boolean isProtectedMethod(String method) {
        return "POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method);
    }

    /**
     * 判断是否为排除路径
     *
     * @param path 请求路径
     * @return 是否排除
     */
    private boolean isExcludePath(String path) {
        if (excludePaths == null || excludePaths.isEmpty()) {
            return false;
        }
        for (String pattern : excludePaths) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取排除路径列表
     *
     * @return 排除路径列表
     */
    public List<String> getExcludePaths() {
        return excludePaths;
    }

    /**
     * 设置排除路径列表
     *
     * @param excludePaths 排除路径列表
     */
    public void setExcludePaths(List<String> excludePaths) {
        this.excludePaths = excludePaths;
    }

    /**
     * 获取启用状态
     *
     * @return 是否启用
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置启用状态
     *
     * @param enabled 是否启用
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
