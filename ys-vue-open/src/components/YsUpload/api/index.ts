import request from '@/utils/request'
import { ref } from 'vue'
import type { 
  FileUploadDTO, 
  ChunkUploadDTO, 
  UploadResult, 
  ChunkUploadResult, 
  UploadProgress,
  UploadApiConfig
} from '../types/upload'
import { UploadUtils } from '../utils'

/**
 * 默认接口配置
 */
export const DEFAULT_API_CONFIG: Required<UploadApiConfig> = {
  uploadFile: '/api/upload/file/upload',
  uploadFiles: '/api/upload/file/upload/batch',
  uploadChunk: '/api/upload/file/chunk',
  mergeChunks: '/api/upload/file/merge',
  getUploadProgress: '/api/upload/file/progress',
  getUploadedChunks: '/api/upload/file/chunks',
  cancelUpload: '/api/upload/file/cancel',
  checkDuplicateFile: '/api/upload/file/check/duplicate',
  deleteFile: '/api/upload/file/delete/physical',
  downloadFile: '/api/upload/file/download',
  getDownloadUrl: '/api/upload/file/download/url',
  getPreviewUrl: '/api/upload/file/preview/url',
  previewFile: '/api/upload/file/preview',
  generateAccessToken: '/api/upload/file/token/generate',
  downloadByToken: '/api/upload/file/download/token',
  getFilesByIds: '/api/upload/file/getByIds',
  getFilesByBusinessIdExt: '/api/upload/file/business'
}
/**
 * 文件上传API接口集合
 * @param apiConfig 接口配置，如果不提供则使用默认配置
 */
export function useFileUploadApi(apiConfig?: UploadApiConfig) {
  // 合并配置，使用用户配置覆盖默认配置
  const config = { ...DEFAULT_API_CONFIG, ...apiConfig }

  return {
    /**
     * 单文件上传
     */
    uploadFile: (file: File, uploadDTO?: FileUploadDTO, onProgress?: (progress: number) => void, signal?: AbortSignal) => {
      const formData = new FormData()
      formData.append('file', file)
      
      // 添加上传参数
      if (uploadDTO) {
        Object.keys(uploadDTO).forEach(key => {
          const value = uploadDTO[key as keyof FileUploadDTO]
          if (value !== undefined && value !== null) {
            formData.append(key, String(value))
          }
        })
      }
      
      return request({
        url: config.uploadFile,
        method: 'post',
        data: formData,
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        signal,
        onUploadProgress: (progressEvent) => {
          if (progressEvent.total && onProgress) {
            const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total)
            onProgress(progress)
          }
        }
      }) as Promise<UploadResult>
    },

    /**
     * 批量文件上传
     */
    uploadFiles: (files: File[], uploadDTO?: FileUploadDTO, onProgress?: (progress: number) => void) => {
      const formData = new FormData()
      
      // 添加多个文件
      files.forEach(file => {
        formData.append('files', file)
      })
      
      // 添加上传参数
      if (uploadDTO) {
        Object.keys(uploadDTO).forEach(key => {
          const value = uploadDTO[key as keyof FileUploadDTO]
          if (value !== undefined && value !== null) {
            formData.append(key, String(value))
          }
        })
      }
      
      return request({
        url: config.uploadFiles,
        method: 'post',
        data: formData,
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        onUploadProgress: (progressEvent) => {
          if (progressEvent.total && onProgress) {
            const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total)
            onProgress(progress)
          }
        }
      }) as Promise<{ data: UploadResult[] }>
    },

    /**
     * 分片上传
     */
    uploadChunk: (chunkFile: File, chunkDTO: ChunkUploadDTO, onProgress?: (progress: number) => void, signal?: AbortSignal) => {
      const formData = new FormData()
      formData.append('file', chunkFile)
      
      // 添加分片参数
      Object.keys(chunkDTO).forEach(key => {
        const value = chunkDTO[key as keyof ChunkUploadDTO]
        if (value !== undefined && value !== null) {
          formData.append(key, String(value))
        }
      })
      
      return request({
        url: config.uploadChunk,
        method: 'post',
        data: formData,
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        signal,
        onUploadProgress: (progressEvent) => {
          if (progressEvent.total && onProgress) {
            const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total)
            onProgress(progress)
          }
        }
      }) as Promise<{ data: ChunkUploadResult }>
    },

    /**
     * 合并分片文件
     */
    mergeChunks: (fileKey: string, uploadDTO?: FileUploadDTO) => {
      const params: any = { fileKey }
      
      if (uploadDTO) {
        Object.assign(params, uploadDTO)
      }
      
      return request({
        url: config.mergeChunks,
        method: 'post',
        params
      }) as Promise<UploadResult>
    },

    /**
     * 获取上传进度
     */
    getUploadProgress: (fileKey: string) => {
      return request({
        url: `${config.getUploadProgress}/${fileKey}`,
        method: 'get'
      }) as Promise<{ data: UploadProgress }>
    },

    /**
     * 获取已上传的分片列表
     */
    getUploadedChunks: (fileKey: string) => {
      return request({
        url: `${config.getUploadedChunks}/${fileKey}`,
        method: 'get'
      }) as Promise<{ data: number[] }>
    },

    /**
     * 取消上传
     */
    cancelUpload: (fileKey: string) => {
      return request({
        url: `${config.cancelUpload}/${fileKey}`,
        method: 'post'
      }) as Promise<{ data: boolean }>
    },

    /**
     * 检查重复文件
     */
    checkDuplicateFile: (fileMd5: string, fileSize: number) => {
      return request({
        url: config.checkDuplicateFile,
        method: 'get',
        params: { fileMd5, fileSize }
      })
    },

    /**
     * 删除文件
     */
    deleteFile: (fileId: string) => {
      return request({
        url: `${config.deleteFile}/${fileId}`,
        method: 'delete'
      }) as Promise<{ data: boolean }>
    },

    /**
     * 文件下载
     */
    downloadFile: (fileId: string) => {
      return request({
        url: `${config.downloadFile}/${fileId}`,
        method: 'get',
        responseType: 'blob'
      })
    },

    /**
     * 获取下载链接
     */
    getDownloadUrl: (fileId: string, expireMinutes?: number) => {
      return request({
        url: `${config.getDownloadUrl}/${fileId}`,
        method: 'get',
        params: { expireMinutes }
      })
    },

    /**
     * 获取文件预览链接
     */
    getPreviewUrl: (fileId: string) => {
      return request({
        url: `${config.getPreviewUrl}/${fileId}`,
        method: 'get'
      })
    },

    /**
     * 文件预览
     */
    previewFile: (fileId: string) => {
      return request({
        url: `${config.previewFile}/${fileId}`,
        method: 'get',
        responseType: 'blob'
      })
    },

    /**
     * 生成访问令牌
     */
    generateAccessToken: (fileId: string, expireMinutes?: number) => {
      return request({
        url: `${config.generateAccessToken}/${fileId}`,
        method: 'post',
        params: { expireMinutes }
      })
    },

    /**
     * 根据令牌下载文件
     */
    downloadByToken: (token: string) => {
      return request({
        url: `${config.downloadByToken}/${token}`,
        method: 'get',
        responseType: 'blob'
      })
    },

    /**
     * 根据文件ID集合获取文件信息
     */
    getFilesByIds: (fileIds: string[]) => {
      return request({
        url: config.getFilesByIds,
        method: 'post',
        data: fileIds
      })
    },

    /**
     * 根据业务ID获取文件列表 - 扩展版本
     */
    getFilesByBusinessIdExt: (businessId: string) => {
      return request({
        url: `${config.getFilesByBusinessIdExt}/${businessId}`,
        method: 'get'
      })
    }
  }
}

/**
 * 验证接口配置
 * @param apiConfig 接口配置
 * @param enableChunk 是否启用分片上传
 * @returns 验证结果和错误信息
 */
export function validateApiConfig(apiConfig?: UploadApiConfig, enableChunk?: boolean): { valid: boolean; errors: string[] } {
  const errors: string[] = []
  
  // 如果启用了分片上传，必须确保有分片相关接口
  if (enableChunk) {
    
    // 如果用户提供了部分配置，检查是否缺少必要的接口
    if (apiConfig) {
      // 如果用户只提供了部分接口，检查缺少的接口
      const chunkRelatedProvided = apiConfig.uploadChunk !== undefined || apiConfig.mergeChunks !== undefined
      if (chunkRelatedProvided) {
        if (!apiConfig.uploadChunk) {
          errors.push('分片上传已启用，但未配置 uploadChunk 接口地址，未使用默认地址')
        }
        if (!apiConfig.mergeChunks) {
          errors.push('分片上传已启用，但未配置 mergeChunks 接口地址，未使用默认地址')
        }
      }
      // 如果用户提供了其他接口但没有提供分片接口，且分片已启用，给出警告
      if (!apiConfig.uploadChunk && !apiConfig.mergeChunks && apiConfig.uploadFile) {
        // 用户配置了普通上传但没有配置分片接口，使用默认地址
      }
    }
    // 如果完全没有提供配置，使用默认配置，不报错
  }
  
  return {
    valid: errors.length === 0,
    errors
  }
}

/**
 * 文件上传Composable
 * 提供文件上传的响应式状态管理和方法
 * @param apiConfig 接口配置，如果不提供则使用默认配置
 */
export function useFileUpload(apiConfig?: UploadApiConfig) {
  const api = useFileUploadApi(apiConfig)
  
  const uploading = ref(false)
  const uploadingFiles = ref<Map<string, boolean>>(new Map())
  const uploadProgress = ref<Map<string, number>>(new Map())
  
  /**
   * 单文件上传
   */
  const uploadSingleFile = async (
    file: File,
    options?: {
      uploadDTO?: FileUploadDTO
      onProgress?: (progress: number) => void
      onSuccess?: (result: UploadResult) => void
      onError?: (error: Error) => void
      signal?: AbortSignal
    }
  ) => {
    const fileKey = UploadUtils.generateFileKey(file)
    
    try {
      uploading.value = true
      uploadingFiles.value.set(fileKey, true)
      uploadProgress.value.set(fileKey, 0)
      
      const result = await api.uploadFile(
        file,
        options?.uploadDTO,
        (progress) => {
          uploadProgress.value.set(fileKey, progress)
          options?.onProgress?.(progress)
        },
        options?.signal
      )
      
      uploadProgress.value.set(fileKey, 100)
      options?.onSuccess?.(result)
      
      return result
    } catch (error) {
      const err = error as Error
      options?.onError?.(err)
      throw err
    } finally {
      uploadingFiles.value.delete(fileKey)
      uploadProgress.value.delete(fileKey)
      uploading.value = uploadingFiles.value.size > 0
    }
  }
  
  /**
   * 批量文件上传
   */
  const uploadMultipleFiles = async (
    files: File[],
    options?: {
      uploadDTO?: FileUploadDTO
      onProgress?: (progress: number) => void
      onSuccess?: (results: UploadResult[]) => void
      onError?: (error: Error) => void
    }
  ) => {
    const batchKey = `batch_${Date.now()}`
    
    try {
      uploading.value = true
      uploadingFiles.value.set(batchKey, true)
      uploadProgress.value.set(batchKey, 0)
      
      const result = await api.uploadFiles(
        files,
        options?.uploadDTO,
        (progress) => {
          uploadProgress.value.set(batchKey, progress)
          options?.onProgress?.(progress)
        }
      )
      
      uploadProgress.value.set(batchKey, 100)
      options?.onSuccess?.(result.data)
      
      return result.data
    } catch (error) {
      const err = error as Error
      options?.onError?.(err)
      throw err
    } finally {
      uploadingFiles.value.delete(batchKey)
      uploadProgress.value.delete(batchKey)
      uploading.value = uploadingFiles.value.size > 0
    }
  }
  
  /**
   * 分片上传（支持断点续传）
   */
  const uploadWithChunks = async (
    file: File,
    options?: {
      chunkSize?: number
      uploadDTO?: FileUploadDTO
      onProgress?: (progress: number) => void
      onChunkProgress?: (chunkIndex: number, chunkProgress: number, chunkResult?: ChunkUploadResult) => void
      onSuccess?: (result: UploadResult) => void
      onError?: (error: Error) => void
      signal?: AbortSignal
      maxRetries?: number
      // 断点续传相关参数
      resumeFromChunk?: number  // 从哪个分片开始上传（用于断点续传）
      uploadedChunks?: boolean[] // 已上传的分片状态数组
      fileKey?: string  // 文件唯一标识，用于断点续传
    }
  ) => {
    const chunkSize = options?.chunkSize || 2 * 1024 * 1024 // 默认2MB
    const totalChunks = Math.ceil(file.size / chunkSize)
    // 使用传入的fileKey或生成新的fileKey（使用MD5哈希确保固定32位长度）
    let fileKey = options?.fileKey
    if (!fileKey) {
      fileKey = UploadUtils.generateFileKey(file)
    }
    const maxRetries = options?.maxRetries || 3
    
    try {
      uploading.value = true
      uploadingFiles.value.set(fileKey, true)
      uploadProgress.value.set(fileKey, 0)
      
      // 计算文件MD5
      const fileMd5 = await UploadUtils.calculateMD5WithChunks(file, chunkSize)
      
      // 初始化分片上传状态（支持断点续传）
      let uploadedChunks = 0
      const chunkUploadStatus: boolean[] = options?.uploadedChunks || new Array(totalChunks).fill(false)
      
      // 如果提供了fileKey，尝试从后端获取已上传的分片列表
      if (options?.fileKey) {
        try {
          const uploadedChunksResult = await api.getUploadedChunks(fileKey)
          if (uploadedChunksResult?.data && Array.isArray(uploadedChunksResult.data)) {
            uploadedChunksResult.data.forEach(chunkIndex => {
              if (chunkIndex >= 0 && chunkIndex < totalChunks) {
                chunkUploadStatus[chunkIndex] = true
              }
            })
            // 重新计算已上传分片数量
            uploadedChunks = chunkUploadStatus.filter(status => status).length
            // 更新进度
            const progress = Math.round((uploadedChunks / totalChunks) * 100)
            uploadProgress.value.set(fileKey, progress)
            options?.onProgress?.(progress)
          }
        } catch (error) {
          console.warn('获取已上传分片列表失败，将重新上传所有分片:', error)
        }
      }
      
      // 如果提供了resumeFromChunk，从指定分片开始
      const startChunk = options?.resumeFromChunk !== undefined ? options.resumeFromChunk : 0
      
      // 上传每个分片
      for (let i = startChunk; i < totalChunks; i++) {
        // 检查是否被取消
        if (options?.signal?.aborted) {
          throw new DOMException('上传已取消', 'AbortError')
        }
        
        // 如果该分片已上传成功，跳过
        if (chunkUploadStatus[i]) {
          continue
        }
        
        const start = i * chunkSize
        const end = Math.min(start + chunkSize, file.size)
        const chunkFile = file.slice(start, end)
        
        const chunkDTO: ChunkUploadDTO = {
          fileKey,
          filename: file.name,
          fileMd5,
          chunkIndex: i,
          chunkSize: chunkFile.size,
          totalChunks,
          totalSize: file.size
        }
        
        // 分片上传重试机制
        let retryCount = 0
        let uploadSuccess = false
        
        while (retryCount <= maxRetries && !uploadSuccess) {
          try {
            // 检查是否被取消
            if (options?.signal?.aborted) {
              throw new DOMException('上传已取消', 'AbortError')
            }
            
            const chunkResult = await api.uploadChunk(
              chunkFile as File,
              chunkDTO,
              (chunkProgress) => {
                // 分片上传进度回调
                options?.onChunkProgress?.(i, chunkProgress)
                
                // 计算总进度（已完成的分片 + 当前分片进度）
                const totalProgress = Math.round(
                  ((uploadedChunks + chunkProgress / 100) / totalChunks) * 100
                )
                uploadProgress.value.set(fileKey, totalProgress)
                options?.onProgress?.(totalProgress)
              },
              options?.signal
            )
            
            // 从后端响应中获取实际的uploadedChunks和progress
            const chunkData = chunkResult?.data
            if (chunkData && typeof chunkData === 'object') {
              // 更新uploadedChunks计数（使用后端返回的值）
              if (chunkData.uploadedChunks !== undefined) {
                uploadedChunks = chunkData.uploadedChunks
              }
              
              // 更新进度（使用后端返回的值，确保进度准确）
              if (chunkData.progress !== undefined) {
                const totalProgress = Math.min(Math.round(chunkData.progress), 100)
                uploadProgress.value.set(fileKey, totalProgress)
                options?.onProgress?.(totalProgress)
              }
              
              // 调用onChunkProgress回调，传递完整的后端响应
              options?.onChunkProgress?.(i, 100, chunkData)
            }
            
            uploadSuccess = true
            chunkUploadStatus[i] = true
            uploadedChunks++
            
          } catch (error: any) {
            // 如果是AbortError（用户暂停），直接抛出，不重试
            if (error.name === 'AbortError' || error.message?.includes('aborted')) {
              throw error
            }
            
            retryCount++
            
            if (retryCount > maxRetries) {
              throw new Error(`分片 ${i + 1}/${totalChunks} 上传失败，已重试 ${maxRetries} 次`)
            }
            
            console.warn(`分片 ${i + 1}/${totalChunks} 上传失败，正在重试 (${retryCount}/${maxRetries})...`, error)
            
            // 指数退避策略
            await new Promise(resolve => setTimeout(resolve, Math.min(1000 * Math.pow(2, retryCount - 1), 5000)))
          }
        }
      }
      
      // 合并分片
      const result = await api.mergeChunks(fileKey, options?.uploadDTO)
      
      uploadProgress.value.set(fileKey, 100)
      options?.onSuccess?.(result)
      
      return result
    } catch (error: any) {
      // 如果是AbortError（用户取消），不调用onError
      if (error.name === 'AbortError' || error.message?.includes('aborted')) {
        throw error
      }
      const err = error as Error
      options?.onError?.(err)
      throw err
    } finally {
      uploadingFiles.value.delete(fileKey)
      uploadProgress.value.delete(fileKey)
      uploading.value = uploadingFiles.value.size > 0
    }
  }
  
  /**
   * 取消上传
   */
  const cancelFileUpload = async (fileKey: string) => {
    try {
      await api.cancelUpload(fileKey)
      uploadingFiles.value.delete(fileKey)
      uploadProgress.value.delete(fileKey)
      uploading.value = uploadingFiles.value.size > 0
    } catch (error) {
      console.error('取消上传失败:', error)
    }
  }
  
  /**
   * 清空上传状态
   */
  const clearUploadStatus = () => {
    uploadingFiles.value.clear()
    uploadProgress.value.clear()
    uploading.value = false
  }
  
  return {
    // 状态
    uploading,
    uploadingFiles,
    uploadProgress,
    
    // 方法
    uploadSingleFile,
    uploadMultipleFiles,
    uploadWithChunks,
    cancelFileUpload,
    clearUploadStatus,
    
    // API
    ...api
  }
}
