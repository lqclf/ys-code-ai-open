package com.eric.common.module.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eric.common.enums.common.IsDeleteEnum;
import com.eric.common.module.domain.query.PageQuery;


/**
 * 基础服务实现类
 * @ClassName:  BaseServiceImpl
 * @author:     liuQ
 * @date:       2025-07-22 15:07:48
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T>{

	public QueryWrapper<T> whereQuery(PageQuery page) {
		QueryWrapper<T> query = new QueryWrapper<>();
		//判断是否存在is_delete字段
        getEntityClass();
        if (getEntityClass().getDeclaredFields().length > 0) {
			for (int i = 0; i < getEntityClass().getDeclaredFields().length; i++) {
				if ("is_delete".equals(getEntityClass().getDeclaredFields()[i].getName())) {
					query.eq("is_delete", IsDeleteEnum.NOT_DELETED.getKey());
				}
			}
		}
		if (page != null) {
			if(page.isDesc() && page.getDescList() != null && !page.getDescList().isEmpty()) {//包含降序
				query.orderByDesc(page.getDescList());
			}
			if(page.isAsc() && page.getAscList() != null && !page.getAscList().isEmpty()) {//包含降序
				query.orderByAsc(page.getAscList());
			}
		}
		return query;
	}
}