package com.eric.dict.module.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eric.common.module.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 字典管理表-实体映射类
 * @ClassName:   SysDict
 * @author:      liuQ
 * @date:        2025-07-23 14:38:08
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@TableName("sys_dict")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name ="SysDict", description = "字典管理表")
public class SysDict extends BaseEntity {

     /**
     * 编码
     */
    @TableField("code")
    @Schema(description = "编码")
	private String code;
     /**
     * 名称
     */
    @TableField("name")
    @Schema(description = "名称")
	private String name;
     /**
     * 描述
     */
    @TableField("descript")
    @Schema(description = "描述")
	private String descript;
     /**
     * 状态 0.禁用 1.正常
     */
    @TableField("status")
    @Schema(description = "状态 0.禁用 1.正常")
	private Integer status;
}
