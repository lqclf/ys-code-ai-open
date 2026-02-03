<template>
  <!--
    NoticeCenter 通知中心组件模板
    
    模板结构说明：
    1. 使用 YsDialog 弹窗组件作为容器，包裹整个通知中心界面
    2. 弹窗分为三个主要区域：
       - 顶部标签栏（notice-center-tabs）：支持全部/未读/已读三种状态切换
       - 左侧来源侧边栏（source-sidebar）：按消息来源分组显示
       - 右侧消息表格（table-container）：展示消息列表
    3. 使用具名插槽和默认插槽实现表格自定义渲染
    4. 搜索表单支持标题搜索和多种快捷操作按钮
  -->
  <YsDialog
    v-model="dialogVisible"
    :title="'通知中心'"
    :width="displayConfig.center?.width || '90%'"
    :height="displayConfig.center?.height || '90vh'"
    :fullscreen="displayConfig.center?.fullscreen"
    @close="handleClose"
  >
    <!--
      notice-center 主容器
      应用主题样式（themeClass）实现不同主题适配
      包含标签栏、侧边栏和内容区的完整布局
    -->
    <div class="notice-center" :class="themeClass">
      <!--
        顶部消息状态标签栏
        功能：支持按消息状态（全部/未读/已读）进行筛选
        交互：点击切换当前激活标签，自动刷新数据
        样式：渐变背景、底部高亮指示条、角标显示未读数量
      -->
      <div class="notice-center-tabs">
        <div
          v-for="tab in tabs"
          :key="tab.key"
          class="tab-item"
          :class="{ 'active': activeTab === tab.key }"
          :style="activeTab === tab.key ? activeTabStyle : {}"
          @click="handleTabChange(tab.key as 'all' | 'unread' | 'read')"
        >
          {{ tab.label }}
          <!--
            未读消息角标
            功能：显示未读消息数量，超过99显示99+
            样式：红色渐变背景、白色文字、阴影效果
          -->
          <el-badge
            v-if="tab.count > 0"
            :value="tab.count"
            :max="99"
            class="tab-badge"
          >
          </el-badge>
        </div>
      </div>

      <!--
        通知中心主内容区
        采用左右双栏布局：左侧来源侧边栏 + 右侧消息表格
        sourceSidebar 条件渲染：仅当存在多个消息来源时显示
      -->
      <div class="notice-center-content">
        <!--
          左侧消息来源侧边栏
          功能：按消息来源类型分组显示，支持快速筛选
          数据来源：sourceConfig 配置或自动从 messageList 推断
          交互：点击切换当前来源，显示对应分类消息
        -->
        <div class="source-sidebar" v-if="sourceTabs.length > 0">
          <div
            v-for="source in sourceTabs"
            :key="source.type"
            class="source-tab"
            :class="{ 'active': activeSource === source.type }"
            :style="activeSource === source.type ? activeSourceStyle : {}"
            @click="handleSourceChange(source.type)"
          >
            <i v-if="source.icon" :class="source.icon" class="source-icon"></i>
            <span class="source-label">{{ source.label }}</span>
            <el-badge
              v-if="source.count > 0"
              :value="source.count"
              :max="99"
              class="source-badge"
            >
            </el-badge>
          </div>
        </div>

        <!--
          右侧消息表格区域
          功能：展示消息列表，支持搜索、分页、排序、标记已读等操作
          组件：使用 YsTable 组件实现表格功能
          特性：自定义列模板、搜索表单、分页控制、操作按钮
        -->
        <div class="table-container">
          <YsTable
            ref="tableRef"
            :options="tableOptions"
            :events="tableEvents"
            :show-pagination="interactionConfig?.enablePagination !== false"
            :page-sizes="interactionConfig?.pageSizes || [10, 20, 50, 100]"
            :data="filteredData"
          >
            <!--
              表格顶部搜索表单插槽
              功能：提供搜索和快捷操作按钮
              包含：标题输入框、查询按钮、重置按钮、全部标记已读按钮
            -->
            <template #page-header>
                <el-form :model="searchForm" ref="searchFormRef" label-width="90px">
                  <!--
                    消息标题搜索输入框
                    功能：按标题关键词筛选消息
                    交互：支持回车键搜索、清除按钮
                  -->
                  <el-input
                    placeholder="请输入消息标题"
                    class="w-180 mr20"
                    v-model="searchForm.title"
                    clearable
                    @keyup.enter="searchTable"
                  />
                  <!--
                    查询按钮
                    交互：触发搜索事件，重新加载数据
                  -->
                  <el-button type="primary" class="ml10" @click="searchTable">
                    <i class="ri-search-line"></i>
                    查询
                  </el-button>
                  <!--
                    重置按钮
                    交互：清空搜索条件，重置表格
                  -->
                  <el-button type="default" class="ml10" @click="resetTable">
                    <i class="ri-reset-left-line"></i>
                    重置
                  </el-button>
                  <!--
                    全部标记已读按钮
                    条件渲染：仅在未读标签页且存在未读消息时显示
                    交互：触发全部标记已读事件
                  -->
                  <el-button
                    v-if="activeTab === 'unread' && unreadCount > 0"
                    type="success"
                    class="ml10"
                    @click="handleMarkAllRead"
                  >
                    <i class="ri-check-double-line"></i>
                    全部标记已读
                  </el-button>
                </el-form>
            </template>

            <!--
              发布时间列自定义模板
              功能：格式化时间显示，使用相对时间格式
            -->
            <template #sendTime="{ row }">
              <span>{{ formatTime(row.sendTime) }}</span>
            </template>

            <!--
              阅读状态列自定义模板
              功能：根据 readStatus 字段显示不同状态标签
              样式：未读显示 danger（红色）标签，已读显示 info（灰色）标签
            -->
            <template #readStatus="{ row }">
              <el-tag
                :type="row.readStatus === 0 ? 'danger' : 'info'"
                size="small"
              >
                {{ row.readStatus === 0 ? '未读' : '已读' }}
              </el-tag>
            </template>

            <!--
              操作列自定义模板
              功能：提供查看消息详情的功能
              文本：可自定义按钮文本，默认为"查看"
              样式：可自定义按钮类型，默认为 primary
            -->
            <template #action="{ row }">
              <el-button
                :type="displayConfig.center?.actionButtonType || 'primary'"
                link
                @click="handleView(row)"
              >
                {{ displayConfig.center?.actionButtonText || '查看' }}
              </el-button>
            </template>
          </YsTable>
        </div>
      </div>
    </div>
  </YsDialog>
</template>

<script setup lang="ts" name="NoticeCenter">
/**
 * NoticeCenter 通知中心组件
 * 
 * 组件功能描述：
 * NoticeCenter 是 YsNoticeBar 通知栏组件的核心子组件之一，
 * 负责在弹窗中展示完整的通知中心界面。
 * 
 * 核心功能：
 * 1. 多维度消息筛选：支持按消息状态（全部/未读/已读）和消息来源进行筛选
 * 2. 消息来源分组：按消息类型/来源自动分组显示，支持快速切换
 * 3. 高级搜索功能：支持按标题关键词搜索消息
 * 4. 分页管理：支持配置分页参数，自定义每页条数
 * 5. 批量操作：支持一键标记全部未读消息为已读
 * 6. 主题定制：支持亮色/暗色/自定义三种主题模式
 * 
 * 使用场景：
 * - 点击通知栏的"前往通知中心"按钮时显示
 * - 用户需要查看和管理所有通知消息时
 * - 需要对消息进行批量操作时
 * 
 * 与父组件的通信：
 * - 通过 props 接收消息数据源和配置参数
 * - 通过 emit 派发用户交互事件（搜索、标签切换、消息点击等）
 * - 通过 v-model:visible 控制弹窗的显示与隐藏
 * 
 * 依赖组件：
 * - YsDialog: 弹窗容器组件
 * - YsTable: 表格组件，用于展示消息列表
 * - el-badge: 角标组件，显示未读消息数量
 * - el-input: 输入框组件，用于搜索
 * - el-button: 按钮组件，用于操作
 */

import { ref, computed, watch, reactive } from 'vue';
import { formatPast } from '@/utils/formatTime';
import type { NoticeCenterEmits, MessageItem, DisplayConfig, StyleConfig, MessageSourceConfig } from '@/types/notice';
import type { FormInstance } from 'element-plus';

defineOptions({
  name: 'NoticeCenter'
});

/**
 * ==================== Props 定义 ====================
 */

/**
 * Props 接口说明
 * 
 * NoticeCenter 组件接收以下几类属性：
 * 1. tabDataSource: 核心数据源，包含按状态分组的消息数据
 * 2. sourceConfig: 可选的消息来源配置，定义来源类型的信息
 * 3. displayConfig: 显示配置，控制弹窗尺寸、按钮样式等
 * 4. interactionConfig: 交互配置，控制分页、排序、刷新等行为
 * 5. styleConfig: 样式配置，控制主题样式
 */

/**
 * tabDataSource: 消息数据源
 * 
 * 数据结构说明：
 * - all: 全部消息，按消息来源类型分组的数组
 * - unread: 未读消息，按消息来源类型分组的数组
 * - read: 已读消息，按消息来源类型分组的数组
 * 
 * 每个分组内的数组元素为 MessageItem 类型，包含消息的完整信息
 */
interface Props {
  /** tabDataSource: 消息数据源，按消息状态（全部/未读/已读）和来源类型分组 */
  tabDataSource: {
    all: Record<string, MessageItem[]>;
    unread: Record<string, MessageItem[]>;
    read: Record<string, MessageItem[]>;
  };
  /** sourceConfig: 消息来源配置，定义各来源类型的显示名称、图标等信息（可选） */
  sourceConfig?: MessageSourceConfig[];
  /** displayConfig: 显示配置，控制弹窗尺寸、按钮样式等视觉效果 */
  displayConfig?: DisplayConfig;
  /** interactionConfig: 交互配置，控制分页、排序、刷新等交互行为 */
  interactionConfig?: {
    /** reloadOnTabChange: 切换标签时是否重新加载数据，默认 true */
    reloadOnTabChange?: boolean;
    /** enableSort: 是否启用排序功能，默认 true */
    enableSort?: boolean;
    /** sortField: 默认排序字段，默认 'sendTime' */
    sortField?: string;
    /** sortOrder: 默认排序方向，默认 'desc'（降序） */
    sortOrder?: 'asc' | 'desc';
    /** enablePagination: 是否启用分页功能，默认 true */
    enablePagination?: boolean;
    /** pageSize: 默认每页条数，默认 10 */
    pageSize?: number;
    /** pageSizes: 可选的每页条数选项，默认 [10, 20, 50, 100] */
    pageSizes?: number[];
    /** onCloseCallback: 关闭时是否触发回调，默认 true */
    onCloseCallback?: boolean;
  };
  /** styleConfig: 样式配置，控制主题颜色等样式 */
  styleConfig?: StyleConfig;
}

// 定义组件 props，使用 withDefaults 提供默认值
const props = withDefaults(defineProps<Props>(), {
  displayConfig: () => ({}),
  interactionConfig: () => ({}),
  styleConfig: () => ({})
});

/**
 * ==================== Emits 定义 ====================
 */

/**
 * Emits 接口说明
 * 
 * NoticeCenter 组件向父组件派发以下事件：
 * - tab-change: 切换消息状态标签时触发
 * - search: 执行搜索操作时触发
 * - message-click: 点击某条消息时触发
 * - mark-read: 标记单条消息已读时触发
 * - mark-all-read: 标记全部消息已读时触发
 * - close: 关闭弹窗时触发
 */
const emit = defineEmits<NoticeCenterEmits & {
  (e: 'search', payload: {
    title: string;
    tab: 'all' | 'unread' | 'read';
    source: string;
    resetPage?: boolean;
  }): void;
  (e: 'message-click', message: MessageItem): void;
}>();

/**
 * ==================== 响应式数据定义 ====================
 */

/** dialogVisible: 控制弹窗显示与隐藏的双向绑定变量，通过 v-model:visible 绑定 */
const dialogVisible = defineModel<boolean>('visible', { default: false });

/** tableRef: 表格组件的引用，用于调用表格的刷新、重置等方法 */
const tableRef = ref();

/** searchFormRef: 搜索表单的引用，用于表单验证和重置操作 */
const searchFormRef = ref<FormInstance>();

/** searchForm: 搜索表单数据，包含标题关键词等搜索条件 */
const searchForm = reactive({
  title: ''  // 消息标题搜索关键词
});

/** activeTab: 当前激活的消息状态标签，可选值：'all'（全部）、'unread'（未读）、'read'（已读） */
const activeTab = ref<'all' | 'unread' | 'read'>('all');

/** activeSource: 当前激活的消息来源类型，'all' 表示所有来源 */
const activeSource = ref<string>('all');

/**
 * ==================== 计算属性定义 ====================
 */

/**
 * tabs: 消息状态标签列表
 * 
 * 计算逻辑：
 * 1. 统计全部消息、未读消息、已读消息的数量
 * 2. 生成三个标签对象：全部消息、未读消息、已读消息
 * 3. 未读标签显示未读消息数量作为角标
 * 
 * 返回值结构：
 * - key: 标签标识
 * - label: 标签显示名称
 * - count: 消息数量（用于角标显示）
 */
const tabs = computed(() => {
  // 统计全部消息数量
  const allCount = Object.values(props.tabDataSource.all).reduce((sum, messages) => sum + messages.length, 0);
  // 统计未读消息数量
  const unreadCount = Object.values(props.tabDataSource.unread).reduce((sum, messages) => sum + messages.length, 0);
  // 统计已读消息数量
  const readCount = Object.values(props.tabDataSource.read).reduce((sum, messages) => sum + messages.length, 0);
  
  return [
    {
      key: 'all',
      label: '全部消息',
      count: unreadCount  // 全部消息标签显示未读数量作为提示
    },
    {
      key: 'unread',
      label: '未读消息',
      count: unreadCount
    },
    {
      key: 'read',
      label: '已读消息',
      count: 0  // 已读消息不显示角标
    }
  ];
});

/**
 * unreadCount: 未读消息总数
 * 
 * 计算逻辑：遍历所有来源类型的未读消息数组，累加消息数量
 */
const unreadCount = computed(() => {
  return Object.values(props.tabDataSource.unread).reduce((sum, messages) => sum + messages.length, 0);
});

/**
 * sourceTabs: 消息来源标签列表
 * 
 * 计算逻辑：
 * 1. 如果配置了 sourceConfig，按配置生成来源标签
 * 2. 否则自动从数据源中推断所有来源类型
 * 3. 每个标签包含类型、显示名称、图标和未读消息数量
 * 
 * 来源标签用于左侧侧边栏的快速筛选
 */
const sourceTabs = computed(() => {
  // 定义来源标签数组
  const tabs: Array<{ type: string; label: string; icon?: string; count: number }> = [];
  
  // 获取配置的消息来源
  const sourceConfig = props.sourceConfig || [];
  
  // 如果配置了来源信息，按配置生成标签
  if (sourceConfig.length > 0) {
    sourceConfig.forEach(source => {
      // 获取该来源类型的未读消息
      const unreadMessages = (props.tabDataSource.unread[source.type] || []);
      tabs.push({
        type: source.type,
        label: source.label,
        icon: source.icon,
        count: unreadMessages.length
      });
    });
  } else {
    // 否则从数据源自动推断所有来源类型
    const sourceTypes = Object.keys(props.tabDataSource.all);
    sourceTypes.forEach(type => {
      const unreadMessages = (props.tabDataSource.unread[type] || []);
      tabs.push({
        type,
        label: getTypeLabel(type),
        count: unreadMessages.length
      });
    });
  }
  
  return tabs;
});

/**
 * currentData: 根据当前选中的标签和来源获取的消息数据
 * 
 * 计算逻辑：
 * 1. 根据 activeTab 从 tabDataSource 获取对应状态的数据源
 * 2. 如果 activeSource 为 'all'，合并所有来源的消息
 * 3. 否则只返回指定来源的消息
 * 
 * 数据流向：currentData → filteredData → tableOptions.data
 */
const currentData = computed(() => {
  let dataSource: Record<string, MessageItem[]>;
  
  // 根据激活的标签选择数据源
  switch (activeTab.value) {
    case 'all':
      dataSource = props.tabDataSource.all;
      break;
    case 'unread':
      dataSource = props.tabDataSource.unread;
      break;
    case 'read':
      dataSource = props.tabDataSource.read;
      break;
    default:
      dataSource = {};
  }
  
  // 如果选择全部来源，合并所有消息
  if (activeSource.value === 'all') {
    const allMessages: MessageItem[] = [];
    Object.values(dataSource).forEach(messages => {
      allMessages.push(...messages);
    });
    return allMessages;
  }
  
  // 返回指定来源的消息
  return dataSource[activeSource.value] || [];
});

/**
 * filteredData: 经过搜索筛选后的消息数据
 * 
 * 计算逻辑：
 * 1. 复制当前数据到新数组
 * 2. 如果存在搜索关键词，在标题和内容中模糊匹配
 * 3. 返回筛选后的数据
 * 
 * 搜索逻辑：忽略大小写的模糊匹配
 */
const filteredData = computed(() => {
  let data = [...currentData.value];
  
  // 如果存在搜索关键词，进行筛选
  if (searchForm.title) {
    const keyword = searchForm.title.toLowerCase();
    data = data.filter(item => 
      item.title.toLowerCase().includes(keyword) ||
      item.content.toLowerCase().includes(keyword)
    );
  }
  
  return data;
});

/**
 * themeClass: 根据配置的主题返回对应的样式类名
 * 
 * 支持的主题：
 * - light: 亮色主题（默认）
 * - dark: 暗色主题
 * - custom: 自定义主题
 */
const themeClass = computed(() => {
  return `theme-${props.styleConfig?.theme || 'light'}`;
});

/**
 * customTheme: 自定义主题的颜色配置
 * 
 * 配置项：
 * - bgColor: 背景颜色
 * - textColor: 文字颜色
 * - borderColor: 边框颜色
 */
const customTheme = computed(() => {
  return props.styleConfig?.customTheme || {};
});

/**
 * activeTabStyle: 激活状态标签的样式
 * 
 * 计算逻辑：
 * 从 displayConfig.center 中获取标签激活状态的样式配置
 * 包括文字颜色和字体粗细
 */
const activeTabStyle = computed(() => {
  const style: Record<string, string> = {};
  const center = props.displayConfig?.center;
  
  if (center?.tabActiveColor) {
    style.color = center.tabActiveColor;
  }
  
  if (center?.tabActiveFontWeight) {
    style.fontWeight = center.tabActiveFontWeight;
  }
  
  return style;
});

/**
 * activeSourceStyle: 激活状态来源标签的样式
 * 
 * 计算逻辑：
 * 从 sourceConfig 中查找当前来源的配置，获取自定义颜色
 */
const activeSourceStyle = computed(() => {
  const style: Record<string, string> = {};
  const activeSourceConfig = props.sourceConfig?.find(s => s.type === activeSource.value);
  
  if (activeSourceConfig?.color) {
    style.color = activeSourceConfig.color;
  }
  
  return style;
});

/**
 * tableOptions: 表格配置选项
 * 
 * 配置说明：
 * - border: 显示边框
 * - stripe: 显示斑马纹
 * - highlightHoverRow: 鼠标悬停高亮行
 * - columns: 列配置，包含序号、标题、发布人、发布时间、阅读状态、操作列
 * 
 * 数据流向：filteredData → tableOptions.data
 */
const tableOptions = reactive<any>({
  border: true,
  stripe: true,
  highlightHoverRow: true,
  data: [],
  columns: [
    { type: 'seq', width: 80, align: 'center' },  // 序号列
    {
      field: 'title',
      title: '消息标题',
      minWidth: 200
    },
    {
      field: 'sendUser',
      title: '发布人',
      width: 120
    },
    {
      field: 'sendTime',
      title: '发布时间',
      width: 180,
      slots: { default: 'sendTime' },
      sortable: true
    },
    {
      field: 'readStatus',
      title: '阅读状态',
      width: 100,
      slots: { default: 'readStatus' }
    },
    {
      field: 'action',
      title: '操作',
      width: 100,
      fixed: 'right',
      slots: { default: 'action' }
    }
  ],
});

/**
 * tableEvents: 表格事件配置
 * 
 * 当前配置为空对象，可扩展表格的事件处理逻辑
 */
const tableEvents = computed(() => ({}));

/**
 * ==================== Watch 监听器定义 ====================
 */

/**
 * 监听 filteredData 变化，更新表格数据
 * 
 * 监听逻辑：当筛选后的消息数据发生变化时，
 * 立即更新 tableOptions.data，实现表格数据的实时同步
 */
watch(filteredData, (newData) => {
  tableOptions.data = newData;
}, { immediate: true });

/**
 * ==================== 方法定义 ====================
 */

/**
 * getTypeLabel: 获取消息类型的显示标签
 * 
 * 功能说明：根据消息类型返回对应的中文显示名称
 * 
 * 类型映射：
 * - system: 系统通知
 * - notice: 公告通知
 * - todo: 待办提醒
 * - alert: 告警消息
 * - message: 消息通知
 * 
 * @param {string} type - 消息类型标识
 * @returns {string} 消息类型的中文显示名称
 */
const getTypeLabel = (type: string): string => {
  const typeLabels: Record<string, string> = {
    'system': '系统通知',
    'notice': '公告通知',
    'todo': '待办提醒',
    'alert': '告警消息',
    'message': '消息通知'
  };
  
  return typeLabels[type] || type;
};

/**
 * getMessageTypeColor: 获取消息类型的颜色标识
 * 
 * 功能说明：根据消息类型返回对应的 Element Plus 标签颜色
 * 
 * 颜色映射：
 * - system: primary（蓝色）
 * - notice: success（绿色）
 * - todo: warning（橙色）
 * - alert: danger（红色）
 * - message: info（灰色）
 * 
 * @param {string} type - 消息类型标识
 * @returns {string} Element Plus 标签颜色类型
 */
const getMessageTypeColor = (type: string): string => {
  const typeColors: Record<string, string> = {
    'system': 'primary',
    'notice': 'success',
    'todo': 'warning',
    'alert': 'danger',
    'message': 'info'
  };
  
  return typeColors[type] || 'default';
};

/**
 * formatTime: 格式化时间显示
 * 
 * 功能说明：将时间戳或日期字符串格式化为相对时间或指定格式
 * 
 * @param {string | Date} time - 时间戳或日期字符串
 * @param {string} format - 时间格式模板，默认 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的时间字符串
 */
const formatTime = (time: string | Date): string => {
  return formatPast(time, 'YYYY-MM-DD HH:mm:ss');
};

/**
 * handleTabChange: 处理消息状态标签切换
 * 
 * 功能说明：
 * 1. 更新当前激活的状态标签
 * 2. 重置来源选择为全部来源
 * 3. 触发 tab-change 事件通知父组件
 * 4. 根据配置决定是否刷新表格数据
 * 
 * @param {('all' | 'unread' | 'read')} tab - 切换到的标签标识
 */
const handleTabChange = (tab: 'all' | 'unread' | 'read') => {
  activeTab.value = tab;
  activeSource.value = 'all';
  emit('tab-change', tab);
  
  // 根据配置决定是否刷新表格
  if (props.interactionConfig?.reloadOnTabChange) {
    tableRef.value?.refresh();
  }
};

/**
 * handleSourceChange: 处理消息来源切换
 * 
 * 功能说明：
 * 1. 更新当前激活的来源类型
 * 2. 触发 source-change 事件（可选）
 * 
 * @param {string} sourceType - 来源类型标识
 */
const handleSourceChange = (sourceType: string) => {
  activeSource.value = sourceType;
};

/**
 * searchTable: 执行搜索操作
 * 
 * 功能说明：
 * 1. 触发 search 事件，将搜索条件传递给父组件
 * 2. 支持重置分页参数
 * 
 * @param {boolean} resetPage - 是否重置到第一页，默认 true
 */
const searchTable = (resetPage = true) => {
  emit('search', {
    title: searchForm.title,
    tab: activeTab.value,
    source: activeSource.value,
    resetPage
  });
};

/**
 * resetTable: 重置搜索条件
 * 
 * 功能说明：
 * 1. 清空搜索表单的标题字段
 * 2. 调用表单的重置方法
 * 3. 调用表格的重置方法
 */
const resetTable = () => {
  searchForm.title = '';
  searchFormRef.value?.resetFields();
  tableRef.value?.reset();
};

/**
 * handleView: 查看消息详情
 * 
 * 功能说明：
 * 1. 触发 message-click 事件，将点击的消息传递给父组件
 * 2. 父组件根据消息数据打开详情弹窗
 * 
 * @param {MessageItem} row - 被点击的消息行数据
 */
const handleView = (row: MessageItem) => {
  emit('message-click', row);
};

/**
 * handleMarkAllRead: 标记全部未读消息为已读
 * 
 * 功能说明：
 * 1. 触发 mark-all-read 事件通知父组件
 * 2. 父组件负责调用后端 API 更新消息状态
 */
const handleMarkAllRead = () => {
  emit('mark-all-read' as any);
};

const handleClose = () => {
  dialogVisible.value = false;
  
  if (props.interactionConfig?.onCloseCallback) {
    emit('close');
  }
};

watch(() => props.tabDataSource, () => {
  if (tableRef.value) {
    tableRef.value.refresh();
  }
}, { deep: true });

watch(activeTab, () => {
  activeSource.value = 'all';
  if (tableRef.value) {
    tableRef.value.refresh();
  }
});

watch(activeSource, () => {
  tableOptions.data = filteredData.value;
  if (tableRef.value) {
    tableRef.value.refresh();
  }
});

watch(filteredData, (newData) => {
  tableOptions.data = newData;
});

watch(() => props.displayConfig?.center, (newConfig) => {
  if (newConfig) {
    tableOptions.border = newConfig.tableBorder !== false;
    tableOptions.stripe = newConfig.tableStripe !== false;
    tableOptions.highlightHoverRow = newConfig.tableHover !== false;
  }
}, { deep: true });

watch(() => props.interactionConfig?.enableSort, (enableSort) => {
  const sendTimeColumn = tableOptions.columns.find((col: any) => col.field === 'sendTime');
  if (sendTimeColumn) {
    sendTimeColumn.sortable = enableSort !== false;
  }
});

defineExpose({
  refresh: () => {
    tableRef.value?.refresh();
  },
  markAllRead: handleMarkAllRead
});
</script>

<style scoped lang="scss">
  :global(.ys-dialog-body-wrapper){ 
    height: 100%;
  }
.notice-center {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  
  &.theme-dark {
    background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%);
    color: #e0e0e0;
  }
  
  &.theme-custom {
    background: linear-gradient(135deg, v-bind('customTheme.bgColor') 0%, v-bind('customTheme.bgColor || "#f8f9fa"') 100%);
    color: v-bind('customTheme.textColor');
  }
}

.notice-center-tabs {
  display: flex;
  gap: v-bind('displayConfig.center?.tabGap + "px" || "24px"');
  padding: 0 10px;
  border-bottom: 2px solid #e8ecef;
  height: v-bind('displayConfig.center?.tabHeight + "px" || "56px"');
  align-items: center;
  background: linear-gradient(to bottom, #ffffff 0%, #f8f9fa 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  position: relative;
  z-index: 10;
  
  .theme-dark & {
    border-bottom-color: #3d3d3d;
    background: linear-gradient(to bottom, #1a1a1a 0%, #2d2d2d 100%);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  }
  
  .theme-custom & {
    border-bottom-color: v-bind('customTheme.borderColor');
    background: linear-gradient(to bottom, v-bind('customTheme.bgColor || "#ffffff"') 0%, v-bind('customTheme.bgColor || "#f8f9fa"') 100%);
  }
}

.tab-item {
  position: relative;
  padding: 0 20px;
  height: 100%;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  color: #6c757d;
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.3px;
  border-radius: 8px 8px 0 0;
  margin-bottom: -2px;
  
  &:hover {
    color: #409eff;
    background: rgba(64, 158, 255, 0.08);
    transform: translateY(-1px);
  }
  
  &.active {
    color: #409eff;
    font-weight: 600;
    background: rgba(64, 158, 255, 0.1);
    
    &::after {
      content: '';
      position: absolute;
      bottom: -2px;
      left: 0;
      right: 0;
      height: 3px;
      background: linear-gradient(90deg, #409eff 0%, #66b1ff 100%);
      border-radius: 3px 3px 0 0;
      box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
    }
  }
}

.tab-badge {
  :deep(.el-badge__content) {
    background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
    color: #ffffff;
    border-radius: 12px;
    font-size: 11px;
    font-weight: 600;
    min-width: 20px;
    height: 20px;
    line-height: 20px;
    padding: 0 6px;
    box-shadow: 0 2px 4px rgba(238, 90, 90, 0.3);
    border: 2px solid #ffffff;
  }
}
.notice-center-content {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: row;
  background: #ffffff;
  border-radius: 0 0 12px 12px;
}

.source-sidebar {
  width: 180px;
  min-width: 180px;
  // border-right: 1px solid #e8ecef;
  padding: 20px 12px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 6px;
  background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%);
  
  .theme-dark & {
    border-right-color: #3d3d3d;
    background: linear-gradient(180deg, #2d2d2d 0%, #1a1a1a 100%);
  }
  
  .theme-custom & {
    border-right-color: v-bind('customTheme.borderColor');
    background: linear-gradient(180deg, v-bind('customTheme.bgColor || "#f8f9fa"') 0%, v-bind('customTheme.bgColor || "#ffffff"') 100%);
  }
  
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: linear-gradient(180deg, #d0d7de 0%, #b0b8c3 100%);
    border-radius: 3px;
    transition: background 0.3s;
  }
  
  &::-webkit-scrollbar-thumb:hover {
    background: linear-gradient(180deg, #c0c7ce 0%, #a0a8b3 100%);
  }
  
  &::-webkit-scrollbar-track {
    background: #f1f3f5;
    border-radius: 3px;
  }
}

.source-tab {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 10px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 14px;
  color: #6c757d;
  position: relative;
  background: #ffffff;
  border: 1px solid transparent;
  
  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 3px;
    height: 0;
    background: linear-gradient(180deg, #409eff 0%, #66b1ff 100%);
    border-radius: 0 3px 3px 0;
    transition: height 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }
  
  &:hover {
    background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
    color: #409eff;
    border-color: #e8ecef;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    transform: translateX(4px);
    
    &::before {
      height: 24px;
    }
  }
  
  &.active {
    color: #409eff;
    font-weight: 600;
    background: linear-gradient(135deg, #e7f3ff 0%, #f0f7ff 100%);
    border-color: #b3d8ff;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
    
    &::before {
      height: 32px;
    }
  }
}

.source-icon {
  font-size: 18px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  background: rgba(64, 158, 255, 0.1);
  color: #409eff;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.source-tab:hover .source-icon {
  background: rgba(64, 158, 255, 0.15);
  transform: scale(1.1);
}

.source-tab.active .source-icon {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #ffffff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
  transform: scale(1.05);
}

.source-label {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.2px;
}

.source-badge {
  :deep(.el-badge__content) {
    background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
    color: #ffffff;
    border-radius: 12px;
    font-size: 11px;
    font-weight: 600;
    min-width: 20px;
    height: 20px;
    line-height: 20px;
    padding: 0 6px;
    box-shadow: 0 2px 4px rgba(238, 90, 90, 0.3);
    border: 2px solid #ffffff;
  }
}

.table-container {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  padding: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8ecef;
  margin-bottom: 16px;
  
  .el-form {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
    margin: 0;
  }
  
  .w-180 {
    width: 180px;
  }
  
  .mr20 {
    margin-right: 20px;
  }
  
  .ml10 {
    margin-left: 10px;
  }
  
  .el-input {
    flex-shrink: 0;
  }
  
  .el-button {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    
    i {
      font-size: 16px;
    }
  }
}

@media (max-width: 768px) {
  .notice-center-content {
    flex-direction: column;
  }
  
  .source-sidebar {
    width: 100%;
    min-width: 100%;
    border-right: none;
    border-bottom: 2px solid #e8ecef;
    padding: 16px 12px;
    flex-direction: row;
    overflow-x: auto;
    overflow-y: visible;
    white-space: nowrap;
    gap: 8px;
    
    .theme-dark & {
      border-bottom-color: #3d3d3d;
    }
    
    .theme-custom & {
      border-bottom-color: v-bind('customTheme.borderColor');
    }
    
    &::-webkit-scrollbar {
      height: 6px;
      width: auto;
    }
    
    &::-webkit-scrollbar-thumb {
      background: linear-gradient(90deg, #d0d7de 0%, #b0b8c3 100%);
      border-radius: 3px;
    }
    
    &::-webkit-scrollbar-track {
      background: #f1f3f5;
      border-radius: 3px;
    }
  }
  
  .source-tab {
    padding: 10px 14px;
    min-width: fit-content;
    
    &::before {
      top: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 0;
      height: 3px;
      border-radius: 0 0 3px 3px;
      transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    }
    
    &:hover {
      transform: translateY(-2px);
      
      &::before {
        width: 24px;
      }
    }
    
    &.active {
      &::before {
        width: 32px;
      }
    }
  }
  
  .source-icon {
    width: 20px;
    height: 20px;
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .notice-center-tabs {
    padding: 0 16px;
    gap: 16px;
  }
  
  .tab-item {
    padding: 0 12px;
    font-size: 13px;
  }
  
  .page-header {
    .el-form {
      flex-direction: column;
      align-items: stretch;
    }
    
    .el-input {
      width: 100% !important;
    }
    
    .el-button {
      width: 100%;
      justify-content: center;
    }
  }
}
</style>
