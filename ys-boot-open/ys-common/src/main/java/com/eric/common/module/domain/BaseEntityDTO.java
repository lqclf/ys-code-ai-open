package com.eric.common.module.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公共基础entity
 * @ClassName:  BaseEntity
 * @author:     liuQ
 * @date:       2025-06-16 14:52:21
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
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
}