package com.eric.common.module.controller;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * <p> Controller基类</p>
 * @ClassName      BaseController
 * @author         Eric
 * @version        v1.0  
 * @date           2024年01月03日 15:27:21    
 */
public class BaseController<T, S extends IService<T>> {

    protected S service;  // 移除字段上的 @Autowired

    @Autowired
    public void setService(S service) {
        this.service = service;
    }
}
