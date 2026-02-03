<template>
	<div class="layout-navbars-container">
		<BreadcrumbIndex />
		<TagsView v-if="setShowTagsView" />
	</div>
</template>

<script setup lang="ts" name="layoutNavBars">
/**
 * 导航栏容器组件
 * 作为顶部导航栏的容器，包含面包屑导航和标签视图
 * 根据布局配置控制标签视图的显示/隐藏
 * @author Eric
 * @since 2026-01-30
 */
import { defineAsyncComponent, computed } from 'vue';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '@/stores/themeConfig';

// 异步引入面包屑导航组件
const BreadcrumbIndex = defineAsyncComponent(() => import('@/layout/navBars/topBar/index.vue'));
// 异步引入标签视图组件
const TagsView = defineAsyncComponent(() => import('@/layout/navBars/tagsView/tagsView.vue'));

// 主题配置状态管理
const storesThemeConfig = useThemeConfig();
// 从store中解构响应式数据
const { themeConfig } = storeToRefs(storesThemeConfig);

/**
 * 计算是否显示标签视图
 * 经典布局模式下不显示标签视图
 * @returns {boolean} 是否显示标签视图
 */
const setShowTagsView = computed(() => {
	let { layout, isTagsview } = themeConfig.value;
	return layout !== 'classic' && isTagsview;
});
</script>

<style scoped lang="scss">
.layout-navbars-container {
	display: flex;
	flex-direction: column;
	width: 100%;
	height: 100%;
}
</style>
