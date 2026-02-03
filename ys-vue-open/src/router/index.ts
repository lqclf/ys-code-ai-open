/**
 * 路由主入口模块
 * 
 * 该模块是 Vue Router 的核心配置文件，负责：
 * 1. 创建 Vue Router 实例
 * 2. 配置路由守卫（导航钩子）
 * 3. 提供路由格式化工具函数
 * 4. 整合前端控制和后端控制两种路由模式
 * 
 * 路由模式说明：
 * - 前端控制：路由配置在前端静态定义，通过 roles 字段控制访问权限
 * - 后端控制：路由配置从后端 API 动态获取，实现更灵活的权限管理
 * 
 * @module router/index
 * @author Eric
 * @since 2025-07-01
 */

import { createRouter, createWebHashHistory } from 'vue-router';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';
import pinia from '@/stores/index';
import { storeToRefs } from 'pinia';
import { useKeepALiveNames } from '@/stores/keepAliveNames';
import { useRoutesList } from '@/stores/routesList';
import { useThemeConfig } from '@/stores/themeConfig';
import { Session } from '@/utils/storage';
import { staticRoutes, notFoundAndNoPower } from '@/router/route';
import { initFrontEndControlRoutes } from '@/router/frontEnd';
import { initBackEndControlRoutes } from '@/router/backEnd';

// 初始化主题配置 Store
const storesThemeConfig = useThemeConfig(pinia);
const { themeConfig } = storeToRefs(storesThemeConfig);
const { isRequestRoutes } = themeConfig.value;

/**
 * 路由初始化状态标志
 * 用于防止重复初始化路由，避免路由守卫中的竞态条件
 */
let isRoutesInitializing = false;

/**
 * 创建 Vue Router 实例
 * 
 * 使用 Hash 模式（createWebHashHistory），兼容性更好
 * 初始路由包含 404/401 页面和静态路由（登录页等）
 */
export const router = createRouter({
	history: createWebHashHistory(),
	routes: [...notFoundAndNoPower, ...staticRoutes],
});

/**
 * 路由扁平化函数
 * 
 * 将嵌套的多级路由扁平化为一级路由数组
 * 用于处理嵌套路由的组件加载和缓存
 * 
 * @param {Array} arr - 嵌套的路由数组
 * @returns {Array|false} 扁平化后的路由数组，输入为空返回 false
 * @example
 * const flatRoutes = formatFlatteningRoutes([
 *   { path: '/', children: [{ path: 'home' }] }
 * ]);
 * // 结果: [{ path: '/' }, { path: 'home' }]
 */
export function formatFlatteningRoutes(arr: any) {
	if (arr.length <= 0) return false;
	for (let i = 0; i < arr.length; i++) {
		if (arr[i].children) {
			// 将子路由插入到当前位置，替换原路由
			arr = arr.slice(0, i + 1).concat(arr[i].children, arr.slice(i + 1));
		}
	}
	return arr;
}

/**
 * 路由格式化为两级结构
 * 
 * 将扁平化的路由数组格式化为两级结构（Layout + 子路由）
 * 同时处理动态路由标记和 KeepAlive 缓存
 * 
 * @param {Array} arr - 扁平化的路由数组
 * @returns {Array|false} 格式化后的两级路由数组
 * @example
 * const twoStageRoutes = formatTwoStageRoutes(flatRoutes);
 * // 结果: [{ path: '/', children: [...] }]
 */
export function formatTwoStageRoutes(arr: any) {
	if (arr.length <= 0) return false;
	const newArr: any = [];
	const cacheList: Array<string> = [];
	
	arr.forEach((v: any) => {
		if (v.path === '/') {
			// 根路由作为 Layout 容器
			newArr.push({ component: v.component, name: v.name, path: v.path, redirect: v.redirect, meta: v.meta, children: [] });
		} else {
			// 标记动态路由（带参数的路由）
			if (v.path.indexOf('/:') > -1) {
				v.meta['isDynamic'] = true;
				v.meta['isDynamicPath'] = v.path;
			}
			// 将非根路由添加到 Layout 的 children 中
			newArr[0].children.push({ ...v });
			
			// 处理 KeepAlive 缓存
			if (newArr[0].meta.isKeepAlive && v.meta.isKeepAlive) {
				cacheList.push(v.name);
				const stores = useKeepALiveNames(pinia);
				stores.setCacheKeepAlive(cacheList);
			}
		}
	});
	
	return newArr;
}

/**
 * 全局前置路由守卫
 * 
 * 在路由跳转前执行，主要功能：
 * 1. 显示进度条
 * 2. 检查登录状态（Token）
 * 3. 未登录用户重定向到登录页
 * 4. 已登录用户初始化动态路由
 * 5. 防止路由重复初始化
 * 
 * @param {RouteLocationNormalized} to - 目标路由
 * @param {RouteLocationNormalized} from - 来源路由
 * @param {NavigationGuardNext} next - 导航控制函数
 */
router.beforeEach(async (to, from, next) => {
	// 配置进度条，不显示旋转器
	NProgress.configure({ showSpinner: false });
	// 有标题时开始进度条
	if (to.meta.title) NProgress.start();
	
	// 获取登录 Token
	const token = Session.get('token');
	
	// 处理登录页访问逻辑
	if (to.path === '/login') {
		if (token) {
			// 已登录用户访问登录页，重定向到首页
			next('/home');
			NProgress.done();
		} else {
			// 未登录用户允许访问登录页
			next();
			NProgress.done();
		}
		return;
	}
	
	// 未登录用户访问非登录页，重定向到登录页
	if (!token) {
		next(`/login?redirect=${to.path}&params=${JSON.stringify(to.query ? to.query : to.params)}`);
		Session.clear();
		NProgress.done();
		return;
	}
	
	// 获取路由列表 Store
	const storesRoutesList = useRoutesList(pinia);
	const { routesList } = storeToRefs(storesRoutesList);
	
	// 路由已初始化或正在初始化，直接放行
	if (isRoutesInitializing || routesList.value.length > 0) {
		next();
		NProgress.done();
		return;
	}
	
	// 开始初始化路由
	isRoutesInitializing = true;
	try {
		// 根据配置选择路由控制模式
		if (isRequestRoutes) {
			// 后端控制模式：从 API 获取路由
			await initBackEndControlRoutes();
		} else {
			// 前端控制模式：使用前端定义的路由
			await initFrontEndControlRoutes();
		}
		isRoutesInitializing = false;
		NProgress.done();
		// 重新导航到目标页面，确保新路由生效
		next({ path: to.path, query: to.query });
	} catch (error) {
		// 路由初始化失败处理
		isRoutesInitializing = false;
		NProgress.done();
		console.error('路由初始化失败:', error);
		next();
	}
});

/**
 * 全局后置路由守卫
 * 
 * 在路由跳转完成后执行，主要功能：
 * 1. 结束进度条
 * 2. 可在此处添加页面统计、日志记录等功能
 */
router.afterEach(() => {
	NProgress.done();
});

export default router;
