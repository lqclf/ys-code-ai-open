/**
 * YsUpload 文件上传工具模块（简化版）
 * 仅保留文件校验、附件管理等基础工具方法，去除分片/MD5相关
 * @author: Eric
 * @date: 2025-06-16
 */
import type { UploadFile, FileValidationRule, UploadError } from '../types/upload'
import { useFileUploadApi } from '../api/index'

/**
 * 文件上传工具类
 */
export class UploadUtils {
  /**
   * 生成唯一ID
   */
  static generateId(): string {
    const timestamp = Date.now().toString(36)
    const random1 = Math.random().toString(36).substr(2, 9)
    return `${timestamp}_${random1}`
  }

  /**
   * 格式化文件大小
   */
  static formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 B'
    const k = 1024
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return `${parseFloat((bytes / Math.pow(k, i)).toFixed(2))} ${sizes[i]}`
  }

  /**
   * 检查文件类型是否符合要求
   */
  static checkFileType(file: File, accept?: string): boolean {
    if (!accept) return true

    const acceptTypes = accept.split(',').map((type) => type.trim())

    for (const acceptType of acceptTypes) {
      if (acceptType.startsWith('.')) {
        const ext = '.' + file.name.split('.').pop()?.toLowerCase()
        if (ext === acceptType.toLowerCase()) {
          return true
        }
      } else if (acceptType.includes('*')) {
        const pattern = acceptType.replace('*', '.*')
        const regex = new RegExp(pattern)
        if (regex.test(file.type)) {
          return true
        }
      } else {
        if (file.type === acceptType) {
          return true
        }
      }
    }

    return false
  }

  /**
   * 验证文件
   */
  static validateFile(file: File, rules: FileValidationRule): UploadError | null {
    if (rules.accept && !this.checkFileType(file, rules.accept.join(','))) {
      return {
        code: 'INVALID_FILE_TYPE',
        message: `文件类型不支持，只支持: ${rules.accept.join(', ')}`,
      }
    }

    if (rules.maxSize && file.size > rules.maxSize) {
      return {
        code: 'FILE_SIZE_EXCEEDED',
        message: `文件大小超过限制，最大允许: ${this.formatFileSize(rules.maxSize)}`,
      }
    }

    if (rules.minSize && file.size < rules.minSize) {
      return {
        code: 'FILE_SIZE_TOO_SMALL',
        message: `文件大小过小，最小要求: ${this.formatFileSize(rules.minSize)}`,
      }
    }

    return null
  }

  /**
   * 创建上传文件对象
   */
  static createUploadFile(file: File): UploadFile {
    return {
      id: this.generateId(),
      name: file.name,
      originalName: file.name,
      size: file.size,
      type: file.type,
      file,
      status: 'ready',
      progress: 0,
    }
  }

  /**
   * 获取文件扩展名
   */
  static getFileExtension(filename: string): string {
    const lastDotIndex = filename.lastIndexOf('.')
    return lastDotIndex !== -1 ? filename.substring(lastDotIndex + 1).toLowerCase() : ''
  }

  /**
   * 下载Blob文件
   */
  static downloadBlob(blob: Blob, filename: string): void {
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = filename
    link.style.display = 'none'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
  }
}

/**
 * 文件附件管理工具类
 */
export class FileAttachmentManager {
  private fileUploadApi = useFileUploadApi()

  /**
   * 从文件列表中提取成功的文件ID集合
   */
  extractFileIds(fileList: UploadFile[]): string[] {
    return fileList
      .filter((file) => file.status === 'success' && this.getFileId(file))
      .map((file) => this.getFileId(file)!)
      .filter(Boolean)
  }

  /**
   * 将文件ID集合转换为JSON字符串
   */
  fileIdsToJson(fileIds: string[]): string {
    return JSON.stringify(fileIds)
  }

  /**
   * 从JSON字符串中解析文件ID集合
   */
  fileIdsFromJson(jsonStr: string): string[] {
    try {
      const parsed = JSON.parse(jsonStr)
      if (Array.isArray(parsed)) {
        return parsed.filter((id) => typeof id === 'string' && id.trim())
      }
      return []
    } catch (error) {
      console.warn('解析文件ID JSON失败:', error, jsonStr)
      return []
    }
  }

  /**
   * 根据文件ID集合获取文件信息
   */
  async getFilesForDisplay(fileIds: string[]): Promise<UploadFile[]> {
    if (!fileIds || fileIds.length === 0) {
      return []
    }

    try {
      const response = await this.fileUploadApi.getFilesByIds(fileIds)

      if (response.code === 200 && response.data) {
        return response.data.map((fileInfo: any, index: number) =>
          this.convertToUploadFile(fileInfo, index),
        )
      }

      return []
    } catch (error) {
      console.error('获取文件信息失败:', error)
      return []
    }
  }

  /**
   * 将服务端文件信息转换为UploadFile格式
   */
  private convertToUploadFile(fileInfo: any, index: number): UploadFile {
    const fileId = fileInfo.fileId || fileInfo.id
    const fileName = fileInfo.originalFilename || fileInfo.fileName || ''

    return {
      id: `file_${Date.now()}_${index}`,
      fileId: fileId,
      name: fileName,
      size: fileInfo.fileSize || 0,
      type: fileInfo.fileType || 'application/octet-stream',
      file: undefined as any,
      status: 'success' as const,
      progress: 100,
      response: {
        data: fileInfo,
      },
    }
  }

  /**
   * 统一的文件ID获取方法
   */
  private getFileId(file: UploadFile): string | null {
    return (
      file.fileId ||
      file.response?.data?.fileId ||
      file.response?.data?.id ||
      file.response?.id ||
      null
    )
  }

  /**
   * 从附件数据中提取文件信息
   */
  parseAttachmentData(attachmentData: string): string[] {
    if (!attachmentData || !attachmentData.trim()) {
      return []
    }

    try {
      const parsed = JSON.parse(attachmentData)

      if (Array.isArray(parsed) && parsed.length > 0 && typeof parsed[0] === 'object') {
        return parsed.map((item) => item.id || item.fileId).filter(Boolean)
      }

      if (Array.isArray(parsed)) {
        return parsed.filter((id) => typeof id === 'string' && id.trim())
      }

      return []
    } catch (error) {
      return attachmentData
        .split(',')
        .map((id) => id.trim())
        .filter(Boolean)
    }
  }

  /**
   * 创建附件数据的完整JSON格式
   */
  createAttachmentJson(fileList: UploadFile[]): string {
    const attachments = fileList
      .filter((file) => file.status === 'success' && this.getFileId(file))
      .map((file) => ({
        id: this.getFileId(file),
        name: file.name,
        url: file.url,
        size: file.size,
        type: file.type,
        fileId: this.getFileId(file),
      }))

    return JSON.stringify(attachments)
  }
}

/**
 * 创建文件附件管理器实例
 */
export function useFileAttachmentManager() {
  return new FileAttachmentManager()
}

/**
 * 业务模块文件管理的快捷方法
 */
export const FileAttachmentUtils = {
  /**
   * 从业务表单中提取文件ID集合并保存
   */
  saveFileIds: (fileList: UploadFile[]): string => {
    const manager = new FileAttachmentManager()
    const fileIds = manager.extractFileIds(fileList)
    return manager.fileIdsToJson(fileIds)
  },

  /**
   * 加载文件ID集合并转换为文件列表用于回显
   */
  loadFiles: async (attachmentData: string): Promise<UploadFile[]> => {
    const manager = new FileAttachmentManager()
    const fileIds = manager.parseAttachmentData(attachmentData)
    return await manager.getFilesForDisplay(fileIds)
  },

  /**
   * 创建完整的附件JSON数据
   */
  createAttachmentData: (fileList: UploadFile[]): string => {
    const manager = new FileAttachmentManager()
    return manager.createAttachmentJson(fileList)
  },
}
