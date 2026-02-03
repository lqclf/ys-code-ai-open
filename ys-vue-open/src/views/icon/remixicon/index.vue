<template>
  <div class="page-container">
    <el-card>
      <div class="icon-explorer">
        <div class="header">
          <h1>Remix Icon Explorer</h1>
          <div class="search-container">
            <input v-model="searchQuery" type="text" placeholder="搜索图标..." class="search-input" />
            <button @click="clearSearch" class="clear-btn">清除</button>
          </div>
        </div>

        <div v-if="loading" class="loading-container">
          <div class="spinner"></div>
          <p>加载图标中...</p>
        </div>

        <div v-else-if="error" class="error-container">
          <p>加载图标时出错: {{ error }}</p>
          <button @click="loadIcons" class="retry-btn">重试</button>
        </div>

        <div v-else>
          <div class="stats">
            <p>共找到 <span class="highlight">{{ filteredIcons.length }}</span> 个图标</p>
            <div class="view-toggle">
              <button @click="viewMode = 'grid'" :class="['view-btn', { active: viewMode === 'grid' }]">
                网格视图
              </button>
              <button @click="viewMode = 'list'" :class="['view-btn', { active: viewMode === 'list' }]">
                列表视图
              </button>
            </div>
          </div>

          <div v-if="viewMode === 'grid'" class="icon-grid">
            <div v-for="icon in filteredIcons" :key="icon" class="icon-card" @click="copyIconClass(icon)">
              <div class="icon-preview">
                <i :class="icon"></i>
              </div>
              <div class="icon-name">{{ icon }}</div>
              <div v-if="copiedIcon === icon" class="copy-feedback">
                <i class="ri-check-line"></i> 已复制!
              </div>
            </div>
          </div>

          <div v-else class="icon-list">
            <div v-for="icon in filteredIcons" :key="icon" class="icon-list-item" @click="copyIconClass(icon)">
              <div class="icon-preview">
                <i :class="icon"></i>
              </div>
              <div class="icon-info">
                <div class="icon-name">{{ icon }}</div>
                <div class="icon-class">{{ icon }}</div>
              </div>
              <div class="copy-action">
                <i class="ri-file-copy-line"></i>
                <span v-if="copiedIcon === icon" class="copy-feedback">
                  <i class="ri-check-line"></i> 已复制
                </span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="showToast" class="toast">
          <i class="ri-check-line"></i> 已复制: {{ copiedIcon }}
        </div>
      </div>
    </el-card>
  </div>
</template>

<script lang="ts" setup name="iconRemixIcon">
import { ref, computed, onMounted } from 'vue'

// 定义响应式数据
const icons = ref<string[]>([])
const loading = ref(true)
const error = ref<string | null>(null)
const searchQuery = ref('')
const viewMode = ref<'grid' | 'list'>('grid')
const copiedIcon = ref<string | null>(null)
const showToast = ref(false)

// 过滤后的图标列表
const filteredIcons = computed(() => {
  if (!searchQuery.value) return icons.value
  const query = searchQuery.value.toLowerCase()
  return icons.value.filter(icon =>
    icon.toLowerCase().includes(query)
  )
})

// 加载图标数据
const loadIcons = async () => {
  loading.value = true
  error.value = null

  try {
    // 直接从本地 remixicon.css 文件加载图标数据
    const response = await fetch('/theme/remixicon/remixicon.css')
    const cssContent = await response.text()

    // 使用更准确的正则表达式提取所有图标类名
    const iconRegex = /\.ri-[a-zA-Z0-9-]+:before\s*\{/g
    const matches = []
    let match

    while ((match = iconRegex.exec(cssContent)) !== null) {
      // 提取类名部分，移除 :before 伪类
      const fullMatch = match[0]
      const className = fullMatch.substring(1, fullMatch.length - 9) // 移除开头的.和结尾的:before
      matches.push(className)
    }

    // 去重并过滤掉非图标类（如尺寸类ri-lg等）
    const uniqueIcons = Array.from(new Set(matches))
      .filter(icon =>
        icon.startsWith('ri-') &&
        !icon.match(/ri-[0-9]+x/) &&
        !icon.match(/ri-(lg|xl|xxs|xs|sm|fw)/)
      )

    icons.value = uniqueIcons.sort()
  } catch (err) {
    console.error('加载图标失败:', err)
    error.value = '无法加载图标数据，请检查网络连接或文件路径'
  } finally {
    loading.value = false
  }
}

// 复制图标类名
const copyIconClass = (iconClass: string) => {
  navigator.clipboard.writeText(iconClass)
    .then(() => {
      copiedIcon.value = iconClass
      showToast.value = true

      setTimeout(() => {
        showToast.value = false
        copiedIcon.value = null
      }, 2000)
    })
    .catch(err => {
      console.error('复制失败:', err)
      // 降级方案
      const textArea = document.createElement('textarea')
      textArea.value = iconClass
      document.body.appendChild(textArea)
      textArea.select()
      document.execCommand('copy')
      document.body.removeChild(textArea)

      copiedIcon.value = iconClass
      showToast.value = true

      setTimeout(() => {
        showToast.value = false
        copiedIcon.value = null
      }, 2000)
    })
}

// 清除搜索
const clearSearch = () => {
  searchQuery.value = ''
}

// 组件挂载时加载图标
onMounted(() => {
  loadIcons()
})
</script>

<style scoped>
.page-container {
  :deep(.el-card__body) {
    overflow: auto;
  }
}

/* 基础样式 */
.icon-explorer {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  padding: 20px;
  color: #333;
}

.header {
  margin-bottom: 20px;
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 18px;
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  color: white;
  animation: fadeInDown 0.8s ease;
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.header h1 {
  font-size: 2.8rem;
  margin-bottom: 20px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.search-container {
  display: flex;
  max-width: 600px;
  margin: 0 auto;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  border-radius: 50px;
  overflow: hidden;
  animation: fadeInUp 1s ease;
}

.search-input {
  flex: 1;
  padding: 15px 20px;
  border: none;
  font-size: 16px;
  outline: none;
  background: rgba(255, 255, 255, 0.9);
  transition: all 0.3s ease;
}

.search-input:focus {
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.3);
}

.clear-btn {
  padding: 0 25px;
  background: #ff6b6b;
  color: white;
  border: none;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.clear-btn:hover {
  background: #ff5252;
  transform: scale(1.05);
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  animation: fadeIn 0.5s ease;
}

.spinner {
  width: 60px;
  height: 60px;
  border: 5px solid rgba(102, 126, 234, 0.2);
  border-radius: 50%;
  border-top-color: #667eea;
  animation: spin 1s ease-in-out infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

/* 错误状态 */
.error-container {
  text-align: center;
  padding: 100px 0;
  color: #e74c3c;
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  animation: fadeIn 0.5s ease;
}

.retry-btn {
  margin-top: 15px;
  padding: 12px 25px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 30px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 10px rgba(102, 126, 234, 0.3);
}

.retry-btn:hover {
  background: #5a6fd8;
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(102, 126, 234, 0.4);
}

/* 统计信息 */
.stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  animation: fadeInUp 0.8s ease;
}

.highlight {
  color: #667eea;
  font-weight: 700;
  font-size: 1.2rem;
}

.view-toggle {
  display: flex;
  gap: 10px;
}

.view-btn {
  padding: 10px 20px;
  background: #f8f9fa;
  border: 2px solid #e9ecef;
  border-radius: 30px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
  color: #495057;
}

.view-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
  box-shadow: 0 4px 10px rgba(102, 126, 234, 0.3);
}

.view-btn:hover:not(.active) {
  background: #e9ecef;
  transform: translateY(-2px);
}

/* 网格视图 */
.icon-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 10px;
  animation: fadeIn 0.8s ease;
}

.icon-card {
  background: white;
  border-radius: 15px;
  padding: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  border: 1px solid #f1f3f4;
  animation: fadeInUp 0.5s ease;
}

.icon-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
  border-color: #667eea;
}

.icon-preview {
  font-size: 2.5rem;
  margin-bottom: 15px;
  color: #616161;
  transition: all 0.3s ease;

  i {
    font-size: 24px;
  }
}

.icon-card:hover .icon-preview {
  transform: scale(1.2);
  color: #764ba2;
}

.icon-name {
  font-size: 0.9rem;
  color: #7896b4;
  word-break: break-all;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  font-weight: 500;
}

.copy-feedback {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  background: linear-gradient(135deg, #00b09b, #96c93d);
  color: white;
  padding: 12px;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transform: translateY(-100%);
  transition: transform 0.3s ease;
  font-weight: 500;
}

.icon-card:hover .copy-feedback {
  transform: translateY(0);
}

/* 列表视图 */
.icon-list {
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  animation: fadeIn 0.8s ease;
}

.icon-list-item {
  display: flex;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid #f1f3f4;
  cursor: pointer;
  transition: all 0.2s ease;
}

.icon-list-item:last-child {
  border-bottom: none;
}

.icon-list-item:hover {
  background: #f8f9fa;
  transform: translateX(5px);
}

.icon-list .icon-preview {
  font-size: 2rem;
  margin-right: 20px;
  color: #667eea;
  width: 50px;
  text-align: center;
  transition: all 0.3s ease;
}

.icon-list-item:hover .icon-preview {
  transform: scale(1.2);
  color: #764ba2;
}

.icon-info {
  flex: 1;
}

.icon-list .icon-name {
  font-weight: 600;
  margin-bottom: 5px;
  color: #212529;
}

.icon-class {
  font-size: 0.9rem;
  color: #6c757d;
  font-family: 'Courier New', monospace;
}

.copy-action {
  display: flex;
  align-items: center;
  color: #6c757d;
  font-size: 1.3rem;
  padding: 10px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.copy-action:hover {
  background: #e9ecef;
  color: #667eea;
}

/* Toast 提示 */
.toast {
  position: fixed;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, #00b09b, #96c93d);
  color: white;
  padding: 15px 30px;
  border-radius: 50px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  display: flex;
  align-items: center;
  z-index: 1000;
  animation: slideUp 0.3s ease, fadeOut 0.3s ease 1.7s;
  animation-fill-mode: forwards;
  font-weight: 500;
}

@keyframes slideUp {
  from {
    transform: translate(-50%, 100px);
    opacity: 0;
  }

  to {
    transform: translate(-50%, 0);
    opacity: 1;
  }
}

@keyframes fadeOut {
  to {
    opacity: 0;
    visibility: hidden;
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .icon-explorer {
    padding: 15px;
  }

  .header {
    padding: 20px;
  }

  .header h1 {
    font-size: 2rem;
  }

  .search-container {
    max-width: 100%;
  }

  .stats {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }

  .icon-grid {
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
    gap: 10px;
  }

  .icon-card {
    padding: 20px 10px;
  }

  .icon-preview {
    font-size: 2rem;
  }

  .icon-list-item {
    padding: 15px;
  }

  .icon-list .icon-preview {
    font-size: 1.5rem;
    margin-right: 15px;
  }
}

@media (max-width: 480px) {
  .icon-grid {
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  }

  .view-btn {
    padding: 8px 15px;
    font-size: 0.9rem;
  }

  .toast {
    padding: 12px 20px;
    font-size: 0.9rem;
  }
}
</style>