<template>
	<div class="layout-navbars-breadcrumb-user pr15" :style="{ flex: layoutUserFlexNum }">
		<el-dropdown :show-timeout="70" :hide-timeout="50" trigger="click" @command="onComponentSizeChange">
			<div class="layout-navbars-breadcrumb-user-icon">
				<i class="cn cn-text" :title="$t('message.user.title0')"></i>
			</div>
			<template #dropdown>
				<el-dropdown-menu>
					<el-dropdown-item command="large" :disabled="state.disabledSize === 'large'">{{ $t('message.user.dropdownLarge') }}</el-dropdown-item>
					<el-dropdown-item command="default" :disabled="state.disabledSize === 'default'">{{ $t('message.user.dropdownDefault') }}</el-dropdown-item>
					<el-dropdown-item command="small" :disabled="state.disabledSize === 'small'">{{ $t('message.user.dropdownSmall') }}</el-dropdown-item>
				</el-dropdown-menu>
			</template>
		</el-dropdown>
		<el-dropdown :show-timeout="70" :hide-timeout="50" trigger="click" @command="onLanguageChange">
			<div class="layout-navbars-breadcrumb-user-icon">
				<i
					class="iconfont"
					:class="state.disabledI18n === 'en' ? 'cn cn-yuyan' : 'cn cn-yuyan'"
					:title="$t('message.user.title1')"
				></i>
			</div>
			<template #dropdown>
				<el-dropdown-menu>
					<el-dropdown-item command="zh-cn" :disabled="state.disabledI18n === 'zh-cn'">简体中文</el-dropdown-item>
					<el-dropdown-item command="en" :disabled="state.disabledI18n === 'en'">English</el-dropdown-item>
					<el-dropdown-item command="zh-tw" :disabled="state.disabledI18n === 'zh-tw'">繁體中文</el-dropdown-item>
				</el-dropdown-menu>
			</template>
		</el-dropdown>
		<div class="layout-navbars-breadcrumb-user-icon" @click="onSearchClick">
			<el-icon :title="$t('message.user.title2')">
				<ele-Search />
			</el-icon>
		</div>
		<div class="layout-navbars-breadcrumb-user-icon" @click="onLayoutSetingClick">
			<i class="cn cn-skin" :title="$t('message.user.title3')"></i>
		</div>
		<div class="layout-navbars-breadcrumb-user-icon">
			<YsNoticeBar
				ref="noticeBarRef"
				:data-config="noticeDataConfig"
				:display-config="noticeDisplayConfig"
				:interaction-config="noticeInteractionConfig"
				:style-config="noticeStyleConfig"
				@message-click="handleMessageClick"
				@to-center="handleToCenter"
				@popover-show="handlePopoverShow"
				@popover-hide="handlePopoverHide"
				@mark-read="handleMarkRead"
				@mark-all-read="handleMarkAllRead"
				@search="handleSearch"
			/>
		</div>
		<div class="layout-navbars-breadcrumb-user-icon mr10" @click="onScreenfullClick">
			<i
				class="iconfont"
				:title="state.isScreenfull ? $t('message.user.title6') : $t('message.user.title5')"
				:class="!state.isScreenfull ? 'cn cn-screen-full_line' : 'cn cn-screen-half_line'"
			></i>
		</div>
		<el-dropdown :show-timeout="70" :hide-timeout="50" @command="onHandleCommandClick">
			<span class="layout-navbars-breadcrumb-user-link">
				<img :src="userInfos.photo" class="layout-navbars-breadcrumb-user-link-photo mr5" />
				{{ userInfos.userName === '' ? 'common' : userInfos.userName }}
				<el-icon class="el-icon--right">
					<ele-ArrowDown />
				</el-icon>
			</span>
			<template #dropdown>
				<el-dropdown-menu>
					<el-dropdown-item command="/home">{{ $t('message.user.dropdown1') }}</el-dropdown-item>
					<el-dropdown-item command="wareHouse">{{ $t('message.user.dropdown6') }}</el-dropdown-item>
					<el-dropdown-item command="/personal">{{ $t('message.user.dropdown2') }}</el-dropdown-item>
					<el-dropdown-item command="/404">{{ $t('message.user.dropdown3') }}</el-dropdown-item>
					<el-dropdown-item command="/401">{{ $t('message.user.dropdown4') }}</el-dropdown-item>
					<el-dropdown-item divided command="logOut">{{ $t('message.user.dropdown5') }}</el-dropdown-item>
				</el-dropdown-menu>
			</template>
		</el-dropdown>
		<Search ref="searchRef" />
		
		<!-- 通知详情对话框 -->
		<NoticeDetailDialog ref="noticeDetailDialogRef" />
	</div>
</template>

<script setup lang="ts" name="layoutBreadcrumbUser">
/**
 * 顶部导航栏用户组件
 * 包含组件尺寸切换、语言切换、搜索、主题设置、消息通知、全屏、用户信息等功能
 * 作为顶部导航栏右侧的功能区域
 * @author Eric
 * @since 2026-01-30
 */
import { defineAsyncComponent, ref, unref, computed, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessageBox, ElMessage, ClickOutside as vClickOutside } from 'element-plus';
import screenfull from 'screenfull';
import { useI18n } from 'vue-i18n';
import { storeToRefs } from 'pinia';
import { useUserInfo } from '@/stores/userInfo';
import { useThemeConfig } from '@/stores/themeConfig';
import other from '@/utils/other';
import mittBus from '@/utils/mitt';
import { Session, Local } from '@/utils/storage';
import { useLoginApi } from '@/api/login';
import { createNoticeService } from '@/api/system/notice/service';
import type { DataConfig, DisplayConfig, InteractionConfig, StyleConfig, MessageItem } from '@/types/notice';

// 异步引入搜索组件
const Search = defineAsyncComponent(() => import('@/layout/navBars/topBar/search.vue'));
// 异步引入通知栏组件
const YsNoticeBar = defineAsyncComponent(() => import('@/components/YsNoticeBar/index.vue'));
// 异步引入通知详情对话框组件
const NoticeDetailDialog = defineAsyncComponent(() => import('@/views/system/notice/detail.vue'));

// i18n国际化实例
const { locale, t } = useI18n();
// 路由实例
const router = useRouter();
// 用户信息状态管理
const stores = useUserInfo();
// 主题配置状态管理
const storesThemeConfig = useThemeConfig();
// 从store中解构响应式数据
const { userInfos } = storeToRefs(stores);
const { themeConfig } = storeToRefs(storesThemeConfig);
// 搜索组件引用
const searchRef = ref();
// 通知栏组件引用
const noticeBarRef = ref();
// 通知详情对话框引用
const noticeDetailDialogRef = ref();
// 通知服务实例
const noticeService = createNoticeService();

// 组件内部状态
const state = reactive({
	isScreenfull: false, // 是否全屏
	disabledI18n: 'zh-cn', // 当前语言
	disabledSize: 'large', // 当前组件尺寸
});

// YsNoticeBar 数据配置
const noticeDataConfig = ref<DataConfig>({
	sourceConfig: [
		{
			type: 'system',
			label: '系统通知',
			icon: 'ri-notification-3-line',
			color: '#409eff'
		},
		{
			type: 'notice',
			label: '公告通知',
			icon: 'ri-megaphone-line',
			color: '#67c23a'
		},
		{
			type: 'todo',
			label: '待办提醒',
			icon: 'ri-checkbox-circle-line',
			color: '#e6a23c'
		},
	],
	messageList: {
		system: [],
		notice: [],
		todo: [],
	},
	tabDataSource: {
		all: {
			system: [],
			notice: [],
			todo: [],
		},
		unread: {
			system: [],
			notice: [],
			todo: [],
		},
		read: {
			system: [],
			notice: [],
			todo: [],
		}
	},
	enableLazyLoad: false,
	filterDuplicate: true,
});

// YsNoticeBar 显示配置
const noticeDisplayConfig = ref<DisplayConfig>({
	icon: {
		size: 14, 
		showBadge: true,
		badgePosition: 'top-right', 
		badgeColor: '#f56c6c',
		badgeSize: 8,
	},
	popover: {
		width: 380,
		maxHeight: 500,
		offset: 10,
		borderRadius: 8,
		shadow: true,
	},
	header: {
		fontSize: 14,
		textColor: '#606266',
		badgeBgColor: '#409eff',
		badgeTextColor: '#ffffff',
		badgeBorderRadius: 10,
		badgeSize: 16,
		wrap: false,
		emptyText: '暂无消息',
	},
	list: {
		itemPadding: '12px 16px',
		itemGap: 0,
		unreadLineColor: '#409eff',
		unreadBgColor: '#ecf5ff',
		unreadFontWeight: 'bold',
		readBgColor: 'transparent',
		readFontWeight: 'normal',
		hoverEffect: true,
		hoverBgColor: '#f5f7fa',
		emptyText: '暂无消息',
		showEmptyImage: true,
	},
	footer: {
		width: '100%',
		height: 35,
		text: '前往通知中心',
		fontSize: 14,
		textColor: '#ffffff',
		bgColor: '#409eff',
		hoverBgColor: '#66b1ff',
		activeBgColor: '#3a8ee6',
		borderRadius: 4,
		showIcon: true,
		iconPosition: 'left',
		icon: 'ri-arrow-right-line',
	},
	common: {
		timeFormat: 'YYYY-MM-DD HH:mm:ss',
		textOverflow: 'ellipsis',
		maxLines: 2,
		typeIcons: {
			system: 'ri-notification-3-line', 
			notice: 'ri-megaphone-line',
			todo: 'ri-checkbox-circle-line',
			alert: 'ri-alarm-warning-line',
		},
		openAnimation: 'fade',
		closeAnimation: 'fade',
		animationDuration: 300,
	},
});

// YsNoticeBar 交互配置
const noticeInteractionConfig = ref<InteractionConfig>({
	trigger: {
		mode: 'click',
		hoverDelay: 200,
		leaveDelay: 300,
		clickOutsideToClose: true,
		clickToToggle: true,
	},
	item: {
		autoMarkRead: true,
		enableDoubleClick: false,
		showPreview: false,
	},
	auto: {
		enableAutoScroll: false,
		scrollDirection: 'up',
		scrollSpeed: 2000,
		scrollPause: 'hover',
		scrollLoop: 0,
	},
	center: {
		reloadOnTabChange: false,
		enableSort: true,
		sortField: 'sendTime',
		sortOrder: 'desc',
		enablePagination: true,
		pageSize: 10,
		pageSizes: [10, 20, 50, 100],
		onCloseCallback: false,
	},
	cache: {
		cacheReadStatus: true,
		cacheLastViewId: false,
		cachePopoverState: false,
	},
});

// YsNoticeBar 样式配置
const noticeStyleConfig = ref<StyleConfig>({
	theme: 'light',
	customTheme: {
		primaryColor: '#409eff',
		successColor: '#67c23a',
		warningColor: '#e6a23c',
		dangerColor: '#f56c6c',
		infoColor: '#909399',
		textColor: '#606266',
		borderColor: '#dcdfe6',
		bgColor: '#ffffff',
	},
	typeStyles: {
		system: {
			textColor: '#409eff',
			lineColor: '#409eff',
			icon: 'ri-notification-3-line',
			bgColor: '#ecf5ff',
		},
		notice: {
			textColor: '#67c23a',
			lineColor: '#67c23a',
			icon: 'ri-megaphone-line',
			bgColor: '#f0f9ff',
		},
		todo: {
			textColor: '#e6a23c',
			lineColor: '#e6a23c',
			icon: 'ri-checkbox-circle-line',
			bgColor: '#fdf6ec',
		},
		alert: {
			textColor: '#f56c6c',
			lineColor: '#f56c6c',
			icon: 'ri-alarm-warning-line',
			bgColor: '#fef0f0',
		},
	},
	statusStyles: {
		unread: {
			fontWeight: 'bold',
			bgColor: '#ecf5ff',
			border: 'none',
			showDot: true,
		},
		read: {
			fontWeight: 'normal',
			bgColor: 'transparent',
			border: 'none',
		},
	},
});

/**
 * 设置分割样式
 * 根据布局模式计算用户区域的flex值
 * @returns {string|number} flex值
 */
const layoutUserFlexNum = computed(() => {
	let num: string | number = '';
	const { layout, isClassicSplitMenu } = themeConfig.value;
	const layoutArr: string[] = ['defaults', 'columns'];
	if (layoutArr.includes(layout) || (layout === 'classic' && !isClassicSplitMenu)) num = '1';
	else num = '';
	return num;
});

/**
 * 全屏点击事件
 * 切换页面全屏状态
 */
const onScreenfullClick = () => {
	if (!screenfull.isEnabled) {
		ElMessage.warning('暂不不支持全屏');
		return false;
	}
	screenfull.toggle();
	screenfull.on('change', () => {
		if (screenfull.isFullscreen) state.isScreenfull = true;
		else state.isScreenfull = false;
	});
};

/**
 * 布局配置图标点击事件
 * 打开布局设置抽屉
 */
const onLayoutSetingClick = () => {
	mittBus.emit('openSetingsDrawer');
};

/**
 * 下拉菜单点击事件
 * 处理用户下拉菜单的点击操作，包括退出登录
 * @param {string} path 点击的菜单命令
 */
const onHandleCommandClick = (path: string) => {
	if (path === 'logOut') {
		ElMessageBox({
			closeOnClickModal: false,
			closeOnPressEscape: false,
			title: t('message.user.logOutTitle'),
			message: t('message.user.logOutMessage'),
			showCancelButton: true,
			confirmButtonText: t('message.user.logOutConfirm'),
			cancelButtonText: t('message.user.logOutCancel'),
			buttonSize: 'default',
			beforeClose: (action, instance, done) => {
				if (action === 'confirm') {
					instance.confirmButtonLoading = true;
					instance.confirmButtonText = t('message.user.logOutExit');
					useLoginApi().signOut().then((res) => {
						if(res.code === 200) {
							done();
							setTimeout(() => {
								instance.confirmButtonLoading = false;
							}, 300);
							// 清除缓存/token等
							Session.clear();
							// 使用 reload 时，不需要调用 resetRoute() 重置路由
							window.location.reload();
						}
					});
				} else {
					done();
				}
			},
		}).catch(() => {});
	} else {
		router.push(path);
	}
};

/**
 * 菜单搜索点击事件
 * 打开搜索对话框
 */
const onSearchClick = () => {
	searchRef.value.openSearch();
};

/**
 * 组件大小改变事件
 * 切换全局组件尺寸并刷新页面
 * @param {string} size 组件尺寸
 */
const onComponentSizeChange = (size: string) => {
	Local.remove('themeConfig');
	themeConfig.value.globalComponentSize = size;
	Local.set('themeConfig', themeConfig.value);
	initI18nOrSize('globalComponentSize', 'disabledSize');
	window.location.reload();
};

/**
 * 语言切换事件
 * 切换系统语言并更新页面标题
 * @param {string} lang 语言代码
 */
const onLanguageChange = (lang: string) => {
	Local.remove('themeConfig');
	themeConfig.value.globalI18n = lang;
	Local.set('themeConfig', themeConfig.value);
	locale.value = lang;
	other.useTitle();
	initI18nOrSize('globalI18n', 'disabledI18n');
};

/**
 * 初始化组件大小/i18n
 * 从本地存储读取配置并设置状态
 * @param {string} value 配置项名称
 * @param {string} attr 状态属性名
 */
const initI18nOrSize = (value: string, attr: string) => {
	(<any>state)[attr] = Local.get('themeConfig')[value];
};

/**
 * 消息点击事件处理
 * 根据消息类型跳转到不同的详情页或打开详情对话框
 * @param {MessageItem} message 消息对象
 */
const handleMessageClick = async (message: MessageItem) => {
	console.log('消息点击:', message);
	
	// 如果消息是未读状态，先标记为已读
	if (message.readStatus === 0 && message.id) {
		try {
			const success = await noticeService.markAsRead(String(message.id));
			if (success) {
				message.readStatus = 1;
				// 刷新通知数据
				await loadNoticeData();
				console.log('消息已标记为已读');
			}
		} catch (error) {
			console.error('标记已读失败:', error);
		}
	}
	
	// 根据消息类型跳转到不同的详情页
	switch (message.type) {
		case 'notice':
			// 通知公告：打开详情对话框
			if (noticeDetailDialogRef.value && message.id) {
				noticeDetailDialogRef.value.openDialog(String(message.id), {
					status: message.readStatus,
					flg: 0
				});
			}
			break;
		case 'system':
			// 系统消息：跳转到系统消息页面
			router.push({
				path: '/system/notice',
				query: {
					id: message.id,
					type: 'system'
				}
			});
			break;
		case 'todo':
			// 待办提醒：跳转到待办页面
			router.push({
				path: '/todo',
				query: {
					id: message.id
				}
			});
			break;
		default:
			// 默认：显示提示信息
			ElMessage.success(`查看消息: ${message.title}`);
			break;
	}
};

/**
 * 前往通知中心事件处理
 */
const handleToCenter = () => {
	if (noticeBarRef.value) {
		noticeBarRef.value.showCenter();
	}
};

/**
 * 气泡弹窗展开事件处理
 */
const handlePopoverShow = () => {
	//console.log('气泡弹窗展开');
};

/**
 * 气泡弹窗收起事件处理
 */
const handlePopoverHide = () => {
	//console.log('气泡弹窗收起');
};

/**
 * 标记已读事件处理
 * 将单条消息标记为已读状态
 * @param {MessageItem} message 消息对象
 */
const handleMarkRead = async (message: MessageItem) => {
	console.log('标记已读:', message);
	if (message.id) {
		const success = await noticeService.markAsRead(String(message.id));
		if (success) {
			message.readStatus = 1;
			await loadNoticeData();
			ElMessage.success('已标记为已读');
		} else {
			ElMessage.error('标记已读失败');
		}
	}
};

/**
 * 全部标记已读事件处理
 * 将所有未读消息标记为已读状态
 */
const handleMarkAllRead = async () => {
	console.log('全部标记已读');
	try {
		const allMessages = noticeDataConfig.value.tabDataSource.all;
		const allIds: string[] = [];
		
		Object.values(allMessages).forEach(messages => {
			messages.forEach(message => {
				if (message.readStatus === 0 && message.id) {
					allIds.push(String(message.id));
				}
			});
		});
		
		if (allIds.length === 0) {
			ElMessage.info('没有未读消息');
			return;
		}
		
		const success = await noticeService.batchMarkAsRead(allIds);
		if (success) {
			await loadNoticeData();
			ElMessage.success('全部消息已标记为已读');
		} else {
			ElMessage.error('标记失败');
		}
	} catch (error) {
		console.error('全部标记已读失败:', error);
		ElMessage.error('标记失败');
	}
};

/**
 * 搜索事件处理
 * 根据搜索条件过滤通知消息
 * @param {Object} searchParams 搜索参数
 */
const handleSearch = async (searchParams: {
	title: string;
	tab: 'all' | 'unread' | 'read';
	source: string;
	resetPage?: boolean;
}) => {
	console.log('通知中心搜索:', searchParams);
	try {
		const params: any = {
			pageNo: 1,
			pageSize: 100
		};
		
		if (searchParams.title) {
			params.title = searchParams.title;
		}
		
		if (searchParams.tab !== 'all') {
			params.readStatus = searchParams.tab === 'unread' ? 0 : 1;
		}
		
		if (searchParams.source !== 'all') {
			params.type = searchParams.source;
		}
		
		const data = await noticeService.loadNoticeData(params);
		
		noticeDataConfig.value.messageList = data.messageList;
		noticeDataConfig.value.tabDataSource = data.tabDataSource;
		
		console.log('通知数据已更新', data);
	} catch (error) {
		console.error('搜索通知失败:', error);
		ElMessage.error('搜索通知失败');
	}
};

/**
 * 页面加载时生命周期钩子
 * 初始化语言和尺寸设置，加载通知数据
 */
onMounted(() => {
	if (Local.get('themeConfig')) {
		initI18nOrSize('globalComponentSize', 'disabledSize');
		initI18nOrSize('globalI18n', 'disabledI18n');
	}
	
	loadNoticeData();
});

/**
 * 加载通知数据
 * 从后端API获取真实的通知数据并更新到组件
 */
const loadNoticeData = async () => {
	try {
		const data = await noticeService.loadNoticeData({
			pageNo: 1,
			pageSize: 100
		});
		
		noticeDataConfig.value.messageList = data.messageList;
		noticeDataConfig.value.tabDataSource = data.tabDataSource;
		
	} catch (error) {
		ElMessage.error('加载通知数据失败');
	}
};
</script>

<style scoped lang="scss">
.layout-navbars-breadcrumb-user {
	display: flex;
	align-items: center;
	justify-content: flex-end;
	&-link {
		height: 100%;
		display: flex;
		align-items: center;
		white-space: nowrap;
		&-photo {
			width: 25px;
			height: 25px;
			border-radius: 100%;
		}
	}
	&-icon {
		padding: 0 10px;
		cursor: pointer;
		color: var(--next-bg-topBarColor);
		height: 50px;
		line-height: 50px;
		display: flex;
		align-items: center;
		&:hover {
			background: var(--next-color-user-hover);
			i {
				display: inline-block;
				animation: logoAnimation 0.3s ease-in-out;
			}
		}
	}
	:deep(.el-dropdown) {
		color: var(--next-bg-topBarColor);
	}
	:deep(.el-badge) {
		height: 40px;
		line-height: 40px;
		display: flex;
		align-items: center;
	}
	:deep(.el-badge__content.is-fixed) {
		top: 12px;
	}
	// YsNoticeBar 组件样式适配
	:deep(.ys-notice-bar) {
		height: 50px;
		display: flex;
		align-items: center;
		.ys-notice-icon {
			cursor: pointer;
			color: var(--next-bg-topBarColor);
			transition: color 0.3s;
			&:hover {
				color: var(--el-color-primary);
			}
		}
	}
}
</style>
