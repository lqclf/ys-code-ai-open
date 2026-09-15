<!--
 * 图标列表组件
 * 展示图标网格、支持搜索过滤、Tooltip预览和滚动定位
 * @author: Eric
 * @date: 2025-06-16
 -->
<template>
    <div class="icon-selector-warp-row">
        <el-scrollbar 
            ref="selectorScrollbarRef" 
            @scroll="handleScroll"
            :view-class="'icon-scrollbar'"
        >
            <!-- 使用flex布局替代el-row和el-col，实现响应式图标排列 -->
            <div class="icons-container" v-if="props.list.length > 0">
                <div 
                    v-for="(v, k) in list" 
                    :key="k" 
                    class="icon-item"
                    :class="{ 'icon-selector-active': prefix === v }"
                    @click="onColClick(v as string)"
                    @mouseenter="showTooltip(v as string, $event)"
                    @mouseleave="hideTooltip"
                >
                    <!-- 根据图标类型使用不同的渲染方式 -->
                    <SvgIcon :name="v" v-if="(v as string).startsWith('ele-')" />

                    <i :class="v" v-else-if="(v as string).startsWith('ri') || (v as string).startsWith('cn ')"></i>
                </div>
            </div>
            <el-empty :image-size="100" v-if="list.length <= 0" :description="empty"></el-empty>
            
            <!-- 底部信息栏 -->
            <div class="icon-info-bar" v-if="props.list.length > 0">
                <div class="info-item">
                    <span class="label">图标总数：</span>
                    <span class="value">{{ props.list.length }}</span>
                </div>
                <div class="info-item">
                    <span class="label">当前位置：</span>
                    <span class="value">{{ currentRow }}/{{ totalRows }}</span>
                </div>
            </div>
        </el-scrollbar>
         <!-- 自定义tooltip -->
        <div 
            v-show="tooltipVisible" 
            class="icon-tooltip"
            :style="tooltipStyle"
        >
            <div class="tooltip-content">
                <div class="tooltip-icon">
                    <SvgIcon :name="tooltipIcon" v-if="tooltipIcon.startsWith('ele-')" />
                    <i :class="tooltipIcon" v-else-if="tooltipIcon.startsWith('fa ') || tooltipIcon.startsWith('cn ')"></i>
                </div>
                <div class="tooltip-info">
                <div class="tooltip-name">{{ props.iconNames[tooltipIcon] || tooltipIcon }}</div>
                <div class="tooltip-type">点击选择</div>
            </div>
            </div>
            <div class="tooltip-arrow"></div>
        </div>
    </div>
</template>
<script setup lang="ts" name="iconSelectorList">
import { computed, ref, watch, onMounted, nextTick } from 'vue';
// 定义父组件传过来的值
const props = defineProps({
    // 图标列表数据
    list: {
        type: Array,
        default: () => [] as string[],
    },
    // 图标名称映射
    iconNames: {
        type: Object,
        default: () => ({}),
    },
    // 自定义空状态描述文字
    empty: {
        type: String,
        default: () => '无相关图标',
    },
    // 高亮当前选中图标
    prefix: {
        type: String,
        default: () => '',
    },
    // 选择框宽度
    width: {
        type: Number,
        default: () => 380,
    },
});
// 定义子组件向父组件传值/事件
const emit = defineEmits(['get-icon']);
// 获取滚动条引用
const selectorScrollbarRef = ref();
// 计算每个图标项的宽度（固定为50px）
const iconItemWidth = 37;
// Tooltip相关状态
const tooltipVisible = ref(false);
const tooltipIcon = ref('');
const tooltipStyle = ref({
    left: '0px',
    top: '0px',
});
// 计算每行可容纳的图标数量
const iconsPerRow = computed(() => {
    //获取icons-container的宽度
    // 减去内边距（左右各15px）和加上一个gap: 5px，减去其他宽度22
    const availableWidth = props.width - 30 + 5 - 22;
    // 计算每行可容纳的图标数量，至少3个，最多8个
    return Math.max(3, Math.min(20, Math.floor(availableWidth / iconItemWidth)));
});
// 计算总行数
const totalRows = computed(() => {
    return Math.ceil(props.list.length / iconsPerRow.value);
});
// 当前显示的行数
const currentRow = ref(1);
// 监听宽度变化，确保图标布局正确
watch(() => props.width, () => {
    // 宽度变化时，可以在这里添加必要的逻辑
    // 由于我们使用了计算属性，布局会自动更新
    updateCurrentRow();
});
// 监听图标列表变化，更新行数信息
watch(() => props.list.length, () => {
    updateCurrentRow();
});
// 处理滚动事件
const handleScroll = ({ scrollTop }: { scrollTop: number }) => {
    updateCurrentRow(scrollTop);
};
// 更新当前行数
const updateCurrentRow = (scrollTop = 0) => {
    nextTick(() => {
        const scrollbar = selectorScrollbarRef.value;
        if (scrollbar && scrollbar.wrapRef) {
            // 获取容器高度
            const containerHeight = scrollbar.wrapRef.clientHeight;
            // 获取内容高度
            const scrollHeight = scrollbar.wrapRef.scrollHeight;
            
            // 计算图标项的高度（包括边框和间距）
            // const iconItemHeight = 37; // 32px高度 + 5px间距
            
            // 计算总行数
            const calculatedTotalRows = Math.ceil(props.list.length / iconsPerRow.value);
            
            // 计算当前滚动位置对应的行数
            // 使用比例计算，确保滚动到最底部时显示最后一行
            const scrollRatio = scrollHeight > containerHeight ? scrollTop / (scrollHeight - containerHeight) : 0;
            const row = Math.floor(scrollRatio * (calculatedTotalRows - 1)) + 1;
            
            // 确保行数在有效范围内
            currentRow.value = Math.max(1, Math.min(row, calculatedTotalRows));
        }
    });
};
// 显示tooltip
const showTooltip = (iconName: string, event: MouseEvent) => {
    tooltipIcon.value = iconName;
    tooltipVisible.value = true;
    
    // 获取图标元素的位置
    const target = event.target as HTMLElement;
    const rect = target.getBoundingClientRect();
    
    // 计算tooltip位置
    const tooltipWidth = 200; // tooltip宽度
    const tooltipHeight = 60; // tooltip高度
    const arrowHeight = 8; // 箭头高度
    
    // 默认位置在图标上方
    let left = rect.left + rect.width / 2 - tooltipWidth / 2;
    let top = rect.top - tooltipHeight - arrowHeight;
    
    // 检查是否超出视窗边界
    const viewportWidth = window.innerWidth;
    const viewportHeight = window.innerHeight;
    
    // 水平方向调整
    if (left < 10) {
        left = 10;
    } else if (left + tooltipWidth > viewportWidth - 10) {
        left = viewportWidth - tooltipWidth - 10;
    }
    
    // 垂直方向调整
    if (top < 10) {
        // 如果上方空间不足，显示在下方
        top = rect.bottom + arrowHeight;
    }
    
    // 更新tooltip位置
    tooltipStyle.value = {
        left: `${left}px`,
        top: `${top}px`,
    };
};
// 隐藏tooltip
const hideTooltip = () => {
    tooltipVisible.value = false;
};
// 当前 icon 图标点击时
const onColClick = (v: string) => {
    emit('get-icon', v);
};
// 组件挂载时初始化
onMounted(() => {
    updateCurrentRow();
});
</script>
<style scoped lang="scss">
.icon-selector-warp-row {
    height: 230px;
    overflow: hidden;
    position: relative;
    
    .el-scrollbar__bar.is-horizontal {
        display: none;
    }
    
    // 使用flex布局实现响应式图标排列
    .icons-container {
        display: flex;
        flex-wrap: wrap;
        padding: 10px 15px;
        gap: 5px; // 图标之间的间距
        justify-content: flex-start; // 左对齐
    }
    
    .icon-item {
        // 固定图标项宽度
        width: 40px;
        height: 40px;
        display: flex;
        justify-content: center;
        align-items: center;
        border: 1px solid var(--el-border-color);
        border-radius: 5px;
        transition: all 0.3s;
        cursor: pointer;
        flex-shrink: 0; // 防止图标被压缩
        
        i {
            font-size: 20px;
            color: var(--el-text-color-regular);
        }
        
        &:hover {
            background-color: var(--el-color-primary-light-9);
            border: 1px solid var(--el-color-primary-light-5);
            
            i {
                color: var(--el-color-primary);
            }
        }
    }
    
    .icon-selector-active {
        background-color: var(--el-color-primary-light-9);
        border: 1px solid var(--el-color-primary-light-5);
        
        i {
            color: var(--el-color-primary);
        }
    }
    
    // 底部信息栏
    .icon-info-bar {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        height: 30px;
        background-color: var(--el-fill-color-light);
        border-top: 1px solid var(--el-border-color-lighter);
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 20px;
        padding: 0 15px;
        font-size: 12px;
        color: var(--el-text-color-secondary);
        z-index: 1;
        
        .info-item {
            display: flex;
            align-items: center;
            gap: 5px;
            
            .label {
                color: var(--el-text-color-placeholder);
            }
            
            .value {
                color: var(--el-text-color-regular);
                font-weight: 500;
            }
        }
    }
    
    // 修改滚动条样式，避免覆盖内容
    :deep(.el-scrollbar__view) {
        padding-bottom: 30px; // 为底部信息栏留出空间
    }
    
    :deep(.el-scrollbar__wrap) {
        padding-bottom: 30px; // 为底部信息栏留出空间
    }
    
    // 自定义tooltip样式
    .icon-tooltip {
        position: fixed;
        z-index: 9999;
        background-color: white;
        border-radius: 6px;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        padding: 8px 12px;
        pointer-events: none;
        transition: opacity 0.2s, transform 0.2s;
        
        .tooltip-content {
            display: flex;
            align-items: center;
            
            .tooltip-icon {
                margin-right: 10px;
                display: flex;
                align-items: center;
                justify-content: center;
                width: 24px;
                height: 24px;
                
                i {
                    font-size: 18px;
                    color: var(--el-color-primary);
                }
            }
            
            .tooltip-info {
                .tooltip-name {
                    font-size: 14px;
                    font-weight: 500;
                    color: var(--el-text-color-primary);
                    margin-bottom: 2px;
                }
                
                .tooltip-type {
                    font-size: 12px;
                    color: var(--el-text-color-secondary);
                }
            }
        }
        
        .tooltip-arrow {
            position: absolute;
            width: 0;
            height: 0;
            border-style: solid;
        }
        
        // 箭头向上
        &[style*="top"] {
            .tooltip-arrow {
                bottom: -8px;
                left: 50%;
                margin-left: -8px;
                border-width: 8px 8px 0;
                border-color: white transparent transparent;
            }
        }
        
        // 箭头向下
        &[style*="bottom"] {
            .tooltip-arrow {
                top: -8px;
                left: 50%;
                margin-left: -8px;
                border-width: 0 8px 8px;
                border-color: transparent transparent white;
            }
        }
    }
}
</style>