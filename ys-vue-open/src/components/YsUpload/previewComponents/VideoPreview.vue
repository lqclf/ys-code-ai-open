<!--
 * 视频预览组件
 * @author: Eric
 * @date: 2025-02-01
 * @description: 支持多种视频格式的预览组件，使用原生video标签
-->
<template>
  <div class="video-preview-container">
    <!-- 工具栏 -->
    <div class="video-toolbar">
      <div class="toolbar-left">
        <div class="file-info">
          <div class="file-icon-wrapper">
            <FileIcons :name="fileInfo.name" :width="20" :height="20" />
          </div>
          <span class="file-name" :title="fileInfo.name">{{ fileInfo.name }}</span>
        </div>
        <el-tag size="small" effect="plain" type="warning">{{ videoFormat }}</el-tag>
      </div>
      <div class="toolbar-right">
        <el-button size="small" text @click="handleDownload">
          <el-icon class="btn-icon"><Download /></el-icon>
          <span class="btn-text">下载</span>
        </el-button>
      </div>
    </div>

    <!-- 视频播放器 -->
    <div class="video-player-wrapper">
      <video
        ref="videoRef"
        class="video-player"
        :src="previewUrl"
        controls
        preload="metadata"
        @loadedmetadata="handleLoaded"
        @error="handleError"
      >
        <p class="video-fallback">
          您的浏览器不支持视频播放，请
          <el-link type="primary" @click="handleDownload">下载文件</el-link>
          后查看
        </p>
      </video>
    </div>

    <!-- 底部信息 -->
    <div class="video-footer">
      <div class="video-meta-info">
        <span class="meta-item">
          <div class="icon-wrapper">
            <FileIcons :name="fileInfo.name" :width="16" :height="16" />
          </div>
          <span>{{ videoFormat }}</span>
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

const videoRef = ref<HTMLVideoElement>()
const previewUrl = ref('')
const isLoaded = ref(false)

const videoFormat = computed(() => {
  const ext = getFileExtension(props.fileInfo.name).toUpperCase()
  return ext || '视频'
})

onMounted(async () => {
  await loadVideo()
})

onUnmounted(() => {
  revokeBlobUrl(previewUrl.value)
})

async function loadVideo() {
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
      emit('error', '无法获取视频预览地址')
      return
    }

    emit('error', '无法获取视频预览地址')
  } catch (error) {
    emit('error', error instanceof Error ? error.message : '视频加载失败')
  }
}

function handleLoaded() {
  isLoaded.value = true
  emit('load')
}

function handleError() {
  emit('error', '视频加载失败，格式可能不受支持')
}

function handleDownload() {
  emit('download')
}
</script>

<style scoped lang="scss">
.video-preview-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.video-toolbar {
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

.video-player-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  padding: 24px;

  .video-player {
    max-width: 100%;
    max-height: 100%;
    width: auto;
    height: auto;
    border-radius: 8px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
  }

  .video-fallback {
    color: #fff;
    text-align: center;
    padding: 40px;
    font-size: 14px;

    .el-link {
      font-weight: 500;
    }
  }
}

.video-footer {
  padding: 10px 16px;
  background-color: #ffffff;
  border-top: 1px solid #ebeef5;

  .video-meta-info {
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
  .video-toolbar {
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

  .video-player-wrapper {
    padding: 16px;
  }

  .video-footer {
    padding: 8px 12px;

    .video-meta-info {
      gap: 16px;
      flex-wrap: wrap;
    }
  }
}
</style>
