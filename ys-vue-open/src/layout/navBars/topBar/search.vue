<template>
	<div class="layout-search-dialog">
		<el-dialog v-model="state.isShowSearch" destroy-on-close :show-close="false">
			<template #footer>
				<el-autocomplete
					v-model="state.menuQuery"
					:fetch-suggestions="menuSearch"
					:placeholder="$t('message.user.searchPlaceholder')"
					ref="layoutMenuAutocompleteRef"
					@select="onHandleSelect"
					:fit-input-width="true"
				>
					<template #prefix>
						<el-icon class="el-input__icon">
							<ele-Search />
						</el-icon>
					</template>
					<template #default="{ item }">
						<div>
							<SvgIcon :name="item.meta.icon" class="mr5" />
							{{ $t(item.meta.title) }}
						</div>
					</template>
				</el-autocomplete>
			</template>
		</el-dialog>
	</div>
</template>

<script setup lang="ts" name="layoutBreadcrumbSearch">
/**
 * 菜单搜索组件
 * 提供全局菜单搜索功能，支持模糊匹配菜单路径和标题
 * 通过对话框形式展示搜索输入框和匹配结果
 * @author Eric
 * @since 2026-01-30
 */
import { reactive, ref, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { storeToRefs } from 'pinia';
import { useTagsViewRoutes } from '@/stores/tagsViewRoutes';

// 标签视图路由状态管理
const storesTagsViewRoutes = useTagsViewRoutes();
// 从store中解构响应式数据
const { tagsViewRoutes } = storeToRefs(storesTagsViewRoutes);
// 自动完成组件引用
const layoutMenuAutocompleteRef = ref();
// i18n国际化实例
const { t } = useI18n();
// 路由实例
const router = useRouter();

// 组件内部状态
const state = reactive<SearchState>({
	isShowSearch: false, // 是否显示搜索对话框
	menuQuery: '', // 搜索关键词
	tagsViewList: [], // 标签视图列表
});

/**
 * 打开搜索弹窗
 * 重置搜索关键词，初始化菜单数据，并聚焦输入框
 */
const openSearch = () => {
	state.menuQuery = '';
	state.isShowSearch = true;
	initTageView();
	nextTick(() => {
		setTimeout(() => {
			layoutMenuAutocompleteRef.value.focus();
		});
	});
};

/**
 * 关闭搜索弹窗
 */
const closeSearch = () => {
	state.isShowSearch = false;
};

/**
 * 菜单搜索数据过滤
 * 根据输入的关键词过滤菜单列表
 * @param {string} queryString 搜索关键词
 * @param {Function} cb 回调函数，返回匹配结果
 */
const menuSearch = (queryString: string, cb: Function) => {
	let results = queryString ? state.tagsViewList.filter(createFilter(queryString)) : state.tagsViewList;
	cb(results);
};

/**
 * 菜单搜索过滤函数
 * 根据路径、标题进行模糊匹配
 * @param {string} queryString 搜索关键词
 * @returns {Function} 过滤函数
 */
const createFilter = (queryString: string) => {
	return (restaurant: RouteItem) => {
		return (
			restaurant.path.toLowerCase().indexOf(queryString.toLowerCase()) > -1 ||
			restaurant.meta!.title!.toLowerCase().indexOf(queryString.toLowerCase()) > -1 ||
			t(restaurant.meta!.title!).indexOf(queryString.toLowerCase()) > -1
		);
	};
};

/**
 * 初始化菜单数据
 * 从标签视图路由中加载所有非隐藏的菜单项
 */
const initTageView = () => {
	if (state.tagsViewList.length > 0) return false;
	tagsViewRoutes.value.map((v: RouteItem) => {
		if (!v.meta?.isHide) state.tagsViewList.push({ ...v });
	});
};

/**
 * 当前菜单选中时
 * 根据选中项的类型进行跳转处理
 * @param {RouteItem} item 选中的菜单项
 */
const onHandleSelect = (item: RouteItem) => {
	let { path, redirect } = item;
	if (item.meta?.isLink && !item.meta?.isIframe) window.open(item.meta?.isLink);
	else if (redirect) router.push(redirect);
	else router.push(path);
	closeSearch();
};

// 暴露变量
defineExpose({
	openSearch,
});
</script>
