package com.eric.module.system.excel;

import com.eric.excel.context.PersistContext;
import com.eric.excel.context.ProcessContext;
import com.eric.excel.handler.AbstractImportHandler;
import com.eric.excel.module.controller.ImportController;
import com.eric.excel.result.ValidationResult;
import com.eric.module.system.dto.SysRoleDTO;
import com.eric.module.system.entity.SysRole;
import com.eric.module.system.excel.dto.SysRoleImportDTO;
import com.eric.module.system.service.SysRoleService;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色导入处理器
 * 实现角色数据的导入逻辑，使用批量操作提升性能
 * 以角色编码(code)为唯一标识，存在则更新，不存在则插入
 *
 * @author Eric
 * @date 2026-05-18
 */
@Component
public class RoleImportHandler extends AbstractImportHandler<SysRoleImportDTO, SysRoleDTO> {

    @Autowired
    private SysRoleService roleService;

    @PostConstruct
    public void register() {
        ImportController.registerHandler("systemRole", this);
    }

    @Override
    public Class<SysRoleImportDTO> getDtoClass() {
        return SysRoleImportDTO.class;
    }

    @Override
    public Map<String, Object> prepareValidate(List<SysRoleImportDTO> dataList) {
        Map<String, Object> context = new HashMap<>();
        List<SysRole> allRoles = roleService.list();
        Map<String, SysRole> roleMap = new HashMap<>();
        for (SysRole r : allRoles) {
            roleMap.put(r.getCode(), r);
        }
        context.put("existingRoleMap", roleMap);
        return context;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ValidationResult validate(SysRoleImportDTO dto, int rowNum, Map<String, Object> context) {
        StringBuilder errorMsg = new StringBuilder();

        if (StringUtils.isBlank(dto.getCode())) {
            errorMsg.append("角色编码不能为空；");
        }

        if (StringUtils.isBlank(dto.getName())) {
            errorMsg.append("角色名称不能为空；");
        }

        if (StringUtils.isNotBlank(dto.getStatusStr())) {
            Integer status = parseStatus(dto.getStatusStr());
            if (status == null) {
                errorMsg.append("状态值只能是0（禁用）或1（正常）；");
            }
        }

        if (errorMsg.length() > 0) {
            return ValidationResult.fail(errorMsg.toString());
        }
        return ValidationResult.success();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ProcessContext<SysRoleImportDTO, SysRoleDTO> processData(List<SysRoleImportDTO> dataList) {
        ProcessContext<SysRoleImportDTO, SysRoleDTO> context = new ProcessContext<>();
        context.setOriginalData(dataList);

        List<SysRoleImportDTO> processedList = new ArrayList<>();
        List<SysRoleDTO> insertList = new ArrayList<>();
        List<SysRoleDTO> updateList = new ArrayList<>();
        Map<SysRoleDTO, Integer> rowNumMap = new HashMap<>();

        List<SysRole> allRoles = roleService.list();
        Map<String, SysRole> roleMap = new HashMap<>();
        for (SysRole r : allRoles) {
            roleMap.put(r.getCode(), r);
        }

        int rowNum = 2;
        for (SysRoleImportDTO importDto : dataList) {
            try {
                SysRoleDTO dto = convertToBusinessDto(importDto);

                if (dto.getStatus() == null) {
                    dto.setStatus(1);
                }
                if (dto.getSeq() == null) {
                    dto.setSeq(0);
                }

                SysRole existingRole = roleMap.get(dto.getCode());
                if (existingRole != null) {
                    dto.setId(existingRole.getId());
                    updateList.add(dto);
                } else {
                    insertList.add(dto);
                }

                rowNumMap.put(dto, rowNum);
                processedList.add(importDto);
            } catch (Exception e) {
                context.addError(rowNum, importDto, "数据处理失败：" + e.getMessage());
            }
            rowNum++;
        }

        context.setProcessedData(processedList);
        context.setInsertList(insertList);
        context.setUpdateList(updateList);
        context.addStatistic("rowNumMap", rowNumMap);
        context.addStatistic("totalCount", dataList.size());
        context.addStatistic("insertCount", insertList.size());
        context.addStatistic("updateCount", updateList.size());
        return context;
    }

    private SysRoleDTO convertToBusinessDto(SysRoleImportDTO importDto) {
        SysRoleDTO dto = new SysRoleDTO();
        dto.setCode(importDto.getCode());
        dto.setName(importDto.getName());
        dto.setRemark(importDto.getRemark());

        if (StringUtils.isNotBlank(importDto.getSeq())) {
            try {
                dto.setSeq(Integer.parseInt(importDto.getSeq().trim()));
            } catch (NumberFormatException e) {
                dto.setSeq(0);
            }
        }

        if (StringUtils.isNotBlank(importDto.getStatusStr())) {
            Integer status = parseStatus(importDto.getStatusStr());
            if (status != null) dto.setStatus(status);
        }

        return dto;
    }

    private Integer parseStatus(String statusStr) {
        if (StringUtils.isBlank(statusStr)) return null;
        if ("正常".equals(statusStr.trim()) || "1".equals(statusStr.trim())) return 1;
        if ("禁用".equals(statusStr.trim()) || "0".equals(statusStr.trim())) return 0;
        return null;
    }

    @Override
    public String getPermissionCode() {
        return "system:role:import";
    }

    @Override
    @SuppressWarnings("unchecked")
    public PersistContext persist(ProcessContext<SysRoleImportDTO, SysRoleDTO> context) {
        int insertCount = 0;
        int updateCount = 0;
        int failCount = 0;
        List<PersistContext.FailDetail> failDetails = new ArrayList<>();

        Map<SysRoleDTO, Integer> rowNumMap = (Map<SysRoleDTO, Integer>) context.getStatistic("rowNumMap");
        if (rowNumMap == null) rowNumMap = new HashMap<>();

        List<SysRoleDTO> insertList = context.getInsertList();
        if (insertList != null && !insertList.isEmpty()) {
            try {
                boolean success = roleService.batchAdd(insertList);
                if (success) {
                    insertCount = insertList.size();
                } else {
                    failCount += insertList.size();
                    for (SysRoleDTO dto : insertList) {
                        failDetails.add(PersistContext.FailDetail.builder()
                                .rowNum(rowNumMap.getOrDefault(dto, 0))
                                .data(dto)
                                .errorMsg("批量新增失败")
                                .build());
                    }
                }
            } catch (Exception e) {
                failCount += insertList.size();
                for (SysRoleDTO dto : insertList) {
                    failDetails.add(PersistContext.FailDetail.builder()
                            .rowNum(rowNumMap.getOrDefault(dto, 0))
                            .data(dto)
                            .errorMsg("批量新增失败：" + e.getMessage())
                            .build());
                }
            }
        }

        List<SysRoleDTO> updateList = context.getUpdateList();
        if (updateList != null && !updateList.isEmpty()) {
            try {
                boolean success = roleService.batchUpdate(updateList);
                if (success) {
                    updateCount = updateList.size();
                } else {
                    failCount += updateList.size();
                    for (SysRoleDTO dto : updateList) {
                        failDetails.add(PersistContext.FailDetail.builder()
                                .rowNum(rowNumMap.getOrDefault(dto, 0))
                                .data(dto)
                                .errorMsg("批量更新失败")
                                .build());
                    }
                }
            } catch (Exception e) {
                failCount += updateList.size();
                for (SysRoleDTO dto : updateList) {
                    failDetails.add(PersistContext.FailDetail.builder()
                            .rowNum(rowNumMap.getOrDefault(dto, 0))
                            .data(dto)
                            .errorMsg("批量更新失败：" + e.getMessage())
                            .build());
                }
            }
        }

        return PersistContext.builder()
                .insertCount(insertCount)
                .updateCount(updateCount)
                .failCount(failCount)
                .failDetails(failDetails)
                .build();
    }
}
