import SparkMD5 from 'spark-md5'
import type { UploadFile, ChunkInfo, FileValidationRule, UploadError } from '../types/upload'
import { useFileUploadApi } from '../api/index'

/**
 * 文件上传工具类
 */
export class UploadUtils {
  
  /**
   * 生成唯一ID
   */
  static generateId(): string {
    // 使用更强的唯一性：时间戳 + 随机数 + 计数器
    const timestamp = Date.now().toString(36)
    const random1 = Math.random().toString(36).substr(2, 9)
    const random2 = Math.random().toString(36).substr(2, 9)
    return `${timestamp}_${random1}_${random2}`
  }

  /**
   * 生成文件唯一标识（使用MD5哈希）
   * 基于文件名、大小、最后修改时间和随机数生成固定32位长度的fileKey
   */
  static generateFileKey(file: File): string {
    const metadata = `${file.name}_${file.size}_${file.lastModified}_${this.generateId()}`
    return this.hashString(metadata)
  }

  /**
   * 字符串MD5哈希（用于生成fileKey）
   */
  static hashString(str: string): string {
    const spark = new SparkMD5.ArrayBuffer()
    const encoder = new TextEncoder()
    const data = encoder.encode(str)
    spark.append(data.buffer)
    return spark.end()
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
   * 计算文件MD5值
   */
  static calculateMD5(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const spark = new SparkMD5.ArrayBuffer()
      const fileReader = new FileReader()
      
      fileReader.onload = (e) => {
        if (e.target?.result) {
          spark.append(e.target.result as ArrayBuffer)
          resolve(spark.end())
        } else {
          reject(new Error('读取文件失败'))
        }
      }
      
      fileReader.onerror = () => {
        reject(new Error('计算MD5失败'))
      }
      
      fileReader.readAsArrayBuffer(file)
    })
  }

  /**
   * 分片计算文件MD5值（适用于大文件）
   */
  static calculateMD5WithChunks(file: File, chunkSize: number = 2 * 1024 * 1024): Promise<string> {
    return new Promise((resolve, reject) => {
      const spark = new SparkMD5.ArrayBuffer()
      const fileReader = new FileReader()
      const chunks = Math.ceil(file.size / chunkSize)
      let currentChunk = 0
      
      const loadNext = () => {
        const start = currentChunk * chunkSize
        const end = Math.min(start + chunkSize, file.size)
        
        // 使用 slice 方法读取文件分片，确保数据完整性
        const blob = file.slice(start, end)
        fileReader.readAsArrayBuffer(blob)
      }
      
      fileReader.onload = (e) => {
        if (e.target?.result) {
          spark.append(e.target.result as ArrayBuffer)
          currentChunk++
          
          if (currentChunk < chunks) {
            // 使用 setTimeout 避免阻塞主线程
            setTimeout(loadNext, 0)
          } else {
            resolve(spark.end())
          }
        } else {
          reject(new Error('读取文件失败'))
        }
      }
      
      fileReader.onerror = () => {
        reject(new Error('计算MD5失败'))
      }
      
      loadNext()
    })
  }

  /**
   * 验证文件MD5
   */
  static async verifyFileMD5(file: File, expectedMd5: string): Promise<boolean> {
    try {
      const actualMd5 = await this.calculateMD5WithChunks(file)
      return actualMd5 === expectedMd5
    } catch (error) {
      console.error('验证文件MD5失败:', error)
      return false
    }
  }

  /**
   * 检查文件类型是否符合要求
   */
  static checkFileType(file: File, accept?: string): boolean {
    if (!accept) return true
    
    const acceptTypes = accept.split(',').map(type => type.trim())
    
    for (const acceptType of acceptTypes) {
      if (acceptType.startsWith('.')) {
        // 扩展名匹配
        const ext = '.' + file.name.split('.').pop()?.toLowerCase()
        if (ext === acceptType.toLowerCase()) {
          return true
        }
      } else if (acceptType.includes('*')) {
        // MIME类型通配符匹配
        const pattern = acceptType.replace('*', '.*')
        const regex = new RegExp(pattern)
        if (regex.test(file.type)) {
          return true
        }
      } else {
        // 精确MIME类型匹配
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
    // 检查文件类型
    if (rules.accept && !this.checkFileType(file, rules.accept.join(','))) {
      return {
        code: 'INVALID_FILE_TYPE',
        message: `文件类型不支持，只支持: ${rules.accept.join(', ')}`
      }
    }
    
    // 检查文件大小
    if (rules.maxSize && file.size > rules.maxSize) {
      return {
        code: 'FILE_SIZE_EXCEEDED',
        message: `文件大小超过限制，最大允许: ${this.formatFileSize(rules.maxSize)}`
      }
    }
    
    if (rules.minSize && file.size < rules.minSize) {
      return {
        code: 'FILE_SIZE_TOO_SMALL',
        message: `文件大小过小，最小要求: ${this.formatFileSize(rules.minSize)}`
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
      progress: 0
    }
  }

  /**
   * 生成分片信息
   */
  static generateChunks(file: File, chunkSize: number): ChunkInfo[] {
    const chunks: ChunkInfo[] = []
    const totalChunks = Math.ceil(file.size / chunkSize)
    
    for (let i = 0; i < totalChunks; i++) {
      const start = i * chunkSize
      const end = Math.min(start + chunkSize, file.size)
      
      chunks.push({
        index: i,
        start,
        end,
        size: end - start,
        uploaded: false,
        retryCount: 0
      })
    }
    
    return chunks
  }

  /**
   * 创建分片文件
   */
  static createChunkFile(file: File, chunk: ChunkInfo): File {
    const blob = file.slice(chunk.start, chunk.end)
    return new File([blob], `${file.name}.chunk.${chunk.index}`, {
      type: file.type
    })
  }

  /**
   * 计算上传速度
   */
  static calculateSpeed(uploadedSize: number, startTime: number): number {
    const elapsedTime = (Date.now() - startTime) / 1000 // 秒
    return elapsedTime > 0 ? uploadedSize / elapsedTime : 0
  }

  /**
   * 格式化上传速度
   */
  static formatSpeed(bytesPerSecond: number): string {
    return `${this.formatFileSize(bytesPerSecond)}/s`
  }

  /**
   * 计算剩余时间
   */
  static calculateRemainingTime(totalSize: number, uploadedSize: number, speed: number): number {
    if (speed <= 0) return 0
    const remainingBytes = totalSize - uploadedSize
    return remainingBytes / speed
  }

  /**
   * 格式化剩余时间
   */
  static formatRemainingTime(seconds: number): string {
    if (seconds <= 0) return '0秒'
    
    const hours = Math.floor(seconds / 3600)
    const minutes = Math.floor((seconds % 3600) / 60)
    const secs = Math.floor(seconds % 60)
    
    if (hours > 0) {
      return `${hours}小时${minutes}分钟`
    } else if (minutes > 0) {
      return `${minutes}分钟${secs}秒`
    } else {
      return `${secs}秒`
    }
  }

  /**
   * 判断是否为图片文件
   */
  static isImageFile(file: File): boolean {
    return file.type.startsWith('image/')
  }

  /**
   * 判断是否为视频文件
   */
  static isVideoFile(file: File): boolean {
    return file.type.startsWith('video/')
  }

  /**
   * 判断是否为音频文件
   */
  static isAudioFile(file: File): boolean {
    return file.type.startsWith('audio/')
  }

  /**
   * 获取文件扩展名
   */
  static getFileExtension(filename: string): string {
    const lastDotIndex = filename.lastIndexOf('.')
    return lastDotIndex !== -1 ? filename.substring(lastDotIndex + 1).toLowerCase() : ''
  }

  /**
   * 获取文件类型图标
   */
  static getFileTypeIcon(file: File): string {
    if (this.isImageFile(file)) {
      return 'el-icon-picture'
    } else if (this.isVideoFile(file)) {
      return 'el-icon-video-camera'
    } else if (this.isAudioFile(file)) {
      return 'el-icon-headset'
    } else {
      const ext = this.getFileExtension(file.name)
      switch (ext) {
        case 'pdf':
          return 'el-icon-document'
        case 'doc':
        case 'docx':
          return 'el-icon-document'
        case 'xls':
        case 'xlsx':
          return 'el-icon-document'
        case 'ppt':
        case 'pptx':
          return 'el-icon-document'
        case 'zip':
        case 'rar':
        case '7z':
          return 'el-icon-box'
        default:
          return 'el-icon-document'
      }
    }
  }

  /**
   * 创建图片预览URL
   */
  static createPreviewUrl(file: File): string {
    return URL.createObjectURL(file)
  }

  /**
   * 释放预览URL
   */
  static revokePreviewUrl(url: string): void {
    URL.revokeObjectURL(url)
  }

  /**
   * 下载文件
   */
  static downloadFile(url: string, filename: string): void {
    const link = document.createElement('a')
    link.href = url
    link.download = filename
    link.style.display = 'none'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }

  /**
   * 下载Blob文件
   */
  static downloadBlob(blob: Blob, filename: string): void {
    const url = URL.createObjectURL(blob)
    this.downloadFile(url, filename)
    URL.revokeObjectURL(url)
  }

  /**
   * 检查浏览器是否支持某个API
   */
  static checkSupport(): {
    fileAPI: boolean
    dragDrop: boolean
    formData: boolean
    xhr2: boolean
  } {
    return {
      fileAPI: !!(window.File && window.FileReader && window.FileList && window.Blob),
      dragDrop: 'draggable' in document.createElement('div'),
      formData: !!window.FormData,
      xhr2: 'upload' in new XMLHttpRequest()
    }
  }

  /**
   * 节流函数
   */
  static throttle<T extends (...args: any[]) => any>(
    func: T,
    delay: number
  ): (...args: Parameters<T>) => void {
    let timeoutId: NodeJS.Timeout | null = null
    let lastExecTime = 0
    
    return (...args: Parameters<T>) => {
      const currentTime = Date.now()
      
      if (currentTime - lastExecTime > delay) {
        func(...args)
        lastExecTime = currentTime
      } else {
        if (timeoutId) {
          clearTimeout(timeoutId)
        }
        timeoutId = setTimeout(() => {
          func(...args)
          lastExecTime = Date.now()
        }, delay - (currentTime - lastExecTime))
      }
    }
  }

  /**
   * 防抖函数
   */
  static debounce<T extends (...args: any[]) => any>(
    func: T,
    delay: number
  ): (...args: Parameters<T>) => void {
    let timeoutId: NodeJS.Timeout | null = null
    
    return (...args: Parameters<T>) => {
      if (timeoutId) {
        clearTimeout(timeoutId)
      }
      timeoutId = setTimeout(() => func(...args), delay)
    }
  }
}

/**
 * 文件附件管理工具类
 * 用于处理业务模块中的文件ID集合管理和回显功能
 */
export class FileAttachmentManager {
  private fileUploadApi = useFileUploadApi()

  /**
   * 从文件列表中提取成功的文件ID集合
   * @param fileList 文件列表
   * @returns 文件ID集合
   */
  extractFileIds(fileList: UploadFile[]): string[] {
    return fileList
      .filter(file => file.status === 'success' && this.getFileId(file))
      .map(file => this.getFileId(file)!)
      .filter(Boolean)
  }

  /**
   * 将文件ID集合转换为JSON字符串（用于保存到数据库）
   * @param fileIds 文件ID集合
   * @returns JSON字符串
   */
  fileIdsToJson(fileIds: string[]): string {
    return JSON.stringify(fileIds)
  }

  /**
   * 从JSON字符串中解析文件ID集合
   * @param jsonStr JSON字符串
   * @returns 文件ID集合
   */
  fileIdsFromJson(jsonStr: string): string[] {
    try {
      const parsed = JSON.parse(jsonStr)
      if (Array.isArray(parsed)) {
        return parsed.filter(id => typeof id === 'string' && id.trim())
      }
      return []
    } catch (error) {
      console.warn('解析文件ID JSON失败:', error, jsonStr)
      return []
    }
  }

  /**
   * 根据文件ID集合获取文件信息，转换为UploadFile格式
   * @param fileIds 文件ID集合
   * @returns UploadFile列表
   */
  async getFilesForDisplay(fileIds: string[]): Promise<UploadFile[]> {
    if (!fileIds || fileIds.length === 0) {
      return []
    }

    try {
      const response = await this.fileUploadApi.getFilesByIds(fileIds)
      
      if (response.code === 200 && response.data) {
        return response.data.map((fileInfo: any, index: number) => this.convertToUploadFile(fileInfo, index))
      }
      
      return []
    } catch (error) {
      console.error('获取文件信息失败:', error)
      return []
    }
  }

  /**
   * 将服务端文件信息转换为UploadFile格式
   * @param fileInfo 服务端文件信息
   * @param index 索引（用于生成唯一ID）
   * @returns UploadFile对象
   */
  private convertToUploadFile(fileInfo: any, index: number): UploadFile {
    // 获取文件ID和构建预览URL
    const fileId = fileInfo.fileId || fileInfo.id
    const previewUrl = this.buildPreviewUrl(fileId)
    const fileType = this.getContentTypeFromFileType(fileInfo.fileType) || 'application/octet-stream'
    const fileName = fileInfo.originalFilename || fileInfo.fileName || ''

    return {
      id: `file_${Date.now()}_${index}`,
      fileId: fileId,
      name: fileName,
      url: previewUrl,
      size: fileInfo.fileSize || 0,
      type: fileType,
      // 不创建虚拟File对象，让预览组件通过URL获取内容
      file: undefined as any,
      status: 'success' as const,
      progress: 100,
      response: {
        data: {
          fileId: fileId,
          fileName: fileInfo.fileName,
          originalFilename: fileInfo.originalFilename,
          fileSize: fileInfo.fileSize,
          fileType: fileInfo.fileType,
          fileMd5: fileInfo.fileMd5,
          category: fileInfo.category,
          businessId: fileInfo.businessId
        }
      }
    }
  }

  /**
   * 根据文件类型代码获取Content-Type
   * @param fileType 文件类型代码
   * @returns Content-Type
   */
  private getContentTypeFromFileType(fileType: string): string | null {
    const typeMap: Record<string, string> = {
      '1': 'image/jpeg',
      '2': 'application/pdf',
      '3': 'video/mp4',
      '4': 'audio/mpeg',
      '5': 'application/octet-stream'
    }
    
    return typeMap[fileType] || null
  }

  /**
   * 构建文件预览URL
   * @param fileId 文件ID
   * @returns 预览URL
   */
  private buildPreviewUrl(fileId: string): string {
    return `/api/upload/file/preview/${fileId}`
  }

  /**
   * 统一的文件ID获取方法
   * @param file UploadFile对象
   * @returns 文件ID
   */
  private getFileId(file: UploadFile): string | null {
    return file.fileId || 
           file.response?.data?.fileId || 
           file.response?.data?.id || 
           file.response?.id || 
           null
  }

  /**
   * 从附件数据中提取文件信息（兼容JSON格式和ID集合格式）
   * @param attachmentData 附件数据（可能是JSON字符串或文件ID集合）
   * @returns 文件ID集合
   */
  parseAttachmentData(attachmentData: string): string[] {
    if (!attachmentData || !attachmentData.trim()) {
      return []
    }

    try {
      const parsed = JSON.parse(attachmentData)
      
      if (Array.isArray(parsed) && parsed.length > 0 && typeof parsed[0] === 'object') {
        return parsed
          .map(item => item.id || item.fileId)
          .filter(Boolean)
      }
      
      if (Array.isArray(parsed)) {
        return parsed.filter(id => typeof id === 'string' && id.trim())
      }
      
      return []
    } catch (error) {
      return attachmentData.split(',')
        .map(id => id.trim())
        .filter(Boolean)
    }
  }

  /**
   * 创建附件数据的完整JSON格式（包含文件详细信息）
   * @param fileList 文件列表
   * @returns JSON字符串
   */
  createAttachmentJson(fileList: UploadFile[]): string {
    const attachments = fileList
      .filter(file => file.status === 'success' && this.getFileId(file))
      .map(file => ({
        id: this.getFileId(file),
        name: file.name,
        url: file.url,
        size: file.size,
        type: file.type,
        fileId: this.getFileId(file)
      }))

    return JSON.stringify(attachments)
  }

  /**
   * 验证文件ID是否有效
   * @param fileId 文件ID
   * @returns 是否有效
   */
  isValidFileId(fileId: string): boolean {
    return !!(fileId && typeof fileId === 'string' && fileId.trim())
  }

  /**
   * 批量验证文件ID
   * @param fileIds 文件ID集合
   * @returns 有效的文件ID集合
   */
  validateFileIds(fileIds: string[]): string[] {
    return fileIds.filter(id => this.isValidFileId(id))
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
   * @param fileList 文件列表
   * @returns 文件ID的JSON字符串
   */
  saveFileIds: (fileList: UploadFile[]): string => {
    const manager = new FileAttachmentManager()
    const fileIds = manager.extractFileIds(fileList)
    return manager.fileIdsToJson(fileIds)
  },

  /**
   * 加载文件ID集合并转换为文件列表用于回显
   * @param attachmentData 附件数据
   * @returns Promise<UploadFile[]>
   */
  loadFiles: async (attachmentData: string): Promise<UploadFile[]> => {
    const manager = new FileAttachmentManager()
    const fileIds = manager.parseAttachmentData(attachmentData)
    return await manager.getFilesForDisplay(fileIds)
  },

  /**
   * 创建完整的附件JSON数据（包含文件详细信息）
   * @param fileList 文件列表
   * @returns JSON字符串
   */
  createAttachmentData: (fileList: UploadFile[]): string => {
    const manager = new FileAttachmentManager()
    return manager.createAttachmentJson(fileList)
  }
}
