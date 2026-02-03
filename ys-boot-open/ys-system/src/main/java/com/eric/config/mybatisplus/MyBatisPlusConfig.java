package com.eric.config.mybatisplus;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatisPlus配置项
 * @ClassName:  MyBatisPlusConfig
 * @author:     liuQ
 * @date:       2025-06-16 16:39:21
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Configuration
public class MyBatisPlusConfig {


	/**
	 * <p> MybatisPlus拦截器，用于分页 </p>
	 * @Title: paginationInterceptor
	 * @return
	 */
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		// 分页拦截器
		interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
		return interceptor;
	}

}