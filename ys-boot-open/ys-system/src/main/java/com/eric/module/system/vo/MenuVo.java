package com.eric.module.system.vo;

import com.eric.module.system.entity.SysMenu;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单vo --列表使用
 * @ClassName:  MenuVo     
 * @author:     liuQ 
 * @date:       2025-08-13 16:31:52
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuVo extends SysMenu {

    public String pidName; // 上级菜单名称
}
