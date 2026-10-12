package com.eric.file.module.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.common.enums.common.IsDeleteEnum;
import com.eric.common.enums.fileupload.FileUploadStatusEnum;
import com.eric.common.exception.BaseException;
import com.eric.common.module.domain.query.PageQuery;
import com.eric.common.module.service.impl.BaseServiceImpl;
import com.eric.common.response.ResponseCode;
import com.eric.common.utils.BeanCopyUtils;
import com.eric.file.module.dto.FileUploadDTO;
import com.eric.file.module.dto.SysFileinfoDTO;
import com.eric.file.module.entity.SysFileinfo;
import com.eric.file.module.mapper.SysFileinfoMapper;
import com.eric.file.module.service.SysFileinfoService;
import com.eric.file.module.vo.FileUploadResultVO;
import com.eric.file.properties.FileUploadProperties;
import com.eric.file.storage.FileStorageStrategy;
import com.eric.file.storage.FileStorageStrategyFactory;
import com.eric.file.utils.FileUploadUtils;
import com.eric.file.validator.FileSecurityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 文件管理 服务实现类（简化版）
 * <p>
 * 仅保留单文件上传、批量上传、下载、删除等基础功能，移除分片上传、统计、Token、预览、文件去重、引用计数等复杂逻辑
 * </p>
 *
 * @author: Eric
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class SysFileinfoServiceImpl extends BaseServiceImpl<SysFileinfoMapper, SysFileinfo> implements SysFileinfoService {

    /** 文件上传配置属性 */
    private final FileUploadProperties properties;
    /** 文件存储策略工厂 */
    private final FileStorageStrategyFactory storageStrategyFactory;

    // =========================== 基础 CRUD ===========================

    @Override
    public String add(SysFileinfoDTO o) {
        SysFileinfo sysFileinfo = BeanCopyUtils.copyObj(o, SysFileinfo.class);
        save(sysFileinfo);
        return sysFileinfo.getId();
    }

    @Override
    public Boolean delete(List<String> ids) {
        return removeByIds(ids);
    }

    @Override
    public Boolean update(SysFileinfoDTO o) {
        SysFileinfo sysFileinfo = baseMapper.selectById(o.getId());
        if (sysFileinfo == null) {
            return false;
        }
        BeanUtils.copyProperties(o, sysFileinfo);
        return updateById(sysFileinfo);
    }

    @Override
    public SysFileinfoDTO get(java.io.Serializable id) {
        SysFileinfo sysFileinfo = baseMapper.selectById(id);
        return sysFileinfo != null ? BeanCopyUtils.copyObj(sysFileinfo, SysFileinfoDTO.class) : null;
    }

    @Override
    public Page<SysFileinfo> pageList(SysFileinfoDTO dto, PageQuery page) {
        QueryWrapper<SysFileinfo> query = whereQuery(page);
        return page(Page.of(page.getPageNo(), page.getPageSize()), query);
    }

    @Override
    public Boolean batchAdd(List<SysFileinfoDTO> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return false;
        }
        List<SysFileinfo> list = new ArrayList<>();
        for (SysFileinfoDTO t : dataList) {
            list.add(BeanCopyUtils.copyObj(t, SysFileinfo.class));
        }
        return this.saveOrUpdateBatch(list, 1000);
    }

    @Override
    public Boolean batchUpdate(List<SysFileinfoDTO> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return false;
        }
        List<SysFileinfo> list = new ArrayList<>();
        for (SysFileinfoDTO t : dataList) {
            list.add(BeanCopyUtils.copyObj(t, SysFileinfo.class));
        }
        return this.updateBatchById(list);
    }

    // =========================== 文件上传 ===========================

    @Override
    public FileUploadResultVO uploadFile(MultipartFile file, FileUploadDTO uploadDTO) {
        try {
            validateFile(file);

            SysFileinfo fileInfo = buildFileInfo(file, uploadDTO);

            // 选择存储策略并执行上传
            FileStorageStrategy storageStrategy = storageStrategyFactory.getStrategy(determineStorageType(uploadDTO));
            String fileUrl = storageStrategy.uploadFile(
                    file.getInputStream(),
                    fileInfo.getFilePath(),
                    FileUploadUtils.getContentType(file.getOriginalFilename())
            );

            if (fileUrl == null) {
                throw new BaseException("文件上传失败", ResponseCode.ERROR);
            }

            fileInfo.setUploadStatus(FileUploadStatusEnum.SUCCESS.getKey());
            save(fileInfo);

            return buildUploadResult(BeanCopyUtils.copyObj(fileInfo, SysFileinfoDTO.class));
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BaseException("文件上传失败: " + e.getMessage(), ResponseCode.ERROR);
        }
    }

    @Override
    public List<FileUploadResultVO> uploadFiles(MultipartFile[] files, FileUploadDTO uploadDTO) {
        List<FileUploadResultVO> results = new ArrayList<>();
        if (files == null || files.length == 0) {
            return results;
        }
        for (MultipartFile file : files) {
            try {
                results.add(uploadFile(file, uploadDTO));
            } catch (Exception e) {
                log.error("批量上传文件失败: {}", file.getOriginalFilename(), e);
                results.add(FileUploadResultVO.builder()
                        .originalFilename(file.getOriginalFilename())
                        .uploadStatus(FileUploadStatusEnum.FAILED.getKey())
                        .uploadStatusDesc(FileUploadStatusEnum.FAILED.getValue())
                        .build());
            }
        }
        return results;
    }

    // =========================== 文件下载 ===========================

    @Override
    public InputStream downloadFile(String fileId) {
        try {
            SysFileinfo fileInfo = getById(fileId);
            if (fileInfo == null) {
                throw new BaseException("文件不存在", ResponseCode.ERROR);
            }
            FileStorageStrategy storageStrategy = storageStrategyFactory.getStrategy(fileInfo.getStorageType());
            return storageStrategy.downloadFile(fileInfo.getFilePath());
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("文件下载失败: {}", fileId, e);
            return null;
        }
    }

    @Override
    public String getDownloadUrl(String fileId, Integer expireMinutes) {
        try {
            SysFileinfo fileInfo = getById(fileId);
            if (fileInfo == null) {
                return null;
            }
            FileStorageStrategy storageStrategy = storageStrategyFactory.getStrategy(fileInfo.getStorageType());
            if (expireMinutes != null && expireMinutes > 0) {
                return storageStrategy.getTemporaryUrl(fileInfo.getFilePath(), expireMinutes);
            }
            return storageStrategy.getFileUrl(fileInfo.getFilePath());
        } catch (Exception e) {
            log.error("获取下载URL失败: {}", fileId, e);
            return null;
        }
    }

    // =========================== 文件删除 ===========================

    @Override
    public Boolean deleteFilePhysically(String fileId) {
        try {
            SysFileinfo fileInfo = getById(fileId);
            if (fileInfo == null) {
                return false;
            }
            FileStorageStrategy storageStrategy = storageStrategyFactory.getStrategy(fileInfo.getStorageType());
            storageStrategy.deleteFile(fileInfo.getFilePath());
            return removeById(fileId);
        } catch (Exception e) {
            log.error("物理删除文件失败: {}", fileId, e);
            return false;
        }
    }

    @Override
    public Boolean deleteFilesPhysically(List<String> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return true;
        }
        boolean allSuccess = true;
        for (String fileId : fileIds) {
            if (!Boolean.TRUE.equals(deleteFilePhysically(fileId))) {
                allSuccess = false;
            }
        }
        return allSuccess;
    }

    // =========================== 文件查询 ===========================

    @Override
    public List<SysFileinfoDTO> getFilesByBusinessId(String businessId) {
        LambdaQueryWrapper<SysFileinfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysFileinfo::getBusinessId, businessId)
                .eq(SysFileinfo::getIsDelete, IsDeleteEnum.NOT_DELETED.key)
                .orderByDesc(SysFileinfo::getCreateTime);
        return BeanCopyUtils.copyList(list(wrapper), SysFileinfoDTO.class);
    }

    @Override
    public List<SysFileinfoDTO> getFilesByIds(List<String> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<SysFileinfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysFileinfo::getId, fileIds)
                .eq(SysFileinfo::getIsDelete, IsDeleteEnum.NOT_DELETED.key)
                .orderByDesc(SysFileinfo::getCreateTime);
        return BeanCopyUtils.copyList(list(wrapper), SysFileinfoDTO.class);
    }

    // =========================== 私有工具方法 ===========================

    /**
     * 校验上传文件
     *
     * @param file 上传文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BaseException("文件不能为空", ResponseCode.ERROR);
        }
        try {
            FileSecurityValidator.validateFile(file);
        } catch (Exception e) {
            throw new BaseException("文件验证失败: " + e.getMessage(), ResponseCode.ERROR);
        }
        if (!FileUploadUtils.isValidFileSize(file.getSize(), properties.getMaxFileSize())) {
            throw new BaseException("文件大小超出限制，最大允许 " +
                    FileUploadUtils.formatFileSize(properties.getMaxFileSize()), ResponseCode.ERROR);
        }
    }

    /**
     * 确定存储类型
     * <p>优先级：DTO传入 > yml配置 > local(默认)</p>
     */
    private String determineStorageType(FileUploadDTO uploadDTO) {
        if (uploadDTO != null && uploadDTO.getStorageType() != null && !uploadDTO.getStorageType().trim().isEmpty()) {
            return uploadDTO.getStorageType();
        }
        String configType = properties.getStorageType();
        return (configType == null || configType.trim().isEmpty()) ? "local" : configType;
    }

    /**
     * 构建文件信息实体
     */
    private SysFileinfo buildFileInfo(MultipartFile file, FileUploadDTO uploadDTO) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = FileUploadUtils.getFileExtension(originalFilename);
        String uniqueFilename = FileUploadUtils.generateUniqueFileName(originalFilename);
        String category = (uploadDTO != null && uploadDTO.getCategory() != null) ? uploadDTO.getCategory() : "other";
        String storagePath = FileUploadUtils.generateStoragePath("", category, uniqueFilename);
        String storageType = determineStorageType(uploadDTO);
        String businessId = uploadDTO != null ? uploadDTO.getBusinessId() : null;
        String extraInfo = uploadDTO != null ? uploadDTO.getExtraInfo() : null;

        SysFileinfo fileInfo = new SysFileinfo();
        fileInfo.setId(IdUtil.getSnowflakeNextIdStr());
        fileInfo.setFileKey(IdUtil.fastUUID());
        fileInfo.setFileName(uniqueFilename);
        fileInfo.setOriginalFilename(originalFilename);
        fileInfo.setCategory(category);
        fileInfo.setFilePath(storagePath);
        fileInfo.setFileSize(file.getSize());
        fileInfo.setFileSizeStr(FileUploadUtils.formatFileSize(file.getSize()));
        fileInfo.setByteSize(file.getSize());
        fileInfo.setSuffix(fileExtension);
        fileInfo.setFileType(String.valueOf(FileUploadUtils.getFileType(originalFilename).getKey()));
        fileInfo.setStorageType(storageType);
        fileInfo.setBusinessId(businessId);
        fileInfo.setExtraInfo(extraInfo);
        fileInfo.setUploadStatus(FileUploadStatusEnum.UPLOADING.getKey());
        return fileInfo;
    }

    /**
     * 构建上传结果VO
     */
    private FileUploadResultVO buildUploadResult(SysFileinfoDTO fileInfo) {
        FileStorageStrategy storageStrategy = storageStrategyFactory.getStrategy(fileInfo.getStorageType());
        if (storageStrategy == null) {
            throw new BaseException("无法获取文件存储策略: " + fileInfo.getStorageType(), ResponseCode.ERROR);
        }
        String fileUrl = storageStrategy.getFileUrl(fileInfo.getFilePath());
        return FileUploadResultVO.builder()
                .fileId(fileInfo.getId())
                .fileKey(fileInfo.getFileKey())
                .originalFilename(fileInfo.getOriginalFilename())
                .fileName(fileInfo.getFileName())
                .fileSize(fileInfo.getFileSize())
                .fileSizeStr(fileInfo.getFileSizeStr())
                .fileType(fileInfo.getFileType())
                .fileUrl(fileUrl)
                .storageType(fileInfo.getStorageType())
                .uploadStatus(fileInfo.getUploadStatus())
                .uploadStatusDesc(FileUploadStatusEnum.getByCode(fileInfo.getUploadStatus()).getValue())
                .businessId(fileInfo.getBusinessId())
                .build();
    }
}
