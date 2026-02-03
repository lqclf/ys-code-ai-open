package com.eric.module.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.common.module.controller.BaseController;
import com.eric.common.module.domain.query.PageQuery;
import com.eric.common.response.ResponseResult;
import com.eric.module.system.dto.SysConfigDTO;
import com.eric.module.system.entity.SysConfig;
import com.eric.module.system.service.SysConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 系统配置Controller
 * @ClassName:  SysConfigController 
 * @author:     liuQ 
 * @date:       2025-06-16 15:20:01
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@RestController
@RequestMapping("/system/sysconfig")
public class SysConfigController extends BaseController<SysConfig, SysConfigService> {

    /**
     * 分页查询
     * @param query
     * @return
     */
    @GetMapping("/list")
    public ResponseResult<Page<SysConfig>> list(SysConfigDTO dto, PageQuery page) {
        return ResponseResult.success(service.pageList(dto, page));
    }

    /**
     * 添加
     * @param o
     * @return
     */
    @PostMapping("/add")
    @SaCheckPermission("system:sysconfig:add")
    public ResponseResult<String> add(SysConfigDTO o){
        return ResponseResult.success("新增成功！", service.add(o));
    }

    /**
     * 编辑
     * @param o
     * @return
     */
    @PutMapping("/update")
    @SaCheckPermission("system:sysconfig:update")
    public ResponseResult<Boolean> edit(SysConfigDTO o){
        return ResponseResult.success(service.update(o));
    }
    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping("/delete/{ids}")
    @SaCheckPermission("system:sysconfig:delete")
    public ResponseResult<Boolean> delete(@PathVariable List<String> ids){
        return ResponseResult.success(service.delete(ids));
    }
    /**
     * 获取对象信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult<SysConfigDTO> get(@PathVariable("id") String id) {
        return ResponseResult.success(service.get(id));
    }
}