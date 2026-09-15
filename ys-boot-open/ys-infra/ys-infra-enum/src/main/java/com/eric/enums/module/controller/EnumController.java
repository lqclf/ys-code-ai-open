package com.eric.enums.module.controller;

import com.eric.common.constant.LogConst;
import com.eric.common.enums.system.*;
import com.eric.common.module.domain.vo.EnumVo;
import com.eric.common.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 枚举接口控制器，提供前端获取枚举值列表的统一入口
 * <p>前端通过枚举编码（如 sys_menutype、sys_depttype 等）调用接口，
 * 获取对应的枚举键值对列表，用于下拉框、标签展示等场景</p>
 * <p>注意：本接口只暴露开源版实际包含的枚举。文件上传、通知公告、站内信等
 * 模块属于商业版能力，其枚举不在开源版提供范围内。</p>
 *
 * @ClassName:  EnumController
 * @author:     Eric
 * @date:       2025-08-05 15:34:15
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Tag(name = "枚举接口")
@RestController
@RequestMapping("/enum")
@Slf4j
public class EnumController {

    /** 菜单类型枚举编码 */
    private static final String SYS_MENUTYPE = "sys_menutype";
    /** 日志类型枚举编码 */
    private static final String SYS_LOGTYPE = "sys_logtype";
    /** 日志级别枚举编码 */
    private static final String SYS_LOGLEVEL = "sys_loglevel";
    /** 日志操作类型枚举编码 */
    private static final String SYS_LOGOPERTYPE = "sys_logopertype";
    /** 部门性质枚举编码 */
    private static final String SYS_DEPTTYPE = "sys_depttype";
    /** 通知优先级枚举编码 */
    private static final String NOTICE_PRIORITY = "priority";

    /**
     * 通过编号获取枚举值列表
     * 根据传入的枚举编码，返回对应的枚举值列表，供前端下拉框等组件使用
     * @param code 枚举编码，如 sys_menutype、sys_depttype 等
     * @return 枚举值列表，包含枚举的key和value
     */
    @Operation(summary = "通过编号获取枚举", description = "通过编号获取枚举")
    @GetMapping("/getEnumValue/{code}")
    public ResponseResult<List<EnumVo>> getEnumValue(@PathVariable String code) {
        // 枚举值结果列表
        List<EnumVo> dataList = new ArrayList<>();

        switch (code) {
            case SYS_MENUTYPE:
                // 菜单类型：目录、菜单、按钮
                dataList = MenuTypeEnum.getSelectList();
                break;
            case SYS_LOGTYPE:
                // 日志类型：登录日志、操作日志等
                dataList = LogTypeEnum.getSelectList();
                break;
            case SYS_LOGLEVEL:
                // 日志级别：DEBUG、INFO、WARN、ERROR 等
                dataList = LogLevelEnum.getSelectList();
                break;
            case SYS_LOGOPERTYPE:
                // 日志操作类型：新增、修改、删除等
                dataList = LogConst.SELECT_LIST;
                break;
            case SYS_DEPTTYPE:
                // 部门性质：行政、技术、生产、销售、财务
                dataList = DeptTypeEnum.getSelectList();
                break;
            case NOTICE_PRIORITY:
                // 通知优先级：低、中、高
                dataList = PriorityEnum.getSelectList();
                break;
            default:
                // 未匹配的编码返回空列表
                break;
        }
        return ResponseResult.success(dataList);
    }

}
