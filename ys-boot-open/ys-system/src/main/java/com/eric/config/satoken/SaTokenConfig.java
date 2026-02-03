package com.eric.config.satoken;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *  Sa-Token 配置
 * @ClassName:   SaTokenConfig     
 * @author:  liuQ 
 * @date:    2025-07-26 22:39:00
 * @Copyright ERIC 微信公众号：Eric技术杂货库
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    // Sa-Token 整合 jwt (Simple 简单模式)
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }


    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，定义详细的拦截路由
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                .excludePathPatterns(               // Sa-Token 不拦截该路径
                        "/auth/login",              // 登录
                        "/auth/logout",             // 登出
                        "/auth/regester",           // 注册
                        "/auth/verify",
                        "/api/**",
                        "/wechat/**",
                        "/localFile/**",
                        "/druid/**",
                        "/error"
                );
    }
}
