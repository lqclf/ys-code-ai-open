package com.eric.module.system.service;

import com.eric.common.module.service.BaseService;
import com.eric.module.system.dto.SysDepartDTO;
import com.eric.module.system.entity.SysDepart;
import com.eric.module.system.vo.SelectTreeVo;

import java.util.List;

/**
 * 部门信息表 服务类
 * @ClassName:  SysDepartService     
 * @author:     liuQ
 * @date:       2025-09-10 15:22:32
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
public interface SysDepartService extends BaseService<SysDepartDTO, SysDepart> {

    /**
     * 获取下拉部门树
     * @return
     */
    List<SelectTreeVo> selectDeptTree();
}
