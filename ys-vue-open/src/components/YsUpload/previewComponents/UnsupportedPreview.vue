<!--
 * 不支持的文件类型预览组件
 * @author: Eric
 * @date: 2025-02-01
 * @description: 显示文件信息并提供下载选项
-->
<template>
  <div class="unsupported-preview-container">
    <!-- 工具栏 -->
    <div class="unsupported-toolbar">
      <div class="toolbar-left">
        <div class="file-info">
          <div class="file-icon-wrapper">
            <FileIcons :name="fileInfo.name" :width="20" :height="20" />
          </div>
          <span class="file-name" :title="fileInfo.name">{{ fileInfo.name }}</span>
        </div>
        <el-tag size="small" effect="plain" type="info">{{ fileTypeName }}</el-tag>
      </div>
      <div class="toolbar-right">
        <el-button size="small" text @click="handleDownload">
          <el-icon class="btn-icon"><Download /></el-icon>
          <span class="btn-text">下载</span>
        </el-button>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="unsupported-content">
      <div class="file-info-card">
        <div class="file-icon-wrapper">
          <div class="file-icon-large">
            <FileIcons :name="fileInfo.name" :width="80" :height="80" />
          </div>
        </div>
        <div class="file-details">
          <h3 class="file-name-display">{{ fileInfo.name }}</h3>
          <div class="file-meta-list">
            <div class="meta-row">
              <span class="meta-label">文件类型</span>
              <span class="meta-value">{{ fileTypeName }}</span>
            </div>
            <div class="meta-row">
              <span class="meta-label">文件大小</span>
              <span class="meta-value">{{ formatFileSize(fileInfo.size) }}</span>
            </div>
            <div class="meta-row">
              <span class="meta-label">MIME类型</span>
              <span class="meta-value">{{ fileInfo.type || '未知' }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="unsupported-message">
        <div class="message-icon-wrapper">
          <el-icon class="message-icon"><WarningFilled /></el-icon>
        </div>
        <h4>暂不支持在线预览</h4>
        <p>该文件类型暂不支持在线预览，请下载后使用相应的软件打开查看</p>
        <el-button type="primary" size="default" @click="handleDownload">
          <el-icon><Download /></el-icon>
          下载文件
        </el-button>
      </div>
    </div>

    <!-- 底部信息 -->
    <div class="unsupported-footer">
      <div class="unsupported-meta-info">
        <span class="meta-item">
          <div class="icon-wrapper">
            <FileIcons :name="fileInfo.name" :width="16" :height="16" />
          </div>
          <span>{{ fileTypeName }}</span>
        </span>
        <span class="meta-item">
          <el-icon><DataLine /></el-icon>
          <span>{{ formatFileSize(fileInfo.size) }}</span>
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElButton, ElIcon, ElTag } from 'element-plus'
import { WarningFilled, Download, DataLine } from '@element-plus/icons-vue'
import FileIcons from 'file-icons-vue'
import { formatFileSize, getFileTypeName } from './utils'

interface PreviewFileInfo {
  id?: string
  name: string
  size: number
  type: string
  url?: string
  file?: File
  response?: any
}

const props = defineProps<{
  fileInfo: PreviewFileInfo
  isFullscreen?: boolean
  containerHeight?: string
}>()

const emit = defineEmits<{
  load: []
  error: [message: string]
  download: []
}>()

const fileTypeName = computed(() => {
  return getFileTypeName(props.fileInfo)
})

function handleDownload() {
  emit('download')
}
</script>

<style scoped lang="scss">
.unsupported-preview-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.unsupported-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background-color: #ffffff;
  border-bottom: 1px solid #ebeef5;

  .toolbar-left {
    display: flex;
    align-items: center;
    gap: 12px;

    .file-info {
      display: flex;
      align-items: center;
      gap: 8px;

      .file-icon-wrapper {
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .file-name {
        font-size: 14px;
        color: #303133;
        max-width: 280px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-weight: 500;
      }
    }

    .el-tag {
      font-size: 11px;
      font-weight: 500;
    }
  }

  .toolbar-right {
    .el-button {
      color: #606266;
      transition: all 0.2s ease;

      &:hover {
        color: #409eff;
        background-color: #f5f7fa;
      }

      .btn-icon {
        font-size: 14px;
        margin-right: 4px;
      }

      .btn-text {
        font-size: 13px;
      }
    }
  }
}

.unsupported-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px;
  gap: 32px;
  background-color: #f5f7fa;

  .file-info-card {
    display: flex;
    align-items: center;
    gap: 24px;
    padding: 32px;
    background-color: #ffffff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
    max-width: 480px;
    width: 100%;
    border: 1px solid #ebeef5;

    .file-icon-wrapper {
      width: 72px;
      height: 72px;
      border-radius: 12px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;

      .file-icon-large {
        font-size: 36px;
        color: #fff;
      }
    }

    .file-details {
      flex: 1;
      min-width: 0;

      .file-name-display {
        margin: 0 0 12px 0;
        font-size: 16px;
        color: #303133;
        word-break: break-all;
        font-weight: 500;
      }

      .file-meta-list {
        display: flex;
        flex-direction: column;
        gap: 8px;

        .meta-row {
          display: flex;
          gap: 12px;
          font-size: 13px;

          .meta-label {
            color: #909399;
            min-width: 70px;
          }

          .meta-value {
            color: #606266;
            word-break: break-all;
          }
        }
      }
    }
  }

  .unsupported-message {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: 12px;

    .message-icon-wrapper {
      width: 56px;
      height: 56px;
      border-radius: 50%;
      background-color: #fdf6ec;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 4px;

      .message-icon {
        font-size: 28px;
        color: #e6a23c;
      }
    }

    h4 {
      margin: 0;
      font-size: 16px;
      color: #303133;
      font-weight: 500;
    }

    p {
      margin: 0;
      font-size: 14px;
      color: #909399;
      max-width: 360px;
    }

    .el-button {
      margin-top: 8px;
      padding: 10px 24px;
    }
  }
}

.unsupported-footer {
  padding: 10px 16px;
  background-color: #ffffff;
  border-top: 1px solid #ebeef5;

  .unsupported-meta-info {
    display: flex;
    align-items: center;
    gap: 20px;

    .meta-item {
      display: flex;
      align-items: center;
      gap: 6px;
      color: #606266;
      font-size: 12px;

      .el-icon {
        font-size: 13px;
        color: #909399;
      }
    }
  }
}

// 响应式适配
@media (max-width: 768px) {
  .unsupported-toolbar {
    padding: 10px 12px;

    .toolbar-left {
      .file-info {
        .file-name {
          max-width: 150px;
        }
      }
    }

    .toolbar-right {
      .btn-text {
        display: none;
      }
    }
  }

  .unsupported-content {
    padding: 24px 16px;

    .file-info-card {
      flex-direction: column;
      text-align: center;
      padding: 24px;

      .file-icon-wrapper {
        width: 60px;
        height: 60px;

        .file-icon-large {
          font-size: 28px;
        }
      }
    }
  }

  .unsupported-footer {
    padding: 8px 12px;

    .unsupported-meta-info {
      gap: 16px;
      flex-wrap: wrap;

      .icon-wrapper {
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
  }
}
</style>
