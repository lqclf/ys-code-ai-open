package com.eric.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.common.module.domain.query.PageQuery;
import com.eric.common.module.service.impl.BaseServiceImpl;
import com.eric.common.utils.BeanCopyUtils;
import com.eric.module.system.dto.SysConfigDTO;
import com.eric.module.system.entity.SysConfig;
import com.eric.module.system.mapper.SysConfigMapper;
import com.eric.module.system.service.SysConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统配置 服务实现类
 * @ClassName:  SysConfigServiceImpl     
 * @author:     liuQ
 * @date:       2025-09-11 15:29:29
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysConfigServiceImpl extends BaseServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {
	
	@Override
	public String add(SysConfigDTO o) {
		SysConfig sysConfig = BeanCopyUtils.copyObj(o, SysConfig.class);
        save(sysConfig);
		return sysConfig.getId();
	}

	@Override
	public Boolean delete(List<String> ids) {
		return removeByIds(ids);
	}

	@Override
	public Boolean update(SysConfigDTO o) {
		SysConfig sysConfig = baseMapper.selectById(o.getId());
		BeanUtils.copyProperties(o, sysConfig);
		return updateById(sysConfig);
	}

	@Override
	public SysConfigDTO get(Serializable id) {
		SysConfig sysConfig = baseMapper.selectById(id);
		return BeanCopyUtils.copyObj(sysConfig, SysConfigDTO.class);
	}
	

	@Override
	public Page<SysConfig> pageList(SysConfigDTO dto, PageQuery page) {
		QueryWrapper<SysConfig> query = whereQuery(page);
		//增加其他查询条件 ...

		return page(Page.of(page.getPageNo(), page.getPageSize()), query);
	}
    
    @Override
	public Boolean batchAdd(List<SysConfigDTO> dataList) {
		if(!dataList.isEmpty()) {
			List<SysConfig> list = new ArrayList<SysConfig>();
			for (SysConfigDTO t : dataList) {
				SysConfig sysConfig = BeanCopyUtils.copyObj(t, SysConfig.class);
				list.add(sysConfig);
			}
			return this.saveOrUpdateBatch(list, 1000);
		}
        return false;
	}

	@Override
	public Boolean batchUpdate(List<SysConfigDTO> dataList) {
		if(!dataList.isEmpty()) {
			List<SysConfig> list = new ArrayList<SysConfig>();
			for (SysConfigDTO t : dataList) {
				SysConfig sysConfig = BeanCopyUtils.copyObj(t, SysConfig.class);
				list.add(sysConfig);
			}
			return this.updateBatchById(list);
		}
        return false;
	}
}
