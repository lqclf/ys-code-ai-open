package com.eric.dict.module.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eric.common.module.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 字典管理明细表-实体映射类
 * @ClassName:   SysDictDetail
 * @author:      liuQ
 * @date:        2025-07-23 14:38:08
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@TableName("sys_dict_detail")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name ="SysDictDetail", description = "字典管理明细表")
public class SysDictDetail extends BaseEntity {

     /**
     * 字典主表id
     */
    @TableField("dict_id")
    @Schema(description = "字典主表id")
	private String dictId;
     /**
     * 字典主表编码
     */
    @TableField("dict_code")
    @Schema(description = "字典主表编码")
	private String dictCode;
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
     * 标签类型
     */
    @TableField("tag_type")
    @Schema(description = "标签类型（primary/success/warning/danger/info）")
	private String tagType;
     /**
     * 序号
     */
    @TableField("seq")
    @Schema(description = "序号")
	private Integer seq;
}
