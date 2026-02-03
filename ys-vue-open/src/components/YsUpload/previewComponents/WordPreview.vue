<!--
 * Word文档预览组件
 * @author: Eric
 * @date: 2025-02-01
 * @description: 使用docx-preview库实现docx文件预览
-->
<template>
  <div class="word-preview-container">
    <!-- 工具栏 -->
    <div class="word-toolbar">
      <div class="toolbar-left">
        <div class="file-info">
          <div class="file-icon-wrapper">
            <FileIcons :name="fileInfo.name" :width="20" :height="20" />
          </div>
          <span class="file-name" :title="fileInfo.name">{{ fileInfo.name }}</span>
        </div>
        <el-tag size="small" effect="plain" :type="isDocx ? 'success' : 'warning'">
          {{ isDocx ? 'DOCX' : 'DOC' }}
        </el-tag>
      </div>
      <div class="toolbar-right">
        <el-button size="small" text @click="handleDownload">
          <el-icon class="btn-icon"><Download /></el-icon>
          <span class="btn-text">下载</span>
        </el-button>
      </div>
    </div>

    <!-- 文档预览区域 -->
    <div class="word-content-wrapper" :style="{ height: containerHeight }">
      <div v-if="loading && !isDocumentLoaded" class="word-loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>文档加载中...</span>
      </div>

      <div v-else-if="error" class="word-error">
        <el-icon class="error-icon"><WarningFilled /></el-icon>
        <h3>{{ error }}</h3>
        <p v-if="!isDocx" class="error-tip">
          提示：当前仅支持 .docx 格式的在线预览，.doc 格式请下载后查看
        </p>
        <el-button type="primary" size="small" @click="handleDownload">
          <el-icon><Download /></el-icon>
          下载查看
        </el-button>
      </div>

      <div v-else-if="!isDocx" class="word-unsupported">
        <div class="unsupported-icon-wrapper">
          <div class="file-icon-large">
            <FileIcons :name="fileInfo.name" :width="64" :height="64" />
          </div>
        </div>
        <h3>.doc 格式暂不支持在线预览</h3>
        <p>该文件为旧版Word格式，请下载后使用Microsoft Word或WPS打开查看</p>
        <el-button type="primary" size="default" @click="handleDownload">
          <el-icon><Download /></el-icon>
          下载文档
        </el-button>
      </div>

      <div ref="docxContainerRef" class="docx-container" :style="{ display: (loading && !isDocumentLoaded) || error || !isDocx ? 'none' : 'block' }"></div>
    </div>

    <!-- 底部信息 -->
    <div class="word-footer">
      <div class="word-meta-info">
        <span class="meta-item">
          <div class="icon-wrapper">
            <FileIcons :name="fileInfo.name" :width="16" :height="16" />
          </div>
          <span>{{ isDocx ? 'DOCX' : 'DOC' }}</span>
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
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { ElButton, ElIcon, ElTag } from 'element-plus'
import { Download, Loading, WarningFilled, DataLine } from '@element-plus/icons-vue'
import FileIcons from 'file-icons-vue'
import { renderAsync } from 'docx-preview'
import { formatFileSize, getFileId, getFileExtension } from './utils'

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

const docxContainerRef = ref<HTMLDivElement>()
const loading = ref(false)
const error = ref('')
const isDocumentLoaded = ref(false)

const isDocx = computed(() => {
  const ext = getFileExtension(props.fileInfo.name).toLowerCase()
  return ext === 'docx'
})

const containerHeight = computed(() => {
  return props.containerHeight || 'calc(100% - 100px)'
})

onMounted(async () => {
  await nextTick()
  await loadDocument()
})

async function loadDocument() {
  loading.value = true
  error.value = ''

  try {
    if (!isDocx.value) {
      loading.value = false
      return
    }

    let arrayBuffer: ArrayBuffer | null = null

    if (props.fileInfo.url) {
      const response = await fetch(props.fileInfo.url)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      arrayBuffer = await response.arrayBuffer()
    }
    else if (props.fileInfo.file && props.fileInfo.file.size > 0) {
      arrayBuffer = await props.fileInfo.file.arrayBuffer()
    }
    else {
      const fileId = getFileId(props.fileInfo)
      if (fileId) {
        error.value = '无法获取文档内容'
        emit('error', error.value)
        return
      }
      emit('error', error.value)
      loading.value = false
      return
    }

    if (arrayBuffer) {
      await renderAsync(
        arrayBuffer,
        docxContainerRef.value!,
        undefined,
        {
          className: 'docx-preview-document',
          inWrapper: true,
          hideWrapperOnPrint: false,
          ignoreWidth: false,
          ignoreHeight: false,
          ignoreFonts: false,
          breakPages: true,
          ignoreLastRenderedPageBreak: true,
          experimental: false,
          trimXmlDeclaration: true,
          useBase64URL: false,
          renderChanges: false,
          renderHeaders: true,
          renderFooters: true,
          renderFootnotes: true,
          renderEndnotes: true,
          renderComments: false,
          renderAltChunks: true,
          debug: false,
        }
      )

      isDocumentLoaded.value = true
      emit('load')
    } else {
      error.value = '文档数据为空'
      emit('error', error.value)
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : '文档加载失败'
    emit('error', error.value)
  } finally {
    loading.value = false
  }
}

function handleDownload() {
  emit('download')
}
</script>

<style scoped lang="scss">
.word-preview-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.word-toolbar {
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

.word-content-wrapper {
  flex: 1;
  overflow: auto;
  background-color: #f0f0f0;
  padding: 20px;

  .word-loading,
  .word-error,
  .word-unsupported {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    gap: 16px;
    color: #606266;
    text-align: center;

    .el-icon {
      font-size: 48px;
    }

    &.word-error {
      color: #f56c6c;

      .error-tip {
        color: #e6a23c;
        font-size: 13px;
      }
    }

    h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 500;
    }

    p {
      margin: 0;
      color: #909399;
      max-width: 400px;
      font-size: 14px;
    }
  }

  .word-unsupported {
    .unsupported-icon-wrapper {
      width: 80px;
      height: 80px;
      border-radius: 16px;
      background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 8px;

      .file-icon-large {
        font-size: 40px;
        color: #fff;
      }
    }
  }

  .docx-container {
    background-color: #ffffff;
    min-height: 100%;
    padding: 40px;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
    max-width: 900px;
    margin: 0 auto;

    :deep(.docx-preview-document) {
      max-width: 100%;
      margin: 0 auto;
      background-color: #ffffff;
    }

    :deep(.docx-wrapper) {
      background-color: #ffffff;
    }

    :deep(section.docx) {
      background-color: #ffffff;
      margin-bottom: 20px;
    }
  }
}

.word-footer {
  padding: 10px 16px;
  background-color: #ffffff;
  border-top: 1px solid #ebeef5;

  .word-meta-info {
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
  .word-toolbar {
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

  .word-content-wrapper {
    padding: 12px;

    .docx-container {
      padding: 24px;
    }
  }

  .word-footer {
    padding: 8px 12px;

    .word-meta-info {
      gap: 16px;
      flex-wrap: wrap;
    }
  }
}
</style>
