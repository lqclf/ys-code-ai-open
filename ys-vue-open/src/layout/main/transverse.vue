<template>
	<el-container class="layout-container flex-center layout-backtop">
		<LayoutHeader />
		<LayoutMain ref="layoutMainRef" />
	</el-container>
</template>

<script setup lang="ts" name="layoutTransverse">
/**
 * 横向布局模式
 * 顶部为横向导航菜单和Header，下方为主内容区
 * 适用于菜单项较少或需要横向展示的场景
 * @author Eric
 * @since 2026-01-30
 */
import { defineAsyncComponent, ref, watch, nextTick, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '@/stores/themeConfig';

// 异步引入头部组件
const LayoutHeader = defineAsyncComponent(() => import('@/layout/component/header.vue'));
// 异步引入主内容区组件
const LayoutMain = defineAsyncComponent(() => import('@/layout/component/main.vue'));

// 主内容区组件引用
const layoutMainRef = ref<InstanceType<typeof LayoutMain>>();
// 主题配置状态管理
const storesThemeConfig = useThemeConfig();
// 从store中解构响应式数据
const { themeConfig } = storeToRefs(storesThemeConfig);
// 当前路由对象
const route = useRoute();

/**
 * 重置滚动条高度
 * 更新主内容区滚动条
 */
const updateScrollbar = () => {
	layoutMainRef.value!.layoutMainScrollbarRef.update();
};

/**
 * 初始化滚动条高度
 * 由于组件是异步引入的，需要延迟执行
 */
const initScrollBarHeight = () => {
	nextTick(() => {
		setTimeout(() => {
			updateScrollbar();
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
	() => themeConfig.value.isTagsview,
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
