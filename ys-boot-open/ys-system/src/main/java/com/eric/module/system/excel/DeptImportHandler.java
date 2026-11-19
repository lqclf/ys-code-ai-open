package com.eric.module.system.excel;

import com.eric.excel.context.PersistContext;
import com.eric.excel.context.ProcessContext;
import com.eric.excel.handler.AbstractImportHandler;
import com.eric.excel.module.controller.ImportController;
import com.eric.excel.result.ValidationResult;
import com.eric.module.system.dto.SysDepartDTO;
import com.eric.module.system.entity.SysDepart;
import com.eric.module.system.excel.dto.SysDepartImportDTO;
import com.eric.module.system.service.SysDepartService;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 部门导入处理器
 * 实现部门数据的导入逻辑，使用批量操作提升性能
 * 部门编码(code)唯一性校验，部门名称(name)+父部门ID(pid)组合唯一性，满足任一条件则更新
 *
 * @author Eric
 * @date 2026-05-18
 */
@Component
public class DeptImportHandler extends AbstractImportHandler<SysDepartImportDTO, SysDepartDTO> {

    @Autowired
    private SysDepartService deptService;

    @PostConstruct
    public void register() {
        ImportController.registerHandler("systemDept", this);
    }

    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Za-z0-9_-]+$");
    private static final Pattern ZIP_CODE_PATTERN = Pattern.compile("^\\d{6}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9-]{7,20}$");

    @Override
    public Class<SysDepartImportDTO> getDtoClass() {
        return SysDepartImportDTO.class;
    }

    @Override
    public Map<String, Object> prepareValidate(List<SysDepartImportDTO> dataList) {
        Map<String, Object> context = new HashMap<>();
        List<SysDepart> allDepts = deptService.list();
        Map<String, SysDepart> codeMap = new HashMap<>();
        Map<String, SysDepart> namePidMap = new HashMap<>();
        Map<String, SysDepart> nameMap = new HashMap<>();
        for (SysDepart d : allDepts) {
            if (StringUtils.isNotBlank(d.getCode())) {
                codeMap.put(d.getCode(), d);
            }
            namePidMap.put(d.getName() + "_" + d.getPid(), d);
            nameMap.put(d.getName(), d);
        }
        context.put("codeMap", codeMap);
        context.put("namePidMap", namePidMap);
        context.put("nameMap", nameMap);
        return context;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ValidationResult validate(SysDepartImportDTO dto, int rowNum, Map<String, Object> context) {
        StringBuilder errorMsg = new StringBuilder();

        if (StringUtils.isBlank(dto.getName())) {
            errorMsg.append("部门名称不能为空；");
        } else if (dto.getName().length() < 2 || dto.getName().length() > 100) {
            errorMsg.append("部门名称长度需在2-100字符之间；");
        }

        if (StringUtils.isNotBlank(dto.getCode())) {
            if (!CODE_PATTERN.matcher(dto.getCode()).matches()) {
                errorMsg.append("部门编码只能包含字母、数字、下划线和横线；");
            } else if (dto.getCode().length() < 2 || dto.getCode().length() > 50) {
                errorMsg.append("部门编码长度需在2-50字符之间；");
            }
        }

        if (StringUtils.isNotBlank(dto.getZipCode()) && !ZIP_CODE_PATTERN.matcher(dto.getZipCode()).matches()) {
            errorMsg.append("邮政编码格式不正确，应为6位数字；");
        }

        if (StringUtils.isNotBlank(dto.getEmail()) && !EMAIL_PATTERN.matcher(dto.getEmail()).matches()) {
            errorMsg.append("邮箱格式不正确；");
        }

        if (StringUtils.isNotBlank(dto.getLinkPhone()) && !PHONE_PATTERN.matcher(dto.getLinkPhone()).matches()) {
            errorMsg.append("联系电话格式不正确；");
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
    public ProcessContext<SysDepartImportDTO, SysDepartDTO> processData(List<SysDepartImportDTO> dataList) {
        ProcessContext<SysDepartImportDTO, SysDepartDTO> context = new ProcessContext<>();
        context.setOriginalData(dataList);

        List<SysDepartImportDTO> processedList = new ArrayList<>();
        List<SysDepartDTO> insertList = new ArrayList<>();
        List<SysDepartDTO> updateList = new ArrayList<>();
        Map<SysDepartDTO, Integer> rowNumMap = new HashMap<>();

        List<SysDepart> allDepts = deptService.list();
        Map<String, SysDepart> codeMap = new HashMap<>();
        Map<String, SysDepart> namePidMap = new HashMap<>();
        Map<String, SysDepart> nameMap = new HashMap<>();
        for (SysDepart d : allDepts) {
            if (StringUtils.isNotBlank(d.getCode())) {
                codeMap.put(d.getCode(), d);
            }
            namePidMap.put(d.getName() + "_" + d.getPid(), d);
            nameMap.put(d.getName(), d);
        }

        int rowNum = 2;
        for (SysDepartImportDTO importDto : dataList) {
            try {
                SysDepartDTO dto = convertToBusinessDto(importDto, nameMap);

                if (dto.getStatus() == null) {
                    dto.setStatus(1);
                }
                if (dto.getSeq() == null) {
                    dto.setSeq(0);
                }
                if (dto.getPid() == null) {
                    dto.setPid("0");
                }

                SysDepart existingDept = findExistingDept(dto, codeMap, namePidMap);
                if (existingDept != null) {
                    dto.setId(existingDept.getId());
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

    private SysDepartDTO convertToBusinessDto(SysDepartImportDTO importDto, Map<String, SysDepart> nameMap) {
        SysDepartDTO dto = new SysDepartDTO();
        dto.setName(importDto.getName());
        dto.setShortName(importDto.getShortName());
        dto.setCode(importDto.getCode());
        dto.setArea(importDto.getArea());
        dto.setEmail(importDto.getEmail());
        dto.setZipCode(importDto.getZipCode());
        dto.setAddress(importDto.getAddress());
        dto.setFax(importDto.getFax());
        dto.setRemark(importDto.getRemark());
        dto.setLinkMan(importDto.getLinkMan());
        dto.setLinkPhone(importDto.getLinkPhone());
        dto.setLeaderName(importDto.getLeaderName());
        dto.setIcon(importDto.getIcon());

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

        if (StringUtils.isNotBlank(importDto.getParentName()) && nameMap != null) {
            SysDepart parent = nameMap.get(importDto.getParentName());
            if (parent != null) {
                dto.setPid(parent.getId());
            } else {
                dto.setPid("0");
            }
        } else {
            dto.setPid("0");
        }

        return dto;
    }

    private SysDepart findExistingDept(SysDepartDTO dto, Map<String, SysDepart> codeMap, Map<String, SysDepart> namePidMap) {
        if (StringUtils.isNotBlank(dto.getCode())) {
            SysDepart deptByCode = codeMap.get(dto.getCode());
            if (deptByCode != null) {
                return deptByCode;
            }
        }
        return namePidMap.get(dto.getName() + "_" + dto.getPid());
    }

    private Integer parseStatus(String statusStr) {
        if (StringUtils.isBlank(statusStr)) return null;
        if ("正常".equals(statusStr.trim()) || "1".equals(statusStr.trim())) return 1;
        if ("禁用".equals(statusStr.trim()) || "0".equals(statusStr.trim())) return 0;
        return null;
    }

    @Override
    public String getPermissionCode() {
        return "system:dept:import";
    }

    @Override
    @SuppressWarnings("unchecked")
    public PersistContext persist(ProcessContext<SysDepartImportDTO, SysDepartDTO> context) {
        int insertCount = 0;
        int updateCount = 0;
        int failCount = 0;
        List<PersistContext.FailDetail> failDetails = new ArrayList<>();

        Map<SysDepartDTO, Integer> rowNumMap = (Map<SysDepartDTO, Integer>) context.getStatistic("rowNumMap");
        if (rowNumMap == null) rowNumMap = new HashMap<>();

        List<SysDepartDTO> insertList = context.getInsertList();
        if (insertList != null && !insertList.isEmpty()) {
            try {
                boolean success = deptService.batchAdd(insertList);
                if (success) {
                    insertCount = insertList.size();
                } else {
                    failCount += insertList.size();
                    for (SysDepartDTO dto : insertList) {
                        failDetails.add(PersistContext.FailDetail.builder()
                                .rowNum(rowNumMap.getOrDefault(dto, 0))
                                .data(dto)
                                .errorMsg("批量新增失败")
                                .build());
                    }
                }
            } catch (Exception e) {
                failCount += insertList.size();
                for (SysDepartDTO dto : insertList) {
                    failDetails.add(PersistContext.FailDetail.builder()
                            .rowNum(rowNumMap.getOrDefault(dto, 0))
                            .data(dto)
                            .errorMsg("批量新增失败：" + e.getMessage())
                            .build());
                }
            }
        }

        List<SysDepartDTO> updateList = context.getUpdateList();
        if (updateList != null && !updateList.isEmpty()) {
            try {
                boolean success = deptService.batchUpdate(updateList);
                if (success) {
                    updateCount = updateList.size();
                } else {
                    failCount += updateList.size();
                    for (SysDepartDTO dto : updateList) {
                        failDetails.add(PersistContext.FailDetail.builder()
                                .rowNum(rowNumMap.getOrDefault(dto, 0))
                                .data(dto)
                                .errorMsg("批量更新失败")
                                .build());
                    }
                }
            } catch (Exception e) {
                failCount += updateList.size();
                for (SysDepartDTO dto : updateList) {
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
