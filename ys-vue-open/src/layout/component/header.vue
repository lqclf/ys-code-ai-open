<template>
	<el-header class="layout-header" v-show="!isTagsViewCurrenFull">
		<NavBarsIndex />
	</el-header>
</template>

<script setup lang="ts" name="layoutHeader">
/**
 * 头部布局组件
 * 作为导航栏的容器组件，负责渲染顶部导航区域
 * 根据全屏状态控制显示/隐藏
 * @author Eric
 * @since 2026-01-30
 */
import { defineAsyncComponent } from 'vue';
import { storeToRefs } from 'pinia';
import { useTagsViewRoutes } from '@/stores/tagsViewRoutes';

// 异步引入导航栏索引组件
const NavBarsIndex = defineAsyncComponent(() => import('@/layout/navBars/index.vue'));

// 标签视图路由状态管理
const storesTagsViewRoutes = useTagsViewRoutes();
// 从store中解构响应式数据：当前是否处于标签视图全屏模式
const { isTagsViewCurrenFull } = storeToRefs(storesTagsViewRoutes);
</script>
