package com.eric.common.module.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p> 枚举前端键值对 <p>
 * @ClassName:  EnumVo
 * @author:     liuQ
 * @date:       2025-07-22 15:02:49
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnumVo {

    // 枚举值 key
    private Object value;

    // 枚举描述
    private Object label;

}
