package com.eric.log.module.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.common.module.domain.query.PageQuery;
import com.eric.common.module.service.impl.BaseServiceImpl;
import com.eric.common.utils.BeanCopyUtils;
import com.eric.log.module.dto.SysLoggerDTO;
import com.eric.log.module.entity.SysLogger;
import com.eric.log.module.mapper.SysLoggerMapper;
import com.eric.log.module.service.SysLoggerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统日志服务实现类
 * <p>
 * 提供日志的增删改查和批量操作功能。
 * </p>
 *
 * @ClassName:  SysLoggerServiceImpl
 * @author:     Eric
 * @date:       2025-01-17
 * @Copyright   ERIC 微信公众号：忆笙的技术杂货库
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysLoggerServiceImpl extends BaseServiceImpl<SysLoggerMapper, SysLogger> implements SysLoggerService {

    /** 批量插入或更新时的批次大小 */
    private static final int BATCH_SIZE = 1000;

    /**
     * 新增系统日志
     *
     * @param o 日志数据传输对象
     * @return 新增日志的ID
     */
    @Override
    public String add(SysLoggerDTO o) {
        // 将DTO转换为实体类
        SysLogger sysLogger = BeanCopyUtils.copyObj(o, SysLogger.class);
        // 保存到数据库
        save(sysLogger);
        return sysLogger.getId();
    }

    /**
     * 批量删除系统日志
     *
     * @param ids 日志ID列表
     * @return 删除是否成功
     */
    @Override
    public Boolean delete(List<String> ids) {
        return removeByIds(ids);
    }

    /**
     * 更新系统日志
     *
     * @param o 日志数据传输对象
     * @return 更新是否成功
     */
    @Override
    public Boolean update(SysLoggerDTO o) {
        // 根据ID查询现有日志记录
        SysLogger sysLogger = baseMapper.selectById(o.getId());
        // 将DTO属性复制到实体类
        BeanUtils.copyProperties(o, sysLogger);
        return updateById(sysLogger);
    }

    /**
     * 根据ID查询系统日志
     *
     * @param id 日志ID
     * @return 日志数据传输对象
     */
    @Override
    public SysLoggerDTO get(Serializable id) {
        SysLogger sysLogger = baseMapper.selectById(id);
        return BeanCopyUtils.copyObj(sysLogger, SysLoggerDTO.class);
    }

    /**
     * 分页查询系统日志
     * <p>
     * 支持按日志类型、日志级别、模块、操作类型、用户名等条件组合查询。
     * </p>
     *
     * @param dto  日志查询条件对象
     * @param page 分页参数对象
     * @return 分页结果，包含日志列表
     */
    @Override
    public Page<SysLogger> pageList(SysLoggerDTO dto, PageQuery page) {
        // 构建基础查询条件（含逻辑删除与排序）
        QueryWrapper<SysLogger> query = whereQuery(page);
        // 按日志类型列表查询（登录/登出页签会同时查询两种类型）
        query.in(dto.getLogTypes() != null && !dto.getLogTypes().isEmpty(), "log_type", dto.getLogTypes());
        // 按单一日志类型查询
        query.eq(dto.getLogType() != null, "log_type", dto.getLogType());
        // 按日志级别查询
        query.eq(dto.getLogLevel() != null, "log_level", dto.getLogLevel());
        // 按模块名称模糊查询
        query.like(StringUtils.isNotBlank(dto.getModul()), "modul", dto.getModul());
        // 按操作类型模糊查询
        query.like(StringUtils.isNotBlank(dto.getType()), "type", dto.getType());
        // 按用户名模糊查询
        query.like(StringUtils.isNotBlank(dto.getUserName()), "user_name", dto.getUserName());

        return page(Page.of(page.getPageNo(), page.getPageSize()), query);
    }

    /**
     * 批量新增系统日志
     *
     * @param dataList 日志数据传输对象列表
     * @return 批量新增是否成功
     */
    @Override
    public Boolean batchAdd(List<SysLoggerDTO> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return false;
        }
        List<SysLogger> list = new ArrayList<>(dataList.size());
        for (SysLoggerDTO t : dataList) {
            list.add(BeanCopyUtils.copyObj(t, SysLogger.class));
        }
        return this.saveOrUpdateBatch(list, BATCH_SIZE);
    }

    /**
     * 批量更新系统日志
     *
     * @param dataList 日志数据传输对象列表
     * @return 批量更新是否成功
     */
    @Override
    public Boolean batchUpdate(List<SysLoggerDTO> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return false;
        }
        List<SysLogger> list = new ArrayList<>(dataList.size());
        for (SysLoggerDTO t : dataList) {
            list.add(BeanCopyUtils.copyObj(t, SysLogger.class));
        }
        return this.updateBatchById(list);
    }
}
