import { ref, reactive, computed } from 'vue';

/**
 * 统计卡片数据类型
 */
export interface StatItem {
  id: number;
  title: string;
  value: number;
  suffix: string;
  icon: string;
  color: string;
  trend: 'up' | 'down';
  percentage: number;
  description: string;
}

/**
 * 通知数据类型
 */
export interface NotificationItem {
  id: number;
  type: 'system' | 'message' | 'todo';
  title: string;
  content: string;
  time: string;
  read: boolean;
}

/**
 * 快捷操作数据类型
 */
export interface QuickActionItem {
  id: number;
  name: string;
  icon: string;
  color: string;
  path: string;
}

/**
 * 系统状态数据类型
 */
export interface SystemStatusItem {
  cpu: number;
  memory: number;
  disk: number;
  uptime: string;
  version: string;
  lastUpdate: string;
}

/**
 * 操作日志数据类型
 */
export interface OperationLogItem {
  id: number;
  user: string;
  type: string;
  content: string;
  time: string;
  ip: string;
}

/**
 * 模拟数据管理 Hook
 * 提供首页所需的各种模拟数据
 */
export function useMockData() {
  const loading = ref(false);

  const statsData: StatItem[] = reactive([
    {
      id: 1,
      title: '用户总数',
      value: 128456,
      suffix: '人',
      icon: 'User',
      color: '#409eff',
      trend: 'up',
      percentage: 12.5,
      description: '较上月增长',
    },
    {
      id: 2,
      title: '订单数量',
      value: 58932,
      suffix: '单',
      icon: 'ShoppingCart',
      color: '#67c23a',
      trend: 'up',
      percentage: 8.3,
      description: '较上月增长',
    },
    {
      id: 3,
      title: '销售总额',
      value: 2456789,
      suffix: '元',
      icon: 'Money',
      color: '#e6a23c',
      trend: 'up',
      percentage: 15.2,
      description: '较上月增长',
    },
    {
      id: 4,
      title: '今日访问',
      value: 34567,
      suffix: '次',
      icon: 'View',
      color: '#f56c6c',
      trend: 'down',
      percentage: 3.2,
      description: '较昨日下降',
    },
    {
      id: 5,
      title: '转化率',
      value: 68.5,
      suffix: '%',
      icon: 'TrendCharts',
      color: '#667eea',
      trend: 'up',
      percentage: 5.8,
      description: '较上月增长',
    },
    {
      id: 6,
      title: '活跃用户',
      value: 8923,
      suffix: '人',
      icon: 'UserFilled',
      color: '#909399',
      trend: 'up',
      percentage: 22.1,
      description: '较昨日增长',
    },
  ]);

  const salesTrendData = computed(() => ({
    xAxis: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
    series: [
      {
        name: '销售额',
        data: [820, 932, 901, 1290, 1330, 1520, 1650, 1820, 2100, 2350, 2500, 2890],
      },
      {
        name: '订单量',
        data: [620, 732, 701, 990, 1030, 1220, 1350, 1520, 1800, 2050, 2200, 2590],
      },
    ],
  }));

  const orderDistributionData = computed(() => ({
    xAxis: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
    series: [
      {
        name: '订单数',
        data: [320, 432, 401, 590, 630, 820, 950],
      },
      {
        name: '完成数',
        data: [280, 382, 351, 520, 580, 750, 880],
      },
    ],
  }));

  const userSourceData = computed(() => [
    { value: 1048, name: '搜索引擎', color: '#409eff' },
    { value: 735, name: '直接访问', color: '#67c23a' },
    { value: 580, name: '邮件营销', color: '#e6a23c' },
    { value: 484, name: '联盟广告', color: '#f56c6c' },
    { value: 300, name: '视频广告', color: '#667eea' },
  ]);

  const realTimeData = computed(() => ({
    xAxis: ['00:00', '02:00', '04:00', '06:00', '08:00', '10:00', '12:00', '14:00', '16:00', '18:00', '20:00', '22:00'],
    series: [
      {
        name: '访问量',
        data: [300, 150, 80, 120, 450, 890, 1200, 1450, 1680, 1520, 1280, 980],
      },
      {
        name: '活跃用户',
        data: [120, 80, 40, 60, 280, 560, 780, 920, 1080, 980, 820, 640],
      },
    ],
  }));

  const quickActions: QuickActionItem[] = reactive([
    { id: 1, name: '用户管理', icon: 'User', color: '#409eff', path: '/system/user' },
    { id: 2, name: '系统设置', icon: 'Setting', color: '#67c23a', path: '/system/config' },
    { id: 3, name: '数据报表', icon: 'DataAnalysis', color: '#e6a23c', path: '/report' },
    { id: 4, name: '消息中心', icon: 'Message', color: '#f56c6c', path: '/message' },
    { id: 5, name: '文件管理', icon: 'Folder', color: '#667eea', path: '/file' },
    { id: 6, name: '日志查询', icon: 'Document', color: '#909399', path: '/log' },
  ]);

  const notifications: NotificationItem[] = reactive([
    {
      id: 1,
      type: 'system',
      title: '系统升级通知',
      content: '系统将于今晚22:00进行升级维护，预计持续2小时',
      time: '10分钟前',
      read: false,
    },
    {
      id: 2,
      type: 'message',
      title: '新消息提醒',
      content: '您有3条未读消息，请及时查看',
      time: '30分钟前',
      read: false,
    },
    {
      id: 3,
      type: 'todo',
      title: '待办事项',
      content: '您有5个待审批流程需要处理',
      time: '1小时前',
      read: false,
    },
    {
      id: 4,
      type: 'system',
      title: '安全提醒',
      content: '检测到异常登录行为，请确认是否为本人操作',
      time: '2小时前',
      read: true,
    },
    {
      id: 5,
      type: 'message',
      title: '任务完成通知',
      content: '数据导出任务已完成，请前往下载',
      time: '3小时前',
      read: true,
    },
  ]);

  const systemStatus = reactive({
    cpu: 45,
    memory: 62,
    disk: 38,
    uptime: '128天 5小时 32分钟',
    version: 'v2.5.0',
    lastUpdate: '2024-01-15',
  });

  const operationLogs = reactive([
    { id: 1, user: '管理员', type: '登录', content: '用户登录系统', time: '2024-01-20 14:32:15', ip: '192.168.1.100' },
    { id: 2, user: '张三', type: '修改', content: '修改用户信息', time: '2024-01-20 14:28:33', ip: '192.168.1.101' },
    { id: 3, user: '李四', type: '新增', content: '新增订单数据', time: '2024-01-20 14:25:18', ip: '192.168.1.102' },
    { id: 4, user: '王五', type: '删除', content: '删除过期数据', time: '2024-01-20 14:20:45', ip: '192.168.1.103' },
    { id: 5, user: '赵六', type: '导出', content: '导出报表数据', time: '2024-01-20 14:15:22', ip: '192.168.1.104' },
    { id: 6, user: '管理员', type: '配置', content: '修改系统配置', time: '2024-01-20 14:10:08', ip: '192.168.1.100' },
  ]);

  const refreshData = async () => {
    loading.value = true;
    await new Promise((resolve) => setTimeout(resolve, 500));
    loading.value = false;
  };

  return {
    loading,
    statsData,
    salesTrendData,
    orderDistributionData,
    userSourceData,
    realTimeData,
    quickActions,
    notifications,
    systemStatus,
    operationLogs,
    refreshData,
  };
}
