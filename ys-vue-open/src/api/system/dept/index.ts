import request from '@/utils/request';

/**
 * 以下为模拟接口地址，gitee 的不通，就换自己的真实接口地址
 *
 * （不建议写成 request.post(xxx)，因为这样 post 时，无法 params 与 data 同时传参）
 *
 * 后端控制路由，isRequestRoutes 为 true，则开启后端控制路由
 * @method getAdminDept 获取后端动态路由部门(admin)
 * @method getTestDept 获取后端动态路由部门(test)
 */
export function useDeptApi() {
	return {
		// 获取用户部门 层级结构，带children
		getAdminDept: (params?: object) => {
			return request({
				url: '/api/system/dept/userDepts',
				method: 'get',
				params,
			});
		},
		// 获取部门树  平级结构，所有的部门数据，不带children
		getDeptList: (params?: object) => {
			return request({
				url: '/api/system/dept/getDeptList',
				method: 'get',
				params,
			});
		},
		// 获取部门下拉列表树
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
