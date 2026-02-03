package com.eric.module.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.common.enums.common.IsDeleteEnum;
import com.eric.common.enums.system.MenuTypeEnum;
import com.eric.common.module.domain.query.PageQuery;
import com.eric.common.module.service.impl.BaseServiceImpl;
import com.eric.common.utils.BeanCopyUtils;
import com.eric.module.system.dto.SysMenuDTO;
import com.eric.module.system.entity.SysMenu;
import com.eric.module.system.vo.MenuTreeVO;
import com.eric.module.system.vo.SelectTreeVo;
import com.eric.module.system.mapper.SysMenuMapper;
import com.eric.module.system.service.SysMenuService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 菜单表 服务实现类
 * @ClassName:  SysMenuServiceImpl
 * @author:     liuQ
 * @date:       2025-07-28 16:37:15
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysMenuServiceImpl extends BaseServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

	public List<SysMenu> getUserMenus() {
		// 获取当前登录用户ID
		String userId = (String) StpUtil.getLoginId();
		// 查询用户菜单列表
		return baseMapper.getUserMenuList(userId);
	}

	@Override
	public List<MenuTreeVO> getUserMenuTree() {
		// 获取当前登录用户菜单列表
		List<SysMenu> menuList = getUserMenus();

		SysMenu homeMenu = new SysMenu();
		homeMenu.setId("1");
		homeMenu.setPath("/home");
		homeMenu.setName("home");
		homeMenu.setComponent("/home/index.vue");
		homeMenu.setSeq(0); // 设置排序
		homeMenu.setTitle("首页");
		homeMenu.setType(MenuTypeEnum.CATALOG.key);
		homeMenu.setIcon("cn cn-index");
		homeMenu.setIsAffix(1);
		homeMenu.setIsLink(0);
		homeMenu.setIsCache(0);
		homeMenu.setIsHide(0);
		menuList.add(homeMenu);

		// 构建树形结构并返回
		return buildMenuTree(menuList);
	}

	@Override
	public List<SelectTreeVo> selectMenuTree() {
		QueryWrapper<SysMenu> query = new QueryWrapper<>();
		query.eq("is_delete", IsDeleteEnum.NOT_DELETED.getKey());
		query.in("type", MenuTypeEnum.CATALOG.getKey(), MenuTypeEnum.MENU.getKey());
		query.orderByAsc("seq");
		List<SysMenu> menuList = baseMapper.selectList(query);
		List<SysMenu> dataList = new ArrayList<>();
		for (SysMenu t : menuList) {
			SysMenu r = new SysMenu();
			BeanUtils.copyProperties(t, r);
			if(t.getPid() == null || t.getPid().isEmpty()) {
				r.setPid("0");
			}
			dataList.add(r);
		}
		return buildSelectTree(dataList, "0");
	}
	private static List<SelectTreeVo> buildSelectTree(List<SysMenu> dataList, String pid) {
		return dataList.stream()
				.filter(t -> t.getPid().equals(pid))
				.map(t -> {
					SelectTreeVo node = new SelectTreeVo();
					node.setValue(t.getId());
					node.setLabel(t.getTitle());
					node.setIcon(t.getIcon());
					node.setData(t);
					node.setChildren(buildSelectTree(dataList, t.getId()));
					return node;
				})
				.collect(Collectors.toList());
	}
	@Override
	public String add(SysMenuDTO o) {
		SysMenu sysMenu = BeanCopyUtils.copyObj(o, SysMenu.class);
		save(sysMenu);
		return sysMenu.getId();
	}

	@Override
	public Boolean delete(List<String> ids) {
		return removeByIds(ids);
	}

	@Override
	public Boolean update(SysMenuDTO o) {
		SysMenu sysMenu = baseMapper.selectById(o.getId());
		BeanUtils.copyProperties(o, sysMenu);
		return updateById(sysMenu);
	}

	@Override
	public SysMenuDTO get(Serializable id) {
		SysMenu sysMenu = baseMapper.selectById(id);
		return BeanCopyUtils.copyObj(sysMenu, SysMenuDTO.class);
	}


	@Override
	public Page<SysMenu> pageList(SysMenuDTO dto, PageQuery page) {
		QueryWrapper<SysMenu> queryWrapper = whereQuery(page);
		//增加其他查询条件 ...

		return page(Page.of(page.getPageNo(), page.getPageSize()), queryWrapper);
	}

	@Override
	public Boolean batchAdd(List<SysMenuDTO> dataList) {
		if(!dataList.isEmpty()) {
			List<SysMenu> list = new ArrayList<SysMenu>();
			for (SysMenuDTO t : dataList) {
				SysMenu sysMenu = BeanCopyUtils.copyObj(t, SysMenu.class);
				list.add(sysMenu);
			}
			return this.saveOrUpdateBatch(list, 1000);
		}
		return false;
	}

	@Override
	public Boolean batchUpdate(List<SysMenuDTO> dataList) {
		if(!dataList.isEmpty()) {
			List<SysMenu> list = new ArrayList<SysMenu>();
			for (SysMenuDTO t : dataList) {
				SysMenu sysMenu = BeanCopyUtils.copyObj(t, SysMenu.class);
				list.add(sysMenu);
			}
			return this.updateBatchById(list);
		}
		return false;
	}


	/**
	 * 将菜单列表转换为前端需要的树形结构（优化版：单函数递归实现）
	 *
	 * @param menuList 菜单列表
	 * @return 树形结构的菜单列表
	 */
	public static List<MenuTreeVO> buildMenuTree(List<SysMenu> menuList) {
		// 构建菜单映射，便于查找父子关系
		Map<String, SysMenu> menuMap = menuList.stream()
				.collect(Collectors.toMap(SysMenu::getId, Function.identity()));

		// 找出所有根节点（pid为空或不存在于menuMap中的节点）
		return menuList.stream()
				.filter(menu -> menu.getPid() == null || menu.getPid().isEmpty() || !menuMap.containsKey(menu.getPid()))
				.sorted(Comparator.comparing(SysMenu::getSeq, Comparator.nullsLast(Integer::compareTo))) // 按seq排序
				.map(menu -> buildTreeNode(menu, menuMap))
				.collect(Collectors.toList());
	}

	/**
	 * 递归构建菜单树节点
	 */
	private static MenuTreeVO buildTreeNode(SysMenu menu, Map<String, SysMenu> menuMap) {
		MenuTreeVO node = new MenuTreeVO();

		// 基本字段
		node.setPath(menu.getPath());
		node.setName(menu.getName());
		node.setComponent(menu.getComponent());
		node.setId(menu.getId());
		node.setPid(menu.getPid());
		node.setSeq(menu.getSeq());

		if (menu.getRedirect() != null && !menu.getRedirect().isEmpty()) {
			node.setRedirect(menu.getRedirect());
		}

		// 构建 meta 信息
		MenuTreeVO.Meta meta = new MenuTreeVO.Meta();
		meta.setTitle(menu.getTitle());
		meta.setIsLink(menu.getLink() != null ? menu.getLink() : "");
		meta.setIsHide(menu.getIsHide() != null && menu.getIsHide() == 1);
		meta.setIsKeepAlive(menu.getIsCache() == null || menu.getIsCache() == 0); // 默认缓存
		meta.setIsAffix(menu.getIsAffix() != null && menu.getIsAffix() == 1);
		meta.setIsIframe(menu.getIsLink() != null && menu.getIsLink() == 1);

		if (menu.getIcon() != null && !menu.getIcon().isEmpty()) {
			meta.setIcon(menu.getIcon());
		}

		// 角色信息 - 简化处理，默认所有用户都有权限
		List<String> roles = Arrays.asList("admin", "common");
		meta.setRoles(roles);

		node.setMeta(meta);

		// 递归构建子节点
		List<MenuTreeVO> children = menuMap.values().stream()
				.filter(child -> menu.getId().equals(child.getPid()))
				.sorted(Comparator.comparing(SysMenu::getSeq, Comparator.nullsLast(Integer::compareTo)))
				.map(child -> buildTreeNode(child, menuMap))
				.collect(Collectors.toList());

		if (!children.isEmpty()) {
			node.setChildren(children);
		}

		return node;
	}


}