import request from '@/utils/request';

/**
 * 日志模块接口
 */
export function useLoggerApi() {
	return {
		list: (params?: object) => {
			return request({
				url: '/api/system/logger/list',
				method: 'get',
				params,
			});
		},
		add: (params?: object) => {
			return request({
				url: '/api/system/logger/add',
				method: 'post',
				params,
			});
		},
		update: (params?: object) => {
			return request({
				url: '/api/system/logger/update',
				method: 'put',
				params,
			});
		},
		delete: (ids?: String) => {
			return request({
				url: '/api/system/logger/delete/' + ids,
				method: 'delete',
			});
		},
		getById: (id?: String) => {
			return request({
				url: '/api/system/logger/' + id,
				method: 'get',
			});
		},
	};
}
