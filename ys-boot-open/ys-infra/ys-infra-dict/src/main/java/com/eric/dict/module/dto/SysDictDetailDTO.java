package com.eric.dict.module.dto;

import com.eric.common.module.domain.BaseEntityDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典管理明细表-DTO层
 * @ClassName:   SysDictDetailDTO
 * @author:      liuQ
 * @date:        2025-07-23 14:38:08
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name ="SysDictDetailDTO", description = "字典管理明细表")
public class SysDictDetailDTO extends BaseEntityDTO {

     /**
     * 字典主表id
     */
    @Schema(description = "字典主表id")
	private String dictId;
     /**
     * 字典主表编码
     */
    @Schema(description = "字典主表编码")
	private String dictCode;
     /**
     * 编码
     */
    @Schema(description = "编码")
	private String code;
     /**
     * 名称
     */
    @Schema(description = "名称")
	private String name;
     /**
     * 标签类型
     */
    @Schema(description = "标签类型（primary/success/warning/danger/info）")
	private String tagType;
     /**
     * 序号
     */
    @Schema(description = "序号")
	private Integer seq;
}
