package com.eric.module.system.dto;

import com.eric.common.module.domain.BaseEntityDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 系统配置-DTO层
 * @ClassName:   SysConfigDTO
 * @author:      liuQ
 * @date:        2025-09-11 15:29:29
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysConfigDTO extends BaseEntityDTO {

     /**
     * 显示名称
     */
	private String name;
     /**
     * 编码
     */
	private String code;
     /**
     * 值，任意字符串
     */
	private String value;
     /**
     * 使用说明
     */
	private String remark;
     /**
     * 分类，备用扩展字段
     */
	private String category;
     /**
     * 过滤，备用扩展字段
     */
	private String filter;
}
