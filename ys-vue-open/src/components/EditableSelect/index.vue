<template>
  <div class="editable-select" ref="containerRef">
    <div>
      <el-input
        ref="inputRef"
        v-model="searchQuery"
        type="text"
        :placeholder="placeholder"
        @focus="handleFocus"
        @input="handleInput"
        @keydown.down.prevent="navigateOptions('down')"
        @keydown.up.prevent="navigateOptions('up')"
        @keydown.enter.prevent="selectCurrentOption"
        @keydown.esc="closeDropdown"
      />
      <div class="dropdown-icon" @click="toggleDropdown">
        <el-icon v-if="!isDropdownOpen"><ele-ArrowDown /></el-icon>
        <el-icon v-else><ele-ArrowUp /></el-icon>
      </div>
    </div>
    
    <transition name="dropdown">
      <div v-if="isDropdownOpen" class="dropdown-list">
        <div v-if="filteredOptions.length === 0" class="no-options">
          无匹配选项
        </div>
        <div
          v-for="(option, index) in filteredOptions"
          :key="option.value"
          class="dropdown-item"
          :class="{ 'selected': index === highlightedIndex }"
          @click="selectOption(option)"
        >
          {{ option.label }}
        </div>
        <div
          v-if="allowCustom && searchQuery && !isOptionSelected"
          class="dropdown-item custom-option"
          @click="addCustomOption"
        >
          添加 "{{ searchQuery }}"
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'

const props = defineProps({
  modelValue: {
    type: [String, Number, Object],
    default: ''
  },
  options: {
    type: Array,
    required: true,
    default: () => []
  },
  placeholder: {
    type: String,
    default: '请选择或输入'
  },
  allowCustom: {
    type: Boolean,
    default: false
  },
  valueKey: {
    type: String,
    default: 'value'
  },
  labelKey: {
    type: String,
    default: 'label'
  }
})

const emit = defineEmits(['update:modelValue', 'change', 'add-custom'])

const inputRef = ref(null)
const containerRef = ref(null)
const searchQuery = ref('')
const isDropdownOpen = ref(false)
const highlightedIndex = ref(-1)

// 计算当前选中的选项是否存在于列表中
const isOptionSelected = computed(() => {
  if (!props.modelValue) return false
  return props.options.some(option => 
    option[props.valueKey] === props.modelValue[props.valueKey] || 
    option[props.valueKey] === props.modelValue
  )
})

// 过滤后的选项列表
const filteredOptions = computed(() => {
  if (!searchQuery.value) return props.options
  
  const query = searchQuery.value.toLowerCase()
  return props.options.filter(option => 
    option[props.labelKey].toLowerCase().includes(query)
  )
})

// 监听模型值变化，更新输入框内容
watch(() => props.modelValue, (newVal) => {
  if (!newVal) {
    searchQuery.value = ''
    return
  }
  
  // 如果是对象类型，获取label值
  if (typeof newVal === 'object' && newVal !== null) {
    searchQuery.value = newVal[props.labelKey] || ''
  } else {
    // 如果是基本类型，查找匹配的选项
    const option = props.options.find(opt => opt[props.valueKey] === newVal)
    searchQuery.value = option ? option[props.labelKey] : String(newVal)
  }
}, { immediate: true })

// 处理输入框聚焦
const handleFocus = () => {
  if (!isDropdownOpen.value) {
    openDropdown()
  }
}

// 处理输入
const handleInput = () => {
  if (!isDropdownOpen.value) {
    openDropdown()
  }
  highlightedIndex.value = -1
  
  // 如果允许自定义且输入不为空，触发更新
  if (props.allowCustom && searchQuery.value) {
    emitCustomValue()
  }
}

// 打开下拉列表
const openDropdown = () => {
  isDropdownOpen.value = true
  nextTick(() => {
    if (inputRef.value) {
      inputRef.value.focus()
    }
  })
}

// 关闭下拉列表
const closeDropdown = () => {
  isDropdownOpen.value = false
  highlightedIndex.value = -1
}

// 切换下拉列表状态
const toggleDropdown = () => {
  if (isDropdownOpen.value) {
    closeDropdown()
  } else {
    openDropdown()
  }
}

// 键盘导航选项
const navigateOptions = (direction) => {
  if (!isDropdownOpen.value) {
    openDropdown()
    return
  }
  
  const itemCount = filteredOptions.value.length
  if (itemCount === 0) return
  
  if (direction === 'down') {
    highlightedIndex.value = (highlightedIndex.value + 1) % itemCount
  } else {
    highlightedIndex.value = highlightedIndex.value <= 0 
      ? itemCount - 1 
      : highlightedIndex.value - 1
  }
  
  // 滚动到高亮项
  scrollToHighlighted()
}

// 滚动到高亮项
const scrollToHighlighted = () => {
  nextTick(() => {
    const highlightedElement = containerRef.value?.querySelector('.dropdown-item.selected')
    if (highlightedElement) {
      highlightedElement.scrollIntoView({ block: 'nearest' })
    }
  })
}

// 选择当前高亮选项
const selectCurrentOption = () => {
  if (highlightedIndex.value >= 0 && highlightedIndex.value < filteredOptions.value.length) {
    selectOption(filteredOptions.value[highlightedIndex])
  }
}

// 选择选项
const selectOption = (option) => {
  searchQuery.value = option[props.labelKey]
  emit('update:modelValue', option[props.valueKey])
  emit('change', option)
  closeDropdown()
}

// 添加自定义选项
const addCustomOption = () => {
  if (!searchQuery.value.trim()) return
  
  emit('add-custom', searchQuery.value)
  emitCustomValue()
  closeDropdown()
}

// 触发自定义值
const emitCustomValue = () => {
  emit('update:modelValue', searchQuery.value)
  emit('change', { 
    [props.labelKey]: searchQuery.value, 
    [props.valueKey]: searchQuery.value 
  })
}

// 点击外部关闭下拉列表
const handleClickOutside = (event) => {
  if (containerRef.value && !containerRef.value.contains(event.target)) {
    closeDropdown()
  }
}
// 添加重置方法
const reset = () => {
  searchQuery.value = ''
  closeDropdown()
}

// 暴露重置方法给父组件
defineExpose({
  reset
})

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.editable-select {
  position: relative;
  width: 100%;
  font-family: inherit;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #fff;
  transition: border-color 0.2s;
  padding: 1px 15px;
}

.input-wrapper:hover {
  border-color: #c0c4cc;
}

.input-wrapper:focus-within {
  border-color: #409eff;
}



.dropdown-icon {
  position: absolute;
  right: 8px;
  top: 2px;
  cursor: pointer;
  color: #c0c4cc;
  transition: transform 0.3s;
}

.dropdown-list {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  max-height: 200px;
  margin-top: 5px;
  overflow-y: auto;
  background-color: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.dropdown-item {
  padding: 1px 15px;
  font-size: 14px;
  color: #606266;
  cursor: pointer;
  transition: background-color 0.2s;
}

.dropdown-item:hover {
  background-color: #f5f7fa;
}

.dropdown-item.selected {
  background-color: #ecf5ff;
  color: #409eff;
  font-weight: bold;
}

.custom-option {
  color: #409eff;
  font-style: italic;
}

.no-options {
  padding: 10px 15px;
  color: #909399;
  text-align: center;
  font-size: 14px;
}

/* 下拉动画 */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>