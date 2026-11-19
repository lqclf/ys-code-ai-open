package com.eric.excel.core;

import com.eric.excel.context.PersistContext;

import java.util.List;

/**
 * Excel数据持久化接口
 * 负责将处理后的数据保存到数据库
 *
 * @ClassName:   ExcelPersister
 * @author:      liuQ
 * @param <T> DTO类型
 * @date:        2025-02-27
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@FunctionalInterface
public interface ExcelPersister<T> {

    /**
     * 数据持久化
     * 支持多种操作类型：插入、更新、删除、自定义操作
     *
     * @param dataList 数据列表（已转换和填充）
     * @return 持久化上下文，包含操作结果统计
     */
    PersistContext persist(List<T> dataList);
}
