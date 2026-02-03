<!--
 * 文件预览对话框组件
 * @author: AI Assistant
 * @date: 2025-01-30
 * @description: 统一的文件预览入口，根据文件类型自动选择对应的预览组件
-->
<template>
  <YsDialog
    ref="dialogRef"
    :title="dialogTitle"
    v-model="isVisible"
    @close="handleClose"
    @fullscreen-change="handleFullscreenChange"
    width="80%"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    :append-to-body="true"
    :show-fullscreen-btn="true"
    :destroy-on-close="true"
  >
    <div v-if="fileInfo" class="file-preview-dialog">
      <!-- 动态组件：根据文件类型渲染对应的预览组件 -->
      <component
        :is="currentPreviewComponent"
        :file-info="previewFileInfo"
        :is-fullscreen="isFullscreen"
        :container-height="previewContainerHeight"
        @load="handlePreviewLoad"
        @error="handlePreviewError"
        @download="handleDownload"
      />
    </div>

    <template #footer>
      <div class="dialog-footer">
        <div class="footer-info">
          <span v-if="fileInfo">{{ fileInfo.name }}</span>
          <span v-if="fileInfo?.size" class="file-size">
            ({{ formatFileSize(fileInfo.size) }})
          </span>
        </div>
        <div class="footer-actions">
          <el-button @click="handleClose">关闭</el-button>
          <el-button type="primary" @click="handleDownload">
            <el-icon><Download /></el-icon>
            下载
          </el-button>
        </div>
      </div>
    </template>
  </YsDialog>
</template>

<script setup lang="ts" name="FilePreviewDialog">
import { ref, computed, defineAsyncComponent, markRaw } from 'vue'
import { ElButton, ElIcon, ElMessage } from 'element-plus'
import { Download } from '@element-plus/icons-vue'
import YsDialog from '@/components/YsDialog/index.vue'
import type { UploadFile } from '@/components/YsUpload/types/upload'
import type { PreviewFileInfo, PreviewType } from './previewComponents/utils'
import { getPreviewType, formatFileSize, getFileTypeName } from './previewComponents/utils'

/**
 * 异步加载预览组件
 */
const ImagePreview = defineAsyncComponent(() => import('./previewComponents/ImagePreview.vue'))
const VideoPreview = defineAsyncComponent(() => import('./previewComponents/VideoPreview.vue'))
const AudioPreview = defineAsyncComponent(() => import('./previewComponents/AudioPreview.vue'))
const PdfPreview = defineAsyncComponent(() => import('./previewComponents/PdfPreview.vue'))
const TextPreview = defineAsyncComponent(() => import('./previewComponents/TextPreview.vue'))
const CodePreview = defineAsyncComponent(() => import('./previewComponents/CodePreview.vue'))
const MarkdownPreview = defineAsyncComponent(() => import('./previewComponents/MarkdownPreview.vue'))
const WordPreview = defineAsyncComponent(() => import('./previewComponents/WordPreview.vue'))
const UnsupportedPreview = defineAsyncComponent(() => import('./previewComponents/UnsupportedPreview.vue'))

/**
 * 预览组件映射表
 */
const PREVIEW_COMPONENT_MAP: Record<PreviewType, any> = {
  image: markRaw(ImagePreview),
  video: markRaw(VideoPreview),
  audio: markRaw(AudioPreview),
  pdf: markRaw(PdfPreview),
  text: markRaw(TextPreview),
  code: markRaw(CodePreview),
  markdown: markRaw(MarkdownPreview),
  word: markRaw(WordPreview),
  excel: markRaw(UnsupportedPreview),
  powerpoint: markRaw(UnsupportedPreview),
  unsupported: markRaw(UnsupportedPreview)
}

/**
 * 组件Props定义
 */
const props = defineProps<{
  /** 自定义下载处理函数 */
  onDownload?: (file: UploadFile) => void | Promise<void>
}>()

/**
 * 组件Emits定义
 */
const emit = defineEmits<{
  /** 关闭事件 */
  (e: 'close'): void
  /** 下载事件 */
  (e: 'download', file: UploadFile): void
}>()

/**
 * 组件状态
 */
const isVisible = ref(false)
const isFullscreen = ref(false)
const fileInfo = ref<UploadFile | null>(null)
const previewType = ref<PreviewType>('unsupported')
const customDownloadHandler = ref<((file: UploadFile) => void | Promise<void>) | undefined>(undefined)

/**
 * 计算对话框标题
 */
const dialogTitle = computed(() => {
  if (!fileInfo.value) return '文件预览'
  const typeName = getFileTypeName(previewFileInfo.value)
  return `${typeName}预览 - ${fileInfo.value.name}`
})

/**
 * 计算当前预览组件
 */
const currentPreviewComponent = computed(() => {
  return PREVIEW_COMPONENT_MAP[previewType.value] || UnsupportedPreview
})

/**
 * 转换文件信息格式
 */
const previewFileInfo = computed<PreviewFileInfo>(() => {
  if (!fileInfo.value) {
    return {
      name: '',
      size: 0,
      type: ''
    }
  }

  return {
    id: fileInfo.value.fileId || fileInfo.value.id,
    name: fileInfo.value.name,
    size: fileInfo.value.size,
    type: fileInfo.value.type,
    url: fileInfo.value.url,
    file: fileInfo.value.file,
    response: fileInfo.value.response
  }
})

/**
 * 计算预览容器高度
 */
const previewContainerHeight = computed(() => {
  if (isFullscreen.value) {
    return 'calc(100vh - 160px)'
  }
  return 'calc(70vh - 60px)'
})

/**
 * 打开预览对话框
 * @param file 文件信息
 * @param options 可选配置
 */
function openDialog(file: UploadFile, options?: {
  title?: string
  onDownload?: (file: UploadFile) => void | Promise<void>
}) {
  fileInfo.value = file
  if (options?.onDownload) {
    customDownloadHandler.value = options.onDownload
  }
  previewType.value = getPreviewType(previewFileInfo.value.name, previewFileInfo.value.type)
  isVisible.value = true
}

/**
 * 关闭对话框
 */
function handleClose() {
  isVisible.value = false
  fileInfo.value = null
  customDownloadHandler.value = undefined
  emit('close')
}

/**
 * 处理全屏状态变化
 */
function handleFullscreenChange(fullscreen: boolean) {
  isFullscreen.value = fullscreen
}

/**
 * 处理预览加载完成
 */
function handlePreviewLoad() {
  // 可以在这里添加加载完成的处理逻辑
}

/**
 * 处理预览错误
 */
function handlePreviewError(message: string) {
  console.error('预览错误:', message)
}

/**
 * 处理下载
 */
async function handleDownload() {
  if (fileInfo.value) {
    if (customDownloadHandler.value) {
      try {
        await customDownloadHandler.value(fileInfo.value)
      } catch (error) {
        console.error('自定义下载函数执行失败:', error)
        ElMessage.error('下载失败')
      }
    } else {
      emit('download', fileInfo.value)
    }
  }
}

/**
 * 暴露方法给父组件
 */
defineExpose({
  openDialog
})
</script>

<style scoped lang="scss">
:deep(.el-dialog__body) {
    height: 100% !important;
    overflow: hidden !important; 
}
.file-preview-dialog {
    height: calc(100vh - 130px);
}
.dialog-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;

  .footer-info {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #606266;
    font-size: 14px;

    .file-size {
      color: #909399;
    }
  }

  .footer-actions {
    display: flex;
    gap: 12px;
  }
}
</style>