<template>
	<el-container class="layout-container">
		<ColumnsAside />
		<el-container class="layout-columns-warp layout-container-view h100">
			<LayoutAside />
			<el-scrollbar ref="layoutScrollbarRef" class="layout-backtop">
				<LayoutHeader />
				<LayoutMain ref="layoutMainRef" />
			</el-scrollbar>
		</el-container>
	</el-container>
</template>

<script setup lang="ts" name="layoutColumns">
/**
 * 分栏布局模式
 * 左侧为一级菜单栏（columnsAside），中间为二级菜单栏（aside），右侧为主内容区
 * 适用于菜单层级较多的后台管理系统
 * @author Eric
 * @since 2026-01-30
 */
import { defineAsyncComponent, watch, onMounted, nextTick, ref } from 'vue';
import { useRoute } from 'vue-router';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '@/stores/themeConfig';

// 异步引入侧边栏组件
const LayoutAside = defineAsyncComponent(() => import('@/layout/component/aside.vue'));
// 异步引入头部组件
const LayoutHeader = defineAsyncComponent(() => import('@/layout/component/header.vue'));
// 异步引入主内容区组件
const LayoutMain = defineAsyncComponent(() => import('@/layout/component/main.vue'));
// 异步引入分栏侧边栏组件（一级菜单）
const ColumnsAside = defineAsyncComponent(() => import('@/layout/component/columnsAside.vue'));

// 外层滚动条引用
const layoutScrollbarRef = ref<RefType>('');
// 主内容区组件引用
const layoutMainRef = ref<InstanceType<typeof LayoutMain>>();
// 当前路由对象
const route = useRoute();
// 主题配置状态管理
const storesThemeConfig = useThemeConfig();
// 从store中解构响应式数据
const { themeConfig } = storeToRefs(storesThemeConfig);

/**
 * 重置滚动条高度
 * 同时更新外层滚动条和主内容区滚动条
 */
const updateScrollbar = () => {
	// 更新父级 scrollbar
	layoutScrollbarRef.value.update();
	// 更新子级 scrollbar
	layoutMainRef.value && layoutMainRef.value!.layoutMainScrollbarRef.update();
};

/**
 * 初始化滚动条高度
 * 由于组件是异步引入的，需要延迟执行
 */
const initScrollBarHeight = () => {
	nextTick(() => {
		setTimeout(() => {
			updateScrollbar();
			layoutScrollbarRef.value.wrapRef.scrollTop = 0;
			layoutMainRef.value!.layoutMainScrollbarRef.wrapRef.scrollTop = 0;
		}, 500);
	});
};

/**
 * 页面加载时生命周期钩子
 * 初始化滚动条高度
 */
onMounted(() => {
	initScrollBarHeight();
});

// 监听路由的变化，切换界面时，滚动条置顶
watch(
	() => route.path,
	() => {
		initScrollBarHeight();
	}
);

// 监听 themeConfig 配置文件的变化，更新菜单 el-scrollbar 的高度
watch(
	() => [themeConfig.value.isTagsview, themeConfig.value.isFixedHeader],
	() => {
		nextTick(() => {
			updateScrollbar();
		});
	},
	{
		deep: true,
	}
);
</script>
