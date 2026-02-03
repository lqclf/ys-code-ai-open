<template>
	<!-- 布局容器：根据主题配置动态切换不同的布局组件 -->
	<div>
		<component :is="layouts[themeConfig.layout]" />
	</div>
</template>

<script setup lang="ts" name="layout">
/**
 * 布局主入口组件
 * 作为整个应用的布局容器，根据主题配置动态加载不同的布局模式
 * 支持四种布局：defaults(默认)、classic(经典)、transverse(横向)、columns(分栏)
 * 同时处理窗口大小变化，实现移动端适配
 * @author Eric
 * @since 2026-01-30
 */
import { onBeforeMount, onUnmounted, defineAsyncComponent } from 'vue';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '@/stores/themeConfig';
import { Local } from '@/utils/storage';
import mittBus from '@/utils/mitt';

/**
 * 布局组件映射表
 * 使用异步组件加载不同的布局模式，实现按需加载
 * @property {Component} defaults - 默认布局：侧边栏+顶部导航
 * @property {Component} classic - 经典布局：顶部导航+侧边栏
 * @property {Component} transverse - 横向布局：顶部横向菜单
 * @property {Component} columns - 分栏布局：双栏侧边栏
 */
const layouts: any = {
	defaults: defineAsyncComponent(() => import('@/layout/main/defaults.vue')),
	classic: defineAsyncComponent(() => import('@/layout/main/classic.vue')),
	transverse: defineAsyncComponent(() => import('@/layout/main/transverse.vue')),
	columns: defineAsyncComponent(() => import('@/layout/main/columns.vue')),
};

// 定义变量内容
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);

/**
 * 窗口大小改变时的处理函数（适配移动端）
 * 监听窗口大小变化，在移动端（宽度小于1000px）自动切换为默认布局
 * 保存原始布局配置，在回到桌面端时恢复
 */
const onLayoutResize = () => {
	// 保存原始布局配置
	if (!Local.get('oldLayout')) Local.set('oldLayout', themeConfig.value.layout);
	const clientWidth = document.body.clientWidth;
	if (clientWidth < 1000) {
		// 移动端：强制使用默认布局，关闭菜单折叠
		themeConfig.value.isCollapse = false;
		mittBus.emit('layoutMobileResize', {
			layout: 'defaults',
			clientWidth,
		});
	} else {
		// 桌面端：恢复原始布局
		mittBus.emit('layoutMobileResize', {
			layout: Local.get('oldLayout') ? Local.get('oldLayout') : themeConfig.value.layout,
			clientWidth,
		});
	}
};

/**
 * 组件挂载前生命周期钩子
 * 初始化布局大小监听，绑定 resize 事件
 */
onBeforeMount(() => {
	onLayoutResize();
	window.addEventListener('resize', onLayoutResize);
});

/**
 * 组件卸载时生命周期钩子
 * 移除 resize 事件监听，防止内存泄漏
 */
onUnmounted(() => {
	window.removeEventListener('resize', onLayoutResize);
});
</script>
