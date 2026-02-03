<template>
	<div class="sponsors-container" title="点击前往体验" v-show="state.sponsors.isShow" @click="onSponsorsClick">
		<el-carousel height="240px" indicator-position="none" :arrow="setCarouselShow" @change="onCarouselChange">
			<el-carousel-item v-for="(v, k) in state.sponsors.list" :key="k">
				<img :src="v.url" class="sponsors-img" />
				<div class="sponsors-text" v-html="v.text"></div>
			</el-carousel-item>
		</el-carousel>
		<div class="sponsors-close">
			<SvgIcon name="ele-Close" :size="12" title="关闭赞助商" @click.stop="onCloseSponsors" />
		</div>
	</div>
</template>

<script setup lang="ts" name="layoutSponsors">
/**
 * 赞助商展示组件
 * 在页面右下角展示赞助商广告轮播，支持点击跳转和关闭功能
 * 延迟显示以避免影响页面初始加载性能
 * @author Eric
 * @since 2026-01-30
 */
import { reactive, computed, onMounted } from 'vue';
import sponsorsOne from '@/assets/ccflowRightNextAdmin.png';

// 组件内部状态
const state = reactive({
	sponsors: {
		// 赞助商列表
		list: [
			{
				url: sponsorsOne,
				text: `驰骋BPM系统包含表单引擎+流程引擎+权限控制,方便集成,配置灵活,功能强大,适合中国国情的工作流引擎.演示:http://demo.ccflow.org。`,
				link: 'http://www.ccflow.org/',
			},
		],
		isShow: false, // 是否显示赞助商
		index: 0, // 当前显示的赞助商索引
	},
});

/**
 * 计算轮播图箭头显示方式
 * 当只有一条赞助商时不显示箭头
 * @returns {string} 箭头显示方式：'never' 或 'hover'
 */
const setCarouselShow = computed(() => {
	return state.sponsors.list.length <= 1 ? 'never' : 'hover';
});

/**
 * 关闭赞助商展示
 * 点击关闭按钮时隐藏赞助商组件
 */
const onCloseSponsors = () => {
	state.sponsors.isShow = false;
};

/**
 * 轮播图切换事件处理
 * 记录当前显示的赞助商索引
 * @param {number} e 当前项索引
 */
const onCarouselChange = (e: number) => {
	state.sponsors.index = e;
};

/**
 * 赞助商点击事件处理
 * 打开当前赞助商链接
 */
const onSponsorsClick = () => {
	window.open(state.sponsors.list[state.sponsors.index].link);
};

/**
 * 延迟显示赞助商
 * 延迟3秒后显示，避免影响页面初始加载
 */
const delayShow = () => {
	setTimeout(() => {
		state.sponsors.isShow = true;
	}, 3000);
};

/**
 * 页面加载时生命周期钩子
 * 初始化延迟显示
 */
onMounted(() => {
	delayShow();
});
</script>

<style scoped lang="scss">
.sponsors-container {
	position: fixed;
	right: 15px;
	bottom: 15px;
	z-index: 3;
	width: 200px;
	background-color: var(--next-bg-main-color);
	box-shadow: var(--el-box-shadow-lighter);
	border-radius: 5px;
	overflow: hidden;
	cursor: pointer;
	.sponsors-img {
		width: 100%;
		height: 80px;
	}
	.sponsors-text {
		padding: 10px;
		color: var(--el-text-color-regular);
		font-size: var(--el-dialog-content-font-size);
	}
	.sponsors-close {
		width: 60px;
		height: 60px;
		border-radius: 100%;
		background: rgba(0, 0, 0, 0.05);
		transition: all 0.3s ease;
		position: absolute;
		right: -35px;
		bottom: -35px;
		:deep(i) {
			position: absolute;
			left: 9px;
			top: 9px;
			color: #afafaf;
			transition: all 0.3s ease;
		}
		&:hover {
			transition: all 0.3s ease;
			:deep(i) {
				color: var(--el-color-primary);
				transition: all 0.3s ease;
			}
		}
	}
}
</style>
