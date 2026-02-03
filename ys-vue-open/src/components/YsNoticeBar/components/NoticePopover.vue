<template>
  <!--
    NoticePopover 通知气泡组件
    功能说明：
    这是 YsNoticeBar 的子组件，负责展示通知消息的预览列表。
    包含头部（消息源 Tab）、主体（消息列表）、底部（操作按钮）三个区域。
    组件支持消息源切换、消息筛选、悬停效果等交互功能。
  -->
  <div class="notice-popover" :class="themeClass">
    <!-- 头部区域：消息源 Tab 切换 -->
    <div class="notice-popover-header">
      <div
        class="source-tabs"
        :class="{ 'wrap': displayConfig.header?.wrap }"
        ref="sourceTabsRef"
        @wheel.prevent="handleWheelScroll"
      >
        <div
          v-for="source in sourceTabs"
          :key="source.type"
          class="source-tab"
          :class="{ 'active': activeSource === source.type }"
          :style="activeSource === source.type ? activeTabStyle : {}"
          @click="handleSourceChange(source.type)"
        >
          <!-- 消息源图标 -->
          <i v-if="source.icon" :class="source.icon" class="source-icon"></i>
          <!-- 消息源标签 -->
          <span class="source-label">{{ source.label }}</span>
          <!-- 未读消息数量徽章 -->
          <el-badge
            v-if="source.count > 0"
            :value="source.count"
            :max="99"
            class="source-badge"
          >
          </el-badge>
        </div>
      </div>
      <!-- 空状态：没有任何消息源时显示 -->
      <div v-if="sourceTabs.length === 0" class="empty-header">
        {{ displayConfig.header?.emptyText || '暂无消息' }}
      </div>
    </div>

    <!-- 主体区域：消息列表 -->
    <div class="notice-popover-body">
      <!-- 有消息时显示消息列表 -->
      <div
        v-if="filteredMessageList.length > 0"
        class="message-list"
        :style="listStyle"
      >
        <div
          v-for="message in filteredMessageList"
          :key="message.id"
          class="message-item"
          :class="{
            'unread': message.readStatus === 0,
            'read': message.readStatus === 1
          }"
          :style="getItemStyle(message)"
          @click="handleMessageClick(message)"
          @dblclick="handleMessageDoubleClick(message)"
          @mouseenter="handleMessageHover(message)"
        >
          <!-- 未读消息左侧指示点 -->
          <div v-if="message.readStatus === 0" class="unread-dot"></div>
          <!-- 消息内容区域 -->
          <div class="message-content">
            <!-- 消息头部：类型图标和时间 -->
            <div class="message-header">
              <div class="message-type-wrapper">
                <!-- 消息类型图标 -->
                <i v-if="getTypeIcon(message.type)" :class="getTypeIcon(message.type)" class="message-type-icon"></i>
              </div>
              <!-- 消息发送时间 -->
              <span class="message-time">{{ formatTime(message.sendTime) }}</span>
            </div>
            <!-- 消息标题 -->
            <div class="message-title" :class="`text-overflow-${displayConfig.common?.textOverflow}`">
              {{ message.title }}
            </div>
            <!-- 消息内容预览（可选） -->
            <div
              v-if="interactionConfig.item?.showPreview && displayConfig.common?.textOverflow === 'ellipsis'"
              class="message-preview"
            >
              {{ message.content }}
            </div>
          </div>
        </div>
      </div>
      
      <!-- 空状态：没有消息时显示 -->
      <div v-else class="empty-list">
        <!-- 空状态图片（可选） -->
        <el-empty
          v-if="displayConfig.list?.showEmptyImage !== false"
          :image-size="150"
          :description="displayConfig.list?.emptyText || '暂无消息'"
        >
        </el-empty>
        <!-- 空状态文字 -->
        <div v-else class="empty-text">
          {{ displayConfig.list?.emptyText || '暂无消息' }}
        </div>
        <!-- 刷新按钮（可选） -->
        <el-button
          v-if="dataConfig && dataConfig.showRefreshBtn"
          type="primary"
          link
          @click="handleRefresh"
        >
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 底部区域：前往通知中心按钮 -->
    <div class="notice-popover-footer">
      <el-button
        type="primary"
        :style="footerButtonStyle"
        @click="handleToCenter"
      >
        <!-- 按钮图标 -->
        <i v-if="displayConfig.footer?.showIcon !== false" :class="displayConfig.footer?.icon || 'ri-arrow-right-line'"></i>
        <!-- 按钮文字 -->
        <span v-if="displayConfig.footer?.iconPosition !== 'right'" class="button-text">
          {{ displayConfig.footer?.text || '前往通知中心' }}
        </span>
        <span v-else class="button-text">
          {{ displayConfig.footer?.text || '前往通知中心' }}
        </span>
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts" name="NoticePopover">
/**
 * NoticePopover 通知气泡预览组件
 * 
 * 组件功能描述：
 * NoticePopover 是 YsNoticeBar 通知栏组件的核心子组件之一，
 * 负责在气泡弹窗中展示通知消息的预览列表。
 * 
 * 核心功能：
 * 1. 消息源 Tab 切换：支持按消息类型/来源筛选显示
 * 2. 消息列表展示：以列表形式展示消息，支持未读/已读状态区分
 * 3. 未读指示：未读消息左侧显示蓝色指示点
 * 4. 悬停效果：支持鼠标悬停高亮和交互反馈
 * 5. 时间格式化：自动将时间戳转换为相对时间显示
 * 6. 前往通知中心：提供跳转入口查看完整消息列表
 * 
 * 使用场景：
 * - 鼠标悬停在通知图标上时显示
 * - 点击通知图标时显示
 * - 作为下拉面板展示最近通知
 * 
 * 与父组件的通信：
 * - 通过 props 接收消息列表和配置
 * - 通过 emit 派发用户交互事件
 */

import { computed, ref, watch } from 'vue';
import { Bell } from '@element-plus/icons-vue';
import { formatPast } from '@/utils/formatTime';
import type { MessageItem, DisplayConfig, InteractionConfig, StyleConfig, DataConfig, MessageSourceConfig } from '@/types/notice';

// 定义组件名称
defineOptions({
  name: 'NoticePopover'
});

/**
 * ==================== Props 定义 ====================
 */

/**
 * Props 接口说明
 * 
 * MessageItem: 消息数据结构
 * - id: 消息唯一标识
 * - title: 消息标题
 * - content: 消息内容
 * - type: 消息类型
 * - sendTime: 发送时间
 * - sendUser: 发送人
 * - readStatus: 阅读状态（0:未读 1:已读）
 */

interface Props {
  /** messageList: 消息列表，以消息源类型为键的消息对象数组 */
  messageList: Record<string, MessageItem[]>;
  /** displayConfig: 显示配置，控制视觉样式 */
  displayConfig: DisplayConfig;
  /** interactionConfig: 交互配置，控制用户行为 */
  interactionConfig: InteractionConfig;
  /** styleConfig: 样式配置，控制主题和样式 */
  styleConfig: StyleConfig;
  /** dataConfig: 数据配置，包含额外的数据源信息 */
  dataConfig: DataConfig;
  /** sourceConfig: 消息源配置，定义消息来源信息（可选） */
  sourceConfig?: MessageSourceConfig[];
}

// 定义组件 props
const props = defineProps<Props>();

/**
 * ==================== Emits 定义 ====================
 */

/**
 * Emits 接口说明
 * 
 * 定义组件向父组件派发的事件：
 * - message-click: 点击消息时触发
 * - to-center: 点击前往通知中心时触发
 * - mark-read: 标记消息已读时触发
 * - mark-all-read: 标记全部已读时触发
 */
const emit = defineEmits<{
  (e: 'message-click', message: MessageItem): void;
  (e: 'to-center'): void;
  (e: 'mark-read', message: MessageItem): void;
  (e: 'mark-all-read'): void;
}>();

/**
 * ==================== 响应式数据 ====================
 */

// activeSource: 当前选中的消息源类型，初始为空字符串表示显示全部
const activeSource = ref<string>('');

// sourceTabsRef: 消息源 Tab 区域的 DOM 引用，用于处理横向滚动
const sourceTabsRef = ref<HTMLElement>();

/**
 * ==================== 计算属性 ====================
 */

/**
 * sourceTabs - 计算消息源 Tab 列表
 * 
 * 功能说明：
 * 根据 sourceConfig 或 messageList 的键生成消息源 Tab 列表。
 * 如果配置了 sourceConfig，使用配置的来源信息；
 * 否则自动从 messageList 中提取来源类型。
 * 
 * Tab 数据结构：
 * - type: 来源类型（用于筛选和标识）
 * - label: 显示标签
 * - icon: 图标类名（可选）
 * - count: 该来源的未读消息数量
 * 
 * @returns {Array<{type, label, icon?, count}>} 消息源 Tab 列表
 */
const sourceTabs = computed(() => {
  const tabs: Array<{ type: string; label: string; icon?: string; count: number }> = [];
  
  const sourceConfig = props.sourceConfig || [];
  
  // 如果配置了消息源配置，使用配置的信息
  if (sourceConfig.length > 0) {
    sourceConfig.forEach(source => {
      const messages = props.messageList[source.type] || [];
      const unreadMessages = messages.filter(m => m.readStatus === 0);
      tabs.push({
        type: source.type,
        label: source.label,
        icon: source.icon,
        count: unreadMessages.length
      });
    });
  } else {
    // 否则从消息列表自动提取来源类型
    const sourceTypes = Object.keys(props.messageList);
    sourceTypes.forEach(type => {
      const messages = props.messageList[type] || [];
      const unreadMessages = messages.filter(m => m.readStatus === 0);
      tabs.push({
        type,
        label: getTypeLabel(type),
        count: unreadMessages.length
      });
    });
  }
  
  return tabs;
});

/**
 * themeClass - 计算主题类名
 * 
 * 功能说明：
 * 根据样式配置中的 theme 生成对应的主题类名。
 * 
 * @returns {string} 主题类名字符串
 */
const themeClass = computed(() => {
  return props.styleConfig?.theme ? `theme-${props.styleConfig.theme}` : '';
});

/**
 * filteredMessageList - 计算过滤后的消息列表
 * 
 * 功能说明：
 * 根据当前选中的消息源类型过滤消息列表。
 * 如果没有选中任何源（activeSource 为空），则返回所有消息。
 * 
 * @returns {MessageItem[]} 过滤后的消息数组
 */
const filteredMessageList = computed(() => {
  // 如果选中了特定消息源，只返回该源的消息
  if (activeSource.value) {
    return props.messageList[activeSource.value] || [];
  }
  
  // 否则合并所有消息源的消息
  const allMessages: MessageItem[] = [];
  Object.values(props.messageList).forEach(messages => {
    allMessages.push(...messages);
  });
  
  // 按发送时间降序排序
  return allMessages.sort((a, b) => {
    return new Date(b.sendTime).getTime() - new Date(a.sendTime).getTime();
  });
});

/**
 * activeTabStyle - 计算激活 Tab 的样式
 * 
 * 功能说明：
 * 生成当前激活 Tab 的内联样式，
 * 包括文字颜色和字体粗细。
 * 
 * @returns {Record<string, string>} 样式对象
 */
const activeTabStyle = computed(() => {
  return {
    color: props.displayConfig.header?.badgeBgColor || '#409eff',
    fontWeight: props.displayConfig.header?.fontWeight || 'bold'
  };
});

/**
 * listStyle - 计算列表样式
 * 
 * 功能说明：
 * 根据配置生成消息列表的样式对象，
 * 主要用于控制列表项的内边距。
 * 
 * @returns {Record<string, string>} 样式对象
 */
const listStyle = computed(() => {
  const style: Record<string, string> = {};
  const list = props.displayConfig.list;
  
  if (list?.itemPadding) {
    style.padding = list.itemPadding;
  }
  
  return style;
});

/**
 * footerButtonStyle - 计算底部按钮样式
 * 
 * 功能说明：
 * 根据配置生成底部按钮的内联样式，
 * 包括宽度、高度、背景色、圆角等。
 * 
 * @returns {Record<string, string>} 样式对象
 */
const footerButtonStyle = computed(() => {
  const footer = props.displayConfig.footer;
  const style: Record<string, string> = {};
  
  if (footer?.width) {
    style.width = typeof footer.width === 'number' ? `${footer.width}px` : footer.width;
  }
  
  if (footer?.height) {
    style.height = `${footer.height}px`;
  }
  
  if (footer?.bgColor) {
    style.backgroundColor = footer.bgColor;
  }
  
  if (footer?.borderRadius) {
    style.borderRadius = typeof footer.borderRadius === 'number' 
      ? `${footer.borderRadius}px` 
      : footer.borderRadius;
  }
  
  if (footer?.textColor) {
    style.color = footer.textColor;
  }
  
  return style;
});

/**
 * ==================== 方法定义 ====================
 */

/**
 * handleSourceChange - 切换消息源
 * 
 * 功能说明：
 * 切换当前选中的消息源类型。
 * 如果点击的是已选中的源，则取消选中（显示全部）。
 * 
 * @param {string} type - 消息源类型
 */
const handleSourceChange = (type: string) => {
  // 如果点击的是当前已选中的源，则取消选中（显示全部）
  if (activeSource.value === type) {
    activeSource.value = '';
  } else {
    activeSource.value = type;
  }
};

/**
 * handleMessageClick - 处理消息点击
 * 
 * 功能说明：
 * 点击消息时触发，标记未读消息为已读，
 * 并派发消息点击事件。
 * 
 * @param {MessageItem} message - 被点击的消息
 */
const handleMessageClick = (message: MessageItem) => {
  // 标记未读消息为已读
  if (message.readStatus === 0) {
    emit('mark-read', message);
  }
  // 派发消息点击事件
  emit('message-click', message);
};

/**
 * handleMessageDoubleClick - 处理消息双击
 * 
 * 功能说明：
 * 双击消息时触发标记已读。
 * 该功能需要 interactionConfig.item.enableDoubleClick 为 true 才启用。
 * 
 * @param {MessageItem} message - 双击的消息
 */
const handleMessageDoubleClick = (message: MessageItem) => {
  if (props.interactionConfig.item?.enableDoubleClick) {
    if (message.readStatus === 0) {
      emit('mark-read', message);
    }
  }
};

/**
 * handleMessageHover - 处理消息悬停
 * 
 * 功能说明：
 * 鼠标悬停在消息上时触发，
 * 可用于实现悬停效果或记录日志。
 * 
 * @param {MessageItem} message - 悬停的消息
 */
const handleMessageHover = (message: MessageItem) => {
  // 目前为空实现，可用于扩展悬停效果
};

/**
 * handleWheelScroll - 处理横向滚动
 * 
 * 功能说明：
 * 拦截鼠标滚轮事件，实现 Tab 区域的横向滚动。
 * 使用 shift + 滚轮实现横向滚动。
 * 
 * @param {WheelEvent} event - 滚轮事件
 */
const handleWheelScroll = (event: WheelEvent) => {
  const element = sourceTabsRef.value;
  if (element) {
    // 水平滚动
    element.scrollLeft += event.deltaY || event.deltaX;
  }
};

/**
 * handleToCenter - 前往通知中心
 * 
 * 功能说明：
 * 点击底部按钮时触发，关闭气泡弹窗，
 * 并通知父组件打开通知中心。
 */
const handleToCenter = () => {
  emit('to-center');
};

/**
 * handleRefresh - 刷新数据
 * 
 * 功能说明：
 * 点击刷新按钮时触发，
 * 用于手动刷新消息列表数据。
 */
const handleRefresh = () => {
  // 目前为空实现，具体逻辑由父组件处理
};

/**
 * getTypeLabel - 获取消息类型标签
 * 
 * 功能说明：
 * 根据消息类型返回对应的显示标签。
 * 支持配置自定义类型标签映射。
 * 
 * @param {string} type - 消息类型
 * @returns {string} 显示标签
 */
const getTypeLabel = (type: string): string => {
  const typeLabels: Record<string, string> = {
    'all': '全部',
    'unread': '未读',
    'read': '已读',
    'system': '系统通知',
    'announcement': '公告',
    'task': '任务',
    'message': '消息',
    'approval': '审批',
    'warning': '警告'
  };
  
  return typeLabels[type] || type;
};

/**
 * getTypeIcon - 获取消息类型图标
 * 
 * 功能说明：
 * 根据消息类型返回对应的图标类名。
 * 优先使用配置中的类型图标映射。
 * 
 * @param {string} type - 消息类型
 * @returns {string | undefined} 图标类名
 */
const getTypeIcon = (type: string): string | undefined => {
  // 优先使用配置中的图标
  const typeIcons = props.displayConfig.common?.typeIcons || {};
  if (typeIcons[type]) {
    return typeIcons[type];
  }
  
  // 默认图标映射
  const defaultIcons: Record<string, string> = {
    'system': 'ri-notification-3-line',
    'announcement': 'ri-megaphone-line',
    'task': 'ri-task-line',
    'message': 'ri-message-3-line',
    'approval': 'ri-checkbox-circle-line',
    'warning': 'ri-error-warning-line'
  };
  
  return defaultIcons[type];
};

/**
 * getTypeTextStyle - 获取消息类型文字样式
 * 
 * 功能说明：
 * 根据消息类型返回对应的文字样式对象，
 * 用于自定义不同类型消息的显示样式。
 * 
 * @param {string} type - 消息类型
 * @returns {Record<string, string>} 样式对象
 */
const getTypeTextStyle = (type: string): Record<string, string> => {
  const typeStyles = props.styleConfig?.typeStyles || {};
  return typeStyles[type] || {};
};

/**
 * getItemStyle - 获取消息项样式
 * 
 * 功能说明：
 * 根据消息状态（已读/未读）返回对应的样式对象，
 * 包括背景色和字体粗细。
 * 
 * @param {MessageItem} message - 消息对象
 * @returns {Record<string, string>} 样式对象
 */
const getItemStyle = (message: MessageItem): Record<string, string> => {
  const style: Record<string, string> = {};
  const list = props.displayConfig.list;
  
  // 根据已读状态设置背景色
  if (message.readStatus === 1) {
    if (list?.readBgColor && list.readBgColor !== 'transparent') {
      style.backgroundColor = list.readBgColor;
    }
  } else {
    if (list?.unreadBgColor) {
      style.backgroundColor = list.unreadBgColor;
    }
  }
  
  return style;
};

/**
 * formatTime - 格式化时间显示
 * 
 * 功能说明：
 * 将时间戳或日期字符串转换为相对时间显示，
 * 如"刚刚"、"5分钟前"、"2小时前"等。
 * 
 * @param {string | Date | number} time - 时间值
 * @returns {string} 格式化后的时间字符串
 */
const formatTime = (time: string | Date | number): string => {
  return formatPast(new Date(time));
};

/**
 * watch - 监听消息源配置变化
 * 
 * 功能说明：
 * 当 sourceConfig 变化时，重置当前选中的消息源。
 * 确保消息源配置更新时 UI 同步更新。
 */
watch(
  () => props.sourceConfig,
  () => {
    // 重置当前选中的消息源
    activeSource.value = '';
  }
);
</script>

<style scoped lang="scss">
/**
 * ==================== 样式定义 ====================
 */

/**
 * notice-popover - 气泡容器基础样式
 * 
 * 功能说明：
 * 设置气泡组件的最小宽度、圆角和文字颜色。
 */
.notice-popover {
  min-width: 300px;
  border-radius: 8px;
  color: #606266;
}

/**
 * notice-popover-header - 头部区域样式
 * 
 * 功能说明：
 * 包含消息源 Tab 切换区域，使用 Flex 布局居中对齐。
 */
.notice-popover-header {
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
}

/**
 * source-tabs - 消息源 Tab 列表
 * 
 * 功能说明：
 * 使用 Flex 布局水平排列 Tab，
 * 支持横向滚动（当 Tab 过多时）。
 */
.source-tabs {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  overflow-y: hidden;
  
  // 允许换行（可选配置）
  &.wrap {
    flex-wrap: wrap;
  }
  
  // 隐藏滚动条
  &::-webkit-scrollbar {
    display: none;
  }
}

/**
 * source-tab - 单个 Tab 样式
 * 
 * 功能说明：
 * Tab 项的基础样式，包含标签和徽章。
 * 支持激活状态的样式切换。
 */
.source-tab {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
  
  &:hover {
    background-color: #f5f7fa;
  }
  
  // 激活状态样式由内联样式控制
  &.active {
    background-color: #ecf5ff;
  }
}

/**
 * source-icon - 消息源图标样式
 */
.source-icon {
  font-size: 14px;
  color: inherit;
}

/**
 * source-label - 消息源标签文字样式
 */
.source-label {
  font-size: 13px;
}

/**
 * source-badge - 消息源徽章样式
 */
.source-badge {
  :deep(.el-badge__content) {
    border-radius: 10px;
    height: 16px;
    line-height: 16px;
    font-size: 12px;
  }
}

/**
 * empty-header - 空头部样式
 */
.empty-header {
  text-align: center;
  color: #909399;
  font-size: 13px;
  padding: 8px 0;
}

/**
 * notice-popover-body - 主体区域样式
 * 
 * 功能说明：
 * 消息列表容器，支持最大高度限制和垂直滚动。
 */
.notice-popover-body {
  max-height: 350px;
  overflow-y: auto;
  
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-thumb {
    background-color: #c0c4cc;
    border-radius: 2px;
  }
}

/**
 * message-list - 消息列表样式
 */
.message-list {
  padding: 0;
}

/**
 * message-item - 单条消息样式
 * 
 * 功能说明：
 * 消息项的基础样式，支持未读/已读状态区分。
 */
.message-item {
  position: relative;
  display: flex;
  align-items: flex-start;
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
  
  &:hover {
    background-color: #f5f7fa;
  }
  
  // 未读消息样式
  &.unread {
    font-weight: bold;
  }
  
  // 已读消息样式
  &.read {
    opacity: 0.8;
  }
}

/**
 * unread-dot - 未读消息指示点
 * 
 * 功能说明：
 * 未读消息左侧的蓝色小圆点，
 * 用于视觉上区分未读和已读消息。
 */
.unread-dot {
  position: absolute;
  left: 6px;
  top: 50%;
  transform: translateY(-50%);
  width: 6px;
  height: 6px;
  background-color: #409eff;
  border-radius: 50%;
}

/**
 * message-content - 消息内容区域
 */
.message-content {
  flex: 1;
  margin-left: 12px;
  min-width: 0;
}

/**
 * message-header - 消息头部（类型+时间）
 */
.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

/**
 * message-type-wrapper - 消息类型图标容器
 */
.message-type-wrapper {
  display: flex;
  align-items: center;
}

/**
 * message-type-icon - 消息类型图标
 */
.message-type-icon {
  font-size: 14px;
  color: #909399;
}

/**
 * message-time - 消息时间样式
 */
.message-time {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

/**
 * message-title - 消息标题样式
 * 
 * 溢出处理：
 * 根据配置使用不同的文本溢出处理方式。
 */
.message-title {
  font-size: 14px;
  line-height: 1.5;
  color: #303133;
  margin-bottom: 4px;
  
  &.text-overflow-ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }
  
  &.text-overflow-truncate {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

/**
 * message-preview - 消息内容预览样式
 */
.message-preview {
  font-size: 12px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-top: 4px;
}

/**
 * empty-list - 空列表样式
 */
.empty-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 16px;
  gap: 12px;
}

/**
 * empty-text - 空状态文字样式
 */
.empty-text {
  color: #909399;
  font-size: 14px;
}

/**
 * notice-popover-footer - 底部区域样式
 * 
 * 功能说明：
 * 包含前往通知中心按钮，使用 Flex 布局居中对齐。
 */
.notice-popover-footer {
  padding: 12px 16px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: center;
  
  // 按钮样式覆盖
  .el-button {
    margin: 0;
  }
}

/**
 * button-text - 按钮文字样式
 */
.button-text {
  margin-left: 6px;
  font-size: 13px;
}

/**
 * 主题样式
 * 
 * 深色主题下的样式覆盖。
 */
.theme-dark {
  .notice-popover {
    background-color: #1f2d3d;
    color: #fff;
  }
  
  .source-tabs {
    &.wrap {
      flex-wrap: wrap;
    }
  }
  
  .source-tab {
    &:hover {
      background-color: #304156;
    }
    
    &.active {
      background-color: #409eff;
      color: #fff;
    }
  }
  
  .message-item {
    &:hover {
      background-color: #304156;
    }
  }
  
  .message-title {
    color: #fff;
  }
  
  .message-time,
  .message-type-icon {
    color: #8094ae;
  }
  
  .empty-text {
    color: #8094ae;
  }
}
</style>
