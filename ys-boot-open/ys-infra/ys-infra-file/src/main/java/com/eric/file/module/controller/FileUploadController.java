package com.eric.file.module.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.eric.common.annotation.log.MyLog;
import com.eric.common.constant.LogConst;
import com.eric.common.response.ResponseResult;
import com.eric.file.module.dto.FileUploadDTO;
import com.eric.file.module.dto.SysFileinfoDTO;
import com.eric.file.module.service.SysFileinfoService;
import com.eric.file.module.vo.FileUploadResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 文件上传控制器（简化版）
 * <p>仅支持单文件上传、批量上传、文件下载、获取下载链接、物理删除等基础功能</p>
 *
 * @author: Eric
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
@Tag(name = "文件上传管理")
@RestController
@RequestMapping("/upload/file")
@RequiredArgsConstructor
public class FileUploadController {

    /** 文件信息服务 */
    private final SysFileinfoService sysFileinfoService;

    /**
     * 单文件上传
     */
    @Operation(summary = "单文件上传", description = "上传单个文件")
    @MyLog(operModul = "文件上传-单文件上传", operType = LogConst.ADD, operDesc = "单文件上传")
    @PostMapping("/upload")
    @SaCheckLogin
    public ResponseResult<FileUploadResultVO> uploadFile(
            @Parameter(description = "上传的文件", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "上传参数")
            @Validated FileUploadDTO uploadDTO) {
        FileUploadResultVO result = sysFileinfoService.uploadFile(file, uploadDTO);
        return ResponseResult.success("文件上传成功", result);
    }

    /**
     * 批量文件上传
     */
    @Operation(summary = "批量文件上传", description = "批量上传多个文件")
    @MyLog(operModul = "文件上传-批量上传", operType = LogConst.ADD, operDesc = "批量文件上传")
    @PostMapping("/upload/batch")
    @SaCheckLogin
    public ResponseResult<List<FileUploadResultVO>> uploadFiles(
            @Parameter(description = "上传的文件列表", required = true)
            @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "上传参数")
            @Validated FileUploadDTO uploadDTO) {
        List<FileUploadResultVO> results = sysFileinfoService.uploadFiles(files, uploadDTO);
        return ResponseResult.success("文件批量上传完成", results);
    }

    /**
     * 文件下载
     */
    @Operation(summary = "文件下载", description = "根据文件ID下载文件")
    @MyLog(operModul = "文件下载", operType = LogConst.OTHER, operDesc = "文件下载")
    @GetMapping("/download/{fileId}")
    @SaCheckLogin
    public void downloadFile(
            @Parameter(description = "文件ID", required = true)
            @PathVariable("fileId") String fileId,
            HttpServletResponse response) {
        try {
            SysFileinfoDTO fileInfo = sysFileinfoService.get(fileId);
            if (fileInfo == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String filename = fileInfo.getOriginalFilename();
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFilename + "\"");
            response.setHeader("Content-Length", String.valueOf(fileInfo.getFileSize()));

            InputStream inputStream = sysFileinfoService.downloadFile(fileId);
            if (inputStream == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            try (inputStream; OutputStream outputStream = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
        } catch (Exception e) {
            log.error("文件下载失败: {}", fileId, e);
            if (!response.isCommitted()) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    /**
     * 获取文件下载链接
     */
    @Operation(summary = "获取文件下载链接", description = "获取文件临时下载链接")
    @GetMapping("/download/url/{fileId}")
    @SaCheckLogin
    public ResponseResult<String> getDownloadUrl(
            @Parameter(description = "文件ID", required = true)
            @PathVariable("fileId") String fileId,
            @Parameter(description = "过期时间(分钟)")
            @RequestParam(value = "expireMinutes", required = false) Integer expireMinutes) {
        String downloadUrl = sysFileinfoService.getDownloadUrl(fileId, expireMinutes);
        if (downloadUrl == null) {
            return ResponseResult.fail("文件不存在或获取下载链接失败");
        }
        return ResponseResult.success("获取下载链接成功", downloadUrl);
    }

    /**
     * 根据业务ID获取文件列表
     */
    @Operation(summary = "根据业务ID获取文件列表", description = "获取指定业务相关的文件列表")
    @GetMapping("/business/{businessId}")
    @SaCheckLogin
    public ResponseResult<List<FileUploadResultVO>> getFilesByBusinessId(
            @PathVariable @Parameter(description = "业务ID", required = true) String businessId) {
        List<SysFileinfoDTO> fileList = sysFileinfoService.getFilesByBusinessId(businessId);
        List<FileUploadResultVO> results = fileList.stream()
                .map(file -> FileUploadResultVO.builder()
                        .fileId(file.getId())
                        .fileKey(file.getFileKey())
                        .originalFilename(file.getOriginalFilename())
                        .fileName(file.getFileName())
                        .fileSize(file.getFileSize())
                        .fileSizeStr(file.getFileSizeStr())
                        .fileType(file.getFileType())
                        .storageType(file.getStorageType())
                        .uploadStatus(file.getUploadStatus())
                        .businessId(file.getBusinessId())
                        .build())
                .toList();
        return ResponseResult.success("获取文件列表成功", results);
    }

    /**
     * 根据文件ID集合获取文件信息列表
     */
    @Operation(summary = "根据文件ID集合获取文件信息", description = "获取指定文件ID集合的文件信息列表")
    @PostMapping("/getByIds")
    @SaCheckLogin
    public ResponseResult<List<SysFileinfoDTO>> getFilesByIds(
            @Parameter(description = "文件ID集合", required = true)
            @RequestBody List<String> fileIds) {
        return ResponseResult.success("获取文件信息成功", sysFileinfoService.getFilesByIds(fileIds));
    }

    /**
     * 物理删除文件
     */
    @Operation(summary = "物理删除文件", description = "彻底删除文件（包括存储文件）")
    @MyLog(operModul = "文件管理-物理删除", operType = LogConst.DELETE, operDesc = "物理删除文件")
    @PostMapping("/delete/physical/{fileId}")
    @SaCheckLogin
    public ResponseResult<Boolean> deleteFilePhysically(
            @PathVariable @Parameter(description = "文件ID", required = true) String fileId) {
        Boolean result = sysFileinfoService.deleteFilePhysically(fileId);
        return Boolean.TRUE.equals(result)
                ? ResponseResult.success("文件删除成功", result)
                : ResponseResult.fail("文件删除失败");
    }
}
