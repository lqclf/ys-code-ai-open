package com.eric.log.module.service;

import com.eric.common.module.service.BaseService;
import com.eric.log.module.dto.SysLoggerDTO;
import com.eric.log.module.entity.SysLogger;

/**
 * 系统日志表 服务类
 * <p>
 * 继承通用 BaseService，具备分页查询、新增、修改、删除等基础能力。
 * </p>
 *
 * @ClassName:  SysLoggerService
 * @author:     Eric
 * @date:       2025-07-23 14:38:08
 * @Copyright   ERIC 微信公众号：忆笙的技术杂货库
 */
public interface SysLoggerService extends BaseService<SysLoggerDTO, SysLogger> {

}
