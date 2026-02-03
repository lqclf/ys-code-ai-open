package com.eric.module.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eric.common.enums.common.IsDeleteEnum;
import com.eric.common.enums.system.MenuTypeEnum;
import com.eric.common.module.controller.BaseController;
import com.eric.common.response.ResponseResult;
import com.eric.common.utils.BeanCopyUtils;
import com.eric.module.system.dto.SysMenuDTO;
import com.eric.module.system.entity.SysMenu;
import com.eric.module.system.service.SysMenuService;
import com.eric.module.system.vo.MenuRoleTreeVO;
import com.eric.module.system.vo.MenuTreeVO;
import com.eric.module.system.vo.MenuVo;
import com.eric.module.system.vo.SelectTreeVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 菜单表Controller
 * @ClassName:  SysMenuController
 * @author:     liuQ
 * @date:       2025-06-16 15:20:01
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@RestController
@RequestMapping("/system/sysmenu")
public class SysMenuController extends BaseController<SysMenu, SysMenuService> {

    /**
     * 获取当前登录用户菜单
     * @return
     */
    @GetMapping("/userMenus")
    public ResponseResult<List<MenuTreeVO>> getUserMenus() {
        // 转换为前端需要的格式
        List<MenuTreeVO> menuTree = service.getUserMenuTree();
        return ResponseResult.success(menuTree);
    }

    /**
     * 获取所有菜单树
     * @return
     */
    @GetMapping("/getMenuList")
    public ResponseResult<List<MenuVo>> getMenuList(SysMenuDTO dto) {
        QueryWrapper<SysMenu> query = new QueryWrapper<>();
        query.eq("is_delete", IsDeleteEnum.NOT_DELETED.getKey());
        query.like(StringUtils.isNotBlank(dto.getTitle()), "title", dto.getTitle());
        query.eq(StringUtils.isNotBlank(dto.getPid()), "pid", dto.getPid());
        query.eq(dto.getType() != null, "type", dto.getType());
        query.orderByAsc("seq");
        List<SysMenu> menuList = service.list(query);

        //获取所有菜单
        List<SysMenu> allList = service.list();
        Map<String, String> pidNameMap = allList.stream().collect(Collectors.toMap(SysMenu::getId, SysMenu::getTitle));

        //组装数据
        List<MenuVo> dataList = menuList.stream().map(menu -> {
            MenuVo node = new MenuVo();
            BeanCopyUtils.copyProperties(menu, node);
            // 增加null校验，防止NullPointerException
            if (menu != null && menu.getPid() != null) {
                node.setPidName(pidNameMap.get(menu.getPid()));
            }
            return node;
        }).toList();

        return ResponseResult.success(dataList);
    }
    /**
     * 获取授权菜单树--角色授权使用
     * @return
     */
    @GetMapping("/getMenuRoleList")
    public ResponseResult<List<MenuRoleTreeVO>> getMenuRoleList() {
        //获取目录和菜单
        QueryWrapper<SysMenu> query = new QueryWrapper<>();
        query.eq("is_delete", IsDeleteEnum.NOT_DELETED.getKey());
        query.in("type", MenuTypeEnum.CATALOG.getKey(), MenuTypeEnum.MENU.getKey());
        query.orderByAsc("seq");
        List<SysMenu> menuList = service.list(query); // 菜单和目录

        //获取按钮集合
        QueryWrapper<SysMenu> queryBtn = new QueryWrapper<>();
        queryBtn.eq("is_delete", IsDeleteEnum.NOT_DELETED.getKey());
        queryBtn.eq("type", MenuTypeEnum.BUTTON.getKey());
        queryBtn.orderByAsc("seq");
        List<SysMenu> btnList = service.list(queryBtn); // 按钮
        Map<String, List<SysMenu>> btnMap = btnList.stream().collect(Collectors.groupingBy(SysMenu::getPid));

        List<MenuRoleTreeVO> dataList = menuList.stream().map(menu -> {
            MenuRoleTreeVO node = new MenuRoleTreeVO();
            node.setId(menu.getId());
            node.setPid(menu.getPid());
            node.setTitle(menu.getTitle());
            node.setSeq(menu.getSeq());
            node.setBtnList(btnMap.get(menu.getId()));
            return node;
        }).collect(Collectors.toList());
        return ResponseResult.success(dataList);
    }
    /**
     * 获取所有菜单树
     * @return
     */
    @GetMapping("/selectMenuTree")
    public ResponseResult<List<SelectTreeVo>> selectMenuTree() {
        List<SelectTreeVo> menuTree = service.selectMenuTree();
        return ResponseResult.success(menuTree);
    }


    /**
     * 添加
     * @param o
     * @return
     */
    @PostMapping("/add")
    @SaCheckPermission("system:menu:add")
    public ResponseResult<String> add(SysMenuDTO o){
        return ResponseResult.success("新增成功！", service.add(o));
    }

    /**
     * 编辑
     * @param o
     * @return
     */
    @PutMapping("/update")
    @SaCheckPermission("system:menu:edit")
    public ResponseResult<Boolean> edit(SysMenuDTO o){
        return ResponseResult.success(service.update(o));
    }
    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping("/delete/{ids}")
    @SaCheckPermission("system:menu:delete")
    public ResponseResult<Boolean> delete(@PathVariable List<String> ids){
        return ResponseResult.success(service.delete(ids));
    }
    /**
     * 获取对象信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult<SysMenuDTO> get(@PathVariable("id") String id) {
        return ResponseResult.success(service.get(id));
    }
}