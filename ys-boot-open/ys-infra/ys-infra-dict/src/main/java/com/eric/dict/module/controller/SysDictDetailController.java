package com.eric.dict.module.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.common.annotation.log.MyLog;
import com.eric.common.constant.LogConst;
import com.eric.common.module.controller.BaseController;
import com.eric.common.module.domain.query.PageQuery;
import com.eric.common.response.ResponseResult;
import com.eric.dict.module.dto.SysDictDetailDTO;
import com.eric.dict.module.entity.SysDictDetail;
import com.eric.dict.module.service.SysDictDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 字典管理明细表Controller
 * @ClassName:  SysDictDetailController
 * @author:     liuQ
 * @date:       2025-06-16 15:20:01
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Tag(name = "字典管理明细表")
@RestController
@RequestMapping("/system/dictdetail")
public class SysDictDetailController extends BaseController<SysDictDetail, SysDictDetailService> {

    /**
     * 分页查询
     * @param query
     * @return
     */
    @Operation(summary = "字典管理明细表-分页查询", description = "返回当页所有记录")
    @GetMapping("/list")
    @SaCheckPermission("system:dict:list")
    public ResponseResult<Page<SysDictDetail>> list(SysDictDetailDTO dto, PageQuery page) {
        return ResponseResult.success(service.pageList(dto, page));
    }

    /**
     * 添加
     * @param o
     * @return
     */
    @Operation(summary = "字典管理明细表-新增", description = "字典管理明细表-新增")
    @MyLog(operModul = "字典管理明细表-新增", operType = LogConst.ADD, operDesc = "新增")
    @PostMapping("/add")
    @SaCheckPermission("system:dict:add")
    public ResponseResult<String> add(SysDictDetailDTO o){
        return ResponseResult.success("新增成功！", service.add(o));
    }

    /**
     * 编辑
     * @param o
     * @return
     */
    @Operation(summary = "字典管理明细表-编辑", description = "字典管理明细表-编辑")
    @MyLog(operModul = "字典管理明细表-编辑", operType = LogConst.UPDATE, operDesc = "编辑")
    @PutMapping("/update")
    @SaCheckPermission("system:dict:edit")
    public ResponseResult<Boolean> edit(SysDictDetailDTO o){
        return ResponseResult.success(service.update(o));
    }
    /**
     * 删除
     * @param ids
     * @return
     */
    @Operation(summary = "字典管理明细表-删除", description = "字典管理明细表-删除")
    @MyLog(operModul = "字典管理明细表-删除", operType = LogConst.DELETE, operDesc = "删除")
    @DeleteMapping("/delete/{ids}")
    @SaCheckPermission("system:dict:delete")
    public ResponseResult<Boolean> delete(@PathVariable List<String> ids){
        return ResponseResult.success(service.delete(ids));
    }
    /**
     * 获取对象信息
     * @param id
     * @return
     */
    @Operation(summary = "字典管理明细表-通过id查询", description = "字典管理明细表-通过id查询")
    @GetMapping("/{id}")
    @SaCheckPermission("system:dict:query")
    public ResponseResult<SysDictDetailDTO> get(@PathVariable("id") String id) {
        return ResponseResult.success(service.get(id));
    }

    /**
     * 根据字典编码查询字典明细列表
     * <p>通用查询接口，被前端下拉框等组件广泛使用，仅需登录即可访问</p>
     * @param dictCode 字典编码
     * @return 字典明细列表
     */
    @Operation(summary = "字典管理明细表-根据字典编码查询", description = "根据字典编码查询字典明细列表")
    @GetMapping("/code/{dictCode}")
    @SaCheckLogin
    public ResponseResult<List<SysDictDetail>> getByDictCode(@PathVariable("dictCode") String dictCode) {
        return ResponseResult.success(service.selectByDictCode(dictCode));
    }
}
