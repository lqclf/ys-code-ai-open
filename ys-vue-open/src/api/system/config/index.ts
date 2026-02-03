import request from '@/utils/request';

/**
 * 系统配置API
 */
export function useConfigApi() {
	return {
		/**
		 * 分页查询
		 */
		list: (params: any) => {
			return request({
				url: '/api/system/sysconfig/list',
				method: 'get',
				params,
			});
		},
		/**
		 * 新增
		 */
		add: (params: any) => {
			return request({
				url: '/api/system/sysconfig/add',
				method: 'post',
				params,
			});
		},
		/**
		 * 编辑
		 */
		update: (params: any) => {
			return request({
				url: '/api/system/sysconfig/update',
				method: 'put',
				params,
			});
		},
		/**
		 * 删除
		 */
		delete: (ids: string) => {
			return request({
				url: `/system/sysconfig/delete/${ids}`,
				method: 'delete',
			});
		},
		/**
		 * 通过id查询
		 */
		get: (id: string) => {
			return request({
				url: `/system/sysconfig/${id}`,
				method: 'get',
			});
		},
	};
}