<template>
	<!-- iframe 视图容器：用于嵌入外部网页 -->
	<div class="layout-padding layout-padding-unset layout-iframe">
		<div class="layout-padding-auto layout-padding-view">
			<!-- 遍历 iframe 列表，只显示已打开的 iframe -->
			<div class="w100" v-for="v in setIframeList" :key="v.path" v-loading="v.meta.loading" element-loading-background="white">
				<transition-group :name="name">
					<!-- iframe 元素：用于加载外部链接 -->
					<iframe
						:src="v.meta.isLink"
						:key="v.path"
						frameborder="0"
						height="100%"
						width="100%"
						style="position: absolute"
						:data-url="v.path"
						v-show="getRoutePath === v.path"
						ref="iframeRef"
					/>
				</transition-group>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts" name="layoutIframeView">
/**
 * iframe 视图组件
 * 用于在系统内嵌入外部网页，支持多 iframe 切换、加载状态显示、刷新等功能
 * 常用于集成第三方系统或外部链接
 * @author Eric
 * @since 2026-01-30
 */
import { computed, watch, ref, nextTick } from 'vue';
import { useRoute } from 'vue-router';

/**
 * 组件 Props 定义
 * @property {string} refreshKey - 刷新标识，用于触发 iframe 重新加载
 * @property {string} name - 过渡动画名称
 * @property {Array} list - iframe 路由列表
 */
const props = defineProps({
	// 刷新 iframe
	refreshKey: {
		type: String,
		default: () => '',
	},
	// 过渡动画 name
	name: {
		type: String,
		default: () => 'slide-right',
	},
	// iframe 列表
	list: {
		type: Array,
		default: () => [],
	},
});

// 定义变量内容
const iframeRef = ref();
const route = useRoute();

/**
 * 计算属性：获取已打开的 iframe 列表
 * 过滤出 isIframeOpen 为 true 的 iframe 项
 * @returns {RouteItems} 已打开的 iframe 列表
 */
const setIframeList = computed(() => {
	return (<RouteItems>props.list).filter((v: RouteItem) => v.meta?.isIframeOpen);
});

/**
 * 计算属性：获取当前路由路径
 * 用于控制显示对应的 iframe
 * @returns {string} 当前路由路径
 */
const getRoutePath = computed(() => {
	return route.path;
});

/**
 * 关闭 iframe 加载状态
 * 当 iframe 加载完成后，关闭 loading 状态
 * @param {string} val - iframe 路径
 * @param {RouteItem} item - 路由项对象
 */
const closeIframeLoading = (val: string, item: RouteItem) => {
	nextTick(() => {
		if (!iframeRef.value) return false;
		iframeRef.value.forEach((v: HTMLElement) => {
			if (v.dataset.url === val) {
				v.onload = () => {
					if (item.meta?.isIframeOpen && item.meta.loading) item.meta.loading = false;
				};
			}
		});
	});
};

/**
 * 监听路由变化
 * 初始化 iframe 数据，防止多个 iframe 时切换不生效
 * 当路由变化时，标记对应的 iframe 为已打开状态
 */
watch(
	() => route.fullPath,
	(val) => {
		const item: any = props.list.find((v: any) => v.path === val);
		if (!item) return false;
		if (!item.meta.isIframeOpen) item.meta.isIframeOpen = true;
		closeIframeLoading(val, item);
	},
	{
		immediate: true,
	}
);

/**
 * 监听 iframe 刷新标识变化
 * 用于 tagsView 右键菜单刷新功能
 * 当 refreshKey 变化时，重新加载当前 iframe
 */
watch(
	() => props.refreshKey,
	() => {
		const item: any = props.list.find((v: any) => v.path === route.path);
		if (!item) return false;
		if (item.meta.isIframeOpen) item.meta.isIframeOpen = false;
		setTimeout(() => {
			item.meta.isIframeOpen = true;
			item.meta.loading = true;
			closeIframeLoading(route.fullPath, item);
		});
	},
	{
		deep: true,
	}
);
</script>
