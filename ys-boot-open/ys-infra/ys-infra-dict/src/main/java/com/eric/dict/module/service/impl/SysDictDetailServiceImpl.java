package com.eric.dict.module.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.common.module.domain.query.PageQuery;
import com.eric.common.module.service.impl.BaseServiceImpl;
import com.eric.common.utils.BeanCopyUtils;
import com.eric.dict.module.dto.SysDictDetailDTO;
import com.eric.dict.module.entity.SysDictDetail;
import com.eric.dict.module.mapper.SysDictDetailMapper;
import com.eric.dict.module.service.SysDictDetailService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字典管理明细表 服务实现类
 * <p>
 * 缓存说明：本实现使用进程内缓存（ConcurrentHashMap）缓存按 dictCode 查询的字典明细，
 * 不依赖 Redis，保证开源版在零中间件环境下开箱即用。
 * 字典项发生变更时同步清理缓存，保证接口立即返回最新数据。
 * </p>
 *
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
	 * 字典缓存过期时间（毫秒），默认 24 小时
	 */
	private static final long DICT_CACHE_EXPIRE_MILLIS = 24 * 60 * 60 * 1000L;

	/**
	 * 进程内字典明细缓存：key = 字典编码，value = 该字典下的明细列表
	 */
	private static final Map<String, List<SysDictDetail>> DETAIL_CACHE = new ConcurrentHashMap<>(64);

	/**
	 * 缓存过期时间戳：key = 字典编码，value = 过期时间（毫秒）
	 */
	private static final Map<String, Long> DETAIL_CACHE_EXPIRE_AT = new ConcurrentHashMap<>(64);

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
		if (oldDictCode == null || !oldDictCode.equals(o.getDictCode())) {
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
			clearDictCacheBatch(collectDictCodes(dataList));
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
			clearDictCacheBatch(collectDictCodes(dataList));
			return result;
		}
		return false;
	}

	/**
	 * 根据字典编码查询字典明细列表（带进程内缓存）
	 * 查询策略：先查缓存，缓存未命中或已过期再查数据库并写入缓存
	 * @param dictCode 字典编码
	 * @return 字典明细列表
	 */
	@Override
	public List<SysDictDetail> selectByDictCode(String dictCode) {
		if (dictCode == null || dictCode.isEmpty()) {
			return new ArrayList<>();
		}

		List<SysDictDetail> cachedList = DETAIL_CACHE.get(dictCode);
		Long expireAt = DETAIL_CACHE_EXPIRE_AT.get(dictCode);
		long now = System.currentTimeMillis();
		if (cachedList != null && expireAt != null && expireAt > now) {
			log.debug("字典缓存命中: {}", dictCode);
			// 返回副本，避免调用方修改污染缓存
			return new ArrayList<>(cachedList);
		}

		// 缓存未命中或已过期，从数据库查询
		QueryWrapper<SysDictDetail> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("dict_code", dictCode);
		queryWrapper.eq("is_delete", 1);
		queryWrapper.orderByAsc("seq");
		List<SysDictDetail> result = this.list(queryWrapper);

		// 将查询结果写入缓存
		if (result != null && !result.isEmpty()) {
			DETAIL_CACHE.put(dictCode, new ArrayList<>(result));
			DETAIL_CACHE_EXPIRE_AT.put(dictCode, now + DICT_CACHE_EXPIRE_MILLIS);
			log.debug("字典缓存已设置: {}, 记录数: {}", dictCode, result.size());
		} else {
			// 查询结果为空时也要清理，避免已删除的字典残留
			DETAIL_CACHE.remove(dictCode);
			DETAIL_CACHE_EXPIRE_AT.remove(dictCode);
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
			DETAIL_CACHE.remove(dictCode);
			DETAIL_CACHE_EXPIRE_AT.remove(dictCode);
			log.debug("字典缓存已清除: {}", dictCode);
		} catch (Exception e) {
			log.warn("字典缓存清除失败: {}", dictCode, e);
		}
	}

	/**
	 * 批量清除多个字典编码的缓存
	 * @param dictCodes 字典编码列表
	 */
	private void clearDictCacheBatch(List<String> dictCodes) {
		for (String dictCode : dictCodes) {
			if (dictCode == null || dictCode.isEmpty()) {
				continue;
			}
			DETAIL_CACHE.remove(dictCode);
			DETAIL_CACHE_EXPIRE_AT.remove(dictCode);
		}
		if (!dictCodes.isEmpty()) {
			log.debug("字典缓存已批量清除: {}", dictCodes);
		}
	}

	/**
	 * 从明细列表中提取去重的字典编码
	 * @param dataList 字典明细列表
	 * @return 去重的字典编码
	 */
	private List<String> collectDictCodes(List<SysDictDetailDTO> dataList) {
		List<String> dictCodes = new ArrayList<>();
		for (SysDictDetailDTO dto : dataList) {
			if (dto.getDictCode() != null && !dictCodes.contains(dto.getDictCode())) {
				dictCodes.add(dto.getDictCode());
			}
		}
		return dictCodes;
	}
}
