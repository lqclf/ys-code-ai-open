/**
 * YsUpload 组件入口文件
 * 导出上传组件、子组件及类型定义
 * @author: Eric
 * @date: 2025-06-16
 */
import YsUpload from './index.vue'
import YsUploadItem from './YsUploadItem.vue'
import YsUploadProgress from './YsUploadProgress.vue'

export default YsUpload
export { YsUploadItem, YsUploadProgress }
export type { UploadFile, UploadProps, UploadApiConfig } from './types/upload'
export { validateApiConfig } from './api'