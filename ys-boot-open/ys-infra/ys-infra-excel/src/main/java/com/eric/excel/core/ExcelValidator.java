package com.eric.excel.core;

import com.eric.excel.result.ValidationResult;

/**
 * Excel数据校验器接口
 * 负责单条数据的校验逻辑
 *
 * @ClassName:   ExcelValidator
 * @author:      liuQ
 * @param <T> DTO类型
 * @date:        2025-02-27
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@FunctionalInterface
public interface ExcelValidator<T> {

    /**
     * 校验单条数据
     *
     * @param dto 数据对象
     * @param rowNum 行号（从2开始，第1行是表头）
     * @return 校验结果
     */
    ValidationResult validate(T dto, int rowNum);
}
