package com.eric.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eric.module.system.entity.SysMenu;

import java.util.List;

/**
 * 菜单表 Mapper 接口
 * @ClassName:  SysMenuMapper    
 * @author:     liuQ
 * @date:       2025-07-23 14:38:08
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 获取用户菜单列表
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> getUserMenuList(String userId);
}