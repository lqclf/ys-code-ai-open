package com.eric.excel.core;

/**
 * Excel数据转换器接口
 * 负责数据转换、关联查询、默认值设置等
 *
 * @ClassName:   ExcelConverter
 * @author:      liuQ
 * @param <T> DTO类型
 * @date:        2025-02-27
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@FunctionalInterface
public interface ExcelConverter<T> {

    /**
     * 数据处理与转换
     * 用于：
     * 1. 设置默认值
     * 2. 字段值转换（如名称转ID）
     * 3. 关联数据查询与填充
     * 4. 业务规则计算
     *
     * @param dto 数据对象
     * @param rowNum 行号
     * @return 处理后的数据对象
     */
    T convert(T dto, int rowNum);
}
