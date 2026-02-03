<template>
	<!-- 垂直菜单组件：基于 Element Plus 的 el-menu -->
	<el-menu
		router
		:default-active="state.defaultActive"
		background-color="transparent"
		:collapse="state.isCollapse"
		:unique-opened="getThemeConfig.isUniqueOpened"
		:collapse-transition="false"
	>
		<!-- 遍历菜单列表渲染菜单项 -->
		<template v-for="val in menuLists">
			<!-- 有子菜单的情况：使用 el-sub-menu 渲染可展开菜单 -->
			<el-sub-menu :index="val.path" v-if="val.children && val.children.length > 0" :key="val.path">
				<template #title>
					<!-- 菜单图标 -->
					<SvgIcon :name="val.meta.icon" />
					<!-- 菜单标题，支持国际化 -->
					<span style="padding: 0 5px;">{{ $t(val.meta.title) }}</span>
				</template>
				<!-- 递归渲染子菜单 -->
				<SubItem :chil="val.children" />
			</el-sub-menu>
			<!-- 无子菜单的情况：使用 el-menu-item 渲染叶子节点 -->
			<template v-else>
				<el-menu-item :index="val.path" :key="val.path">
					<SvgIcon :name="val.meta.icon" />
					<!-- 内部链接或 iframe 嵌入的链接 -->
					<template #title v-if="!val.meta.isLink || (val.meta.isLink && val.meta.isIframe)">
						<span style="padding: 0 5px;">{{ $t(val.meta.title) }}</span>
					</template>
					<!-- 外部链接：使用 a 标签在新窗口打开 -->
					<template #title v-else>
						<a class="w100" @click.prevent="onALinkClick(val)">{{ $t(val.meta.title) }}</a>
					</template>
				</el-menu-item>
			</template>
		</template>
	</el-menu>
</template>

<script setup lang="ts" name="navMenuVertical">
/**
 * 垂直菜单组件
 * 用于侧边栏布局模式下展示垂直导航菜单
 * 支持多级菜单嵌套、菜单折叠、外部链接跳转等功能
 * @author Eric
 * @since 2026-01-30
 */
import { defineAsyncComponent, reactive, computed, onMounted, watch } from 'vue';
import { useRoute, onBeforeRouteUpdate, RouteRecordRaw } from 'vue-router';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '@/stores/themeConfig';
import other from '@/utils/other';

// 异步引入子菜单递归组件
const SubItem = defineAsyncComponent(() => import('@/layout/navMenu/subItem.vue'));

/**
 * 组件 Props 定义
 * @property {Array<RouteRecordRaw>} menuList - 菜单列表数据
 */
const props = defineProps({
	menuList: {
		type: Array<RouteRecordRaw>,
		default: () => [],
	},
});

// 定义变量内容
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);
const route = useRoute();

/**
 * 响应式状态对象
 * @property {string} defaultActive - 当前激活的菜单项
 * @property {boolean} isCollapse - 菜单是否折叠
 */
const state = reactive({
	defaultActive: route.meta.isDynamic ? route.meta.isDynamicPath : route.path,
	isCollapse: false,
});

/**
 * 计算属性：获取菜单列表
 * 将 props.menuList 转换为响应式数据
 * @returns {RouteItems} 菜单项数组
 */
const menuLists = computed(() => {
	return <RouteItems>props.menuList;
});

/**
 * 计算属性：获取主题配置
 * @returns {ThemeConfigState} 主题配置对象
 */
const getThemeConfig = computed(() => {
	return themeConfig.value;
});

/**
 * 设置父级菜单高亮
 * 处理动态路由和隐藏路由的高亮逻辑
 * 当路由层级大于等于4且为隐藏路由时，截取前3级路径作为高亮项
 * @param {RouteToFrom} currentRoute - 当前路由对象
 * @returns {string} 计算后的高亮路径
 */
const setParentHighlight = (currentRoute: RouteToFrom) => {
	const { path, meta } = currentRoute;
	const pathSplit = meta?.isDynamic ? meta.isDynamicPath!.split('/') : path!.split('/');
	if (pathSplit.length >= 4 && meta?.isHide) return pathSplit.splice(0, 3).join('/');
	else return path;
};

/**
 * 打开外部链接
 * 调用工具函数处理外部链接的跳转逻辑
 * @param {RouteItem} val - 路由项对象，包含链接信息
 */
const onALinkClick = (val: RouteItem) => {
	other.handleOpenLink(val);
};

/**
 * 页面加载时生命周期钩子
 * 初始化当前激活的菜单项
 */
onMounted(() => {
	state.defaultActive = setParentHighlight(route);
});

/**
 * 路由更新前守卫
 * 更新当前激活的菜单项
 * 在小屏幕设备（宽度小于1000px）下自动展开菜单
 * @param {RouteToFrom} to - 目标路由对象
 */
onBeforeRouteUpdate((to) => {
	state.defaultActive = setParentHighlight(to);
	const clientWidth = document.body.clientWidth;
	if (clientWidth < 1000) themeConfig.value.isCollapse = false;
});

/**
 * 监听主题配置中的菜单折叠状态
 * 根据屏幕宽度决定是否应用折叠状态
 * 在小屏幕设备（宽度小于等于1000px）下强制不折叠
 */
watch(
	() => themeConfig.value.isCollapse,
	(isCollapse) => {
		document.body.clientWidth <= 1000 ? (state.isCollapse = false) : (state.isCollapse = isCollapse);
	},
	{
		immediate: true,
	}
);
</script>
