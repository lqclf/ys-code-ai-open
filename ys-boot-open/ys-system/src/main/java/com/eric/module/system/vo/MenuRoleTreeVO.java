package com.eric.module.system.vo;

import com.eric.module.system.entity.SysMenu;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 角色授权菜单树VO类  --角色授权使用
 * @ClassName: MenuRoleTreeVO
 * @author: liuQ
 * @date: 2025-07-31
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MenuRoleTreeVO {
    /**
     * id
     */
    private String id;
    /**
     * 菜单名称
     */
    private String title;
    /**
     * 父id
     */
    private String pid;
    /*
     * 排序
     */
    private Integer seq;

    /**
     * 按钮菜单集合
     */
    private List<SysMenu> btnList;
}