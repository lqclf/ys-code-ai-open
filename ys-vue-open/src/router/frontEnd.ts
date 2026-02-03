/**
 * 前端控制路由模块
 * 
 * 该模块负责基于前端定义的路由配置和用户角色权限，动态构建路由系统。
 * 主要功能包括：
 * 1. 根据用户角色过滤路由
 * 2. 动态添加/重置路由
 * 3. 设置菜单列表和标签视图
 * 
 * 与后端控制路由的区别：路由数据在前端静态定义，通过 roles 字段控制访问权限
 * 
 * @module router/frontEnd
 * @author Eric
 * @since 2025-07-01
 */

import { RouteRecordRaw } from 'vue-router';
import { storeToRefs } from 'pinia';
import { formatTwoStageRoutes, formatFlatteningRoutes, router } from '@/router/index';
import { dynamicRoutes, notFoundAndNoPower } from '@/router/route';
import pinia from '@/stores/index';
import { Session } from '@/utils/storage';
import { useUserInfo } from '@/stores/userInfo';
import { useTagsViewRoutes } from '@/stores/tagsViewRoutes';
import { useRoutesList } from '@/stores/routesList';
import { NextLoading } from '@/utils/loading';

/**
 * 初始化前端控制路由
 * 
 * 这是前端路由控制的核心入口函数，执行流程：
 * 1. 显示加载动画
 * 2. 检查用户登录状态（Token）
 * 3. 获取用户信息
 * 4. 检查用户是否有角色权限
 * 5. 添加动态路由
 * 6. 设置菜单列表和标签视图
 * 
 * @returns {Promise<boolean>} 初始化成功返回 true，无权限返回 true，未登录返回 false
 * @example
 * await initFrontEndControlRoutes();
 */
export async function initFrontEndControlRoutes() {
	// 开启页面加载动画
	if (window.nextLoading === undefined) NextLoading.start();
	
	// 检查用户是否已登录，未登录则返回 false
	if (!Session.get('token')) return false;
	
	// 获取并设置用户信息
	await useUserInfo(pinia).setUserInfos();
	
	// 检查用户是否有角色权限，无权限则返回
	if (useUserInfo().userInfos.roles.length <= 0) return Promise.resolve(true);
	
	// 添加动态路由到 Vue Router
	await setAddRoute();
	
	// 设置菜单列表和标签视图路由
	setFilterMenuAndCacheTagsViewRoutes();
}

/**
 * 添加路由到 Vue Router 实例
 * 
 * 将过滤后的动态路由添加到 Vue Router 中
 */
export async function setAddRoute() {
	await setFilterRouteEnd().forEach((route: RouteRecordRaw) => {
		router.addRoute(route);
	});
}

/**
 * 重置前端路由
 * 
 * 用于用户切换或权限变更时，移除已添加的动态路由
 * 遍历所有过滤后的路由，如果存在则移除
 */
export async function frontEndsResetRoute() {
	await setFilterRouteEnd().forEach((route: RouteRecordRaw) => {
		const routeName: any = route.name;
		// 检查路由是否存在，存在则移除
		router.hasRoute(routeName) && router.removeRoute(routeName);
	});
}

/**
 * 设置过滤后的最终路由
 * 
 * 对动态路由进行格式化处理，根据用户角色过滤路由，
 * 并添加 404 和 401 等通配符路由
 * 
 * @returns {Array} 处理后的路由数组
 */
export function setFilterRouteEnd() {
	// 将动态路由扁平化后格式化为两级结构
	let filterRouteEnd: any = formatTwoStageRoutes(formatFlatteningRoutes(dynamicRoutes));
	// 根据用户角色过滤路由，并添加 404 和 401 页面路由
	filterRouteEnd[0].children = [...setFilterRoute(filterRouteEnd[0].children), ...notFoundAndNoPower];
	return filterRouteEnd;
}

/**
 * 根据用户角色过滤路由
 * 
 * 遍历路由数组，根据路由 meta.roles 和当前用户角色进行匹配过滤
 * 只保留用户有权限访问的路由
 * 
 * @param {Array} chil - 需要过滤的路由数组
 * @returns {Array} 过滤后的路由数组
 * @example
 * const filteredRoutes = setFilterRoute(routes);
 */
export function setFilterRoute(chil: any) {
	const stores = useUserInfo(pinia);
	const { userInfos } = storeToRefs(stores);
	let filterRoute: any = [];
	
	// 遍历所有路由，检查角色权限
	chil.forEach((route: any) => {
		if (route.meta.roles) {
			// 遍历路由所需角色
			route.meta.roles.forEach((metaRoles: any) => {
				// 遍历用户拥有的角色
				userInfos.value.roles.forEach((roles: any) => {
					// 角色匹配则添加到过滤后的路由数组
					if (metaRoles === roles) filterRoute.push({ ...route });
				});
			});
		}
	});
	
	return filterRoute;
}

/**
 * 设置缓存的标签视图路由
 * 
 * 根据用户角色过滤动态路由，并将格式化后的数据保存到 tagsViewRoutes Store 中
 * 用于标签导航栏的显示
 */
export function setCacheTagsViewRoutes() {
	const stores = useUserInfo(pinia);
	const storesTagsView = useTagsViewRoutes(pinia);
	const { userInfos } = storeToRefs(stores);
	
	// 根据用户角色过滤路由
	let rolesRoutes = setFilterHasRolesMenu(dynamicRoutes, userInfos.value.roles);
	
	// 将过滤后的路由格式化为两级结构，并设置到标签视图
	storesTagsView.setTagsViewRoutes(formatTwoStageRoutes(formatFlatteningRoutes(rolesRoutes))[0].children);
}

/**
 * 设置过滤后的菜单列表并缓存标签视图路由
 * 
 * 根据用户角色过滤动态路由的子路由，保存到 routesList Store 中供侧边栏菜单使用
 * 同时设置标签视图路由
 */
export function setFilterMenuAndCacheTagsViewRoutes() {
	const stores = useUserInfo(pinia);
	const storesRoutesList = useRoutesList(pinia);
	const { userInfos } = storeToRefs(stores);
	
	// 根据用户角色过滤动态路由的子路由，设置为菜单列表
	storesRoutesList.setRoutesList(setFilterHasRolesMenu(dynamicRoutes[0].children, userInfos.value.roles));
	
	// 设置缓存的标签视图路由
	setCacheTagsViewRoutes();
}

/**
 * 检查用户是否拥有路由所需角色
 * 
 * 检查用户角色数组中是否有任一角色匹配路由所需角色
 * 
 * @param {Array} roles - 用户拥有的角色数组
 * @param {Object} route - 路由对象
 * @returns {boolean} 有权限返回 true，无权限返回 false
 * @example
 * const hasAccess = hasRoles(['admin'], { meta: { roles: ['admin', 'user'] } }); // true
 */
export function hasRoles(roles: any, route: any) {
	if (route.meta && route.meta.roles) {
		// 检查用户角色是否包含在路由所需角色中
		return roles.some((role: any) => route.meta.roles.includes(role));
	} else {
		// 路由未定义 roles，默认允许访问
		return true;
	}
}

/**
 * 递归过滤有权限的菜单
 * 
 * 遍历路由树，根据用户角色递归过滤出有权限访问的菜单
 * 保留有权限的路由及其子路由
 * 
 * @param {Array} routes - 路由数组
 * @param {Array} roles - 用户角色数组
 * @returns {Array} 过滤后的菜单数组
 * @example
 * const menu = setFilterHasRolesMenu(routes, ['admin']);
 */
export function setFilterHasRolesMenu(routes: any, roles: any) {
	const menu: any = [];
	
	routes.forEach((route: any) => {
		const item = { ...route };
		// 检查当前路由是否有权限
		if (hasRoles(roles, item)) {
			// 递归处理子路由
			if (item.children) {
				item.children = setFilterHasRolesMenu(item.children, roles);
			}
			menu.push(item);
		}
	});
	
	return menu;
}
