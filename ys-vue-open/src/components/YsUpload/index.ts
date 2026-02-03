import YsUpload from './index.vue'
import YsUploadItem from './YsUploadItem.vue'
import YsUploadProgress from './YsUploadProgress.vue'

export default YsUpload
export { YsUploadItem, YsUploadProgress }
export type { UploadFile, UploadProps, UploadApiConfig } from './types/upload'
export { validateApiConfig } from './api'