package com.eric.module.system.excel;

import com.eric.common.enums.common.StatusEnum;
import com.eric.common.utils.BeanCopyUtils;
import com.eric.excel.handler.AbstractExportHandler;
import com.eric.excel.module.controller.ExportController;
import com.eric.module.system.dto.SysDepartDTO;
import com.eric.module.system.entity.SysDepart;
import com.eric.module.system.excel.dto.SysDepartExportDTO;
import com.eric.module.system.service.SysDepartService;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 部门导出处理器
 * 实现部门数据的导出逻辑
 *
 * @author Eric
 * @date 2026-05-18
 */
@Component
public class DeptExportHandler extends AbstractExportHandler<SysDepartExportDTO, SysDepartDTO> {

    @Autowired
    private SysDepartService deptService;

    @PostConstruct
    public void register() {
        ExportController.registerHandler("systemDept", this);
    }

    @Override
    public Class<SysDepartExportDTO> getDtoClass() {
        return SysDepartExportDTO.class;
    }

    @Override
    public List<SysDepartExportDTO> queryData(SysDepartDTO queryParams) {
        List<SysDepart> entityList = deptService.list(queryParams);
        List<SysDepartExportDTO> dtoList = new ArrayList<>();

        List<SysDepart> allDepts = deptService.list();

        for (SysDepart entity : entityList) {
            SysDepartExportDTO dto = BeanCopyUtils.copyObj(entity, SysDepartExportDTO.class);
            if (dto == null) continue;

            if (entity.getStatus() != null && StatusEnum.getByKey(entity.getStatus()) != null) {
                dto.setStatusStr(Objects.requireNonNull(StatusEnum.getByKey(entity.getStatus())).getValue());
            }

            if (StringUtils.isNotBlank(entity.getPid()) && !"0".equals(entity.getPid())) {
                for (SysDepart d : allDepts) {
                    if (d.getId().equals(entity.getPid())) {
                        dto.setParentName(d.getName());
                        break;
                    }
                }
            }

            dtoList.add(dto);
        }
        return dtoList;
    }

    /*@Override
    public String getPermissionCode() {
        return "system:dept:export";
    }*/

    @Override
    public Class<SysDepartDTO> getQueryClass() {
        return SysDepartDTO.class;
    }

    @Override
    public String getFileName() {
        return "部门信息";
    }

    @Override
    public String getSheetName() {
        return "部门列表";
    }
}
