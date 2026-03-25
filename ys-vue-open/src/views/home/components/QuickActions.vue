<template>
  <div class="quick-actions-panel">
    <div class="panel-header">
      <span class="panel-title">快捷操作</span>
      <span class="panel-subtitle">常用功能入口</span>
    </div>
    <div class="actions-grid">
      <div
        v-for="(item, index) in data"
        :key="item.id"
        class="action-item"
        :style="{ animationDelay: `${index * 0.05}s` }"
        @click="handleClick(item)"
      >
        <div class="action-icon" :style="{ background: `${item.color}15`, color: item.color }">
          <el-icon :size="24">
            <component :is="getIcon(item.icon)" />
          </el-icon>
        </div>
        <div class="action-name">{{ item.name }}</div>
        <div class="action-arrow">
          <el-icon><Right /></el-icon>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="QuickActions">
import { markRaw } from 'vue';
import { useRouter } from 'vue-router';
import {
  User,
  Setting,
  DataAnalysis,
  Message,
  Folder,
  Document,
  Right,
} from '@element-plus/icons-vue';
import type { QuickActionItem } from '../hooks/useMockData';

interface Props {
  data: QuickActionItem[];
}

defineProps<Props>();

const router = useRouter();

const iconMap: Record<string, any> = {
  User: markRaw(User),
  Setting: markRaw(Setting),
  DataAnalysis: markRaw(DataAnalysis),
  Message: markRaw(Message),
  Folder: markRaw(Folder),
  Document: markRaw(Document),
};

const getIcon = (iconName: string) => {
  return iconMap[iconName] || User;
};

const handleClick = (item: QuickActionItem) => {
  if (item.path) {
    router.push(item.path);
  }
};
</script>

<style scoped lang="scss">
.quick-actions-panel {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  height: 100%;

  .panel-header {
    margin-bottom: 16px;

    .panel-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }

    .panel-subtitle {
      font-size: 12px;
      color: #909399;
      margin-left: 8px;
    }
  }

  .actions-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .action-item {
    display: flex;
    align-items: center;
    padding: 16px;
    background: #f5f7fa;
    border-radius: 10px;
    cursor: pointer;
    transition: all 0.3s ease;
    animation: fadeInUp 0.4s ease-out forwards;
    opacity: 0;

    &:hover {
      background: #ecf5ff;
      transform: translateX(4px);

      .action-icon {
        transform: scale(1.1);
      }

      .action-arrow {
        opacity: 1;
        transform: translateX(4px);
      }
    }

    .action-icon {
      width: 44px;
      height: 44px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 12px;
      transition: transform 0.3s ease;
    }

    .action-name {
      flex: 1;
      font-size: 14px;
      font-weight: 500;
      color: #303133;
    }

    .action-arrow {
      opacity: 0;
      color: #909399;
      transition: all 0.3s ease;
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

@media screen and (max-width: 768px) {
  .quick-actions-panel {
    .actions-grid {
      grid-template-columns: 1fr;
    }
  }
}
</style>
