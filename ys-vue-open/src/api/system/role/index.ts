import request from '@/utils/request';

let currentBatchId: string = '';

/**
 * 角色管理api接口集合
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
				data: params,
			});
		},
		update: (params?: object) => {
			return request({
				url: '/api/system/role/update',
				method: 'put',
				data: params,
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
		getRoleMenus: (id?: String) => {
			return request({
				url: '/api/system/role/getRoleMenus/' + id,
				method: 'get',
			});
		},
		updateRoleMenus: (params?: object) => {
			return request({
				url: '/api/system/role/updateRoleMenus',
				method: 'post',
				data: params,
			});
		},
		getRoleList: () => {
			return request({
				url: '/api/system/role/getRoleList',
				method: 'get',
			});
		},
	};
}