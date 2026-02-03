package com.eric.common.enums.system;

import com.eric.common.enums.BaseEnum;
import com.eric.common.module.domain.vo.EnumVo;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 菜单类型枚举
 */
@Getter
public enum MenuTypeEnum implements BaseEnum {

    CATALOG(1, "目录"),
    MENU(2, "菜单"),
    BUTTON(3, "按钮");

    public final Integer key;
    public final String value;

    MenuTypeEnum(Integer key, String value) {
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
    public static MenuTypeEnum getByKey(Integer key) {
        for (MenuTypeEnum type : values()) {
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
        return Arrays.stream(MenuTypeEnum.values()).map(e -> new EnumVo(e.getKey(), e.getValue())).collect(Collectors.toList());
    }
} 