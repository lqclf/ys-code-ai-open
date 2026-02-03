<template>
	<transition name="el-zoom-in-center">
		<div
			aria-hidden="true"
			class="el-dropdown__popper el-popper is-light is-pure custom-contextmenu"
			role="tooltip"
			data-popper-placement="bottom"
			:style="`top: ${dropdowns.y + 5}px;left: ${dropdowns.x}px;`"
			:key="Math.random()"
			v-show="state.isShow"
		>
			<ul class="el-dropdown-menu">
				<template v-for="(v, k) in state.dropdownList">
					<li
						class="el-dropdown-menu__item"
						aria-disabled="false"
						tabindex="-1"
						:key="k"
						v-if="!v.affix"
						@click="onCurrentContextmenuClick(v.contextMenuClickId)"
					>
						<SvgIcon :name="v.icon" />
						<span>{{ $t(v.txt) }}</span>
					</li>
				</template>
			</ul>
			<div class="el-popper__arrow" :style="{ left: `${state.arrowLeft}px` }"></div>
		</div>
	</transition>
</template>

<script setup lang="ts" name="layoutTagsViewContextmenu">
/**
 * 标签视图右键菜单组件
 * 提供标签页的右键上下文菜单功能，包括刷新、关闭、关闭其他、关闭全部、全屏等操作
 * 根据标签是否固定(affix)动态显示/隐藏关闭选项
 * @author Eric
 * @since 2026-01-30
 */
import { computed, reactive, onMounted, onUnmounted, watch } from 'vue';

// 定义父组件传过来的值
const props = defineProps({
	dropdown: {
		type: Object,
		default: () => {
			return {
				x: 0,
				y: 0,
			};
		},
	},
});

// 定义子组件向父组件传值/事件
const emit = defineEmits(['currentContextmenuClick']);

// 组件内部状态
const state = reactive({
	isShow: false, // 是否显示右键菜单
	dropdownList: [
		// 右键菜单列表
		{ contextMenuClickId: 0, txt: 'message.tagsView.refresh', affix: false, icon: 'ele-RefreshRight' },
		{ contextMenuClickId: 1, txt: 'message.tagsView.close', affix: false, icon: 'ele-Close' },
		{ contextMenuClickId: 2, txt: 'message.tagsView.closeOther', affix: false, icon: 'ele-CircleClose' },
		{ contextMenuClickId: 3, txt: 'message.tagsView.closeAll', affix: false, icon: 'ele-FolderDelete' },
		{
			contextMenuClickId: 4,
			txt: 'message.tagsView.fullscreen',
			affix: false,
			icon: 'iconfont icon-fullscreen',
		},
	],
	item: {}, // 当前选中的标签项
	arrowLeft: 10, // 箭头位置
});

/**
 * 计算右键菜单位置
 * 如果菜单超出视口右边界，则调整位置
 * @returns {Object} 菜单坐标对象 {x, y}
 */
const dropdowns = computed(() => {
	if (props.dropdown.x + 117 > document.documentElement.clientWidth) {
		return {
			x: document.documentElement.clientWidth - 117 - 5,
			y: props.dropdown.y,
		};
	} else {
		return props.dropdown;
	}
});

/**
 * 当前项菜单点击
 * 触发父组件事件，传递点击的菜单项ID
 * @param {number} contextMenuClickId 菜单项ID
 */
const onCurrentContextmenuClick = (contextMenuClickId: number) => {
	emit('currentContextmenuClick', Object.assign({}, { contextMenuClickId }, state.item));
};

/**
 * 打开右键菜单
 * 根据标签是否固定设置关闭选项的显示状态
 * @param {RouteItem} item 标签项
 */
const openContextmenu = (item: RouteItem) => {
	state.item = item;
	item.meta?.isAffix ? (state.dropdownList[1].affix = true) : (state.dropdownList[1].affix = false);
	closeContextmenu();
	setTimeout(() => {
		state.isShow = true;
	}, 10);
};

/**
 * 关闭右键菜单
 */
const closeContextmenu = () => {
	state.isShow = false;
};

/**
 * 页面加载时生命周期钩子
 * 添加点击事件监听，点击页面其他地方关闭菜单
 */
onMounted(() => {
	document.body.addEventListener('click', closeContextmenu);
});

/**
 * 页面卸载时生命周期钩子
 * 移除事件监听
 */
onUnmounted(() => {
	document.body.removeEventListener('click', closeContextmenu);
});

/**
 * 监听下拉菜单位置变化
 * 调整箭头位置以保持视觉对齐
 */
watch(
	() => props.dropdown,
	({ x }) => {
		if (x + 117 > document.documentElement.clientWidth) state.arrowLeft = 117 - (document.documentElement.clientWidth - x);
		else state.arrowLeft = 10;
	},
	{
		deep: true,
	}
);

// 暴露变量
defineExpose({
	openContextmenu,
});
</script>

<style scoped lang="scss">
.custom-contextmenu {
	transform-origin: center top;
	z-index: 2190;
	position: fixed;
	.el-dropdown-menu__item {
		font-size: 12px !important;
		white-space: nowrap;
		i {
			font-size: 12px !important;
		}
	}
}
</style>
