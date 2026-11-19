package com.eric.common.module.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 公共基础entity
 * @ClassName:  BaseEntity
 * @author:     liuQ
 * @date:       2025-06-16 14:52:21
 * @Copyright   ERIC 微信公众号：忆笙的技术杂货库
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BaseEntityDTO implements Serializable {

    /**
     * 主键id
     */
    private String id;
    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 创建人id
     */
    private String createBy;
    /**
     * 创建人名称
     */
    private String createName;
    /**
     * 删除状态 0已删除 1正常
     */
    private Boolean isDelete;

    // ==================== 导入辅助字段 ====================

    /**
     * 行号（用于导入时标记错误位置）
     */
    private Integer rowNum;

    /**
     * 错误信息（用于标记导入错误）
     */
    private String errorMsg;

    /**
     * 是否有效（用于导入预览）
     */
    private Boolean valid;

    /**
     * 导出时选中的字段列表
     */
    private List<String> selectedFields;
}