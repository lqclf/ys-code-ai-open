package com.eric.common.enums.system;

import com.eric.common.enums.BaseEnum;
import com.eric.common.module.domain.vo.EnumVo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 优先级枚举
 * @ClassName:  PriorityEnum
 * @author:     liuQ
 * @date:       2025-07-19
 * @Copyright   ERIC 微信公众号：Eric技术杂货库
 */
public enum PriorityEnum implements BaseEnum {

    LOW(0, "低"),
    MEDIUM(1, "中"),
    HIGH(2, "高");

    public final Integer key;
    public final String value;

    PriorityEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Object getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    /**
     * 组装下拉列表数据
     * @return
     */
    public static List<EnumVo> getSelectList() {
        return Arrays.stream(PriorityEnum.values()).map(e -> new EnumVo(e.getKey(), e.getValue())).collect(Collectors.toList());
    }
}
