package com.eric.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eric.common.module.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 部门信息表-实体映射类
 * @ClassName:   SysDepart
 * @author:      liuQ
 * @date:        2025-09-10 15:22:32
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@TableName("sys_depart")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysDepart extends BaseEntity {

     /**
     * 部门名称
     */
    @TableField("name")
	private String name;
     /**
     * 部门简称
     */
    @TableField("short_name")
	private String shortName;
     /**
     * 部门编码
     */
    @TableField("code")
	private String code;
     /**
     * 上级id
     */
    @TableField("pid")
	private String pid;
     /**
     * 部门分类(如:公司、部门、小组等)
     */
    @TableField("category")
	private Integer category;
     /**
     * 部门性质(如:行政、技术、生产等)
     */
    @TableField("dept_type")
	private Integer deptType;
     /**
     * 部门层级(如:总部、分公司、部门等)
     */
    @TableField("level_type")
	private Integer levelType;
     /**
     * 所属地区
     */
    @TableField("area")
	private String area;
     /**
     * 部门邮箱
     */
    @TableField("email")
	private String email;
     /**
     * 邮政编码
     */
    @TableField("zip_code")
	private String zipCode;
     /**
     * 详细地址
     */
    @TableField("address")
	private String address;
     /**
     * 传真号码
     */
    @TableField("fax")
	private String fax;
     /**
     * 备注
     */
    @TableField("remark")
	private String remark;
     /**
     * 联系人
     */
    @TableField("link_man")
	private String linkMan;
     /**
     * 联系电话
     */
    @TableField("link_phone")
	private String linkPhone;
     /**
     * 显示顺序(值越小越靠前)
     */
    @TableField("seq")
	private Integer seq;
     /**
     * 图标
     */
    @TableField("icon")
	private String icon;
     /**
     * 状态 0.禁用 1.正常
     */
    @TableField("status")
	private Integer status;
     /**
     * 更新时间
     */
    @TableField("update_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;
     /**
     * 更新人ID
     */
    @TableField("update_by")
	private String updateBy;
     /**
     * 更新人名称
     */
    @TableField("update_name")
	private String updateName;
}
