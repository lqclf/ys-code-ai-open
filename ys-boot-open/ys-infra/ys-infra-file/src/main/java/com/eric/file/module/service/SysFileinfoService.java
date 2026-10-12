package com.eric.file.module.service;

import com.eric.common.module.service.BaseService;
import com.eric.file.module.dto.FileUploadDTO;
import com.eric.file.module.dto.SysFileinfoDTO;
import com.eric.file.module.entity.SysFileinfo;
import com.eric.file.module.vo.FileUploadResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * 文件管理 服务类（简化版）
 * <p>仅支持单文件上传、批量上传、下载、删除等基础功能</p>
 *
 * @author: Eric
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
public interface SysFileinfoService extends BaseService<SysFileinfoDTO, SysFileinfo> {

    /**
     * 单文件上传
     *
     * @param file      上传的文件
     * @param uploadDTO 上传参数
     * @return 上传结果
     */
    FileUploadResultVO uploadFile(MultipartFile file, FileUploadDTO uploadDTO);

    /**
     * 批量文件上传
     *
     * @param files     上传的文件列表
     * @param uploadDTO 上传参数
     * @return 上传结果列表
     */
    List<FileUploadResultVO> uploadFiles(MultipartFile[] files, FileUploadDTO uploadDTO);

    /**
     * 文件下载
     *
     * @param fileId 文件ID
     * @return 文件输入流
     */
    InputStream downloadFile(String fileId);

    /**
     * 获取文件下载URL
     *
     * @param fileId         文件ID
     * @param expireMinutes  过期时间（分钟）
     * @return 下载URL
     */
    String getDownloadUrl(String fileId, Integer expireMinutes);

    /**
     * 物理删除文件（同时删除存储文件）
     *
     * @param fileId 文件ID
     * @return 是否成功
     */
    Boolean deleteFilePhysically(String fileId);

    /**
     * 批量物理删除文件
     *
     * @param fileIds 文件ID列表
     * @return 是否成功
     */
    Boolean deleteFilesPhysically(List<String> fileIds);

    /**
     * 根据业务ID获取文件列表
     *
     * @param businessId 业务ID
     * @return 文件列表
     */
    List<SysFileinfoDTO> getFilesByBusinessId(String businessId);

    /**
     * 根据文件ID集合获取文件信息列表
     *
     * @param fileIds 文件ID集合
     * @return 文件信息列表
     */
    List<SysFileinfoDTO> getFilesByIds(List<String> fileIds);
}
