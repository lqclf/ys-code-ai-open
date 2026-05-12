<template>
	<div class="layout-logo" v-if="setShowLogo" @click="onThemeConfigChange">
		<img :src="logoMini" class="layout-logo-medium-img" />
		<span>{{ themeConfig.globalTitle }}</span>
	</div>
	<div class="layout-logo-size" v-else @click="onThemeConfigChange">
		<img :src="logoMini" class="layout-logo-size-img" />
	</div>
</template>

<script setup lang="ts" name="layoutLogo">
/**
 * Logo布局组件
 * 负责渲染系统Logo和标题，支持展开/收起两种状态
 * 点击可切换侧边栏折叠状态（横向布局除外）
 * @author Eric
 * @since 2026-01-30
 */
import { computed } from 'vue';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '@/stores/themeConfig';
import logoMini from '@/assets/logo.png';

// 主题配置状态管理
const storesThemeConfig = useThemeConfig();
// 从store中解构响应式数据
const { themeConfig } = storeToRefs(storesThemeConfig);

/**
 * 计算是否显示完整Logo
 * 根据侧边栏折叠状态、布局模式和窗口宽度决定显示完整Logo还是仅图标
 * @returns {boolean} 是否显示完整Logo（包含标题）
 */
const setShowLogo = computed(() => {
	let { isCollapse, layout } = themeConfig.value;
	return !isCollapse || layout === 'classic' || document.body.clientWidth < 1000;
});

/**
 * 主题配置变更事件处理
 * 点击Logo时切换侧边栏折叠状态（横向布局模式下不生效）
 */
const onThemeConfigChange = () => {
	if (themeConfig.value.layout === 'transverse') return false;
	themeConfig.value.isCollapse = !themeConfig.value.isCollapse;
};
</script>

<style scoped lang="scss">
.layout-logo {
	width: 220px;
	height: 50px;
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);
	color: #ffffff;
	font-size: 16px;
	cursor: pointer;
	animation: logoAnimation 0.3s ease-in-out;
	background: linear-gradient(90deg, rgba(64, 158, 255, 0.15) 0%, transparent 100%);
	border-bottom: 1px solid rgba(64, 158, 255, 0.2);
	span {
		white-space: nowrap;
		display: inline-block;
		font-weight: 600;
		letter-spacing: 1px;
	}
	&:hover {
		span {
			color: #409eff;
		}
	}
	&-medium-img {
		width: 24px;
		margin-right: 8px;
		filter: drop-shadow(0 0 4px rgba(64, 158, 255, 0.6));
	}
}
.layout-logo-size {
	width: 100%;
	height: 50px;
	display: flex;
	cursor: pointer;
	animation: logoAnimation 0.3s ease-in-out;
	background: linear-gradient(90deg, rgba(64, 158, 255, 0.15) 0%, transparent 100%);
	border-bottom: 1px solid rgba(64, 158, 255, 0.2);
	&-img {
		width: 24px;
		margin: auto;
		filter: drop-shadow(0 0 4px rgba(64, 158, 255, 0.6));
	}
	&:hover {
		img {
			animation: logoAnimation 0.3s ease-in-out;
		}
	}
}
</style>
