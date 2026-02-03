/**
 * 后端控制路由模块
 * 
 * 该模块负责从后端 API 获取路由菜单数据，并动态构建前端路由系统。
 * 主要功能包括：
 * 1. 从后端获取菜单数据
 * 2. 动态导入组件
 * 3. 构建动态路由表
 * 4. 设置菜单和标签视图
 * 
 * @module router/backEnd
 * @author Eric
 * @since 2025-07-01
 */

import { RouteRecordRaw } from 'vue-router';
import { storeToRefs } from 'pinia';
import pinia from '@/stores/index';
import { useUserInfo } from '@/stores/userInfo';
import { useRequestOldRoutes } from '@/stores/requestOldRoutes';
import { Session } from '@/utils/storage';
import { NextLoading } from '@/utils/loading';
import { dynamicRoutes, notFoundAndNoPower } from '@/router/route';
import { formatTwoStageRoutes, formatFlatteningRoutes, router } from '@/router/index';
import { useRoutesList } from '@/stores/routesList';
import { useTagsViewRoutes } from '@/stores/tagsViewRoutes';
import { useMenuApi } from '@/api/system/menu';

// 菜单 API 实例
const menuApi = useMenuApi();

/**
 * 布局组件模块映射
 * 使用 import.meta.glob 动态导入 layout/routerView 目录下的所有 Vue/TSX 组件
 * 用于匹配布局级别的组件
 */
const layouModules: any = import.meta.glob('../layout/routerView/*.{vue,tsx}');

/**
 * 视图组件模块映射
 * 使用 import.meta.glob 动态导入 views 目录下的所有 Vue/TSX 组件
 * 用于匹配页面级别的组件
 */
const viewsModules: any = import.meta.glob('../views/**/*.{vue,tsx}');

/**
 * 合并后的动态视图模块
 * 将布局组件和视图组件合并为一个对象，用于后续动态导入
 */
const dynamicViewsModules: Record<string, Function> = Object.assign({}, { ...layouModules }, { ...viewsModules });

/**
 * 初始化后端控制路由
 * 
 * 这是后端路由控制的核心入口函数，执行流程：
 * 1. 显示加载动画
 * 2. 检查用户登录状态（Token）
 * 3. 获取用户信息
 * 4. 从后端获取菜单数据
 * 5. 处理菜单数据并构建动态路由
 * 6. 设置菜单列表和标签视图
 * 
 * @returns {Promise<boolean>} 初始化成功返回 true，失败返回 false
 * @example
 * await initBackEndControlRoutes();
 */
export async function initBackEndControlRoutes() {
	// 开启页面加载动画
	if (window.nextLoading === undefined) NextLoading.start();
	
	// 检查用户是否已登录，未登录则返回 false
	if (!Session.get('token')) {
		return false;
	}
	
	// 获取并设置用户信息
	await useUserInfo().setUserInfos();
	
	// 从后端获取路由菜单数据
	const res = await getBackEndControlRoutes();
	
	// 校验后端返回数据的有效性
	if (!res || !res.data || !Array.isArray(res.data) || res.data.length <= 0) {
		console.warn('获取菜单接口返回异常，使用默认路由配置');
		return Promise.resolve(true);
	}
	
	// 保存原始路由数据到状态管理（用于后续比较或恢复）
	useRequestOldRoutes().setRequestOldRoutes(JSON.parse(JSON.stringify(res.data)));
	
	// 将后端返回的组件路径转换为实际组件，并赋值给动态路由
	dynamicRoutes[0].children = await backEndComponent(res.data);
	
	// 添加路由到 Vue Router
	await setAddRoute();
	
	// 设置菜单列表和标签视图路由
	setFilterMenuAndCacheTagsViewRoutes();
}

/**
 * 设置过滤后的菜单列表并缓存标签视图路由
 * 
 * 将处理后的动态路由数据保存到状态管理中，
 * 供侧边栏菜单和标签视图使用
 */
export async function setFilterMenuAndCacheTagsViewRoutes() {
	const storesRoutesList = useRoutesList(pinia);
	// 将动态路由的子路由设置为菜单列表
	storesRoutesList.setRoutesList(dynamicRoutes[0].children as any);
	// 设置缓存的标签视图路由
	setCacheTagsViewRoutes();
}

/**
 * 设置缓存的标签视图路由
 * 
 * 将格式化后的路由数据保存到 tagsViewRoutes Store 中，
 * 用于标签导航栏的显示
 */
export function setCacheTagsViewRoutes() {
	const storesTagsView = useTagsViewRoutes(pinia);
	// 先扁平化路由，再格式化为两级路由结构，最后取子路由设置到标签视图
	storesTagsView.setTagsViewRoutes(formatTwoStageRoutes(formatFlatteningRoutes(dynamicRoutes))[0].children);
}

/**
 * 设置过滤后的最终路由
 * 
 * 对动态路由进行格式化处理，并添加 404 和 401 等通配符路由
 * 
 * @returns {Array} 处理后的路由数组
 */
export function setFilterRouteEnd() {
	// 将动态路由扁平化后格式化为两级结构
	let filterRouteEnd: any = formatTwoStageRoutes(formatFlatteningRoutes(dynamicRoutes));
	// 在子路由中添加 404 和 401 页面路由
	filterRouteEnd[0].children = [...filterRouteEnd[0].children, ...notFoundAndNoPower];
	return filterRouteEnd;
}

/**
 * 添加路由到 Vue Router 实例
 * 
 * 将处理后的路由动态添加到 Vue Router 中
 */
export async function setAddRoute() {
	await setFilterRouteEnd().forEach((route: RouteRecordRaw) => {
		router.addRoute(route);
	});
}

/**
 * 从后端获取控制路由数据
 * 
 * 调用后端 API 获取当前用户的菜单权限数据
 * 
 * @returns {Promise<any>} 后端返回的菜单数据
 */
export function getBackEndControlRoutes() {
	const stores = useUserInfo(pinia);
	const { userInfos } = storeToRefs(stores);
	const auth = userInfos.value.roles[0];
	// 调用菜单 API 获取管理员菜单
	return menuApi.getAdminMenu();
}

/**
 * 刷新后端控制路由
 * 
 * 用于重新获取后端路由数据（如权限变更后刷新）
 */
export async function setBackEndControlRefreshRoutes() {
	await getBackEndControlRoutes();
}

/**
 * 递归处理后端返回的路由组件
 * 
 * 遍历后端返回的路由数组，将 component 字符串转换为实际的组件导入函数
 * 
 * @param {Array} routes - 后端返回的路由数组
 * @returns {Array} 处理后的路由数组
 * @example
 * const processedRoutes = backEndComponent([
 *   { path: '/user', component: 'system/user/index' }
 * ]);
 */
export function backEndComponent(routes: any) {
	if (!routes) return;
	return routes.map((item: any) => {
		// 将组件路径字符串转换为动态导入函数
		if (item.component) item.component = dynamicImport(dynamicViewsModules, item.component as string);
		// 递归处理子路由
		item.children && backEndComponent(item.children);
		return item;
	});
}

/**
 * 动态导入组件
 * 
 * 根据组件路径从动态模块映射中找到匹配的组件并返回
 * 
 * @param {Record<string, Function>} dynamicViewsModules - 动态视图模块映射对象
 * @param {string} component - 组件路径（如 'system/user/index'）
 * @returns {Function|boolean} 匹配的组件导入函数，未找到返回 false
 * @example
 * const comp = dynamicImport(dynamicViewsModules, 'system/user/index');
 */
export function dynamicImport(dynamicViewsModules: Record<string, Function>, component: string) {
	const keys = Object.keys(dynamicViewsModules);
	// 过滤出匹配的组件路径
	const matchKeys = keys.filter((key) => {
		const k = key.replace(/..\/views|../, '');
		return k.startsWith(`${component}`) || k.startsWith(`/${component}`);
	});
	// 找到唯一匹配项，返回对应的组件
	if (matchKeys?.length === 1) {
		const matchKey = matchKeys[0];
		return dynamicViewsModules[matchKey];
	}
	// 找到多个匹配项，返回 false（存在歧义）
	if (matchKeys?.length > 1) {
		return false;
	}
}
