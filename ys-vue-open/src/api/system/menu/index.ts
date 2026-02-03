import request from '@/utils/request';

/**
 * 以下为模拟接口地址，gitee 的不通，就换自己的真实接口地址
 *
 * （不建议写成 request.post(xxx)，因为这样 post 时，无法 params 与 data 同时传参）
 *
 * 后端控制路由，isRequestRoutes 为 true，则开启后端控制路由
 * @method getAdminMenu 获取后端动态路由菜单(admin)
 * @method getTestMenu 获取后端动态路由菜单(test)
 */
export function useMenuApi() {
	return {
		// 获取用户菜单 层级结构，带children
		getAdminMenu: (params?: object) => {
			return request({
				url: '/api/system/sysmenu/userMenus',
				method: 'get',
				params,
			});
		},
		// 获取菜单树  平级结构，所有的菜单数据，不带children
		getMenuList: (params?: object) => {
			return request({
				url: '/api/system/sysmenu/getMenuList',
				method: 'get',
				params,
			});
		},
		// 获取角色授权菜单树 --角色授权使用
		getMenuRoleList: () => {
			return request({
				url: '/api/system/sysmenu/getMenuRoleList',
				method: 'get',
			});
		},
		// 获取菜单下拉列表树
		selectMenuTree: (params?: object) => {
			return request({
				url: '/api/system/sysmenu/selectMenuTree',
				method: 'get',
				params,
			});
		},
		addMenu: (params?: object) => {
			return request({
				url: '/api/system/sysmenu/add',
				method: 'post',
				params,
			});
		},
		updateMenu: (params?: object) => {
			return request({
				url: '/api/system/sysmenu/update',
				method: 'put',
				params,
			});
		},
		deleteMenu: (ids?: String) => {
			return request({
				url: '/api/system/sysmenu/delete/' + ids,
				method: 'delete',
			});
		},
		getMenu: (id?: String) => {
			return request({
				url: '/api/system/sysmenu/' + id,
				method: 'get',
			});
		},
	};
}
