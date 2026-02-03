<!--
 * Markdown预览组件
 * @author: Eric
 * @date: 2025-02-01
 * @description: 使用markdown-it渲染Markdown文件，支持图片预览、目录导航、代码高亮
-->
<template>
  <div class="markdown-preview-container">
    <!-- 工具栏 -->
    <div class="markdown-toolbar">
      <div class="toolbar-left">
        <div class="file-info">
          <div class="file-icon-wrapper">
            <FileIcons :name="fileInfo.name" :width="20" :height="20" />
          </div>
          <span class="file-name" :title="fileInfo.name">{{ fileInfo.name }}</span>
        </div>
        <el-tag size="small" effect="plain" type="success">Markdown</el-tag>
      </div>
      <div class="toolbar-right">
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button value="preview">预览</el-radio-button>
          <el-radio-button value="source">源码</el-radio-button>
          <el-radio-button value="split">分屏</el-radio-button>
        </el-radio-group>
        <el-button-group class="action-btns">
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

    <!-- 内容区域 -->
    <div class="markdown-content-wrapper" :style="{ height: containerHeight }">
      <div v-if="loading" class="markdown-loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>

      <div v-else-if="error" class="markdown-error">
        <el-icon class="error-icon"><WarningFilled /></el-icon>
        <p>{{ error }}</p>
      </div>

      <template v-else>
        <!-- 目录侧边栏（仅在预览和分屏模式下显示） -->
        <div
          v-if="(viewMode === 'preview' || viewMode === 'split') && toc.length > 0"
          class="toc-sidebar"
          :class="{ collapsed: tocCollapsed }"
        >
          <div class="toc-header" @click="toggleToc">
            <el-icon><List /></el-icon>
            <span>目录</span>
            <el-icon class="toggle-icon" :class="{ rotated: tocCollapsed }">
              <ArrowLeft />
            </el-icon>
          </div>
          <div v-show="!tocCollapsed" class="toc-content">
            <div
              v-for="item in toc"
              :key="item.id"
              class="toc-item"
              :class="{
                'toc-level-1': item.level === 1,
                'toc-level-2': item.level === 2,
                'toc-level-3': item.level === 3,
                'toc-level-4': item.level === 4,
                'toc-level-5': item.level === 5,
                'toc-level-6': item.level === 6,
                'active': activeHeading === item.id
              }"
              @click="scrollToHeading(item.id)"
            >
              {{ item.text }}
            </div>
          </div>
        </div>

        <!-- 预览模式容器 -->
        <div v-if="viewMode === 'preview'" class="preview-container" :class="{ 'no-toc': toc.length === 0 }">
          <div class="markdown-preview" ref="previewRef">
            <div class="markdown-body" v-html="renderedContent"></div>
          </div>
        </div>

        <!-- 源码模式容器 -->
        <div v-else-if="viewMode === 'source'" class="source-container">
          <div class="markdown-source">
            <div class="source-header">
              <div class="window-controls">
                <span class="control-dot red"></span>
                <span class="control-dot yellow"></span>
                <span class="control-dot green"></span>
              </div>
              <span class="source-meta">{{ lineCount }} 行</span>
            </div>
            <pre><code class="language-markdown">{{ markdownContent }}</code></pre>
          </div>
        </div>

        <!-- 分屏模式容器 -->
        <div v-else class="split-container">
          <div class="markdown-split">
            <div class="split-pane source-pane">
              <div class="pane-title">源码</div>
              <pre><code class="language-markdown">{{ markdownContent }}</code></pre>
            </div>
            <div class="split-pane preview-pane" ref="splitPreviewRef">
              <div class="pane-title">预览</div>
              <div class="markdown-body" v-html="renderedContent"></div>
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- 底部信息 -->
    <div class="markdown-footer">
      <div class="markdown-meta-info">
        <span class="meta-item">
          <div class="icon-wrapper">
            <FileIcons :name="fileInfo.name" :width="16" :height="16" />
          </div>
          <span>Markdown</span>
        </span>
        <span class="meta-item">
          <el-icon><DataLine /></el-icon>
          <span>{{ charCount }} 字符</span>
        </span>
        <span class="meta-item">
          <el-icon><DataLine /></el-icon>
          <span>{{ formatFileSize(fileInfo.size) }}</span>
        </span>
        <span v-if="toc.length > 0" class="meta-item">
          <el-icon><List /></el-icon>
          <span>{{ toc.length }} 标题</span>
        </span>
      </div>
    </div>

    <!-- 图片预览弹窗 -->
    <teleport to="body">
      <div
        v-if="showImagePreview"
        class="image-preview-overlay"
        @click="closeImagePreview"
      >
        <div class="image-preview-container" @click.stop>
          <img :src="previewImageSrc" alt="预览图片" />
          <div class="image-preview-close" @click="closeImagePreview">
            <el-icon><Close /></el-icon>
          </div>
          <div class="image-preview-nav" v-if="imageList.length > 1">
            <div
              class="nav-btn prev"
              :class="{ disabled: currentImageIndex <= 0 }"
              @click.stop="prevImage"
            >
              <el-icon><ArrowLeft /></el-icon>
            </div>
            <div class="image-counter">
              {{ currentImageIndex + 1 }} / {{ imageList.length }}
            </div>
            <div
              class="nav-btn next"
              :class="{ disabled: currentImageIndex >= imageList.length - 1 }"
              @click.stop="nextImage"
            >
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>
    </teleport>
  </div>
</template>

<script setup lang="ts">
import {
  ref,
  computed,
  onMounted,
  onUnmounted,
  nextTick,
  watch
} from 'vue'
import {
  ElButton,
  ElButtonGroup,
  ElIcon,
  ElTag,
  ElRadioGroup,
  ElRadioButton,
  ElMessage
} from 'element-plus'
import {
  CopyDocument,
  Download,
  Loading,
  WarningFilled,
  DataLine,
  Close,
  List,
  ArrowLeft,
  ArrowRight
} from '@element-plus/icons-vue'
import FileIcons from 'file-icons-vue'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'
import { formatFileSize, getFileId, copyToClipboard } from './utils'

interface PreviewFileInfo {
  id?: string
  name: string
  size: number
  type: string
  url?: string
  file?: File
  response?: any
}

interface TocItem {
  id: string
  level: number
  text: string
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

const markdownContent = ref('')
const viewMode = ref<'preview' | 'source' | 'split'>('preview')
const loading = ref(false)
const error = ref('')
const showImagePreview = ref(false)
const previewImageSrc = ref('')
const imageList = ref<string[]>([])
const currentImageIndex = ref(0)
const toc = ref<TocItem[]>([])
const tocCollapsed = ref(false)
const activeHeading = ref('')
const previewRef = ref<HTMLElement>()
const splitPreviewRef = ref<HTMLElement>()

const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight: (str, lang) => {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(str, { language: lang }).value
      } catch (__) {}
    }
    return ''
  }
})

const originalHeadingOpen = md.renderer.rules.heading_open
md.renderer.rules.heading_open = (tokens, idx, options, env, self) => {
  const token = tokens[idx]
  const level = parseInt(token.tag.slice(1), 10)
  const nextToken = tokens[idx + 1]

  if (nextToken && nextToken.type === 'inline') {
    const text = nextToken.content
    const id = generateHeadingId(text, level)
    token.attrSet('id', id)
    token.attrSet('class', 'markdown-heading')
    token.attrSet('data-level', level.toString())
  }

  return originalHeadingOpen
    ? originalHeadingOpen(tokens, idx, options, env, self)
    : self.renderToken(tokens, idx, options)
}

md.renderer.rules.image = (tokens, idx, options, env, self) => {
  const token = tokens[idx]
  const src = token.attrGet('src') || ''
  const alt = token.content || ''
  const title = token.attrGet('title') || ''

  return `<img
    src="${src}"
    alt="${alt}"
    ${title ? `title="${title}"` : ''}
    class="markdown-image"
    data-src="${src}"
    loading="lazy"
  />`
}

function generateHeadingId(text: string, level: number): string {
  const baseId = text
    .toLowerCase()
    .replace(/[^\w\s-]/g, '')
    .replace(/\s+/g, '-')
    .substring(0, 50)
  return `heading-${level}-${baseId}`
}

function extractToc(content: string): TocItem[] {
  const items: TocItem[] = []
  const headingRegex = /<h([1-6])[^>]*id="([^"]*)"[^>]*>(.*?)<\/h[1-6]>/gi
  let match

  while ((match = headingRegex.exec(content)) !== null) {
    const level = parseInt(match[1], 10)
    const id = match[2]
    const text = match[3].replace(/<[^>]+>/g, '')
    items.push({ id, level, text })
  }

  return items
}

function extractImages(content: string): string[] {
  const images: string[] = []
  const imgRegex = /<img[^>]*data-src="([^"]*)"[^>]*>/gi
  let match

  while ((match = imgRegex.exec(content)) !== null) {
    images.push(match[1])
  }

  return images
}

const renderedContent = computed(() => {
  return md.render(markdownContent.value)
})

const containerHeight = computed(() => {
  return props.containerHeight || 'calc(100% - 100px)'
})

const charCount = computed(() => {
  return markdownContent.value.length
})

const lineCount = computed(() => {
  return markdownContent.value.split('\n').length
})

onMounted(async () => {
  await loadMarkdown()
})

onUnmounted(() => {
  const previewContainer = previewRef.value?.parentElement
  const splitContainer = splitPreviewRef.value?.closest('.split-container')

  if (previewContainer) {
    previewContainer.removeEventListener('scroll', handleScroll)
  }
  if (splitContainer) {
    splitContainer.removeEventListener('scroll', handleScroll)
  }
})

watch(renderedContent, async () => {
  await nextTick()

  toc.value = extractToc(renderedContent.value)
  imageList.value = extractImages(renderedContent.value)
  bindImageClickEvents()
  highlightCode()
  bindScrollEvents()
})

watch(viewMode, async (newMode) => {
  if (newMode === 'source' || newMode === 'split') {
    await nextTick()
    highlightCode()
  }
  if (newMode === 'preview' || newMode === 'split') {
    await nextTick()
    bindImageClickEvents()
  }
  await nextTick()
  bindScrollEvents()
})

function bindScrollEvents() {
  const previewContainer = previewRef.value?.parentElement
  const splitContainer = splitPreviewRef.value?.closest('.split-container')

  if (previewContainer) {
    previewContainer.removeEventListener('scroll', handleScroll)
    previewContainer.addEventListener('scroll', handleScroll)
  }
  if (splitContainer) {
    splitContainer.removeEventListener('scroll', handleScroll)
    splitContainer.addEventListener('scroll', handleScroll)
  }
}

async function loadMarkdown() {
  loading.value = true
  error.value = ''

  try {
    if (props.fileInfo.url) {
      const response = await fetch(props.fileInfo.url)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      markdownContent.value = await response.text()
    }
    else if (props.fileInfo.file && props.fileInfo.file.size > 0) {
      markdownContent.value = await props.fileInfo.file.text()
    }
    else {
      const fileId = getFileId(props.fileInfo)
      if (fileId) {
        error.value = '无法获取Markdown内容'
        emit('error', error.value)
        return
      }
      error.value = '无法获取Markdown内容'
      emit('error', error.value)
      return
    }

    emit('load')
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Markdown加载失败'
    emit('error', error.value)
  } finally {
    loading.value = false
  }
}

function highlightCode() {
  document.querySelectorAll('pre code').forEach((block) => {
    hljs.highlightElement(block as HTMLElement)
  })
}

function bindImageClickEvents() {
  const previewContainer = viewMode.value === 'split'
    ? splitPreviewRef.value
    : previewRef.value

  if (!previewContainer) return

  const images = previewContainer.querySelectorAll('.markdown-image')
  images.forEach((img, index) => {
    img.addEventListener('click', () => {
      const src = img.getAttribute('data-src')
      if (src) {
        previewImageSrc.value = src
        currentImageIndex.value = index
        showImagePreview.value = true
      }
    })
  })
}

async function handleCopy() {
  const success = await copyToClipboard(markdownContent.value)
  if (success) {
    ElMessage.success('内容已复制到剪贴板')
  } else {
    ElMessage.error('复制失败')
  }
}

function handleDownload() {
  emit('download')
}

function closeImagePreview() {
  showImagePreview.value = false
  previewImageSrc.value = ''
}

function prevImage() {
  if (currentImageIndex.value > 0) {
    currentImageIndex.value--
    previewImageSrc.value = imageList.value[currentImageIndex.value]
  }
}

function nextImage() {
  if (currentImageIndex.value < imageList.value.length - 1) {
    currentImageIndex.value++
    previewImageSrc.value = imageList.value[currentImageIndex.value]
  }
}

function toggleToc() {
  tocCollapsed.value = !tocCollapsed.value
}

function scrollToHeading(id: string) {
  const previewContainer = viewMode.value === 'split'
    ? splitPreviewRef.value
    : previewRef.value

  if (!previewContainer) return

  const element = previewContainer.querySelector(`#${id}`)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth', block: 'start' })
    activeHeading.value = id
  }
}

function handleScroll(event: Event) {
  const target = event.target as HTMLElement

  if (!target.classList.contains('preview-container') &&
      !target.classList.contains('source-container') &&
      !target.classList.contains('split-container') &&
      !target.classList.contains('markdown-preview') &&
      !target.classList.contains('preview-pane')) {
    return
  }

  let scrollContainer = target
  if (target.classList.contains('preview-container') ||
      target.classList.contains('source-container') ||
      target.classList.contains('split-container')) {
    scrollContainer = target.querySelector('.markdown-preview') || target.querySelector('.preview-pane') || target
  }

  const headings = scrollContainer.querySelectorAll('.markdown-heading')
  let currentHeading = ''

  headings.forEach((heading) => {
    const rect = heading.getBoundingClientRect()
    const containerRect = scrollContainer.getBoundingClientRect()
    const relativeTop = rect.top - containerRect.top

    if (relativeTop <= 100) {
      currentHeading = heading.id
    }
  })

  if (currentHeading) {
    activeHeading.value = currentHeading
  }
}
</script>

<style scoped lang="scss">
.markdown-preview-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.markdown-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background-color: #ffffff;
  border-bottom: 1px solid #ebeef5;
  flex-shrink: 0;

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
    display: flex;
    align-items: center;
    gap: 12px;

    .el-radio-group {
      .el-radio-button__inner {
        padding: 6px 12px;
        font-size: 13px;
      }
    }

    .action-btns {
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
}

.markdown-content-wrapper {
  flex: 1;
  overflow: hidden;
  background-color: #fafafa;
  display: flex;
  position: relative;

  .markdown-loading,
  .markdown-error {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    width: 100%;
    gap: 12px;
    color: #909399;

    .el-icon {
      font-size: 32px;
    }

    &.markdown-error {
      color: #f56c6c;
    }
  }

  .toc-sidebar {
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 240px;
    background-color: #fff;
    border-right: 1px solid #ebeef5;
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    transition: width 0.3s ease;
    z-index: 10;

    &.collapsed {
      width: 48px;

      .toc-header {
        justify-content: center;
        padding: 12px 8px;

        span {
          display: none;
        }
      }
    }

    .toc-header {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 16px;
      border-bottom: 1px solid #ebeef5;
      cursor: pointer;
      user-select: none;
      transition: background-color 0.2s;
      flex-shrink: 0;

      &:hover {
        background-color: #f5f7fa;
      }

      .el-icon {
        font-size: 16px;
        color: #606266;
      }

      span {
        font-size: 14px;
        font-weight: 500;
        color: #303133;
        flex: 1;
      }

      .toggle-icon {
        font-size: 14px;
        color: #909399;
        transition: transform 0.3s ease;

        &.rotated {
          transform: rotate(180deg);
        }
      }
    }

    .toc-content {
      flex: 1;
      overflow-y: auto;
      padding: 8px 0;

      .toc-item {
        padding: 8px 16px;
        font-size: 13px;
        color: #606266;
        cursor: pointer;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        transition: all 0.2s;
        border-left: 3px solid transparent;

        &:hover {
          background-color: #f5f7fa;
          color: #409eff;
        }

        &.active {
          background-color: #ecf5ff;
          color: #409eff;
          border-left-color: #409eff;
        }

        &.toc-level-1 {
          font-weight: 600;
          color: #303133;
        }

        &.toc-level-2 {
          padding-left: 24px;
        }

        &.toc-level-3 {
          padding-left: 32px;
          font-size: 12px;
        }

        &.toc-level-4,
        &.toc-level-5,
        &.toc-level-6 {
          padding-left: 40px;
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }

  .preview-container {
    flex: 1;
    overflow-y: auto;
    padding: 24px;
    padding-left: 264px;
    transition: padding-left 0.3s ease;

    &.no-toc {
      padding-left: 24px;
    }

    .markdown-preview {
      max-width: 900px;
      margin: 0 auto;

      .markdown-body {
        background-color: #fff;
        padding: 32px;
        border-radius: 8px;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
        min-height: calc(100% - 64px);
      }
    }
  }

  .source-container {
    flex: 1;
    overflow-y: auto;
    padding: 16px;

    .markdown-source {
      max-width: 900px;
      margin: 0 auto;
      background-color: #fff;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
      border: 1px solid #ebeef5;

      .source-header {
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

        .source-meta {
          font-size: 12px;
          color: #909399;
          font-family: 'Consolas', 'Monaco', monospace;
        }
      }

      pre {
        margin: 0;
        padding: 16px;
        background-color: #fff;
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

  .split-container {
    flex: 1;
    overflow-y: auto;
    padding: 16px;

    .markdown-split {
      max-width: 100%;
      display: flex;
      overflow: hidden;
      gap: 16px;

      .split-pane {
        flex: 1;
        overflow-y: auto;
        padding: 16px;
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
        border: 1px solid #ebeef5;

        .pane-title {
          font-size: 12px;
          color: #909399;
          margin-bottom: 12px;
          text-transform: uppercase;
          letter-spacing: 1px;
          font-weight: 500;
        }

        pre {
          margin: 0;
          padding: 16px;
          background-color: #f6f8fa;
          border-radius: 6px;
          overflow: auto;

          code {
            font-family: 'JetBrains Mono', 'Consolas', 'Monaco', 'Courier New', monospace;
            font-size: 13px;
            line-height: 1.6;
          }
        }
      }

      .preview-pane {
        .markdown-body {
          padding: 8px;
        }
      }
    }
  }
}

.markdown-footer {
  padding: 10px 16px;
  background-color: #ffffff;
  border-top: 1px solid #ebeef5;
  flex-shrink: 0;

  .markdown-meta-info {
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

:deep(.markdown-body) {
  font-size: 16px;
  line-height: 1.6;
  color: #24292e;

  h1, h2, h3, h4, h5, h6 {
    margin-top: 24px;
    margin-bottom: 16px;
    font-weight: 600;
    line-height: 1.25;
    scroll-margin-top: 20px;
  }

  h1 { font-size: 2em; border-bottom: 1px solid #eaecef; padding-bottom: 0.3em; }
  h2 { font-size: 1.5em; border-bottom: 1px solid #eaecef; padding-bottom: 0.3em; }
  h3 { font-size: 1.25em; }
  h4 { font-size: 1em; }
  h5 { font-size: 0.875em; }
  h6 { font-size: 0.85em; color: #6a737d; }

  p {
    margin-top: 0;
    margin-bottom: 16px;
  }

  code {
    padding: 0.2em 0.4em;
    margin: 0;
    font-size: 85%;
    background-color: rgba(27, 31, 35, 0.05);
    border-radius: 3px;
    font-family: 'JetBrains Mono', 'Consolas', 'Monaco', 'Courier New', monospace;
  }

  pre {
    padding: 16px;
    overflow: auto;
    font-size: 85%;
    line-height: 1.45;
    background-color: #f6f8fa;
    border-radius: 6px;

    code {
      padding: 0;
      background-color: transparent;
    }
  }

  blockquote {
    padding: 0 1em;
    color: #6a737d;
    border-left: 0.25em solid #dfe2e5;
    margin: 0 0 16px 0;
  }

  ul, ol {
    padding-left: 2em;
    margin-top: 0;
    margin-bottom: 16px;
  }

  li + li {
    margin-top: 0.25em;
  }

  table {
    border-spacing: 0;
    border-collapse: collapse;
    margin-bottom: 16px;
    width: 100%;
    overflow: auto;
    display: block;

    th, td {
      padding: 6px 13px;
      border: 1px solid #dfe2e5;
    }

    th {
      font-weight: 600;
      background-color: #f6f8fa;
    }

    tr:nth-child(2n) {
      background-color: #f6f8fa;
    }
  }

  hr {
    height: 0.25em;
    padding: 0;
    margin: 24px 0;
    background-color: #e1e4e8;
    border: 0;
  }

  a {
    color: #0366d6;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }

  .markdown-image {
    max-width: 100%;
    box-sizing: content-box;
    background-color: #fff;
    cursor: zoom-in;
    transition: all 0.2s ease;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

    &:hover {
      opacity: 0.9;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    }
  }

  .task-list-item {
    list-style-type: none;

    input {
      margin-right: 8px;
    }
  }
}

.image-preview-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.92);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.2s ease;

  .image-preview-container {
    position: relative;
    max-width: 90vw;
    max-height: 90vh;
    display: flex;
    flex-direction: column;
    align-items: center;

    img {
      max-width: 100%;
      max-height: 80vh;
      object-fit: contain;
      border-radius: 8px;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
    }

    .image-preview-close {
      position: absolute;
      top: -48px;
      right: 0;
      color: #fff;
      font-size: 28px;
      cursor: pointer;
      padding: 8px;
      transition: all 0.2s;
      background-color: rgba(255, 255, 255, 0.1);
      border-radius: 50%;
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;

      &:hover {
        background-color: rgba(255, 255, 255, 0.2);
        transform: rotate(90deg);
      }
    }

    .image-preview-nav {
      display: flex;
      align-items: center;
      gap: 20px;
      margin-top: 20px;

      .nav-btn {
        width: 44px;
        height: 44px;
        border-radius: 50%;
        background-color: rgba(255, 255, 255, 0.1);
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        cursor: pointer;
        transition: all 0.2s;
        font-size: 20px;

        &:hover:not(.disabled) {
          background-color: rgba(255, 255, 255, 0.2);
          transform: scale(1.1);
        }

        &.disabled {
          opacity: 0.3;
          cursor: not-allowed;
        }
      }

      .image-counter {
        color: #fff;
        font-size: 14px;
        min-width: 60px;
        text-align: center;
        font-variant-numeric: tabular-nums;
      }
    }
  }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@media (max-width: 768px) {
  .markdown-toolbar {
    padding: 10px 12px;
    flex-wrap: wrap;
    gap: 10px;

    .toolbar-left {
      .file-info {
        .file-name {
          max-width: 150px;
        }
      }
    }

    .toolbar-right {
      .action-btns {
        .btn-text {
          display: none;
        }
      }
    }
  }

  .markdown-content-wrapper {
    .toc-sidebar {
      width: 200px;
    }

    .preview-container {
      padding: 16px;
      padding-left: 16px !important;
    }

    .split-container {
      .markdown-split {
        flex-direction: column;
      }
    }
  }

  .markdown-footer {
    padding: 8px 12px;

    .markdown-meta-info {
      gap: 12px;
      flex-wrap: wrap;
    }
  }

  :deep(.markdown-body) {
    padding: 20px !important;
  }
}
</style>