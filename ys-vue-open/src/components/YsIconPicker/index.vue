<!--
 * 图标选择器组件
 * 支持Element Plus图标、Remix Icon、IconFont三种图标库的搜索和选择
 * @author: Eric
 * @date: 2025-06-16
 -->
<template>
    <div class="icon-selector w100 h100">
        <el-input
            v-model="state.fontIconSearch"
            :placeholder="state.fontIconPlaceholder"
            :clearable="clearable"
            :disabled="disabled"
            :size="size"
            ref="inputWidthRef"
            @clear="onClearFontIcon"
            @focus="onIconFocus"
            @blur="onIconBlur"
        >
            <template #prepend>
                <SvgIcon
                    :name="state.fontIconPrefix === '' ? prepend : state.fontIconPrefix"
                    class="font14"
                    v-if="state.fontIconPrefix === '' ? prepend?.indexOf('ele-') > -1 : state.fontIconPrefix?.indexOf('ele-') > -1"
                />
                <i v-else :class="state.fontIconPrefix === '' ? prepend : state.fontIconPrefix" class="font14"></i>
            </template>
        </el-input>
        <el-popover
            placement="right"
            :width="state.fontIconWidth"
            transition="el-zoom-in-top"
            popper-class="icon-selector-popper"
            trigger="click"
            :virtual-ref="inputWidthRef"
            virtual-triggering
            ref="popoverRef"
            @before-enter="onPopoverShow"
            @after-leave="onPopoverHide"
        >
            <template #default>
                <div class="icon-selector-warp">
                    <div class="icon-selector-warp-title">{{ title }}</div>
                    <div class="icon-selector-tabs-container">
                        <el-tabs v-model="state.fontIconTabActive" @tab-click="onIconClick" class="icon-selector-tabs">
                            <el-tab-pane lazy label="ele" name="ele">
                                <IconList 
                                    :list="fontIconSheetsFilterList" 
                                    :empty="emptyDescription" 
                                    :prefix="state.fontIconPrefix" 
                                    :width="state.fontIconWidth"
                                    @get-icon="onColClick" 
                                />
                            </el-tab-pane>
                            <el-tab-pane lazy label="remixicon" name="remixicon" >
                                <IconList 
                                    :list="remixiconFilterList" 
                                    :icon-names="state.remixiconDetails"
                                    :empty="emptyDescription" 
                                    :prefix="state.fontIconPrefix" 
                                    :width="state.fontIconWidth"
                                    @get-icon="onColClick" 
                                />
                            </el-tab-pane> 
                            <el-tab-pane lazy label="iconfont" name="iconfont">
                                <IconList 
                                    :list="iconfontFilterList" 
                                    :icon-names="state.iconfontDetails"
                                    :empty="emptyDescription" 
                                    :prefix="state.fontIconPrefix" 
                                    :width="state.fontIconWidth"
                                    @get-icon="onColClick" 
                                />
                            </el-tab-pane>
                        </el-tabs>
                    </div>
                </div>
            </template>
        </el-popover>
    </div>
</template>
<script setup lang="ts" name="iconSelector">
import { defineAsyncComponent, ref, reactive, onMounted, computed, watch } from 'vue';
import type { TabsPaneContext } from 'element-plus';
import initIconfont from '@/utils/getStyleSheets';
import '@/theme/iconSelector.scss';
// 定义父组件传过来的值
const props = defineProps({
    // 输入框前置内容
    prepend: {
        type: String,
        default: () => 'ele-Pointer',
    },
    // 输入框占位文本
    placeholder: {
        type: String,
        default: () => '请输入内容搜索图标或者选择图标',
    },
    // 输入框占位文本
    size: {
        type: String,
        default: () => 'default',
    },
    // 弹窗标题
    title: {
        type: String,
        default: () => '请选择图标',
    },
    // 禁用
    disabled: {
        type: Boolean,
        default: () => false,
    },
    // 是否可清空
    clearable: {
        type: Boolean,
        default: () => true,
    },
    // 自定义空状态描述文字
    emptyDescription: {
        type: String,
        default: () => '无相关图标',
    },
    // 双向绑定值，默认为 modelValue
    modelValue: String,
});
// 定义子组件向父组件传值/事件
const emit = defineEmits(['update:modelValue', 'get', 'clear']);
// 引入组件
const IconList = defineAsyncComponent(() => import('@/components/YsIconPicker/list.vue'));
// 定义变量内容
/** 输入框引用 */
const inputWidthRef = ref();
const popoverRef = ref(); // 弹出框引用
/** 图标选择器状态 */
const state = reactive({
    fontIconPrefix: '', // 当前选中图标前缀
    fontIconWidth: 380, // 选择器宽度
    fontIconSearch: '', // 搜索关键词
    fontIconPlaceholder: '', // 输入框占位文本
    fontIconTabActive: '', // 当前激活的Tab
    fontIconList: { // 各图标库列表
        ele: [],
        remixicon: [],
        iconfont: [],
    },
    iconfontDetails: {} as Record<string, string>, // iconfont图标详情
    remixiconDetails: {} as Record<string, string>, // remixicon图标详情
    isPopoverVisible: false, // 弹出框是否可见
});

/**
 * 输入框获取焦点时处理
 * 有值时改变placeholder显示当前值
 */
const onIconFocus = () => {
    if (!props.modelValue) return false;
    state.fontIconSearch = '';
    state.fontIconPlaceholder = props.modelValue;
};
/**
 * 输入框失去焦点时处理
 * 空值清空输入，非选中图标时恢复原值
 */
const onIconBlur = () => {
    const list = fontIconTabNameList();
    setTimeout(() => {
        const icon = list.filter((icon: string) => icon === state.fontIconSearch);
        if (icon.length <= 0) state.fontIconSearch = '';
    }, 300);
};
/** 弹出框显示回调 */
const onPopoverShow = () => {
    state.isPopoverVisible = true;
};
/** 弹出框隐藏回调 */
const onPopoverHide = () => {
    state.isPopoverVisible = false;
};
/** Element Plus图标过滤列表 */
const fontIconSheetsFilterList = computed(() => {
    const list = fontIconTabNameList();
    if (!state.fontIconSearch) return list;
    let search = state.fontIconSearch.trim().toLowerCase();
    return list.filter((item: string) => {
        if (item.toLowerCase().indexOf(search) !== -1) return item;
    });
});
/** Remix Icon图标过滤列表 */
const remixiconFilterList = computed(() => {
    const list = state.fontIconList.remixicon;
    if (!state.fontIconSearch) return list;
    let search = state.fontIconSearch.trim().toLowerCase();
    return list.filter((item: string) => {
        if (item.toLowerCase().indexOf(search) !== -1) return item;
    });
});

/** IconFont图标过滤列表 */
const iconfontFilterList = computed(() => {
    const list = state.fontIconList.iconfont;
    if (!state.fontIconSearch) return list;
    let search = state.fontIconSearch.trim().toLowerCase();
    return list.filter((item: string) => {
        if (item.toLowerCase().indexOf(search) !== -1) return item;
    });
});
/**
 * 根据当前Tab获取对应图标列表
 * @returns 当前Tab对应的图标名称列表
 */
const fontIconTabNameList = () => {
    let iconList: any = [];
    if (state.fontIconTabActive === 'ele') iconList = state.fontIconList.ele;
    else if (state.fontIconTabActive === 'remixicon') iconList = state.fontIconList.remixicon;
    else if (state.fontIconTabActive === 'iconfont') iconList = state.fontIconList.iconfont;
    return iconList;
};
/** 处理图标双向绑定数值回显 */
const initModeValueEcho = () => {
    if (props.modelValue === '') return ((<string | undefined>state.fontIconPlaceholder) = props.placeholder);
    (<string | undefined>state.fontIconPlaceholder) = props.modelValue;
    (<string | undefined>state.fontIconPrefix) = props.modelValue;
};
/**
 * 判断图标类型并设置Tab高亮
 * @returns 图标类型名称
 */
const initFontIconName = () => {
    let name = 'ele';
    if (props.modelValue && props.modelValue.indexOf('ele-') > -1) name = 'ele';
    else if (props.modelValue && props.modelValue.indexOf('ri-') > -1) name = 'remixicon';
    else if (props.modelValue && props.modelValue.startsWith('cn ')) name = 'iconfont'; // 修改这里
    
    // 初始化 tab 高亮回显
    state.fontIconTabActive = name;
    return name;
};
/**
 * 初始化图标数据
 * @param name 图标类型名称
 */
const initFontIconData = async (name: string) => {
    if (name === 'ele') {
        // element plus 图标
        if (state.fontIconList.ele.length > 0) return;
        await initIconfont.ele().then((res: any) => {
            state.fontIconList.ele = res;
        });
    } else if (name === 'remixicon') {
        // remixicon ``
        if (state.fontIconList.remixicon.length > 0) return;
        await initIconfont.remixicon().then((res: any) => {
            state.fontIconList.remixicon = res.iconList;
            state.remixiconDetails = res.iconfontDetails;
        });
    } else if (name === 'iconfont') {
        // 加载iconfont图标
        if (state.fontIconList.iconfont.length > 0) return;
        // 使用getStyleSheets.ts中的方法获取iconfont图标
        await initIconfont.iconfont().then((res: any) => {
            state.fontIconList.iconfont = res.iconList;
            state.iconfontDetails = res.iconfontDetails;
        });
    }
    
    // 初始化 input 的 placeholder
    state.fontIconPlaceholder = props.placeholder;
    // 初始化双向绑定回显
    initModeValueEcho();
};
/**
 * 图标Tab切换点击
 * @param pane Tab面板上下文
 */
const onIconClick = (pane: TabsPaneContext) => {
    initFontIconData(pane.paneName as string);
    inputWidthRef.value.focus();
};
/**
 * 选中图标点击
 * @param v 图标名称
 */
const onColClick = (v: string) => {
    state.fontIconPlaceholder = v;
    state.fontIconPrefix = v;
    emit('get', state.fontIconPrefix);
    emit('update:modelValue', state.fontIconPrefix);
    // 移除inputWidthRef.value.focus()调用，避免触发焦点事件导致tab切换
};
/** 清空当前选中的图标 */
const onClearFontIcon = () => {
    state.fontIconPrefix = '';
    emit('clear', state.fontIconPrefix);
    emit('update:modelValue', state.fontIconPrefix);
};
let resizeTimer: number;
// 监听页面宽度改变
const initResize = () => {
    window.addEventListener('resize', () => {
         // 使用防抖函数，避免频繁触发
        clearTimeout(resizeTimer);
        resizeTimer = window.setTimeout(() => {
        }, 100);
    });
};
// 页面加载时
onMounted(() => {
    // 初始化图标数据
    initFontIconData(initFontIconName());
    // 初始化窗口大小改变监听
    initResize();
    // 获取输入框宽度
});
// 监听双向绑定 modelValue 的变化
watch(
    () => props.modelValue,
    () => {
        initModeValueEcho();
        initFontIconName();
    }
);
</script>