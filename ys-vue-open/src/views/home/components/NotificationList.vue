<template>
  <div class="notification-panel">
    <div class="panel-header">
      <div class="header-left">
        <span class="panel-title">系统通知</span>
        <el-badge :value="unreadCount" :max="99" class="notification-badge" v-if="unreadCount > 0" />
      </div>
      <el-button type="primary" link size="small" @click="handleReadAll" v-if="unreadCount > 0">
        全部已读
      </el-button>
    </div>
    <div class="notification-list">
      <div
        v-for="(item, index) in data"
        :key="item.id"
        class="notification-item"
        :class="{ unread: !item.read }"
        :style="{ animationDelay: `${index * 0.05}s` }"
        @click="handleClick(item)"
      >
        <div class="notification-icon" :class="item.type">
          <el-icon :size="16">
            <component :is="getTypeIcon(item.type)" />
          </el-icon>
        </div>
        <div class="notification-content">
          <div class="notification-title">{{ item.title }}</div>
          <div class="notification-text">{{ item.content }}</div>
          <div class="notification-time">{{ item.time }}</div>
        </div>
        <div class="notification-dot" v-if="!item.read"></div>
      </div>
      <el-empty v-if="data.length === 0" description="暂无通知" :image-size="80" />
    </div>
  </div>
</template>

<script setup lang="ts" name="NotificationList">
import { computed, markRaw } from 'vue';
import { Bell, ChatDotRound, Tickets } from '@element-plus/icons-vue';
import type { NotificationItem } from '../hooks/useMockData';

interface Props {
  data: NotificationItem[];
}

const props = defineProps<Props>();

const emit = defineEmits<{
  readAll: [];
  click: [item: NotificationItem];
}>();

const iconMap: Record<string, any> = {
  system: markRaw(Bell),
  message: markRaw(ChatDotRound),
  todo: markRaw(Tickets),
};

const getTypeIcon = (type: string) => {
  return iconMap[type] || Bell;
};

const unreadCount = computed(() => {
  return props.data.filter((item) => !item.read).length;
});

const handleReadAll = () => {
  emit('readAll');
};

const handleClick = (item: NotificationItem) => {
  emit('click', item);
};
</script>

<style scoped lang="scss">
.notification-panel {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  height: 100%;
  display: flex;
  flex-direction: column;

  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .header-left {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .panel-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }

    .notification-badge {
      :deep(.el-badge__content) {
        font-size: 10px;
      }
    }
  }

  .notification-list {
    flex: 1;
    overflow-y: auto;
    max-height: 320px;

    &::-webkit-scrollbar {
      width: 4px;
    }

    &::-webkit-scrollbar-thumb {
      background: #dcdfe6;
      border-radius: 2px;
    }
  }

  .notification-item {
    display: flex;
    align-items: flex-start;
    padding: 12px;
    margin-bottom: 8px;
    background: #f5f7fa;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;
    position: relative;
    animation: fadeInUp 0.4s ease-out forwards;
    opacity: 0;

    &:last-child {
      margin-bottom: 0;
    }

    &:hover {
      background: #ecf5ff;
    }

    &.unread {
      background: #f0f9eb;

      &:hover {
        background: #e1f3d8;
      }
    }

    .notification-icon {
      width: 32px;
      height: 32px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 12px;
      flex-shrink: 0;

      &.system {
        background: #409eff15;
        color: #409eff;
      }

      &.message {
        background: #67c23a15;
        color: #67c23a;
      }

      &.todo {
        background: #e6a23c15;
        color: #e6a23c;
      }
    }

    .notification-content {
      flex: 1;
      min-width: 0;

      .notification-title {
        font-size: 14px;
        font-weight: 500;
        color: #303133;
        margin-bottom: 4px;
      }

      .notification-text {
        font-size: 12px;
        color: #606266;
        margin-bottom: 4px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .notification-time {
        font-size: 11px;
        color: #909399;
      }
    }

    .notification-dot {
      width: 8px;
      height: 8px;
      background: #f56c6c;
      border-radius: 50%;
      position: absolute;
      right: 12px;
      top: 12px;
    }
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
