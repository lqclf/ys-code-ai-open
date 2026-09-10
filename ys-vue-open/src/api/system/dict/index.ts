import request from '@/utils/request';

/**
 * 字典管理API接口
 */
export function useDictApi() {
	return {
		/**
		 * 获取字典列表
		 * @param params 参数
		 * @returns 
		 */
		list: (params: any) => {
			return request({
				url: '/api/system/dict/list',
				method: 'get',
				params,
			});
		},
		/**
		 * 新增字典
		 * @param data 参数
		 * @returns 
		 */
		add: (params?: object) => {
			return request({
				url: '/api/system/dict/add',
				method: 'post',
				params,
			});
		},
		/**
		 * 修改字典
		 * @param data 参数
		 * @returns 
		 */
		update: (params?: object) => {
			return request({
				url: '/api/system/dict/update',
				method: 'put',
				params,
			});
		},
		/**
		 * 删除字典
		 * @param id 参数
		 * @returns 
		 */
		delete: (ids: string) => {
			return request({
				url: `/api/system/dict/delete/${ids}`,
				method: 'delete',
			});
		},
		/**
		 * 获取字典详情
		 * @param id 参数
		 * @returns 
		 */
		getById: (id: string) => {
			return request({
				url: `/api/system/dict/${id}`,
				method: 'get',
			});
		},
		/**
		 * 获取字典明细列表
		 * @param params 参数
		 * @returns 
		 */
		getDetailList: (params: any) => {
			return request({
				url: '/api/system/dictdetail/list',
				method: 'get',
				params,
			});
		},
		/**
		 * 新增字典明细
		 * @param params 参数
		 * @returns 
		 */
		addDetail: (params?: object) => {
			return request({
				url: '/api/system/dictdetail/add',
				method: 'post',
				params,
			});
		},
		/**
		 * 修改字典明细
		 * @param params 参数
		 * @returns 
		 */
		updateDetail: (params?: object) => {
			return request({
				url: '/api/system/dictdetail/update',
				method: 'put',
				params,
			});
		},
		/**
		 * 删除字典明细
		 * @param id 参数
		 * @returns
		 */
		delDetail: (ids: string) => {
			return request({
				url: `/api/system/dictdetail/delete/${ids}`,
				method: 'delete',
			});
		},
		/**
		 * 根据字典编码查询字典明细列表
		 * @param dictCode 字典编码
		 * @returns 字典明细列表
		 */
		getDetailByCode: (dictCode: string) => {
			return request({
				url: `/api/system/dictdetail/code/${dictCode}`,
				method: 'get',
			});
		},
	};
}