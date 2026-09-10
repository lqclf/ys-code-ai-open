package com.eric.dict.module.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.common.module.domain.query.PageQuery;
import com.eric.common.module.service.impl.BaseServiceImpl;
import com.eric.common.utils.BeanCopyUtils;
import com.eric.dict.module.dto.SysDictDTO;
import com.eric.dict.module.entity.SysDict;
import com.eric.dict.module.mapper.SysDictMapper;
import com.eric.dict.module.service.SysDictService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 字典管理表 服务实现类
 * @ClassName:  SysDictServiceImpl
 * @author:     liuQ
 * @date:       2025-07-28 16:37:16
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysDictServiceImpl extends BaseServiceImpl<SysDictMapper, SysDict> implements SysDictService {

	@Override
	public String add(SysDictDTO o) {
		SysDict sysDict = BeanCopyUtils.copyObj(o, SysDict.class);
		save(sysDict);
		return sysDict.getId();
	}

	@Override
	public Boolean delete(List<String> ids) {
		return removeByIds(ids);
	}

	@Override
	public Boolean update(SysDictDTO o) {
		SysDict sysDict = baseMapper.selectById(o.getId());
		BeanUtils.copyProperties(o, sysDict);
		return updateById(sysDict);
	}

	@Override
	public SysDictDTO get(Serializable id) {
		SysDict sysDict = baseMapper.selectById(id);
		return BeanCopyUtils.copyObj(sysDict, SysDictDTO.class);
	}


	@Override
	public Page<SysDict> pageList(SysDictDTO dto, PageQuery page) {
		QueryWrapper<SysDict> query = whereQuery(page);
		//增加其他查询条件 ...
		query.like(StringUtils.isNotBlank(dto.getName()), "name", dto.getName());

		return page(Page.of(page.getPageNo(), page.getPageSize()), query);
	}

	@Override
	public Boolean batchAdd(List<SysDictDTO> dataList) {
		if(!dataList.isEmpty()) {
			List<SysDict> list = new ArrayList<SysDict>();
			for (SysDictDTO t : dataList) {
				SysDict sysDict = BeanCopyUtils.copyObj(t, SysDict.class);
				list.add(sysDict);
			}
			return this.saveOrUpdateBatch(list, 1000);
		}
		return false;
	}

	@Override
	public Boolean batchUpdate(List<SysDictDTO> dataList) {
		if(!dataList.isEmpty()) {
			List<SysDict> list = new ArrayList<SysDict>();
			for (SysDictDTO t : dataList) {
				SysDict sysDict = BeanCopyUtils.copyObj(t, SysDict.class);
				list.add(sysDict);
			}
			return this.updateBatchById(list);
		}
		return false;
	}
}
