package com.eric.excel.context;

/**
 * 数据操作类型枚举
 * 定义Excel数据导入时的操作类型
 *
 * @ClassName: DataOperation
 * @author: liuQ
 * @date: 2025-02-26
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
public enum DataOperation {

    /**
     * 新增操作 - 将数据作为新记录插入数据库
     */
    INSERT,

    /**
     * 更新操作 - 根据主键更新已有记录
     */
    UPDATE,

    /**
     * 删除操作 - 根据主键删除记录
     */
    DELETE,

    /**
     * 自定义操作 - 由业务方自定义处理逻辑
     */
    CUSTOM,

    /**
     * 混合操作 - 根据数据状态自动判断是新增还是更新
     */
    MIXED
}
