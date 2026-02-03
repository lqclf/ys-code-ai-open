package com.eric.common.module.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eric.common.module.domain.query.PageQuery;

import java.io.Serializable;
import java.util.List;


/**
 * 基础服务类
 * @ClassName:  BaseService
 * @author:     liuQ
 * @date:       2025-07-22 15:06:42
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
public interface BaseService<DTO, T> extends IService<T> {

	/**
	 * 新增
	 * @param o Vo对象
	 * @return String
	 */
	String add(DTO dto);
	/**
	 * 删除
	 * @param 对象的主键oid
	 * @return
	 */
	Boolean delete(List<String> ids);

	/**
	 * 修改
	 * @param o Vo对象
	 * @return
	 */
	Boolean update(DTO dto);

	/**
	 * 根据id查询
	 * @param id
	 * @return Vo
	 */
	DTO get(Serializable id);

	/**
	 * 分页查询
	 * @param o  Vo对象
	 * @param page 分页对象
	 * @return Page<Vo>
	 */
	Page<T> pageList(DTO dto, PageQuery page);

	/**
	 * 批量新增
	 * @param list
	 */
	Boolean batchAdd(List<DTO> list);

	/**
	 * 批量更新
	 * @param dataList
	 */
	Boolean batchUpdate(List<DTO> list);
}
