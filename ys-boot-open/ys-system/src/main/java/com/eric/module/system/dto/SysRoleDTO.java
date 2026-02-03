package com.eric.module.system.dto;

import com.eric.common.module.domain.BaseEntityDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 角色信息表-DTO层
 * @ClassName:   SysRoleDTO
 * @author:      liuQ
 * @date:        2025-07-23 14:38:08
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysRoleDTO extends BaseEntityDTO {

     /**
     * 角色编码
     */
	private String code;
     /**
     * 角色名称
     */
	private String name;
     /**
     * 备注
     */
	private String remark;
     /**
     * 排序
     */
	private Integer seq;
     /**    
     * 状态 0.禁用 1.正常
     */
	private Integer status;
}
