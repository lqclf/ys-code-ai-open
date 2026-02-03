<template>
	<div class="layout-navbars-breadcrumb-index">
		<!-- Logo 组件，根据布局配置条件显示 -->
		<Logo v-if="setIsShowLogo" />
		<!-- 面包屑导航组件 -->
		<Breadcrumb />
		<!-- 横向菜单组件，仅在横向布局或经典布局分割菜单时显示 -->
		<Horizontal :menuList="state.menuList" v-if="isLayoutTransverse" />
		<!-- 用户菜单组件 -->
		<User />
	</div>
</template>

<script setup lang="ts" name="layoutBreadcrumbIndex">
import { defineAsyncComponent, computed, reactive, onMounted, onUnmounted } from 'vue';
import { useRoute } from 'vue-router';
import { storeToRefs } from 'pinia';
import { useRoutesList } from '@/stores/routesList';
import { useThemeConfig } from '@/stores/themeConfig';
import mittBus from '@/utils/mitt';

/**
 * 顶部导航栏容器组件
 * 整合 Logo、面包屑、横向菜单、用户菜单等组件
 * 根据布局配置动态控制各组件的显示/隐藏
 * @author Eric
 * @since 2026-01-30
 */

// 引入组件 - 使用异步加载优化性能
const Breadcrumb = defineAsyncComponent(() => import('@/layout/navBars/topBar/breadcrumb.vue'));
const User = defineAsyncComponent(() => import('@/layout/navBars/topBar/user.vue'));
const Logo = defineAsyncComponent(() => import('@/layout/logo/index.vue'));
const Horizontal = defineAsyncComponent(() => import('@/layout/navMenu/horizontal.vue'));

// 定义变量内容
const stores = useRoutesList();
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);
const { routesList } = storeToRefs(stores);
const route = useRoute();

/**
 * 响应式状态对象
 * @property {RouteItems} menuList - 菜单列表数据
 */
const state = reactive({
	menuList: [] as RouteItems,
});

/**
 * 计算属性：是否显示 Logo
 * 经典布局和横向布局时显示 Logo
 * @returns {boolean} 是否显示 Logo
 */
const setIsShowLogo = computed(() => {
	let { isShowLogo, layout } = themeConfig.value;
	return (isShowLogo && layout === 'classic') || (isShowLogo && layout === 'transverse');
});

/**
 * 计算属性：是否为横向布局
 * 横向布局或经典布局且开启分割菜单时显示横向菜单
 * @returns {boolean} 是否为横向布局
 */
const isLayoutTransverse = computed(() => {
	let { layout, isClassicSplitMenu } = themeConfig.value;
	return layout === 'transverse' || (isClassicSplitMenu && layout === 'classic');
});

/**
 * 设置过滤后的路由列表
 * 根据布局配置过滤路由，经典布局分割菜单时移除子菜单
 */
const setFilterRoutes = () => {
	let { layout, isClassicSplitMenu } = themeConfig.value;
	if (layout === 'classic' && isClassicSplitMenu) {
		// 经典布局分割菜单模式：移除子菜单，只保留一级菜单
		state.menuList = delClassicChildren(filterRoutesFun(routesList.value));
		const resData = setSendClassicChildren(route.path);
		// 发送当前选中菜单的子菜单数据
		mittBus.emit('setSendClassicChildren', resData);
	} else {
		// 其他布局：正常过滤路由
		state.menuList = filterRoutesFun(routesList.value);
	}
};

/**
 * 移除经典布局中的子菜单
 * 用于分割菜单模式，只保留一级菜单
 * @template T - 菜单项类型
 * @param {T[]} arr - 菜单数组
 * @returns {T[]} 移除子菜单后的数组
 */
const delClassicChildren = <T extends ChilType>(arr: T[]): T[] => {
	arr.map((v: T) => {
		if (v.children) delete v.children;
	});
	return arr;
};

/**
 * 过滤路由列表
 * 过滤掉隐藏的路由，并递归处理子路由
 * @template T - 路由项类型
 * @param {T[]} arr - 路由数组
 * @returns {T[]} 过滤后的路由数组
 */
const filterRoutesFun = <T extends RouteItem>(arr: T[]): T[] => {
	return arr
		.filter((item: T) => !item.meta?.isHide)
		.map((item: T) => {
			item = Object.assign({}, item);
			if (item.children) item.children = filterRoutesFun(item.children);
			return item;
		});
};

/**
 * 设置发送经典布局的子菜单数据
 * 根据当前路径获取对应的子菜单数据
 * @param {string} path - 当前路由路径
 * @returns {MittMenu} 包含当前菜单项和子菜单的数据对象
 */
const setSendClassicChildren = (path: string) => {
	const currentPathSplit = path.split('/');
	let currentData: MittMenu = { children: [] };
	filterRoutesFun(routesList.value).map((v: RouteItem, k: number) => {
		if (v.path === `/${currentPathSplit[1]}`) {
			v['k'] = k;
			currentData['item'] = { ...v };
			currentData['children'] = [{ ...v }];
			if (v.children) currentData['children'] = v.children;
		}
	});
	return currentData;
};

/**
 * 页面加载时生命周期钩子
 * 初始化路由过滤，监听路由更新事件
 */
onMounted(() => {
	setFilterRoutes();
	// 监听路由更新事件，重新过滤路由
	mittBus.on('getBreadcrumbIndexSetFilterRoutes', () => {
		setFilterRoutes();
	});
});

/**
 * 页面卸载时生命周期钩子
 * 移除事件监听，防止内存泄漏
 */
onUnmounted(() => {
	mittBus.off('getBreadcrumbIndexSetFilterRoutes', () => {});
});
</script>

<style scoped lang="scss">
.layout-navbars-breadcrumb-index {
	height: 50px;
	display: flex;
	align-items: center;
	background: var(--next-bg-topBar);
	border-bottom: 1px solid var(--next-border-color-light);
}
</style>
