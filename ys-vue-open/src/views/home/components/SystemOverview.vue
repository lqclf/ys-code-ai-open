<template>
  <div class="system-overview">
    <div class="panel-header">
      <span class="panel-title">系统概览</span>
      <span class="panel-subtitle">运行状态</span>
    </div>
    <div class="overview-content">
      <div class="status-item">
        <div class="status-header">
          <span class="status-label">CPU 使用率</span>
          <span class="status-value">{{ data.cpu }}%</span>
        </div>
        <el-progress
          :percentage="data.cpu"
          :stroke-width="8"
          :show-text="false"
          :color="getProgressColor(data.cpu)"
        />
      </div>
      <div class="status-item">
        <div class="status-header">
          <span class="status-label">内存使用率</span>
          <span class="status-value">{{ data.memory }}%</span>
        </div>
        <el-progress
          :percentage="data.memory"
          :stroke-width="8"
          :show-text="false"
          :color="getProgressColor(data.memory)"
        />
      </div>
      <div class="status-item">
        <div class="status-header">
          <span class="status-label">磁盘使用率</span>
          <span class="status-value">{{ data.disk }}%</span>
        </div>
        <el-progress
          :percentage="data.disk"
          :stroke-width="8"
          :show-text="false"
          :color="getProgressColor(data.disk)"
        />
      </div>
      <div class="status-info">
        <div class="info-item">
          <el-icon :size="16" color="#409eff"><Clock /></el-icon>
          <span class="info-label">运行时间</span>
          <span class="info-value">{{ data.uptime }}</span>
        </div>
        <div class="info-item">
          <el-icon :size="16" color="#67c23a"><Promotion /></el-icon>
          <span class="info-label">系统版本</span>
          <span class="info-value">{{ data.version }}</span>
        </div>
        <div class="info-item">
          <el-icon :size="16" color="#e6a23c"><Calendar /></el-icon>
          <span class="info-label">更新日期</span>
          <span class="info-value">{{ data.lastUpdate }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="SystemOverview">
import { Clock, Promotion, Calendar } from '@element-plus/icons-vue';
import type { SystemStatusItem } from '../hooks/useMockData';

interface Props {
  data: SystemStatusItem;
}

defineProps<Props>();

const getProgressColor = (value: number): string => {
  if (value < 50) return '#67c23a';
  if (value < 80) return '#e6a23c';
  return '#f56c6c';
};
</script>

<style scoped lang="scss">
.system-overview {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  height: 100%;

  .panel-header {
    margin-bottom: 20px;

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

  .overview-content {
    .status-item {
      margin-bottom: 20px;

      &:last-of-type {
        margin-bottom: 0;
      }

      .status-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;

        .status-label {
          font-size: 13px;
          color: #606266;
        }

        .status-value {
          font-size: 14px;
          font-weight: 600;
          color: #303133;
        }
      }

      :deep(.el-progress-bar__outer) {
        background: #f0f2f5;
        border-radius: 4px;
      }

      :deep(.el-progress-bar__inner) {
        border-radius: 4px;
      }
    }

    .status-info {
      margin-top: 24px;
      padding-top: 20px;
      border-top: 1px solid #f0f2f5;

      .info-item {
        display: flex;
        align-items: center;
        margin-bottom: 12px;

        &:last-child {
          margin-bottom: 0;
        }

        .info-label {
          font-size: 13px;
          color: #909399;
          margin-left: 8px;
          width: 70px;
        }

        .info-value {
          font-size: 13px;
          color: #303133;
          margin-left: auto;
        }
      }
    }
  }
}
</style>
