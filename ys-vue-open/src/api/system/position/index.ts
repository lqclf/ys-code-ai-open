import request from '@/utils/request';

let currentBatchId: string = '';

/**
 * 岗位管理 API 接口集合
 */
export function usePositionApi() {
	return {
		list: (params?: object) => {
			return request({
				url: '/api/system/position/list',
				method: 'get',
				params,
			});
		},
		add: (params?: object) => {
			return request({
				url: '/api/system/position/add',
				method: 'post',
				params,
			});
		},
		update: (params?: object) => {
			return request({
				url: '/api/system/position/update',
				method: 'put',
				params,
			});
		},
		delete: (ids?: String) => {
			return request({
				url: '/api/system/position/delete/' + ids,
				method: 'delete',
			});
		},
		getById: (id?: String) => {
			return request({
				url: '/api/system/position/' + id,
				method: 'get',
			});
		},
		getPositionList: () => {
			return request({
				url: '/api/system/position/getPositionList',
				method: 'get',
			});
		},
	};
}