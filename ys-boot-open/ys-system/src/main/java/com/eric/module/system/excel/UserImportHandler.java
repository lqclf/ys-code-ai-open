package com.eric.module.system.excel;

import com.eric.common.enums.common.UserTypeEnum;
import com.eric.excel.context.PersistContext;
import com.eric.excel.context.ProcessContext;
import com.eric.excel.handler.AbstractImportHandler;
import com.eric.excel.module.controller.ImportController;
import com.eric.excel.result.ValidationResult;
import com.eric.module.system.dto.SysUserDTO;
import com.eric.module.system.entity.SysDepart;
import com.eric.module.system.entity.SysUser;
import com.eric.module.system.excel.dto.SysUserImportDTO;
import com.eric.module.system.service.SysDepartService;
import com.eric.module.system.service.SysUserService;
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
 * 用户导入处理器
 * 实现用户数据的导入逻辑，使用批量操作提升性能
 * 以登录名(loginName)为唯一标识，存在则更新允许字段，不存在则插入（使用默认密码）
 *
 * @author Eric
 * @date 2026-05-18
 */
@Component
public class UserImportHandler extends AbstractImportHandler<SysUserImportDTO, SysUserDTO> {

    @Autowired
    private SysUserService userService;

    @PostConstruct
    public void register() {
        ImportController.registerHandler("systemUser", this);
    }

    @Autowired
    private SysDepartService departService;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final String DEFAULT_PASSWORD = "123456";

    private String specifiedDepartId;
    private String specifiedDepartName;

    @Override
    public String getPermissionCode() {
        return "system:user:import";
    }

    @Override
    public void beforeImport(Map<String, String> params) {
        this.specifiedDepartId = params != null ? params.get("departId") : null;
        if (StringUtils.isNotBlank(this.specifiedDepartId)) {
            SysDepart dept = departService.getById(this.specifiedDepartId);
            this.specifiedDepartName = dept != null ? dept.getName() : null;
        } else {
            this.specifiedDepartName = null;
        }
    }

    @Override
    public Class<SysUserImportDTO> getDtoClass() {
        return SysUserImportDTO.class;
    }

    @Override
    public Map<String, Object> prepareValidate(List<SysUserImportDTO> dataList) {
        Map<String, Object> context = new HashMap<>();
        List<SysUser> allUsers = userService.list();
        Map<String, SysUser> userMap = new HashMap<>();
        for (SysUser u : allUsers) {
            userMap.put(u.getLoginName(), u);
        }
        context.put("existingUserMap", userMap);

        List<SysDepart> allDepts = departService.list();
        Map<String, SysDepart> deptMap = new HashMap<>();
        for (SysDepart d : allDepts) {
            deptMap.put(d.getName(), d);
        }
        context.put("deptMap", deptMap);
        return context;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ValidationResult validate(SysUserImportDTO dto, int rowNum, Map<String, Object> context) {
        StringBuilder errorMsg = new StringBuilder();

        if (StringUtils.isBlank(dto.getLoginName())) {
            errorMsg.append("登录名不能为空；");
        } else if (dto.getLoginName().length() < 3 || dto.getLoginName().length() > 30) {
            errorMsg.append("登录名长度需在3-30字符之间；");
        }

        if (StringUtils.isBlank(dto.getUserName())) {
            errorMsg.append("真实姓名不能为空；");
        }

        if (StringUtils.isNotBlank(dto.getPhone()) && !PHONE_PATTERN.matcher(dto.getPhone()).matches()) {
            errorMsg.append("手机号格式不正确；");
        }

        if (StringUtils.isNotBlank(dto.getEmail()) && !EMAIL_PATTERN.matcher(dto.getEmail()).matches()) {
            errorMsg.append("邮箱格式不正确；");
        }

        if (StringUtils.isNotBlank(dto.getStatusStr())) {
            Integer status = parseStatus(dto.getStatusStr());
            if (status == null) {
                errorMsg.append("状态值只能是0（禁用）或1（正常）；");
            }
        }

        Map<String, SysDepart> deptMap = (Map<String, SysDepart>) context.get("deptMap");
        if (StringUtils.isNotBlank(specifiedDepartId)) {
            if (StringUtils.isNotBlank(dto.getDepartName()) && !specifiedDepartName.equals(dto.getDepartName())) {
                errorMsg.append("部门名称与指定部门不一致，应为：").append(specifiedDepartName).append("；");
            }
        } else {
            if (StringUtils.isNotBlank(dto.getDepartName()) && (deptMap == null || !deptMap.containsKey(dto.getDepartName()))) {
                errorMsg.append("部门名称不存在：").append(dto.getDepartName()).append("；");
            }
        }

        if (errorMsg.length() > 0) {
            return ValidationResult.fail(errorMsg.toString());
        }
        return ValidationResult.success();
    }

    @Override
    public ProcessContext<SysUserImportDTO, SysUserDTO> processData(List<SysUserImportDTO> dataList) {
        ProcessContext<SysUserImportDTO, SysUserDTO> context = new ProcessContext<>();
        context.setOriginalData(dataList);

        List<SysUserImportDTO> processedList = new ArrayList<>();
        List<SysUserDTO> insertList = new ArrayList<>();
        List<SysUserDTO> updateList = new ArrayList<>();

        List<SysUser> allUsers = userService.list();
        Map<String, SysUser> userMap = new HashMap<>();
        for (SysUser u : allUsers) {
            userMap.put(u.getLoginName(), u);
        }

        List<SysDepart> allDepts = departService.list();
        Map<String, SysDepart> deptMap = new HashMap<>();
        for (SysDepart d : allDepts) {
            deptMap.put(d.getName(), d);
        }

        int rowNum = 2;
        for (SysUserImportDTO importDto : dataList) {
            try {
                SysUserDTO dto = convertToBusinessDto(importDto, deptMap);
                dto.setRowNum(rowNum);

                if (dto.getStatus() == null) {
                    dto.setStatus(1);
                }

                SysUser existingUser = userMap.get(dto.getLoginName());
                if (existingUser != null) {
                    SysUserDTO updateDto = createUpdateDto(existingUser.getId(), dto);
                    updateList.add(updateDto);
                } else {
                    dto.setPassword(DEFAULT_PASSWORD);
                    insertList.add(dto);
                }
                processedList.add(importDto);
            } catch (Exception e) {
                context.addError(rowNum, importDto, "数据处理失败：" + e.getMessage());
            }
            rowNum++;
        }

        context.setProcessedData(processedList);
        context.setInsertList(insertList);
        context.setUpdateList(updateList);
        return context;
    }

    private SysUserDTO convertToBusinessDto(SysUserImportDTO importDto, Map<String, SysDepart> deptMap) {
        SysUserDTO dto = new SysUserDTO();
        dto.setLoginName(importDto.getLoginName());
        dto.setUserName(importDto.getUserName());
        dto.setCode(importDto.getCode());
        dto.setPhone(importDto.getPhone());
        dto.setEmail(importDto.getEmail());
        dto.setThirdAccount(importDto.getThirdAccount());
        dto.setRemark(importDto.getRemark());

        if (StringUtils.isNotBlank(importDto.getUserTypeStr())) {
            Integer userType = parseUserType(importDto.getUserTypeStr());
            dto.setUserType(userType);
        }

        if (StringUtils.isNotBlank(importDto.getStatusStr())) {
            Integer status = parseStatus(importDto.getStatusStr());
            if (status != null) dto.setStatus(status);
        }

        if (StringUtils.isNotBlank(importDto.getIsLockStr())) {
            Integer isLock = parseStatus(importDto.getIsLockStr());
            if (isLock != null) dto.setIsLock(isLock);
        }

        if (StringUtils.isNotBlank(specifiedDepartId)) {
            dto.setDepartId(specifiedDepartId);
        } else if (StringUtils.isNotBlank(importDto.getDepartName()) && deptMap != null) {
            SysDepart dept = deptMap.get(importDto.getDepartName());
            if (dept != null) {
                dto.setDepartId(dept.getId());
            }
        }

        return dto;
    }

    private SysUserDTO createUpdateDto(String userId, SysUserDTO source) {
        SysUserDTO updateDto = new SysUserDTO();
        updateDto.setId(userId);
        updateDto.setUserName(source.getUserName());
        updateDto.setDepartId(source.getDepartId());
        updateDto.setPhone(source.getPhone());
        updateDto.setEmail(source.getEmail());
        updateDto.setLoginName(source.getLoginName());
        updateDto.setStatus(source.getStatus());
        return updateDto;
    }

    private Integer parseStatus(String statusStr) {
        if (StringUtils.isBlank(statusStr)) return null;
        if ("正常".equals(statusStr.trim()) || "1".equals(statusStr.trim())) return 1;
        if ("禁用".equals(statusStr.trim()) || "0".equals(statusStr.trim())) return 0;
        return null;
    }

    private Integer parseUserType(String userTypeStr) {
        if (StringUtils.isBlank(userTypeStr)) return null;
        for (UserTypeEnum e : UserTypeEnum.values()) {
            if (e.getValue().equals(userTypeStr.trim())) {
                return (Integer) e.getKey();
            }
        }
        return null;
    }

    @Override
    public PersistContext persist(ProcessContext<SysUserImportDTO, SysUserDTO> context) {
        int insertCount = 0;
        int updateCount = 0;
        int failCount = 0;
        List<PersistContext.FailDetail> failDetails = new ArrayList<>();

        List<SysUserDTO> insertList = context.getInsertList();
        if (insertList != null && !insertList.isEmpty()) {
            try {
                boolean success = userService.batchAdd(insertList);
                if (success) {
                    insertCount = insertList.size();
                } else {
                    failCount += insertList.size();
                    for (SysUserDTO dto : insertList) {
                        failDetails.add(PersistContext.FailDetail.builder()
                                .rowNum(dto.getRowNum())
                                .data(dto)
                                .errorMsg("批量新增失败")
                                .build());
                    }
                }
            } catch (Exception e) {
                failCount += insertList.size();
                for (SysUserDTO dto : insertList) {
                    failDetails.add(PersistContext.FailDetail.builder()
                            .rowNum(dto.getRowNum())
                            .data(dto)
                            .errorMsg("批量新增失败：" + e.getMessage())
                            .build());
                }
            }
        }

        List<SysUserDTO> updateList = context.getUpdateList();
        if (updateList != null && !updateList.isEmpty()) {
            try {
                boolean success = userService.batchUpdate(updateList);
                if (success) {
                    updateCount = updateList.size();
                } else {
                    failCount += updateList.size();
                    for (SysUserDTO dto : updateList) {
                        failDetails.add(PersistContext.FailDetail.builder()
                                .rowNum(dto.getRowNum())
                                .data(dto)
                                .errorMsg("批量更新失败")
                                .build());
                    }
                }
            } catch (Exception e) {
                failCount += updateList.size();
                for (SysUserDTO dto : updateList) {
                    failDetails.add(PersistContext.FailDetail.builder()
                            .rowNum(dto.getRowNum())
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
