import request from '@/utils/request';

/**
 * 系统日志API
 * 提供日志分页查询、详情查询、删除以及枚举字典查询能力
 */
export function useLoggerApi() {
	return {
		/**
		 * 分页查询日志
		 * @param params 查询条件，支持 logTypes/logLevel/modul/type/userName 等
		 */
		list: (params?: object) => {
			return request({
				url: '/api/system/logger/list',
				method: 'get',
				params,
			});
		},
		/**
		 * 查询日志相关枚举字典（日志类型、日志级别、操作类型）
		 */
		enums: () => {
			return request({
				url: '/api/system/logger/enums',
				method: 'get',
			});
		},
		/**
		 * 批量删除日志
		 * @param ids 日志ID，多个以英文逗号分隔
		 */
		delete: (ids?: string) => {
			return request({
				url: `/api/system/logger/delete/${ids}`,
				method: 'delete',
			});
		},
		/**
		 * 通过id查询日志详情
		 * @param id 日志ID
		 */
		getById: (id?: string) => {
			return request({
				url: `/api/system/logger/${id}`,
				method: 'get',
			});
		},
	};
}
