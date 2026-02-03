package com.eric.module.system.vo;

import lombok.Data;

import java.util.List;

/**
 * 
 * @ClassName:   UserDeptVo     
 * @author:  liuQ 
 * @date:    2025-09-11 22:33:24
 * @Copyright ERIC 微信公众号：Eric技术杂货库
 */
@Data
public class UserDeptVo {

    private List<String> userIds;

    private String deptId;
}
