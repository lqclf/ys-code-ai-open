package com.eric.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eric.common.module.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 菜单表-实体映射类
 * @ClassName:   SysMenu
 * @author:      liuQ
 * @date:        2025-07-23 14:38:08
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@TableName("sys_menu")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysMenu extends BaseEntity {

     /**
     * 菜单名称
     */
    @TableField("title")
	private String title;
     /**
     * 上级菜单id
     */
    @TableField("pid")
	private String pid;
     /**
     * 路由名称
     */
    @TableField("name")
	private String name;
     /**
     * 组件路径
     */
    @TableField("component")
	private String component;
     /**
     * 重定向路径
     */
    @TableField("redirect")
	private String redirect;
     /**
     * 路由路径
     */
    @TableField("path")
	private String path;
     /**
     * 权限标识
     */
    @TableField("permission")
	private String permission;
     /**
     * 图标
     */
    @TableField("icon")
	private String icon;
     /**
     * 外链地址
     */
    @TableField("link")
	private String link;
     /**
     * 排序
     */
    @TableField("seq")
	private Integer seq;
     /**
     * 菜单类型 0菜单 1按钮 2目录
     */
    @TableField("type")
	private Integer type;
     /**
     * 是否外链 0否 1是
     */
    @TableField("is_link")
	private Integer isLink;
     /**
     * 是否隐藏 0否 1是
     */
    @TableField("is_hide")
	private Integer isHide;
     /**
     * 是否缓存 0否 1是
     */
    @TableField("is_cache")
	private Integer isCache;
     /**
     * 是否固定 0否 1是
     */
    @TableField("is_affix")
	private Integer isAffix;
}
