<template>
  <YsDialog
    v-model="dialogVisible"
    :title="message?.title || '消息详情'"
    :width="width"
    :height="height"
    @close="handleClose"
  >
    <div v-if="message" class="notice-detail" :class="themeClass">
      <div class="detail-header">
        <div class="detail-info">
          <div class="info-item">
            <span class="info-label">消息类型：</span>
            <el-tag
              :type="getTypeTagType(message.type) as any"
              size="small"
            >
              {{ getTypeLabel(message.type) }}
            </el-tag>
          </div>
          <div class="info-item">
            <span class="info-label">发布人：</span>
            <span class="info-value">{{ message.sendUser }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">发布时间：</span>
            <span class="info-value">{{ formatTime(message.sendTime) }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">阅读状态：</span>
            <el-tag
              :type="message.readStatus === 0 ? 'danger' : 'success'"
              size="small"
            >
              {{ message.readStatus === 0 ? '未读' : '已读' }}
            </el-tag>
          </div>
        </div>
      </div>

      <div class="detail-content">
        <div class="content-label">消息内容：</div>
        <div class="content-text" v-html="message.content"></div>
      </div>

      <div v-if="hasExtraFields" class="detail-extra">
        <div class="extra-label">附加信息：</div>
        <div class="extra-fields">
          <div
            v-for="(value, key) in extraFields"
            :key="key"
            class="extra-item"
          >
            <span class="extra-key">{{ key }}：</span>
            <span class="extra-value">{{ value }}</span>
          </div>
        </div>
      </div>

      <div v-if="showActions" class="detail-actions">
        <el-button
          v-if="message.readStatus === 0 && !autoMarkRead"
          type="primary"
          @click="handleMarkRead"
        >
          标记为已读
        </el-button>
        <el-button
          v-if="allowDelete"
          type="danger"
          @click="handleDelete"
        >
          删除消息
        </el-button>
      </div>
    </div>

    <div v-else class="detail-empty">
      <el-empty description="暂无消息详情"></el-empty>
    </div>

    <template #footer>
      <el-button @click="handleClose">
        关闭
      </el-button>
    </template>
  </YsDialog>
</template>

<script setup lang="ts" name="NoticeDetailDialog">
/**
 * NoticeDetailDialog 消息详情对话框组件
 * 
 * 组件功能描述：
 * NoticeDetailDialog 是 YsNoticeBar 通知栏组件的子组件之一，
 * 负责在弹窗中展示单条通知消息的完整详情信息。
 * 
 * 核心功能：
 * 1. 消息详情展示：显示消息的完整内容，包括标题、类型、发布人、发布时间等
 * 2. 消息类型标识：根据消息类型显示对应的标签颜色
 * 3. 阅读状态显示：区分展示未读和已读状态
 * 4. 自动标记已读：弹窗打开时自动将未读消息标记为已读（可选）
 * 5. 消息删除：支持删除消息（需配置 allowDelete）
 * 6. 附加字段展示：自动显示消息中的附加字段信息
 * 7. 主题支持：支持亮色/暗色/自定义三种主题模式
 * 
 * 使用场景：
 * - 点击消息列表中的某条消息时显示
 * - 用户需要查看消息的完整内容时
 * - 需要对消息进行标记已读或删除操作时
 * 
 * 与父组件的通信：
 * - 通过 props 接收消息数据和配置参数
 * - 通过 emit 派发用户交互事件（关闭、标记已读、删除等）
 * - 通过 v-model:visible 控制弹窗的显示与隐藏
 * 
 * 依赖组件：
 * - YsDialog: 弹窗容器组件
 * - el-tag: 标签组件，用于显示消息类型和阅读状态
 * - el-button: 按钮组件，用于操作按钮
 * - el-empty: 空状态组件，当无消息时显示
 */

import { computed, watch } from 'vue';
import { ElTag, ElButton, ElEmpty } from 'element-plus';
import { formatDate } from '@/utils/formatTime';
import type { MessageItem, StyleConfig } from '@/types/notice';

defineOptions({
  name: 'NoticeDetailDialog'
});

/**
 * ==================== Props 定义 ====================
 */

/**
 * Props 接口说明
 * 
 * NoticeDetailDialog 组件接收以下属性：
 * 1. visible: 控制弹窗显示与隐藏
 * 2. message: 要展示的消息数据
 * 3. width/height: 弹窗尺寸配置
 * 4. autoMarkRead: 是否自动标记已读
 * 5. allowDelete: 是否允许删除消息
 * 6. showActions: 是否显示操作按钮区域
 * 7. styleConfig: 样式配置，控制主题
 */

/**
 * visible: 弹窗显示控制
 * 
 * 使用方式：通过 v-model:visible 绑定实现双向控制
 * 触发时机：弹窗打开或关闭时
 */
interface Props {
  /** visible: 控制弹窗显示与隐藏的双向绑定变量 */
  visible: boolean;
  /** message: 要展示的消息详情数据，为 null 时显示空状态 */
  message?: MessageItem | null;
  /** width: 弹窗宽度，支持百分比或像素值，默认 '60%' */
  width?: number | string;
  /** height: 弹窗高度，支持百分比或像素值，默认 'auto' */
  height?: number | string;
  /** autoMarkRead: 打开弹窗时是否自动将未读消息标记为已读，默认 true */
  autoMarkRead?: boolean;
  /** allowDelete: 是否显示删除按钮，默认 false */
  allowDelete?: boolean;
  /** showActions: 是否显示操作按钮区域（标记已读、删除），默认 true */
  showActions?: boolean;
  /** styleConfig: 样式配置，控制主题颜色等 */
  styleConfig?: StyleConfig;
}

// 定义组件 props，使用 withDefaults 提供默认值
const props = withDefaults(defineProps<Props>(), {
  width: '60%',
  height: 'auto',
  autoMarkRead: true,
  allowDelete: false,
  showActions: true,
  styleConfig: () => ({})
});

/**
 * ==================== Emits 定义 ====================
 */

/**
 * Emits 接口说明
 * 
 * NoticeDetailDialog 组件向父组件派发以下事件：
 * - update:visible: 更新弹窗显示状态
 * - mark-read: 标记消息已读时触发
 * - delete: 删除消息时触发
 * - close: 关闭弹窗时触发
 */
const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
  (e: 'mark-read', message: MessageItem): void;
  (e: 'delete', message: MessageItem): void;
  (e: 'close'): void;
}>();

/**
 * ==================== 计算属性定义 ====================
 */

/**
 * dialogVisible: 弹窗显示状态的双向计算属性
 * 
 * 实现逻辑：
 * - get: 返回 props.visible 作为显示状态
 * - set: 触发 update:visible 事件更新父组件状态
 * 
 * 用途：实现 v-model:visible 的双向绑定
 */
const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
});

/**
 * themeClass: 根据配置的主题返回对应的样式类名
 * 
 * 支持的主题：
 * - light: 亮色主题（默认）
 * - dark: 暗色主题
 * - custom: 自定义主题
 * 
 * 样式类名用于模板中的条件样式绑定
 */
const themeClass = computed(() => {
  return `theme-${props.styleConfig?.theme || 'light'}`;
});

/**
 * customTheme: 自定义主题的颜色配置
 * 
 * 配置项：
 * - bgColor: 背景颜色
 * - textColor: 文字颜色
 * - borderColor: 边框颜色
 * 
 * 用于模板中的 v-bind 动态样式绑定
 */
const customTheme = computed(() => {
  return props.styleConfig?.customTheme || {};
});

/**
 * extraFields: 提取消息中的附加字段
 * 
 * 实现逻辑：
 * 1. 排除标准字段（id、type、title、content、sendTime、sendUser、readStatus）
 * 2. 将剩余字段作为附加字段返回
 * 3. 忽略 undefined 值的字段
 * 
 * 用途：在模板中展示消息的附加自定义字段
 */
const extraFields = computed(() => {
  if (!props.message) return {};
  
  // 定义需要排除的标准字段列表
  const excludeKeys = ['id', 'type', 'title', 'content', 'sendTime', 'sendUser', 'readStatus'];
  const extra: Record<string, any> = {};
  
  // 遍历消息的所有字段，排除标准字段后添加到附加字段
  Object.keys(props.message).forEach(key => {
    if (!excludeKeys.includes(key) && props.message![key] !== undefined) {
      extra[key] = props.message![key];
    }
  });
  
  return extra;
});

/**
 * hasExtraFields: 判断是否存在附加字段
 * 
 * 实现逻辑：判断 extraFields 对象的键数量是否大于 0
 * 
 * 用途：控制附加字段区域的显示与隐藏
 */
const hasExtraFields = computed(() => {
  return Object.keys(extraFields.value).length > 0;
});

/**
 * ==================== 方法定义 ====================
 */

/**
 * getTypeLabel: 获取消息类型的显示标签
 * 
 * 功能说明：根据消息类型返回对应的中文显示名称
 * 
 * 类型映射：
 * - system: 系统通知
 * - notice: 公告通知
 * - todo: 待办提醒
 * - alert: 告警消息
 * - message: 消息通知
 * 
 * @param {string} type - 消息类型标识
 * @returns {string} 消息类型的中文显示名称
 */
const getTypeLabel = (type: string): string => {
  const typeLabels: Record<string, string> = {
    'system': '系统通知',
    'notice': '公告通知',
    'todo': '待办提醒',
    'alert': '告警消息',
    'message': '消息通知'
  };
  
  return typeLabels[type] || type;
};

/**
 * getTypeTagType: 获取消息类型的标签颜色
 * 
 * 功能说明：根据消息类型返回对应的 Element Plus 标签颜色类型
 * 
 * 颜色映射：
 * - system: info（灰色）
 * - notice: primary（蓝色）
 * - todo: warning（橙色）
 * - alert: danger（红色）
 * - message: success（绿色）
 * 
 * @param {string} type - 消息类型标识
 * @returns {string} Element Plus 标签颜色类型
 */
const getTypeTagType = (type: string): string => {
  const typeMap: Record<string, string> = {
    'system': 'info',
    'notice': 'primary',
    'todo': 'warning',
    'alert': 'danger',
    'message': 'success'
  };
  
  return typeMap[type] || 'info';
};

/**
 * formatTime: 格式化时间显示
 * 
 * 功能说明：将时间戳或日期字符串格式化为指定的日期时间格式
 * 
 * 格式化模板：YYYY-mm-dd HH:MM:SS
 * - YYYY: 四位年份
 * - mm: 两位月份
 * - dd: 两位日期
 * - HH: 24小时制小时
 * - MM: 分钟
 * - SS: 秒
 * 
 * @param {string | Date} time - 时间戳或日期字符串
 * @returns {string} 格式化后的日期时间字符串
 */
const formatTime = (time: string | Date): string => {
  return formatDate(new Date(time), 'YYYY-mm-dd HH:MM:SS');
};

/**
 * handleMarkRead: 标记消息为已读
 * 
 * 功能说明：
 * 1. 检查消息是否存在且为未读状态
 * 2. 触发 mark-read 事件通知父组件
 * 3. 父组件负责调用后端 API 更新消息状态
 * 
 * 触发时机：用户点击"标记为已读"按钮时
 */
const handleMarkRead = () => {
  if (props.message && props.message.readStatus === 0) {
    emit('mark-read', props.message);
  }
};

/**
 * handleDelete: 删除消息
 * 
 * 功能说明：
 * 1. 检查消息是否存在
 * 2. 触发 delete 事件通知父组件
 * 3. 关闭弹窗
 * 
 * 触发时机：用户点击"删除消息"按钮时
 * 
 * 注意事项：删除操作应在父组件中确认后再执行实际删除
 */
const handleDelete = () => {
  if (props.message) {
    emit('delete', props.message);
    dialogVisible.value = false;
  }
};

/**
 * handleClose: 关闭弹窗
 * 
 * 功能说明：
 * 1. 触发 close 事件通知父组件
 * 2. 关闭弹窗（设置 dialogVisible 为 false）
 * 
 * 触发时机：用户点击关闭按钮或点击弹窗遮罩层时
 */
const handleClose = () => {
  emit('close');
  dialogVisible.value = false;
};

/**
 * ==================== Watch 监听器定义 ====================
 */

/**
 * 监听弹窗显示状态，自动标记已读
 * 
 * 监听逻辑：
 * 1. 当弹窗打开（visible 变为 true）时触发
 * 2. 检查是否配置了自动标记已读（autoMarkRead）
 * 3. 检查消息是否为未读状态
 * 4. 满足条件时调用 handleMarkRead 方法
 * 
 * 用途：实现打开弹窗时自动将未读消息标记为已读的功能
 */
watch(() => props.visible, (newVal) => {
  if (newVal && props.autoMarkRead && props.message?.readStatus === 0) {
    handleMarkRead();
  }
});
</script>

<style scoped lang="scss">
.notice-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
  background-color: #ffffff;
  
  &.theme-dark {
    background-color: #1a1a1a;
    color: #e0e0e0;
  }
  
  &.theme-custom {
    background-color: v-bind('customTheme.bgColor');
    color: v-bind('customTheme.textColor');
  }
}

.detail-header {
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
  
  .theme-dark & {
    border-bottom-color: #333333;
  }
  
  .theme-custom & {
    border-bottom-color: v-bind('customTheme.borderColor');
  }
}

.detail-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-label {
  font-size: 14px;
  color: #909399;
  white-space: nowrap;
}

.info-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.detail-content {
  padding: 16px 0;
}

.content-label {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.content-text {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 4px;
  min-height: 100px;
  max-height: 400px;
  overflow-y: auto;
  
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-thumb {
    background-color: #dcdfe6;
    border-radius: 3px;
  }
  
  &::-webkit-scrollbar-track {
    background-color: #f5f7fa;
    border-radius: 3px;
  }
}

.detail-extra {
  padding: 16px 0;
  border-top: 1px solid #ebeef5;
  
  .theme-dark & {
    border-top-color: #333333;
  }
  
  .theme-custom & {
    border-top-color: v-bind('customTheme.borderColor');
  }
}

.extra-label {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.extra-fields {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.extra-item {
  display: flex;
  gap: 8px;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.extra-key {
  font-size: 13px;
  color: #909399;
  white-space: nowrap;
}

.extra-value {
  font-size: 13px;
  color: #606266;
  word-break: break-all;
}

.detail-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
  
  .theme-dark & {
    border-top-color: #333333;
  }
  
  .theme-custom & {
    border-top-color: v-bind('customTheme.borderColor');
  }
}

.detail-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
}
</style>
