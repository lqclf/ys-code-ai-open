package com.eric.dict.module.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.common.annotation.log.MyLog;
import com.eric.common.constant.LogConst;
import com.eric.common.module.controller.BaseController;
import com.eric.common.module.domain.query.PageQuery;
import com.eric.common.response.ResponseResult;
import com.eric.dict.module.dto.SysDictDTO;
import com.eric.dict.module.entity.SysDict;
import com.eric.dict.module.service.SysDictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 字典管理表Controller
 * @ClassName:  SysDictController
 * @author:     liuQ
 * @date:       2025-06-16 15:20:01
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Tag(name = "字典管理表")
@RestController
@RequestMapping("/system/dict")
public class SysDictController extends BaseController<SysDict, SysDictService> {

    /**
     * 分页查询
     * @param query
     * @return
     */
    @Operation(summary = "分页查询", description = "返回当页所有记录")
    @GetMapping("/list")
    public ResponseResult<Page<SysDict>> list(SysDictDTO dto, PageQuery page) {
        return ResponseResult.success(service.pageList(dto, page));
    }

    /**
     * 添加
     * @param o
     * @return
     */
    @Operation(summary = "新增", description = "字典管理表-新增")
    @MyLog(operModul = "字典管理表-新增", operType = LogConst.ADD, operDesc = "新增")
    @PostMapping("/add")
    @SaCheckPermission("system:dict:add")
    public ResponseResult<String> add(SysDictDTO o){
        return ResponseResult.success("新增成功！", service.add(o));
    }

    /**
     * 编辑
     * @param o
     * @return
     */
    @Operation(summary = "编辑", description = "字典管理表-编辑")
    @MyLog(operModul = "字典管理表-编辑", operType = LogConst.UPDATE, operDesc = "编辑")
    @PutMapping("/update")
    @SaCheckPermission("system:dict:edit")
    public ResponseResult<Boolean> edit(SysDictDTO o){
        return ResponseResult.success(service.update(o));
    }
    /**
     * 删除
     * @param ids
     * @return
     */
    @Operation(summary = "删除", description = "字典管理表-删除")
    @MyLog(operModul = "字典管理表-删除", operType = LogConst.DELETE, operDesc = "删除")
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
    @Operation(summary = "通过id查询", description = "字典管理表-通过id查询")
    @GetMapping("/{id}")
    public ResponseResult<SysDictDTO> get(@PathVariable("id") String id) {
        return ResponseResult.success(service.get(id));
    }
}
