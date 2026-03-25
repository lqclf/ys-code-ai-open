<template>
  <div class="operation-log">
    <div class="panel-header">
      <span class="panel-title">操作记录</span>
      <el-button type="primary" link size="small" @click="handleViewAll">
        查看全部
      </el-button>
    </div>
    <div class="log-table">
      <el-table :data="data" style="width: 100%" size="small" :header-cell-style="headerStyle">
        <el-table-column prop="user" label="操作用户" min-width="80">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="24" :style="{ background: getAvatarColor(row.user) }">
                {{ row.user.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ row.user }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="操作类型" min-width="70">
          <template #default="{ row }">
            <el-tag :type="getTagType(row.type)" size="small" effect="light">
              {{ row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="操作内容" min-width="140" show-overflow-tooltip />
        <el-table-column prop="time" label="操作时间" min-width="140">
          <template #default="{ row }">
            <span class="time-text">{{ row.time }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="IP地址" min-width="110">
          <template #default="{ row }">
            <span class="ip-text">{{ row.ip }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts" name="OperationLog">
import type { OperationLogItem } from '../hooks/useMockData';

interface Props {
  data: OperationLogItem[];
}

defineProps<Props>();

const emit = defineEmits<{
  viewAll: [];
}>();

const headerStyle = {
  background: '#f5f7fa',
  color: '#606266',
  fontWeight: 600,
  fontSize: '13px',
};

const tagTypeMap: Record<string, string> = {
  登录: 'primary',
  修改: 'warning',
  新增: 'success',
  删除: 'danger',
  导出: 'info',
  配置: '',
};

const getTagType = (type: string): string => {
  return tagTypeMap[type] || '';
};

const avatarColors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#667eea', '#909399'];

const getAvatarColor = (name: string): string => {
  const index = name.charCodeAt(0) % avatarColors.length;
  return avatarColors[index];
};

const handleViewAll = () => {
  emit('viewAll');
};
</script>

<style scoped lang="scss">
.operation-log {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  height: 100%;

  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .panel-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }

  .log-table {
    :deep(.el-table) {
      --el-table-border-color: #f0f2f5;
      --el-table-header-bg-color: #f5f7fa;
      --el-table-row-hover-bg-color: #f5f7fa;

      .el-table__header-wrapper {
        th {
          padding: 10px 0;
        }
      }

      .el-table__body-wrapper {
        .el-table__row {
          transition: all 0.3s ease;

          &:hover {
            background: #f5f7fa;
          }
        }

        td {
          padding: 12px 0;
        }
      }
    }

    .user-cell {
      display: flex;
      align-items: center;
      gap: 8px;

      .user-name {
        font-size: 13px;
        color: #303133;
      }
    }

    .time-text {
      font-size: 12px;
      color: #909399;
    }

    .ip-text {
      font-size: 12px;
      color: #606266;
      font-family: 'Courier New', monospace;
    }
  }
}
</style>
