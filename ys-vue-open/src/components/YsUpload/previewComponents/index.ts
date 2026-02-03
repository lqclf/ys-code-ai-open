/**
 * 文件预览组件统一导出
 * @author: AI Assistant
 * @date: 2025-01-30
 */

// 导出类型定义（现在直接从utils导出）
export type {
  PreviewFileInfo,
  PreviewType,
  CodeFileType,
  FileTypeConfig
} from './utils'

// 导出工具函数
export {
  FILE_TYPE_CONFIGS,
  getPreviewType,
  getFileExtension,
  getFileTypeConfig,
  getCodeLanguage,
  formatFileSize,
  formatDateTime,
  getFileIcon,
  getFileTypeName,
  copyToClipboard,
  downloadFile,
  createBlobUrl,
  revokeBlobUrl,
  getFileId
} from './utils'

// 导出预览组件（用于动态导入）
export { default as ImagePreview } from './ImagePreview.vue'
export { default as VideoPreview } from './VideoPreview.vue'
export { default as AudioPreview } from './AudioPreview.vue'
export { default as PdfPreview } from './PdfPreview.vue'
export { default as TextPreview } from './TextPreview.vue'
export { default as CodePreview } from './CodePreview.vue'
export { default as MarkdownPreview } from './MarkdownPreview.vue'
export { default as WordPreview } from './WordPreview.vue'
export { default as UnsupportedPreview } from './UnsupportedPreview.vue'
