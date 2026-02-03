package com.eric.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eric.common.module.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 角色信息表-实体映射类
 * @ClassName:   SysRole
 * @author:      liuQ
 * @date:        2025-07-23 14:38:08
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@TableName("sys_role")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysRole extends BaseEntity {

     /**
     * 角色编码
     */
    @TableField("code")
	private String code;
     /**
     * 角色名称
     */
    @TableField("name")
	private String name;
     /**
     * 备注
     */
    @TableField("remark")
	private String remark;
     /**
     * 排序
     */
    @TableField("seq")
	private Integer seq;
     /**
     * 状态 0.禁用 1.正常
     */
    @TableField("status")
	private Integer status;
}
