package com.eric.excel.core;

import com.eric.excel.context.PersistContext;
import com.eric.excel.context.ProcessContext;
import com.eric.excel.result.ImportResult;

import java.util.List;
import java.util.Map;

/**
 * 导入处理器接口
 * 整合校验、转换、持久化三个阶段的处理逻辑
 *
 * @ClassName:   ImportHandler
 * @author:      liuQ
 * @param <R> 原始DTO类型（ImportDTO，从Excel读取的DTO）
 * @param <T> 业务DTO类型（BusinessDTO，转换后与数据库交互的DTO）
 * @date:        2025-02-27
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
public interface ImportHandler<R, T> {

    /**
     * 获取原始DTO类
     */
    Class<R> getDtoClass();

    /**
     * 校验单条数据（可选实现）
     * 默认实现：校验通过
     *
     * @param dto    数据对象
     * @param rowNum 行号
     * @return 校验结果
     */
    default com.eric.excel.result.ValidationResult validate(R dto, int rowNum) {
        return com.eric.excel.result.ValidationResult.success();
    }

    /**
     * 校验前置准备（在循环校验之前调用一次）
     * 用于预加载数据库数据等上下文信息，避免在校验循环中逐条查询数据库（N+1问题）
     *
     * @param dataList 待校验的数据列表
     * @return 校验上下文数据，默认返回空Map
     */
    default Map<String, Object> prepareValidate(List<R> dataList) {
        return java.util.Collections.emptyMap();
    }

    /**
     * 单条数据校验（携带预加载的上下文数据）
     * <p>
     * 用户只需实现此方法或{@link #validate(Object, int)}其中之一即可：
     * <ul>
     *   <li>需要使用预加载数据库数据校验 → 实现此方法</li>
     *   <li>不需要预加载数据 → 实现{@link #validate(Object, int)}</li>
     * </ul>
     *
     * @param dto     数据对象
     * @param rowNum  行号
     * @param context 预加载的上下文数据（来自{@link #prepareValidate(List)}）
     * @return 校验结果
     */
    default com.eric.excel.result.ValidationResult validate(R dto, int rowNum, Map<String, Object> context) {
        return validate(dto, rowNum);
    }

    /**
     * 数据处理与转换
     * 在此方法中对整个数据列表进行处理，包括：
     * - 数据分类（新增、更新、删除等）
     * - 数据转换、默认值填充
     * - 关联数据查询
     * - 复杂业务逻辑处理
     *
     * @param dataList 数据列表
     * @return 处理上下文，包含分类后的数据列表
     */
    ProcessContext<R, T> processData(List<R> dataList);

    /**
     * 数据持久化
     * 支持多种操作类型：插入、更新、删除、自定义操作
     *
     * @param context 处理上下文，包含分类后的数据列表
     * @return 持久化上下文，包含操作结果统计
     */
    PersistContext persist(ProcessContext<R, T> context);

    /**
     * 导入前置准备（接收业务特有参数）
     * 在 import 之前由统一控制器调用
     *
     * @param params 额外参数（来自前端请求，如 departId=xxx）
     */
    default void beforeImport(Map<String, String> params) {
    }

    /**
     * 获取导入所需的权限标识
     * 由统一控制器在调用前进行校验
     *
     * @return 权限标识，如 "system:user:import"，返回null表示不需要权限校验
     */
    default String getPermissionCode() {
        return null;
    }

    /**
     * 获取导入文件名（用于错误数据下载时的文件名）
     *
     * @return 文件名（不含扩展名），如 "用户信息"
     */
    default String getFileName() {
        return "导入数据";
    }
}
