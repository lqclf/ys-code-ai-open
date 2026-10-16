import request from '@/utils/request'
import type { FileUploadDTO, UploadResult } from '@/types/upload'

/**
 * 文件上传API接口集合（简化版）
 * 去除分片上传、文件预览、Token下载等接口
 */
export function useFileUploadApi() {
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
        url: '/api/upload/file/upload',
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
        url: '/api/upload/file/upload/batch',
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
        url: `/api/upload/file/delete/physical/${fileId}`,
        method: 'post',
      }) as Promise<{ data: boolean }>
    },

    /**
     * 文件下载
     */
    downloadFile: (fileId: string) => {
      return request({
        url: `/api/upload/file/download/${fileId}`,
        method: 'get',
        responseType: 'blob',
      })
    },

    /**
     * 根据文件ID集合获取文件信息
     */
    getFilesByIds: (fileIds: string[]) => {
      return request({
        url: '/api/upload/file/getByIds',
        method: 'post',
        data: fileIds,
      })
    },
  }
}
