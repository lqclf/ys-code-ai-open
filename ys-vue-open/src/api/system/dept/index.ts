import request from '@/utils/request';

let currentBatchId: string = '';

/**
 * 部门管理api接口集合
 */
export function useDeptApi() {
	return {
		getAdminDept: (params?: object) => {
			return request({
				url: '/api/system/dept/userDepts',
				method: 'get',
				params,
			});
		},
		getDeptList: (params?: object) => {
			return request({
				url: '/api/system/dept/getDeptList',
				method: 'get',
				params,
			});
		},
		selectDeptTree: (params?: object) => {
			return request({
				url: '/api/system/dept/selectDeptTree',
				method: 'get',
				params,
			});
		},
		addDept: (params?: object) => {
			return request({
				url: '/api/system/dept/add',
				method: 'post',
				params,
			});
		},
		updateDept: (params?: object) => {
			return request({
				url: '/api/system/dept/update',
				method: 'put',
				params,
			});
		},
		deleteDept: (ids?: String) => {
			return request({
				url: '/api/system/dept/delete/' + ids,
				method: 'delete',
			});
		},
		getDept: (id?: String) => {
			return request({
				url: '/api/system/dept/' + id,
				method: 'get',
			});
		},
	};
}