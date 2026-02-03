package com.eric.common.module.domain.query;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 分页查询参数
 * @ClassName:  PageQuery
 * @author:     liuQ
 * @date:       2025-07-22 14:11:09
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Data
public class PageQuery implements java.io.Serializable {

	//当前页码 默认1
	@NotNull(message = "当前页码不能为空")
    private Integer pageNo;// 当前页

	//每页显示记录数
	@NotNull(message = "每页显示记录数不能为空")
	private Integer pageSize;

	//是否降序
	public boolean desc;

	//降序字段
	public List<String> descList;

	//是否升序
	public boolean asc;
	//升序字段
	public List<String> ascList;

}
