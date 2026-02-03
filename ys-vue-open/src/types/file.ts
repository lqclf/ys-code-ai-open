export interface FileInfo {
  id: string
  fileKey: string
  originalFilename: string
  fileName: string
  fileSize: number
  fileSizeStr: string
  fileType: number
  fileMd5: string
  storageType: string | undefined
  uploadStatus: number
  businessId?: string
  category?: string
  isPublic?: boolean
  thumbnailPath?: string
  createTime: string
  updateTime: string
}

export enum FileType {
  IMAGE = 0,
  DOCUMENT = 1,
  VIDEO = 2,
  AUDIO = 3,
  OTHER = 4
}

export enum StorageType {
  LOCAL = 'local',
  MINIO = 'minio',
  ALIYUN_OSS = 'aliyun-oss',
  TENCENT_COS = 'tencent-cos',
  AWS_S3 = 'aws-s3'
}

export enum UploadStatus {
  UPLOADING = 0,
  SUCCESS = 1,
  FAILED = 2
}

export interface UploadTask {
  id: string
  file: File
  options: UploadOptions
  status: TaskStatus
  progress: number
  retryCount: number
  error?: string
  abortController?: AbortController
  uploadedChunks?: Set<number>
  totalChunks?: number
}

export enum TaskStatus {
  PENDING = 'pending',
  UPLOADING = 'uploading',
  PAUSED = 'paused',
  COMPLETED = 'completed',
  FAILED = 'failed',
  CANCELLED = 'cancelled'
}

export interface UploadOptions {
  category?: string
  businessId?: string
  storageType?: StorageType
  generateThumbnail?: boolean
  description?: string
  useChunk?: boolean
  chunkSize?: number
  onProgress?: (progress: number) => void
  onComplete?: (result: UploadResult) => void
  onError?: (error: Error) => void
}

export interface UploadResult {
  success: boolean
  isDuplicate?: boolean
  fileInfo?: FileInfo
  error?: string
}

export interface ChunkUploadResult {
  fileKey: string
  chunkIndex: number
  totalChunks: number
  uploadedChunks: number
  progress: number
  isCompleted: boolean
  status: string
  errorMessage?: string
  isCancelled?: boolean
}

export interface ChunkUploadOptions {
  fileKey: string
  chunkIndex: number
  totalChunks: number
  chunkSize: number
  totalSize: number
  fileMd5: string
  filename: string
}

export interface FileUploadProgress {
  filename: string
  totalSize: number
  uploadedSize: number
  progress: number
  status: string
  startTime: number
}

export interface ApiResponse<T = any> {
  code: number
  success: boolean
  msg: string
  data: T
}

export interface PageResponse<T> {
  records: T[]
  total: number
  pageNo: number
  pageSize: number
}

export interface FileQueryParams {
  pageNo?: number
  pageSize?: number
  fileName?: string
  fileType?: number
  category?: string
  businessId?: string
  uploadStatus?: number
  startTime?: string
  endTime?: string
}
export interface UploadConfig {
	category: string
	businessId: string
	storageType: string
	generateThumbnail: boolean
	description: string
	maxSize: number
	maxCount: number
	enableChunk: boolean
	chunkSize: number
	chunkThreshold: number
	enableDeduplication: boolean
	autoUpload: boolean
}

export interface UploadResult {
	fileName: string
	originalFilename: string
	fileSize: number
	fileType: string
	fileMd5: string
	storageType: string
	uploadStatus: number
	success: boolean
	message: string
	error?: string
}
