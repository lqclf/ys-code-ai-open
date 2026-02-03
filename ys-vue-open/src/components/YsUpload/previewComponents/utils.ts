/**
 * 文件预览工具函数
 * @author: Eric
 * @date: 2025-01-30
 */

/**
 * 支持的预览类型
 */
export type PreviewType =
  | 'image'      // 图片
  | 'pdf'        // PDF
  | 'video'      // 视频
  | 'audio'      // 音频
  | 'text'       // 纯文本
  | 'code'       // 代码文件
  | 'markdown'   // Markdown
  | 'word'       // Word文档
  | 'excel'      // Excel表格
  | 'powerpoint' // PPT
  | 'unsupported' // 不支持

/**
 * 代码文件类型
 */
export type CodeFileType = 'javascript' | 'typescript' | 'css' | 'html' | 'java' | 'json' | 'yaml' | 'xml' | 'sql' | 'python' | 'cpp' | 'csharp' | 'php' | 'go' | 'rust' | 'shell' | 'plaintext'

/**
 * 预览文件信息
 */
export interface PreviewFileInfo {
  /** 文件ID */
  id?: string
  /** 文件名 */
  name: string
  /** 文件大小 */
  size: number
  /** 文件类型 */
  type: string
  /** 文件URL */
  url?: string
  /** 原始文件对象 */
  file?: File
  /** 后端返回的响应数据 */
  response?: any
}

/**
 * 文件类型配置
 */
export interface FileTypeConfig {
  /** 类型名称 */
  name: string
  /** MIME类型列表 */
  mimeTypes: string[]
  /** 文件扩展名列表 */
  extensions: string[]
  /** 对应的预览类型 */
  previewType: PreviewType
  /** 图标名称 */
  icon: string
}

/**
 * 文件类型配置映射表
 */
export const FILE_TYPE_CONFIGS: FileTypeConfig[] = [
  // 图片类型
  {
    name: '图片',
    mimeTypes: ['image/jpeg', 'image/png', 'image/gif', 'image/bmp', 'image/webp', 'image/svg+xml', 'image/tiff'],
    extensions: ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'svg', 'tiff', 'tif'],
    previewType: 'image',
    icon: 'Picture'
  },
  // PDF类型
  {
    name: 'PDF文档',
    mimeTypes: ['application/pdf'],
    extensions: ['pdf'],
    previewType: 'pdf',
    icon: 'Document'
  },
  // 视频类型
  {
    name: '视频',
    mimeTypes: ['video/mp4', 'video/webm', 'video/ogg', 'video/quicktime', 'video/x-msvideo', 'video/x-matroska'],
    extensions: ['mp4', 'webm', 'ogg', 'mov', 'avi', 'mkv'],
    previewType: 'video',
    icon: 'VideoCamera'
  },
  // 音频类型
  {
    name: '音频',
    mimeTypes: ['audio/mpeg', 'audio/wav', 'audio/ogg', 'audio/aac', 'audio/flac', 'audio/m4a'],
    extensions: ['mp3', 'wav', 'ogg', 'aac', 'flac', 'm4a'],
    previewType: 'audio',
    icon: 'Headset'
  },
  // 文本类型
  {
    name: '文本文件',
    mimeTypes: ['text/plain'],
    extensions: ['txt'],
    previewType: 'text',
    icon: 'Document'
  },
  // 代码文件类型
  {
    name: 'JavaScript',
    mimeTypes: ['application/javascript', 'text/javascript'],
    extensions: ['js'],
    previewType: 'code',
    icon: 'Document'
  },
  {
    name: 'TypeScript',
    mimeTypes: ['application/typescript'],
    extensions: ['ts', 'tsx'],
    previewType: 'code',
    icon: 'Document'
  },
  {
    name: 'CSS',
    mimeTypes: ['text/css'],
    extensions: ['css', 'scss', 'sass', 'less'],
    previewType: 'code',
    icon: 'Document'
  },
  {
    name: 'HTML',
    mimeTypes: ['text/html'],
    extensions: ['html', 'htm'],
    previewType: 'code',
    icon: 'Document'
  },
  {
    name: 'Java',
    mimeTypes: ['text/x-java'],
    extensions: ['java'],
    previewType: 'code',
    icon: 'Document'
  },
  {
    name: 'JSON',
    mimeTypes: ['application/json'],
    extensions: ['json'],
    previewType: 'code',
    icon: 'Document'
  },
  {
    name: 'YAML',
    mimeTypes: ['application/x-yaml', 'text/yaml'],
    extensions: ['yaml', 'yml'],
    previewType: 'code',
    icon: 'Document'
  },
  {
    name: 'XML',
    mimeTypes: ['application/xml', 'text/xml'],
    extensions: ['xml'],
    previewType: 'code',
    icon: 'Document'
  },
  {
    name: 'SQL',
    mimeTypes: ['application/sql'],
    extensions: ['sql'],
    previewType: 'code',
    icon: 'Document'
  },
  {
    name: 'Python',
    mimeTypes: ['text/x-python'],
    extensions: ['py'],
    previewType: 'code',
    icon: 'Document'
  },
  {
    name: 'C/C++',
    mimeTypes: ['text/x-c'],
    extensions: ['c', 'cpp', 'cc', 'h', 'hpp'],
    previewType: 'code',
    icon: 'Document'
  },
  {
    name: 'C#',
    mimeTypes: ['text/x-csharp'],
    extensions: ['cs'],
    previewType: 'code',
    icon: 'Document'
  },
  {
    name: 'PHP',
    mimeTypes: ['text/x-php'],
    extensions: ['php'],
    previewType: 'code',
    icon: 'Document'
  },
  {
    name: 'Go',
    mimeTypes: ['text/x-go'],
    extensions: ['go'],
    previewType: 'code',
    icon: 'Document'
  },
  {
    name: 'Rust',
    mimeTypes: ['text/x-rust'],
    extensions: ['rs'],
    previewType: 'code',
    icon: 'Document'
  },
  {
    name: 'Shell',
    mimeTypes: ['text/x-shellscript'],
    extensions: ['sh', 'bash', 'zsh'],
    previewType: 'code',
    icon: 'Document'
  },
  // Markdown类型
  {
    name: 'Markdown',
    mimeTypes: ['text/markdown'],
    extensions: ['md', 'markdown'],
    previewType: 'markdown',
    icon: 'Document'
  },
  // Word类型
  {
    name: 'Word文档',
    mimeTypes: ['application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'application/msword'],
    extensions: ['docx', 'doc'],
    previewType: 'word',
    icon: 'Document'
  },
  // Excel类型
  {
    name: 'Excel表格',
    mimeTypes: ['application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'application/vnd.ms-excel'],
    extensions: ['xlsx', 'xls'],
    previewType: 'excel',
    icon: 'Document'
  },
  // PPT类型
  {
    name: 'PPT演示文稿',
    mimeTypes: ['application/vnd.openxmlformats-officedocument.presentationml.presentation', 'application/vnd.ms-powerpoint'],
    extensions: ['pptx', 'ppt'],
    previewType: 'powerpoint',
    icon: 'Document'
  }
]

/**
 * 根据文件名获取预览类型
 * @param fileName - 文件名
 * @param mimeType - MIME类型（可选）
 * @returns 预览类型
 */
export function getPreviewType(fileName: string, mimeType?: string): PreviewType {
  const ext = getFileExtension(fileName).toLowerCase()

  // 首先根据扩展名匹配
  for (const config of FILE_TYPE_CONFIGS) {
    if (config.extensions.includes(ext)) {
      return config.previewType
    }
  }

  // 如果没有匹配到，尝试根据MIME类型匹配
  if (mimeType) {
    for (const config of FILE_TYPE_CONFIGS) {
      if (config.mimeTypes.includes(mimeType)) {
        return config.previewType
      }
    }
  }

  return 'unsupported'
}

/**
 * 获取文件扩展名
 * @param fileName - 文件名
 * @returns 扩展名（不含点）
 */
export function getFileExtension(fileName: string): string {
  if (!fileName) return ''
  const lastDotIndex = fileName.lastIndexOf('.')
  return lastDotIndex === -1 ? '' : fileName.slice(lastDotIndex + 1)
}

/**
 * 获取文件类型配置
 * @param fileName - 文件名
 * @returns 文件类型配置
 */
export function getFileTypeConfig(fileName: string): FileTypeConfig | undefined {
  const ext = getFileExtension(fileName).toLowerCase()
  return FILE_TYPE_CONFIGS.find(config => config.extensions.includes(ext))
}

/**
 * 根据文件名获取代码语言
 * @param fileName - 文件名
 * @returns 代码语言标识
 */
export function getCodeLanguage(fileName: string): CodeFileType {
  const ext = getFileExtension(fileName).toLowerCase()

  const languageMap: Record<string, CodeFileType> = {
    'js': 'javascript',
    'ts': 'typescript',
    'tsx': 'typescript',
    'css': 'css',
    'scss': 'css',
    'sass': 'css',
    'less': 'css',
    'html': 'html',
    'htm': 'html',
    'java': 'java',
    'json': 'json',
    'yaml': 'yaml',
    'yml': 'yaml',
    'xml': 'xml',
    'sql': 'sql',
    'py': 'python',
    'c': 'cpp',
    'cpp': 'cpp',
    'cc': 'cpp',
    'h': 'cpp',
    'hpp': 'cpp',
    'cs': 'csharp',
    'php': 'php',
    'go': 'go',
    'rs': 'rust',
    'sh': 'shell',
    'bash': 'shell',
    'zsh': 'shell'
  }

  return languageMap[ext] || 'plaintext'
}

/**
 * 格式化文件大小
 * @param bytes - 字节数
 * @returns 格式化后的字符串
 */
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'

  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  const k = 1024
  const i = Math.floor(Math.log(bytes) / Math.log(k))

  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + units[i]
}

/**
 * 格式化日期时间
 * @param date - 日期对象或时间戳
 * @returns 格式化后的字符串
 */
export function formatDateTime(date: Date | number): string {
  const d = typeof date === 'number' ? new Date(date) : date
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  const second = String(d.getSeconds()).padStart(2, '0')

  return `${year}-${month}-${day} ${hour}:${minute}:${second}`
}

/**
 * 获取文件图标
 * @param fileInfo - 文件信息
 * @returns 图标名称
 */
export function getFileIcon(fileInfo: PreviewFileInfo): string {
  const config = getFileTypeConfig(fileInfo.name)
  return config?.icon || 'Document'
}

/**
 * 获取文件类型名称
 * @param fileInfo - 文件信息
 * @returns 类型名称
 */
export function getFileTypeName(fileInfo: PreviewFileInfo): string {
  const config = getFileTypeConfig(fileInfo.name)
  return config?.name || '未知类型'
}

/**
 * 复制文本到剪贴板
 * @param text - 要复制的文本
 * @returns 是否复制成功
 */
export async function copyToClipboard(text: string): Promise<boolean> {
  try {
    await navigator.clipboard.writeText(text)
    return true
  } catch (err) {
    // 降级方案
    const textarea = document.createElement('textarea')
    textarea.value = text
    textarea.style.position = 'fixed'
    textarea.style.opacity = '0'
    document.body.appendChild(textarea)
    textarea.select()

    try {
      document.execCommand('copy')
      return true
    } catch (e) {
      return false
    } finally {
      document.body.removeChild(textarea)
    }
  }
}

/**
 * 下载文件
 * @param url - 文件URL
 * @param fileName - 文件名
 */
export function downloadFile(url: string, fileName: string): void {
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  link.style.display = 'none'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

/**
 * 创建Blob URL
 * @param blob - Blob对象或File对象
 * @returns Blob URL
 */
export function createBlobUrl(blob: Blob | File): string {
  return URL.createObjectURL(blob)
}

/**
 * 释放Blob URL
 * @param url - Blob URL
 */
export function revokeBlobUrl(url: string): void {
  if (url && url.startsWith('blob:')) {
    URL.revokeObjectURL(url)
  }
}

/**
 * 获取文件ID
 * @param fileInfo - 文件信息
 * @returns 文件ID
 */
export function getFileId(fileInfo: PreviewFileInfo): string | undefined {
  return fileInfo.id || fileInfo.response?.data?.id
}

