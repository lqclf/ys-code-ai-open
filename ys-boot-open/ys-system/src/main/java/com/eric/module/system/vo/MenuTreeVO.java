package com.eric.module.system.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 菜单树VO类 --左侧菜单使用
 * @ClassName: MenuTreeVO
 * @author: liuQ
 * @date: 2025-07-31
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MenuTreeVO {
    /**
     * id
     */
    private String id;
    /**
     * 父id
     */
    private String pid;
    /**
     * 路由路径
     */
    private String path;

    /**
     * 路由名称
     */
    private String name;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 重定向路径
     */
    private String redirect;

    /**
     * 菜单元数据
     */
    private Meta meta;

    /**
     * 子菜单
     */
    private List<MenuTreeVO> children;
    /*
     * 排序
     */
    private Integer seq;
    /**
     * 菜单元数据内部类
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public static class Meta {
        /**
         * 菜单标题
         */
        private String title;

        /**
         * 外链地址
         */
        private String isLink;

        /**
         * 是否隐藏
         */
        private Boolean isHide;

        /**
         * 是否缓存
         */
        private Boolean isKeepAlive;

        /**
         * 是否固定
         */
        private Boolean isAffix;

        /**
         * 是否iframe
         */
        private Boolean isIframe;

        /**
         * 图标
         */
        private String icon;

        /**
         * 角色列表
         */
        private List<String> roles;
    }
}