package com.eric.dict.module.dto;

import com.eric.common.module.domain.BaseEntityDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典管理表-DTO层
 * @ClassName:   SysDictDTO
 * @author:      liuQ
 * @date:        2025-07-23 14:38:08
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name ="SysDictDTO", description = "字典管理表")
public class SysDictDTO extends BaseEntityDTO {

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
     * 描述
     */
    @Schema(description = "描述")
	private String descript;
     /**
     * 状态 0.禁用 1.正常
     */
    @Schema(description = "状态 0.禁用 1.正常")
	private Integer status;
}
