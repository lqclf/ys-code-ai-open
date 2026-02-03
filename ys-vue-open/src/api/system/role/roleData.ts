import request from '@/utils/request';

/**
 * 角色数据权限接口
 */
export function useRoleDataApi() {
	return {
		/**
		 * 分页查询角色数据权限列表
		 * @param params 查询参数
		 * @returns 分页结果
		 */
		list: (params?: object) => {
			return request({
				url: '/api/system/roleData/list',
				method: 'get',
				params,
			});
		},
		/**
		 * 新增角色数据权限
		 * @param params 角色数据权限对象
		 * @returns 操作结果
		 */
		add: (params?: object) => {
			return request({
				url: '/api/system/roleData/add',
				method: 'post',
				data: params,
			});
		},
		/**
		 * 编辑角色数据权限
		 * @param params 角色数据权限对象
		 * @returns 操作结果
		 */
		update: (params?: object) => {
			return request({
				url: '/api/system/roleData/update',
				method: 'put',
				data: params,
			});
		},
		/**
		 * 删除角色数据权限
		 * @param ids 角色数据权限ID列表
		 * @returns 操作结果
		 */
		delete: (ids?: String) => {
			return request({
				url: '/api/system/roleData/delete/' + ids,
				method: 'delete',
			});
		},
		/**
		 * 通过ID查询角色数据权限
		 * @param id 角色数据权限ID
		 * @returns 角色数据权限对象
		 */
		getById: (id?: String) => {
			return request({
				url: '/api/system/roleData/' + id,
				method: 'get',
			});
		},
		/**
		 * 获取角色数据权限
		 * @param roleId 角色ID
		 * @returns 角色数据权限对象
		 */
		getRoleDataPermission: (roleId?: String) => {
			return request({
				url: '/api/system/roleData/getRoleDataPermission/' + roleId,
				method: 'get',
			});
		},
		/**
		 * 保存角色数据权限
		 * @param params 角色数据权限对象
		 * @returns 操作结果
		 */
		saveRoleDataPermission: (params?: object) => {
			return request({
				url: '/api/system/roleData/saveRoleDataPermission',
				method: 'post',
				data: params,
			});
		},
	};
}
