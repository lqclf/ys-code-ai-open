<!--
 * PDF预览组件
 * @author: Eric
 * @date: 2025-02-01
 * @description: 使用iframe嵌入PDF预览，支持下载功能
-->
<template>
  <div class="pdf-preview-container">
    <!-- 工具栏 -->
    <div class="pdf-toolbar">
      <div class="toolbar-left">
        <div class="file-info">
          <div class="file-icon-wrapper">
            <FileIcons :name="fileInfo.name" :width="20" :height="20" />
          </div>
          <span class="file-name" :title="fileInfo.name">{{ fileInfo.name }}</span>
        </div>
        <el-tag size="small" effect="plain" type="danger">PDF</el-tag>
      </div>
      <div class="toolbar-right">
        <el-button size="small" text @click="handleDownload">
          <el-icon class="btn-icon"><Download /></el-icon>
          <span class="btn-text">下载</span>
        </el-button>
      </div>
    </div>

    <!-- PDF预览区域 -->
    <div class="pdf-viewer-wrapper">
      <iframe
        v-if="previewUrl"
        ref="iframeRef"
        :src="previewUrl"
        class="pdf-viewer"
        frameborder="0"
        @load="handleLoad"
        @error="handleError"
      ></iframe>

      <!-- 加载状态 -->
      <div v-else-if="!loadError" class="pdf-loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>PDF加载中...</span>
      </div>

      <!-- 错误状态 -->
      <div v-if="loadError" class="pdf-error">
        <el-icon class="error-icon"><WarningFilled /></el-icon>
        <p>PDF加载失败</p>
        <el-button type="primary" size="small" @click="handleDownload">
          <el-icon><Download /></el-icon>
          下载查看
        </el-button>
      </div>
    </div>

    <!-- 底部信息 -->
    <div class="pdf-footer">
      <div class="pdf-meta-info">
        <span class="meta-item">
          <div class="icon-wrapper">
            <FileIcons :name="fileInfo.name" :width="16" :height="16" />
          </div>
          <span>PDF</span>
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
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElButton, ElIcon, ElTag } from 'element-plus'
import { Download, Loading, WarningFilled, DataLine } from '@element-plus/icons-vue'
import FileIcons from 'file-icons-vue'
import { createBlobUrl, revokeBlobUrl, getFileId, formatFileSize } from './utils'

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

const previewUrl = ref('')
const loadError = ref(false)
const blobUrlToRevoke = ref('')

const containerHeight = computed(() => {
  return props.containerHeight || 'calc(100% - 100px)'
})

onMounted(async () => {
  await loadPdf()
})

onUnmounted(() => {
  if (blobUrlToRevoke.value) {
    revokeBlobUrl(blobUrlToRevoke.value)
  }
})

async function loadPdf() {
  try {
    loadError.value = false

    if (props.fileInfo.url) {
      const response = await fetch(props.fileInfo.url)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      const blob = await response.blob()
      const blobUrl = createBlobUrl(blob)
      blobUrlToRevoke.value = blobUrl
      previewUrl.value = blobUrl
      return
    }

    if (props.fileInfo.file && props.fileInfo.file.size > 0) {
      const blobUrl = createBlobUrl(props.fileInfo.file)
      blobUrlToRevoke.value = blobUrl
      previewUrl.value = blobUrl
      return
    }

    const fileId = getFileId(props.fileInfo)
    if (fileId) {
      emit('error', '无法获取PDF预览地址')
      return
    }

    emit('error', '无法获取PDF预览地址')
  } catch (error) {
    loadError.value = true
    emit('error', error instanceof Error ? error.message : 'PDF加载失败')
  }
}

function handleLoad() {
  emit('load')
}

function handleError() {
  loadError.value = true
  emit('error', 'PDF加载失败')
}

function handleDownload() {
  emit('download')
}
</script>

<style scoped lang="scss">
.pdf-preview-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.pdf-toolbar {
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

.pdf-viewer-wrapper {
  flex: 1;
  position: relative;
  background-color: #525659;
  overflow: hidden;

  .pdf-viewer {
    width: 100%;
    height: 100%;
    border: none;
  }

  .pdf-loading {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    color: #fff;

    .el-icon {
      font-size: 32px;
    }
  }

  .pdf-error {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 16px;
    color: #fff;
    text-align: center;

    .error-icon {
      font-size: 48px;
      color: #f56c6c;
    }

    p {
      margin: 0;
      font-size: 14px;
    }
  }
}

.pdf-footer {
  padding: 10px 16px;
  background-color: #ffffff;
  border-top: 1px solid #ebeef5;

  .pdf-meta-info {
    display: flex;
    align-items: center;
    gap: 20px;

    .meta-item {
      display: flex;
      align-items: center;
      gap: 6px;
      color: #606266;
      font-size: 12px;

      .icon-wrapper {
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .el-icon {
        font-size: 13px;
        color: #909399;
      }
    }
  }
}

// 响应式适配
@media (max-width: 768px) {
  .pdf-toolbar {
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

  .pdf-footer {
    padding: 8px 12px;

    .pdf-meta-info {
      gap: 16px;
      flex-wrap: wrap;
    }
  }
}
</style>
