package com.eric.excel.validator;

import com.eric.excel.result.ValidationResult;

/**
 * 数据校验器接口
 * @param <T> 数据类型
 * @ClassName: DataValidator
 * @author: liuQ
 * @date: 2025-02-26
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@FunctionalInterface
public interface DataValidator<T> {

    /**
     * 校验数据
     * @param data 数据对象
     * @param rowIndex 行索引
     * @return 校验结果
     */
    ValidationResult validate(T data, Integer rowIndex);
}
