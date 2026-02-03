package com.eric.module.system.service;

import com.eric.common.module.service.BaseService;
import com.eric.module.system.dto.SysMenuDTO;
import com.eric.module.system.entity.SysMenu;
import com.eric.module.system.vo.MenuTreeVO;
import com.eric.module.system.vo.SelectTreeVo;

import java.util.List;

/**
 * 菜单表 服务类
 * @ClassName:  SysMenuService     
 * @author:     liuQ
 * @date:       2025-07-23 14:38:08
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
public interface SysMenuService extends BaseService<SysMenuDTO, SysMenu> {
    
    /**
     * 获取当前用户菜单列表
     * @return 菜单列表
     */
    List<MenuTreeVO> getUserMenuTree();
    /**
     * 获取下拉菜单树
     * @return 菜单列表
     */
    List<SelectTreeVo> selectMenuTree();

}