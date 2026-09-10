package com.eric.dict.module.service;

import com.eric.common.module.service.BaseService;
import com.eric.dict.module.dto.SysDictDetailDTO;
import com.eric.dict.module.entity.SysDictDetail;

import java.util.List;

/**
 * 字典管理明细表 服务类
 * @ClassName:  SysDictDetailService
 * @author:     liuQ
 * @date:       2025-07-23 14:38:08
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
public interface SysDictDetailService extends BaseService<SysDictDetailDTO, SysDictDetail> {

	/**
	 * 根据字典编码查询字典明细列表
	 * @param dictCode 字典编码
	 * @return 字典明细列表
	 */
	List<SysDictDetail> selectByDictCode(String dictCode);

}
