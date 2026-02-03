<template>
  <div class="ys-notice-bar" :class="customClass" :style="customStyle">
    <!--
      通知气泡组件 (el-popover)
      trigger: 触发方式，支持 hover/click/both
      placement: 弹出位置，这里固定为底部
      visible: 控制气泡显示/隐藏
      show-arrow: 不显示箭头，保持简洁风格
    -->
    <el-popover
      ref="popoverRef"
      :visible="popoverVisible"
      :width="mergedDisplayConfig.popover?.width || 380"
      :placement="'bottom'"
      :trigger="mergedInteractionConfig.trigger?.mode === 'click' ? 'click' : 'hover'"
      :offset="mergedDisplayConfig.popover?.offset || 0"
      :show-arrow="false"
      :popper-class="`ys-notice-popover ${mergedDisplayConfig.popover?.shadow ? 'has-shadow' : ''}`"
      :popper-style="popoverStyle"
      @show="handlePopoverShow"
      @hide="handlePopoverHide"
    >
      <!-- 气泡触发源：通知图标 -->
      <template #reference>
        <div
          ref="iconRef"
          class="notice-icon-wrapper"
          @mouseenter="handleMouseEnter"
          @mouseleave="handleMouseLeave"
          @click="handleIconClick"
        >
          <!-- 徽标组件：显示未读消息数量 -->
          <el-badge
            :is-dot="true"
            :hidden="!mergedDisplayConfig.icon?.showBadge || unreadCount === 0"
            :class="`badge-${mergedDisplayConfig.icon?.badgePosition || 'top-right'}`"
          >
            <el-icon
              :size="mergedDisplayConfig.icon?.size || 20"
              :color="mergedDisplayConfig.icon?.color"
              :class="{
                'notice-icon': true,
                'rotate-animation': mergedDisplayConfig.icon?.rotate,
                'blink-animation': mergedDisplayConfig.icon?.blink
              }"
            >
              <Bell />
            </el-icon>
          </el-badge>
        </div>
      </template>
      
      <!-- 气泡内容：NoticePopover 通知预览组件 -->
      <NoticePopover
        :message-list="dataConfig.messageList"
        :display-config="mergedDisplayConfig"
        :interaction-config="mergedInteractionConfig"
        :style-config="mergedStyleConfig"
        :data-config="dataConfig"
        :source-config="dataConfig.sourceConfig"
        @message-click="handleMessageClick"
        @to-center="handleToCenter"
        @mark-read="handleMarkRead"
        @mark-all-read="handleMarkAllRead"
      />
    </el-popover>

    <!-- 通知中心弹窗：查看所有通知的完整列表 -->
    <NoticeCenter
      v-model="centerVisible"
      :tab-data-source="dataConfig.tabDataSource"
      :source-config="dataConfig.sourceConfig"
      :display-config="mergedDisplayConfig"
      :interaction-config="mergedInteractionConfig.center"
      :style-config="mergedStyleConfig"
      @message-click="handleMessageClick"
      @close="handleCenterClose"
      @tab-change="handleTabChange"
      @mark-all-read="handleMarkAllRead"
      @search="handleSearch"
    />

    <!-- 通知详情对话框：查看单条通知的详细内容 -->
    <NoticeDetailDialog
      v-model:visible="detailVisible"
      :message="currentMessage"
      :width="'60%'"
      :height="'auto'"
      :auto-mark-read="mergedInteractionConfig.item?.autoMarkRead !== false"
      :allow-delete="false"
      :show-actions="true"
      :style-config="mergedStyleConfig"
      @mark-read="handleMarkRead"
      @close="handleDetailClose"
    />
  </div>
</template>

<script setup lang="ts" name="YsNoticeBar">
/**
 * YsNoticeBar 通知栏组件
 * 
 * 组件功能描述：
 * 这是一个功能完整的通知栏组件，提供通知图标展示、未读消息徽标、气泡预览、
 * 通知中心弹窗和详情查看等核心功能。组件支持高度自定义，包括图标样式、
 * 气泡样式、交互行为等多个维度的配置。
 * 
 * 核心功能：
 * 1. 通知图标展示：支持自定义图标大小、颜色、动画效果（旋转、闪烁）
 * 2. 未读消息徽标：显示未读消息数量，支持自定义位置和样式
 * 3. 气泡预览：鼠标悬停或点击时显示通知预览列表
 * 4. 通知中心：提供完整的通知列表查看、分页、筛选功能
 * 5. 消息详情：支持查看单条消息的详细内容
 * 
 * 使用示例：
 * <YsNoticeBar
 *   :data-config="noticeDataConfig"
 *   :display-config="displayConfig"
 *   :interaction-config="interactionConfig"
 *   @message-click="handleMessageClick"
 * />
 */

import { ref, computed, defineAsyncComponent, watch, onMounted, onUnmounted } from 'vue';
import { ElPopover, ElIcon, ElBadge } from 'element-plus';
import { Bell } from '@element-plus/icons-vue';
import type { YsNoticeBarProps, YsNoticeBarEmits, MessageItem, DisplayConfig, InteractionConfig, StyleConfig, DataConfig } from '@/types/notice';

// 异步加载子组件，实现按需加载优化性能
const NoticePopover = defineAsyncComponent(() => import('./components/NoticePopover.vue'));
const NoticeCenter = defineAsyncComponent(() => import('./components/NoticeCenter.vue'));
const NoticeDetailDialog = defineAsyncComponent(() => import('./components/NoticeDetailDialog.vue'));

// 定义组件名称，用于 keep-alive 缓存和调试
defineOptions({
  name: 'YsNoticeBar'
});

/**
 * Props 属性说明
 * 
 * YsNoticeBarProps 接口定义了组件所有可配置属性，分为以下几类：
 * 1. dataConfig: 数据配置，包含消息列表和来源配置
 * 2. displayConfig: 显示配置，控制图标、气泡、列表、尾部等视觉效果
 * 3. interactionConfig: 交互配置，控制触发方式、自动行为、缓存等交互逻辑
 * 4. styleConfig: 样式配置，控制主题、响应式断点等全局样式
 */

// 定义组件接收的属性，使用 withDefaults 提供默认值
const props = withDefaults(defineProps<YsNoticeBarProps>(), {
  displayConfig: () => ({}),
  interactionConfig: () => ({}),
  styleConfig: () => ({}),
  
});

/**
 * Emits 事件说明
 * 
 * YsNoticeBarEmits 接口定义了组件派发的事件：
 * - message-click: 点击消息时触发
 * - mark-read: 标记消息已读时触发
 * - mark-all-read: 标记全部已读时触发
 * - to-center: 前往通知中心时触发
 * - popover-show: 气泡显示时触发
 * - popover-hide: 气泡隐藏时触发
 * - search: 搜索通知时触发
 */

// 定义组件派发的事件
const emit = defineEmits<YsNoticeBarEmits>();

/**
 * ==================== 响应式数据定义 ====================
 */

// popoverRef: Popover 组件的引用，用于获取组件实例
const popoverRef = ref<InstanceType<typeof ElPopover>>();

// iconRef: 图标容器的 DOM 引用，用于点击外部检测
const iconRef = ref<HTMLElement>();

// popoverVisible: 控制气泡弹窗的显示与隐藏
const popoverVisible = ref(false);

// centerVisible: 控制通知中心弹窗的显示与隐藏
const centerVisible = ref(false);

// detailVisible: 控制详情对话框的显示与隐藏
const detailVisible = ref(false);

// currentMessage: 当前查看的消息详情
const currentMessage = ref<MessageItem | null>(null);

// hoverTimer: 鼠标悬停定时器，用于延迟显示气泡
let hoverTimer: number | null = null;

// leaveTimer: 鼠标离开定时器，用于延迟隐藏气泡
let leaveTimer: number | null = null;

/**
 * ==================== 计算属性 ====================
 */

/**
 * mergedDisplayConfig - 合并后的显示配置
 * 
 * 功能说明：
 * 将用户传入的 displayConfig 与默认配置进行深度合并，
 * 确保所有配置项都有有效的默认值，同时保留用户的自定义值。
 * 
 * 配置层次结构：
 * 1. icon: 图标相关配置（大小、颜色、徽标等）
 * 2. popover: 气泡弹窗配置（宽度、高度、阴影等）
 * 3. header: 头部配置（字体、颜色、徽章等）
 * 4. list: 列表配置（项高度、背景色、悬停效果等）
 * 5. footer: 底部配置（按钮文字、背景色、图标等）
 * 6. center: 通知中心配置（宽度、高度、全屏等）
 * 7. common: 通用配置（时间格式、文本溢出类型等）
 */
const mergedDisplayConfig = computed<DisplayConfig>(() => ({
  // 图标配置
  icon: {
    size: 20,  // 图标尺寸，单位 px
    color: 'var(--el-text-color-primary)',  // 默认颜色，使用 Element Plus 变量
    hoverColor: 'var(--el-color-primary)',  // 悬停时的颜色
    showBadge: true,  // 是否显示未读消息徽标
    badgePosition: 'top-right',  // 徽标位置：top-right/top-left/bottom-right/bottom-left
    badgeColor: '#f56c6c',  // 徽标背景色（红色）
    badgeSize: 8,  // 徽标小圆点尺寸
    rotate: false,  // 是否显示旋转动画
    blink: false,  // 是否显示闪烁动画
    ...props.displayConfig.icon  // 用户自定义配置覆盖默认值
  },
  // 气泡弹窗配置
  popover: {
    width: 380,  // 宽度，单位 px
    height: 'auto',  // 高度，auto 表示自适应
    maxHeight: 500,  // 最大高度，单位 px
    offset: 0,  // 弹出偏移量
    borderRadius: 8,  // 边框圆角，单位 px
    shadow: true,  // 是否显示阴影
    showMask: false,  // 是否显示遮罩层
    ...props.displayConfig.popover
  },
  // 头部配置
  header: {
    fontSize: 14,  // 字体大小
    textColor: '#606266',  // 文字颜色
    fontWeight: 'normal',  // 字体粗细
    badgeBgColor: '#409eff',  // 徽章背景色
    badgeTextColor: '#ffffff',  // 徽章文字颜色
    badgeBorderRadius: 10,  // 徽章圆角
    badgeSize: 16,  // 徽章尺寸
    wrap: false,  // 是否允许换行
    emptyText: '暂无消息',  // 空状态文字
    ...props.displayConfig.header
  },
  // 列表配置
  list: {
    itemHeight: 'auto',  // 列表项高度
    itemPadding: '12px 16px',  // 列表项内边距
    itemGap: 0,  // 列表项间距
    unreadLineColor: '#409eff',  // 未读消息左侧线条颜色
    unreadBgColor: '#ecf5ff',  // 未读消息背景色
    unreadFontWeight: 'bold',  // 未读消息文字粗细
    readBgColor: 'transparent',  // 已读消息背景色
    readFontWeight: 'normal',  // 已读消息文字粗细
    hoverEffect: true,  // 是否启用悬停效果
    hoverBgColor: '#f5f7fa',  // 悬停背景色
    emptyText: '暂无消息',  // 空状态文字
    showEmptyImage: true,  // 是否显示空状态图片
    ...props.displayConfig.list
  },
  // 底部配置
  footer: {
    width: '100%',  // 宽度
    height: 40,  // 高度
    text: '前往通知中心',  // 按钮文字
    fontSize: 14,  // 字体大小
    textColor: '#ffffff',  // 文字颜色
    bgColor: '#409eff',  // 背景色
    hoverBgColor: '#66b1ff',  // 悬停背景色
    activeBgColor: '#3a8ee6',  // 激活背景色
    borderRadius: 4,  // 圆角
    showIcon: true,  // 是否显示图标
    iconPosition: 'left',  // 图标位置：left/right
    icon: 'ri-arrow-right-line',  // 图标类名（使用 remixicon 图标库）
    ...props.displayConfig.footer
  },
  // 通知中心配置
  center: {
    width: '90%',  // 宽度，支持百分比
    height: '90vh',  // 高度，支持 vh 单位
    fullscreen: false,  // 是否全屏显示
    tabHeight: 50,  // Tab 高度
    tabGap: 20,  // Tab 间距
    tabActiveColor: '#409eff',  // 激活 Tab 颜色
    tabActiveFontWeight: 'bold',  // 激活 Tab 字体粗细
    tableBorder: true,  // 表格是否显示边框
    tableStripe: true,  // 表格是否显示斑马纹
    tableHover: true,  // 表格是否启用悬停高亮
    actionButtonText: '查看',  // 操作按钮文字
    actionButtonType: 'primary',  // 操作按钮类型
    ...props.displayConfig.center
  },
  // 通用配置
  common: {
    timeFormat: 'YYYY-MM-DD HH:mm:ss',  // 时间格式化模板
    textOverflow: 'ellipsis',  // 文本溢出处理方式：ellipsis/truncate/none
    maxLines: 2,  // 最大显示行数
    typeIcons: {},  // 不同类型消息的图标映射
    openAnimation: 'fade',  // 打开动画类型
    closeAnimation: 'fade',  // 关闭动画类型
    animationDuration: 300,  // 动画持续时间，单位 ms
    ...props.displayConfig.common
  },
  ...props.displayConfig  // 用户传入的顶级配置覆盖默认配置
}));

/**
 * mergedInteractionConfig - 合并后的交互配置
 * 
 * 功能说明：
 * 合并用户配置的交互行为与默认配置，提供灵活的交互控制。
 * 
 * 配置层次结构：
 * 1. trigger: 触发方式配置（模式、延迟时间等）
 * 2. item: 单条消息配置（自动标记已读、双击等）
 * 3. auto: 自动滚动配置（是否自动滚动、方向、速度等）
 * 4. center: 通知中心配置（排序、分页等）
 * 5. cache: 缓存配置（读取状态、状态持久化等）
 */
const mergedInteractionConfig = computed<InteractionConfig>(() => ({
  // 触发方式配置
  trigger: {
    mode: 'hover',  // 触发模式：hover/click/both
    hoverDelay: 200,  // 鼠标悬停延迟显示时间，单位 ms
    leaveDelay: 300,  // 鼠标离开延迟隐藏时间，单位 ms
    clickOutsideToClose: true,  // 点击外部是否关闭气泡
    clickToToggle: true,  // 点击是否切换显示状态
    ...props.interactionConfig.trigger
  },
  // 单条消息配置
  item: {
    autoMarkRead: true,  // 点击是否自动标记为已读
    enableDoubleClick: false,  // 是否启用双击功能
    showPreview: false,  // 是否显示消息预览
    ...props.interactionConfig.item
  },
  // 自动滚动配置
  auto: {
    enableAutoScroll: false,  // 是否启用自动滚动
    scrollDirection: 'up',  // 滚动方向：up/down
    scrollSpeed: 2000,  // 滚动速度，单位 ms
    scrollPause: 'hover',  // 暂停方式：hover/none
    scrollLoop: 0,  // 循环次数，0 表示无限循环
    ...props.interactionConfig.auto
  },
  // 通知中心配置
  center: {
    reloadOnTabChange: false,  // 切换 Tab 是否重新加载数据
    enableSort: true,  // 是否启用排序
    sortField: 'sendTime',  // 排序字段
    sortOrder: 'desc',  // 排序方向：asc/desc
    enablePagination: true,  // 是否启用分页
    pageSize: 10,  // 每页显示数量
    pageSizes: [10, 20, 50, 100],  // 可选每页数量
    onCloseCallback: false,  // 关闭时是否回调
    ...props.interactionConfig.center
  },
  // 缓存配置
  cache: {
    cacheReadStatus: true,  // 是否缓存已读状态
    cacheLastViewId: false,  // 是否缓存最后查看的消息 ID
    cachePopoverState: false,  // 是否缓存气泡状态
    ...props.interactionConfig.cache
  },
  ...props.interactionConfig
}));

/**
 * mergedStyleConfig - 合并后的样式配置
 * 
 * 功能说明：
 * 合并用户配置的样式与默认配置，支持主题切换和响应式设计。
 * 
 * 配置项说明：
 * 1. theme: 主题模式：light/dark/custom
 * 2. customTheme: 自定义主题颜色
 * 3. typeStyles: 不同类型消息的样式映射
 * 4. statusStyles: 不同状态消息的样式映射
 * 5. customClass: 自定义 CSS 类名
 * 6. customStyle: 自定义内联样式
 * 7. responsive: 响应式断点配置
 */
const mergedStyleConfig = computed<StyleConfig>(() => ({
  theme: 'light',  // 主题模式
  customTheme: {},  // 自定义主题颜色配置
  typeStyles: {},  // 消息类型样式映射
  statusStyles: {},  // 消息状态样式映射
  customClass: '',  // 自定义 CSS 类名
  customStyle: {},  // 自定义内联样式对象
  responsive: {
    large: 1920,  // 大屏幕断点
    medium: 1366,  // 中屏幕断点
    small: 768,  // 小屏幕断点
    ...props.styleConfig?.responsive
  },
  ...props.styleConfig
}));

/**
 * customClass - 获取自定义 CSS 类名
 * 
 * @returns {string} 自定义类名字符串
 */
const customClass = computed(() => mergedStyleConfig.value.customClass);

/**
 * customStyle - 获取自定义内联样式
 * 
 * @returns {Record<string, string>} 样式对象
 */
const customStyle = computed(() => mergedStyleConfig.value.customStyle);

/**
 * popoverStyle - 计算气泡弹窗的动态样式
 * 
 * 实现思路：
 * 根据配置中的 borderRadius、height、maxHeight 动态生成样式对象，
 * 支持数字和字符串两种格式的单位处理。
 * 
 * @returns {Record<string, string>} 样式对象
 */
const popoverStyle = computed(() => {
  const style: Record<string, string> = {};
  const popover = mergedDisplayConfig.value.popover;
  
  // 处理圆角：支持数字（自动加 px）和字符串（直接使用）
  if (popover?.borderRadius) {
    style.borderRadius = typeof popover.borderRadius === 'number' 
      ? `${popover.borderRadius}px` 
      : popover.borderRadius;
  }
  
  // 处理高度：排除 'auto' 值
  if (popover?.height && popover.height !== 'auto') {
    style.height = typeof popover.height === 'number' 
      ? `${popover.height}px` 
      : popover.height;
  }
  
  // 处理最大高度
  if (popover?.maxHeight) {
    style.maxHeight = typeof popover.maxHeight === 'number' 
      ? `${popover.maxHeight}px` 
      : popover.maxHeight;
  }
  
  return style;
});

/**
 * unreadCount - 计算未读消息总数
 * 
 * 功能说明：
 * 遍历所有消息源，统计 readStatus === 0 的消息数量。
 * 
 * @returns {number} 未读消息总数
 */
const unreadCount = computed(() => {
  let count = 0;
  Object.values(props.dataConfig.messageList).forEach(messages => {
    count += messages.filter(item => item.readStatus === 0).length;
  });
  return count;
});

/**
 * ==================== 事件处理方法 ====================
 */

/**
 * handleMouseEnter - 处理鼠标进入事件
 * 
 * 功能说明：
 * 当触发模式为 hover 或 both 时，设置延迟定时器后显示气泡。
 * 使用延迟可以避免用户快速划过时频繁显示/隐藏。
 * 
 * 实现逻辑：
 * 1. 先清除可能存在的悬停定时器
 * 2. 设置新的定时器，延迟时间为 hoverDelay 配置
 * 3. 定时器触发后设置 popoverVisible 为 true
 */
const handleMouseEnter = () => {
  const triggerMode = mergedInteractionConfig.value.trigger?.mode;
  if (triggerMode === 'hover' || triggerMode === 'both') {
    if (hoverTimer) clearTimeout(hoverTimer);
    hoverTimer = window.setTimeout(() => {
      popoverVisible.value = true;
    }, mergedInteractionConfig.value.trigger?.hoverDelay || 200);
  }
};

/**
 * handleMouseLeave - 处理鼠标离开事件
 * 
 * 功能说明：
 * 当触发模式为 hover 或 both 时，设置延迟定时器后隐藏气泡。
 * 同时清除悬停定时器，避免冲突。
 */
const handleMouseLeave = () => {
  const triggerMode = mergedInteractionConfig.value.trigger?.mode;
  if (triggerMode === 'hover' || triggerMode === 'both') {
    if (hoverTimer) clearTimeout(hoverTimer);
    if (leaveTimer) clearTimeout(leaveTimer);
    leaveTimer = window.setTimeout(() => {
      popoverVisible.value = false;
    }, mergedInteractionConfig.value.trigger?.leaveDelay || 300);
  }
};

/**
 * handleIconClick - 处理图标点击事件
 * 
 * 功能说明：
 * 当触发模式为 click 或 both 时，切换气泡显示状态。
 * 可配置是否允许点击切换（clickToToggle）。
 * 
 * @returns {void}
 */
const handleIconClick = () => {
  const triggerMode = mergedInteractionConfig.value.trigger?.mode;
  if (triggerMode === 'click' || triggerMode === 'both') {
    if (mergedInteractionConfig.value.trigger?.clickToToggle !== false) {
      popoverVisible.value = !popoverVisible.value;
    } else {
      popoverVisible.value = true;
    }
  }
};

/**
 * handlePopoverShow - 处理气泡显示事件
 * 
 * 功能说明：
 * 气泡显示时派发事件，通知父组件。
 */
const handlePopoverShow = () => {
  emit('popover-show');
};

/**
 * handlePopoverHide - 处理气泡隐藏事件
 * 
 * 功能说明：
 * 气泡隐藏时派发事件，通知父组件。
 */
const handlePopoverHide = () => {
  emit('popover-hide');
};

/**
 * handleMessageClick - 处理消息点击事件
 * 
 * 功能说明：
 * 点击消息时，根据配置决定是否自动标记为已读，
 * 然后派发事件并可能关闭气泡。
 * 
 * @param {MessageItem} message - 被点击的消息对象
 */
const handleMessageClick = (message: MessageItem) => {
  // 根据配置自动标记为已读
  if (mergedInteractionConfig.value.item?.autoMarkRead !== false && message.readStatus === 0) {
    emit('mark-read', message);
  }
  // 派发消息点击事件
  emit('message-click', message);
  // 根据配置点击外部关闭气泡
  if (mergedInteractionConfig.value.trigger?.clickOutsideToClose !== false) {
    popoverVisible.value = false;
  }
};

/**
 * handleToCenter - 前往通知中心
 * 
 * 功能说明：
 * 关闭气泡弹窗，打开通知中心弹窗。
 */
const handleToCenter = () => {
  emit('to-center');
  popoverVisible.value = false;
  centerVisible.value = true;
};

/**
 * handleMarkRead - 标记单条消息已读
 * 
 * @param {MessageItem} message - 要标记的消息
 */
const handleMarkRead = (message: MessageItem) => {
  emit('mark-read', message);
};

/**
 * handleMarkAllRead - 标记全部消息已读
 * 
 * 功能说明：
 * 派发事件通知父组件标记所有消息为已读。
 */
const handleMarkAllRead = () => {
  emit('mark-all-read');
};

/**
 * handleCenterClose - 处理通知中心关闭事件
 * 
 * 功能说明：
 * 关闭通知中心弹窗。
 */
const handleCenterClose = () => {
  centerVisible.value = false;
};

/**
 * handleTabChange - 处理 Tab 切换事件
 * 
 * 功能说明：
 * 当用户切换通知中心的 Tab 时触发。
 * 目前保留为空实现，具体逻辑由父组件处理。
 * 
 * @param {'all' | 'unread' | 'read'} tab - 切换到的 Tab
 */
const handleTabChange = (tab: 'all' | 'unread' | 'read') => {
  // 目前为空实现，具体逻辑由父组件通过事件处理
};

/**
 * handleSearch - 处理搜索事件
 * 
 * 功能说明：
 * 当用户在通知中心执行搜索时触发，
 * 派发搜索参数给父组件处理。
 * 
 * @param {Object} payload - 搜索参数对象
 * @param {string} payload.title - 搜索关键词
 * @param {'all' | 'unread' | 'read'} payload.tab - 当前搜索的 Tab
 * @param {string} payload.source - 搜索的消息源
 * @param {boolean} payload.resetPage - 是否重置分页
 */
const handleSearch = (payload: {
  title: string;
  tab: 'all' | 'unread' | 'read';
  source: string;
  resetPage?: boolean;
}) => {
  emit('search', payload);
};

/**
 * handleDetailClose - 处理详情对话框关闭事件
 * 
 * 功能说明：
 * 关闭详情对话框并清空当前查看的消息。
 */
const handleDetailClose = () => {
  detailVisible.value = false;
  currentMessage.value = null;
};

/**
 * handleClickOutside - 处理点击外部区域事件
 * 
 * 功能说明：
 * 检测点击是否发生在气泡和图标外部，
 * 如果是则根据配置关闭气泡。
 * 
 * @param {MouseEvent} event - 点击事件对象
 */
const handleClickOutside = (event: MouseEvent) => {
  if (mergedInteractionConfig.value.trigger?.clickOutsideToClose !== false) {
    const popoverEl = document.querySelector('.ys-notice-popover');
    const iconEl = iconRef.value;
    
    // 检查点击元素是否在气泡或图标内部
    if (popoverEl && !popoverEl.contains(event.target as Node) && 
        iconEl && !iconEl.contains(event.target as Node)) {
      popoverVisible.value = false;
    }
  }
};

/**
 * ==================== 生命周期钩子 ====================
 */

/**
 * onMounted - 组件挂载完成
 * 
 * 功能说明：
 * 注册全局点击事件监听器，用于检测点击外部区域。
 */
onMounted(() => {
  if (mergedInteractionConfig.value.trigger?.clickOutsideToClose !== false) {
    document.addEventListener('click', handleClickOutside);
  }
});

/**
 * onUnmounted - 组件卸载前
 * 
 * 功能说明：
 * 清除所有定时器并移除事件监听器，防止内存泄漏。
 */
onUnmounted(() => {
  if (hoverTimer) clearTimeout(hoverTimer);
  if (leaveTimer) clearTimeout(leaveTimer);
  document.removeEventListener('click', handleClickOutside);
});

/**
 * ==================== 暴露方法 ====================
 */

/**
 * defineExpose - 向父组件暴露的方法
 * 
 * 暴露的方法允许父组件直接控制组件行为：
 * 1. showPopover/hidePopover/togglePopover: 控制气泡显示
 * 2. showCenter/hideCenter: 控制通知中心
 * 3. showDetail/hideDetail: 控制详情对话框
 */
defineExpose({
  popoverRef,
  iconRef,
  /**
   * showPopover - 显示气泡弹窗
   */
  showPopover: () => {
    popoverVisible.value = true;
  },
  /**
   * hidePopover - 隐藏气泡弹窗
   */
  hidePopover: () => {
    popoverVisible.value = false;
  },
  /**
   * togglePopover - 切换气泡弹窗显示状态
   */
  togglePopover: () => {
    popoverVisible.value = !popoverVisible.value;
  },
  /**
   * showCenter - 显示通知中心
   */
  showCenter: () => {
    centerVisible.value = true;
  },
  /**
   * hideCenter - 隐藏通知中心
   */
  hideCenter: () => {
    centerVisible.value = false;
  },
  /**
   * showDetail - 显示消息详情
   * 
   * @param {MessageItem} message - 要显示的消息
   */
  showDetail: (message: MessageItem) => {
    currentMessage.value = message;
    detailVisible.value = true;
  },
  /**
   * hideDetail - 隐藏消息详情
   */
  hideDetail: () => {
    detailVisible.value = false;
    currentMessage.value = null;
  }
});
</script>

<style scoped lang="scss">
/**
 * ==================== 样式定义 ====================
 */

/**
 * ys-notice-bar - 通知栏容器样式
 * 
 * 功能说明：
 * 使用 inline-block 使组件宽度由内容决定，
 * 相对定位用于内部绝对定位元素的定位参考。
 */
.ys-notice-bar {
  display: inline-block;
  position: relative;
}

/**
 * notice-icon-wrapper - 通知图标容器
 * 
 * 功能说明：
 * 包含通知图标的容器，支持居中对齐和鼠标交互。
 */
.notice-icon-wrapper {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    color: v-bind('mergedDisplayConfig.icon?.hoverColor');
  }
}

/**
 * notice-icon - 通知图标基础样式
 */
.notice-icon {
  transition: all 0.3s;
}

/**
 * rotate-animation - 旋转动画
 * 
 * 动画说明：
 * 从 0 度到 360 度无限旋转，用于提醒用户有新消息。
 */
.rotate-animation {
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/**
 * blink-animation - 闪烁动画
 * 
 * 动画说明：
 * 透明度在 1 和 0.5 之间循环变化，用于吸引用户注意。
 */
.blink-animation {
  animation: blink 1.5s ease-in-out infinite;
}

@keyframes blink {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

/**
 * 气泡弹窗样式覆盖
 * 
 * 功能说明：
 * 使用 :deep() 穿透 scoped 样式，
 * 自定义气泡弹窗的内边距和圆角。
 */
:deep(.ys-notice-popover) {
  padding: 0 !important;
  border-radius: v-bind('mergedDisplayConfig.popover?.borderRadius + "px"') !important;
  
  &.has-shadow {
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }
}

/**
 * 徽标位置样式
 * 
 * 支持四个位置的徽标定位：
 * top-right: 右上角（默认）
 * top-left: 左上角
 * bottom-right: 右下角
 * bottom-left: 左下角
 */
.badge-top-right :deep(.el-badge__content) {
  top: -2px;
  right: -2px;
}

.badge-top-left :deep(.el-badge__content) {
  top: -2px;
  right: auto;
  left: -2px;
}

.badge-bottom-right :deep(.el-badge__content) {
  top: auto;
  bottom: -2px;
  right: -2px;
}

.badge-bottom-left :deep(.el-badge__content) {
  top: auto;
  bottom: -2px;
  right: auto;
  left: -2px;
}
</style>
