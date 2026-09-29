package com.eric.common.wrapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * XSS请求包装器
 * 对请求参数和请求体进行XSS攻击过滤
 * 通过重写getParameter、getParameterValues和getInputStream方法实现
 * @ClassName:   XssHttpServletRequestWrapper
 * @author:      Eric
 * @date:        2026-04-16 10:00:00
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 缓存的请求体内容
     */
    private byte[] cachedBody;

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        cacheRequestBody(request);
    }

    /**
     * 重写getParameter方法，对单个参数值进行XSS过滤
     *
     * @param name 参数名
     * @return 过滤后的参数值
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value != null && !value.isEmpty()) {
            return xssClean(value);
        }
        return value;
    }

    /**
     * 重写getParameterValues方法，对参数数组进行XSS过滤
     *
     * @param name 参数名
     * @return 过滤后的参数数组
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            String[] cleanedValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                cleanedValues[i] = xssClean(values[i]);
            }
            return cleanedValues;
        }
        return values;
    }

    /**
     * 重写getParameterMap方法，对所有参数进行XSS过滤
     *
     * @return 过滤后的参数Map
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        Map<String, String[]> cleanedMap = new LinkedHashMap<>();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String[] values = entry.getValue();
            String[] cleanedValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                cleanedValues[i] = xssClean(values[i]);
            }
            cleanedMap.put(entry.getKey(), cleanedValues);
        }
        return cleanedMap;
    }

    /**
     * 重写getHeader方法，对请求头进行XSS过滤
     *
     * @param name 请求头名
     * @return 过滤后的请求头值
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value != null && !value.isEmpty()) {
            return xssClean(value);
        }
        return value;
    }

    /**
     * 重写getInputStream方法，对请求体进行XSS过滤
     * 适用于POST请求中JSON/XML格式的请求体
     *
     * @return 过滤后的输入流
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (cachedBody == null || cachedBody.length == 0) {
            return super.getInputStream();
        }

        // 对缓存的内容进行XSS清理
        String originalBody = new String(cachedBody, StandardCharsets.UTF_8);
        String cleanedBody = xssClean(originalBody);
        byte[] cleanedBytes = cleanedBody.getBytes(StandardCharsets.UTF_8);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cleanedBytes);
        return new XssServletInputStream(byteArrayInputStream);
    }

    /**
     * 重写getReader方法，对请求体进行XSS过滤
     *
     * @return 过滤后的Reader
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    /**
     * 缓存请求体内容
     * 由于请求体只能读取一次，需要提前缓存
     *
     * @param request 原始请求
     */
    private void cacheRequestBody(HttpServletRequest request) {
        try {
            cachedBody = StreamUtils.copyToByteArray(request.getInputStream());
        } catch (IOException e) {
            log.warn("缓存请求体失败", e);
            cachedBody = new byte[0];
        }
    }

    /**
     * XSS清理方法
     * 移除或转义潜在的XSS攻击字符
     *
     * @param value 原始值
     * @return 清理后的值
     */
    private String xssClean(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        // 第一步：HTML标签清理
        // 移除<script>标签及其内容
        value = value.replaceAll("(?i)<script[^>]*?>.*?</script>", "");
        // 移除<style>标签及其内容
        value = value.replaceAll("(?i)<style[^>]*?>.*?</style>", "");
        // 移除<iframe>标签及其内容
        value = value.replaceAll("(?i)<iframe[^>]*?>.*?</iframe>", "");
        // 移除<object>标签及其内容
        value = value.replaceAll("(?i)<object[^>]*?>.*?</object>", "");
        // 移除<embed>标签
        value = value.replaceAll("(?i)<embed[^>]*?>", "");
        // 移除<applet>标签及其内容
        value = value.replaceAll("(?i)<applet[^>]*?>.*?</applet>", "");

        // 第二步：事件属性清理
        // 移除on*事件属性（onclick, onerror, onload等）
        value = value.replaceAll("(?i)\\s*on\\w+\\s*=\\s*([\"'][^\"']*[\"']|[^\\s>]*)", "");

        // 第三步：javascript/vb脚本协议清理
        // 移除javascript:协议
        value = value.replaceAll("(?i)javascript\\s*:", "");
        // 移除vbscript:协议
        value = value.replaceAll("(?i)vbscript\\s*:", "");
        // 移除data:协议
        value = value.replaceAll("(?i)data\\s*:", "");

        // 第四步：HTML实体编码攻击清理
        // 移除&#x开头的十六进制实体编码
        value = value.replaceAll("(?i)&#x[0-9a-f]+;", "");
        // 移除&#开头的十进制实体编码
        value = value.replaceAll("(?i)&#[0-9]+;", "");

        // 第五步：特殊字符转义
        // 对<和>进行转义
        value = value.replaceAll("<", "&lt;");
        value = value.replaceAll(">", "&gt;");
        // 对引号进行转义
        value = value.replaceAll("\"", "&quot;");
        value = value.replaceAll("'", "&#x27;");

        return value;
    }

    /**
     * XSS过滤输入流实现
     */
    private static class XssServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream inputStream;

        public XssServletInputStream(ByteArrayInputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            // 不支持异步读取
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
    }
}
