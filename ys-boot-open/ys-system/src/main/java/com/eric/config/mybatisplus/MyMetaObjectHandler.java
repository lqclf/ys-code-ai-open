package com.eric.config.mybatisplus;

import cn.dev33.satoken.exception.SaTokenContextException;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.eric.common.constant.GlobalConstant;
import com.eric.common.enums.common.IsDeleteEnum;
import com.eric.common.module.domain.vo.LoginUserVo;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器
 * @ClassName:  MyMetaObjectHandler
 * @author:     liuQ
 * @date:       2025-07-28 15:43:05
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    private static final Logger log = LoggerFactory.getLogger(MyMetaObjectHandler.class);

    /**
     * 新增数据执行
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LoginUserVo loginUser = getCurrentUser();
        if (loginUser != null) {
            if (metaObject.hasSetter("createBy")) {
                this.strictInsertFill(metaObject, "createBy", String.class, loginUser.getId());
            }
            if (metaObject.hasSetter("createName")) {
                this.strictInsertFill(metaObject, "createName", String.class, loginUser.getUserName());
            }
            // 插入时同时填充更新字段
            if (metaObject.hasSetter("updateBy")) {
                this.strictInsertFill(metaObject, "updateBy", String.class, loginUser.getId());
            }
            if (metaObject.hasSetter("updateName")) {
                this.strictInsertFill(metaObject, "updateName", String.class, loginUser.getUserName());
            }
        } else {
            // 定时任务等无用户上下文场景处理
            if (metaObject.hasSetter("createBy")) {
                this.strictInsertFill(metaObject, "createBy", String.class, "system");
            }
            if (metaObject.hasSetter("createName")) {
                this.strictInsertFill(metaObject, "createName", String.class, "系统");
            }
            // 插入时同时填充更新字段
            if (metaObject.hasSetter("updateBy")) {
                this.strictInsertFill(metaObject, "updateBy", String.class, "system");
            }
            if (metaObject.hasSetter("updateName")) {
                this.strictInsertFill(metaObject, "updateName", String.class, "系统");
            }
        }
        if (metaObject.hasSetter("createTime")) {
            this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        }
        if (metaObject.hasSetter("updateTime")) {
            this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        }
        if (metaObject.hasSetter("isDelete")) {
            this.strictInsertFill(metaObject, "isDelete", Boolean.class, IsDeleteEnum.NOT_DELETED.key);
        }
    }

    /**
     * 更新填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        LoginUserVo loginUser = getCurrentUser();
        if (loginUser != null) {
            //增加校验，可能有些表没有这个字段
            if (metaObject.hasSetter("updateBy")) {
                this.strictUpdateFill(metaObject, "updateBy", String.class, loginUser.getId());
            }
            if (metaObject.hasSetter("updateName")) {
                this.strictUpdateFill(metaObject, "updateName", String.class, loginUser.getUserName());
            }
        } else {
            // 定时任务等无用户上下文场景处理
            if (metaObject.hasSetter("updateBy")) {
                this.strictUpdateFill(metaObject, "updateBy", String.class, "system");
            }
            if (metaObject.hasSetter("updateName")) {
                this.strictUpdateFill(metaObject, "updateName", String.class, "系统");
            }
        }
        if (metaObject.hasSetter("updateTime")) {
            this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        }

    }

    private LoginUserVo getCurrentUser() {
        try {
            if (!StpUtil.isLogin()){
                return null;
            }
            return (LoginUserVo) StpUtil.getSession().get(GlobalConstant.SESSION_USERINFO);
        } catch (SaTokenContextException e) {
            // 在定时任务等无用户上下文环境中，SaToken可能会抛出上下文异常
            log.debug("获取当前用户失败，可能在非Web上下文中执行: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("获取当前用户时发生未预期的异常: {}", e.getMessage(), e);
            return null;
        }
    }
}