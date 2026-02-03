<template>
	<div class="layout-breadcrumb-seting">
		<!-- 主题配置抽屉组件 -->
		<el-drawer
			:title="$t('message.layout.configTitle')"
			v-model="getThemeConfig.isDrawer"
			direction="rtl"
			destroy-on-close
			size="300px"
			@close="onDrawerClose"
		>
			<el-scrollbar class="layout-breadcrumb-seting-bar">
				<!-- 全局主题设置区域 -->
				<el-divider content-position="left" class="settings-divider">{{ $t('message.layout.oneTitle') }}</el-divider>
				<!-- 主题色设置 -->
				<div class="layout-breadcrumb-seting-bar-flex">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Brush /></el-icon>
						<span>primary</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-color-picker v-model="getThemeConfig.primary" size="default" @change="onColorPickerChange"> </el-color-picker>
					</div>
				</div>
				<!-- 深色模式切换 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Moon /></el-icon>
						<span>{{ $t('message.layout.fourIsDark') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch v-model="getThemeConfig.isIsDark" size="small" @change="onAddDarkChange"></el-switch>
					</div>
				</div>

				<!-- 顶栏设置区域 -->
				<el-divider content-position="left" class="settings-divider">{{ $t('message.layout.twoTopTitle') }}</el-divider>
				<!-- 顶栏背景色 -->
				<div class="layout-breadcrumb-seting-bar-flex">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Top /></el-icon>
						<span>{{ $t('message.layout.twoTopBar') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-color-picker v-model="getThemeConfig.topBar" size="default" @change="onBgColorPickerChange('topBar')"> </el-color-picker>
					</div>
				</div>
				<!-- 顶栏文字颜色 -->
				<div class="layout-breadcrumb-seting-bar-flex">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-EditPen /></el-icon>
						<span>{{ $t('message.layout.twoTopBarColor') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-color-picker v-model="getThemeConfig.topBarColor" size="default" @change="onBgColorPickerChange('topBarColor')"> </el-color-picker>
					</div>
				</div>
				<!-- 顶栏渐变效果 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Picture /></el-icon>
						<span>{{ $t('message.layout.twoIsTopBarColorGradual') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch v-model="getThemeConfig.isTopBarColorGradual" size="small" @change="onTopBarGradualChange"></el-switch>
					</div>
				</div>

				<!-- 菜单设置区域 -->
				<el-divider content-position="left" class="settings-divider">{{ $t('message.layout.twoMenuTitle') }}</el-divider>
				<!-- 菜单背景色 -->
				<div class="layout-breadcrumb-seting-bar-flex">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Menu /></el-icon>
						<span>{{ $t('message.layout.twoMenuBar') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-color-picker v-model="getThemeConfig.menuBar" size="default" @change="onBgColorPickerChange('menuBar')"> </el-color-picker>
					</div>
				</div>
				<!-- 菜单文字颜色 -->
				<div class="layout-breadcrumb-seting-bar-flex">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-EditPen /></el-icon>
						<span>{{ $t('message.layout.twoMenuBarColor') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-color-picker v-model="getThemeConfig.menuBarColor" size="default" @change="onBgColorPickerChange('menuBarColor')"> </el-color-picker>
					</div>
				</div>
				<!-- 菜单激活颜色 -->
				<div class="layout-breadcrumb-seting-bar-flex">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-CircleCheck /></el-icon>
						<span>{{ $t('message.layout.twoMenuBarActiveColor') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-color-picker
							v-model="getThemeConfig.menuBarActiveColor"
							size="default"
							show-alpha
							@change="onBgColorPickerChange('menuBarActiveColor')"
						/>
					</div>
				</div>
				<!-- 菜单渐变效果 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Picture /></el-icon>
						<span>{{ $t('message.layout.twoIsMenuBarColorGradual') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch v-model="getThemeConfig.isMenuBarColorGradual" size="small" @change="onMenuBarGradualChange"></el-switch>
					</div>
				</div>

				<!-- 分栏设置区域 -->
				<el-divider content-position="left" class="settings-divider" :style="{ opacity: getThemeConfig.layout !== 'columns' ? 0.5 : 1 }">{{
					$t('message.layout.twoColumnsTitle')
				}}</el-divider>
				<!-- 分栏菜单背景色 -->
				<div class="layout-breadcrumb-seting-bar-flex" :style="{ opacity: getThemeConfig.layout !== 'columns' ? 0.5 : 1 }">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Menu /></el-icon>
						<span>{{ $t('message.layout.twoColumnsMenuBar') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-color-picker
							v-model="getThemeConfig.columnsMenuBar"
							size="default"
							@change="onBgColorPickerChange('columnsMenuBar')"
							:disabled="getThemeConfig.layout !== 'columns'"
						>
						</el-color-picker>
					</div>
				</div>
				<!-- 分栏菜单文字颜色 -->
				<div class="layout-breadcrumb-seting-bar-flex" :style="{ opacity: getThemeConfig.layout !== 'columns' ? 0.5 : 1 }">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-EditPen /></el-icon>
						<span>{{ $t('message.layout.twoColumnsMenuBarColor') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-color-picker
							v-model="getThemeConfig.columnsMenuBarColor"
							size="default"
							@change="onBgColorPickerChange('columnsMenuBarColor')"
							:disabled="getThemeConfig.layout !== 'columns'"
						>
						</el-color-picker>
					</div>
				</div>
				<!-- 分栏菜单渐变效果 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16" :style="{ opacity: getThemeConfig.layout !== 'columns' ? 0.5 : 1 }">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Picture /></el-icon>
						<span>{{ $t('message.layout.twoIsColumnsMenuBarColorGradual') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch
							v-model="getThemeConfig.isColumnsMenuBarColorGradual"
							size="small"
							@change="onColumnsMenuBarGradualChange"
							:disabled="getThemeConfig.layout !== 'columns'"
						></el-switch>
					</div>
				</div>
				<!-- 分栏菜单悬停预加载 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16" :style="{ opacity: getThemeConfig.layout !== 'columns' ? 0.5 : 1 }">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-CircleCheck /></el-icon>
						<span>{{ $t('message.layout.twoIsColumnsMenuHoverPreload') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch
							v-model="getThemeConfig.isColumnsMenuHoverPreload"
							size="small"
							@change="onColumnsMenuHoverPreloadChange"
							:disabled="getThemeConfig.layout !== 'columns'"
						></el-switch>
					</div>
				</div>

				<!-- 界面设置区域 -->
				<el-divider content-position="left" class="settings-divider">{{ $t('message.layout.threeTitle') }}</el-divider>
				<!-- 菜单折叠 -->
				<div class="layout-breadcrumb-seting-bar-flex" :style="{ opacity: getThemeConfig.layout === 'transverse' ? 0.5 : 1 }">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Fold /></el-icon>
						<span>{{ $t('message.layout.threeIsCollapse') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch
							v-model="getThemeConfig.isCollapse"
							:disabled="getThemeConfig.layout === 'transverse'"
							size="small"
							@change="onThemeConfigChange"
						></el-switch>
					</div>
				</div>
				<!-- 菜单手风琴模式 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16" :style="{ opacity: getThemeConfig.layout === 'transverse' ? 0.5 : 1 }">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Files /></el-icon>
						<span>{{ $t('message.layout.threeIsUniqueOpened') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch
							v-model="getThemeConfig.isUniqueOpened"
							:disabled="getThemeConfig.layout === 'transverse'"
							size="small"
							@change="setLocalThemeConfig"
						></el-switch>
					</div>
				</div>
				<!-- 固定 Header -->
				<div class="layout-breadcrumb-seting-bar-flex mt16">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-TopLeft /></el-icon>
						<span>{{ $t('message.layout.threeIsFixedHeader') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch v-model="getThemeConfig.isFixedHeader" size="small" @change="onIsFixedHeaderChange"></el-switch>
					</div>
				</div>
				<!-- 经典布局分割菜单 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16" :style="{ opacity: getThemeConfig.layout !== 'classic' ? 0.5 : 1 }">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-CopyDocument /></el-icon>
						<span>{{ $t('message.layout.threeIsClassicSplitMenu') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch
							v-model="getThemeConfig.isClassicSplitMenu"
							:disabled="getThemeConfig.layout !== 'classic'"
							size="small"
							@change="onClassicSplitMenuChange"
						>
						</el-switch>
					</div>
				</div>
				<!-- 锁屏设置 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Lock /></el-icon>
						<span>{{ $t('message.layout.threeIsLockScreen') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch v-model="getThemeConfig.isLockScreen" size="small" @change="setLocalThemeConfig"></el-switch>
					</div>
				</div>
				<!-- 锁屏时间 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Clock /></el-icon>
						<span>{{ $t('message.layout.threeLockScreenTime') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-input-number
							v-model="getThemeConfig.lockScreenTime"
							controls-position="right"
							:min="1"
							:max="9999"
							@change="setLocalThemeConfig"
							size="default"
							style="width: 90px"
						>
						</el-input-number>
					</div>
				</div>

				<!-- 界面显示设置区域 -->
				<el-divider content-position="left" class="settings-divider">{{ $t('message.layout.fourTitle') }}</el-divider>
				<!-- 显示 Logo -->
				<div class="layout-breadcrumb-seting-bar-flex mt16">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Grid /></el-icon>
						<span>{{ $t('message.layout.fourIsShowLogo') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch v-model="getThemeConfig.isShowLogo" size="small" @change="onIsShowLogoChange"></el-switch>
					</div>
				</div>
				<!-- 显示面包屑 -->
				<div
					class="layout-breadcrumb-seting-bar-flex mt16"
					:style="{ opacity: getThemeConfig.layout === 'classic' || getThemeConfig.layout === 'transverse' ? 0.5 : 1 }"
				>
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Location /></el-icon>
						<span>{{ $t('message.layout.fourIsBreadcrumb') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch
							v-model="getThemeConfig.isBreadcrumb"
							:disabled="getThemeConfig.layout === 'classic' || getThemeConfig.layout === 'transverse'"
							size="small"
							@change="onIsBreadcrumbChange"
						></el-switch>
					</div>
				</div>
				<!-- 面包屑图标 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Menu /></el-icon>
						<span>{{ $t('message.layout.fourIsBreadcrumbIcon') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch v-model="getThemeConfig.isBreadcrumbIcon" size="small" @change="setLocalThemeConfig"></el-switch>
					</div>
				</div>
				<!-- 显示标签页 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Tickets /></el-icon>
						<span>{{ $t('message.layout.fourIsTagsview') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch v-model="getThemeConfig.isTagsview" size="small" @change="setLocalThemeConfig"></el-switch>
					</div>
				</div>
				<!-- 标签页图标 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Picture /></el-icon>
						<span>{{ $t('message.layout.fourIsTagsviewIcon') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch v-model="getThemeConfig.isTagsviewIcon" size="small" @change="setLocalThemeConfig"></el-switch>
					</div>
				</div>
				<!-- 缓存标签页 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Collection /></el-icon>
						<span>{{ $t('message.layout.fourIsCacheTagsView') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch v-model="getThemeConfig.isCacheTagsView" size="small" @change="setLocalThemeConfig"></el-switch>
					</div>
				</div>
				<!-- 标签页拖拽排序 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16" :style="{ opacity: state.isMobile ? 0.5 : 1 }">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Sort /></el-icon>
						<span>{{ $t('message.layout.fourIsSortableTagsView') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch
							v-model="getThemeConfig.isSortableTagsView"
							:disabled="state.isMobile ? true : false"
							size="small"
							@change="onSortableTagsViewChange"
						></el-switch>
					</div>
				</div>
				<!-- 标签页共享 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Share /></el-icon>
						<span>{{ $t('message.layout.fourIsShareTagsView') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch v-model="getThemeConfig.isShareTagsView" size="small" @change="onShareTagsViewChange"></el-switch>
					</div>
				</div>
				<!-- 显示页脚 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Bottom /></el-icon>
						<span>{{ $t('message.layout.fourIsFooter') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch v-model="getThemeConfig.isFooter" size="small" @change="setLocalThemeConfig"></el-switch>
					</div>
				</div>
				<!-- 灰色模式 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-MagicStick /></el-icon>
						<span>{{ $t('message.layout.fourIsGrayscale') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch v-model="getThemeConfig.isGrayscale" size="small" @change="onAddFilterChange('grayscale')"></el-switch>
					</div>
				</div>
				<!-- 色弱模式 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-RefreshLeft /></el-icon>
						<span>{{ $t('message.layout.fourIsInvert') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch v-model="getThemeConfig.isInvert" size="small" @change="onAddFilterChange('invert')"></el-switch>
					</div>
				</div>
				<!-- 水印设置 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Crop /></el-icon>
						<span>{{ $t('message.layout.fourIsWartermark') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-switch v-model="getThemeConfig.isWartermark" size="small" @change="onWartermarkChange"></el-switch>
					</div>
				</div>
				<!-- 水印文案 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Edit /></el-icon>
						<span>{{ $t('message.layout.fourWartermarkText') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-input v-model="getThemeConfig.wartermarkText" size="default" style="width: 90px" @input="onWartermarkTextInput"></el-input>
					</div>
				</div>

				<!-- 其它设置区域 -->
				<el-divider content-position="left" class="settings-divider">{{ $t('message.layout.fiveTitle') }}</el-divider>
				<!-- 标签页风格 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-PriceTag /></el-icon>
						<span>{{ $t('message.layout.fiveTagsStyle') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-select v-model="getThemeConfig.tagsStyle" placeholder="请选择" size="default" style="width: 90px" @change="setLocalThemeConfig">
							<el-option label="风格1" value="tags-style-one"></el-option>
							<el-option label="风格4" value="tags-style-four"></el-option>
							<el-option label="风格5" value="tags-style-five"></el-option>
						</el-select>
					</div>
				</div>
				<!-- 页面动画 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-VideoPlay /></el-icon>
						<span>{{ $t('message.layout.fiveAnimation') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-select v-model="getThemeConfig.animation" placeholder="请选择" size="default" style="width: 90px" @change="setLocalThemeConfig">
							<el-option label="slide-right" value="slide-right"></el-option>
							<el-option label="slide-left" value="slide-left"></el-option>
							<el-option label="opacitys" value="opacitys"></el-option>
						</el-select>
					</div>
				</div>
				<!-- 分栏侧边栏风格 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16" :style="{ opacity: getThemeConfig.layout !== 'columns' ? 0.5 : 1 }">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Monitor /></el-icon>
						<span>{{ $t('message.layout.fiveColumnsAsideStyle') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-select
							v-model="getThemeConfig.columnsAsideStyle"
							placeholder="请选择"
							size="default"
							style="width: 90px"
							:disabled="getThemeConfig.layout !== 'columns' ? true : false"
							@change="setLocalThemeConfig"
						>
							<el-option label="圆角" value="columns-round"></el-option>
							<el-option label="卡片" value="columns-card"></el-option>
						</el-select>
					</div>
				</div>
				<!-- 分栏侧边栏布局 -->
				<div class="layout-breadcrumb-seting-bar-flex mt16" :style="{ opacity: getThemeConfig.layout !== 'columns' ? 0.5 : 1 }">
					<div class="layout-breadcrumb-seting-bar-flex-label">
						<el-icon class="setting-icon"><ele-Grid /></el-icon>
						<span>{{ $t('message.layout.fiveColumnsAsideLayout') }}</span>
					</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-select
							v-model="getThemeConfig.columnsAsideLayout"
							placeholder="请选择"
							size="default"
							style="width: 90px"
							:disabled="getThemeConfig.layout !== 'columns' ? true : false"
							@change="setLocalThemeConfig"
						>
							<el-option label="水平" value="columns-horizontal"></el-option>
							<el-option label="垂直" value="columns-vertical"></el-option>
						</el-select>
					</div>
				</div>

				<!-- 布局切换区域 -->
				<el-divider content-position="left">{{ $t('message.layout.sixTitle') }}</el-divider>
				<div class="layout-drawer-content-flex">
					<!-- defaults 布局 -->
					<div class="layout-drawer-content-item" @click="onSetLayout('defaults')">
						<section class="el-container el-circular" :class="{ 'drawer-layout-active': getThemeConfig.layout === 'defaults' }">
							<aside class="el-aside" style="width: 20px"></aside>
							<section class="el-container is-vertical">
								<header class="el-header" style="height: 10px"></header>
								<main class="el-main"></main>
							</section>
						</section>
						<div class="layout-tips-warp" :class="{ 'layout-tips-warp-active': getThemeConfig.layout === 'defaults' }">
							<div class="layout-tips-box">
								<p class="layout-tips-txt">{{ $t('message.layout.sixDefaults') }}</p>
							</div>
						</div>
					</div>
					<!-- classic 布局 -->
					<div class="layout-drawer-content-item" @click="onSetLayout('classic')">
						<section class="el-container is-vertical el-circular" :class="{ 'drawer-layout-active': getThemeConfig.layout === 'classic' }">
							<header class="el-header" style="height: 10px"></header>
							<section class="el-container">
								<aside class="el-aside" style="width: 20px"></aside>
								<section class="el-container is-vertical">
									<main class="el-main"></main>
								</section>
							</section>
						</section>
						<div class="layout-tips-warp" :class="{ 'layout-tips-warp-active': getThemeConfig.layout === 'classic' }">
							<div class="layout-tips-box">
								<p class="layout-tips-txt">{{ $t('message.layout.sixClassic') }}</p>
							</div>
						</div>
					</div>
					<!-- transverse 布局 -->
					<div class="layout-drawer-content-item" @click="onSetLayout('transverse')">
						<section class="el-container is-vertical el-circular" :class="{ 'drawer-layout-active': getThemeConfig.layout === 'transverse' }">
							<header class="el-header" style="height: 10px"></header>
							<section class="el-container">
								<section class="el-container is-vertical">
									<main class="el-main"></main>
								</section>
							</section>
						</section>
						<div class="layout-tips-warp" :class="{ 'layout-tips-warp-active': getThemeConfig.layout === 'transverse' }">
							<div class="layout-tips-box">
								<p class="layout-tips-txt">{{ $t('message.layout.sixTransverse') }}</p>
							</div>
						</div>
					</div>
					<!-- columns 布局 -->
					<div class="layout-drawer-content-item" @click="onSetLayout('columns')">
						<section class="el-container el-circular" :class="{ 'drawer-layout-active': getThemeConfig.layout === 'columns' }">
							<aside class="el-aside-dark" style="width: 10px"></aside>
							<aside class="el-aside" style="width: 20px"></aside>
							<section class="el-container is-vertical">
								<header class="el-header" style="height: 10px"></header>
								<main class="el-main"></main>
							</section>
						</section>
						<div class="layout-tips-warp" :class="{ 'layout-tips-warp-active': getThemeConfig.layout === 'columns' }">
							<div class="layout-tips-box">
								<p class="layout-tips-txt">{{ $t('message.layout.sixColumns') }}</p>
							</div>
						</div>
					</div>
				</div>
				<!-- 配置操作按钮区域 -->
				<div class="copy-config">
					<el-alert :title="$t('message.layout.tipText')" type="warning" :closable="false"> </el-alert>
					<el-button size="default" class="copy-config-btn" type="primary" ref="copyConfigBtnRef" @click="onCopyConfigClick">
						<el-icon class="mr5">
							<ele-CopyDocument />
						</el-icon>
						{{ $t('message.layout.copyText') }}
					</el-button>
					<el-button size="default" class="copy-config-btn-reset" type="info" @click="onResetConfigClick">
						<el-icon class="mr5">
							<ele-RefreshRight />
						</el-icon>
						{{ $t('message.layout.resetText') }}
					</el-button>
				</div>
			</el-scrollbar>
		</el-drawer>
	</div>
</template>

<script setup lang="ts" name="layoutBreadcrumbSeting">
import { nextTick, onUnmounted, onMounted, computed, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '@/stores/themeConfig';
import { useChangeColor } from '@/utils/theme';
import { verifyAndSpace } from '@/utils/toolsValidate';
import { Local } from '@/utils/storage';
import Watermark from '@/utils/watermark';
import commonFunction from '@/utils/commonFunction';
import other from '@/utils/other';
import mittBus from '@/utils/mitt';
import { VxeUI } from 'vxe-table'
import { loadHighlightTheme } from '@/utils/highlightTheme'

/**
 * 主题设置抽屉组件
 * 提供全局主题、顶栏、菜单、界面显示、布局切换等配置功能
 * 支持一键复制配置和恢复默认设置
 * @author Eric
 * @since 2026-01-30
 */

// 定义变量内容
const { locale } = useI18n();
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);
const { copyText } = commonFunction();
const { getLightColor, getDarkColor } = useChangeColor();

/**
 * 响应式状态对象
 * @property {boolean} isMobile - 是否为移动端设备
 */
const state = reactive({
	isMobile: false,
});

/**
 * 计算属性：获取布局配置信息
 * @returns {ThemeConfigState} 主题配置对象
 */
const getThemeConfig = computed(() => {
	return themeConfig.value;
});

/**
 * 全局主题色变化处理
 * 设置主色调及其深浅色阶
 */
const onColorPickerChange = () => {
	if (!getThemeConfig.value.primary) return ElMessage.warning('全局主题 primary 颜色值不能为空');
	// 颜色加深
	document.documentElement.style.setProperty('--el-color-primary-dark-2', `${getDarkColor(getThemeConfig.value.primary, 0.1)}`);
	document.documentElement.style.setProperty('--el-color-primary', getThemeConfig.value.primary);
	// 颜色变浅
	for (let i = 1; i <= 9; i++) {
		document.documentElement.style.setProperty(`--el-color-primary-light-${i}`, `${getLightColor(getThemeConfig.value.primary, i / 10)}`);
	}
	setDispatchThemeConfig();
};

/**
 * 菜单/顶栏背景色变化处理
 * @param {string} bg - 背景色配置项名称
 */
const onBgColorPickerChange = (bg: string) => {
  const themeValue = themeConfig.value[bg as keyof typeof themeConfig.value];
  document.documentElement.style.setProperty(`--next-bg-${bg}`, themeValue as string);
  if (bg === 'menuBar') {
    document.documentElement.style.setProperty(`--next-bg-menuBar-light-1`, getLightColor(getThemeConfig.value.menuBar, 0.05));
  }
  onTopBarGradualChange();
  onMenuBarGradualChange();
  onColumnsMenuBarGradualChange();
  setDispatchThemeConfig();
};

/**
 * 顶栏背景渐变变化处理
 */
const onTopBarGradualChange = () => {
	setGraduaFun('.layout-navbars-breadcrumb-index', getThemeConfig.value.isTopBarColorGradual, getThemeConfig.value.topBar);
};

/**
 * 菜单背景渐变变化处理
 */
const onMenuBarGradualChange = () => {
	setGraduaFun('.layout-container .el-aside', getThemeConfig.value.isMenuBarColorGradual, getThemeConfig.value.menuBar);
};

/**
 * 分栏菜单背景渐变变化处理
 */
const onColumnsMenuBarGradualChange = () => {
	setGraduaFun('.layout-container .layout-columns-aside', getThemeConfig.value.isColumnsMenuBarColorGradual, getThemeConfig.value.columnsMenuBar);
};

/**
 * 背景渐变设置函数
 * 根据配置为指定元素设置渐变背景
 * @param {string} el - CSS 选择器
 * @param {boolean} bool - 是否启用渐变
 * @param {string} color - 基础颜色
 */
const setGraduaFun = (el: string, bool: boolean, color: string) => {
	nextTick(() => {
		setTimeout(() => {
			let els = document.querySelector(el);
			if (!els) return false;
			document.documentElement.style.setProperty('--el-menu-bg-color', document.documentElement.style.getPropertyValue('--next-bg-menuBar'));
			if (bool) els.setAttribute('style', `background:linear-gradient(to bottom , ${color}, ${getLightColor(color, 0.5)})`);
			else els.setAttribute('style', ``);
			setLocalThemeConfig();
		}, 300);
	});
};

/**
 * 分栏菜单悬停预加载变化处理
 */
const onColumnsMenuHoverPreloadChange = () => {
	setLocalThemeConfig();
};

/**
 * 菜单折叠变化处理
 */
const onThemeConfigChange = () => {
	setDispatchThemeConfig();
};

/**
 * 固定 Header 变化处理
 */
const onIsFixedHeaderChange = () => {
	getThemeConfig.value.isFixedHeaderChange = getThemeConfig.value.isFixedHeader ? false : true;
	setLocalThemeConfig();
};

/**
 * 经典布局分割菜单变化处理
 */
const onClassicSplitMenuChange = () => {
	getThemeConfig.value.isBreadcrumb = false;
	setLocalThemeConfig();
	mittBus.emit('getBreadcrumbIndexSetFilterRoutes');
};

/**
 * 侧边栏 Logo 显示变化处理
 */
const onIsShowLogoChange = () => {
	getThemeConfig.value.isShowLogoChange = getThemeConfig.value.isShowLogo ? false : true;
	setLocalThemeConfig();
};

/**
 * 面包屑显示变化处理
 */
const onIsBreadcrumbChange = () => {
	if (getThemeConfig.value.layout === 'classic') {
		getThemeConfig.value.isClassicSplitMenu = false;
	}
	setLocalThemeConfig();
};

/**
 * 标签页拖拽排序变化处理
 */
const onSortableTagsViewChange = () => {
	mittBus.emit('openOrCloseSortable');
	setLocalThemeConfig();
};

/**
 * 标签页共享变化处理
 */
const onShareTagsViewChange = () => {
	mittBus.emit('openShareTagsView');
	setLocalThemeConfig();
};

/**
 * 灰色模式/色弱模式变化处理
 * @param {string} attr - 滤镜类型（grayscale/invert）
 */
const onAddFilterChange = (attr: string) => {
	if (attr === 'grayscale') {
		if (getThemeConfig.value.isGrayscale) getThemeConfig.value.isInvert = false;
	} else {
		if (getThemeConfig.value.isInvert) getThemeConfig.value.isGrayscale = false;
	}
	const cssAttr =
		attr === 'grayscale' ? `grayscale(${getThemeConfig.value.isGrayscale ? 1 : 0})` : `invert(${getThemeConfig.value.isInvert ? '80%' : '0%'})`;
	const appEle = document.body;
	appEle.setAttribute('style', `filter: ${cssAttr}`);
	setLocalThemeConfig();
};

/**
 * 深色模式变化处理
 */
const onAddDarkChange = () => {
	const body = document.documentElement as HTMLElement;
	if (getThemeConfig.value.isIsDark) {
		body.setAttribute('data-theme', 'dark');
		VxeUI.setTheme('dark')

		//此处动态改变
		loadHighlightTheme('dark');
	} else {
		body.setAttribute('data-theme', '');
		VxeUI.setTheme('light')
		loadHighlightTheme('light');
	}
};

/**
 * 水印显示变化处理
 */
const onWartermarkChange = () => {
	getThemeConfig.value.isWartermark ? Watermark.set(getThemeConfig.value.wartermarkText) : Watermark.del();
	setLocalThemeConfig();
};

/**
 * 水印文案输入处理
 * @param {string} val - 输入的水印文案
 */
const onWartermarkTextInput = (val: string) => {
	getThemeConfig.value.wartermarkText = verifyAndSpace(val);
	if (getThemeConfig.value.wartermarkText === '') return false;
	if (getThemeConfig.value.isWartermark) Watermark.set(getThemeConfig.value.wartermarkText);
	setLocalThemeConfig();
};

/**
 * 布局切换处理
 * @param {string} layout - 布局类型（defaults/classic/transverse/columns）
 */
const onSetLayout = (layout: string) => {
	Local.set('oldLayout', layout);
	if (getThemeConfig.value.layout === layout) return false;
	if (layout === 'transverse') getThemeConfig.value.isCollapse = false;
	getThemeConfig.value.layout = layout;
	getThemeConfig.value.isDrawer = false;
	initLayoutChangeFun();
};

/**
 * 初始化布局切换相关样式
 */
const initLayoutChangeFun = () => {
	onBgColorPickerChange('menuBar');
	onBgColorPickerChange('menuBarColor');
	onBgColorPickerChange('menuBarActiveColor');
	onBgColorPickerChange('topBar');
	onBgColorPickerChange('topBarColor');
	onBgColorPickerChange('columnsMenuBar');
	onBgColorPickerChange('columnsMenuBarColor');
};

/**
 * 抽屉关闭处理
 * 初始化相关变量，用于处理滚动条高度更新
 */
const onDrawerClose = () => {
	getThemeConfig.value.isFixedHeaderChange = false;
	getThemeConfig.value.isShowLogoChange = false;
	getThemeConfig.value.isDrawer = false;
	setLocalThemeConfig();
};

/**
 * 打开抽屉
 * 暴露给外部调用的方法
 */
const openDrawer = () => {
	getThemeConfig.value.isDrawer = true;
};

/**
 * 触发 store 布局配置更新
 */
const setDispatchThemeConfig = () => {
	setLocalThemeConfig();
	setLocalThemeConfigStyle();
};

/**
 * 存储布局配置到本地
 */
const setLocalThemeConfig = () => {
	Local.remove('themeConfig');
	Local.set('themeConfig', getThemeConfig.value);
};

/**
 * 存储布局配置全局主题样式到本地
 * 保存 HTML 根标签的 CSS 变量
 */
const setLocalThemeConfigStyle = () => {
	Local.set('themeConfigStyle', document.documentElement.style.cssText);
};

/**
 * 一键复制配置
 * 将当前主题配置复制到剪贴板
 */
const onCopyConfigClick = () => {
	let copyThemeConfig = Local.get('themeConfig');
	copyThemeConfig.isDrawer = false;
	copyText(JSON.stringify(copyThemeConfig)).then(() => {
		getThemeConfig.value.isDrawer = false;
	});
};

/**
 * 一键恢复默认
 * 清除本地存储并刷新页面
 */
const onResetConfigClick = () => {
	Local.clear();
	window.location.reload();
	// @ts-ignore
	Local.set('version', __NEXT_VERSION__);
};

/**
 * 初始化菜单样式
 * 设置顶栏、菜单、分栏菜单的渐变效果
 */
const initSetStyle = () => {
	// 2、菜单 / 顶栏 --> 顶栏背景渐变
	onTopBarGradualChange();
	// 2、菜单 / 顶栏 --> 菜单背景渐变
	onMenuBarGradualChange();
	// 2、菜单 / 顶栏 --> 分栏菜单背景渐变
	onColumnsMenuBarGradualChange();
};

/**
 * 组件挂载时生命周期钩子
 * 初始化布局样式、监听窗口大小变化、应用主题配置
 */
onMounted(() => {
	nextTick(() => {
		// 判断当前布局是否不相同，不相同则初始化当前布局的样式，防止监听窗口大小改变时，布局配置logo、菜单背景等部分布局失效问题
		if (!Local.get('frequency')) initLayoutChangeFun();
		Local.set('frequency', 1);
		// 监听窗口大小改变，非默认布局，设置成默认布局（适配移动端）
		mittBus.on('layoutMobileResize', (res: LayoutMobileResize) => {
			getThemeConfig.value.layout = res.layout;
			getThemeConfig.value.isDrawer = false;
			initLayoutChangeFun();
			state.isMobile = other.isMobile();
		});
		setTimeout(() => {
			// 默认样式
			onColorPickerChange();
			// 灰色模式
			if (getThemeConfig.value.isGrayscale) onAddFilterChange('grayscale');
			// 色弱模式
			if (getThemeConfig.value.isInvert) onAddFilterChange('invert');
			// 深色模式
			if (getThemeConfig.value.isIsDark) onAddDarkChange();
			// 开启水印
			onWartermarkChange();
			// 语言国际化
			if (Local.get('themeConfig')) locale.value = Local.get('themeConfig').globalI18n;
			// 初始化菜单样式等
			initSetStyle();
		}, 100);
	});
});

/**
 * 组件卸载时生命周期钩子
 * 移除事件监听，防止内存泄漏
 */
onUnmounted(() => {
	mittBus.off('layoutMobileResize', () => {});
});

/**
 * 暴露变量
 * 提供 openDrawer 方法供外部调用
 */
defineExpose({
	openDrawer,
});
</script>

<style scoped lang="scss">
.layout-breadcrumb-seting-bar {
	height: calc(100vh - 50px);
	padding: 0 15px;
	font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
	:deep(.el-scrollbar__view) {
		overflow-x: hidden !important;
	}
	.layout-breadcrumb-seting-bar-flex {
		display: flex;
		align-items: center;
		margin-bottom: 16px;
		padding: 8px 12px;
		border-radius: 6px;
		transition: all 0.2s ease;
		&:hover {
			background-color: var(--el-fill-color-light);
		}
		&-label {
			flex: 1;
			color: var(--el-text-color-primary);
			display: flex;
			align-items: center;
			gap: 8px;
			font-size: 14px;
			font-weight: 500;
			line-height: 1.5;
			letter-spacing: 0.01em;
			.setting-icon {
				font-size: 16px;
				color: var(--el-color-primary);
				transition: all 0.2s ease;
			}
		}
		&-value {
			display: flex;
			align-items: center;
		}
	}
	.settings-divider {
		margin: 24px 0 16px 0;
		font-weight: 600;
		font-size: 15px;
		color: var(--el-text-color-primary);
		line-height: 1.5;
		letter-spacing: 0.02em;
		:deep(.el-divider__text) {
			background-color: var(--el-bg-color);
			padding: 0 8px;
		}
	}
	.layout-drawer-content-flex {
		overflow: hidden;
		display: flex;
		flex-wrap: wrap;
		align-content: flex-start;
		margin: 0 -5px;
		.layout-drawer-content-item {
			width: 50%;
			height: 70px;
			cursor: pointer;
			border: 1px solid transparent;
			position: relative;
			padding: 5px;
			transition: all 0.3s ease;
			border-radius: 8px;
			&:hover {
				transform: translateY(-2px);
				box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
			}
			.el-container {
				height: 100%;
				.el-aside-dark {
					background-color: var(--next-color-seting-header);
				}
				.el-aside {
					background-color: var(--next-color-seting-aside);
				}
				.el-header {
					background-color: var(--next-color-seting-header);
				}
				.el-main {
					background-color: var(--next-color-seting-main);
				}
			}
			.el-circular {
				border-radius: 2px;
				overflow: hidden;
				border: 1px solid transparent;
				transition: all 0.3s ease-in-out;
			}
			.drawer-layout-active {
				border: 1px solid;
				border-color: var(--el-color-primary);
			}
			.layout-tips-warp,
			.layout-tips-warp-active {
				transition: all 0.3s ease-in-out;
				position: absolute;
				left: 50%;
				top: 50%;
				transform: translate(-50%, -50%);
				border: 1px solid;
				border-color: var(--el-color-primary-light-5);
				border-radius: 100%;
				padding: 4px;
				.layout-tips-box {
					transition: inherit;
					width: 30px;
					height: 30px;
					z-index: 9;
					border: 1px solid;
					border-color: var(--el-color-primary-light-5);
					border-radius: 100%;
					.layout-tips-txt {
						transition: inherit;
						position: relative;
						top: 5px;
						font-size: 12px;
						font-weight: 600;
						line-height: 1;
						letter-spacing: 2px;
						white-space: nowrap;
						color: var(--el-color-primary-light-5);
						text-align: center;
						transform: rotate(30deg);
						left: -1px;
						background-color: var(--next-color-seting-main);
						width: 32px;
						height: 17px;
						line-height: 17px;
					}
				}
			}
			.layout-tips-warp-active {
				border: 1px solid;
				border-color: var(--el-color-primary);
				.layout-tips-box {
					border: 1px solid;
					border-color: var(--el-color-primary);
					.layout-tips-txt {
						color: var(--el-color-primary) !important;
						background-color: var(--next-color-seting-main) !important;
					}
				}
			}
			&:hover {
				.el-circular {
					transition: all 0.3s ease-in-out;
					border: 1px solid;
					border-color: var(--el-color-primary);
				}
				.layout-tips-warp {
					transition: all 0.3s ease-in-out;
					border-color: var(--el-color-primary);
					.layout-tips-box {
						transition: inherit;
						border-color: var(--el-color-primary);
						.layout-tips-txt {
							transition: inherit;
							color: var(--el-color-primary) !important;
							background-color: var(--next-color-seting-main) !important;
						}
					}
				}
			}
		}
	}
	.copy-config {
		margin: 20px 0;
		padding: 16px;
		background-color: var(--el-fill-color-light);
		border-radius: 8px;
		.copy-config-btn {
			width: 100%;
			margin-top: 12px;
			height: 40px;
			font-size: 14px;
			font-weight: 500;
			transition: all 0.2s ease;
		}
		.copy-config-btn-reset {
			width: 100%;
			margin: 12px 0 0;
			height: 40px;
			font-size: 14px;
			font-weight: 500;
			transition: all 0.2s ease;
		}
	}
}
</style>
