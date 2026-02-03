<template>
  <div class="ys-upload-progress">
    <!-- 基础进度条 -->
    <el-progress
      v-if="!showDetail"
      :percentage="percentage"
      :stroke-width="strokeWidth"
      :show-text="showText"
      :status="progressStatus"
      :color="strokeColor"
      :type="type"
      :indeterminate="indeterminate"
      :format="formatText"
    />
    
    <!-- 详细进度信息 -->
    <div v-else class="ys-upload-progress-detail">
      <div class="progress-header">
        <div class="file-info">
          <el-icon class="file-icon">
            <component :is="fileIcon" />
          </el-icon>
          <div class="file-details">
            <div class="file-name" :title="file.name">{{ file.name }}</div>
            <div class="file-size">{{ formattedSize }}</div>
          </div>
        </div>
        <div class="progress-actions">
          <el-button
            v-if="file.status === 'uploading' && showPause"
            link
            size="small"
            @click="handlePause"
          >
            <el-icon><VideoPause /></el-icon>
          </el-button>
          <el-button
            v-if="file.status === 'paused' && showResume"
            link
            size="small"
            @click="handleResume"
          >
            <el-icon><VideoPlay /></el-icon>
          </el-button>
          <el-button
            v-if="showCancel && ['uploading', 'paused'].includes(file.status)"
            link
            size="small"
            @click="handleCancel"
          >
            <el-icon><Close /></el-icon>
          </el-button>
          <el-button
            v-if="file.status === 'error' && showRetry"
            link
            size="small"
            @click="handleRetry"
          >
            <el-icon><RefreshRight /></el-icon>
          </el-button>
        </div>
      </div>
      
      <div class="progress-content">
        <el-progress
          :percentage="percentage"
          :stroke-width="strokeWidth"
          :show-text="false"
          :status="progressStatus"
          :color="strokeColor"
          :indeterminate="indeterminate"
        />
        
        <div class="progress-info">
          <div class="progress-text">
            <span class="progress-percentage">{{ percentage }}%</span>
            <span v-if="file.status === 'uploading'" class="progress-status">上传中...</span>
            <span v-else-if="file.status === 'success'" class="progress-status success">上传成功</span>
            <span v-else-if="file.status === 'error'" class="progress-status error">上传失败</span>
            <span v-else-if="file.status === 'paused'" class="progress-status paused">已暂停</span>
            <span v-else-if="file.status === 'cancelled'" class="progress-status cancelled">已取消</span>
          </div>
          
          <div v-if="showSpeed && uploadSpeed > 0" class="upload-speed">
            {{ formattedSpeed }}
          </div>
        </div>
        
        <div v-if="showRemainingTime && remainingTime > 0" class="remaining-time">
          剩余时间: {{ formattedRemainingTime }}
        </div>
        
        <!-- 错误信息 -->
        <div v-if="file.status === 'error' && file.error" class="error-message">
          <el-icon class="error-icon"><WarningFilled /></el-icon>
          <span>{{ file.error }}</span>
        </div>
        
        <!-- 分片上传进度 -->
        <div v-if="showChunkProgress && file.chunks && file.chunks.length > 1" class="chunk-progress">
          <div class="chunk-info">
            分片进度: {{ uploadedChunks }}/{{ totalChunks }}
          </div>
          <div class="chunk-list">
            <div
              v-for="(chunk, index) in file.chunks"
              :key="index"
              class="chunk-item"
              :class="{
                'chunk-uploaded': chunk.uploaded,
                'chunk-uploading': !chunk.uploaded && index === currentChunkIndex,
                'chunk-waiting': !chunk.uploaded && index !== currentChunkIndex
              }"
            >
              <div class="chunk-index">{{ index + 1 }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElProgress, ElButton, ElIcon } from 'element-plus'
import {
  Document,
  Picture,
  VideoCamera,
  Headset,
  Folder,
  VideoPause,
  VideoPlay,
  Close,
  RefreshRight,
  WarningFilled
} from '@element-plus/icons-vue'
import type { UploadFile } from './types/upload'
import { UploadUtils } from './utils'

// Props 定义
interface Props {
  file: UploadFile
  showText?: boolean
  showSpeed?: boolean
  showRemainingTime?: boolean
  showDetail?: boolean
  showPause?: boolean
  showResume?: boolean
  showCancel?: boolean
  showRetry?: boolean
  showChunkProgress?: boolean
  strokeWidth?: number
  strokeColor?: string
  type?: 'line' | 'circle' | 'dashboard'
  startTime?: number
}

const props = withDefaults(defineProps<Props>(), {
  showText: true,
  showSpeed: true,
  showRemainingTime: true,
  showDetail: false,
  showPause: true,
  showResume: true,
  showCancel: true,
  showRetry: true,
  showChunkProgress: false,
  strokeWidth: 8,
  type: 'line'
})

// Emits 定义
const emit = defineEmits<{
  pause: [file: UploadFile]
  resume: [file: UploadFile]
  cancel: [file: UploadFile]
  retry: [file: UploadFile]
}>()

// 计算属性
const percentage = computed(() => {
  return Math.min(Math.max(props.file.progress || 0, 0), 100)
})

const progressStatus = computed(() => {
  switch (props.file.status) {
    case 'success':
      return 'success'
    case 'error':
      return 'exception'
    case 'uploading':
      return undefined
    default:
      return undefined
  }
})

const indeterminate = computed(() => {
  return props.file.status === 'ready' || (props.file.status === 'uploading' && props.file.progress === 0)
})

const fileIcon = computed(() => {
  if (UploadUtils.isImageFile(props.file.file)) {
    return Picture
  } else if (UploadUtils.isVideoFile(props.file.file)) {
    return VideoCamera
  } else if (UploadUtils.isAudioFile(props.file.file)) {
    return Headset
  } else {
    return Document
  }
})

const formattedSize = computed(() => {
  return UploadUtils.formatFileSize(props.file.size)
})

// 上传速度计算
const uploadSpeed = computed(() => {
  if (!props.startTime || props.file.status !== 'uploading') {
    return 0
  }
  
  const uploadedSize = (props.file.progress / 100) * props.file.size
  return UploadUtils.calculateSpeed(uploadedSize, props.startTime)
})

const formattedSpeed = computed(() => {
  return UploadUtils.formatSpeed(uploadSpeed.value)
})

// 剩余时间计算
const remainingTime = computed(() => {
  if (!props.startTime || props.file.status !== 'uploading' || uploadSpeed.value <= 0) {
    return 0
  }
  
  const uploadedSize = (props.file.progress / 100) * props.file.size
  return UploadUtils.calculateRemainingTime(props.file.size, uploadedSize, uploadSpeed.value)
})

const formattedRemainingTime = computed(() => {
  return UploadUtils.formatRemainingTime(remainingTime.value)
})

// 分片进度
const uploadedChunks = computed(() => {
  if (!props.file.chunks) return 0
  return props.file.chunks.filter(chunk => chunk.uploaded).length
})

const totalChunks = computed(() => {
  return props.file.chunks?.length || 0
})

const currentChunkIndex = computed(() => {
  if (!props.file.chunks) return -1
  return props.file.chunks.findIndex(chunk => !chunk.uploaded)
})

// 格式化进度文本
const formatText = (percentage: number): string => {
  if (props.file.status === 'success') {
    return '上传成功'
  } else if (props.file.status === 'error') {
    return '上传失败'
  } else if (props.file.status === 'paused') {
    return '已暂停'
  } else if (props.file.status === 'cancelled') {
    return '已取消'
  }
  return `${percentage}%`
}

// 事件处理
const handlePause = () => {
  emit('pause', props.file)
}

const handleResume = () => {
  emit('resume', props.file)
}

const handleCancel = () => {
  emit('cancel', props.file)
}

const handleRetry = () => {
  emit('retry', props.file)
}
</script>

<style scoped lang="scss">
.ys-upload-progress {
  width: 100%;

  .ys-upload-progress-detail {
    .progress-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 8px;

      .file-info {
        display: flex;
        align-items: center;
        flex: 1;
        min-width: 0;

        .file-icon {
          font-size: 20px;
          color: var(--el-color-primary);
          margin-right: 8px;
          flex-shrink: 0;
        }

        .file-details {
          flex: 1;
          min-width: 0;

          .file-name {
            font-size: 14px;
            font-weight: 500;
            color: var(--el-text-color-primary);
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .file-size {
            font-size: 12px;
            color: var(--el-text-color-secondary);
            margin-top: 2px;
          }
        }
      }

      .progress-actions {
        display: flex;
        align-items: center;
        gap: 4px;
        flex-shrink: 0;

        .el-button {
          padding: 4px;
          color: var(--el-text-color-secondary);

          &:hover {
            color: var(--el-color-primary);
          }
        }
      }
    }

    .progress-content {
      .progress-info {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-top: 4px;

        .progress-text {
          display: flex;
          align-items: center;
          gap: 8px;

          .progress-percentage {
            font-weight: 500;
            color: var(--el-text-color-primary);
          }

          .progress-status {
            font-size: 12px;
            color: var(--el-text-color-secondary);

            &.success {
              color: var(--el-color-success);
            }

            &.error {
              color: var(--el-color-error);
            }

            &.paused {
              color: var(--el-color-warning);
            }

            &.cancelled {
              color: var(--el-text-color-disabled);
            }
          }
        }

        .upload-speed {
          font-size: 12px;
          color: var(--el-text-color-secondary);
        }
      }

      .remaining-time {
        font-size: 12px;
        color: var(--el-text-color-secondary);
        margin-top: 4px;
      }

      .error-message {
        display: flex;
        align-items: center;
        gap: 4px;
        margin-top: 8px;
        padding: 8px;
        background-color: var(--el-color-error-light-9);
        border: 1px solid var(--el-color-error-light-7);
        border-radius: 4px;
        font-size: 12px;
        color: var(--el-color-error);

        .error-icon {
          flex-shrink: 0;
        }
      }

      .chunk-progress {
        margin-top: 12px;
        padding: 8px;
        background-color: var(--el-fill-color-light);
        border-radius: 4px;

        .chunk-info {
          font-size: 12px;
          color: var(--el-text-color-secondary);
          margin-bottom: 8px;
        }

        .chunk-list {
          display: flex;
          flex-wrap: wrap;
          gap: 2px;

          .chunk-item {
            width: 20px;
            height: 20px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 2px;
            font-size: 10px;
            font-weight: 500;

            .chunk-index {
              line-height: 1;
            }

            &.chunk-uploaded {
              background-color: var(--el-color-success);
              color: white;
            }

            &.chunk-uploading {
              background-color: var(--el-color-primary);
              color: white;
              animation: pulse 1.5s ease-in-out infinite;
            }

            &.chunk-waiting {
              background-color: var(--el-border-color-lighter);
              color: var(--el-text-color-secondary);
            }
          }
        }
      }
    }
  }
}

@keyframes pulse {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
  100% {
    opacity: 1;
  }
}
</style>