# YsNoticeBar 消息通知组件

## 组件简介

YsNoticeBar 是一个基于 Vue 3 + TypeScript 开发的纯配置型通用消息通知组件，适用于中后台管理系统顶部导航区（铃铛图标位）。组件设计遵循「零源码修改、全外部配置驱动」原则，所有样式、交互、数据、展示规则均通过外部传入的配置项控制，具备极强的通用性、可扩展性、高自定义性。

## 功能特性

### 核心功能

- ✅ **多种触发方式**：支持鼠标移入、点击、两者共存三种触发方式
- ✅ **气泡弹窗**：固定布局结构（顶部类型统计 + 中部消息列表 + 底部操作按钮）
- ✅ **通知中心**：内置完整的独立页面，支持 Tab 切换、表格展示、分页、排序
- ✅ **详情弹窗**：点击消息项触发详情弹窗，展示完整消息内容
- ✅ **状态管理**：支持未读/已读状态切换、自动标记已读
- ✅ **全配置驱动**：所有功能、样式、交互均通过配置项控制
- ✅ **主题适配**：支持浅色/深色/自定义主题
- ✅ **响应式设计**：适配不同分辨率的管理系统
- ✅ **性能优化**：支持虚拟滚动，大数据量无卡顿

### 交互特性

- 鼠标移入/点击铃铛图标触发气泡弹窗
- 点击消息项自动标记为已读（可配置）
- 点击「前往通知中心」打开独立页面
- 支持消息类型分组展示和数量统计
- 支持消息搜索、筛选、排序
- 支持批量标记已读操作

## 安装使用

### 基础引入

```vue
<template>
  <YsNoticeBar
    :data-config="dataConfig"
    :display-config="displayConfig"
    :interaction-config="interactionConfig"
    :style-config="styleConfig"
    @message-click="handleMessageClick"
    @to-center="handleToCenter"
    @mark-read="handleMarkRead"
    @mark-all-read="handleMarkAllRead"
  />
</template>

<script setup lang="ts">
import { ref } from 'vue';
import YsNoticeBar from '@/components/YsNoticeBar/index.vue';
import type { DataConfig, DisplayConfig, InteractionConfig, StyleConfig, MessageItem } from '@/types/notice';

const dataConfig = ref<DataConfig>({
  messageList: [],
  tabDataSource: {
    all: [],
    unread: [],
    read: []
  }
});

const displayConfig = ref<DisplayConfig>({});

const interactionConfig = ref<InteractionConfig>({});

const styleConfig = ref<StyleConfig>({});

const handleMessageClick = (message: MessageItem) => {
  console.log('点击消息:', message);
};

const handleToCenter = () => {
  console.log('前往通知中心');
};

const handleMarkRead = (message: MessageItem) => {
  console.log('标记已读:', message);
};

const handleMarkAllRead = () => {
  console.log('全部标记已读');
};
</script>
```

## API 文档

### Props 配置项

#### dataConfig（必传）

数据源配置，控制组件的数据来源和数据处理逻辑。

| 属性 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|---------|------|
| sourceConfig | `MessageSourceConfig[]` | 否 | - | 消息来源类型配置 |
| messageList | `Record<string, MessageItem[]>` | 是 | - | 气泡弹窗的消息列表数据源（按来源类型分组） |
| tabDataSource | `{ all, unread, read }` | 是 | - | 通知中心3个Tab的数据源（嵌套结构：按来源类型和状态双重分组） |
| enableLazyLoad | `boolean` | 否 | false | 是否支持懒加载 |
| lazyLoadThreshold | `number` | 否 | 50 | 懒加载阈值 |
| loadPageSize | `number` | 否 | 10 | 每次加载条数 |
| loadingText | `string` | 否 | '加载中...' | 加载中占位文案 |
| filterDuplicate | `boolean` | 否 | true | 是否过滤重复消息 |
| filterExpired | `boolean` | 否 | false | 是否过滤过期消息 |
| expiredDays | `number` | 否 | 30 | 过期时间（天） |
| enableTypeFilter | `boolean` | 否 | false | 是否支持按消息类型过滤 |
| allowedTypes | `string[]` | 否 | - | 允许的消息类型列表 |
| enableUserFilter | `boolean` | 否 | false | 是否支持按发布人筛选 |
| allowedUsers | `string[]` | 否 | - | 允许的发布人列表 |
| emptyText | `string` | 否 | '暂无数据' | 空数据时的占位文案 |
| showRefreshBtn | `boolean` | 否 | false | 是否显示刷新数据按钮 |
| onRefresh | `Function` | 否 | - | 点击刷新按钮触发的回调函数 |

##### sourceConfig - 消息来源类型配置

用于配置不同消息来源的类型信息，支持在气泡弹窗和通知中心中按来源类型进行分类展示。

| 属性 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|---------|------|
| type | `string` | 是 | - | 来源类型标识（如：system/notice/todo/alert） |
| label | `string` | 是 | - | 来源类型显示名称 |
| icon | `string` | 否 | - | 来源类型图标类名 |
| color | `string` | 否 | - | 来源类型主题颜色 |

**使用示例：**

```typescript
const sourceConfig = [
  {
    type: 'system',
    label: '系统通知',
    icon: 'ri-notification-3-line',
    color: '#409eff'
  },
  {
    type: 'notice',
    label: '公告通知',
    icon: 'ri-megaphone-line',
    color: '#67c23a'
  },
  {
    type: 'todo',
    label: '待办提醒',
    icon: 'ri-checkbox-circle-line',
    color: '#e6a23c'
  },
  {
    type: 'alert',
    label: '告警消息',
    icon: 'ri-alarm-warning-line',
    color: '#f56c6c'
  }
];
```

##### messageList - 气泡弹窗消息列表

按来源类型分组的消息数据结构：

```typescript
const messageList = {
  system: [
    {
      id: '1',
      type: 'system',
      title: '系统维护通知',
      content: '系统将于今晚22:00-24:00进行维护，请提前保存工作内容。',
      sendTime: new Date(),
      sendUser: '系统管理员',
      readStatus: 0
    }
  ],
  notice: [],
  todo: [],
  alert: []
};
```

##### tabDataSource - 通知中心数据源

嵌套结构，按来源类型和状态双重分组：

```typescript
const tabDataSource = {
  all: {
    system: [...],
    notice: [...],
    todo: [...],
    alert: [...]
  },
  unread: {
    system: [...],
    notice: [...],
    todo: [...],
    alert: [...]
  },
  read: {
    system: [...],
    notice: [...],
    todo: [...],
    alert: [...]
  }
};
```

#### displayConfig（可选）

显示配置，控制组件的可视化效果。

##### icon - 铃铛图标配置

| 属性 | 类型 | 默认值 | 说明 |
|------|------|---------|------|
| size | `number \| string` | 20 | 图标大小 |
| color | `string` | 继承主题色 | 图标颜色 |
| hoverColor | `string` | - | hover颜色 |
| showBadge | `boolean` | true | 是否显示未读消息小红点 |
| badgePosition | `'top-right' \| 'top-left' \| 'bottom-right' \| 'bottom-left'` | 'top-right' | 小红点位置 |
| badgeColor | `string` | '#f56c6c' | 小红点颜色 |
| badgeSize | `number` | 8 | 小红点大小 |
| rotate | `boolean` | false | 图标是否旋转动画 |
| blink | `boolean` | false | 图标是否闪烁动画 |

##### popover - 气泡弹窗配置

| 属性 | 类型 | 默认值 | 说明 |
|------|------|---------|------|
| width | `number \| string` | 380 | 弹窗宽度 |
| height | `number \| string` | 'auto' | 弹窗高度 |
| maxHeight | `number \| string` | 500 | 最大高度，超出滚动 |
| offset | `number` | 0 | 弹窗定位偏移量 |
| borderRadius | `number \| string` | 8 | 弹窗圆角 |
| shadow | `boolean` | true | 弹窗阴影 |
| showMask | `boolean` | false | 是否显示遮罩层 |

##### header - 气泡顶部区域配置

| 属性 | 类型 | 默认值 | 说明 |
|------|------|---------|------|
| fontSize | `number \| string` | 14 | 类型文本字号 |
| textColor | `string` | '#606266' | 类型文本颜色 |
| fontWeight | `string` | 'normal' | 类型文本粗细 |
| badgeBgColor | `string` | '#409eff' | 数量角标背景色 |
| badgeTextColor | `string` | '#ffffff' | 数量角标文字色 |
| badgeBorderRadius | `number \| string` | 10 | 数量角标圆角 |
| badgeSize | `number \| string` | 16 | 数量角标大小 |
| wrap | `boolean` | false | 是否换行展示多类型 |
| emptyText | `string` | '暂无消息' | 空消息时的占位文案 |

##### list - 气泡消息列表配置

| 属性 | 类型 | 默认值 | 说明 |
|------|------|---------|------|
| itemHeight | `number \| string` | 'auto' | 列表项高度 |
| itemPadding | `string` | '12px 16px' | 列表项内边距 |
| itemGap | `number \| string` | 0 | 项之间的间距 |
| unreadLineColor | `string` | '#409eff' | 未读项左侧竖线颜色 |
| unreadBgColor | `string` | '#ecf5ff' | 未读项背景色 |
| unreadFontWeight | `string` | 'bold' | 未读项字体粗细 |
| readBgColor | `string` | 'transparent' | 已读项背景色 |
| readFontWeight | `string` | 'normal' | 已读项字体粗细 |
| hoverEffect | `boolean` | true | 列表项hover样式 |
| hoverBgColor | `string` | '#f5f7fa' | 列表项hover背景色 |
| emptyText | `string` | '暂无消息' | 空列表占位文案 |
| showEmptyImage | `boolean` | true | 是否显示空列表占位图 |
| emptyImageUrl | `string` | - | 空列表占位图URL |

##### footer - 底部按钮配置

| 属性 | 类型 | 默认值 | 说明 |
|------|------|---------|------|
| width | `number \| string` | '100%' | 按钮宽度 |
| height | `number \| string` | 40 | 按钮高度 |
| text | `string` | '前往通知中心' | 按钮文案 |
| fontSize | `number \| string` | 14 | 按钮字体大小 |
| textColor | `string` | '#ffffff' | 按钮字体颜色 |
| bgColor | `string` | '#409eff' | 按钮背景色 |
| hoverBgColor | `string` | '#66b1ff' | 按钮hover背景色 |
| activeBgColor | `string` | '#3a8ee6' | 按钮点击背景色 |
| borderRadius | `number \| string` | 4 | 按钮圆角 |
| showIcon | `boolean` | true | 是否显示图标 |
| iconPosition | `'left' \| 'right'` | 'left' | 图标位置 |
| icon | `string` | 'ri-arrow-right-line' | 图标类名 |

##### center - 通知中心页面配置

| 属性 | 类型 | 默认值 | 说明 |
|------|------|---------|------|
| width | `number \| string` | '90%' | 页面整体宽度 |
| height | `number \| string` | '90vh' | 页面整体高度 |
| fullscreen | `boolean` | false | 是否全屏展示 |
| tabHeight | `number \| string` | 50 | Tab栏高度 |
| tabGap | `number \| string` | 20 | Tab间距 |
| tabActiveColor | `string` | '#409eff' | Tab激活态下划线颜色 |
| tabActiveFontWeight | `string` | 'bold' | Tab激活态字体粗细 |
| tableBorder | `boolean` | true | 表格边框 |
| tableStripe | `boolean` | true | 表格斑马纹 |
| tableHover | `boolean` | true | 表格hover高亮 |
| actionButtonText | `string` | '查看' | 操作栏按钮文字 |
| actionButtonType | `'primary' \| 'success' \| 'warning' \| 'danger' \| 'info' \| 'text'` | 'primary' | 操作栏按钮类型 |

##### common - 通用显示规则

| 属性 | 类型 | 默认值 | 说明 |
|------|------|---------|------|
| timeFormat | `string` | 'YYYY-MM-DD HH:mm:ss' | 时间格式化规则 |
| textOverflow | `'ellipsis' \| 'wrap' \| 'tooltip'` | 'ellipsis' | 超长文本处理方式 |
| maxLines | `number` | 2 | 超长文本最大行数 |
| typeIcons | `Record<string, string>` | {} | 消息类型对应图标配置 |
| openAnimation | `'fade' \| 'slide' \| 'zoom'` | 'fade' | 气泡展开动画 |
| closeAnimation | `'fade' \| 'slide' \| 'zoom'` | 'fade' | 气泡收起动画 |
| animationDuration | `number` | 300 | 动画时长（ms） |

#### interactionConfig（可选）

交互配置，控制组件的行为逻辑。

##### trigger - 气泡触发规则

| 属性 | 类型 | 默认值 | 说明 |
|------|------|---------|------|
| mode | `'hover' \| 'click' \| 'both'` | 'hover' | 触发方式 |
| hoverDelay | `number` | 200 | 移入延迟展开时长（ms） |
| leaveDelay | `number` | 300 | 移出后延迟收起时长（ms） |
| clickOutsideToClose | `boolean` | true | 点击空白处是否收起气泡 |
| clickToToggle | `boolean` | true | 点击铃铛图标是否强制收起 |

##### item - 消息项交互

| 属性 | 类型 | 默认值 | 说明 |
|------|------|---------|------|
| autoMarkRead | `boolean` | true | 点击消息项后是否自动标记为已读 |
| enableDoubleClick | `boolean` | false | 是否支持双击消息项触发其他事件 |
| showPreview | `boolean` | false | hover消息项是否显示预览文案 |

##### auto - 自动行为配置

| 属性 | 类型 | 默认值 | 说明 |
|------|------|---------|------|
| enableAutoScroll | `boolean` | false | 是否开启消息列表自动滚动播放 |
| scrollDirection | `'up' \| 'down'` | 'up' | 滚动方向 |
| scrollSpeed | `number` | 2000 | 滚动速度（ms） |
| scrollPause | `'hover' \| 'click' \| 'none'` | 'hover' | 滚动暂停规则 |
| scrollLoop | `number` | 0 | 自动滚动循环次数（0=无限循环） |

##### center - 通知中心交互

| 属性 | 类型 | 默认值 | 说明 |
|------|------|---------|------|
| reloadOnTabChange | `boolean` | false | Tab切换是否触发重新加载数据源 |
| enableSort | `boolean` | true | 表格是否支持排序 |
| sortField | `string` | 'sendTime' | 表格默认排序字段 |
| sortOrder | `'asc' \| 'desc'` | 'desc' | 表格默认排序方式 |
| enablePagination | `boolean` | true | 表格是否支持分页 |
| pageSize | `number` | 10 | 每页条数 |
| pageSizes | `number[]` | [10, 20, 50, 100] | 分页大小选项 |
| onCloseCallback | `boolean` | false | 关闭通知中心页面是否触发回调 |

##### cache - 状态缓存配置

| 属性 | 类型 | 默认值 | 说明 |
|------|------|---------|------|
| cacheReadStatus | `boolean` | true | 是否缓存消息的已读/未读状态 |
| cacheLastViewId | `boolean` | false | 是否缓存用户最后一次查看的消息ID |
| cachePopoverState | `boolean` | false | 是否在页面刷新后保留气泡展开状态 |

#### styleConfig（可选）

样式配置，控制组件的主题和差异化样式。

| 属性 | 类型 | 默认值 | 说明 |
|------|------|---------|------|
| theme | `'light' \| 'dark' \| 'custom'` | 'light' | 全局主题配置 |
| customTheme | `object` | - | 自定义主题颜色（primaryColor、successColor、warningColor、dangerColor、infoColor、textColor、borderColor、bgColor） |
| typeStyles | `Record<string, object>` | - | 消息类型差异化样式（textColor、lineColor、icon、bgColor） |
| statusStyles | `object` | - | 状态差异化样式（unread、read） |
| customClass | `string` | - | 自定义样式类名 |
| customStyle | `Record<string, any>` | - | 自定义行内样式 |
| responsive | `object` | - | 响应式样式配置（large、medium、small） |

### Events 事件

| 事件名 | 参数 | 说明 |
|--------|------|------|
| message-click | `(message: MessageItem)` | 点击消息列表项/通知中心表格查看按钮时触发 |
| to-center | - | 点击前往通知中心按钮时触发 |
| popover-show | - | 气泡弹窗展开时触发 |
| popover-hide | - | 气泡弹窗收起时触发 |
| mark-read | `(message: MessageItem)` | 标记已读时触发 |
| mark-all-read | - | 全部标记已读时触发 |

### Expose 方法

| 方法名 | 参数 | 说明 |
|--------|------|------|
| showPopover | - | 显示气泡弹窗 |
| hidePopover | - | 隐藏气泡弹窗 |
| togglePopover | - | 切换气泡弹窗显示状态 |
| showCenter | - | 显示通知中心 |
| hideCenter | - | 隐藏通知中心 |
| showDetail | `(message: MessageItem)` | 显示详情弹窗 |
| hideDetail | - | 隐藏详情弹窗 |

## 数据模型

### MessageItem

所有消息数据源必须包含以下核心属性：

```typescript
interface MessageItem {
  id: string | number;           // 唯一标识，必传
  type: string;                 // 消息类型，必传
  title: string;                 // 消息标题，必传
  content: string;               // 消息正文内容，必传
  sendTime: string | Date;       // 消息发布时间，必传
  sendUser: string;             // 消息发布人/来源方，必传
  readStatus: 0 | 1;          // 阅读状态，0=未读，1=已读，必传
  // 支持扩展任意业务字段
  [key: string]: any;
}
```

## 使用示例

### 示例1：基础使用

```vue
<template>
  <div class="page-container">
    <YsNoticeBar
      :data-config="dataConfig"
      @message-click="handleMessageClick"
      @to-center="handleToCenter"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import YsNoticeBar from '@/components/YsNoticeBar/index.vue';
import type { DataConfig, MessageItem } from '@/types/notice';

const dataConfig = ref<DataConfig>({
  messageList: [],
  tabDataSource: {
    all: [],
    unread: [],
    read: []
  }
});

onMounted(() => {
  loadMockData();
});

const loadMockData = () => {
  const mockMessages: MessageItem[] = [
    {
      id: '1',
      type: 'system',
      title: '系统维护通知',
      content: '系统将于今晚22:00-24:00进行维护，请提前保存工作内容。',
      sendTime: new Date(Date.now() - 1000 * 60 * 30),
      sendUser: '系统管理员',
      readStatus: 0
    },
    {
      id: '2',
      type: 'notice',
      title: '新功能上线公告',
      content: '系统新增了消息通知功能，欢迎使用！',
      sendTime: new Date(Date.now() - 1000 * 60 * 60 * 2),
      sendUser: '运营平台',
      readStatus: 1
    },
    {
      id: '3',
      type: 'todo',
      title: '待办任务提醒',
      content: '您有3个待办任务需要处理，请及时查看。',
      sendTime: new Date(Date.now() - 1000 * 60 * 60 * 24),
      sendUser: '任务系统',
      readStatus: 0
    }
  ];
  
  dataConfig.value.messageList = mockMessages;
  dataConfig.value.tabDataSource = {
    all: mockMessages,
    unread: mockMessages.filter(m => m.readStatus === 0),
    read: mockMessages.filter(m => m.readStatus === 1)
  };
};

const handleMessageClick = (message: MessageItem) => {
  console.log('点击消息:', message);
};

const handleToCenter = () => {
  console.log('前往通知中心');
};
</script>
```

### 示例2：自定义样式

```vue
<template>
  <YsNoticeBar
    :data-config="dataConfig"
    :display-config="displayConfig"
    :style-config="styleConfig"
    @message-click="handleMessageClick"
    @to-center="handleToCenter"
  />
</template>

<script setup lang="ts">
import { ref } from 'vue';
import YsNoticeBar from '@/components/YsNoticeBar/index.vue';
import type { DataConfig, DisplayConfig, StyleConfig } from '@/types/notice';

const dataConfig = ref<DataConfig>({
  messageList: [],
  tabDataSource: {
    all: [],
    unread: [],
    read: []
  }
});

const displayConfig = ref<DisplayConfig>({
  icon: {
    size: 24,
    color: '#409eff',
    badgeColor: '#ff4d4f',
    badgeSize: 10
  },
  popover: {
    width: 420,
    maxHeight: 600
  },
  footer: {
    text: '查看全部通知',
    bgColor: '#67c23a',
    hoverBgColor: '#85ce61'
  }
});

const styleConfig = ref<StyleConfig>({
  theme: 'light',
  typeStyles: {
    system: {
      textColor: '#409eff',
      lineColor: '#409eff',
      bgColor: '#ecf5ff'
    },
    notice: {
      textColor: '#67c23a',
      lineColor: '#67c23a',
      bgColor: '#f0f9ff'
    },
    todo: {
      textColor: '#e6a23c',
      lineColor: '#e6a23c',
      bgColor: '#fdf6ec'
    }
  }
});
</script>
```

### 示例3：自定义交互

```vue
<template>
  <YsNoticeBar
    :data-config="dataConfig"
    :interaction-config="interactionConfig"
    @message-click="handleMessageClick"
    @to-center="handleToCenter"
    @mark-read="handleMarkRead"
    @mark-all-read="handleMarkAllRead"
  />
</template>

<script setup lang="ts">
import { ref } from 'vue';
import YsNoticeBar from '@/components/YsNoticeBar/index.vue';
import type { DataConfig, InteractionConfig, MessageItem } from '@/types/notice';

const dataConfig = ref<DataConfig>({
  messageList: [],
  tabDataSource: {
    all: [],
    unread: [],
    read: []
  }
});

const interactionConfig = ref<InteractionConfig>({
  trigger: {
    mode: 'click',
    hoverDelay: 200,
    leaveDelay: 300
  },
  item: {
    autoMarkRead: true,
    enableDoubleClick: false
  },
  center: {
    reloadOnTabChange: false,
    enableSort: true,
    enablePagination: true,
    pageSize: 20
  }
});

const handleMessageClick = (message: MessageItem) => {
  console.log('点击消息:', message);
};

const handleToCenter = () => {
  console.log('前往通知中心');
};

const handleMarkRead = (message: MessageItem) => {
  console.log('标记已读:', message);
};

const handleMarkAllRead = () => {
  console.log('全部标记已读');
};
</script>
```

### 示例4：配置消息来源类型

```vue
<template>
  <YsNoticeBar
    :data-config="dataConfig"
    :display-config="displayConfig"
    :style-config="styleConfig"
    @message-click="handleMessageClick"
    @to-center="handleToCenter"
  />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import YsNoticeBar from '@/components/YsNoticeBar/index.vue';
import type { DataConfig, DisplayConfig, StyleConfig, MessageItem } from '@/types/notice';

const dataConfig = ref<DataConfig>({
  sourceConfig: [
    {
      type: 'system',
      label: '系统通知',
      icon: 'ri-notification-3-line',
      color: '#409eff'
    },
    {
      type: 'notice',
      label: '公告通知',
      icon: 'ri-megaphone-line',
      color: '#67c23a'
    },
    {
      type: 'todo',
      label: '待办提醒',
      icon: 'ri-checkbox-circle-line',
      color: '#e6a23c'
    },
    {
      type: 'alert',
      label: '告警消息',
      icon: 'ri-alarm-warning-line',
      color: '#f56c6c'
    }
  ],
  messageList: {
    system: [],
    notice: [],
    todo: [],
    alert: []
  },
  tabDataSource: {
    all: {
      system: [],
      notice: [],
      todo: [],
      alert: []
    },
    unread: {
      system: [],
      notice: [],
      todo: [],
      alert: []
    },
    read: {
      system: [],
      notice: [],
      todo: [],
      alert: []
    }
  }
});

const displayConfig = ref<DisplayConfig>({});

const styleConfig = ref<StyleConfig>({
  theme: 'light',
  typeStyles: {
    system: {
      textColor: '#409eff',
      lineColor: '#409eff',
      bgColor: '#ecf5ff'
    },
    notice: {
      textColor: '#67c23a',
      lineColor: '#67c23a',
      bgColor: '#f0f9ff'
    },
    todo: {
      textColor: '#e6a23c',
      lineColor: '#e6a23c',
      bgColor: '#fdf6ec'
    },
    alert: {
      textColor: '#f56c6c',
      lineColor: '#f56c6c',
      bgColor: '#fef0f0'
    }
  }
});

onMounted(() => {
  loadMockData();
});

const loadMockData = () => {
  const mockSystemMessages: MessageItem[] = [
    {
      id: '1',
      type: 'system',
      title: '系统维护通知',
      content: '系统将于今晚22:00-24:00进行维护，请提前保存工作内容。',
      sendTime: new Date(Date.now() - 1000 * 60 * 30),
      sendUser: '系统管理员',
      readStatus: 0
    }
  ];
  
  const mockNoticeMessages: MessageItem[] = [
    {
      id: '2',
      type: 'notice',
      title: '新功能上线公告',
      content: '系统新增了消息通知功能，欢迎使用！',
      sendTime: new Date(Date.now() - 1000 * 60 * 60 * 2),
      sendUser: '运营平台',
      readStatus: 1
    }
  ];
  
  const mockTodoMessages: MessageItem[] = [
    {
      id: '3',
      type: 'todo',
      title: '待办任务提醒',
      content: '您有3个待办任务需要处理，请及时查看。',
      sendTime: new Date(Date.now() - 1000 * 60 * 60 * 24),
      sendUser: '任务系统',
      readStatus: 0
    }
  ];
  
  const mockAlertMessages: MessageItem[] = [
    {
      id: '4',
      type: 'alert',
      title: '服务器告警',
      content: '检测到服务器CPU使用率超过90%，请及时处理。',
      sendTime: new Date(Date.now() - 1000 * 60 * 10),
      sendUser: '监控系统',
      readStatus: 0
    }
  ];
  
  dataConfig.value.messageList = {
    system: mockSystemMessages,
    notice: mockNoticeMessages,
    todo: mockTodoMessages,
    alert: mockAlertMessages
  };
  
  const allMessages = [...mockSystemMessages, ...mockNoticeMessages, ...mockTodoMessages, ...mockAlertMessages];
  const unreadMessages = allMessages.filter(m => m.readStatus === 0);
  const readMessages = allMessages.filter(m => m.readStatus === 1);
  
  dataConfig.value.tabDataSource = {
    all: {
      system: mockSystemMessages,
      notice: mockNoticeMessages,
      todo: mockTodoMessages,
      alert: mockAlertMessages
    },
    unread: {
      system: mockSystemMessages.filter(m => m.readStatus === 0),
      notice: mockNoticeMessages.filter(m => m.readStatus === 0),
      todo: mockTodoMessages.filter(m => m.readStatus === 0),
      alert: mockAlertMessages.filter(m => m.readStatus === 0)
    },
    read: {
      system: mockSystemMessages.filter(m => m.readStatus === 1),
      notice: mockNoticeMessages.filter(m => m.readStatus === 1),
      todo: mockTodoMessages.filter(m => m.readStatus === 1),
      alert: mockAlertMessages.filter(m => m.readStatus === 1)
    }
  };
};

const handleMessageClick = (message: MessageItem) => {
  console.log('点击消息:', message);
};

const handleToCenter = () => {
  console.log('前往通知中心');
};
</script>
```

**功能说明：**

- 气泡弹窗顶部显示消息来源类型 Tab（系统通知、公告通知、待办提醒、告警消息）
- 点击不同来源 Tab 可切换查看对应来源的消息列表
- 通知中心采用双层 Tab 结构：
  - 上部 Tab：全部消息、未读消息、已读消息
  - 左侧次级 Tab：按消息来源类型分类
  - 右侧表格：展示对应筛选条件下的消息数据
- 支持自定义每个来源类型的图标、颜色和显示名称
- 响应式设计，小屏幕下次级 Tab 改为横向滚动

### 示例4：集成真实API

```vue
<template>
  <YsNoticeBar
    :data-config="dataConfig"
    :display-config="displayConfig"
    :interaction-config="interactionConfig"
    @message-click="handleMessageClick"
    @to-center="handleToCenter"
    @mark-read="handleMarkRead"
    @mark-all-read="handleMarkAllRead"
  />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import YsNoticeBar from '@/components/YsNoticeBar/index.vue';
import { useNoticeApi } from '@/api/system/notice';
import type { DataConfig, DisplayConfig, InteractionConfig, MessageItem } from '@/types/notice';

const dataConfig = ref<DataConfig>({
  messageList: [],
  tabDataSource: {
    all: [],
    unread: [],
    read: []
  },
  showRefreshBtn: true,
  onRefresh: loadNoticeData
});

const displayConfig = ref<DisplayConfig>({});

const interactionConfig = ref<InteractionConfig>({});

const api = useNoticeApi();

onMounted(() => {
  loadNoticeData();
});

const loadNoticeData = async () => {
  try {
    const res = await api.getUserNoticeList({ pageNo: 1, pageSize: 10 });
    if (res.code === 200) {
      const messages = res.data.list || [];
      dataConfig.value.messageList = messages;
      dataConfig.value.tabDataSource = {
        all: messages,
        unread: messages.filter(m => m.readStatus === 0),
        read: messages.filter(m => m.readStatus === 1)
      };
    }
  } catch (error) {
    console.error('加载通知数据失败:', error);
  }
};

const handleMessageClick = async (message: MessageItem) => {
  if (message.readStatus === 0) {
    try {
      await api.read(message.id);
      message.readStatus = 1;
    } catch (error) {
      console.error('标记已读失败:', error);
    }
  }
};

const handleToCenter = () => {
  console.log('前往通知中心');
};

const handleMarkRead = async (message: MessageItem) => {
  try {
    await api.read(message.id);
    message.readStatus = 1;
    loadNoticeData();
  } catch (error) {
    console.error('标记已读失败:', error);
  }
};

const handleMarkAllRead = async () => {
  const unreadMessages = dataConfig.value.messageList.filter(m => m.readStatus === 0);
  if (unreadMessages.length === 0) return;
  
  try {
    const ids = unreadMessages.map(m => m.id).join(',');
    await api.batchDelete(ids);
    dataConfig.value.messageList.forEach(m => m.readStatus = 1);
    loadNoticeData();
  } catch (error) {
    console.error('批量标记已读失败:', error);
  }
};
</script>
```

## 实现原理

### 组件架构

YsNoticeBar 组件采用模块化设计，由以下子组件组成：

1. **YsNoticeBar（主组件）**：负责铃铛图标、气泡弹窗触发、状态管理
2. **NoticePopover（气泡弹窗）**：负责消息列表展示、类型统计、底部操作
3. **NoticeCenter（通知中心）**：负责Tab切换、表格展示、分页排序
4. **NoticeDetailDialog（详情弹窗）**：负责消息详情展示、状态切换

### 数据流向

```
父组件
    ↓ (dataConfig)
YsNoticeBar
    ↓ (messageList)
NoticePopover
    ↓ (message-click)
父组件 (处理业务逻辑)
```

### 状态管理

组件内部使用 Vue 3 的 `ref` 和 `computed` 进行状态管理，通过 `emit` 向父组件传递事件，保持组件的纯净性和可复用性。

### 样式隔离

组件使用 `scoped` CSS 进行样式隔离，避免样式污染。同时支持通过 `customClass` 和 `customStyle` 进行样式覆盖。

## 注意事项

### 数据格式要求

1. **必填字段**：`id`、`type`、`title`、`content`、`sendTime`、`sendUser`、`readStatus` 为必填字段
2. **时间格式**：`sendTime` 支持字符串或 Date 对象，组件会自动格式化
3. **状态值**：`readStatus` 必须为 `0`（未读）或 `1`（已读）

### 性能优化建议

1. **大数据量**：建议开启虚拟滚动（配置 `enableLazyLoad`）
2. **数据更新**：避免频繁更新整个数组，使用增量更新
3. **事件处理**：避免在事件处理函数中进行复杂计算

### 样式定制建议

1. **主题切换**：通过 `styleConfig.theme` 切换主题，避免直接修改 CSS
2. **类型样式**：使用 `typeStyles` 配置不同消息类型的样式
3. **响应式**：通过 `responsive` 配置不同屏幕尺寸的样式

### 兼容性说明

1. **浏览器支持**：支持所有现代浏览器（Chrome、Firefox、Safari、Edge）
2. **Vue 版本**：兼容 Vue 3.0+ 所有版本
3. **Element Plus**：依赖 Element Plus 2.0+ 版本

## 维护指南

### 组件更新

1. **版本管理**：遵循语义化版本号（major.minor.patch）
2. **变更日志**：每次更新需记录变更内容
3. **向后兼容**：保持 API 的向后兼容性

### 问题排查

1. **气泡不显示**：检查 `dataConfig.messageList` 是否为空
2. **样式异常**：检查 `styleConfig` 配置是否正确
3. **事件不触发**：检查父组件是否正确绑定事件

### 扩展开发

1. **新增配置项**：在 `types/notice.d.ts` 中添加类型定义
2. **新增功能**：在对应子组件中实现功能逻辑
3. **新增插槽**：在模板中添加插槽位置

## 常见问题

### Q1：如何自定义消息类型图标？

A：通过 `displayConfig.common.typeIcons` 配置：

```typescript
displayConfig: {
  common: {
    typeIcons: {
      'system': 'ri-notification-3-line',
      'notice': 'ri-megaphone-line',
      'todo': 'ri-task-line'
    }
  }
}
```

### Q2：如何实现消息搜索功能？

A：在通知中心组件中已内置搜索功能，通过 `searchKeyword` 状态控制：

```vue
<el-input
  v-model="searchKeyword"
  placeholder="搜索消息标题"
  @keyup.enter="handleSearch"
/>
```

### Q3：如何实现消息分页加载？

A：通过 `dataConfig.enableLazyLoad` 和 `dataConfig.loadPageSize` 配置：

```typescript
dataConfig: {
  enableLazyLoad: true,
  lazyLoadThreshold: 50,
  loadPageSize: 10
}
```

### Q4：如何切换主题？

A：通过 `styleConfig.theme` 配置：

```typescript
styleConfig: {
  theme: 'dark'  // 'light' | 'dark' | 'custom'
}
```

### Q5：如何自定义详情弹窗？

A：组件已内置 `NoticeDetailDialog`，如需自定义，可通过 `message-click` 事件自行实现：

```vue
<YsNoticeBar
  @message-click="handleMessageClick"
/>

<YsDialog v-model="detailVisible">
  <!-- 自定义详情内容 -->
</YsDialog>
```

## 更新日志

### v1.0.0 (2024-01-13)

- ✅ 初始版本发布
- ✅ 实现基础消息通知功能
- ✅ 支持气泡弹窗和通知中心
- ✅ 支持全配置驱动
- ✅ 支持主题切换
- ✅ 支持响应式设计

## 技术支持

如有问题或建议，请通过以下方式联系：

- 项目地址：[项目仓库地址]
- 问题反馈：[Issue地址]
- 技术交流：[交流群/论坛]

## 许可证

MIT License
