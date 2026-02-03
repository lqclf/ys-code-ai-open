<template>
  <div class="ys-upload-item" :class="itemClasses">
    <!-- 文本列表样式 -->
    <div v-if="listType === 'text'" class="upload-item-text">
      <div class="file-info-row">
        <div class="file-info">
          <div class="file-icon">
            <FileIcons :name="file.name" :width="24" :height="24" />
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
          <span v-else-if="file.status === 'ready'" class="upload-status ready">
            <el-icon>
              <Clock />
            </el-icon>
            准备上传
          </span>
          <span v-else-if="file.status === 'paused'" class="upload-status paused">
            <el-icon>
              <VideoPause />
            </el-icon>
            已暂停 {{ file.progress }}%
          </span>
          <span v-else-if="file.status === 'cancelled'" class="upload-status cancelled">
            <el-icon>
              <Close />
            </el-icon>
            已取消
          </span>
        </div>

        <div class="file-actions">
          <el-button v-if="file.status === 'uploading' && mode === 'upload'" link size="small"
            :disabled="isInCooldown()" @click="handlePause" title="暂停上传">
            <el-icon>
              <VideoPause />
            </el-icon>
          </el-button>
          <el-button v-if="file.status === 'paused' && mode === 'upload'" link size="small"
            :disabled="isInCooldown()" @click="handleResume" title="继续上传">
            <el-icon>
              <VideoPlay />
            </el-icon>
          </el-button>
          <el-button v-if="file.status === 'error' && mode === 'upload'" link size="small"
            :disabled="isInCooldown()" @click="handleRetry" title="重试上传">
            <el-icon>
              <RefreshRight />
            </el-icon>
          </el-button>
          <el-button v-if="showPreview && previewable" link size="small" @click="handlePreview" title="预览">
            <el-icon>
              <View />
            </el-icon>
          </el-button>
          <el-button v-if="showDownload && file.url" link size="small" @click="handleDownload" title="下载">
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
      <div v-if="['uploading', 'ready', 'paused'].includes(file.status)" class="progress-wrapper">
        <YsUploadProgress :file="file" :show-detail="false" :show-speed="true" :show-remaining-time="true" :start-time="startTime" />
      </div>
    </div>

    <!-- 图片卡片样式 -->
    <div v-else-if="listType === 'picture'" class="upload-item-picture">
      <div class="picture-container">
        <div class="picture-preview">
          <img v-if="isImageFile && previewUrl" :src="previewUrl" :alt="file.name" @load="handleImageLoad"
            @error="handleImageError" />
          <div v-else class="file-placeholder">
            <div class="placeholder-icon">
              <FileIcons :name="file.name" :width="40" :height="40" />
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
          <el-button v-if="showPreview && previewable" link size="small" @click="handlePreview">
            <el-icon>
              <View />
            </el-icon>
          </el-button>
          <el-button v-if="showDownload && file.url" link size="small" @click="handleDownload">
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

    <!-- 图片卡片网格样式 -->
    <div v-else-if="listType === 'picture-card'" class="upload-item-picture-card">
      <div class="card-container">
        <div class="card-preview">
          <img v-if="isImageFile && previewUrl" :src="previewUrl" :alt="file.name" @load="handleImageLoad"
            @error="handleImageError" />
          <div v-else class="file-placeholder">
            <div class="placeholder-icon">
              <FileIcons :name="file.name" :width="32" :height="32" />
            </div>
          </div>
        </div>

        <!-- 上传状态遮罩 -->
        <div v-if="file.status !== 'success'" class="upload-mask">
          <div v-if="file.status === 'uploading'" class="upload-progress">
            <el-progress type="circle" :percentage="file.progress" :width="40" :stroke-width="4" />
          </div>
          <div v-else-if="file.status === 'error'" class="upload-error">
            <el-icon class="error-icon">
              <CircleClose />
            </el-icon>
          </div>
        </div>

        <!-- 悬浮操作按钮 -->
        <div class="card-actions">
          <el-button v-if="showPreview && previewable" link @click="handlePreview">
            <el-icon>
              <ZoomIn />
            </el-icon>
          </el-button>
          <el-button v-if="showDownload && file.url" link @click="handleDownload">
            <el-icon>
              <Download />
            </el-icon>
          </el-button>
          <el-button v-if="showRemove && !disabled" link @click="handleRemove">
            <el-icon>
              <Delete />
            </el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 详细进度信息 -->
    <div v-if="showDetailProgress && file.status === 'uploading'" class="detail-progress">
      <YsUploadProgress :file="file" :show-detail="true" :start-time="startTime" @pause="$emit('pause', file)"
        @resume="$emit('resume', file)" @cancel="$emit('cancel', file)" @retry="$emit('retry', file)" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted, watch } from 'vue'
import { ElIcon, ElButton, ElProgress } from 'element-plus'
import {
  VideoPlay,
  CircleCheck,
  CircleClose,
  Loading,
  View,
  Download,
  Delete,
  ZoomIn,
  Clock,
  VideoPause,
  RefreshRight,
  Close
} from '@element-plus/icons-vue'
import FileIcons from 'file-icons-vue'
import YsUploadProgress from './YsUploadProgress.vue'
import type { UploadFile, FileOperationConfig } from './types/upload'
import { UploadUtils } from './utils'

// Props 定义
interface Props {
  file: UploadFile
  listType?: 'text' | 'picture' | 'picture-card' | string
  showProgress?: boolean
  showDetailProgress?: boolean
  showPreview?: boolean
  showDownload?: boolean
  showRemove?: boolean
  disabled?: boolean
  startTime?: number
  fileOperations?: FileOperationConfig
  mode?: 'upload' | 'view'
}

const props = withDefaults(defineProps<Props>(), {
  listType: 'text',
  showProgress: true,
  showDetailProgress: false,
  showPreview: true,
  showDownload: true,
  showRemove: true,
  disabled: false,
  mode: 'upload'
})

// Emits 定义
const emit = defineEmits<{
  preview: [file: UploadFile]
  download: [file: UploadFile]
  remove: [file: UploadFile]
  pause: [file: UploadFile]
  resume: [file: UploadFile]
  cancel: [file: UploadFile]
  retry: [file: UploadFile]
}>()

// 响应式数据
const previewUrl = ref<string>('')
const imageLoaded = ref(false)
const imageError = ref(false)

// 点击冷却时间记录
const lastClickTime = ref<number>(0)
const CLICK_COOLDOWN = 500 // 点击冷却时间（毫秒）

// 检查是否在冷却时间内
const isInCooldown = (): boolean => {
  const now = Date.now()
  return now - lastClickTime.value < CLICK_COOLDOWN
}

// 记录点击时间
const recordClickTime = () => {
  lastClickTime.value = Date.now()
}

// 计算属性
const itemClasses = computed(() => {
  return [
    `upload-item-${props.listType}`,
    `upload-item-${props.file.status}`,
    {
      'upload-item-disabled': props.disabled
    }
  ]
})

const formattedSize = computed(() => {
  return UploadUtils.formatFileSize(props.file.size)
})

const isImageFile = computed(() => {
  // 优先使用文件类型判断，如果file对象不存在则使用文件名后缀判断
  const file = props.file.file
  const fileName = props.file.name || ''
  const ext = fileName.split('.').pop()?.toLowerCase()
  const imageExts = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'svg']
  
  if (file && file.size > 0) {
    return UploadUtils.isImageFile(file)
  }
  return ext && imageExts.includes(ext)
})

const previewable = computed(() => {
  const file = props.file
  const hasPreviewSource = file.url || file.fileId || file.response?.data?.fileId || file.response?.data?.id
  
  if (!hasPreviewSource) {
    return false
  }
  
  const fileType = file.type || ''
  const fileName = file.name || ''
  const ext = fileName.split('.').pop()?.toLowerCase()
  
  const imageTypes = ['image/']
  const videoTypes = ['video/']
  const audioTypes = ['audio/']
  const textTypes = ['text/', 'application/json', 'application/javascript', 'application/xml']
  const officeTypes = [
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'application/vnd.openxmlformats-officedocument.presentationml.presentation',
    'application/msword',
    'application/vnd.ms-excel',
    'application/vnd.ms-powerpoint'
  ]
  
  const imageExts = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'svg']
  const videoExts = ['mp4', 'avi', 'mov', 'wmv', 'flv', 'webm']
  const audioExts = ['mp3', 'wav', 'ogg', 'aac', 'flac']
  const textExts = ['txt', 'md', 'json', 'xml', 'html', 'css', 'js', 'ts', 'vue']
  const officeExts = ['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'pdf']
  
  const isImage = imageTypes.some(type => fileType.startsWith(type)) || (ext && imageExts.includes(ext))
  const isVideo = videoTypes.some(type => fileType.startsWith(type)) || (ext && videoExts.includes(ext))
  const isAudio = audioTypes.some(type => fileType.startsWith(type)) || (ext && audioExts.includes(ext))
  const isText = textTypes.some(type => fileType.startsWith(type)) || (ext && textExts.includes(ext))
  const isOffice = officeTypes.some(type => fileType.includes(type)) || (ext && officeExts.includes(ext))
  
  return isImage || isVideo || isAudio || isText || isOffice
})

// 生命周期
onMounted(() => {
  if (isImageFile.value) {
    createPreviewUrl()
  }
})

// 监听文件状态变化，重置冷却时间
watch(() => props.file.status, (newStatus, oldStatus) => {
  // 当状态从 uploading 变为 paused，或从 paused 变为 uploading 时
  // 重置冷却时间，确保按钮可以立即点击
  if (
    (oldStatus === 'uploading' && newStatus === 'paused') ||
    (oldStatus === 'paused' && newStatus === 'uploading') ||
    (oldStatus === 'error' && newStatus === 'uploading')
  ) {
    lastClickTime.value = 0
  }
})

// 组件卸载时，撤销预览 URL
onUnmounted(() => {
  if (previewUrl.value) {
    UploadUtils.revokePreviewUrl(previewUrl.value)
  }
})

// 方法
const createPreviewUrl = () => {
  if (props.file.url) {
    previewUrl.value = props.file.url
  } else if (isImageFile.value && props.file.file && props.file.file.size > 0) {
    // 只有当file对象有效时才创建预览URL
    previewUrl.value = UploadUtils.createPreviewUrl(props.file.file)
  }
}
// 图片加载完成
const handleImageLoad = () => {
  imageLoaded.value = true
  imageError.value = false
}
// 图片加载失败
const handleImageError = () => {
  imageLoaded.value = false
  imageError.value = true
}

const handlePreview = () => {
  emit('preview', props.file)
}

const handleDownload = () => {
  emit('download', props.file)
}

const handleRemove = () => {
  emit('remove', props.file)
}

const handlePause = () => {
  // 检查冷却时间
  if (isInCooldown()) {
    console.warn('[YsUploadItem] 点击过于频繁，请稍后再试')
    return
  }
  
  // 记录点击时间
  recordClickTime()
  
  emit('pause', props.file)
}

const handleResume = () => {
  // 检查冷却时间
  if (isInCooldown()) {
    console.warn('[YsUploadItem] 点击过于频繁，请稍后再试')
    return
  }
  
  // 记录点击时间
  recordClickTime()
  
  emit('resume', props.file)
}

const handleRetry = () => {
  // 检查冷却时间
  if (isInCooldown()) {
    console.warn('[YsUploadItem] 点击过于频繁，请稍后再试')
    return
  }
  
  // 记录点击时间
  recordClickTime()
  
  emit('retry', props.file)
}
</script>

<style scoped lang="scss">
.ys-upload-item {
  &.upload-item-disabled {
    opacity: 0.6;
    pointer-events: none;
  }

  // 文本列表样式
  .upload-item-text {
    padding: 12px 16px;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 8px;
    margin-bottom: 12px;
    transition: all 0.3s;
    background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);

    &:hover {
      border-color: var(--el-color-primary);
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
      transform: translateY(-2px);
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

          &.ready {
            color: var(--el-color-info);
            background: var(--el-color-info-light-9);
          }

          &.paused {
            color: var(--el-color-warning);
            background: var(--el-color-warning-light-9);
          }

          &.cancelled {
            color: var(--el-text-color-secondary);
            background: var(--el-fill-color-light);
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

          .placeholder-icon {
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 4px;
          }

          .placeholder-text {
            max-width: 100%;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
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

  // 图片卡片样式
  .upload-item-picture-card {
    width: 148px;
    height: 148px;
    display: inline-block;
    margin-right: 8px;
    margin-bottom: 8px;

    .card-container {
      position: relative;
      width: 100%;
      height: 100%;
      border: 1px solid var(--el-border-color-lighter);
      border-radius: 6px;
      overflow: hidden;
      transition: all 0.3s;

      &:hover {
        border-color: var(--el-color-primary);

        .card-actions {
          opacity: 1;
        }
      }

      .card-preview {
        width: 100%;
        height: 100%;
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
          color: var(--el-text-color-secondary);

          .placeholder-icon {
            display: flex;
            align-items: center;
            justify-content: center;
          }
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

        .upload-error {
          color: white;
          text-align: center;

          .error-icon {
            font-size: 32px;
          }
        }
      }

      .card-actions {
        position: absolute;
        width: 100%;
        height: 100%;
        top: 0;
        left: 0;
        background-color: rgba(0, 0, 0, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        opacity: 0;
        transition: opacity 0.3s;

        .el-button {
          width: 32px;
          height: 32px;
          padding: 0;
          background-color: white;
          border: none;
          border-radius: 50%;
          color: var(--el-text-color-primary);
          font-size: 14px;

          &:hover {
            color: var(--el-color-primary);
          }
        }
      }
    }
  }

  .detail-progress {
    margin-top: 12px;
    padding: 12px;
    background-color: var(--el-fill-color-light);
    border-radius: 4px;
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