package com.eric.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.common.enums.common.IsDeleteEnum;
import com.eric.common.module.domain.query.PageQuery;
import com.eric.common.module.service.impl.BaseServiceImpl;
import com.eric.common.utils.BeanCopyUtils;
import com.eric.module.system.dto.SysRoleDTO;
import com.eric.module.system.entity.SysRole;
import com.eric.module.system.mapper.SysRoleMapper;
import com.eric.module.system.service.SysRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 角色信息表 服务实现类
 * @ClassName:  SysRoleServiceImpl
 * @author:     liuQ
 * @date:       2025-07-28 16:37:15
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysRoleServiceImpl extends BaseServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

	@Override
	public String add(SysRoleDTO o) {
		// 检查角色编码是否已存在
		if (checkCodeExists(o.getCode(), null)) {
			throw new RuntimeException("角色编码已存在");
		}
		SysRole sysRole = BeanCopyUtils.copyObj(o, SysRole.class);
		save(sysRole);
		return sysRole.getId();
	}

	@Override
	public Boolean delete(List<String> ids) {
		return removeByIds(ids);
	}

	@Override
	public Boolean update(SysRoleDTO o) {
		// 检查角色编码是否已存在
		if (checkCodeExists(o.getCode(), o.getId())) {
			throw new RuntimeException("角色编码已存在");
		}
		SysRole sysRole = baseMapper.selectById(o.getId());
		if (sysRole == null){
			throw new RuntimeException("角色不存在");
		}
		BeanUtils.copyProperties(o, sysRole);
		return updateById(sysRole);
	}

	@Override
	public SysRoleDTO get(Serializable id) {
		SysRole sysRole = baseMapper.selectById(id);
		return BeanCopyUtils.copyObj(sysRole, SysRoleDTO.class);
	}


	@Override
	public Page<SysRole> pageList(SysRoleDTO dto, PageQuery page) {
		QueryWrapper<SysRole> query = whereQuery(page);
		//增加其他查询条件 ...
		query.eq(StringUtils.isNotBlank(dto.getCode()), "code", dto.getCode());
		query.like(StringUtils.isNotBlank(dto.getName()), "name", dto.getName());
		return page(Page.of(page.getPageNo(), page.getPageSize()), query);
	}

	@Override
	public Boolean batchAdd(List<SysRoleDTO> dataList) {
		if(!dataList.isEmpty()) {
			List<SysRole> list = new ArrayList<SysRole>();
			for (SysRoleDTO t : dataList) {
				SysRole sysRole = BeanCopyUtils.copyObj(t, SysRole.class);
				list.add(sysRole);
			}
			return this.saveOrUpdateBatch(list, 1000);
		}
		return false;
	}

	@Override
	public Boolean batchUpdate(List<SysRoleDTO> dataList) {
		if(!dataList.isEmpty()) {
			List<SysRole> list = new ArrayList<SysRole>();
			for (SysRoleDTO t : dataList) {
				SysRole sysRole = BeanCopyUtils.copyObj(t, SysRole.class);
				list.add(sysRole);
			}
			return this.updateBatchById(list);
		}
		return false;
	}

	@Override
	public List<String> getRoleMenusIds(String roleId) {
		return baseMapper.getRoleMenusIds(roleId);
	}

	@Override
	public Boolean 	updateRoleMenus(String id, List<String> menuIds) {
		baseMapper.deleteMenuByRoleId(Collections.singletonList(id));
		if (!menuIds.isEmpty()) {
			baseMapper.insertRoleMenus(id, menuIds);
		}
		return Boolean.TRUE;
	}

	@Override
	public List<SysRole> getRoleList() {
		QueryWrapper<SysRole> query = new QueryWrapper<>();
		query.eq("is_delete", IsDeleteEnum.NOT_DELETED.getKey());
		return baseMapper.selectList(query);
	}


	/**
	 * 检查角色编码是否已存在
	 *
	 * @param code 角色编码
	 * @param excludeId 排除的角色ID
	 * @return true:已存在 false:不存在
	 */
	private boolean checkCodeExists(String code, String excludeId) {
		LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(SysRole::getCode, code);
		if (excludeId != null) {
			wrapper.ne(SysRole::getId, excludeId);
		}
		return baseMapper.selectCount(wrapper) > 0;
	}

}
