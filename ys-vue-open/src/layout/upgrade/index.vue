<template>
	<div class="upgrade-dialog">
		<el-dialog
			v-model="state.isUpgrade"
			width="300px"
			destroy-on-close
			:show-close="false"
			:close-on-click-modal="false"
			:close-on-press-escape="false"
		>
			<div class="upgrade-title">
				<div class="upgrade-title-warp">
					<span class="upgrade-title-warp-txt">{{ $t('message.upgrade.title') }}</span>
					<span class="upgrade-title-warp-version">v{{ state.version }}</span>
				</div>
			</div>
			<div class="upgrade-content">
				{{ getThemeConfig.globalTitle }} {{ $t('message.upgrade.msg') }}
				<div class="mt5">
					<!-- <el-link type="primary" class="font12" href="" target="_black">
						CHANGELOG.md
					</el-link> -->
				</div>
				<div class="upgrade-content-desc mt5">{{ $t('message.upgrade.desc') }}</div>
			</div>
			<div class="upgrade-btn">
				<el-button round size="default" type="info" text @click="onCancel">{{ $t('message.upgrade.btnOne') }}</el-button>
				<el-button type="primary" round size="default" @click="onUpgrade" :loading="state.isLoading">{{ state.btnTxt }}</el-button>
			</div>
		</el-dialog>
	</div>
</template>

<script setup lang="ts" name="layoutUpgrade">
/**
 * 系统升级提示组件
 * 在页面加载后延迟显示升级提示对话框，提醒用户有新版本可用
 * 提供取消和立即更新两种操作选项
 * @author Eric
 * @since 2026-01-30
 */
import { reactive, computed, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '@/stores/themeConfig';
import { Local } from '@/utils/storage';

// 国际化函数
const { t } = useI18n();
// 主题配置状态管理
const storesThemeConfig = useThemeConfig();
// 从store中解构响应式数据
const { themeConfig } = storeToRefs(storesThemeConfig);

// 组件内部状态
const state = reactive({
	isUpgrade: false, // 是否显示升级对话框
	// @ts-ignore
	version: __NEXT_VERSION__, // 当前版本号（由构建时注入）
	isLoading: false, // 是否正在更新中
	btnTxt: '', // 更新按钮文本
});

/**
 * 获取布局配置信息
 * 用于显示系统标题
 * @returns {object} 主题配置对象
 */
const getThemeConfig = computed(() => {
	return themeConfig.value;
});

/**
 * 取消升级事件处理
 * 关闭升级提示对话框
 */
const onCancel = () => {
	state.isUpgrade = false;
};

/**
 * 确认升级事件处理
 * 显示加载状态，清除本地缓存并刷新页面
 */
const onUpgrade = () => {
	state.isLoading = true;
	state.btnTxt = t('message.upgrade.btnTwoLoading');
	setTimeout(() => {
		Local.clear();
		window.location.reload();
		Local.set('version', state.version);
	}, 2000);
};

/**
 * 延迟显示升级对话框
 * 延迟2秒后显示，避免页面刷新时界面显示太快
 */
const delayShow = () => {
	setTimeout(() => {
		state.isUpgrade = true;
	}, 2000);
};

/**
 * 页面加载时生命周期钩子
 * 初始化延迟显示和按钮文本
 */
onMounted(() => {
	delayShow();
	setTimeout(() => {
		state.btnTxt = t('message.upgrade.btnTwo');
	}, 200);
});
</script>

<style scoped lang="scss">
.upgrade-dialog {
	:deep(.el-dialog) {
		padding: 0 !important;
		.el-dialog__body {
			padding: 0 !important;
		}
		.el-dialog__header {
			display: none !important;
		}
		.upgrade-title {
			text-align: center;
			height: 130px;
			display: flex;
			align-items: center;
			justify-content: center;
			position: relative;
			&::after {
				content: '';
				position: absolute;
				background-color: var(--el-color-primary-light-1);
				width: 130%;
				height: 130px;
				border-bottom-left-radius: 100%;
				border-bottom-right-radius: 100%;
			}
			.upgrade-title-warp {
				z-index: 1;
				position: relative;
				.upgrade-title-warp-txt {
					color: var(--next-color-white);
					font-size: 22px;
					letter-spacing: 3px;
				}
				.upgrade-title-warp-version {
					color: var(--next-color-white);
					background-color: var(--el-color-primary-light-4);
					font-size: 12px;
					position: absolute;
					display: flex;
					top: -2px;
					right: -50px;
					padding: 2px 4px;
					border-radius: 2px;
				}
			}
		}
		.upgrade-content {
			padding: 20px;
			line-height: 22px;
			.upgrade-content-desc {
				color: var(--el-color-info-light-5);
				font-size: 12px;
			}
		}
		.upgrade-btn {
			border-top: 1px solid var(--el-border-color-lighter, #ebeef5);
			display: flex;
			justify-content: space-around;
			padding: 15px 20px;
			.el-button {
				width: 100%;
			}
		}
	}
}
</style>
