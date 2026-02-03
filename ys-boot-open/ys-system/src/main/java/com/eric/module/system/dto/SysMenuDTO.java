package com.eric.module.system.dto;

import com.eric.common.module.domain.BaseEntityDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 菜单表-DTO层
 *
 * @ClassName: SysMenuDTO
 * @author: liuQ
 * @date: 2025-07-23 14:38:08
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysMenuDTO extends BaseEntityDTO {

    /**
     * 菜单名称
     */
    private String title;
    /**
     * 上级菜单id
     */
    private String pid;
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
     * 路由路径
     */
    private String path;
    /**
     * 权限标识
     */
    private String permission;
    /**
     * 图标
     */
    private String icon;
    /**
     * 外链地址
     */
    private String link;
    /**
     * 排序
     */
    private Integer seq;
    /**
     * 菜单类型 0菜单 1按钮
     */
    private Integer type;
    /**
     * 是否外链 0否 1是
     */
    private Integer isLink;
    /**
     * 是否隐藏 0否 1是
     */
    private Integer isHide;
    /**
     * 是否缓存 0否 1是
     */
    private Integer isCache;
    /**
     * 是否固定 0否 1是
     */
    private Integer isAffix;
}
