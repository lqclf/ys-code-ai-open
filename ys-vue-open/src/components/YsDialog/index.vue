<template>
  <el-dialog
    ref="dialogRef"
    v-model="dialogVisible"
    v-bind="$attrs"
    align-center
    :fullscreen="isFullscreen"
    :class="{
      'ys-dialog': true,
      'ys-dialog-fullscreen': isFullscreen,
      'ys-dialog-custom-height': customHeight
    }"
    :style="dialogStyle"
    :show-close="isShowClose"
    @open="$emit('open')"
    @opened="$emit('opened')"
    @close="$emit('close')"
    @closed="$emit('closed')"
    @open-auto-focus="$emit('open-auto-focus')"
    @close-auto-focus="$emit('close-auto-focus')"
  >
        <!-- 自定义 header 插槽，添加全屏按钮 -->
    <template #header>
      <slot name="header">
        <div class="ys-dialog-header">
          <!-- 左侧内容插槽 -->
          <div class="ys-dialog-header-left">
            <slot name="header-left">
              <span class="ys-dialog-title" v-html="props.title"></span>
            </slot>
          </div>
          <div class="ys-dialog-header-actions">
            <!-- 全屏按钮 -->
            <el-link
                v-if="props.showFullscreenBtn"
                class="fullscreen-btn"
                @click="toggleFullscreen"
            >
                <i class="ri-fullscreen-line" v-if="!isFullscreen"/>
                <i class="ri-fullscreen-exit-line" v-else></i>
            </el-link>
                <!-- 其他扩展按钮插槽 -->
                <slot name="header-actions"></slot>
                <!-- 关闭按钮 -->
            <el-link
                v-if="!isShowClose"
                class="close-btn"
                @click="handleCloseClick"
            >
                <el-icon :size="16"><Close /></el-icon>
            </el-link>
          </div>
        </div>
      </slot>
    </template>

    <!-- 默认内容插槽 -->
    <div class="ys-dialog-body-wrapper">
      <slot></slot>
    </div>

    <!-- 自定义 footer 插槽，确保始终在底部 -->
    <template #footer>
      <div class="ys-dialog-footer">
        <slot name="footer"></slot>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElDialog } from 'element-plus'
import { Close } from '@element-plus/icons-vue'

// 定义组件的 props
interface Props {
  modelValue?: boolean
  title?: string
  height?: string | number
  showFullscreenBtn?: boolean
  // 继承所有 el-dialog 的属性
  [key: string]: any
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: false,
  title: '',
  height: 'auto',
  showFullscreenBtn: true
})
// 方法：处理关闭按钮点击
const handleCloseClick = () => {
  if (props.beforeClose) {
    props.beforeClose(() => {
      dialogVisible.value = false
    })
  } else {
    dialogVisible.value = false
  }
}
// 定义组件的 emits
const emit = defineEmits([
  'update:modelValue',
  'open',
  'opened',
  'close',
  'closed',
  'open-auto-focus',
  'close-auto-focus',
  'fullscreen-change'
])

// 对话框引用
const dialogRef = ref<InstanceType<typeof ElDialog>>()

// 对话框可见性
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => {
    emit('update:modelValue', value)
  }
})

// 全屏状态
const isFullscreen = ref(false)
// 关闭按钮是否显示
const isShowClose = ref(false)


// 自定义高度
const customHeight = computed(() => {
  return props.height !== 'auto' && props.height !== undefined
})

// 对话框样式
const dialogStyle = computed(() => {
  const style: Record<string, string> = {}
  
  if (!isFullscreen.value && customHeight.value) {
    // 确保自定义高度不超过屏幕高度的90%
    const heightValue = typeof props.height === 'number' ? `${props.height}px` : props.height
    style.height = heightValue
    style.maxHeight = '90vh' // 限制最大高度
  }
  
  return style
})

// 切换全屏
const toggleFullscreen = () => {
  isFullscreen.value = !isFullscreen.value
  emit('fullscreen-change', isFullscreen.value)
}

// 监听对话框关闭，重置全屏状态
watch(dialogVisible, (newVal) => {
  if (!newVal) {
    isFullscreen.value = false
  }
})

// 暴露方法给父组件
defineExpose({
  dialogRef,
  toggleFullscreen,
  isFullscreen: computed(() => isFullscreen.value)
})
</script>

<style scoped lang="scss">

.ys-dialog {
  /* 确保对话框内容区域可以滚动 */
  --el-dialog-padding-primary: 20px;
  /* padding: var(--el-dialog-padding-primary); */
}

.ys-dialog :deep(.el-dialog) {
  display: flex;
  flex-direction: column;
  max-height: 95vh; /* 非全屏状态下最大高度为90vh */
  margin-bottom: 0; /* 防止底部间距过大 */
  padding: 0 !important;
  /* padding: var(--el-dialog-padding-primary); */
}

.ys-dialog :deep(.el-dialog__header) {
  padding: var(--el-dialog-padding-primary) var(--el-dialog-padding-primary) 10px;
  margin-right: 0;
  flex-shrink: 0; /* 防止标题栏被压缩 */
}

.ys-dialog :deep(.el-dialog__footer) {
  padding: 10px var(--el-dialog-padding-primary) var(--el-dialog-padding-primary);
  margin-top: 0;
  flex-shrink: 0; /* 防止底部栏被压缩 */
}

/* 内容区域包装器 */
.ys-dialog-body-wrapper {
  flex: 1;
  //overflow-y: auto;
  padding: 0 var(--el-dialog-padding-primary);
  min-height: 0; /* 确保flex子元素可以收缩 */
}

/* 自定义高度样式 */
.ys-dialog-custom-height :deep(.el-dialog) {
  height: v-bind('typeof height === "number" ? height + "px" : height');
  max-height: 90vh; /* 自定义高度时，最大高度仍不超过90vh */
}

/* 全屏样式 */
.ys-dialog-fullscreen :deep(.el-dialog) {
  width: 100vw !important;
  height: 100vh !important;
  max-height: 100vh !important;
  margin: 0 !important;
  border-radius: 0 !important;
}

.ys-dialog-body-wrapper {
  max-height: calc(100vh - 140px); /* 减去 header 和 footer 的高度 */
  padding: 0 var(--el-dialog-padding-primary);
}


.ys-dialog-header-actions {
  display: flex;
  align-items: center;
  gap: 18px;
}

.ys-dialog-fullscreen-btn {
  padding: 4px 8px;
  color: var(--el-text-color-secondary);
  transition: color 0.3s;
}

.ys-dialog-fullscreen-btn:hover {
  color: var(--el-color-primary);
}

/* 确保 footer 始终在底部 */
.ys-dialog-footer {
  width: 100%;
}

/* 内容区域滚动条样式优化 */
.ys-dialog-body-wrapper::-webkit-scrollbar {
  width: 6px;
}

.ys-dialog-body-wrapper::-webkit-scrollbar-thumb {
  background-color: var(--el-border-color-hover);
  border-radius: 3px;
}

.ys-dialog-body-wrapper::-webkit-scrollbar-track {
  background-color: var(--el-fill-color-light);
  border-radius: 3px;
}
/* 自定义 header 样式 */
.ys-dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 5px;
}

.ys-dialog-header-left {
  display: flex;
  align-items: center;
  flex: 1;
  min-width: 0; /* 防止内容溢出 */
}

.ys-dialog-title {
  font-size: var(--el-dialog-title-font-size);
  font-weight: var(--el-dialog-title-font-weight);
  color: var(--el-text-color-primary);
  line-height: var(--el-dialog-font-line-height);
}
</style>