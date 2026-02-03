package com.eric.common.utils;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


/**
 *  <p> Spring工具类 <p>
 * @ClassName:  SpringUtils
 * @author:     liuQ
 * @date:       2025-09-15 10:31:33
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Component
public class SpringUtils implements BeanFactoryPostProcessor, ApplicationContextAware {

    @Getter
    private static ApplicationContext applicationContext;

    private static ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringUtils.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    public static <T> T getBean(String beanName) {
        return (T) beanFactory.getBean(beanName);
    }

    public static <T> T getBean(Class<T> clazz) {
        return beanFactory.getBean(clazz);
    }

    public static boolean containsBean(String beanName) {
        return beanFactory.containsBean(beanName);
    }

}

