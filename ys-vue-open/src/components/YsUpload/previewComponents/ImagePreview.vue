<!--
 * 图片预览组件
 * @author: Eric
 * @date: 2025-02-01
 * @description: 支持缩放、旋转、拖拽、全屏的图片预览组件
-->
<template>
  <div class="image-preview-container">
    <!-- 工具栏 -->
    <div class="image-toolbar">
      <div class="toolbar-left">
        <div class="file-info">
          <el-icon class="file-icon"><Picture /></el-icon>
          <span class="file-name" :title="fileInfo.name">{{ fileInfo.name }}</span>
        </div>
      </div>
      <div class="toolbar-center">
        <el-button-group>
          <el-button size="small" text title="放大" @click="handleZoomIn">
            <el-icon><ZoomIn /></el-icon>
          </el-button>
          <el-button size="small" text title="缩小" @click="handleZoomOut">
            <el-icon><ZoomOut /></el-icon>
          </el-button>
          <el-button size="small" text title="向左旋转" @click="handleRotateLeft">
            <el-icon><RefreshLeft /></el-icon>
          </el-button>
          <el-button size="small" text title="向右旋转" @click="handleRotateRight">
            <el-icon><RefreshRight /></el-icon>
          </el-button>
          <el-button size="small" text title="重置" @click="handleReset">
            <el-icon><Refresh /></el-icon>
          </el-button>
          <el-button size="small" text title="全屏" @click="handleFullscreen">
            <el-icon><FullScreen /></el-icon>
          </el-button>
        </el-button-group>
      </div>
      <div class="toolbar-right">
        <div class="scale-info">{{ Math.round(transform.scale * 100) }}%</div>
      </div>
    </div>

    <!-- 图片容器 -->
    <div
      ref="containerRef"
      class="image-container"
      :class="{ 'is-dragging': isDragging }"
      @wheel="handleWheel"
      @mousedown="handleMouseDown"
      @mousemove="handleMouseMove"
      @mouseup="handleMouseUp"
      @mouseleave="handleMouseUp"
    >
      <img
        ref="imageRef"
        :src="previewUrl"
        :alt="fileInfo.name"
        :style="imageStyle"
        @load="handleLoad"
        @error="handleError"
        @dragstart.prevent
      />
    </div>

    <!-- 底部信息 -->
    <div class="image-footer">
      <div class="image-meta-info">
        <span class="meta-item">
          <el-icon><Picture /></el-icon>
          <span>{{ imageNaturalSize.width }} × {{ imageNaturalSize.height }}</span>
        </span>
        <span class="meta-item">
          <el-icon><DataLine /></el-icon>
          <span>{{ formatFileSize(fileInfo.size) }}</span>
        </span>
        <span class="meta-item rotate-info">
          <el-icon><RefreshRight /></el-icon>
          <span>{{ transform.rotate }}°</span>
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted, onUnmounted } from 'vue'
import { ElButton, ElButtonGroup, ElIcon } from 'element-plus'
import {
  ZoomIn,
  ZoomOut,
  RefreshLeft,
  RefreshRight,
  Refresh,
  FullScreen,
  Picture,
  DataLine
} from '@element-plus/icons-vue'
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

interface PreviewComponentProps {
  fileInfo: PreviewFileInfo
  isFullscreen?: boolean
  containerHeight?: string
}

interface PreviewComponentEmits {
  load: []
  error: [message: string]
  download: []
}

interface ImageTransform {
  scale: number
  rotate: number
  translateX: number
  translateY: number
}

const props = defineProps<PreviewComponentProps>()
const emit = defineEmits<PreviewComponentEmits>()

const containerRef = ref<HTMLDivElement>()
const imageRef = ref<HTMLImageElement>()
const previewUrl = ref('')
const isDragging = ref(false)
const containerSize = reactive({ width: 0, height: 0 })
let resizeObserver: ResizeObserver | null = null

const transform = reactive<ImageTransform>({
  scale: 1,
  rotate: 0,
  translateX: 0,
  translateY: 0
})

const dragState = reactive({
  startX: 0,
  startY: 0,
  initialTranslateX: 0,
  initialTranslateY: 0
})

const ZOOM_CONFIG = {
  min: 0.1,
  max: 5,
  step: 0.1,
  wheelStep: 0.1
}

const imageNaturalSize = reactive({ width: 0, height: 0 })

const imageStyle = computed(() => ({
  transform: `scale(${transform.scale}) rotate(${transform.rotate}deg) translate(${transform.translateX}px, ${transform.translateY}px)`,
  transformOrigin: 'center center',
  transition: isDragging.value ? 'none' : 'transform 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
  maxWidth: '100%',
  maxHeight: '100%',
  cursor: isDragging.value ? 'grabbing' : 'grab',
  objectFit: 'contain' as const
}))

onMounted(async () => {
  await loadImage()
  updateContainerSize()

  if (containerRef.value) {
    resizeObserver = new ResizeObserver(() => {
      handleContainerResize()
    })
    resizeObserver.observe(containerRef.value)
  }
})

onUnmounted(() => {
  revokeBlobUrl(previewUrl.value)

  if (resizeObserver) {
    resizeObserver.disconnect()
    resizeObserver = null
  }
})

async function loadImage() {
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
      emit('error', '无法获取图片预览地址')
      return
    }

    emit('error', '无法获取图片预览地址')
  } catch (error) {
    emit('error', error instanceof Error ? error.message : '图片加载失败')
  }
}

function handleLoad() {
  if (imageRef.value) {
    imageNaturalSize.width = imageRef.value.naturalWidth
    imageNaturalSize.height = imageRef.value.naturalHeight
  }
  fitImageToContainer()
  emit('load')
}

function updateContainerSize() {
  if (containerRef.value) {
    const rect = containerRef.value.getBoundingClientRect()
    containerSize.width = rect.width
    containerSize.height = rect.height
  }
}

function fitImageToContainer() {
  transform.scale = 1
  transform.rotate = 0
  transform.translateX = 0
  transform.translateY = 0
}

function handleContainerResize() {
  updateContainerSize()
  fitImageToContainer()
}

function handleError() {
  emit('error', '图片加载失败')
}

function handleZoomIn() {
  if (transform.scale < ZOOM_CONFIG.max) {
    transform.scale = Math.min(transform.scale + ZOOM_CONFIG.step, ZOOM_CONFIG.max)
  }
}

function handleZoomOut() {
  if (transform.scale > ZOOM_CONFIG.min) {
    transform.scale = Math.max(transform.scale - ZOOM_CONFIG.step, ZOOM_CONFIG.min)
  }
}

function handleRotateLeft() {
  transform.rotate -= 90
}

function handleRotateRight() {
  transform.rotate += 90
}

function handleReset() {
  fitImageToContainer()
}

function handleFullscreen() {
  if (!containerRef.value) return

  if (!document.fullscreenElement) {
    containerRef.value.requestFullscreen().catch(err => {
      console.error('全屏切换失败:', err)
    })
  } else {
    document.exitFullscreen()
  }
}

function handleWheel(e: WheelEvent) {
  e.preventDefault()
  const delta = e.deltaY > 0 ? -ZOOM_CONFIG.wheelStep : ZOOM_CONFIG.wheelStep
  const newScale = transform.scale + delta

  if (newScale >= ZOOM_CONFIG.min && newScale <= ZOOM_CONFIG.max) {
    transform.scale = newScale
  }
}

function handleMouseDown(e: MouseEvent) {
  isDragging.value = true
  dragState.startX = e.clientX
  dragState.startY = e.clientY
  dragState.initialTranslateX = transform.translateX
  dragState.initialTranslateY = transform.translateY
}

function handleMouseMove(e: MouseEvent) {
  if (!isDragging.value) return

  const deltaX = e.clientX - dragState.startX
  const deltaY = e.clientY - dragState.startY

  transform.translateX = dragState.initialTranslateX + deltaX / transform.scale
  transform.translateY = dragState.initialTranslateY + deltaY / transform.scale
}

function handleMouseUp() {
  isDragging.value = false
}

defineExpose({
  reset: handleReset,
  zoomIn: handleZoomIn,
  zoomOut: handleZoomOut,
  rotateLeft: handleRotateLeft,
  rotateRight: handleRotateRight
})
</script>

<style scoped lang="scss">
.image-preview-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.image-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background-color: #ffffff;
  border-bottom: 1px solid #ebeef5;

  .toolbar-left {
    flex: 1;

    .file-info {
      display: flex;
      align-items: center;
      gap: 8px;

      .file-icon {
        font-size: 18px;
        color: #67c23a;
      }

      .file-name {
        font-size: 14px;
        color: #303133;
        max-width: 200px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-weight: 500;
      }
    }
  }

  .toolbar-center {
    flex: 0 0 auto;

    .el-button {
      color: #606266;
      transition: all 0.2s ease;

      &:hover {
        color: #409eff;
        background-color: #f5f7fa;
      }

      .el-icon {
        font-size: 16px;
      }
    }
  }

  .toolbar-right {
    flex: 1;
    display: flex;
    justify-content: flex-end;

    .scale-info {
      font-size: 13px;
      color: #606266;
      font-family: 'Consolas', 'Monaco', monospace;
      background-color: #f5f7fa;
      padding: 4px 10px;
      border-radius: 4px;
      font-weight: 500;
    }
  }
}

.image-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
  padding: 20px;
  background-color: #fafafa;
  background-image:
    linear-gradient(45deg, #f0f0f0 25%, transparent 25%),
    linear-gradient(-45deg, #f0f0f0 25%, transparent 25%),
    linear-gradient(45deg, transparent 75%, #f0f0f0 75%),
    linear-gradient(-45deg, transparent 75%, #f0f0f0 75%);
  background-size: 20px 20px;
  background-position: 0 0, 0 10px, 10px -10px, -10px 0px;

  &.is-dragging {
    cursor: grabbing;
  }

  img {
    max-width: 100%;
    max-height: 100%;
    object-fit: contain;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    user-select: none;
    border-radius: 4px;
  }
}

.image-footer {
  padding: 10px 16px;
  background-color: #ffffff;
  border-top: 1px solid #ebeef5;

  .image-meta-info {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 24px;

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

      &.rotate-info {
        font-family: 'Consolas', 'Monaco', monospace;
      }
    }
  }
}

// 响应式适配
@media (max-width: 768px) {
  .image-toolbar {
    flex-wrap: wrap;
    gap: 10px;
    padding: 10px 12px;

    .toolbar-left,
    .toolbar-right {
      flex: 1 1 100%;
      justify-content: center;
    }

    .toolbar-center {
      order: -1;
      flex: 1 1 100%;
      justify-content: center;

      .el-button-group {
        display: flex;
        justify-content: center;
      }
    }

    .file-name {
      max-width: 150px !important;
    }
  }

  .image-container {
    padding: 12px;
  }

  .image-footer {
    padding: 8px 12px;

    .image-meta-info {
      gap: 16px;
      flex-wrap: wrap;
    }
  }
}
</style>
