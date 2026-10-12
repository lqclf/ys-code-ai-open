package com.eric.file.module.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.common.annotation.enums.AutoEnum;
import com.eric.common.annotation.log.MyLog;
import com.eric.common.constant.LogConst;
import com.eric.common.module.controller.BaseController;
import com.eric.common.module.domain.query.PageQuery;
import com.eric.common.response.ResponseResult;
import com.eric.file.module.dto.SysFileinfoDTO;
import com.eric.file.module.entity.SysFileinfo;
import com.eric.file.module.service.SysFileinfoService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 文件管理Controller（简化版）
 * <p>仅提供文件信息的基础CRUD及按业务ID、文件ID集合查询功能</p>
 *
 * @author: Eric
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/system/fileinfo")
public class SysFileinfoController extends BaseController<SysFileinfo, SysFileinfoService> {

    /**
     * 分页查询文件信息
     */
    @Operation(summary = "分页查询", description = "返回当页所有记录")
    @GetMapping("/list")
    @AutoEnum
    @SaCheckPermission("system:fileinfo:list")
    public ResponseResult<Page<SysFileinfo>> list(SysFileinfoDTO dto, PageQuery page) {
        return ResponseResult.success(service.pageList(dto, page));
    }

    /**
     * 新增文件信息
     */
    @Operation(summary = "新增", description = "文件管理-新增")
    @MyLog(operModul = "文件管理-新增", operType = LogConst.ADD, operDesc = "新增")
    @PostMapping("/add")
    @SaCheckPermission("system:fileinfo:upload")
    public ResponseResult<String> add(SysFileinfoDTO o) {
        return ResponseResult.success("新增成功！", service.add(o));
    }

    /**
     * 编辑文件信息
     */
    @Operation(summary = "编辑", description = "文件管理-编辑")
    @MyLog(operModul = "文件管理-编辑", operType = LogConst.UPDATE, operDesc = "编辑")
    @PutMapping("/update")
    @SaCheckPermission("system:fileinfo:upload")
    public ResponseResult<Boolean> edit(SysFileinfoDTO o) {
        return ResponseResult.success(service.update(o));
    }

    /**
     * 删除文件信息
     */
    @Operation(summary = "删除", description = "文件管理-删除")
    @MyLog(operModul = "文件管理-删除", operType = LogConst.DELETE, operDesc = "删除")
    @DeleteMapping("/delete/{ids}")
    @SaCheckPermission("system:fileinfo:delete")
    public ResponseResult<Boolean> delete(@PathVariable List<String> ids) {
        return ResponseResult.success(service.delete(ids));
    }

    /**
     * 通过ID查询文件信息
     */
    @Operation(summary = "通过id查询", description = "文件管理-通过id查询")
    @GetMapping("/{id}")
    @SaCheckPermission("system:fileinfo:query")
    public ResponseResult<SysFileinfoDTO> get(@PathVariable("id") String id) {
        return ResponseResult.success(service.get(id));
    }

    /**
     * 批量物理删除文件，包括存储中的物理文件
     */
    @Operation(summary = "批量物理删除", description = "批量物理删除文件（包括存储文件）")
    @MyLog(operModul = "文件管理-批量物理删除", operType = LogConst.DELETE, operDesc = "批量物理删除文件")
    @DeleteMapping("/deletePhysically")
    @SaCheckPermission("system:fileinfo:delete")
    public ResponseResult<Boolean> deleteFilesPhysically(@RequestBody List<String> fileIds) {
        Boolean result = service.deleteFilesPhysically(fileIds);
        return Boolean.TRUE.equals(result)
                ? ResponseResult.success("批量删除成功", result)
                : ResponseResult.fail("批量删除失败");
    }

    /**
     * 根据业务ID获取文件列表
     */
    @Operation(summary = "根据业务ID获取文件列表", description = "获取指定业务相关的文件列表")
    @GetMapping("/business/{businessId}")
    @SaCheckPermission("system:fileinfo:query")
    public ResponseResult<List<SysFileinfoDTO>> getFilesByBusinessId(@PathVariable("businessId") String businessId) {
        return ResponseResult.success("获取文件列表成功", service.getFilesByBusinessId(businessId));
    }

    /**
     * 根据文件ID集合获取文件信息列表
     */
    @Operation(summary = "根据文件ID集合获取文件信息", description = "获取指定文件ID集合的文件信息列表")
    @PostMapping("/getByIds")
    @SaCheckPermission("system:fileinfo:query")
    public ResponseResult<List<SysFileinfoDTO>> getFilesByIds(@RequestBody List<String> fileIds) {
        return ResponseResult.success("获取文件信息成功", service.getFilesByIds(fileIds));
    }
}
