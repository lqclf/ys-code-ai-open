<template>
	<!-- 递归渲染子菜单项 -->
	<template v-for="val in chils">
		<!-- 有子菜单的情况：使用 el-sub-menu 组件渲染可展开的菜单项 -->
		<el-sub-menu :index="val.path" :key="val.path" v-if="val.children && val.children.length > 0">
			<template #title>
				<!-- 菜单图标 -->
				<SvgIcon :name="val.meta.icon" />
				<!-- 菜单标题，支持国际化 -->
				<span style="padding: 0 5px;">{{ $t(val.meta.title) }}</span>
			</template>
			<!-- 递归调用自身渲染子菜单 -->
			<sub-item :chil="val.children" />
		</el-sub-menu>
		<!-- 无子菜单的情况：使用 el-menu-item 渲染叶子菜单项 -->
		<template v-else>
			<el-menu-item :index="val.path" :key="val.path">
				<!-- 内部链接或 iframe 嵌入的链接 -->
				<template v-if="!val.meta.isLink || (val.meta.isLink && val.meta.isIframe)">
					<SvgIcon :name="val.meta.icon" />
					<span style="padding: 0 5px;">{{ $t(val.meta.title) }}</span>
				</template>
				<!-- 外部链接：使用 a 标签在新窗口打开 -->
				<template v-else>
					<a class="w100" @click.prevent="onALinkClick(val)">
						<SvgIcon :name="val.meta.icon" />
						{{ $t(val.meta.title) }}
					</a>
				</template>
			</el-menu-item>
		</template>
	</template>
</template>

<script setup lang="ts" name="navMenuSubItem">
/**
 * 子菜单递归组件
 * 用于递归渲染多级嵌套菜单结构，支持无限层级
 * 该组件被 horizontal.vue 和 vertical.vue 引用
 * @author Eric
 * @since 2026-01-30
 */
import { computed } from 'vue';
import { RouteRecordRaw } from 'vue-router';
import other from '@/utils/other';

/**
 * 组件 Props 定义
 * @property {Array<RouteRecordRaw>} chil - 子菜单列表数据
 */
const props = defineProps({
	// 菜单列表
	chil: {
		type: Array<RouteRecordRaw>,
		default: () => [],
	},
});

/**
 * 计算属性：获取父级传递的菜单数据
 * 将 props.chil 转换为响应式数据供模板使用
 * @returns {RouteItems} 菜单项数组
 */
const chils = computed(() => {
	return <RouteItems>props.chil;
});

/**
 * 打开外部链接
 * 调用工具函数处理外部链接的跳转逻辑
 * @param {RouteItem} val - 路由项对象，包含链接信息
 */
const onALinkClick = (val: RouteItem) => {
	other.handleOpenLink(val);
};
</script>
