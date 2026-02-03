<template>
	<!-- 父级路由视图容器：负责渲染子路由和 iframe 页面 -->
	<div class="layout-parent">
		<!-- 普通路由视图：使用 Vue Router 的 router-view -->
		<router-view v-slot="{ Component }">
			<transition :name="setTransitionName" mode="out-in">
				<!-- keep-alive 缓存组件，根据配置决定是否缓存 -->
				<keep-alive :include="getKeepAliveNames">
					<component :is="Component" :key="state.refreshRouterViewKey" class="w100" v-show="!isIframePage" />
				</keep-alive>
			</transition>
		</router-view>
		<!-- iframe 路由视图：用于嵌入外部页面 -->
		<transition :name="setTransitionName" mode="out-in">
			<Iframes class="w100" v-show="isIframePage" :refreshKey="state.iframeRefreshKey" :name="setTransitionName" :list="state.iframeList" />
		</transition>
	</div>
</template>

<script setup lang="ts" name="layoutParentView">
/**
 * 父级路由视图组件
 * 作为所有页面内容的容器，负责渲染普通路由组件和 iframe 嵌入页面
 * 支持页面切换动画、组件缓存、iframe 管理等功能
 * @author Eric
 * @since 2026-01-30
 */
import { defineAsyncComponent, computed, reactive, onBeforeMount, onUnmounted, nextTick, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { storeToRefs } from 'pinia';
import { useKeepALiveNames } from '@/stores/keepAliveNames';
import { useThemeConfig } from '@/stores/themeConfig';
import { Session } from '@/utils/storage';
import mittBus from '@/utils/mitt';

// 异步引入 iframe 视图组件
const Iframes = defineAsyncComponent(() => import('@/layout/routerView/iframes.vue'));

// 定义变量内容
const route = useRoute();
const router = useRouter();
const storesKeepAliveNames = useKeepALiveNames();
const storesThemeConfig = useThemeConfig();
const { keepAliveNames, cachedViews } = storeToRefs(storesKeepAliveNames);
const { themeConfig } = storeToRefs(storesThemeConfig);

/**
 * 响应式状态对象
 * @property {string} refreshRouterViewKey - 非 iframe 页面刷新标识
 * @property {string} iframeRefreshKey - iframe 页面刷新标识
 * @property {Array} keepAliveNameList - 组件缓存名称列表
 * @property {Array} iframeList - iframe 路由列表
 */
const state = reactive<ParentViewState>({
	refreshRouterViewKey: '', // 非 iframe tagsview 右键菜单刷新时
	iframeRefreshKey: '', // iframe tagsview 右键菜单刷新时
	keepAliveNameList: [],
	iframeList: [],
});

/**
 * 计算属性：获取页面切换动画名称
 * 从主题配置中获取动画效果配置
 * @returns {string} 动画名称
 */
const setTransitionName = computed(() => {
	return themeConfig.value.animation;
});

/**
 * 计算属性：获取组件缓存列表
 * 根据是否启用 tagsView 决定使用缓存视图列表还是默认列表
 * @returns {Array<string>} 需要缓存的组件名称列表
 */
const getKeepAliveNames = computed(() => {
	return themeConfig.value.isTagsview ? cachedViews.value : state.keepAliveNameList;
});

/**
 * 计算属性：判断当前是否为 iframe 页面
 * @returns {boolean} 是否为 iframe 页面
 */
const isIframePage = computed(() => {
	return route.meta.isIframe;
});

/**
 * 获取 iframe 路由列表
 * 遍历所有路由，收集标记为 iframe 的路由项
 * 初始化 iframe 的打开状态和加载状态
 */
const getIframeListRoutes = async () => {
	router.getRoutes().forEach((v) => {
		if (v.meta.isIframe) {
			v.meta.isIframeOpen = false;
			v.meta.loading = true;
			state.iframeList.push({ ...v });
		}
	});
};

/**
 * 组件挂载前生命周期钩子
 * 初始化组件缓存列表
 * 监听 tagsView 刷新事件，处理页面刷新逻辑
 */
onBeforeMount(() => {
	state.keepAliveNameList = keepAliveNames.value;
	// 监听刷新事件
	mittBus.on('onTagsViewRefreshRouterView', (fullPath: string) => {
		// 刷新时临时移除当前路由的缓存
		state.keepAliveNameList = keepAliveNames.value.filter((name: string) => route.name !== name);
		state.refreshRouterViewKey = '';
		state.iframeRefreshKey = '';
		nextTick(() => {
			// 恢复缓存并设置刷新标识
			state.refreshRouterViewKey = fullPath;
			state.iframeRefreshKey = fullPath;
			state.keepAliveNameList = keepAliveNames.value;
		});
	});
});

/**
 * 组件挂载时生命周期钩子
 * 初始化 iframe 路由列表
 * 恢复缓存的 tagsView 数据
 */
onMounted(() => {
	getIframeListRoutes();
	nextTick(() => {
		setTimeout(() => {
			// 如果启用了缓存，从 sessionStorage 恢复缓存的视图
			if (themeConfig.value.isCacheTagsView) {
				let tagsViewArr: RouteItem[] = Session.get('tagsViewList') || [];
				cachedViews.value = tagsViewArr.filter((item) => item.meta?.isKeepAlive).map((item) => item.name as string);
			}
		}, 0);
	});
});

/**
 * 组件卸载时生命周期钩子
 * 移除事件监听，防止内存泄漏
 */
onUnmounted(() => {
	mittBus.off('onTagsViewRefreshRouterView', () => {});
});

/**
 * 监听路由变化
 * 更新刷新标识，确保页面正确渲染
 */
watch(
	() => route.fullPath,
	() => {
		state.refreshRouterViewKey = decodeURI(route.fullPath);
	},
	{
		immediate: true,
	}
);
</script>
