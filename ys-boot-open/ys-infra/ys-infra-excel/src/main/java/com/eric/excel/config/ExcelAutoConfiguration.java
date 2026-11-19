package com.eric.excel.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Excel自动配置类
 * 扫描com.eric.excel包下的所有组件，完成Excel模块的自动装配
 * @ClassName:   ExcelAutoConfiguration
 * @author:      liuQ
 * @date:        2025-02-27
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = "com.eric.excel")
public class ExcelAutoConfiguration {

    /**
     * 构造函数，Excel模块加载时自动调用
     */
    public ExcelAutoConfiguration() {
        //log.info("Excel通用导入导出模块已加载");
    }
}
