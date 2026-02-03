<template>
	<div class="h100" v-show="!isTagsViewCurrenFull">
		<el-aside class="layout-aside" :class="setCollapseStyle">
			<Logo v-if="setShowLogo" />
			<el-scrollbar class="flex-auto" :height="setAsideHeight" ref="layoutAsideScrollbarRef" @mouseenter="onAsideEnterLeave(true)" @mouseleave="onAsideEnterLeave(false)">
				<Vertical :menuList="state.menuList" />
			</el-scrollbar>
		</el-aside>
	</div>
</template>

<script setup lang="ts" name="layoutAside">
/**
 * 侧边栏布局组件
 * 负责渲染左侧导航菜单，支持多种布局模式（默认、经典、分栏）
 * 包含移动端适配、菜单展开/收起、Logo显示等功能
 * @author Eric
 * @since 2026-01-30
 */
import { defineAsyncComponent, reactive, computed, watch, onBeforeMount, ref } from 'vue';
import { storeToRefs } from 'pinia';
import { useRoutesList } from '@/stores/routesList';
import { useThemeConfig } from '@/stores/themeConfig';
import { useTagsViewRoutes } from '@/stores/tagsViewRoutes';
import mittBus from '@/utils/mitt';

// 异步引入Logo组件
const Logo = defineAsyncComponent(() => import('@/layout/logo/index.vue'));
// 异步引入垂直菜单组件
const Vertical = defineAsyncComponent(() => import('@/layout/navMenu/vertical.vue'));

// 侧边栏滚动条引用
const layoutAsideScrollbarRef = ref();
// 路由列表状态管理
const stores = useRoutesList();
// 主题配置状态管理
const storesThemeConfig = useThemeConfig();
// 标签视图路由状态管理
const storesTagsViewRoutes = useTagsViewRoutes();
// 从store中解构响应式数据
const { routesList } = storeToRefs(stores);
const { themeConfig } = storeToRefs(storesThemeConfig);
const { isTagsViewCurrenFull } = storeToRefs(storesTagsViewRoutes);

// 组件内部状态
const state = reactive<AsideState>({
	menuList: [], // 当前显示的菜单列表
	clientWidth: 0, // 客户端窗口宽度
});

/**
 * 计算侧边栏高度
 * 根据是否显示Logo动态计算可用高度
 * @returns {string} 计算后的CSS高度值
 */
const setAsideHeight = computed(() => {
	return `calc(100vh - ${setShowLogo.value ? 50 : 0}px)`;
});

/**
 * 计算侧边栏折叠样式
 * 根据当前布局模式、窗口宽度、折叠状态返回对应的CSS类名
 * 支持移动端适配和多种布局模式（columns、classic、defaults）
 * @returns {string[]} CSS类名数组
 */
const setCollapseStyle = computed(() => {
	const { layout, isCollapse, menuBar } = themeConfig.value;
	// 白色背景主题列表
	const asideBrTheme = ['#FFFFFF', '#FFF', '#fff', '#ffffff'];
	// 判断是否需要添加边框颜色类
	const asideBrColor = asideBrTheme.includes(menuBar) ? 'layout-el-aside-br-color' : '';
	// 移动端适配（窗口宽度小于等于1000px）
	if (state.clientWidth <= 1000) {
		if (isCollapse) {
			// 展开状态：添加遮罩层并阻止背景滚动
			document.body.setAttribute('class', 'el-popup-parent--hidden');
			const asideEle = document.querySelector('.layout-container') as HTMLElement;
			const modeDivs = document.createElement('div');
			modeDivs.setAttribute('class', 'layout-aside-mobile-mode');
			asideEle.appendChild(modeDivs);
			modeDivs.addEventListener('click', closeLayoutAsideMobileMode);
			return [asideBrColor, 'layout-aside-mobile', 'layout-aside-mobile-open'];
		} else {
			// 收起状态：移除遮罩层
			closeLayoutAsideMobileMode();
			return [asideBrColor, 'layout-aside-mobile', 'layout-aside-mobile-close'];
		}
	} else {
		// PC端适配
		if (layout === 'columns' || layout === 'classic') {
			// 分栏布局或经典布局
			if (isCollapse) return [asideBrColor, 'layout-aside-pc-1'];
			else return [asideBrColor, 'layout-aside-pc-220'];
		} else {
			// 默认布局
			if (isCollapse) return [asideBrColor, 'layout-aside-pc-64'];
			else return [asideBrColor, 'layout-aside-pc-220'];
		}
	}
});

/**
 * 计算是否显示Logo
 * 仅在默认布局或分栏布局且配置允许时显示
 * @returns {boolean} 是否显示Logo
 */
const setShowLogo = computed(() => {
	let { layout, isShowLogo } = themeConfig.value;
	return (isShowLogo && layout === 'defaults') || (isShowLogo && layout === 'columns');
});

/**
 * 关闭移动端侧边栏遮罩模式
 * 移除遮罩层DOM元素并恢复body滚动
 */
const closeLayoutAsideMobileMode = () => {
	const el = document.querySelector('.layout-aside-mobile-mode');
	el?.setAttribute('style', 'animation: error-img-two 0.3s');
	setTimeout(() => {
		el?.parentNode?.removeChild(el);
	}, 300);
	const clientWidth = document.body.clientWidth;
	if (clientWidth < 1000) themeConfig.value.isCollapse = false;
	document.body.setAttribute('class', '');
};

/**
 * 设置过滤后的路由菜单
 * 排除分栏布局，对其他布局进行路由过滤
 */
const setFilterRoutes = () => {
	if (themeConfig.value.layout === 'columns') return false;
	state.menuList = filterRoutesFun(routesList.value);
};

/**
 * 路由过滤递归函数
 * 过滤掉设置了isHide的路由项，并递归处理子路由
 * @template T 路由项类型
 * @param {T[]} arr 路由数组
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
 * 初始化菜单固定状态
 * 记录当前客户端窗口宽度
 * @param {number} clientWidth 客户端窗口宽度
 */
const initMenuFixed = (clientWidth: number) => {
	state.clientWidth = clientWidth;
};

/**
 * 侧边栏鼠标进入/离开事件处理
 * 仅在分栏布局且启用悬停预加载时生效
 * @param {boolean} bool 是否鼠标进入
 */
const onAsideEnterLeave = (bool: Boolean) => {
	let { layout } = themeConfig.value;
	if (layout !== 'columns') return false;
	if (!bool) mittBus.emit('restoreDefault');
	if (themeConfig.value.isColumnsMenuHoverPreload) stores.setColumnsMenuHover(bool);
};

// 组件挂载前生命周期钩子
onBeforeMount(() => {
	// 初始化窗口宽度
	initMenuFixed(document.body.clientWidth);
	// 设置过滤后的路由菜单
	setFilterRoutes();
	// 监听分栏布局子菜单数据发送事件
	mittBus.on('setSendColumnsChildren', (res: MittMenu) => {
		state.menuList = res.children;
	});
	// 监听经典布局子菜单数据发送事件
	mittBus.on('setSendClassicChildren', (res: MittMenu) => {
		let { layout, isClassicSplitMenu } = themeConfig.value;
		if (layout === 'classic' && isClassicSplitMenu) {
			res.children.length <= 1 ? (themeConfig.value.isCollapse = true) : (themeConfig.value.isCollapse = false);
			state.menuList = [];
			state.menuList = res.children;
		}
	});
	// 监听面包屑索引设置过滤路由事件
	mittBus.on('getBreadcrumbIndexSetFilterRoutes', () => {
		setFilterRoutes();
	});
	// 监听布局移动端尺寸变化事件
	mittBus.on('layoutMobileResize', (res: LayoutMobileResize) => {
		initMenuFixed(res.clientWidth);
		closeLayoutAsideMobileMode();
	});
});

// 监听主题配置变化
watch(
	() => [themeConfig.value.isShowLogoChange, themeConfig.value.isShowLogo, themeConfig.value.layout, themeConfig.value.isClassicSplitMenu],
	([isShowLogoChange, isShowLogo, layout, isClassicSplitMenu]) => {
		if (isShowLogoChange !== isShowLogo) {
			if (layoutAsideScrollbarRef.value) layoutAsideScrollbarRef.value.update();
		}
		if (layout === 'classic' && isClassicSplitMenu) return false;
	}
);

// 监听路由列表变化
watch(
	() => routesList.value,
	() => {
		setFilterRoutes();
	}
);
</script>
