package com.eric.module.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.common.enums.common.IsDeleteEnum;
import com.eric.common.module.controller.BaseController;
import com.eric.common.module.domain.query.PageQuery;
import com.eric.common.response.ResponseResult;
import com.eric.module.system.dto.SysDepartDTO;
import com.eric.module.system.entity.SysDepart;
import com.eric.module.system.service.SysDepartService;
import com.eric.module.system.vo.SelectTreeVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 部门信息表Controller
 * @ClassName:  SysDepartController 
 * @author:     liuQ 
 * @date:       2025-06-16 15:20:01
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@RestController
@RequestMapping("/system/dept")
public class SysDepartController extends BaseController<SysDepart, SysDepartService> {

    /**
     * 分页查询
     * @param query
     * @return
     */
    @GetMapping("/list")
    public ResponseResult<Page<SysDepart>> list(SysDepartDTO dto, PageQuery page) {
        return ResponseResult.success(service.pageList(dto, page));
    }

    /**
     * 添加
     * @param o
     * @return
     */
    @PostMapping("/add")
  //  @SaCheckPermission("system:depart:add")
    public ResponseResult<String> add(SysDepartDTO o){
        return ResponseResult.success("新增成功！", service.add(o));
    }

    /**
     * 编辑
     * @param o
     * @return
     */
    @PutMapping("/update")
 //   @SaCheckPermission("system:depart:update")
    public ResponseResult<Boolean> edit(SysDepartDTO o){
        return ResponseResult.success(service.update(o));
    }
    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping("/delete/{ids}")
   // @SaCheckPermission("system:depart:delete")
    public ResponseResult<Boolean> delete(@PathVariable List<String> ids){
        return ResponseResult.success(service.delete(ids));
    }
    /**
     * 获取对象信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult<SysDepartDTO> get(@PathVariable("id") String id) {
        return ResponseResult.success(service.get(id));
    }

    /**
     * 获取所有菜单树
     * @return
     */
    @GetMapping("/getDeptList")
    public ResponseResult<List<SysDepart>> getMenuList(SysDepartDTO dto) {
        QueryWrapper<SysDepart> query = new QueryWrapper<>();
        query.eq("is_delete", IsDeleteEnum.NOT_DELETED.getKey());
        query.like(StringUtils.isNotBlank(dto.getName()), "name", dto.getName());
        query.orderByAsc("seq");
        List<SysDepart> dataList = service.list(query);
        return ResponseResult.success(dataList);
    }

    /**
     * 获取所有菜单树
     * @return
     */
    @GetMapping("/selectDeptTree")
    public ResponseResult<List<SelectTreeVo>> selectDeptTree() {
        List<SelectTreeVo> dataTree = service.selectDeptTree();
        return ResponseResult.success(dataTree);
    }


}