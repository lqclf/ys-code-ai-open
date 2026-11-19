package com.eric.module.system.excel;

import com.eric.common.enums.common.StatusEnum;
import com.eric.common.utils.BeanCopyUtils;
import com.eric.excel.handler.AbstractExportHandler;
import com.eric.excel.module.controller.ExportController;
import com.eric.module.system.dto.SysRoleDTO;
import com.eric.module.system.entity.SysRole;
import com.eric.module.system.excel.dto.SysRoleExportDTO;
import com.eric.module.system.service.SysRoleService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 角色导出处理器
 * 实现角色数据的导出逻辑
 *
 * @author Eric
 * @date 2026-05-18
 */
@Component
public class RoleExportHandler extends AbstractExportHandler<SysRoleExportDTO, SysRoleDTO> {

    @Autowired
    private SysRoleService roleService;

    @PostConstruct
    public void register() {
        ExportController.registerHandler("systemRole", this);
    }

    @Override
    public Class<SysRoleExportDTO> getDtoClass() {
        return SysRoleExportDTO.class;
    }

    @Override
    public List<SysRoleExportDTO> queryData(SysRoleDTO queryParams) {
        List<SysRole> entityList = roleService.list(queryParams);
        List<SysRoleExportDTO> dtoList = new ArrayList<>();
        for (SysRole entity : entityList) {
            SysRoleExportDTO dto = BeanCopyUtils.copyObj(entity, SysRoleExportDTO.class);
            if (dto == null) continue;

            if (entity.getStatus() != null && StatusEnum.getByKey(entity.getStatus()) != null) {
                dto.setStatusStr(Objects.requireNonNull(StatusEnum.getByKey(entity.getStatus())).getValue());
            }

            dtoList.add(dto);
        }
        return dtoList;
    }

   /* @Override
    public String getPermissionCode() {
        return "system:role:export";
    }*/

    @Override
    public Class<SysRoleDTO> getQueryClass() {
        return SysRoleDTO.class;
    }

    @Override
    public String getFileName() {
        return "角色信息";
    }

    @Override
    public String getSheetName() {
        return "角色列表";
    }
}
