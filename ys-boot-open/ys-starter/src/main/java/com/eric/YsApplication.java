package com.eric;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Description: 启动类
 * @ClassName: YsApplication
 * @author: liuQ
 * @date: 2025-07-21 15:37:41
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
@SpringBootApplication
//@EnableFileStorage
@EnableCaching
@MapperScan("com.eric.*.*.mapper")
//@ProcessApplication
public class YsApplication {

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(YsApplication.class, args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = StringUtils.isNotBlank(env.getProperty("server.servlet.context-path"))?env.getProperty("server.servlet.context-path"):"";
        log.info("\n\n----------------------------------------------------------\n\t" +
                "Application is running! Access URLs:\n\t" +
                "API文档: \thttp://localhost:{}{}/doc.html\n" +
                "----------------------------------------------------------", port, path);

    }
}