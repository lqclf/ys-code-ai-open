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
  originalName?: string  // 原始文件名，用于下载时保持原始文件名
  size: number
  type: string
  file?: File  // 文件对象，本地上传时有值，回显模式下可能为空
  status: UploadStatus
  progress: number
  url?: string
  fileId?: string  // 添加fileId字段，用于存储后端返回的文件ID
  response?: any
  error?: string
  md5?: string
  uploadId?: string
  chunks?: ChunkInfo[]
  resumable?: boolean
  uploadTime?: string
  fileKey?: string  // 文件唯一标识，用于断点续传
  chunkUploadResult?: ChunkUploadResult  // 后端返回的分片上传结果，用于断点续传
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
  // 预览配置
  preview?: {
    enabled?: boolean // 是否启用预览功能
    useDefault?: boolean // 是否使用默认预览方式
    customHandler?: FilePreviewFunction // 自定义预览处理函数
    supportedTypes?: string[] // 支持预览的文件类型
  }
  
  // 下载配置
  download?: {
    enabled?: boolean // 是否启用下载功能
    useDefault?: boolean // 是否使用默认下载方式
    customHandler?: FileDownloadFunction // 自定义下载处理函数
    downloadUrl?: string // 自定义下载API地址
  }
  
  // 删除配置
  remove?: {
    enabled?: boolean // 是否启用删除功能
    useDefault?: boolean // 是否使用默认删除方式
    customHandler?: FileRemoveFunction // 自定义删除处理函数
    deleteUrl?: string // 自定义删除API地址
    confirmDelete?: boolean // 是否需要确认删除
    confirmMessage?: string // 删除确认提示
  }
}

// 文件上传配置 - 包含文件选择、文件限制等基础配置
export interface UploadFileConfig {
  action?: string // 上传接口地址
  method?: string // 请求方法，默认 'POST'
  headers?: Record<string, string> // 请求头配置
  data?: Record<string, any> // 上传时携带的额外参数
  name?: string // 上传文件的字段名，默认 'file'
  accept?: string // 接受的文件类型，如 '.jpg,.png,.pdf'
  multiple?: boolean // 是否支持多文件上传，默认 true
  maxSize?: number // 单个文件最大大小（字节），默认 50MB
  maxCount?: number // 最大文件数量，0 表示无限制，默认 0
  autoUpload?: boolean // 是否自动上传，默认 true
}

// 分片上传配置 - 包含分片上传相关的所有配置
export interface ChunkUploadConfig {
  enableChunk?: boolean // 是否启用分片上传，默认 true
  chunkSize?: number // 分片大小（字节），默认 2MB
  chunkThreshold?: number // 启用分片上传的文件大小阈值（字节），默认 10MB
  enableResume?: boolean // 是否启用断点续传，默认 true
}

// 显示配置 - 包含显示相关的配置
export interface DisplayConfig {
  showFileList?: boolean // 是否显示文件列表，默认 true
  listType?: 'text' | 'picture' | 'picture-card' | string // 文件列表类型，作用：text 文本列表，picture 图片列表，picture-card 图片卡片列表，默认 'text'
  drag?: boolean // 是否启用拖拽上传，默认 false
  tip?: string // 上传提示文案
  placeholder?: string // 占位符文本
  dragText?: string // 拖拽上传提示文案，默认 '将文件拖拽到此处，或点击上传'
  width?: string | number // 组件宽度
  height?: string | number // 组件高度
}

// 业务配置 - 包含业务相关的配置
export interface BusinessConfig {
  businessId?: string // 业务ID，用于关联业务数据
  category?: string // 文件分类
  storageType?: string // 存储类型
  enableDeduplication?: boolean // 是否启用文件去重，默认 true
}

// 响应码配置 - 包含响应码相关的配置
export interface ResponseConfig {
  successCode?: number | string | (number | string)[] // 成功状态码，默认 200
  errorCode?: number | string | (number | string)[] // 失败状态码
  successCodePath?: string // 成功标识字段路径（支持嵌套，如 'data.code'），默认 'code'
  errorCodePath?: string // 失败标识字段路径，默认 'code'
  messagePath?: string // 消息字段路径，默认 'message'
  dataPath?: string // 数据字段路径，默认 'data'
  isSuccess?: (response: any) => boolean // 自定义成功判断函数
  isError?: (response: any) => boolean // 自定义失败判断函数
  getMessage?: (response: any) => string // 自定义消息提取函数
  getData?: (response: any) => any // 自定义数据提取函数
}

// 上传组件Props - 组件主配置接口
export interface UploadProps {
  // 文件列表
  modelValue?: UploadFile[] // 文件列表数据，支持 v-model 双向绑定
  
  // 文件ID集合（用于回显）
  fileIds?: string[] // 文件ID集合，用于回显已有文件
  
  // 文件上传配置 - 文件选择、文件限制等基础配置
  uploadConfig?: UploadFileConfig
  
  // 分片上传配置 - 分片上传相关的所有配置
  chunkConfig?: ChunkUploadConfig
  
  // 显示配置 - 显示相关的配置
  displayConfig?: DisplayConfig
  
  // 业务配置 - 业务相关的配置
  businessConfig?: BusinessConfig
  
  // 文件操作配置 - 预览、下载、删除等操作配置
  fileOperations?: FileOperationConfig
  
  // 接口配置 - 所有上传相关的接口地址
  apiConfig?: UploadApiConfig
  
  // 响应码配置 - 响应码相关的配置
  responseConfig?: ResponseConfig
  
  // 组件配置 - 组件基础配置
  disabled?: boolean // 是否禁用上传，默认 false
  mode?: 'upload' | 'view' // 组件模式：'upload' 上传模式，'view' 查看模式，默认 'upload'
  
  // 回调函数
  beforeUpload?: (file: File) => boolean | Promise<boolean> // 上传前钩子函数
  onProgress?: (file: UploadFile, progress: number) => void // 上传进度回调
  onSuccess?: (file: UploadFile, response: any) => void // 上传成功回调
  onError?: (file: UploadFile, error: string) => void // 上传失败回调
  onChange?: (fileList: UploadFile[]) => void // 文件列表变化回调
  onRemove?: (file: UploadFile, fileList: UploadFile[]) => Boolean // 删除文件回调
  onPreview?: (file: UploadFile) => void // 预览文件回调
  onDownload?: (file: UploadFile) => void // 下载文件回调
  onExceed?: (files: File[], fileList: UploadFile[]) => void // 文件数量超限回调
}

// 响应码配置接口
export interface ResponseConfig {
  // 成功状态码
  successCode?: number | string | (number | string)[]
  // 失败状态码
  errorCode?: number | string | (number | string)[]
  // 成功标识字段路径（支持嵌套，如 'data.code'）
  successCodePath?: string
  // 失败标识字段路径
  errorCodePath?: string
  // 消息字段路径
  messagePath?: string
  // 数据字段路径
  dataPath?: string
  // 自定义成功判断函数
  isSuccess?: (response: any) => boolean
  // 自定义失败判断函数
  isError?: (response: any) => boolean
  // 自定义消息提取函数
  getMessage?: (response: any) => string
  // 自定义数据提取函数
  getData?: (response: any) => any
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

/**
 * 上传接口配置
 * 用于配置所有上传相关的接口地址
 */
export interface UploadApiConfig {
  /**
   * 单文件上传接口地址
   */
  uploadFile?: string
  
  /**
   * 批量文件上传接口地址
   */
  uploadFiles?: string
  
  /**
   * 分片上传接口地址
   */
  uploadChunk?: string
  
  /**
   * 合并分片接口地址
   */
  mergeChunks?: string
  
  /**
   * 获取上传进度接口地址
   */
  getUploadProgress?: string
  
  /**
   * 获取已上传分片列表接口地址
   */
  getUploadedChunks?: string
  
  /**
   * 取消上传接口地址
   */
  cancelUpload?: string
  
  /**
   * 检查重复文件接口地址
   */
  checkDuplicateFile?: string
  
  /**
   * 删除文件接口地址
   */
  deleteFile?: string
  
  /**
   * 文件下载接口地址
   */
  downloadFile?: string
  
  /**
   * 获取下载链接接口地址
   */
  getDownloadUrl?: string
  
  /**
   * 获取文件预览链接接口地址
   */
  getPreviewUrl?: string
  
  /**
   * 文件预览接口地址
   */
  previewFile?: string
  
  /**
   * 生成访问令牌接口地址
   */
  generateAccessToken?: string
  
  /**
   * 根据令牌下载文件接口地址
   */
  downloadByToken?: string
  
  /**
   * 根据文件ID集合获取文件信息接口地址
   */
  getFilesByIds?: string
  
  /**
   * 根据业务ID获取文件列表接口地址
   */
  getFilesByBusinessIdExt?: string
}
