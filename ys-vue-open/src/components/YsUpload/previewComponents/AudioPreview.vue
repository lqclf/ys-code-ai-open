<!--
 * 音频预览组件
 * @author: Eric
 * @date: 2025-02-01
 * @description: 支持多种音频格式的预览组件
-->
<template>
  <div class="audio-preview-container">
    <!-- 工具栏 -->
    <div class="audio-toolbar">
      <div class="toolbar-left">
        <div class="file-info">
          <div class="file-icon-wrapper">
            <FileIcons :name="fileInfo.name" :width="20" :height="20" />
          </div>
          <span class="file-name" :title="fileInfo.name">{{ fileInfo.name }}</span>
        </div>
        <el-tag size="small" effect="plain" type="success">{{ audioFormat }}</el-tag>
      </div>
      <div class="toolbar-right">
        <el-button size="small" text @click="handleDownload">
          <el-icon class="btn-icon"><Download /></el-icon>
          <span class="btn-text">下载</span>
        </el-button>
      </div>
    </div>

    <!-- 音频播放器区域 -->
    <div class="audio-player-wrapper">
      <div class="audio-visual">
        <div class="audio-icon-wrapper">
          <div class="audio-icon">
            <FileIcons :name="fileInfo.name" :width="48" :height="48" />
          </div>
        </div>
        <div class="audio-wave">
          <div v-for="i in 20" :key="i" class="wave-bar" :style="{ height: `${Math.random() * 60 + 20}%` }"></div>
        </div>
      </div>

      <audio
        ref="audioRef"
        class="audio-player"
        :src="previewUrl"
        controls
        preload="metadata"
        @loadedmetadata="handleLoaded"
        @error="handleError"
      >
        <p class="audio-fallback">
          您的浏览器不支持音频播放，请
          <el-link type="primary" @click="handleDownload">下载文件</el-link>
          后查看
        </p>
      </audio>
    </div>

    <!-- 底部信息 -->
    <div class="audio-footer">
      <div class="audio-meta-info">
        <span class="meta-item">
          <div class="icon-wrapper">
            <FileIcons :name="fileInfo.name" :width="16" :height="16" />
          </div>
          <span>{{ audioFormat }}</span>
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
import { ElButton, ElIcon, ElLink, ElTag } from 'element-plus'
import { Download, DataLine } from '@element-plus/icons-vue'
import FileIcons from 'file-icons-vue'
import { createBlobUrl, revokeBlobUrl, getFileId, formatFileSize, getFileExtension } from './utils'

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

const audioRef = ref<HTMLAudioElement>()
const previewUrl = ref('')

const audioFormat = computed(() => {
  const ext = getFileExtension(props.fileInfo.name).toUpperCase()
  return ext || '音频'
})

onMounted(async () => {
  await loadAudio()
})

onUnmounted(() => {
  revokeBlobUrl(previewUrl.value)
})

async function loadAudio() {
  try {
    if (props.fileInfo.url) {
      previewUrl.value = props.fileInfo.url
      return
    }

    if (props.fileInfo.file) {
      previewUrl.value = createBlobUrl(props.fileInfo.file)
      return
    }

    const fileId = getFileId(props.fileInfo)
    if (fileId) {
      emit('error', '无法获取音频预览地址')
      return
    }

    emit('error', '无法获取音频预览地址')
  } catch (error) {
    emit('error', error instanceof Error ? error.message : '音频加载失败')
  }
}

function handleLoaded() {
  emit('load')
}

function handleError() {
  emit('error', '音频加载失败，格式可能不受支持')
}

function handleDownload() {
  emit('download')
}
</script>

<style scoped lang="scss">
.audio-preview-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.audio-toolbar {
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

.audio-player-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  gap: 24px;

  .audio-visual {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;

    .audio-icon-wrapper {
      width: 100px;
      height: 100px;
      border-radius: 50%;
      background-color: rgba(255, 255, 255, 0.2);
      display: flex;
      align-items: center;
      justify-content: center;
      animation: pulse 2s ease-in-out infinite;

      .audio-icon {
        font-size: 48px;
        color: #fff;
      }
    }

    .audio-wave {
      display: flex;
      align-items: center;
      gap: 4px;
      height: 40px;

      .wave-bar {
        width: 4px;
        background-color: rgba(255, 255, 255, 0.6);
        border-radius: 2px;
        animation: wave 1s ease-in-out infinite;

        @for $i from 1 through 20 {
          &:nth-child(#{$i}) {
            animation-delay: #{$i * 0.05}s;
          }
        }
      }
    }
  }

  .audio-player {
    width: 100%;
    max-width: 500px;
    height: 40px;
    border-radius: 20px;
  }

  .audio-fallback {
    color: #fff;
    text-align: center;
    padding: 20px;
    font-size: 14px;

    .el-link {
      font-weight: 500;
    }
  }
}

.audio-footer {
  padding: 10px 16px;
  background-color: #ffffff;
  border-top: 1px solid #ebeef5;

  .audio-meta-info {
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

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.8;
  }
}

@keyframes wave {
  0%, 100% {
    transform: scaleY(0.5);
  }
  50% {
    transform: scaleY(1);
  }
}

// 响应式适配
@media (max-width: 768px) {
  .audio-toolbar {
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

  .audio-player-wrapper {
    padding: 24px 16px;

    .audio-visual {
      .audio-icon-wrapper {
        width: 80px;
        height: 80px;

        .audio-icon {
          font-size: 36px;
        }
      }
    }
  }

  .audio-footer {
    padding: 8px 12px;

    .audio-meta-info {
      gap: 16px;
      flex-wrap: wrap;
    }
  }
}
</style>
