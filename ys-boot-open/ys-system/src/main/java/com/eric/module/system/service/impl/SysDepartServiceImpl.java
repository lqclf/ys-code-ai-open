package com.eric.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.common.enums.common.IsDeleteEnum;
import com.eric.common.module.domain.query.PageQuery;
import com.eric.common.module.service.impl.BaseServiceImpl;
import com.eric.common.utils.BeanCopyUtils;
import com.eric.module.system.dto.SysDepartDTO;
import com.eric.module.system.entity.SysDepart;
import com.eric.module.system.vo.SelectTreeVo;
import com.eric.module.system.mapper.SysDepartMapper;
import com.eric.module.system.service.SysDepartService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门信息表 服务实现类
 * @ClassName:  SysDepartServiceImpl     
 * @author:     liuQ
 * @date:       2025-09-10 15:22:32
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysDepartServiceImpl extends BaseServiceImpl<SysDepartMapper, SysDepart> implements SysDepartService {
	
	@Override
	public String add(SysDepartDTO o) {
		SysDepart sysDepart = BeanCopyUtils.copyObj(o, SysDepart.class);
        save(sysDepart);
		return sysDepart.getId();
	}

	@Override
	public Boolean delete(List<String> ids) {
		return removeByIds(ids);
	}

	@Override
	public Boolean update(SysDepartDTO o) {
		SysDepart sysDepart = baseMapper.selectById(o.getId());
		BeanUtils.copyProperties(o, sysDepart);
		return updateById(sysDepart);
	}

	@Override
	public SysDepartDTO get(Serializable id) {
		SysDepart sysDepart = baseMapper.selectById(id);
		return BeanCopyUtils.copyObj(sysDepart, SysDepartDTO.class);
	}
	

	@Override
	public Page<SysDepart> pageList(SysDepartDTO o, PageQuery page) {
		QueryWrapper<SysDepart> query = whereQuery(page);
		//增加其他查询条件 ...

		return page(Page.of(page.getPageNo(), page.getPageSize()), query);
	}
    
    @Override
	public Boolean batchAdd(List<SysDepartDTO> dataList) {
		if(!dataList.isEmpty()) {
			List<SysDepart> list = new ArrayList<SysDepart>();
			for (SysDepartDTO t : dataList) {
				SysDepart sysDepart = BeanCopyUtils.copyObj(t, SysDepart.class);
				list.add(sysDepart);
			}
			return this.saveOrUpdateBatch(list, 1000);
		}
        return false;
	}

	@Override
	public Boolean batchUpdate(List<SysDepartDTO> dataList) {
		if(!dataList.isEmpty()) {
			List<SysDepart> list = new ArrayList<SysDepart>();
			for (SysDepartDTO t : dataList) {
				SysDepart sysDepart = BeanCopyUtils.copyObj(t, SysDepart.class);
				list.add(sysDepart);
			}
			return this.updateBatchById(list);
		}
        return false;
	}

	@Override
	public List<SelectTreeVo> selectDeptTree() {
		QueryWrapper<SysDepart> query = new QueryWrapper<>();
		query.eq("is_delete", IsDeleteEnum.NOT_DELETED.getKey());
		query.orderByAsc("seq");
		List<SysDepart> dataList = baseMapper.selectList(query);
		return buildSelectTree(dataList, "0");
	}

	private static List<SelectTreeVo> buildSelectTree(List<SysDepart> dataList, String pid) {
		return dataList.stream()
				.filter(t -> t.getPid().equals(pid))
				.map(t -> {
					SelectTreeVo node = new SelectTreeVo();
					node.setValue(t.getId());
					node.setLabel(t.getName());
					node.setIcon(t.getIcon());
					node.setData(t);
					node.setChildren(buildSelectTree(dataList, t.getId()));
					return node;
				})
				.collect(Collectors.toList());
	}

}
