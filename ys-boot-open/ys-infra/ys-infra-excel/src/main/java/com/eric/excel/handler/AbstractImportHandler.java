package com.eric.excel.handler;

import com.eric.excel.context.PersistContext;
import com.eric.excel.context.ProcessContext;
import com.eric.excel.core.ImportHandler;

import java.util.List;

/**
 * 导入处理器抽象类
 * 提供通用的导入流程实现，子类只需实现特定的业务逻辑
 *
 * @author Eric
 * @param <R> 原始DTO类型（ImportDTO，从Excel读取的DTO）
 * @param <T> 业务DTO类型（BusinessDTO，转换后与数据库交互的DTO）
 * @date 2025-02-27
 */
public abstract class AbstractImportHandler<R, T> implements ImportHandler<R, T> {

    /**
     * 数据处理与转换
     * 子类可重写此方法实现自定义数据处理逻辑
     * 包括：数据分类、转换、默认值填充、关联数据查询等
     *
     * @param dataList 数据列表
     * @return 处理上下文，包含分类后的数据列表
     */
    @Override
    public abstract ProcessContext<R, T> processData(List<R> dataList);

    /**
     * 数据持久化
     * 子类必须实现此方法
     * 支持多种操作类型：插入、更新、删除、自定义操作
     *
     * @param context 处理上下文，包含分类后的数据列表
     * @return 持久化上下文，包含操作结果统计
     */
    @Override
    public abstract PersistContext persist(ProcessContext<R, T> context);
}
