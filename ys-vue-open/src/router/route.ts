/**
 * 路由配置定义模块
 * 
 * 该模块定义了应用的所有路由配置，包括：
 * 1. 动态路由（dynamicRoutes）- 需要权限控制的路由，支持前端/后端两种控制模式
 * 2. 静态路由（staticRoutes）- 不需要权限控制的基础路由
 * 3. 404/401 错误页面路由（notFoundAndNoPower）
 * 
 * 路由元信息（RouteMeta）说明：
 * - title: 页面标题，用于标签页和浏览器标题
 * - isLink: 外链地址，不为空时表示外链
 * - isHide: 是否在侧边栏隐藏
 * - isKeepAlive: 是否缓存页面（KeepAlive）
 * - isAffix: 是否固定在标签栏（不可关闭）
 * - isIframe: 是否使用 iframe 打开
 * - roles: 允许访问的角色数组
 * - icon: 侧边栏菜单图标
 * 
 * @module router/route
 * @author Eric
 * @since 2025-07-01
 */

import { RouteRecordRaw } from 'vue-router';

/**
 * 扩展 Vue Router 的 RouteMeta 接口
 * 定义项目自定义的路由元信息字段
 */
declare module 'vue-router' {
	interface RouteMeta {
		/** 页面标题，用于标签页和浏览器标题栏显示 */
		title?: string;
		/** 外链地址，配置后点击菜单将在新窗口打开该链接 */
		isLink?: string;
		/** 是否在侧边栏菜单中隐藏该路由 */
		isHide?: boolean;
		/** 是否使用 KeepAlive 缓存该页面组件 */
		isKeepAlive?: boolean;
		/** 是否固定在标签栏（固定后不可关闭） */
		isAffix?: boolean;
		/** 是否使用 iframe 嵌入页面 */
		isIframe?: boolean;
		/** 允许访问该页面的角色数组，为空表示允许所有角色 */
		roles?: string[];
		/** 侧边栏菜单图标类名 */
		icon?: string;
	}
}

/**
 * 动态路由配置
 * 
 * 这些路由需要根据用户权限动态添加到路由表中
 * 支持两种控制模式：
 * 1. 前端控制：通过 meta.roles 字段在前端过滤
 * 2. 后端控制：从后端 API 获取，替换 children 数组
 * 
 * 路由结构说明：
 * - 根路由（path: '/'）作为 Layout 布局容器
 * - 所有业务页面作为根路由的 children
 * - 支持多级嵌套路由（如 /system/user）
 */
export const dynamicRoutes: Array<RouteRecordRaw> = [
	{
		path: '/',
		name: '/',
		component: () => import('@/layout/index.vue'),
		redirect: '/home',
		meta: {
			isKeepAlive: true,
		},
		children: [
			{
				path: '/home',
				name: 'home',
				component: () => import('@/views/home/index.vue'),
				meta: {
					title: 'message.router.home',
					isLink: '',
					isHide: false,
					isKeepAlive: true,
					isAffix: true,
					isIframe: false,
					roles: ['admin', 'common'],
					icon: 'iconfont icon-shouye',
				},
			},
			{
				path: '/system',
				name: 'system',
				component: () => import('@/layout/routerView/parent.vue'),
				redirect: '/system/menu',
				meta: {
					title: 'message.router.system',
					isLink: '',
					isHide: false,
					isKeepAlive: true,
					isAffix: false,
					isIframe: false,
					roles: ['admin'],
					icon: 'iconfont icon-xitongshezhi',
				},
				children: [
					{
						path: '/system/menu',
						name: 'systemMenu',
						component: () => import('@/views/system/menu/index.vue'),
						meta: {
							title: 'message.router.systemMenu',
							isLink: '',
							isHide: false,
							isKeepAlive: true,
							isAffix: false,
							isIframe: false,
							roles: ['admin'],
							icon: 'iconfont icon-caidan',
						},
					},
					{
						path: '/system/role',
						name: 'systemRole',
						component: () => import('@/views/system/role/index.vue'),
						meta: {
							title: 'message.router.systemRole',
							isLink: '',
							isHide: false,
							isKeepAlive: true,
							isAffix: false,
							isIframe: false,
							roles: ['admin'],
							icon: 'ele-ColdDrink',
						},
					},
					{
						path: '/system/user',
						name: 'systemUser',
						component: () => import('@/views/system/user/index.vue'),
						meta: {
							title: 'message.router.systemUser',
							isLink: '',
							isHide: false,
							isKeepAlive: true,
							isAffix: false,
							isIframe: false,
							roles: ['admin'],
							icon: 'iconfont icon-icon-',
						},
					},
					{
						path: '/system/dept',
						name: 'systemDept',
						component: () => import('@/views/system/dept/index.vue'),
						meta: {
							title: 'message.router.systemDept',
							isLink: '',
							isHide: false,
							isKeepAlive: true,
							isAffix: false,
							isIframe: false,
							roles: ['admin'],
							icon: 'ele-OfficeBuilding',
						},
					},
				],
			},
		],
	},
];

/**
 * 404 和 401 错误页面路由
 * 
 * 这些路由用于处理未匹配的路径和无权限访问的情况
 * 会在动态路由处理完成后添加到路由表中
 */
export const notFoundAndNoPower = [
	{
		path: '/:path(.*)*',
		name: 'notFound',
		component: () => import('@/views/error/404.vue'),
		meta: {
			title: 'message.staticRoutes.notFound',
			isHide: true,
		},
	},
	{
		path: '/401',
		name: 'noPower',
		component: () => import('@/views/error/401.vue'),
		meta: {
			title: 'message.staticRoutes.noPower',
			isHide: true,
		},
	},
];

/**
 * 静态路由配置
 * 
 * 这些路由不需要权限控制，在应用初始化时就直接注册到路由表中
 * 包括：根路由重定向、首页、登录页、以及一些公开页面
 */
export const staticRoutes: Array<RouteRecordRaw> = [
	{
		path: '/',
		name: 'root',
		redirect: '/home',
		meta: {
			isHide: true,
		},
	},
	{
		path: '/home',
		name: 'home',
		component: () => import('@/views/home/index.vue'),
		meta: {
			title: 'message.router.home',
			isLink: '',
			isHide: false,
			isKeepAlive: true,
			isAffix: true,
			isIframe: false,
			roles: ['admin', 'common'],
			icon: 'iconfont icon-shouye',
		},
	},
	{
		path: '/login',
		name: 'login',
		component: () => import('@/views/login/index.vue'),
		meta: {
			title: '登录',
		},
	},
];
