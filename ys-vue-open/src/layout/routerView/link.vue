<template>
	<!-- 外部链接提示页面：当页面在新窗口打开时显示 -->
	<div class="layout-padding layout-link-container">
		<div class="layout-padding-auto layout-padding-view">
			<div class="layout-link-warp">
				<!-- 星球图标 -->
				<i class="layout-link-icon iconfont icon-xingqiu"></i>
				<!-- 提示信息 -->
				<div class="layout-link-msg">页面 "{{ $t(state.title) }}" 已在新窗口中打开</div>
				<!-- 前往按钮 -->
				<el-button class="mt30" round size="default" @click="onGotoFullPage">
					<i class="iconfont icon-lianjie"></i>
					<span>立即前往体验</span>
				</el-button>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts" name="layoutLinkView">
/**
 * 外部链接视图组件
 * 当配置为外部链接的路由被访问时，显示提示页面
 * 告知用户页面已在新窗口打开，并提供跳转按钮
 * @author Eric
 * @since 2026-01-30
 */
import { reactive, watch } from 'vue';
import { useRoute } from 'vue-router';
import { verifyUrl } from '@/utils/toolsValidate';

// 定义变量内容
const route = useRoute();

/**
 * 响应式状态对象
 * @property {string} title - 页面标题
 * @property {string} isLink - 外部链接地址
 */
const state = reactive<LinkViewState>({
	title: '',
	isLink: '',
});

/**
 * 立即前往外部链接
 * 验证链接格式后在新窗口打开
 * 支持完整 URL 和相对路径两种格式
 */
const onGotoFullPage = () => {
	const { origin, pathname } = window.location;
	// 验证是否为完整 URL，是则直接打开，否则拼接当前域名
	if (verifyUrl(<string>state.isLink)) window.open(state.isLink);
	else window.open(`${origin}${pathname}#${state.isLink}`);
};

/**
 * 监听路由变化
 * 从路由元数据中获取页面标题和链接地址
 */
watch(
	() => route.path,
	() => {
		state.title = <string>route.meta.title;
		state.isLink = <string>route.meta.isLink;
	},
	{
		immediate: true,
	}
);
</script>

<style scoped lang="scss">
.layout-link-container {
	.layout-link-warp {
		margin: auto;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		i.layout-link-icon {
			position: relative;
			font-size: 100px;
			color: var(--el-color-primary);
			&::after {
				content: '';
				position: absolute;
				left: 50px;
				top: 0;
				width: 15px;
				height: 100px;
				background: linear-gradient(
					rgba(255, 255, 255, 0.01),
					rgba(255, 255, 255, 0.01),
					rgba(255, 255, 255, 0.01),
					rgba(255, 255, 255, 0.05),
					rgba(255, 255, 255, 0.05),
					rgba(255, 255, 255, 0.05),
					rgba(235, 255, 255, 0.5),
					rgba(255, 255, 255, 0.05),
					rgba(255, 255, 255, 0.05),
					rgba(255, 255, 255, 0.05),
					rgba(255, 255, 255, 0.01),
					rgba(255, 255, 255, 0.01),
					rgba(255, 255, 255, 0.01)
				);
				transform: rotate(-15deg);
				animation: toRight 5s linear infinite;
			}
		}
		.layout-link-msg {
			font-size: 12px;
			color: var(--next-bg-topBarColor);
			opacity: 0.7;
			margin-top: 15px;
		}
	}
}
</style>
