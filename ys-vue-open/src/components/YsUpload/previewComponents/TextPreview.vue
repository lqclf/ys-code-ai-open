<!--
 * 文本文件预览组件
 * @author: Eric
 * @date: 2025-02-01
 * @description: 支持纯文本文件预览，包含自动换行、复制等功能
-->
<template>
  <div class="text-preview-container">
    <!-- 工具栏 -->
    <div class="text-toolbar">
      <div class="toolbar-left">
        <div class="file-info">
          <div class="file-icon-wrapper">
            <FileIcons :name="fileInfo.name" :width="20" :height="20" />
          </div>
          <span class="file-name" :title="fileInfo.name">{{ fileInfo.name }}</span>
        </div>
        <el-tag size="small" effect="plain" type="info">TXT</el-tag>
      </div>
      <div class="toolbar-right">
        <el-button-group>
          <el-button
            size="small"
            text
            :type="isWrap ? 'primary' : 'default'"
            @click="toggleWrap"
          >
            <el-icon class="btn-icon"><Reading /></el-icon>
            <span class="btn-text">{{ isWrap ? '不换行' : '自动换行' }}</span>
          </el-button>
          <el-button size="small" text @click="handleCopy">
            <el-icon class="btn-icon"><CopyDocument /></el-icon>
            <span class="btn-text">复制</span>
          </el-button>
          <el-button size="small" text @click="handleDownload">
            <el-icon class="btn-icon"><Download /></el-icon>
            <span class="btn-text">下载</span>
          </el-button>
        </el-button-group>
      </div>
    </div>

    <!-- 文本内容区域 -->
    <div class="text-content-wrapper" :style="{ height: containerHeight }">
      <div v-if="loading" class="text-loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>

      <div v-else-if="error" class="text-error">
        <el-icon class="error-icon"><WarningFilled /></el-icon>
        <p>{{ error }}</p>
      </div>

      <div v-else class="text-content">
        <div class="text-header">
          <div class="window-controls">
            <span class="control-dot red"></span>
            <span class="control-dot yellow"></span>
            <span class="control-dot green"></span>
          </div>
          <span class="text-meta">{{ lineCount }} 行 · {{ charCount }} 字符</span>
        </div>
        <pre :class="{ 'text-wrap': isWrap }">{{ textContent }}</pre>
      </div>
    </div>

    <!-- 底部信息 -->
    <div class="text-footer">
      <div class="text-meta-info">
        <span class="meta-item">
          <div class="icon-wrapper">
            <FileIcons :name="fileInfo.name" :width="16" :height="16" />
          </div>
          <span>纯文本</span>
        </span>
        <span class="meta-item">
          <el-icon><DataLine /></el-icon>
          <span>{{ charCount }} 字符</span>
        </span>
        <span class="meta-item">
          <el-icon><DataLine /></el-icon>
          <span>{{ lineCount }} 行</span>
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
import { ref, computed, onMounted } from 'vue'
import { ElButton, ElButtonGroup, ElIcon, ElTag, ElMessage } from 'element-plus'
import { Reading, CopyDocument, Download, Loading, WarningFilled, DataLine } from '@element-plus/icons-vue'
import FileIcons from 'file-icons-vue'
import { formatFileSize, getFileId, copyToClipboard } from './utils'

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

const textContent = ref('')
const isWrap = ref(true)
const loading = ref(false)
const error = ref('')

const containerHeight = computed(() => {
  return props.containerHeight || 'calc(100% - 100px)'
})

const charCount = computed(() => {
  return textContent.value.length
})

const lineCount = computed(() => {
  return textContent.value.split('\n').length
})

onMounted(async () => {
  await loadText()
})

async function loadText() {
  loading.value = true
  error.value = ''

  try {
    if (props.fileInfo.url) {
      const response = await fetch(props.fileInfo.url)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      textContent.value = await response.text()
      emit('load')
      return
    }

    if (props.fileInfo.file && props.fileInfo.file.size > 0) {
      textContent.value = await props.fileInfo.file.text()
      emit('load')
      return
    }

    const fileId = getFileId(props.fileInfo)
    if (fileId) {
      error.value = '无法获取文本内容'
      emit('error', error.value)
      return
    }

    error.value = '无法获取文本内容'
    emit('error', error.value)
  } catch (err) {
    error.value = err instanceof Error ? err.message : '文本加载失败'
    emit('error', error.value)
  } finally {
    loading.value = false
  }
}

function toggleWrap() {
  isWrap.value = !isWrap.value
}

async function handleCopy() {
  const success = await copyToClipboard(textContent.value)
  if (success) {
    ElMessage.success('内容已复制到剪贴板')
  } else {
    ElMessage.error('复制失败')
  }
}

function handleDownload() {
  emit('download')
}
</script>

<style scoped lang="scss">
.text-preview-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.text-toolbar {
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

      &.el-button--primary {
        color: #409eff;
        background-color: #ecf5ff;
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

.text-content-wrapper {
  flex: 1;
  overflow: auto;
  background-color: #f5f7fa;
  padding: 16px;

  .text-loading,
  .text-error {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    gap: 12px;
    color: #909399;

    .el-icon {
      font-size: 32px;
    }

    &.text-error {
      color: #f56c6c;

      .error-icon {
        font-size: 40px;
      }
    }
  }

  .text-content {
    background-color: #ffffff;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    border: 1px solid #ebeef5;

    .text-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 10px 16px;
      background-color: #fafafa;
      border-bottom: 1px solid #ebeef5;

      .window-controls {
        display: flex;
        gap: 6px;

        .control-dot {
          width: 10px;
          height: 10px;
          border-radius: 50%;

          &.red { background-color: #ff5f57; }
          &.yellow { background-color: #febc2e; }
          &.green { background-color: #28c840; }
        }
      }

      .text-meta {
        font-size: 12px;
        color: #909399;
        font-family: 'Consolas', 'Monaco', monospace;
      }
    }

    pre {
      margin: 0;
      padding: 16px;
      background-color: #ffffff;
      font-family: 'JetBrains Mono', 'Consolas', 'Monaco', 'Courier New', monospace;
      font-size: 13px;
      line-height: 1.6;
      color: #24292e;
      white-space: pre;
      word-wrap: normal;
      overflow-wrap: normal;
      overflow: auto;

      &.text-wrap {
        white-space: pre-wrap;
        word-wrap: break-word;
        overflow-wrap: break-word;
      }
    }
  }
}

.text-footer {
  padding: 10px 16px;
  background-color: #ffffff;
  border-top: 1px solid #ebeef5;

  .text-meta-info {
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
  .text-toolbar {
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

  .text-content-wrapper {
    padding: 12px;
  }

  .text-footer {
    padding: 8px 12px;

    .text-meta-info {
      gap: 12px;
      flex-wrap: wrap;
    }
  }
}
</style>
