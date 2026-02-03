<template>
	<div class="layout-navbars-close-full" v-if="isTagsViewCurrenFull">
		<div class="layout-navbars-close-full-icon">
			<SvgIcon name="ele-Close" :title="$t('message.tagsView.closeFullscreen')" @click="onCloseFullscreen" />
		</div>
	</div>
</template>

<script setup lang="ts" name="layoutCloseFull">
/**
 * 关闭全屏组件
 * 在标签页全屏模式下显示关闭按钮，用于退出全屏状态
 * 固定在页面右上角，提供便捷的全屏退出操作
 * @author Eric
 * @since 2026-01-30
 */
import { storeToRefs } from 'pinia';
import { useTagsViewRoutes } from '@/stores/tagsViewRoutes';

// 标签视图路由状态管理
const stores = useTagsViewRoutes();
// 从store中解构响应式数据
const { isTagsViewCurrenFull } = storeToRefs(stores);

/**
 * 关闭当前全屏
 * 调用store方法退出全屏状态
 */
const onCloseFullscreen = () => {
	stores.setCurrenFullscreen(false);
};
</script>

<style scoped lang="scss">
.layout-navbars-close-full {
	position: fixed;
	z-index: 9999999999;
	right: -30px;
	top: -30px;
	.layout-navbars-close-full-icon {
		width: 60px;
		height: 60px;
		border-radius: 100%;
		cursor: pointer;
		background: rgba(0, 0, 0, 0.1);
		transition: all 0.3s ease;
		position: relative;
		:deep(i) {
			position: absolute;
			left: 10px;
			top: 35px;
			color: #333333;
			transition: all 0.3s ease;
		}
	}
	&:hover {
		transition: all 0.3s ease;
		:deep(i) {
			color: var(--el-color-primary);
			transition: all 0.3s ease;
		}
	}
}
</style>
