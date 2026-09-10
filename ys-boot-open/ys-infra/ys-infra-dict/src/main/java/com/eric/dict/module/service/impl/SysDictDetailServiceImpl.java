package com.eric.dict.module.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.common.module.domain.query.PageQuery;
import com.eric.common.module.service.impl.BaseServiceImpl;
import com.eric.common.utils.BeanCopyUtils;
import com.eric.common.utils.RedisUtils;
import com.eric.dict.module.dto.SysDictDetailDTO;
import com.eric.dict.module.entity.SysDictDetail;
import com.eric.dict.module.mapper.SysDictDetailMapper;
import com.eric.dict.module.service.SysDictDetailService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 字典管理明细表 服务实现类
 * @ClassName:  SysDictDetailServiceImpl
 * @author:     liuQ
 * @date:       2025-07-28 16:36:39
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysDictDetailServiceImpl extends BaseServiceImpl<SysDictDetailMapper, SysDictDetail> implements SysDictDetailService {

	private static final Logger log = LoggerFactory.getLogger(SysDictDetailServiceImpl.class);

	/**
	 * 字典缓存键前缀
	 */
	private static final String DICT_CACHE_PREFIX = "dict:";

	/**
	 * 字典缓存过期时间（小时）
	 */
	private static final long DICT_CACHE_EXPIRE_HOURS = 24;

	@Autowired
	private RedisUtils redisUtils;

	@Override
	public String add(SysDictDetailDTO o) {
		SysDictDetail sysDictDetail = BeanCopyUtils.copyObj(o, SysDictDetail.class);
		save(sysDictDetail);
		// 清除字典缓存
		clearDictCache(o.getDictCode());
		return sysDictDetail.getId();
	}

	@Override
	public Boolean delete(List<String> ids) {
		// 查询要删除的记录，获取字典编码用于清除缓存
		List<SysDictDetail> details = baseMapper.selectBatchIds(ids);
		List<String> dictCodes = new ArrayList<>();
		for (SysDictDetail detail : details) {
			if (detail.getDictCode() != null && !dictCodes.contains(detail.getDictCode())) {
				dictCodes.add(detail.getDictCode());
			}
		}
		Boolean result = removeByIds(ids);
		// 清除字典缓存
		for (String dictCode : dictCodes) {
			clearDictCache(dictCode);
		}
		return result;
	}

	@Override
	public Boolean update(SysDictDetailDTO o) {
		SysDictDetail sysDictDetail = baseMapper.selectById(o.getId());
		String oldDictCode = sysDictDetail.getDictCode();
		BeanUtils.copyProperties(o, sysDictDetail);
		Boolean result = updateById(sysDictDetail);
		// 清除新旧字典编码的缓存
		clearDictCache(oldDictCode);
		if (!oldDictCode.equals(o.getDictCode())) {
			clearDictCache(o.getDictCode());
		}
		return result;
	}

	@Override
	public SysDictDetailDTO get(Serializable id) {
		SysDictDetail sysDictDetail = baseMapper.selectById(id);
		return BeanCopyUtils.copyObj(sysDictDetail, SysDictDetailDTO.class);
	}


	@Override
	public Page<SysDictDetail> pageList(SysDictDetailDTO dto, PageQuery page) {
		QueryWrapper<SysDictDetail> query = whereQuery(page);
		//增加其他查询条件 ...
		query.eq("dict_id", dto.getDictId());
		query.like(StringUtils.isNotBlank(dto.getName()), "name", dto.getName());
		return page(Page.of(page.getPageNo(), page.getPageSize()), query);
	}

	@Override
	public Boolean batchAdd(List<SysDictDetailDTO> dataList) {
		if(!dataList.isEmpty()) {
			List<SysDictDetail> list = new ArrayList<SysDictDetail>();
			for (SysDictDetailDTO t : dataList) {
				SysDictDetail sysDictDetail = BeanCopyUtils.copyObj(t, SysDictDetail.class);
				list.add(sysDictDetail);
			}
			Boolean result = this.saveOrUpdateBatch(list, 1000);
			// 清除所有相关字典的缓存
			List<String> dictCodes = new ArrayList<>();
			for (SysDictDetailDTO dto : dataList) {
				if (dto.getDictCode() != null && !dictCodes.contains(dto.getDictCode())) {
					dictCodes.add(dto.getDictCode());
				}
			}
			for (String dictCode : dictCodes) {
				clearDictCache(dictCode);
			}
			return result;
		}
		return false;
	}

	@Override
	public Boolean batchUpdate(List<SysDictDetailDTO> dataList) {
		if(!dataList.isEmpty()) {
			List<SysDictDetail> list = new ArrayList<SysDictDetail>();
			for (SysDictDetailDTO t : dataList) {
				SysDictDetail sysDictDetail = BeanCopyUtils.copyObj(t, SysDictDetail.class);
				list.add(sysDictDetail);
			}
			Boolean result = this.updateBatchById(list);
			// 清除所有相关字典的缓存
			List<String> dictCodes = new ArrayList<>();
			for (SysDictDetailDTO dto : dataList) {
				if (dto.getDictCode() != null && !dictCodes.contains(dto.getDictCode())) {
					dictCodes.add(dto.getDictCode());
				}
			}
			for (String dictCode : dictCodes) {
				clearDictCache(dictCode);
			}
			return result;
		}
		return false;
	}

	/**
	 * 根据字典编码查询字典明细列表（带Redis缓存优化）
	 * 查询策略：先查缓存，缓存未命中再查数据库并写入缓存
	 * @param dictCode 字典编码
	 * @return 字典明细列表
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<SysDictDetail> selectByDictCode(String dictCode) {
		String cacheKey = DICT_CACHE_PREFIX + dictCode;
		
		// 先从Redis缓存中查询
		try {
			List<SysDictDetail> cachedList = (List<SysDictDetail>) redisUtils.get(cacheKey);
			if (cachedList != null && !cachedList.isEmpty()) {
				log.debug("字典缓存命中: {}", dictCode);
				return cachedList;
			}
		} catch (Exception e) {
			log.warn("字典缓存读取失败: {}, 将从数据库查询", dictCode, e);
		}

		// 缓存未命中，从数据库查询
		QueryWrapper<SysDictDetail> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("dict_code", dictCode);
		queryWrapper.eq("is_delete", 1);
		queryWrapper.orderByAsc("seq");
		List<SysDictDetail> result = this.list(queryWrapper);

		// 将查询结果写入缓存
		if (result != null && !result.isEmpty()) {
			try {
				redisUtils.set(cacheKey, result, DICT_CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
				log.debug("字典缓存已设置: {}, 记录数: {}", dictCode, result.size());
			} catch (Exception e) {
				log.warn("字典缓存写入失败: {}", dictCode, e);
			}
		}

		return result;
	}

	/**
	 * 清除指定字典编码的缓存
	 * @param dictCode 字典编码
	 */
	private void clearDictCache(String dictCode) {
		if (dictCode == null || dictCode.isEmpty()) {
			return;
		}
		try {
			String cacheKey = DICT_CACHE_PREFIX + dictCode;
			redisUtils.delete(cacheKey);
			log.debug("字典缓存已清除: {}", dictCode);
		} catch (Exception e) {
			log.warn("字典缓存清除失败: {}", dictCode, e);
		}
	}
}
