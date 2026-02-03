package com.eric.module.system.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eric.common.enums.common.IsDeleteEnum;
import com.eric.common.module.domain.query.PageQuery;
import com.eric.common.utils.BeanCopyUtils;
import com.eric.module.system.dto.SysUserDTO;
import com.eric.module.system.entity.SysDepart;
import com.eric.module.system.entity.SysMenu;
import com.eric.module.system.entity.SysRole;
import com.eric.module.system.entity.SysUser;
import com.eric.module.system.mapper.SysUserMapper;
import com.eric.module.system.service.SysDepartService;
import com.eric.module.system.service.SysRoleService;
import com.eric.module.system.service.SysUserService;
import com.eric.module.system.vo.UserRoleVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户基础表-服务层实现
 * @ClassName:   SysUserServiceImpl
 * @author:      liuQ
 * @date:        2025-07-23 12:29:26
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

	@Autowired
	private SysDepartService departService;
	
	@Autowired
	private SysRoleService roleService;
	
	@Override
	public String add(SysUserDTO o) {
		SysUser sysUser = BeanCopyUtils.copyObj(o, SysUser.class);

		sysUser.setCode(IdUtil.simpleUUID());   // 编号
		save(sysUser);

		String id = sysUser.getId();
		//添加用户角色
		List<String> roleIds = o.getRoleIds();
		if(roleIds != null && !roleIds.isEmpty()){
			baseMapper.addUserRole(id, roleIds);
		}
		return sysUser.getId();
	}

	@Override
	public Boolean delete(List<String> ids) {
		return removeByIds(ids);
	}

	@Override
	public Boolean update(SysUserDTO o) {
		SysUser sysUser = baseMapper.selectById(o.getId());
		BeanUtils.copyProperties(o, sysUser);

		//移除当前用户所有角色
		baseMapper.removeUserRole(o.getId());

		//添加用户角色
		List<String> roleIds = o.getRoleIds();
		if(roleIds != null && !roleIds.isEmpty()){
			baseMapper.addUserRole(o.getId(), roleIds);
		}
		return updateById(sysUser);
	}

	@Override
	public SysUserDTO get(Serializable id) {
		SysUser sysUser = baseMapper.selectById(id);
		return BeanCopyUtils.copyObj(sysUser, SysUserDTO.class);
	}

	@Override
	public Page<SysUser> pageList(SysUserDTO dto, PageQuery page) {
		QueryWrapper<SysUser> query = new QueryWrapper<>();
		//增加其他查询条件 ...
		query.like(StringUtils.isNotBlank(dto.getLoginName()), "login_name", dto.getLoginName());
		query.like(StringUtils.isNotBlank(dto.getUserName()), "real_name", dto.getUserName());
		query.like(StringUtils.isNotBlank(dto.getPhone()), "phone", dto.getPhone());
		query.like(StringUtils.isNotBlank(dto.getEmail()), "email", dto.getEmail());
		query.eq(StringUtils.isNotBlank(dto.getDepartId()), "depart_id", dto.getDepartId());
		query.eq(dto.getStatus() != null, "status", dto.getStatus());
		query.eq(dto.getIsDelete() != null, "is_delete", dto.getIsDelete());
		query.eq(dto.getIsLock() != null, "is_lock", dto.getIsLock());
		query.eq(dto.getUserType() != null, "user_type", dto.getUserType());

		query.ne(StringUtils.isNotBlank(dto.getExcludeDeptId()), "depart_id", dto.getExcludeDeptId()); //排除部门

		if(StringUtils.isNotBlank(dto.getExcludeRoleId())){
			query.notInSql("id", "SELECT user_id FROM sys_user_role WHERE role_id = '" + dto.getExcludeRoleId() + "'");
		}
		page.setAsc(true);
		page.setAscList(List.of("create_time"));
		// 添加基于角色ID的查询条件
		if (StringUtils.isNotBlank(dto.getRoleId())) {
			query.inSql("id", "SELECT user_id FROM sys_user_role WHERE role_id = '" + dto.getRoleId() + "'");
		}

		
		Page<SysUser> pageList = page(Page.of(page.getPageNo(), page.getPageSize()), query);
		//List<SysUser> dataList = new ArrayList<>();
		//获取所有角色对应的角色名
		Map<String, List<SysRole>> userRoleMap = getAllUsersRoleMap();
		List<SysUser> dataList = pageList.getRecords();
		pageList.setRecords(dataList.stream().peek(item -> {
			if (userRoleMap.containsKey(item.getId())) {
				item.setRoleList(userRoleMap.get(item.getId()));
			}
        }).collect(Collectors.toList()));

		return pageList;
	}

	@Override
	public Boolean batchAdd(List<SysUserDTO> dataList) {
		if(!dataList.isEmpty()) {
			List<SysUser> list = new ArrayList<SysUser>();
			for (SysUserDTO t : dataList) {
				SysUser sysUser = BeanCopyUtils.copyObj(t, SysUser.class);
				list.add(sysUser);
			}
			return this.saveOrUpdateBatch(list, 1000);
		}
		return false;
	}

	@Override
	public Boolean batchUpdate(List<SysUserDTO> dataList) {
		if(!dataList.isEmpty()) {
			List<SysUser> list = new ArrayList<SysUser>();
			for (SysUserDTO t : dataList) {
				SysUser sysUser = BeanCopyUtils.copyObj(t, SysUser.class);
				list.add(sysUser);
			}
			return this.updateBatchById(list);
		}
		return false;
	}

	@Override
	public List<UserRoleVo> getUserRoleList(String userId) {
		return baseMapper.getUserRoleList(userId);
	}

	/**
	 * 获取所有用户对应的角色列表
	 * @return Map<UserId, List<SysRole>> 用户ID对应的角色列表映射
	 */
	public Map<String, List<SysRole>> getAllUsersRoleMap() {
		// 查询所有用户
		List<SysUser> userList = list();

		// 创建返回结果
		Map<String, List<SysRole>> userRoleMap = new HashMap<>();
		
		// 如果没有用户，直接返回空Map
		if (userList.isEmpty()) {
			return userRoleMap;
		}
		
		// 获取所有用户ID
		List<String> userIds = userList.stream().map(SysUser::getId).collect(Collectors.toList());
		
		// 查询所有用户角色关系
		List<UserRoleVo> userRoleList = baseMapper.getAllUsersRoleList(userIds);
		
		// 按用户ID分组
		Map<String, List<UserRoleVo>> userRoleGroup = userRoleList.stream()
				.collect(Collectors.groupingBy(UserRoleVo::getUserId));
		
		// 查询所有角色信息
		List<SysRole> allRoles = roleService.list();
		Map<String, SysRole> roleMap = allRoles.stream()
				.collect(Collectors.toMap(SysRole::getId, Function.identity()));
		
		// 构建用户ID到角色列表的映射
		for (SysUser user : userList) {
			String userId = user.getId();
			List<UserRoleVo> roles = userRoleGroup.getOrDefault(userId, new ArrayList<>());
			List<SysRole> roleList = roles.stream()
					.map(ur -> roleMap.get(ur.getRoleId()))
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
			userRoleMap.put(userId, roleList);
		}
		
		return userRoleMap;
	}

	@Override
	public List<String> getUserPermissionList(String userId) {
		List<String> permissionSet = new ArrayList<>();
		List<SysMenu> menuList = baseMapper.getUserMenuList(userId);
		for (SysMenu sysMenu : menuList) {
			if (StringUtils.isNotBlank(sysMenu.getPermission())){
				permissionSet.add(sysMenu.getPermission());
			}
		}
		return permissionSet;
	}

	@Override
	public SysUser getUserByLoginName(String loginName) {
		LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<>();
		query.eq(SysUser::getLoginName, loginName);
		query.eq(SysUser::getIsDelete, IsDeleteEnum.NOT_DELETED.getKey());
		return baseMapper.selectOne(query);
	}

	@Override
	public SysUser getUserByPhone(String phone) {
		LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<>();
		query.eq(SysUser::getPhone, phone);
		query.eq(SysUser::getIsDelete, IsDeleteEnum.NOT_DELETED.getKey());
		return baseMapper.selectOne(query);
	}

	@Override
	public SysUser getUserByEmail(String email) {
		LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<>();
		query.eq(SysUser::getEmail, email);
		query.eq(SysUser::getIsDelete, IsDeleteEnum.NOT_DELETED.getKey());
		return baseMapper.selectOne(query);
	}

	@Override
	public Boolean assignUsersToDept(String deptId, List<String> userIds) {
		// 检查部门是否存在
		SysDepart depart = departService.getById(deptId);
		if (depart == null) {
			throw new RuntimeException("指定的部门不存在");
		}
		
		// 更新用户表中的部门信息
		List<SysUser> userList = new ArrayList<>();
		for (String userId : userIds) {
			SysUser user = new SysUser();
			user.setId(userId);
			user.setDepartId(deptId);
			user.setDepartName(depart.getName());
			userList.add(user);
		}
		
		return updateBatchById(userList);
	}
	
	@Override
	public Boolean removeUsersFromDept(String deptId, List<String> userIds) {
		// 检查部门是否存在
		SysDepart depart = departService.getById(deptId);
		if (depart == null) {
			throw new RuntimeException("指定的部门不存在");
		}

		if (userIds.isEmpty()) {
			throw new RuntimeException("需要移除的用户id为空");
		}
		List<SysUser> userList = listByIds(userIds);
		// 更新用户表中的部门信息，将指定部门的用户移除部门信息
		List<SysUser> dataList = new ArrayList<>();
		for (SysUser t : userList) {
			SysUser user = new SysUser();
			BeanUtils.copyProperties(t, user);
			user.setDepartId(null);
			user.setDepartName(null);
			dataList.add(user);
		}
		
		return updateBatchById(dataList);
	}

	@Override
	public Boolean addRoleUsers(String roleId, List<String> userIds) {
		//添加用户角色
		if(userIds != null && !userIds.isEmpty()){
			baseMapper.addRoleToUsers(roleId, userIds);
		}
		return true;
	}
	@Override
	public Boolean removeRoleUsers(String roleId, List<String> userIds) {
		// 移除用户角色
		if(userIds != null && !userIds.isEmpty()){
			baseMapper.removeRoleUsers(roleId, userIds);
		}
		return true;
	}
	@Override
	public Boolean freezeOrUnUser(String userId, Integer isLock) {
		SysUser user = baseMapper.selectById(userId);
		if (user == null) {
			throw new RuntimeException("用户不存在");
		}
		
		// 检查用户是否已经是冻结状态
		if (isLock == 0 && user.getIsLock() != null && user.getIsLock() == 0) {
			throw new RuntimeException("用户已经是冻结状态");
		}
		
		// 冻结用户
		user.setIsLock(isLock); // 0表示冻结 1表示正常
		user.setUpdateTime(LocalDateTime.now());
		return updateById(user);
	}

	
	@Override
	public Boolean changePassword(String userId, String oldPassword, String newPassword) {
		SysUser user = baseMapper.selectById(userId);
		if (user == null) {
			throw new RuntimeException("用户不存在");
		}
		
		// 验证旧密码是否正确
		if (!user.getPassword().equals(oldPassword)) {
			throw new RuntimeException("旧密码错误");
		}
		
		// 更新密码
		user.setPassword(newPassword);
		user.setPasswordUpdateTime(LocalDateTime.now());
		user.setUpdateTime(LocalDateTime.now());
		
		return updateById(user);
	}
	
	@Override
	public Boolean resetPassword(String userId, String newPassword) {
		SysUser user = baseMapper.selectById(userId);
		if (user == null) {
			throw new RuntimeException("用户不存在");
		}
		
		// 更新密码
		user.setPassword(newPassword);
		user.setPasswordUpdateTime(LocalDateTime.now());
		user.setUpdateTime(LocalDateTime.now());
		
		return updateById(user);
	}

	@Override
	public List<SysUser> getUsersByIds(List<String> userIds) {
		if (userIds == null || userIds.isEmpty()) {
			return new ArrayList<>();
		}
		return baseMapper.selectByIds(userIds);
	}

	@Override
	public List<SysUser> getAllUsers() {
		return baseMapper.selectList(null);
	}

}