/**
 * YsNoticeBar 消息通知组件类型定义
 * @description 定义消息通知组件的所有类型接口
 */

/**
 * 消息项基础数据模型
 * @description 所有消息数据源必须包含以下核心属性
 */
export interface MessageItem {
  /** 唯一标识 */
  id: string | number;
  /** 消息类型，支持自定义枚举（如：system/notice/todo/alert） */
  type: string;
  /** 消息标题 */
  title: string;
  /** 消息正文内容 */
  content: string;
  /** 消息发布时间，支持字符串/日期格式 */
  sendTime: string | Date;
  /** 消息发布人/来源方 */
  sendUser: string;
  /** 阅读状态，0=未读，1=已读 */
  readStatus: 0 | 1;
  /** 支持扩展任意业务字段 */
  [key: string]: any;
}

/**
 * 消息类型配置
 */
export interface MessageTypeConfig {
  /** 类型标识 */
  type: string;
  /** 类型显示名称 */
  label: string;
  /** 类型图标类名 */
  icon?: string;
  /** 类型颜色 */
  color?: string;
}

/**
 * 消息来源类型配置
 * @description 用于配置不同消息来源的类型信息
 */
export interface MessageSourceConfig {
  /** 来源类型标识（如：system/notice/todo/alert） */
  type: string;
  /** 来源类型显示名称 */
  label: string;
  /** 来源类型图标类名 */
  icon?: string;
  /** 来源类型主题颜色 */
  color?: string;
}

/**
 * 显示配置
 */
export interface DisplayConfig {
  /** 铃铛图标配置 */
  icon?: {
    /** 图标大小，默认 20px */
    size?: number | string;
    /** 图标颜色，默认继承主题色 */
    color?: string;
    /** hover颜色 */
    hoverColor?: string;
    /** 是否显示未读消息小红点，默认 true */
    showBadge?: boolean;
    /** 小红点位置，默认 'top-right' */
    badgePosition?: 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left';
    /** 小红点颜色，默认 '#f56c6c' */
    badgeColor?: string;
    /** 小红点大小，默认 8px */
    badgeSize?: number;
    /** 图标是否旋转动画，默认 false */
    rotate?: boolean;
    /** 图标是否闪烁动画，默认 false */
    blink?: boolean;
  };
  /** 气泡弹窗配置 */
  popover?: {
    /** 弹窗宽度，默认 380px */
    width?: number | string;
    /** 弹窗高度，默认 'auto' */
    height?: number | string;
    /** 最大高度，超出滚动，默认 500px */
    maxHeight?: number | string;
    /** 弹窗定位偏移量，防止遮挡导航 */
    offset?: number;
    /** 弹窗圆角，默认 8px */
    borderRadius?: number | string;
    /** 弹窗阴影，默认 true */
    shadow?: boolean;
    /** 是否显示遮罩层，默认 false */
    showMask?: boolean;
  };
  /** 气泡顶部区域配置 */
  header?: {
    /** 类型文本字号，默认 14px */
    fontSize?: number | string;
    /** 类型文本颜色，默认 '#606266' */
    textColor?: string;
    /** 类型文本粗细，默认 'normal' */
    fontWeight?: string;
    /** 数量角标背景色，默认 '#409eff' */
    badgeBgColor?: string;
    /** 数量角标文字色，默认 '#ffffff' */
    badgeTextColor?: string;
    /** 数量角标圆角，默认 10px */
    badgeBorderRadius?: number | string;
    /** 数量角标大小，默认 16px */
    badgeSize?: number | string;
    /** 是否换行展示多类型，默认 false */
    wrap?: boolean;
    /** 空消息时的占位文案，默认 '暂无消息' */
    emptyText?: string;
  };
  /** 气泡消息列表配置 */
  list?: {
    /** 列表项高度，默认 'auto' */
    itemHeight?: number | string;
    /** 列表项内边距，默认 '12px 16px' */
    itemPadding?: string;
    /** 项之间的间距，默认 0 */
    itemGap?: number | string;
    /** 未读项左侧竖线颜色，默认 '#409eff' */
    unreadLineColor?: string;
    /** 未读项背景色，默认 '#ecf5ff' */
    unreadBgColor?: string;
    /** 未读项字体粗细，默认 'bold' */
    unreadFontWeight?: string;
    /** 已读项背景色，默认 'transparent' */
    readBgColor?: string;
    /** 已读项字体粗细，默认 'normal' */
    readFontWeight?: string;
    /** 列表项hover样式，默认 true */
    hoverEffect?: boolean;
    /** 列表项hover背景色，默认 '#f5f7fa' */
    hoverBgColor?: string;
    /** 空列表占位文案，默认 '暂无消息' */
    emptyText?: string;
    /** 是否显示空列表占位图，默认 true */
    showEmptyImage?: boolean;
    /** 空列表占位图URL */
    emptyImageUrl?: string;
  };
  /** 底部按钮配置 */
  footer?: {
    /** 按钮宽度，默认 '100%' */
    width?: number | string;
    /** 按钮高度，默认 40px */
    height?: number | string;
    /** 按钮文案，默认 '前往通知中心' */
    text?: string;
    /** 按钮字体大小，默认 14px */
    fontSize?: number | string;
    /** 按钮字体颜色，默认 '#ffffff' */
    textColor?: string;
    /** 按钮背景色，默认 '#409eff' */
    bgColor?: string;
    /** 按钮hover背景色，默认 '#66b1ff' */
    hoverBgColor?: string;
    /** 按钮点击背景色，默认 '#3a8ee6' */
    activeBgColor?: string;
    /** 按钮圆角，默认 4px */
    borderRadius?: number | string;
    /** 是否显示图标，默认 true */
    showIcon?: boolean;
    /** 图标位置，默认 'left' */
    iconPosition?: 'left' | 'right';
    /** 图标类名，默认 'ri-arrow-right-line' */
    icon?: string;
  };
  /** 通知中心页面配置 */
  center?: {
    /** 页面整体宽度，默认 '90%' */
    width?: number | string;
    /** 页面整体高度，默认 '90vh' */
    height?: number | string;
    /** 是否全屏展示，默认 false */
    fullscreen?: boolean;
    /** Tab栏高度，默认 50px */
    tabHeight?: number | string;
    /** Tab间距，默认 20px */
    tabGap?: number | string;
    /** Tab激活态下划线颜色，默认 '#409eff' */
    tabActiveColor?: string;
    /** Tab激活态字体粗细，默认 'bold' */
    tabActiveFontWeight?: string;
    /** 表格边框，默认 true */
    tableBorder?: boolean;
    /** 表格斑马纹，默认 true */
    tableStripe?: boolean;
    /** 表格hover高亮，默认 true */
    tableHover?: boolean;
    /** 操作栏按钮文字，默认 '查看' */
    actionButtonText?: string;
    /** 操作栏按钮类型，默认 'primary' */
    actionButtonType?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'text';
  };
  /** 通用显示规则 */
  common?: {
    /** 时间格式化规则，默认 'YYYY-MM-DD HH:mm:ss' */
    timeFormat?: string;
    /** 超长文本处理方式，默认 'ellipsis' */
    textOverflow?: 'ellipsis' | 'wrap' | 'tooltip';
    /** 超长文本最大行数，默认 2 */
    maxLines?: number;
    /** 消息类型对应图标配置 */
    typeIcons?: Record<string, string>;
    /** 气泡展开动画，默认 'fade' */
    openAnimation?: 'fade' | 'slide' | 'zoom';
    /** 气泡收起动画，默认 'fade' */
    closeAnimation?: 'fade' | 'slide' | 'zoom';
    /** 动画时长，默认 300ms */
    animationDuration?: number;
  };
}

/**
 * 交互配置
 */
export interface InteractionConfig {
  /** 气泡触发规则 */
  trigger?: {
    /** 触发方式，默认 'hover' */
    mode?: 'hover' | 'click' | 'both';
    /** 移入延迟展开时长，默认 200ms */
    hoverDelay?: number;
    /** 移出后延迟收起时长，默认 300ms */
    leaveDelay?: number;
    /** 点击空白处是否收起气泡，默认 true */
    clickOutsideToClose?: boolean;
    /** 点击铃铛图标是否强制收起，默认 true */
    clickToToggle?: boolean;
  };
  /** 消息项交互 */
  item?: {
    /** 点击消息项后是否自动标记为已读，默认 true */
    autoMarkRead?: boolean;
    /** 是否支持双击消息项触发其他事件，默认 false */
    enableDoubleClick?: boolean;
    /** hover消息项是否显示预览文案，默认 false */
    showPreview?: boolean;
  };
  /** 自动行为配置 */
  auto?: {
    /** 是否开启消息列表自动滚动播放，默认 false */
    enableAutoScroll?: boolean;
    /** 滚动方向，默认 'up' */
    scrollDirection?: 'up' | 'down';
    /** 滚动速度，默认 2000ms */
    scrollSpeed?: number;
    /** 滚动暂停规则，默认 'hover' */
    scrollPause?: 'hover' | 'click' | 'none';
    /** 自动滚动循环次数，默认 0（无限循环） */
    scrollLoop?: number;
  };
  /** 通知中心交互 */
  center?: {
    /** Tab切换是否触发重新加载数据源，默认 false */
    reloadOnTabChange?: boolean;
    /** 表格是否支持排序，默认 true */
    enableSort?: boolean;
    /** 表格默认排序字段，默认 'sendTime' */
    sortField?: string;
    /** 表格默认排序方式，默认 'desc' */
    sortOrder?: 'asc' | 'desc';
    /** 表格是否支持分页，默认 true */
    enablePagination?: boolean;
    /** 每页条数，默认 10 */
    pageSize?: number;
    /** 分页大小选项，默认 [10, 20, 50, 100] */
    pageSizes?: number[];
    /** 关闭通知中心页面是否触发回调，默认 false */
    onCloseCallback?: boolean;
  };
  /** 状态缓存配置 */
  cache?: {
    /** 是否缓存消息的已读/未读状态，默认 true */
    cacheReadStatus?: boolean;
    /** 是否缓存用户最后一次查看的消息ID，默认 false */
    cacheLastViewId?: boolean;
    /** 是否在页面刷新后保留气泡展开状态，默认 false */
    cachePopoverState?: boolean;
  };
}

/**
 * 数据配置
 */
export interface DataConfig {
  /** 消息来源类型配置 */
  sourceConfig?: MessageSourceConfig[];
  /** 气泡弹窗的消息列表数据源（按来源类型分组） */
  messageList: Record<string, MessageItem[]>;
  /** 通知中心3个Tab的数据源（嵌套结构：按来源类型和状态双重分组） */
  tabDataSource: {
    /** 全部消息数据源（按来源类型分组） */
    all: Record<string, MessageItem[]>;
    /** 未读消息数据源（按来源类型分组） */
    unread: Record<string, MessageItem[]>;
    /** 已读消息数据源（按来源类型分组） */
    read: Record<string, MessageItem[]>;
  };
  /** 是否支持懒加载，默认 false */
  enableLazyLoad?: boolean;
  /** 懒加载阈值，默认 50 */
  lazyLoadThreshold?: number;
  /** 每次加载条数，默认 10 */
  loadPageSize?: number;
  /** 加载中占位文案，默认 '加载中...' */
  loadingText?: string;
  /** 是否过滤重复消息，默认 true */
  filterDuplicate?: boolean;
  /** 是否过滤过期消息，默认 false */
  filterExpired?: boolean;
  /** 过期时间（天），默认 30 */
  expiredDays?: number;
  /** 是否支持按消息类型过滤气泡列表，默认 false */
  enableTypeFilter?: boolean;
  /** 允许的消息类型列表 */
  allowedTypes?: string[];
  /** 是否支持按发布人筛选，默认 false */
  enableUserFilter?: boolean;
  /** 允许的发布人列表 */
  allowedUsers?: string[];
  /** 空数据时的占位文案，默认 '暂无数据' */
  emptyText?: string;
  /** 是否显示刷新数据按钮，默认 false */
  showRefreshBtn?: boolean;
  /** 点击刷新按钮触发的回调函数 */
  onRefresh?: () => Promise<void> | void;
}

/**
 * 样式配置
 */
export interface StyleConfig {
  /** 全局主题配置 */
  theme?: 'light' | 'dark' | 'custom';
  /** 自定义主题颜色 */
  customTheme?: {
    /** 主色调 */
    primaryColor?: string;
    /** 成功色 */
    successColor?: string;
    /** 警告色 */
    warningColor?: string;
    /** 危险色 */
    dangerColor?: string;
    /** 信息色 */
    infoColor?: string;
    /** 文本色 */
    textColor?: string;
    /** 边框色 */
    borderColor?: string;
    /** 背景色 */
    bgColor?: string;
  };
  /** 消息类型差异化样式 */
  typeStyles?: Record<string, {
    /** 类型文本颜色 */
    textColor?: string;
    /** 左侧标识颜色 */
    lineColor?: string;
    /** 图标样式 */
    icon?: string;
    /** 列表项背景色 */
    bgColor?: string;
  }>;
  /** 状态差异化样式 */
  statusStyles?: {
    /** 未读样式 */
    unread?: {
      /** 文字粗细 */
      fontWeight?: string;
      /** 背景色 */
      bgColor?: string;
      /** 边框 */
      border?: string;
      /** 是否显示未读红点 */
      showDot?: boolean;
    };
    /** 已读样式 */
    read?: {
      /** 文字粗细 */
      fontWeight?: string;
      /** 背景色 */
      bgColor?: string;
      /** 边框 */
      border?: string;
    };
  };
  /** 自定义样式注入 */
  customClass?: string;
  customStyle?: Record<string, any>;
  /** 响应式样式配置 */
  responsive?: {
    /** 大屏断点，默认 1920px */
    large?: number;
    /** 中屏断点，默认 1366px */
    medium?: number;
    /** 小屏断点，默认 768px */
    small?: number;
  };
}

/**
 * 组件Props总配置
 */
export interface YsNoticeBarProps {
  /** 显示配置 */
  displayConfig?: DisplayConfig;
  /** 交互配置 */
  interactionConfig?: InteractionConfig;
  /** 数据配置 */
  dataConfig: DataConfig;
  /** 样式配置 */
  styleConfig?: StyleConfig;
}

/**
 * 组件Events
 */
export interface YsNoticeBarEmits {
  /** 点击消息列表项/通知中心表格查看按钮时触发 */
  (e: 'message-click', message: MessageItem): void;
  /** 点击前往通知中心按钮时触发 */
  (e: 'to-center'): void;
  /** 气泡弹窗展开时触发 */
  (e: 'popover-show'): void;
  /** 气泡弹窗收起时触发 */
  (e: 'popover-hide'): void;
  /** 标记已读时触发 */
  (e: 'mark-read', message: MessageItem): void;
  /** 全部标记已读时触发 */
  (e: 'mark-all-read'): void;
  /** 通知中心搜索时触发 */
  (e: 'search', payload: {
    title: string;
    tab: 'all' | 'unread' | 'read';
    source: string;
    resetPage?: boolean;
  }): void;
}

/**
 * 通知中心Props
 */
export interface NoticeCenterProps {
  /** Tab数据源（嵌套结构：按来源类型和状态双重分组） */
  tabDataSource: DataConfig['tabDataSource'];
  /** 消息来源类型配置 */
  sourceConfig?: MessageSourceConfig[];
  /** 显示配置 */
  displayConfig?: DisplayConfig;
  /** 交互配置 */
  interactionConfig?: InteractionConfig['center'];
  /** 样式配置 */
  styleConfig?: StyleConfig;
}

/**
 * 通知中心Emits
 */
export interface NoticeCenterEmits {
  /** 点击查看按钮时触发 */
  (e: 'message-click', message: MessageItem): void;
  /** 关闭通知中心时触发 */
  (e: 'close'): void;
  /** Tab切换时触发 */
  (e: 'tab-change', tab: 'all' | 'unread' | 'read'): void;
}
