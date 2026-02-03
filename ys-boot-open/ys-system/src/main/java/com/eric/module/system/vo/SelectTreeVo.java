package com.eric.module.system.vo;

import lombok.Data;

import java.util.List;

/**
 * 下拉树VO --下拉列表使用
 * @ClassName:   SelectTreeVo
 * @author:  liuQ 
 * @date:    2025-08-07 23:40:22
 * @Copyright ERIC 微信公众号：Eric技术杂货库
 */
@Data
public class SelectTreeVo {

    private String value;

    private String label;

    private String icon;

    private Object data;

    private List<SelectTreeVo> children;
}
