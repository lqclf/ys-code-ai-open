import request from '@/utils/request';

/**
 * 角色模块接口
 */
export function useRoleApi() {
	return {
		list: (params?: object) => {
			return request({
				url: '/api/system/role/list',
				method: 'get',
				params,
			});
		},
		add: (params?: object) => {
			return request({
				url: '/api/system/role/add',
				method: 'post',
				params,
			});
		},
		update: (params?: object) => {
			return request({
				url: '/api/system/role/update',
				method: 'put',
				params,
			});
		},
		delete: (ids?: String) => {
			return request({
				url: '/api/system/role/delete/' + ids,
				method: 'delete',
			});
		},
		getById: (id?: String) => {
			return request({
				url: '/api/system/role/' + id,
				method: 'get',
			});
		},
		/*获取角色所拥有的权限id */
		getRoleMenus: (id?: String) => { 
			return request({
				url: '/api/system/role/getRoleMenus/' + id,
				method: 'get',
			});
		},
		/*修改角色权限 */
		updateRoleMenus: (params?: object) => {
			return request({
				url: '/api/system/role/updateRoleMenus',
				method: 'post',
				data: params,  
			});
		},
		/*获取角色列表 */
		getRoleList: () => {
			return request({
				url: '/api/system/role/getRoleList',
				method: 'get',
			});
		},
	};
}
