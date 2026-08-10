package com.eric.log.module.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.common.annotation.log.MyLog;
import com.eric.common.constant.LogConst;
import com.eric.common.enums.system.LogLevelEnum;
import com.eric.common.enums.system.LogTypeEnum;
import com.eric.common.module.controller.BaseController;
import com.eric.common.module.domain.query.PageQuery;
import com.eric.common.module.domain.vo.EnumVo;
import com.eric.common.response.ResponseResult;
import com.eric.log.module.dto.SysLoggerDTO;
import com.eric.log.module.entity.SysLogger;
import com.eric.log.module.service.SysLoggerService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统日志Controller
 * <p>
 * 提供日志的分页查询、详情查询、删除以及枚举字典查询功能。
 * 日志数据由 AOP 切面自动采集，因此不对外提供手工新增入口。
 * </p>
 *
 * @ClassName:  SysLoggerController
 * @author:     Eric
 * @date:       2025-01-17
 * @Copyright   ERIC 微信公众号：忆笙的技术杂货库
 */
@RestController
@RequestMapping("/system/logger")
public class SysLoggerController extends BaseController<SysLogger, SysLoggerService> {

    /**
     * 分页查询系统日志
     * <p>
     * 支持按日志类型、日志级别、模块、操作类型、用户名等条件组合查询，
     * 默认按操作时间倒序排列，保证最新日志展示在最前。
     * </p>
     *
     * @param dto  日志查询条件对象
     * @param page 分页参数对象
     * @return 分页结果，包含日志列表
     */
    @GetMapping("/list")
    @SaCheckPermission("system:logger:list")
    public ResponseResult<Page<SysLogger>> list(SysLoggerDTO dto, PageQuery page) {
        // 默认按操作时间倒序展示
        page.setDescList(List.of("log_time"));
        page.setDesc(true);
        return ResponseResult.success(service.pageList(dto, page));
    }

    /**
     * 查询日志相关枚举字典
     * <p>
     * 返回日志类型、日志级别、操作类型三组下拉数据，供前端筛选条件与列展示使用。
     * </p>
     *
     * @return 枚举字典集合，key 分别为 logType、logLevel、operType
     */
    @GetMapping("/enums")
    public ResponseResult<Map<String, List<EnumVo>>> enums() {
        Map<String, List<EnumVo>> result = new HashMap<>(4);
        result.put("logType", LogTypeEnum.getSelectList());
        result.put("logLevel", LogLevelEnum.getSelectList());
        result.put("operType", LogConst.SELECT_LIST);
        return ResponseResult.success(result);
    }

    /**
     * 删除系统日志
     *
     * @param ids 日志ID列表
     * @return 删除是否成功
     */
    @MyLog(operModul = "系统日志", operType = LogConst.DELETE, operDesc = "删除日志")
    @DeleteMapping("/delete/{ids}")
    @SaCheckPermission("system:logger:delete")
    public ResponseResult<Boolean> delete(@PathVariable List<String> ids) {
        return ResponseResult.success(service.delete(ids));
    }

    /**
     * 根据ID查询系统日志详情
     *
     * @param id 日志ID
     * @return 日志详细信息
     */
    @GetMapping("/{id}")
    public ResponseResult<SysLoggerDTO> get(@PathVariable("id") String id) {
        return ResponseResult.success(service.get(id));
    }
}
