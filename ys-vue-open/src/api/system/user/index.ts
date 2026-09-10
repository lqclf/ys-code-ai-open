import request from '@/utils/request';

/**
 * 用户管理api接口集合
 */
export function useUserApi() {
	return {
		getUserInfo: () => {
			return request({
				url: '/api/auth/getLoginUser',
				method: 'get',
			});
		},
		list: (params?: object) => {
			return request({
				url: '/api/system/user/list',
				method: 'get',
				params,
			});
		},
		add: (params?: object) => {
			return request({
				url: '/api/system/user/add',
				method: 'post',
				data: params,
			});
		},
		update: (params?: object) => {
			return request({
				url: '/api/system/user/update',
				method: 'put',
				data: params,
			});
		},
		delete: (ids: string) => {
			return request({
				url: `/api/system/user/delete/${ids}`,
				method: 'delete',
			});
		},
		getById: (id: string) => {
			return request({
				url: `/api/system/user/${id}`,
				method: 'get',
			});
		},
		updatePassword: (params?: object) => {
			return request({
				url: '/api/system/personal/password',
				method: 'put',
				data: params,
			});
		},
		resetPassword: (params?: object) => {
			return request({
				url: '/api/system/user/resetPassword',
				method: 'put',
				params,
			});
		},
		getUserDetail: (userId: string) => {
			return request({
				url: `/api/system/user/${userId}`,
				method: 'get',
			});
		},
		freezeUser: (id: string) => {
			return request({
				url: `/api/system/user/freeze/${id}`,
				method: 'put',
			});
		},
		unfreezeUser: (id: string) => {
			return request({
				url: `/api/system/user/unfreeze/${id}`,
				method: 'put',
			});
		},
		exportData: (params?: any) => {
			const exportParams: Record<string, any> = { ...params };
			if (Array.isArray(exportParams.selectedFields)) {
				exportParams.selectedFields = exportParams.selectedFields.join(',');
			}
			return request({
				url: '/api/excel/export/execute',
				method: 'get',
				params: {
					exportType: 'systemUser',
					...exportParams,
				},
				responseType: 'blob',
			});
		},
		assignUsersToDept: (params?: object) => {
			return request({
				url: '/api/system/user/assignUsersToDept',
				method: 'post',
				data: params,
			});
		},
		removeUsersFromDept: (params?: object) => {
			return request({
				url: '/api/system/user/removeUsersFromDept',
				method: 'post',
				data: params,
			});
		},
		getUsersByIds: (userIds: string) => {
			return request({
				url: '/api/system/user/getUsersByIds',
				method: 'get',
				params: { userIds: userIds },
			});
		},
		addRoleUsers: (params?: object) => {
			return request({
				url: '/api/system/user/addRoleUsers',
				method: 'post',
				data: params,
			});
		},
		removeRoleUsers: (params?: object) => {
			return request({
				url: '/api/system/user/removeRoleUsers',
				method: 'post',
				data: params,
			});
		},
	};
}