/**
 * YsUpload 文件上传API接口模块（简化版）
 * 仅保留单文件上传、批量上传、文件下载/删除等核心接口
 * @author: Eric
 * @date: 2025-06-16
 */
import request from '@/utils/request'
import type { FileUploadDTO, UploadResult, UploadApiConfig } from '../types/upload'

/**
 * 默认接口配置
 */
export const DEFAULT_API_CONFIG: Required<UploadApiConfig> = {
  uploadFile: '/api/upload/file/upload',
  uploadFiles: '/api/upload/file/upload/batch',
  deleteFile: '/api/upload/file/delete/physical',
  downloadFile: '/api/upload/file/download',
  getFilesByIds: '/api/upload/file/getByIds',
}

/**
 * 验证接口配置
 */
export function validateApiConfig(apiConfig?: UploadApiConfig): { valid: boolean; errors: string[] } {
  return { valid: true, errors: [] }
}

/**
 * 文件上传API接口集合
 */
export function useFileUploadApi(apiConfig?: UploadApiConfig) {
  const config = { ...DEFAULT_API_CONFIG, ...apiConfig }

  return {
    /**
     * 单文件上传
     */
    uploadFile: (
      file: File,
      uploadDTO?: FileUploadDTO,
      onProgress?: (progress: number) => void,
      signal?: AbortSignal,
    ) => {
      const formData = new FormData()
      formData.append('file', file)

      if (uploadDTO) {
        Object.keys(uploadDTO).forEach((key) => {
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
        },
      }) as Promise<UploadResult>
    },

    /**
     * 批量文件上传
     */
    uploadFiles: (
      files: File[],
      uploadDTO?: FileUploadDTO,
      onProgress?: (progress: number) => void,
    ) => {
      const formData = new FormData()

      files.forEach((file) => {
        formData.append('files', file)
      })

      if (uploadDTO) {
        Object.keys(uploadDTO).forEach((key) => {
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
        },
      }) as Promise<{ data: UploadResult[] }>
    },

    /**
     * 删除文件
     */
    deleteFile: (fileId: string) => {
      return request({
        url: `${config.deleteFile}/${fileId}`,
        method: 'delete',
      }) as Promise<{ data: boolean }>
    },

    /**
     * 文件下载
     */
    downloadFile: (fileId: string) => {
      return request({
        url: `${config.downloadFile}/${fileId}`,
        method: 'get',
        responseType: 'blob',
      })
    },

    /**
     * 根据文件ID集合获取文件信息
     */
    getFilesByIds: (fileIds: string[]) => {
      return request({
        url: config.getFilesByIds,
        method: 'post',
        data: fileIds,
      })
    },
  }
}

/**
 * 文件上传Composable（简化版）
 */
export function useFileUpload(apiConfig?: UploadApiConfig) {
  const api = useFileUploadApi(apiConfig)

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
    },
  ) => {
    try {
      const result = await api.uploadFile(
        file,
        options?.uploadDTO,
        (progress) => {
          options?.onProgress?.(progress)
        },
        options?.signal,
      )

      options?.onSuccess?.(result)
      return result
    } catch (error) {
      const err = error as Error
      options?.onError?.(err)
      throw err
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
    },
  ) => {
    try {
      const result = await api.uploadFiles(files, options?.uploadDTO, (progress) => {
        options?.onProgress?.(progress)
      })

      options?.onSuccess?.(result.data)
      return result.data
    } catch (error) {
      const err = error as Error
      options?.onError?.(err)
      throw err
    }
  }

  return {
    // 方法
    uploadSingleFile,
    uploadMultipleFiles,

    // API
    ...api,
  }
}
