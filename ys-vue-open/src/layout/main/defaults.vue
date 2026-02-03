<template>
	<el-container class="layout-container">
		<LayoutAside />
		<el-container class="layout-container-view h100">
			<el-scrollbar ref="layoutScrollbarRef" class="layout-backtop">
				<LayoutHeader />
				<LayoutMain ref="layoutMainRef" />
			</el-scrollbar>
		</el-container>
	</el-container>
</template>

<script setup lang="ts" name="layoutDefaults">
/**
 * 默认布局模式
 * 左侧为侧边栏，右侧为头部和主内容区
 * 最常用的后台管理系统布局方式
 * @author Eric
 * @since 2026-01-30
 */
import { defineAsyncComponent, watch, onMounted, nextTick, ref } from 'vue';
import { useRoute } from 'vue-router';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '@/stores/themeConfig';
import { NextLoading } from '@/utils/loading';

// 异步引入侧边栏组件
const LayoutAside = defineAsyncComponent(() => import('@/layout/component/aside.vue'));
// 异步引入头部组件
const LayoutHeader = defineAsyncComponent(() => import('@/layout/component/header.vue'));
// 异步引入主内容区组件
const LayoutMain = defineAsyncComponent(() => import('@/layout/component/main.vue'));

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
	layoutScrollbarRef.value?.update();
	layoutMainRef.value?.layoutMainScrollbarRef?.update();
};

/**
 * 初始化滚动条高度
 * 由于组件是异步引入的，需要延迟执行
 */
const initScrollBarHeight = () => {
	nextTick(() => {
		setTimeout(() => {
			updateScrollbar();
			if (layoutScrollbarRef.value) {
				layoutScrollbarRef.value.wrapRef.scrollTop = 0;
			}
			if (layoutMainRef.value?.layoutMainScrollbarRef) {
				layoutMainRef.value.layoutMainScrollbarRef.wrapRef.scrollTop = 0;
			}
		}, 500);
	});
};

/**
 * 页面加载时生命周期钩子
 * 初始化滚动条高度并关闭加载动画
 */
onMounted(() => {
	initScrollBarHeight();
	NextLoading.done(600);
});

// 监听路由的变化，切换界面时，滚动条置顶
watch(
	() => route.path,
	() => {
		initScrollBarHeight();
	}
);

// 监听主题配置变化，更新滚动条
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
