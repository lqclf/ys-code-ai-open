package com.eric.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eric.common.module.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 系统配置-实体映射类
 * @ClassName:   SysConfig
 * @author:      liuQ
 * @date:        2025-09-11 15:29:29
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@TableName("sys_config")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysConfig extends BaseEntity {

     /**
     * 显示名称
     */
    @TableField("name")
	private String name;
     /**
     * 编码
     */
    @TableField("code")
	private String code;
     /**
     * 值，任意字符串
     */
    @TableField("value")
	private String value;
     /**
     * 使用说明
     */
    @TableField("remark")
	private String remark;
     /**
     * 分类，备用扩展字段
     */
    @TableField("category")
	private String category;
     /**
     * 过滤，备用扩展字段
     */
    @TableField("filter")
	private String filter;
}
