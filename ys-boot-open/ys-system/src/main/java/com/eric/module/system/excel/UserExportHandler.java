package com.eric.module.system.excel;

import com.eric.common.enums.common.StatusEnum;
import com.eric.common.enums.common.UserTypeEnum;
import com.eric.common.utils.BeanCopyUtils;
import com.eric.excel.handler.AbstractExportHandler;
import com.eric.excel.module.controller.ExportController;
import com.eric.module.system.dto.SysUserDTO;
import com.eric.module.system.entity.SysUser;
import com.eric.module.system.excel.dto.SysUserExportDTO;
import com.eric.module.system.service.SysUserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 用户导出处理器
 * 实现用户数据的导出逻辑
 *
 * @author Eric
 * @date 2026-05-18
 */
@Component
public class UserExportHandler extends AbstractExportHandler<SysUserExportDTO, SysUserDTO> {

    @Autowired
    private SysUserService userService;

    @PostConstruct
    public void register() {
        ExportController.registerHandler("systemUser", this);
    }

    @Override
    public Class<SysUserExportDTO> getDtoClass() {
        return SysUserExportDTO.class;
    }

    @Override
    public List<SysUserExportDTO> queryData(SysUserDTO queryParams) {
        List<SysUser> entityList = userService.list(queryParams);
        List<SysUserExportDTO> dtoList = new ArrayList<>();
        for (SysUser entity : entityList) {
            SysUserExportDTO dto = BeanCopyUtils.copyObj(entity, SysUserExportDTO.class);
            if (dto == null) continue;

            if (entity.getUserType() != null && UserTypeEnum.getByKey(entity.getUserType()) != null) {
                dto.setUserTypeStr(Objects.requireNonNull(UserTypeEnum.getByKey(entity.getUserType())).getValue());
            }
            if (entity.getStatus() != null && StatusEnum.getByKey(entity.getStatus()) != null) {
                dto.setStatusStr(Objects.requireNonNull(StatusEnum.getByKey(entity.getStatus())).getValue());
            }
            if (entity.getIsLock() != null) {
                dto.setIsLockStr(entity.getIsLock() == 1 ? "正常" : "已锁定");
            }

            dtoList.add(dto);
        }
        return dtoList;
    }
    /*
    @Override
    public String getPermissionCode() {
        return "system:user:export";
    }*/

    @Override
    public Class<SysUserDTO> getQueryClass() {
        return SysUserDTO.class;
    }

    @Override
    public String getFileName() {
        return "用户信息";
    }

    @Override
    public String getSheetName() {
        return "用户列表";
    }
}
