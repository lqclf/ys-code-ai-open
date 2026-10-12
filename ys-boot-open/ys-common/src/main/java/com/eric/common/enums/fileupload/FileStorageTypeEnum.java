package com.eric.common.enums.fileupload;

import com.eric.common.enums.BaseEnum;
import com.eric.common.module.domain.vo.EnumVo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件存储类型枚举
 * @ClassName: FileStorageTypeEnum
 * @author: Eric
 * @date: 2025-09-12
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Getter
@AllArgsConstructor
public enum FileStorageTypeEnum implements BaseEnum {
    /**
     * 文件存储类型枚举
     */
    LOCAL("local", "本地存储"),
    MINIO("minio", "MinIO对象存储");


    public final String key;
    public final String value;

    @Override
    public Object getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    /**
     * 根据代码获取枚举
     */
    public static FileStorageTypeEnum getByCode(String key) {
        for (FileStorageTypeEnum type : values()) {
            if (type.getKey().equals(key)) {
                return type;
            }
        }
        return LOCAL; // 默认返回本地存储
    }

    /**
     * 组装下拉列表数据
     * @return
     */
    public static List<EnumVo> getSelectList() {
        return Arrays.stream(FileStorageTypeEnum.values()).map(e -> new EnumVo(e.getKey(), e.getValue())).collect(Collectors.toList());
    }
}