<template>
	<el-container class="layout-container flex-center">
		<LayoutHeader />
		<el-container class="layout-mian-height-50">
			<LayoutAside />
			<div class="flex-center layout-backtop">
				<LayoutTagsView v-if="isTagsview" />
				<LayoutMain ref="layoutMainRef" />
			</div>
		</el-container>
	</el-container>
</template>

<script setup lang="ts" name="layoutClassic">
/**
 * 经典布局模式
 * 顶部为Header，左侧为侧边栏，右侧为主内容区
 * 适用于传统的后台管理系统布局
 * @author Eric
 * @since 2026-01-30
 */
import { defineAsyncComponent, computed, ref, watch, nextTick, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '@/stores/themeConfig';

// 异步引入侧边栏组件
const LayoutAside = defineAsyncComponent(() => import('@/layout/component/aside.vue'));
// 异步引入头部组件
const LayoutHeader = defineAsyncComponent(() => import('@/layout/component/header.vue'));
// 异步引入主内容区组件
const LayoutMain = defineAsyncComponent(() => import('@/layout/component/main.vue'));
// 异步引入标签视图组件
const LayoutTagsView = defineAsyncComponent(() => import('@/layout/navBars/tagsView/tagsView.vue'));

// 主内容区组件引用
const layoutMainRef = ref<InstanceType<typeof LayoutMain>>();
// 当前路由对象
const route = useRoute();
// 主题配置状态管理
const storesThemeConfig = useThemeConfig();
// 从store中解构响应式数据
const { themeConfig } = storeToRefs(storesThemeConfig);

/**
 * 计算是否显示标签视图
 * @returns {boolean} 是否显示标签视图
 */
const isTagsview = computed(() => {
	return themeConfig.value.isTagsview;
});

/**
 * 更新滚动条
 * 调用主内容区滚动条的update方法
 */
const updateScrollbar = () => {
	layoutMainRef.value?.layoutMainScrollbarRef.update();
};

/**
 * 初始化滚动条高度
 * 在DOM更新后重置滚动条位置到顶部
 */
const initScrollBarHeight = () => {
	nextTick(() => {
		setTimeout(() => {
			updateScrollbar();
			if (layoutMainRef.value) layoutMainRef.value!.layoutMainScrollbarRef.wrapRef.scrollTop = 0;
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

// 监听路由变化，切换界面时滚动条置顶
watch(
	() => route.path,
	() => {
		initScrollBarHeight();
	}
);

// 监听标签视图配置变化，更新滚动条
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
