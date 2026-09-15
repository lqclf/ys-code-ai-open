package com.eric.common.enums.system;

import com.eric.common.enums.BaseEnum;
import com.eric.common.module.domain.vo.EnumVo;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 部门性质枚举
 * <p>
 * 对应 sys_depart.dept_type 字段（部门性质，如：行政、技术、生产等）。
 * 前端通过 /enum/getEnumValue/sys_depttype 获取该枚举的下拉选项。
 * 若业务上有更多部门性质，直接在此扩展即可。
 * </p>
 */
@Getter
public enum DeptTypeEnum implements BaseEnum {

    ADMINISTRATIVE(1, "行政"),
    TECHNICAL(2, "技术"),
    PRODUCTION(3, "生产"),
    SALES(4, "销售"),
    FINANCE(5, "财务");

    public final Integer key;
    public final String value;

    DeptTypeEnum(Integer key, String value) {
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
     * 根据code获取枚举
     */
    public static DeptTypeEnum getByKey(Integer key) {
        for (DeptTypeEnum type : values()) {
            if (Objects.equals(type.getKey(), key)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 组装下拉列表数据
     * @return
     */
    public static List<EnumVo> getSelectList() {
        return Arrays.stream(DeptTypeEnum.values()).map(e -> new EnumVo(e.getKey(), e.getValue())).collect(Collectors.toList());
    }
}
