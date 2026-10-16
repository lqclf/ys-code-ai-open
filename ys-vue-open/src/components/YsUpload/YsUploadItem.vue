<!--
 * YsUpload 文件列表项组件（简化版）
 * 去除分片上传/暂停/恢复/重试/预览/取消等操作
 * @author: Eric
 * @date: 2025-06-16
 -->
<template>
  <div class="ys-upload-item" :class="itemClasses">
    <!-- 文本列表样式 -->
    <div v-if="listType === 'text'" class="upload-item-text">
      <div class="file-info-row">
        <div class="file-info">
          <div class="file-icon">
            <el-icon class="default-icon" :size="24">
              <Document v-if="!isImageFile" />
              <Picture v-else />
            </el-icon>
          </div>
          <div class="file-details">
            <div class="file-name" :title="file.name">
              {{ file.name }}
            </div>
          </div>
        </div>

        <div class="file-meta">
          <span class="file-size">{{ formattedSize }}</span>
          <span v-if="file.status === 'success' && mode === 'upload'" class="upload-status success">
            <el-icon>
              <CircleCheck />
            </el-icon>
            上传成功
          </span>
          <span v-else-if="file.status === 'error'" class="upload-status error">
            <el-icon>
              <CircleClose />
            </el-icon>
            上传失败
          </span>
          <span v-else-if="file.status === 'uploading'" class="upload-status uploading">
            <el-icon class="loading">
              <Loading />
            </el-icon>
            上传中 {{ file.progress }}%
          </span>
        </div>

        <div class="file-actions">
          <el-button v-if="showDownload && file.status === 'success'" link size="small" @click="handleDownload" title="下载">
            <el-icon>
              <Download />
            </el-icon>
          </el-button>
          <el-button v-if="showRemove && !disabled" link size="small" @click="handleRemove" title="删除">
            <el-icon>
              <Delete />
            </el-icon>
          </el-button>
        </div>
      </div>

      <!-- 进度条 - 始终显示上传中的进度 -->
      <div v-if="file.status === 'uploading'" class="progress-wrapper">
        <el-progress :percentage="file.progress" :stroke-width="6" />
      </div>
    </div>

    <!-- 图片列表样式 -->
    <div v-else-if="listType === 'picture'" class="upload-item-picture">
      <div class="picture-container">
        <div class="picture-preview">
          <img v-if="isImageFile && previewUrl" :src="previewUrl" :alt="file.name" @load="handleImageLoad" @error="handleImageError" />
          <div v-else class="file-placeholder">
            <div class="placeholder-icon">
              <el-icon :size="40">
                <Document />
              </el-icon>
            </div>
            <div class="placeholder-text">{{ file.name }}</div>
          </div>
        </div>

        <!-- 上传状态遮罩 -->
        <div v-if="file.status !== 'success'" class="upload-mask">
          <div v-if="file.status === 'uploading'" class="upload-progress">
            <el-progress type="circle" :percentage="file.progress" :width="60" :stroke-width="6" />
          </div>
          <div v-else-if="file.status === 'error'" class="upload-error">
            <el-icon class="error-icon">
              <CircleClose />
            </el-icon>
            <div class="error-text">上传失败</div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="picture-actions">
          <el-button v-if="showDownload && file.status === 'success'" link size="small" @click="handleDownload">
            <el-icon>
              <Download />
            </el-icon>
          </el-button>
          <el-button v-if="showRemove && !disabled" link size="small" @click="handleRemove">
            <el-icon>
              <Delete />
            </el-icon>
          </el-button>
        </div>
      </div>

      <div class="picture-info">
        <div class="file-name" :title="file.name">{{ file.name }}</div>
        <div class="file-size">{{ formattedSize }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { ElIcon, ElButton, ElProgress } from 'element-plus'
import {
  CircleCheck,
  CircleClose,
  Loading,
  Download,
  Delete,
  Document,
  Picture,
} from '@element-plus/icons-vue'
import type { UploadFile, FileOperationConfig } from './types/upload'
import { UploadUtils } from './utils'

// Props 定义
interface Props {
  file: UploadFile
  listType?: 'text' | 'picture' | 'picture-card' | string
  showDownload?: boolean
  showRemove?: boolean
  disabled?: boolean
  fileOperations?: FileOperationConfig
  mode?: 'upload' | 'view'
}

const props = withDefaults(defineProps<Props>(), {
  listType: 'text',
  showDownload: true,
  showRemove: true,
  disabled: false,
  mode: 'upload',
})

// Emits 定义
const emit = defineEmits<{
  download: [file: UploadFile]
  remove: [file: UploadFile]
}>()

// 响应式数据
const previewUrl = ref<string>('')

// 计算属性
const itemClasses = computed(() => {
  return [
    `upload-item-${props.listType}`,
    `upload-item-${props.file.status}`,
    {
      'upload-item-disabled': props.disabled,
    },
  ]
})

const formattedSize = computed(() => {
  return UploadUtils.formatFileSize(props.file.size)
})

const isImageFile = computed(() => {
  const file = props.file.file
  const fileName = props.file.name || ''
  const ext = fileName.split('.').pop()?.toLowerCase()
  const imageExts = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'svg']

  if (file && file.size > 0) {
    return file.type?.startsWith('image/')
  }
  return ext && imageExts.includes(ext)
})

// 生命周期
onMounted(() => {
  if (isImageFile.value) {
    createPreviewUrl()
  }
})

onUnmounted(() => {
  if (previewUrl.value && previewUrl.value.startsWith('blob:')) {
    URL.revokeObjectURL(previewUrl.value)
  }
})

// 方法
const createPreviewUrl = () => {
  if (props.file.url) {
    previewUrl.value = props.file.url
  } else if (props.file.file && props.file.file.size > 0) {
    previewUrl.value = URL.createObjectURL(props.file.file)
  }
}

const handleImageLoad = () => {}

const handleImageError = () => {}

const handleDownload = () => {
  emit('download', props.file)
}

const handleRemove = () => {
  emit('remove', props.file)
}
</script>

<style scoped lang="scss">
.ys-upload-item {
  &.upload-item-disabled {
    opacity: 0.6;
    pointer-events: none;
  }

  .default-icon {
    color: var(--el-text-color-secondary);
  }

  // 文本列表样式
  .upload-item-text {
    padding: 12px 16px;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 8px;
    margin-bottom: 12px;
    transition: all 0.3s;
    background: var(--next-bg-card);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);

    &:hover {
      border-color: var(--el-color-primary);
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
    }

    .file-info-row {
      display: flex;
      align-items: center;
      gap: 16px;
      flex-wrap: nowrap;

      .file-info {
        display: flex;
        align-items: center;
        flex: 1;
        min-width: 0;
        flex-shrink: 0;

        .file-icon {
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 12px;
          flex-shrink: 0;
        }

        .file-details {
          flex: 1;
          min-width: 0;

          .file-name {
            font-size: 15px;
            font-weight: 600;
            color: var(--el-text-color-primary);
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            letter-spacing: 0.3px;
          }
        }
      }

      .file-meta {
        display: flex;
        align-items: center;
        gap: 12px;
        font-size: 13px;
        flex-shrink: 0;

        .file-size {
          color: var(--el-text-color-regular);
          font-weight: 500;
          background: var(--el-fill-color-light);
          padding: 4px 10px;
          border-radius: 4px;
        }

        .upload-status {
          display: flex;
          align-items: center;
          gap: 4px;
          font-weight: 500;
          padding: 4px 10px;
          border-radius: 4px;

          &.success {
            color: var(--el-color-success);
            background: var(--el-color-success-light-9);
          }

          &.error {
            color: var(--el-color-error);
            background: var(--el-color-error-light-9);
          }

          &.uploading {
            color: var(--el-color-primary);
            background: var(--el-color-primary-light-9);

            .loading {
              animation: rotating 2s linear infinite;
            }
          }
        }
      }

      .file-actions {
        display: flex;
        align-items: center;
        gap: 4px;
        flex-shrink: 0;

        .el-button {
          width: 32px;
          height: 32px;
          padding: 0;
          border-radius: 6px;
          transition: all 0.3s;
          display: flex;
          align-items: center;
          justify-content: center;

          &:hover:not(:disabled) {
            background-color: var(--el-color-primary-light-9);
            color: var(--el-color-primary);
          }

          &:disabled {
            opacity: 0.4;
            cursor: not-allowed;
          }
        }
      }
    }

    .progress-wrapper {
      margin-top: 12px;
      padding: 12px;
      background: var(--el-fill-color-blank);
      border-radius: 6px;
      border: 1px solid var(--el-border-color-lighter);
    }
  }

  // 图片列表样式
  .upload-item-picture {
    display: flex;
    align-items: center;
    padding: 8px;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 4px;
    margin-bottom: 8px;
    transition: all 0.3s;

    &:hover {
      border-color: var(--el-color-primary);
    }

    .picture-container {
      position: relative;
      width: 60px;
      height: 60px;
      margin-right: 12px;
      flex-shrink: 0;

      .picture-preview {
        width: 100%;
        height: 100%;
        border-radius: 4px;
        overflow: hidden;
        background-color: var(--el-fill-color-light);

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .file-placeholder {
          width: 100%;
          height: 100%;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          font-size: 12px;
          color: var(--el-text-color-secondary);
        }
      }

      .upload-mask {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 4px;

        .upload-error {
          color: white;
          text-align: center;

          .error-icon {
            font-size: 20px;
            margin-bottom: 4px;
          }

          .error-text {
            font-size: 12px;
          }
        }
      }

      .picture-actions {
        position: absolute;
        top: 4px;
        right: 4px;
        display: flex;
        gap: 4px;
        opacity: 0;
        transition: opacity 0.3s;

        .el-button {
          width: 24px;
          height: 24px;
          padding: 0;
          background-color: rgba(255, 255, 255, 0.8);
          border: none;
          font-size: 12px;

          &:hover {
            background-color: white;
          }
        }
      }

      &:hover .picture-actions {
        opacity: 1;
      }
    }

    .picture-info {
      flex: 1;
      min-width: 0;

      .file-name {
        font-size: 14px;
        font-weight: 500;
        color: var(--el-text-color-primary);
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        margin-bottom: 4px;
      }

      .file-size {
        font-size: 12px;
        color: var(--el-text-color-secondary);
      }
    }
  }
}

@keyframes rotating {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}
</style>
