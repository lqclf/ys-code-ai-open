package com.eric.common.constant;

import com.eric.common.module.domain.vo.EnumVo;

import java.util.List;

/**
 * 日志常量类
 * <p>
 * 定义操作类型常量以及供前端下拉选择使用的枚举列表。
 * </p>
 *
 * @ClassName:  LogConst
 * @author:     Eric
 * @date:       2025-07-22 11:00:36
 * @Copyright   ERIC 微信公众号：忆笙的技术杂货库
 */
public class LogConst {

    /** 新增操作 */
    public final static String ADD = "新增";
    /** 更新操作 */
    public final static String UPDATE = "更新";
    /** 删除操作 */
    public final static String DELETE = "删除";
    /** 查询操作 */
    public final static String SELECT = "查询";
    /** 导入操作 */
    public final static String IMPORT = "导入";
    /** 导出操作 */
    public final static String EXPORT = "导出";
    /** 其他操作 */
    public final static String OTHER = "其他";

    /** 操作类型枚举列表，供前端下拉框使用 */
    public static final List<EnumVo> SELECT_LIST = List.of(
            new EnumVo(ADD, "新增"),
            new EnumVo(UPDATE, "更新"),
            new EnumVo(DELETE, "删除"),
            new EnumVo(SELECT, "查询"),
            new EnumVo(IMPORT, "导入"),
            new EnumVo(EXPORT, "导出"),
            new EnumVo(OTHER, "其他")
    );
}
