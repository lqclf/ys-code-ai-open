<template>
	<div class="el-menu-horizontal-warp">
		<el-menu router :default-active="state.defaultActive" background-color="transparent" mode="horizontal">
			<template v-for="val in menuLists">
				<el-sub-menu :index="val.path" v-if="val.children && val.children.length > 0" :key="val.path">
					<template #title>
						<SvgIcon :name="val.meta.icon" />
						<span style="padding: 0 5px;">{{ $t(val.meta.title) }}</span>
					</template>
					<SubItem :chil="val.children" />
				</el-sub-menu>
				<template v-else>
					<el-menu-item :index="val.path" :key="val.path">
						<template #title v-if="!val.meta.isLink || (val.meta.isLink && val.meta.isIframe)">
							<SvgIcon :name="val.meta.icon" />
							{{ $t(val.meta.title) }}
						</template>
						<template #title v-else>
							<a class="w100" @click.prevent="onALinkClick(val)">
								<SvgIcon :name="val.meta.icon" />
								{{ $t(val.meta.title) }}
							</a>
						</template>
					</el-menu-item>
				</template>
			</template>
		</el-menu>
	</div>
</template>

<script setup lang="ts" name="navMenuHorizontal">
/**
 * 横向菜单组件
 * 用于横向布局模式下展示顶部导航菜单
 * 支持多级菜单嵌套和外部链接跳转
 * @author Eric
 * @since 2026-01-30
 */
import { defineAsyncComponent, reactive, computed, onBeforeMount } from 'vue';
import { useRoute, onBeforeRouteUpdate, RouteRecordRaw } from 'vue-router';
import { storeToRefs } from 'pinia';
import { useRoutesList } from '@/stores/routesList';
import { useThemeConfig } from '@/stores/themeConfig';
import other from '@/utils/other';
import mittBus from '@/utils/mitt';

// 异步引入子菜单组件
const SubItem = defineAsyncComponent(() => import('@/layout/navMenu/subItem.vue'));

// 定义父组件传过来的值
const props = defineProps({
	menuList: {
		type: Array<RouteRecordRaw>,
		default: () => [],
	},
});

// 路由列表状态管理
const stores = useRoutesList();
// 主题配置状态管理
const storesThemeConfig = useThemeConfig();
// 从store中解构响应式数据
const { routesList } = storeToRefs(stores);
const { themeConfig } = storeToRefs(storesThemeConfig);
// 当前路由对象
const route = useRoute();

// 组件内部状态
const state = reactive({
	defaultActive: '' as string | undefined, // 当前激活的菜单项
});

/**
 * 计算菜单列表
 * @returns {RouteItems} 菜单列表
 */
const menuLists = computed(() => {
	return <RouteItems>props.menuList;
});

/**
 * 路由过滤递归函数
 * 过滤掉设置了isHide的路由项，并递归处理子路由
 * @template T 路由项类型
 * @param {T[]} arr 路由数组
 * @returns {T[]} 过滤后的路由数组
 */
const filterRoutesFun = <T extends RouteItem>(arr: T[]): T[] => {
	return arr
		.filter((item: T) => !item.meta?.isHide)
		.map((item: T) => {
			item = Object.assign({}, item);
			if (item.children) item.children = filterRoutesFun(item.children);
			return item;
		});
};

/**
 * 传送当前子级数据到菜单中
 * 根据路径查找对应的一级菜单及其子菜单
 * @param {string} path 当前路径
 * @returns {MittMenu} 包含当前菜单项和子菜单的数据对象
 */
const setSendClassicChildren = (path: string) => {
	const currentPathSplit = path.split('/');
	let currentData: MittMenu = { children: [] };
	filterRoutesFun(routesList.value).map((v, k) => {
		if (v.path === `/${currentPathSplit[1]}`) {
			v['k'] = k;
			currentData['item'] = { ...v };
			currentData['children'] = [{ ...v }];
			if (v.children) currentData['children'] = v.children;
		}
	});
	return currentData;
};

/**
 * 设置当前路由高亮
 * 根据布局模式和路由信息设置当前激活的菜单项
 * @param {RouteToFrom} currentRoute 当前路由对象
 */
const setCurrentRouterHighlight = (currentRoute: RouteToFrom) => {
	const { path, meta } = currentRoute;
	if (themeConfig.value.layout === 'classic') {
		state.defaultActive = `/${path?.split('/')[1]}`;
	} else {
		const pathSplit = meta?.isDynamic ? meta.isDynamicPath!.split('/') : path!.split('/');
		if (pathSplit.length >= 4 && meta?.isHide) state.defaultActive = pathSplit.splice(0, 3).join('/');
		else state.defaultActive = path;
	}
};

/**
 * 外部链接点击事件
 * 处理外部链接的跳转
 * @param {RouteItem} val 路由项
 */
const onALinkClick = (val: RouteItem) => {
	other.handleOpenLink(val);
};

/**
 * 页面挂载前生命周期钩子
 * 初始化当前路由高亮
 */
onBeforeMount(() => {
	setCurrentRouterHighlight(route);
});

/**
 * 路由更新时生命周期钩子
 * 更新路由高亮并发送经典布局子菜单数据
 */
onBeforeRouteUpdate((to) => {
	setCurrentRouterHighlight(to);
	let { layout, isClassicSplitMenu } = themeConfig.value;
	if (layout === 'classic' && isClassicSplitMenu) {
		mittBus.emit('setSendClassicChildren', setSendClassicChildren(to.path));
	}
});
</script>

<style scoped lang="scss">
.el-menu-horizontal-warp {
	flex: 1;
	overflow: hidden;
	margin-right: 30px;
	:deep(.el-scrollbar__bar.is-vertical) {
		display: none;
	}
	:deep(a) {
		width: 100%;
	}
	.el-menu.el-menu--horizontal {
		display: flex;
		height: 100%;
		width: 100%;
		box-sizing: border-box;
	}
}
</style>
