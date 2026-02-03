package com.eric.module.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.common.module.controller.BaseController;
import com.eric.common.module.domain.query.PageQuery;
import com.eric.common.response.ResponseResult;
import com.eric.module.system.dto.SysRoleDTO;
import com.eric.module.system.entity.SysRole;
import com.eric.module.system.service.SysRoleService;
import com.eric.module.system.vo.UpdateRoleMenuVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色信息表Controller
 * @ClassName:  SysRoleController
 * @author:     liuQ
 * @date:       2025-06-16 15:20:01
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends BaseController<SysRole, SysRoleService> {

    /**
     * 分页查询
     * @param query
     * @return
     */
    @GetMapping("/list")
    public ResponseResult<Page<SysRole>> list(SysRoleDTO dto, PageQuery page) {
        return ResponseResult.success(service.pageList(dto, page));
    }

    /**
     * 添加
     * @param o
     * @return
     */
    @PostMapping("/add")
    @SaCheckPermission("system:role:add")
    public ResponseResult<String> add(SysRoleDTO o){
        return ResponseResult.success("新增成功！", service.add(o));
    }

    /**
     * 编辑
     * @param o
     * @return
     */
    @PutMapping("/update")
    @SaCheckPermission("system:role:edit")
    public ResponseResult<Boolean> edit(SysRoleDTO o){
        return ResponseResult.success(service.update(o));
    }
    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping("/delete/{ids}")
    @SaCheckPermission("system:role:delete")
    public ResponseResult<Boolean> delete(@PathVariable List<String> ids){
        return ResponseResult.success(service.delete(ids));
    }
    /**
     * 获取对象信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult<SysRoleDTO> get(@PathVariable("id") String id) {
        return ResponseResult.success(service.get(id));
    }
    //获取所有角色列表
    @GetMapping("/getRoleList")
    public ResponseResult<List<SysRole>> getRoleList() {
        return ResponseResult.success(service.getRoleList());
    }

    /**
     *  获取角色所拥有的权限id
     * @param id
     * @return
     */
    @GetMapping("/getRoleMenus/{id}")
    public ResponseResult<List<String>> getRoleMenus(@PathVariable String id) {
        return ResponseResult.success(service.getRoleMenusIds(id));
    }

    /**
     * 修改角色权限
     * @param dto
     * @return
     */
    @PostMapping("/updateRoleMenus")
    @SaCheckPermission("system:role:updateAuth")
    public ResponseResult<Boolean> updateRoleMenus(@RequestBody UpdateRoleMenuVO vo) {
        return ResponseResult.success(service.updateRoleMenus(vo.id, vo.roleIds));
    }

}
