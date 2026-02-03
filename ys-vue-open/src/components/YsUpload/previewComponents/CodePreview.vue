<!--
 * 代码文件预览组件
 * @author: Eric
 * @date: 2025-02-01
 * @description: 支持多种编程语言的代码预览，集成highlight.js语法高亮
-->
<template>
  <div class="code-preview-container">
    <!-- 工具栏 -->
    <div class="code-toolbar">
      <div class="toolbar-left">
        <div class="file-info">
          <el-icon class="file-icon"><Document /></el-icon>
          <span class="file-name" :title="fileInfo.name">{{ fileInfo.name }}</span>
        </div>
        <el-tag size="small" effect="plain" class="language-tag">{{ languageLabel }}</el-tag>
      </div>
      <div class="toolbar-right">
        <el-button-group>
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

    <!-- 代码内容区域 -->
    <div class="code-content-wrapper" :style="{ height: containerHeight }">
      <div v-if="loading" class="code-loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>

      <div v-else-if="error" class="code-error">
        <el-icon class="error-icon"><WarningFilled /></el-icon>
        <p>{{ error }}</p>
      </div>

      <div v-else class="code-content">
        <div class="code-header">
          <div class="window-controls">
            <span class="control-dot red"></span>
            <span class="control-dot yellow"></span>
            <span class="control-dot green"></span>
          </div>
          <span class="code-meta">{{ lineCount }} 行</span>
        </div>
        <pre><code ref="codeRef" :class="codeClass">{{ codeContent }}</code></pre>
      </div>
    </div>

    <!-- 底部信息 -->
    <div class="code-footer">
      <div class="code-meta-info">
        <span class="meta-item">
          <el-icon><Document /></el-icon>
          <span>{{ languageLabel }}</span>
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
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElButton, ElButtonGroup, ElIcon, ElTag, ElMessage } from 'element-plus'
import { Document, CopyDocument, Download, Loading, WarningFilled, DataLine } from '@element-plus/icons-vue'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'
import { formatFileSize, getFileId, copyToClipboard, getCodeLanguage } from './utils'

/**
 * 预览文件信息接口
 */
interface PreviewFileInfo {
  id?: string
  name: string
  size: number
  type: string
  url?: string
  file?: File
  response?: any
}

/**
 * 组件Props定义
 */
const props = defineProps<{
  fileInfo: PreviewFileInfo
  isFullscreen?: boolean
  containerHeight?: string
}>()

/**
 * 组件Emits定义
 */
const emit = defineEmits<{
  load: []
  error: [message: string]
  download: []
}>()

/**
 * 组件状态
 */
const codeRef = ref<HTMLElement>()
const codeContent = ref('')
const loading = ref(false)
const error = ref('')

/**
 * 计算代码语言
 */
const language = computed(() => {
  return getCodeLanguage(props.fileInfo.name)
})

/**
 * 计算代码语言显示标签
 */
const languageLabel = computed(() => {
  const labels: Record<string, string> = {
    'javascript': 'JavaScript',
    'typescript': 'TypeScript',
    'css': 'CSS',
    'html': 'HTML',
    'java': 'Java',
    'json': 'JSON',
    'yaml': 'YAML',
    'xml': 'XML',
    'sql': 'SQL',
    'python': 'Python',
    'cpp': 'C/C++',
    'csharp': 'C#',
    'php': 'PHP',
    'go': 'Go',
    'rust': 'Rust',
    'shell': 'Shell',
    'plaintext': '纯文本'
  }
  return labels[language.value] || language.value.toUpperCase()
})

/**
 * 计算代码class
 */
const codeClass = computed(() => {
  return `language-${language.value}`
})

/**
 * 计算容器高度
 */
const containerHeight = computed(() => {
  return props.containerHeight || 'calc(100% - 100px)'
})

/**
 * 计算行数
 */
const lineCount = computed(() => {
  return codeContent.value.split('\n').length
})

/**
 * 组件挂载时加载代码
 */
onMounted(async () => {
  await loadCode()
})

/**
 * 加载代码内容
 */
async function loadCode() {
  loading.value = true
  error.value = ''

  try {
    // 优先使用URL获取（回显模式下file对象可能为空）
    if (props.fileInfo.url) {
      const response = await fetch(props.fileInfo.url)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      codeContent.value = await response.text()
    }
    // 其次使用文件对象（本地上传的文件）
    else if (props.fileInfo.file && props.fileInfo.file.size > 0) {
      codeContent.value = await props.fileInfo.file.text()
    }
    // 最后尝试通过文件ID获取
    else {
      const fileId = getFileId(props.fileInfo)
      if (fileId) {
        error.value = '无法获取代码内容'
        emit('error', error.value)
        return
      }
      error.value = '无法获取代码内容'
      emit('error', error.value)
      return
    }

    // 应用语法高亮
    await nextTick()
    highlightCode()
    emit('load')
  } catch (err) {
    error.value = err instanceof Error ? err.message : '代码加载失败'
    emit('error', error.value)
  } finally {
    loading.value = false
  }
}

/**
 * 语法高亮
 */
function highlightCode() {
  if (codeRef.value) {
    hljs.highlightElement(codeRef.value)
  }
}

/**
 * 处理复制
 */
async function handleCopy() {
  const success = await copyToClipboard(codeContent.value)
  if (success) {
    ElMessage.success('代码已复制到剪贴板')
  } else {
    ElMessage.error('复制失败')
  }
}

/**
 * 处理下载
 */
function handleDownload() {
  emit('download')
}
</script>

<style scoped lang="scss">
.code-preview-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.code-toolbar {
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

      .file-icon {
        font-size: 18px;
        color: #409eff;
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

    .language-tag {
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

.code-content-wrapper {
  flex: 1;
  overflow: auto;
  background-color: #f5f7fa;
  padding: 16px;

  .code-loading,
  .code-error {
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

    &.code-error {
      color: #f56c6c;

      .error-icon {
        font-size: 40px;
      }
    }
  }

  .code-content {
    background-color: #ffffff;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    border: 1px solid #ebeef5;

    .code-header {
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

      .code-meta {
        font-size: 12px;
        color: #909399;
        font-family: 'Consolas', 'Monaco', monospace;
      }
    }

    pre {
      margin: 0;
      padding: 16px;
      background-color: #ffffff;
      overflow: auto;

      code {
        font-family: 'JetBrains Mono', 'Consolas', 'Monaco', 'Courier New', monospace;
        font-size: 13px;
        line-height: 1.6;
        color: #24292e;
      }
    }
  }
}

.code-footer {
  padding: 10px 16px;
  background-color: #ffffff;
  border-top: 1px solid #ebeef5;

  .code-meta-info {
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
  .code-toolbar {
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

  .code-content-wrapper {
    padding: 12px;
  }

  .code-footer {
    padding: 8px 12px;

    .code-meta-info {
      gap: 12px;
      flex-wrap: wrap;
    }
  }
}
</style>
