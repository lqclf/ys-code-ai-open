package com.eric.enums.module.controller;

import com.eric.common.constant.LogConst;
import com.eric.common.enums.fileupload.FileStorageTypeEnum;
import com.eric.common.enums.fileupload.FileTypeEnum;
import com.eric.common.enums.fileupload.FileUploadStatusEnum;
import com.eric.common.enums.message.MessageBizTypeEnum;
import com.eric.common.enums.message.MessageTypeEnum;
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
 * <p>前端通过枚举编码（如 sys_menutype、file_storagetype 等）调用接口，
 * 获取对应的枚举键值对列表，用于下拉框、标签展示等场景</p>
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
    /** 日志操作类型枚举编码 */
    private static final String SYS_LOGOPERTYPE = "sys_logopertype";

    /** 文件存储类型枚举编码 */
    private static final String FILE_STORAGE_TYPE = "file_storagetype";
    /** 文件上传状态枚举编码 */
    private static final String FILE_UPLOAD_STATUS = "file_uploadstatus";
    /** 文件类型枚举编码 */
    private static final String FILE_TYPE = "file_type";

    /** 通知公告阅读状态枚举编码 */
    private static final String READ_STATUS = "read_status";
    /** 通知公告优先级枚举编码 */
    private static final String NOTICE_PRIORITY = "priority";
    /** 通知公告发布状态枚举编码 */
    private static final String NOTICE_STATUS = "notice_status";
    /** 通知公告接收用户类型枚举编码 */
    private static final String RECEIVE_USER_TYPE = "receive_user_type";

    /** 站内信消息类型枚举编码 */
    private static final String MESSAGE_TYPE = "message_type";
    /** 站内信消息业务类型枚举编码 */
    private static final String MESSAGE_BIZ_TYPE = "message_biz_type";

    /**
     * 通过编号获取枚举值列表
     * 根据传入的枚举编码，返回对应的枚举值列表，供前端下拉框等组件使用
     * @param code 枚举编码，如 sys_menutype、file_storagetype 等
     * @return 枚举值列表，包含枚举的key和value
     */
    @Operation(summary = "通过编号获取枚举", description = "通过编号获取枚举")
    @GetMapping("/getEnumValue/{code}")
    public ResponseResult<List<EnumVo>> getEnumValue(@PathVariable String code) {
        // 枚举值结果列表
        List<EnumVo> dataList = new ArrayList<>();

        switch (code) {
            // === 系统管理模块枚举 ===
            case SYS_MENUTYPE:
                // 菜单类型：目录、菜单、按钮
                dataList = MenuTypeEnum.getSelectList();
                break;
            case SYS_LOGTYPE:
                // 日志类型：登录日志、操作日志等
                dataList = LogTypeEnum.getSelectList();
                break;
            case SYS_LOGOPERTYPE:
                // 日志操作类型：新增、修改、删除等
                dataList = LogConst.SELECT_LIST;
                break;

            // === 文件上传模块枚举 ===
            case FILE_STORAGE_TYPE:
                // 文件存储类型：本地、MinIO、阿里云OSS等
                dataList = FileStorageTypeEnum.getSelectList();
                break;
            case FILE_UPLOAD_STATUS:
                // 文件上传状态：上传中、成功、失败等
                dataList = FileUploadStatusEnum.getSelectList();
                break;
            case FILE_TYPE:
                // 文件类型：图片、文档、视频等
                dataList = FileTypeEnum.getSelectList();
                break;

            // === 通知公告模块枚举 ===
            case READ_STATUS:
                // 阅读状态：未读、已读
                dataList = ReadStatusEnum.getSelectList();
                break;
            case NOTICE_PRIORITY:
                // 通知优先级：低、中、高
                dataList = PriorityEnum.getSelectList();
                break;
            case NOTICE_STATUS:
                // 通知发布状态：草稿、已发布、已撤回
                dataList = PublishStatusEnum.getSelectList();
                break;
            case RECEIVE_USER_TYPE:
                // 接收用户类型：全部用户、指定用户、指定部门
                dataList = ReceiveUserTypeEnum.getSelectList();
                break;

            // === 站内信模块枚举 ===
            case MESSAGE_TYPE:
                // 消息类型：系统通知、业务提醒等
                dataList = MessageTypeEnum.getSelectList();
                break;
            case MESSAGE_BIZ_TYPE:
                // 消息业务类型：订单、审批、告警等
                dataList = MessageBizTypeEnum.getSelectList();
                break;
            default:
                // 未匹配的编码返回空列表
                break;
        }
        return ResponseResult.success(dataList);
    }

}
