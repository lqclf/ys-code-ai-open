package com.eric.module.system.dto;

import com.eric.common.module.domain.BaseEntityDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 部门信息表-DTO层
 * @ClassName:   SysDepartDTO
 * @author:      liuQ
 * @date:        2025-09-10 15:22:32
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysDepartDTO extends BaseEntityDTO {

     /**
     * 部门名称
     */
	private String name;
     /**
     * 部门简称
     */
	private String shortName;
     /**
     * 部门编码
     */
	private String code;
     /**
     * 上级id
     */
	private String pid;
     /**
     * 部门分类(如:公司、部门、小组等)
     */
	private Integer category;
     /**
     * 部门性质(如:行政、技术、生产等)
     */
	private Integer deptType;
     /**
     * 部门层级(如:总部、分公司、部门等)
     */
	private Integer levelType;
     /**
     * 所属地区
     */
	private String area;
     /**
     * 部门邮箱
     */
	private String email;
     /**
     * 邮政编码
     */
	private String zipCode;
     /**
     * 详细地址
     */
	private String address;
     /**
     * 传真号码
     */
	private String fax;
     /**
     * 备注
     */
	private String remark;
     /**
     * 联系人
     */
	private String linkMan;
     /**
     * 联系电话
     */
	private String linkPhone;
     /**
     * 显示顺序(值越小越靠前)
     */
	private Integer seq;
     /**
     * 图标
     */
	private String icon;
     /**
     * 状态 0.禁用 1.正常
     */
	private Integer status;
     /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;
     /**
     * 更新人ID
     */
	private String updateBy;
     /**
     * 更新人名称
     */
	private String updateName;
}
