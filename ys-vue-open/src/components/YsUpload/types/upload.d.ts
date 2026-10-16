/**
 * 文件上传相关类型定义（简化版）
 * 去除分片/预览/Token/统计相关，仅保留核心上传/下载/删除
 */

// 上传状态枚举
export type UploadStatus = 'ready' | 'uploading' | 'success' | 'error'

// 文件类型
export interface UploadFile {
  id: string
  name: string
  originalName?: string // 原始文件名，用于下载时保持原始文件名
  size: number
  type: string
  file?: File // 文件对象，本地上传时有值，回显模式下可能为空
  status: UploadStatus
  progress: number
  url?: string
  fileId?: string // 后端返回的文件ID
  response?: any
  error?: string
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
  }
}

// 文件上传DTO（与后端对应）
export interface FileUploadDTO {
  businessId?: string
  category?: string
  storageType?: string
}

// 文件操作函数类型
export type FileDownloadFunction = (file: UploadFile) => void | Promise<void>
export type FileRemoveFunction = (file: UploadFile) => boolean | Promise<boolean>

// 文件操作配置
export interface FileOperationConfig {
  // 下载配置
  download?: {
    enabled?: boolean
    useDefault?: boolean
    customHandler?: FileDownloadFunction
  }

  // 删除配置
  remove?: {
    enabled?: boolean
    useDefault?: boolean
    customHandler?: FileRemoveFunction
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

// 显示配置
export interface DisplayConfig {
  showFileList?: boolean
  listType?: 'text' | 'picture' | 'picture-card' | string
  tip?: string
  placeholder?: string
  width?: string | number
  height?: string | number
}

// 业务配置
export interface BusinessConfig {
  businessId?: string
  category?: string
  storageType?: string
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
  // 文件列表
  modelValue?: UploadFile[]

  // 文件ID集合（用于回显）
  fileIds?: string[]

  // 文件上传配置
  uploadConfig?: UploadFileConfig

  // 显示配置
  displayConfig?: DisplayConfig

  // 业务配置
  businessConfig?: BusinessConfig

  // 文件操作配置
  fileOperations?: FileOperationConfig

  // 接口配置
  apiConfig?: UploadApiConfig

  // 响应码配置
  responseConfig?: ResponseConfig

  // 组件配置
  disabled?: boolean
  mode?: 'upload' | 'view'

  // 回调函数
  beforeUpload?: (file: File) => boolean | Promise<boolean>
  onProgress?: (file: UploadFile, progress: number) => void
  onSuccess?: (file: UploadFile, response: any) => void
  onError?: (file: UploadFile, error: string) => void
  onChange?: (fileList: UploadFile[]) => void
  onRemove?: (file: UploadFile, fileList: UploadFile[]) => Boolean
  onDownload?: (file: UploadFile) => void
  onExceed?: (files: File[], fileList: UploadFile[]) => void
}

// 上传项组件Props
export interface UploadItemProps {
  file: UploadFile
  listType?: 'text' | 'picture' | 'picture-card' | string
  showProgress?: boolean
  showRemove?: boolean
  showDownload?: boolean
  disabled?: boolean
  fileOperations?: FileOperationConfig
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
  deleteFile?: string
  downloadFile?: string
  getFilesByIds?: string
}
