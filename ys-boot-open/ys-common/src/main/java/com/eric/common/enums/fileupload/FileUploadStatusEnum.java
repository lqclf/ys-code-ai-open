package com.eric.common.enums.fileupload;

import com.eric.common.enums.BaseEnum;
import com.eric.common.module.domain.vo.EnumVo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 文件上传状态枚举
 * @ClassName: FileUploadStatusEnum
 * @author: Eric
 * @date: 2025-09-12
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Getter
@AllArgsConstructor
public enum FileUploadStatusEnum implements BaseEnum {

    UPLOADING(0, "上传中"),
    SUCCESS(1, "上传完成"),
    FAILED(2, "上传失败"),
    PROCESSING(3, "处理中"),
    CANCELLED(4, "已取消");

    public final Integer key;
    public final String value;

    @Override
    public Integer getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    /**
     * 根据代码获取枚举
     */
    public static FileUploadStatusEnum getByCode(Integer key) {
        for (FileUploadStatusEnum status : values()) {
            if (Objects.equals(status.getKey(), key)) {
                return status;
            }
        }
        return FAILED; // 默认返回失败状态
    }
    /**
     * 组装下拉列表数据
     * @return
     */
    public static List<EnumVo> getSelectList() {
        return Arrays.stream(FileUploadStatusEnum.values()).map(e -> new EnumVo(e.getKey(), e.getValue())).collect(Collectors.toList());
    }
}