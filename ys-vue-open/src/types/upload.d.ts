/**
 * 文件上传相关类型定义
 */

// 上传状态枚举
export type UploadStatus = 'ready' | 'uploading' | 'success' | 'error' | 'cancelled' | 'paused'

// 上传方式枚举
export type UploadMethod = 'normal' | 'chunk'

// 文件类型
export interface UploadFile {
  id: string
  name: string
  size: number
  type: string
  file: File
  status: UploadStatus
  progress: number
  url?: string
  fileId?: string
  response?: any
  error?: string
  md5?: string
  uploadId?: string
  chunks?: ChunkInfo[]
  resumable?: boolean
  uploadTime?: string
  fileKey?: string
  chunkUploadResult?: ChunkUploadResult
}

// 分片信息
export interface ChunkInfo {
  index: number
  start: number
  end: number
  size: number
  uploaded: boolean
  retryCount: number
}

// 上传进度信息
export interface UploadProgress {
  fileKey: string
  filename: string
  totalSize: number
  uploadedSize: number
  progress: number
  status: string
  startTime: number
  currentTime: number
  speed?: number
  remainingTime?: number
}

// 上传结果
export interface UploadResult {
  success: boolean
  message: string
  data?: {
    id: string
    fileId: string
    fileName: string
    originalFilename: string
    fileUrl: string
    fileSize: number
    fileType: string
    fileMd5: string
    uploadStatus: number
    uploadStatusDesc: string
  }
}

// 分片上传结果
export interface ChunkUploadResult {
  fileKey: string
  chunkIndex: number
  totalChunks: number
  uploadedChunks: number
  isCompleted: boolean
  status: string
  progress: number
  errorMessage?: string
}

// 文件上传DTO（与后端对应）
export interface FileUploadDTO {
  businessId?: string
  category?: string
  storageType?: string
  enableDeduplication?: boolean
  enableThumbnail?: boolean
  parentId?: string
}

// 分片上传DTO（与后端对应）
export interface ChunkUploadDTO {
  fileKey: string
  filename: string
  fileMd5: string
  chunkIndex: number
  chunkSize: number
  totalChunks: number
  totalSize: number
  chunkMd5?: string
}

// 文件操作函数类型
export type FileOperationFunction = (file: UploadFile) => void | Promise<void>
export type FilePreviewFunction = (file: UploadFile) => void | Promise<void>
export type FileDownloadFunction = (file: UploadFile) => void | Promise<void>
export type FileRemoveFunction = (file: UploadFile) => boolean | Promise<boolean>

// 文件操作配置
export interface FileOperationConfig {
  preview?: {
    enabled?: boolean
    useDefault?: boolean
    customHandler?: FilePreviewFunction
    supportedTypes?: string[]
  }
  
  download?: {
    enabled?: boolean
    useDefault?: boolean
    customHandler?: FileDownloadFunction
    downloadUrl?: string
  }
  
  remove?: {
    enabled?: boolean
    useDefault?: boolean
    customHandler?: FileRemoveFunction
    deleteUrl?: string
    confirmDelete?: boolean
    confirmMessage?: string
  }
}

// 文件上传配置
export interface UploadFileConfig {
  action?: string
  method?: string
  headers?: Record<string, string>
  data?: Record<string, any>
  name?: string
  accept?: string
  multiple?: boolean
  maxSize?: number
  maxCount?: number
  autoUpload?: boolean
}

// 分片上传配置
export interface ChunkUploadConfig {
  enableChunk?: boolean
  chunkSize?: number
  chunkThreshold?: number
  enableResume?: boolean
}

// 显示配置
export interface DisplayConfig {
  showFileList?: boolean
  listType?: 'text' | 'picture' | 'picture-card' | string
  drag?: boolean
  tip?: string
  placeholder?: string
  dragText?: string
  width?: string | number
  height?: string | number
}

// 业务配置
export interface BusinessConfig {
  businessId?: string
  category?: string
  storageType?: string
  enableDeduplication?: boolean
}

// 响应码配置
export interface ResponseConfig {
  successCode?: number | string | (number | string)[]
  errorCode?: number | string | (number | string)[]
  successCodePath?: string
  errorCodePath?: string
  messagePath?: string
  dataPath?: string
  isSuccess?: (response: any) => boolean
  isError?: (response: any) => boolean
  getMessage?: (response: any) => string
  getData?: (response: any) => any
}

// 上传组件Props
export interface UploadProps {
  modelValue?: UploadFile[]
  fileIds?: string[]
  uploadConfig?: UploadFileConfig
  chunkConfig?: ChunkUploadConfig
  displayConfig?: DisplayConfig
  businessConfig?: BusinessConfig
  fileOperations?: FileOperationConfig
  apiConfig?: UploadApiConfig
  responseConfig?: ResponseConfig
  disabled?: boolean
  mode?: 'upload' | 'view'
  beforeUpload?: (file: File) => boolean | Promise<boolean>
  onProgress?: (file: UploadFile, progress: number) => void
  onSuccess?: (file: UploadFile, response: any) => void
  onError?: (file: UploadFile, error: string) => void
  onChange?: (fileList: UploadFile[]) => void
  onRemove?: (file: UploadFile, fileList: UploadFile[]) => Boolean
  onPreview?: (file: UploadFile) => void
  onDownload?: (file: UploadFile) => void
  onExceed?: (files: File[], fileList: UploadFile[]) => void
}

// 上传项组件Props
export interface UploadItemProps {
  file: UploadFile
  listType?: 'text' | 'picture' | 'picture-card' | string
  showProgress?: boolean
  showRemove?: boolean
  showPreview?: boolean
  showDownload?: boolean
  disabled?: boolean
  fileOperations?: FileOperationConfig
}

// 上传进度组件Props
export interface UploadProgressProps {
  file: UploadFile
  showText?: boolean
  showSpeed?: boolean
  showRemainingTime?: boolean
  strokeWidth?: number
  strokeColor?: string
  trailColor?: string
}

// 拖拽状态
export interface DragState {
  isDragOver: boolean
  isDragging: boolean
  dragCounter: number
}

// 文件校验规则
export interface FileValidationRule {
  accept?: string[]
  maxSize?: number
  minSize?: number
  maxCount?: number
  required?: boolean
}

// 错误类型
export interface UploadError {
  code: string
  message: string
  file?: UploadFile
}

// 上传接口配置
export interface UploadApiConfig {
  uploadFile?: string
  uploadFiles?: string
  uploadChunk?: string
  mergeChunks?: string
  getUploadProgress?: string
  getUploadedChunks?: string
  cancelUpload?: string
  checkDuplicateFile?: string
  deleteFile?: string
  downloadFile?: string
  getDownloadUrl?: string
  getPreviewUrl?: string
  previewFile?: string
  generateAccessToken?: string
  downloadByToken?: string
  getFilesByIds?: string
  getFilesByBusinessIdExt?: string
}
