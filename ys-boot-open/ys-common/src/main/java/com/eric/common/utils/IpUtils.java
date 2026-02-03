package com.eric.common.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.xdb.Searcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.locks.ReentrantLock;

public class IpUtils {

    private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);
    private static final String FORMAT_URL = "https://apis.map.qq.com/ws/location/v1/ip?ip={}&key=XJIBZ-ZNUWU-ZHGVM-2Z3JG-VQKF2-HXFTB";
    private static final String LOCAL_IP = "127.0.0.1";
    private static final String RESOURCE_NAME = "ip2region.xdb";
    private static final String UNKNOWN_RESULT = "未知";

    // 使用双重校验锁保证线程安全的Searcher初始化
    private static volatile Searcher searcher;
    private static final ReentrantLock lock = new ReentrantLock();

    /**
     * 初始化IP查询器，加载IP2Region数据库到内存
     */
    private static void initSearcher() {
        if (searcher == null) {
            lock.lock();
            try {
                if (searcher == null) {
                    try (InputStream inputStream = IpUtils.class.getClassLoader().getResourceAsStream(RESOURCE_NAME)) {
                        if (inputStream == null) {
                            throw new FileNotFoundException("IP数据库资源未找到: " + RESOURCE_NAME);
                        }
                        File tempFile = File.createTempFile("ip2region_", ".xdb");
                        FileUtil.writeFromStream(inputStream, tempFile);
                        RandomAccessFile randomAccessFile = new RandomAccessFile(tempFile, "r");
                        byte[] cBuff = Searcher.loadContent(randomAccessFile);
                        searcher = Searcher.newWithBuffer(cBuff);
                        tempFile.deleteOnExit();
                    } catch (Exception e) {
                        logger.error("初始化IP查询器失败: {}", e.getMessage(), e);
                        throw new RuntimeException("IP查询器初始化失败", e);
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 获取当前HTTP请求对象
     *
     * @return HttpServletRequest 对象，如果无请求上下文则返回null
     */
    public static HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return requestAttributes != null ? requestAttributes.getRequest() : null;
        } catch (Exception e) {
            logger.debug("获取请求对象异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取客户端真实IP地址（支持代理场景）
     *
     * @return 客户端IP地址，获取失败返回空字符串
     */
    public static String getIp() {
        HttpServletRequest request = getRequest();
        if (request == null) return "";

        String[] headers = {"x-forwarded-for", "Proxy-Client-IP", "WL-Proxy-Client-IP"};
        String ip = null;

        // 遍历可能的代理头
        for (String header : headers) {
            ip = request.getHeader(header);
            if (isValidIp(ip)) break;
        }

        // 直接获取远程地址
        if (!isValidIp(ip)) {
            ip = request.getRemoteAddr();
            if (LOCAL_IP.equals(ip)) {
                try {
                    ip = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    logger.debug("获取本地IP失败: {}", e.getMessage());
                }
            }
        }

        // 处理多级代理
        if (ip != null && ip.indexOf(',') > 0) {
            ip = ip.substring(0, ip.indexOf(','));
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? LOCAL_IP : StringUtils.defaultIfBlank(ip, "");
    }

    /**
     * 校验IP地址是否有效
     */
    private static boolean isValidIp(String ip) {
        return StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip);
    }

    /**
     * 通过腾讯位置服务解析IP地理信息
     *
     * @param ip 要解析的IP地址
     * @return 国家-省份-城市格式的地理信息，解析失败返回"未知"
     */
    public static String getCityInfo(String ip) {
        if (StringUtils.isBlank(ip) || LOCAL_IP.equals(ip)) return UNKNOWN_RESULT;

        try {
            String json = analyzeIp(ip);
            JSONObject result = JSONUtil.parseObj(json);
            if (result.getInt("status") == 0) {
                JSONObject adInfo = result.getJSONObject("result").getJSONObject("ad_info");
                return String.format("%s-%s-%s",
                        adInfo.getStr("nation", UNKNOWN_RESULT),
                        adInfo.getStr("province", UNKNOWN_RESULT),
                        adInfo.getStr("city", UNKNOWN_RESULT));
            }
        } catch (Exception e) {
            logger.debug("IP解析异常: {}, IP: {}", e.getMessage(), ip);
        }
        return UNKNOWN_RESULT;
    }

    /**
     * 使用ip2region本地库解析IP信息
     *
     * @param ip 要解析的IP地址
     * @return 格式化后的IP地理信息，解析失败返回"未知"
     */
    public static String getIp2region(String ip) {
        if (searcher == null) initSearcher();
        if (StringUtils.isBlank(ip) || LOCAL_IP.equals(ip)) return UNKNOWN_RESULT;

        try {
            String region = searcher.search(ip);
            if (StringUtils.isNotBlank(region)) {
                return region.replace("|0", "").replace("0|", "");
            }
        } catch (Exception e) {
            logger.debug("本地IP解析失败: {}, IP: {}", e.getMessage(), ip);
        }
        return UNKNOWN_RESULT;
    }

    /**
     * 获取用户代理信息
     *
     * @param request HTTP请求对象
     * @return UserAgent解析对象
     */
    public static UserAgent getUserAgent(HttpServletRequest request) {
        return UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
    }

    /**
     * 获取本机IP地址
     *
     * @return 本机IP地址，获取失败返回127.0.0.1
     */
    public static String getHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.debug("获取本机IP失败: {}", e.getMessage());
            return LOCAL_IP;
        }
    }

    /**
     * 获取本机主机名
     *
     * @return 本机主机名，获取失败返回"未知"
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.debug("获取本机名称失败: {}", e.getMessage());
            return "未知";
        }
    }

    /**
     * 调用腾讯API解析IP信息
     *
     * @param ip 要解析的IP地址
     * @return API返回的JSON字符串
     */
    private static String analyzeIp(String ip) {
        StringBuilder result = new StringBuilder();
        String url = FORMAT_URL.replace("{}", ip);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new URL(url).openConnection().getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            logger.error("IP解析请求失败: {}, URL: {}", e.getMessage(), url);
        }
        return result.toString();
    }
}