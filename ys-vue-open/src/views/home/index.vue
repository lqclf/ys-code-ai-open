<template>
  <div class="home-container">
    <!-- 页面头部欢迎区域 -->
    <el-row :gutter="20" class="mb-20">
      <el-col :span="24">
        <div class="welcome-section">
          <div class="welcome-content">
            <div class="welcome-info">
              <div class="user-avatar">
                <el-avatar :size="80" :src="userInfo.avatar">
                  <el-icon>
                    <UserFilled />
                  </el-icon>
                </el-avatar>
                <div class="online-indicator" v-if="userInfo.isOnline"></div>
              </div>
              <div class="welcome-text">
                <h1 class="greeting">{{ currentGreeting }}, {{ userInfo.name }}!</h1>
                <p class="welcome-subtitle">
                  <el-icon>
                    <Calendar />
                  </el-icon>
                  {{ currentDate }}
                  <span class="time-display">{{ currentTime }}</span>
                </p>
                <div class="weather-info" v-if="weather">
                  <el-icon>
                    <Sunny />
                  </el-icon>
                  {{ weather.temperature }}°C · {{ weather.description }}
                </div>
              </div>
            </div>

            <!-- 快速统计 -->
            <div class="quick-stats">
              <div class="stat-card-mini">
                <div class="stat-icon users">
                  <el-icon>
                    <User />
                  </el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-number">{{ formatNumber(onlineUsers.count) }}</div>
                  <div class="stat-label">在线用户</div>
                </div>
              </div>

              <div class="stat-card-mini">
                <div class="stat-icon visits">
                  <el-icon>
                    <View />
                  </el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-number">{{ formatNumber(totalVisits.count) }}</div>
                  <div class="stat-label">总访问量</div>
                </div>
              </div>

              <div class="stat-card-mini">
                <div class="stat-icon today">
                  <el-icon>
                    <TrendCharts />
                  </el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-number">{{ formatNumber(todayVisits.count) }}</div>
                  <div class="stat-label">今日访问</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 关键指标卡片 -->
    <el-row :gutter="20" class="mb-20">
      <el-col v-for="(metric, index) in keyMetrics" :key="index" :xs="24" :sm="12" :lg="6">
        <div class="metric-card" :class="metric.theme" @click="handleMetricClick(metric)">
          <div class="metric-header">
            <div class="metric-icon-wrapper">
              <div class="metric-icon" :class="metric.theme">
                <el-icon :size="20">
                  <component :is="metric.icon" />
                </el-icon>
              </div>
            </div>
            <div class="metric-trend" :class="metric.trend.direction">
              <el-icon>
                <ArrowUp v-if="metric.trend.direction === 'up'" />
                <ArrowDown v-else />
              </el-icon>
              <span class="trend-value">{{ Math.abs(metric.trend.value) }}%</span>
            </div>
          </div>
          <div class="metric-content">
            <div class="metric-main">
              <h3 class="metric-title">{{ metric.title }}</h3>
              <div class="metric-value">{{ metric.value }}</div>
            </div>
            <div class="metric-side">
              <div class="metric-description">{{ metric.description }}</div>
            </div>
          </div>
          <div class="metric-chart">
            <div class="mini-chart" :style="{ background: metric.chartGradient }"></div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 新增图表卡片区域 -->
    <el-row :gutter="20" class="mb-20">
      <!-- 雷达图 - 性能指标分析 -->
      <el-col :xs="24" :lg="8">
        <div class="chart-container">
          <div class="chart-header">
            <div class="chart-title">
              <h3>性能指标分析</h3>
              <p class="chart-subtitle">系统关键性能指标对比</p>
            </div>
          </div>
          <div class="chart-content">
            <div v-if="chartsLoading.radarChart" class="chart-loading">
              <el-skeleton animated :rows="4" />
            </div>
            <div v-else-if="chartsError.radarChart" class="chart-error">
              <el-empty description="图表加载失败" :image-size="80">
                <el-button type="primary" @click="retryChartLoad('radarChart')">重试</el-button>
              </el-empty>
            </div>
            <v-chart v-else class="chart" :option="radarChartOption" autoresize />
          </div>
        </div>
      </el-col>

      <!-- 散点图 - 用户行为分析 -->
      <el-col :xs="24" :lg="8">
        <div class="chart-container">
          <div class="chart-header">
            <div class="chart-title">
              <h3>用户行为分析</h3>
              <p class="chart-subtitle">用户活跃度与转化关系</p>
            </div>
          </div>
          <div class="chart-content">
            <div v-if="chartsLoading.scatterChart" class="chart-loading">
              <el-skeleton animated :rows="4" />
            </div>
            <div v-else-if="chartsError.scatterChart" class="chart-error">
              <el-empty description="图表加载失败" :image-size="80">
                <el-button type="primary" @click="retryChartLoad('scatterChart')">重试</el-button>
              </el-empty>
            </div>
            <v-chart v-else class="chart" :option="scatterChartOption" autoresize />
          </div>
        </div>
      </el-col>

      <!-- 热力图 - 访问时段分析 -->
      <el-col :xs="24" :lg="8">
        <div class="chart-container">
          <div class="chart-header">
            <div class="chart-title">
              <h3>访问时段分析</h3>
              <p class="chart-subtitle">一周内各时段访问热度</p>
            </div>
          </div>
          <div class="chart-content">
            <div v-if="chartsLoading.heatmapChart" class="chart-loading">
              <el-skeleton animated :rows="4" />
            </div>
            <div v-else-if="chartsError.heatmapChart" class="chart-error">
              <el-empty description="图表加载失败" :image-size="80">
                <el-button type="primary" @click="retryChartLoad('heatmapChart')">重试</el-button>
              </el-empty>
            </div>
            <v-chart v-else class="chart" :option="heatmapChartOption" autoresize />
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 主要图表区域 -->
    <el-row :gutter="20" class="mb-20">
      <!-- 访问趋势折线图 -->
      <el-col :xs="24" :xl="12">
        <div class="chart-container">
          <div class="chart-header">
            <div class="chart-title">
              <h3>访问趋势分析</h3>
              <p class="chart-subtitle">近{{ chartPeriods[activePeriod] }}访问量变化</p>
            </div>
            <div class="chart-controls">
              <el-button-group v-model="activePeriod" class="period-selector">
                <el-button v-for="(label, key) in chartPeriods" :key="key"
                  :type="activePeriod === key ? 'primary' : 'default'" size="small" @click="handlePeriodChange(key)">
                  {{ label }}
                </el-button>
              </el-button-group>
            </div>
          </div>

          <div class="chart-content">
            <div v-if="chartsLoading.lineChart" class="chart-loading">
              <el-skeleton animated :rows="4" />
            </div>
            <div v-else-if="chartsError.lineChart" class="chart-error">
              <el-empty description="图表加载失败" :image-size="80">
                <el-button type="primary" @click="retryChartLoad('lineChart')">重试</el-button>
              </el-empty>
            </div>
            <v-chart v-else class="chart" :option="lineChartOption" autoresize @click="handleChartClick" />
          </div>
        </div>
      </el-col>

      <!-- 用户来源饼图 -->
      <el-col :xs="24" :xl="12">
        <div class="chart-container">
          <div class="chart-header">
            <div class="chart-title">
              <h3>用户来源分布</h3>
              <p class="chart-subtitle">不同渠道用户占比分析</p>
            </div>
          </div>

          <div class="chart-content">
            <div v-if="chartsLoading.pieChart" class="chart-loading">
              <el-skeleton animated :rows="4" />
            </div>
            <div v-else-if="chartsError.pieChart" class="chart-error">
              <el-empty description="图表加载失败" :image-size="80">
                <el-button type="primary" @click="retryChartLoad('pieChart')">重试</el-button>
              </el-empty>
            </div>
            <v-chart v-else class="chart" :option="pieChartOption" autoresize @click="handleChartClick" />
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 底部区域 -->
    <el-row :gutter="20">
      <!-- 模块访问量柱状图 -->
      <el-col :xs="24" :xl="16">
        <div class="chart-container">
          <div class="chart-header">
            <div class="chart-title">
              <h3>模块访问统计</h3>
              <p class="chart-subtitle">各功能模块访问量对比</p>
            </div>
            <div class="chart-controls">
              <el-dropdown @command="handleModuleSort">
                <span class="el-dropdown-link">
                  排序方式<i class="el-icon--right el-icon-arrow-down"></i>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="asc">访问量升序</el-dropdown-item>
                    <el-dropdown-item command="desc">访问量降序</el-dropdown-item>
                    <el-dropdown-item command="name">按名称排序</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>

          <div class="chart-content">
            <div v-if="chartsLoading.barChart" class="chart-loading">
              <el-skeleton animated :rows="4" />
            </div>
            <div v-else-if="chartsError.barChart" class="chart-error">
              <el-empty description="图表加载失败" :image-size="80">
                <el-button type="primary" @click="retryChartLoad('barChart')">重试</el-button>
              </el-empty>
            </div>
            <v-chart v-else class="chart" :option="barChartOption" autoresize @click="handleChartClick" />
          </div>
        </div>
      </el-col>

      <!-- 最新动态 -->
      <el-col :xs="24" :xl="8">
        <div class="activities-panel">
          <div class="panel-header">
            <h3>系统动态</h3>
            <el-badge :value="unreadActivities" class="activity-badge">
              <el-button size="small" circle>
                <el-icon>
                  <Bell />
                </el-icon>
              </el-button>
            </el-badge>
          </div>

          <div class="activities-list">
            <div v-for="(activity, index) in activities" :key="index" class="activity-item"
              :class="{ 'unread': !activity.read }" @click="markAsRead(activity)">
              <div class="activity-icon" :class="activity.type">
                <el-icon>
                  <component :is="activity.icon" />
                </el-icon>
              </div>
              <div class="activity-content">
                <div class="activity-title">{{ activity.title }}</div>
                <div class="activity-description">{{ activity.description }}</div>
                <div class="activity-time">
                  <el-icon>
                    <Clock />
                  </el-icon>
                  {{ activity.time }}
                </div>
              </div>
              <div v-if="!activity.read" class="activity-indicator"></div>
            </div>
          </div>

          <div class="panel-footer">
            <el-button link @click="viewAllActivities">
              查看全部动态<i class="el-icon--right el-icon-arrow-right"></i>
            </el-button>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick, watch } from 'vue';
import { useThemeConfig } from '@/stores/themeConfig';
import { formatDate } from '@/utils/formatTime';
import { ElMessage, ElNotification } from 'element-plus';
import {
  User, UserFilled, Calendar, Sunny, View, TrendCharts, ArrowUp, ArrowDown,
  Bell, Clock, ShoppingCart, Document, DataAnalysis, Setting, Warning,
  Check, Star
} from '@element-plus/icons-vue';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { LineChart, PieChart, BarChart, RadarChart, ScatterChart, HeatmapChart } from 'echarts/charts';
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  PolarComponent,
  VisualMapComponent,
  CalendarComponent
} from 'echarts/components';
import VChart from 'vue-echarts';

// 注册ECharts组件
use([
  CanvasRenderer,
  LineChart,
  PieChart,
  BarChart,
  RadarChart,
  ScatterChart,
  HeatmapChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  PolarComponent,
  VisualMapComponent,
  CalendarComponent
]);

// VChart组件已经通过import导入，无需再次注册

// 存储
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storesThemeConfig;

// 响应式数据
const currentTime = ref('');
const currentDate = ref('');
const isDark = computed(() => themeConfig.isDark);

// 用户信息
const userInfo = reactive({
  name: '管理员',
  avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
  isOnline: true
});

// 统计数据
const onlineUsers = reactive({
  count: 128,
  change: '+5.2%'
});

const totalVisits = reactive({
  count: 128456,
  change: '+12.8%'
});

const todayVisits = reactive({
  count: 1245,
  change: '+8.7%'
});

// 天气信息
const weather = ref({
  temperature: 22,
  description: '晴朗'
});

// 关键指标
const keyMetrics = ref([
  {
    title: '总销售额',
    value: '¥128,000',
    icon: ShoppingCart,
    theme: 'primary',
    trend: { direction: 'up', value: 12.5 },
    description: '较上月增长',
    chartGradient: 'linear-gradient(135deg, #409EFF 0%, #337ECC 100%)',
  },
  {
    title: '订单数量',
    value: '1,280',
    icon: Document,
    theme: 'success',
    trend: { direction: 'down', value: 3.2 },
    description: '较上月下降',
    chartGradient: 'linear-gradient(135deg, #67C23A 0%, #529B2E 100%)',
  },
  {
    title: '用户增长',
    value: '8,654',
    icon: User,
    theme: 'warning',
    trend: { direction: 'up', value: 8.7 },
    description: '较上月增长',
    chartGradient: 'linear-gradient(135deg, #E6A23C 0%, #CF9236 100%)',
  },
  {
    title: '转化率',
    value: '24.5%',
    icon: DataAnalysis,
    theme: 'info',
    trend: { direction: 'up', value: 1.2 },
    description: '较上月增长',
    chartGradient: 'linear-gradient(135deg, #909399 0%, #787B7F 100%)',
  }
]);

// 图表数据
const chartsLoading = reactive({
  lineChart: false,
  pieChart: false,
  barChart: false,
  radarChart: false,
  scatterChart: false,
  heatmapChart: false
});

const chartsError = reactive({
  lineChart: false,
  pieChart: false,
  barChart: false,
  radarChart: false,
  scatterChart: false,
  heatmapChart: false
});

// 图表配置
const lineChartOption = ref({});
const pieChartOption = ref({});
const barChartOption = ref({});
const radarChartOption = ref({});
const scatterChartOption = ref({});
const heatmapChartOption = ref({});

// 图表周期
const chartPeriods = {
  week: '一周',
  month: '一月',
  quarter: '一季度'
};

const activePeriod = ref('week');

// 活动数据
const activities = ref([
  {
    type: 'success',
    icon: Check,
    title: '系统升级完成',
    description: '系统功能已成功升级，性能优化完成',
    time: '5分钟前',
    read: false
  },
  {
    type: 'warning',
    icon: Warning,
    title: '服务器维护通知',
    description: '预计今晚23:00-01:00进行服务器维护',
    time: '1小时前',
    read: false
  },
  {
    type: 'info',
    icon: Warning,
    title: '新功能上线',
    description: '数据分析模块已上线，欢迎体验',
    time: '3小时前',
    read: true
  },
  {
    type: 'primary',
    icon: Star,
    title: '用户反馈收集',
    description: '感谢您的反馈，我们将持续优化产品',
    time: '1天前',
    read: true
  }
]);

const unreadActivities = computed(() =>
  activities.value.filter(activity => !activity.read).length
);

// 工具函数
const formatNumber = (num: number) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w';
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k';
  }
  return num.toString();
};

// 计算问候语
const currentGreeting = computed(() => {
  const hour = new Date().getHours();
  if (hour < 6) return '深夜好';
  if (hour < 9) return '早上好';
  if (hour < 12) return '上午好';
  if (hour < 14) return '中午好';
  if (hour < 17) return '下午好';
  if (hour < 19) return '傍晚好';
  if (hour < 22) return '晚上好';
  return '深夜好';
});

// 模拟数据生成
const generateChartData = (period: string) => {
  const days = period === 'week' ? 7 : period === 'month' ? 30 : 90;
  const labels = [];
  const visitData = [];
  const userData = [];

  for (let i = days - 1; i >= 0; i--) {
    const date = new Date();
    date.setDate(date.getDate() - i);

    if (period === 'week') {
      labels.push(['周一', '周二', '周三', '周四', '周五', '周六', '周日'][6 - i]);
    } else if (period === 'month') {
      labels.push(`${Math.ceil((i + 1) / 7)}周`);
    } else {
      labels.push(`${Math.ceil((i + 1) / 30)}月`);
    }

    visitData.push(Math.floor(Math.random() * 1000) + 500);
    userData.push(Math.floor(Math.random() * 800) + 300);
  }

  return { labels, visitData, userData };
};

// 初始化折线图
const initLineChart = async () => {
  chartsLoading.lineChart = true;
  chartsError.lineChart = false;

  try {
    await new Promise(resolve => setTimeout(resolve, 1000)); // 模拟加载
    const data = generateChartData(activePeriod.value);

    lineChartOption.value = {
      tooltip: {
        trigger: 'axis',
        backgroundColor: isDark.value ? '#1f1f1f' : '#ffffff',
        borderColor: isDark.value ? '#404040' : '#e8eaec',
        textStyle: { color: isDark.value ? '#ffffff' : '#333333' }
      },
      legend: {
        data: ['访问量', '独立访客'],
        textStyle: { color: isDark.value ? '#ffffff' : '#333333' }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: data.labels,
        axisLine: { lineStyle: { color: isDark.value ? '#404040' : '#e8eaec' } },
        axisLabel: { color: isDark.value ? '#cccccc' : '#666666' }
      },
      yAxis: {
        type: 'value',
        axisLine: { lineStyle: { color: isDark.value ? '#404040' : '#e8eaec' } },
        axisLabel: { color: isDark.value ? '#cccccc' : '#666666' }
      },
      splitLine: { lineStyle: { color: isDark.value ? '#404040' : '#f0f0f0' } },
      series: [
        {
          name: '访问量',
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: { width: 3, color: '#409EFF' },
          itemStyle: { color: '#409EFF' },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
              ]
            }
          },
          data: data.visitData
        },
        {
          name: '独立访客',
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: { width: 3, color: '#67C23A' },
          itemStyle: { color: '#67C23A' },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
                { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
              ]
            }
          },
          data: data.userData
        }
      ]
    };
  } catch (error) {
    chartsError.lineChart = true;
    ElMessage.error('折线图加载失败');
  } finally {
    chartsLoading.lineChart = false;
  }
};

// 初始化饼图
const initPieChart = async () => {
  chartsLoading.pieChart = true;
  chartsError.pieChart = false;

  try {
    await new Promise(resolve => setTimeout(resolve, 800)); // 模拟加载

    pieChartOption.value = {
      tooltip: {
        trigger: 'item',
        backgroundColor: isDark.value ? '#1f1f1f' : '#ffffff',
        borderColor: isDark.value ? '#404040' : '#e8eaec',
        textStyle: { color: isDark.value ? '#ffffff' : '#333333' }
      },
      legend: {
        bottom: '5%',
        left: 'center',
        textStyle: { color: isDark.value ? '#ffffff' : '#333333' }
      },
      series: [
        {
          name: '用户来源',
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 8,
            borderColor: isDark.value ? '#1f1f1f' : '#ffffff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '18',
              fontWeight: 'bold'
            },
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          },
          labelLine: { show: false },
          data: [
            { value: 1048, name: '搜索引擎', itemStyle: { color: '#409EFF' } },
            { value: 735, name: '直接访问', itemStyle: { color: '#67C23A' } },
            { value: 580, name: '邮件营销', itemStyle: { color: '#E6A23C' } },
            { value: 484, name: '联盟广告', itemStyle: { color: '#F56C6C' } },
            { value: 300, name: '社交媒体', itemStyle: { color: '#909399' } }
          ]
        }
      ],
      animationType: 'scale',
      animationEasing: 'elasticOut',
      animationDelay: function (idx: number) {
        return idx * 200;
      }
    };
  } catch (error) {
    chartsError.pieChart = true;
    ElMessage.error('饼图加载失败');
  } finally {
    chartsLoading.pieChart = false;
  }
};

// 初始化柱状图
const initBarChart = async () => {
  chartsLoading.barChart = true;
  chartsError.barChart = false;

  try {
    await new Promise(resolve => setTimeout(resolve, 600)); // 模拟加载

    const modules = [
      { name: '首页', visits: 1020 },
      { name: '用户管理', visits: 850 },
      { name: '系统设置', visits: 650 },
      { name: '数据分析', visits: 720 },
      { name: '内容管理', visits: 580 },
      { name: '帮助中心', visits: 420 }
    ];

    barChartOption.value = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
        backgroundColor: isDark.value ? '#1f1f1f' : '#ffffff',
        borderColor: isDark.value ? '#404040' : '#e8eaec',
        textStyle: { color: isDark.value ? '#ffffff' : '#333333' }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: modules.map(m => m.name),
        axisLine: { lineStyle: { color: isDark.value ? '#404040' : '#e8eaec' } },
        axisLabel: { color: isDark.value ? '#cccccc' : '#666666' }
      },
      yAxis: {
        type: 'value',
        axisLine: { lineStyle: { color: isDark.value ? '#404040' : '#e8eaec' } },
        axisLabel: { color: isDark.value ? '#cccccc' : '#666666' }
      },
      splitLine: { lineStyle: { color: isDark.value ? '#404040' : '#f0f0f0' } },
      series: [
        {
          name: '访问量',
          type: 'bar',
          barWidth: '60%',
          itemStyle: {
            borderRadius: [4, 4, 0, 0],
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: '#409EFF' },
                { offset: 1, color: '#337ECC' }
              ]
            }
          },
          emphasis: {
            itemStyle: {
              color: {
                type: 'linear',
                x: 0, y: 0, x2: 0, y2: 1,
                colorStops: [
                  { offset: 0, color: '#66B1FF' },
                  { offset: 1, color: '#409EFF' }
                ]
              }
            }
          },
          data: modules.map(m => m.visits),
          animationDelay: function (idx: number) {
            return idx * 100;
          }
        }
      ],
      animationEasing: 'elasticOut',
      animationDelayUpdate: function (idx: number) {
        return idx * 50;
      }
    };
  } catch (error) {
    chartsError.barChart = true;
    ElMessage.error('柱状图加载失败');
  } finally {
    chartsLoading.barChart = false;
  }
};

// 初始化雷达图
const initRadarChart = async () => {
  chartsLoading.radarChart = true;
  chartsError.radarChart = false;

  try {
    await new Promise(resolve => setTimeout(resolve, 800)); // 模拟加载

    radarChartOption.value = {
      tooltip: {
        trigger: 'item',
        backgroundColor: isDark.value ? '#1f1f1f' : '#ffffff',
        borderColor: isDark.value ? '#404040' : '#e8eaec',
        textStyle: { color: isDark.value ? '#ffffff' : '#333333' }
      },
      radar: {
        indicator: [
          { name: '响应速度', max: 100 },
          { name: '稳定性', max: 100 },
          { name: '安全性', max: 100 },
          { name: '用户体验', max: 100 },
          { name: '功能完整性', max: 100 },
          { name: '数据准确性', max: 100 }
        ],
        shape: 'circle',
        splitNumber: 5,
        axisLine: {
          lineStyle: {
            color: isDark.value ? '#404040' : '#e8eaec'
          }
        },
        splitLine: {
          lineStyle: {
            color: isDark.value ? '#404040' : '#f0f0f0'
          }
        },
        splitArea: {
          show: true,
          areaStyle: {
            color: isDark.value ? ['rgba(64, 64, 64, 0.1)', 'rgba(64, 64, 64, 0.05)'] : ['rgba(240, 240, 240, 0.3)', 'rgba(240, 240, 240, 0.1)']
          }
        },
        name: {
          textStyle: {
            color: isDark.value ? '#ffffff' : '#333333'
          }
        }
      },
      series: [
        {
          name: '性能指标',
          type: 'radar',
          data: [
            {
              value: [85, 90, 78, 92, 88, 95],
              name: '当前指标',
              areaStyle: {
                color: 'rgba(64, 158, 255, 0.3)'
              },
              lineStyle: {
                width: 2,
                color: '#409EFF'
              },
              itemStyle: {
                color: '#409EFF'
              }
            }
          ]
        }
      ]
    };
  } catch (error) {
    chartsError.radarChart = true;
    ElMessage.error('雷达图加载失败');
  } finally {
    chartsLoading.radarChart = false;
  }
};

// 初始化散点图
const initScatterChart = async () => {
  chartsLoading.scatterChart = true;
  chartsError.scatterChart = false;

  try {
    await new Promise(resolve => setTimeout(resolve, 700)); // 模拟加载

    // 生成散点数据
    const scatterData = [];
    for (let i = 0; i < 100; i++) {
      scatterData.push([
        Math.random() * 100,
        Math.random() * 100,
        Math.random() * 20 + 5
      ]);
    }

    scatterChartOption.value = {
      tooltip: {
        trigger: 'item',
        backgroundColor: isDark.value ? '#1f1f1f' : '#ffffff',
        borderColor: isDark.value ? '#404040' : '#e8eaec',
        textStyle: { color: isDark.value ? '#ffffff' : '#333333' },
        formatter: function (params: any) {
          return `访问时长: ${params.value[0]}分钟<br>页面深度: ${params.value[1]}<br>访问次数: ${params.value[2]}`;
        }
      },
      xAxis: {
        type: 'value',
        name: '访问时长(分钟)',
        axisLine: { lineStyle: { color: isDark.value ? '#404040' : '#e8eaec' } },
        axisLabel: { color: isDark.value ? '#cccccc' : '#666666' },
        nameTextStyle: { color: isDark.value ? '#ffffff' : '#333333' }
      },
      yAxis: {
        type: 'value',
        name: '页面深度',
        axisLine: { lineStyle: { color: isDark.value ? '#404040' : '#e8eaec' } },
        axisLabel: { color: isDark.value ? '#cccccc' : '#666666' },
        nameTextStyle: { color: isDark.value ? '#ffffff' : '#333333' }
      },
      visualMap: {
        type: 'continuous',
        min: 5,
        max: 25,
        dimension: 2,
        calculable: true,
        orient: 'vertical',
        right: '5%',
        top: 'center',
        textStyle: { color: isDark.value ? '#ffffff' : '#333333' },
        inRange: {
          color: ['#67C23A', '#E6A23C', '#F56C6C']
        }
      },
      series: [
        {
          name: '用户行为',
          type: 'scatter',
          symbolSize: function (data: any) {
            return data[2];
          },
          data: scatterData,
          itemStyle: {
            opacity: 0.7
          }
        }
      ]
    };
  } catch (error) {
    chartsError.scatterChart = true;
    ElMessage.error('散点图加载失败');
  } finally {
    chartsLoading.scatterChart = false;
  }
};

// 初始化热力图
const initHeatmapChart = async () => {
  chartsLoading.heatmapChart = true;
  chartsError.heatmapChart = false;

  try {
    await new Promise(resolve => setTimeout(resolve, 900)); // 模拟加载

    // 生成热力数据
    const hours = ['00:00', '02:00', '04:00', '06:00', '08:00', '10:00', '12:00', '14:00', '16:00', '18:00', '20:00', '22:00'];
    const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
    const heatmapData = [];

    for (let i = 0; i < days.length; i++) {
      for (let j = 0; j < hours.length; j++) {
        // 模拟访问量数据，工作日白天访问量高
        let value = Math.floor(Math.random() * 100);
        if (i < 5) { // 工作日
          if (j >= 4 && j <= 8) { // 工作时间段
            value = Math.floor(Math.random() * 200) + 100;
          }
        } else { // 周末
          if (j >= 8 && j <= 12) { // 上午时间段
            value = Math.floor(Math.random() * 150) + 50;
          }
        }
        heatmapData.push([j, i, value]);
      }
    }

    heatmapChartOption.value = {
      tooltip: {
        position: 'top',
        backgroundColor: isDark.value ? '#1f1f1f' : '#ffffff',
        borderColor: isDark.value ? '#404040' : '#e8eaec',
        textStyle: { color: isDark.value ? '#ffffff' : '#333333' },
        formatter: function (params: any) {
          return `${days[params.value[1]]} ${hours[params.value[0]]}<br>访问量: ${params.value[2]}`;
        }
      },
      grid: {
        height: '80%',
        top: '10%'
      },
      xAxis: {
        type: 'category',
        data: hours,
        splitArea: { show: true },
        axisLine: { lineStyle: { color: isDark.value ? '#404040' : '#e8eaec' } },
        axisLabel: { color: isDark.value ? '#cccccc' : '#666666' }
      },
      yAxis: {
        type: 'category',
        data: days,
        splitArea: { show: true },
        axisLine: { lineStyle: { color: isDark.value ? '#404040' : '#e8eaec' } },
        axisLabel: { color: isDark.value ? '#cccccc' : '#666666' }
      },
      visualMap: {
        min: 0,
        max: 300,
        calculable: true,
        orient: 'vertical',
        left: 'left',
        top: 'center',
        textStyle: { color: isDark.value ? '#ffffff' : '#333333' },
        inRange: {
          color: ['#313695', '#4575b4', '#74add1', '#abd9e9', '#e0f3f8', '#ffffbf', '#fee090', '#fdae61', '#f46d43', '#d73027', '#a50026']
        }
      },
      series: [
        {
          name: '访问量',
          type: 'heatmap',
          data: heatmapData,
          label: { show: false },
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    };
  } catch (error) {
    chartsError.heatmapChart = true;
    ElMessage.error('热力图加载失败');
  } finally {
    chartsLoading.heatmapChart = false;
  }
};

// 事件处理函数
const handlePeriodChange = (period: string) => {
  activePeriod.value = period;
  initLineChart();
};

const handleChartClick = (params: any) => {
  ElNotification({
    title: '图表点击',
    message: `点击了 ${params.seriesName} 的 ${params.name}`,
    type: 'info',
    duration: 2000
  });
};

const handleMetricClick = (metric: any) => {
  ElNotification({
    title: '指标详情',
    message: `查看了 ${metric.title} 的详细数据`,
    type: 'success',
    duration: 2000
  });
};

const handleModuleSort = (command: string) => {
  ElMessage.success(`已切换为${command === 'asc' ? '访问量升序' : command === 'desc' ? '访问量降序' : '按名称排序'}排序`);
};

const markAsRead = (activity: any) => {
  activity.read = true;
};

const viewAllActivities = () => {
  ElMessage.info('跳转到活动页面');
};

const retryChartLoad = (chartType: string) => {
  (chartsError as Record<string, boolean>)[chartType] = false;
  switch (chartType) {
    case 'lineChart':
      initLineChart();
      break;
    case 'pieChart':
      initPieChart();
      break;
    case 'barChart':
      initBarChart();
      break;
    case 'radarChart':
      initRadarChart();
      break;
    case 'scatterChart':
      initScatterChart();
      break;
    case 'heatmapChart':
      initHeatmapChart();
      break;
  }
};

// 实时更新时间
const updateTime = () => {
  const now = new Date();
  currentTime.value = formatDate(now, 'HH:MM:SS');
  currentDate.value = formatDate(now, 'YYYY年mm月dd日');
};

// 定时器
let timeTimer: NodeJS.Timeout | null = null;
let dataUpdateTimer: NodeJS.Timeout | null = null;

// 生命周期钩子
onMounted(async () => {
  // 立即更新时间，确保初始化时显示正确
  updateTime();

  // 初始化图表
  await Promise.all([
    initLineChart(),
    initPieChart(),
    initBarChart(),
    initRadarChart(),
    initScatterChart(),
    initHeatmapChart()
  ]);

  // 启动时间更新定时器
  timeTimer = setInterval(updateTime, 1000);

  // 模拟数据更新
  dataUpdateTimer = setInterval(() => {
    onlineUsers.count = Math.floor(Math.random() * 50) + 100;
    todayVisits.count = Math.floor(Math.random() * 200) + 1000;
  }, 30000);
});

// 组件卸载前清理
onBeforeUnmount(() => {
  if (timeTimer) clearInterval(timeTimer);
  if (dataUpdateTimer) clearInterval(dataUpdateTimer);
});

// 监听主题变化
watch(isDark, () => {
  // 主题切换时重新初始化图表
  nextTick(() => {
    initLineChart();
    initPieChart();
    initBarChart();
    initRadarChart();
    initScatterChart();
    initHeatmapChart();
  });
});
</script>

<style scoped lang="scss">
.home-container {
  padding: 16px;
  background: var(--el-bg-color-page);
  height: calc(100vh - 120px);
  max-width: 100%;
  overflow-x: hidden;

  .mb-20 {
    margin-bottom: 16px;
  }

  .mb-30 {
    margin-bottom: 20px;
  }
}

// 欢迎区域样式
.welcome-section {
  background: linear-gradient(135deg, var(--el-color-primary-light-9) 0%, var(--el-color-primary-light-8) 100%);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.12);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -10%;
    width: 120px;
    height: 120px;
    background: rgba(255, 255, 255, 0.15);
    border-radius: 50%;
    animation: float 6s ease-in-out infinite;
  }

  .welcome-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
    z-index: 1;
    flex-wrap: wrap;
  }

  .welcome-info {
    display: flex;
    align-items: center;
    margin-bottom: 12px;

    .user-avatar {
      position: relative;
      margin-right: 16px;

      .online-indicator {
        position: absolute;
        bottom: 2px;
        right: 2px;
        width: 12px;
        height: 12px;
        background: var(--el-color-success);
        border: 2px solid var(--el-color-white);
        border-radius: 50%;
        animation: pulse 2s infinite;
      }
    }

    .welcome-text {
      .greeting {
        font-size: 24px;
        font-weight: 600;
        color: var(--el-text-color-primary);
        margin: 0 0 6px 0;
        background: linear-gradient(45deg, var(--el-color-primary), var(--el-color-info));
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }

      .welcome-subtitle {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 14px;
        color: var(--el-text-color-secondary);
        margin: 0 0 6px 0;

        .time-display {
          padding: 2px 8px;
          background: rgba(255, 255, 255, 0.25);
          border-radius: 8px;
          font-family: 'SF Mono', monospace;
          font-weight: 600;
          font-size: 13px;
        }
      }

      .weather-info {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 13px;
        color: var(--el-text-color-regular);

        .el-icon {
          color: var(--el-color-warning);
        }
      }
    }
  }

  .quick-stats {
    display: flex;
    gap: 16px;

    .stat-card-mini {
      display: flex;
      align-items: center;
      padding: 16px 20px;
      background: rgba(255, 255, 255, 0.95);
      border-radius: 12px;
      border: 1px solid rgba(255, 255, 255, 0.3);
      box-shadow:
        0 2px 8px rgba(0, 0, 0, 0.06),
        0 0 0 1px rgba(255, 255, 255, 0.1) inset;
      cursor: pointer;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      position: relative;
      overflow: hidden;
      backdrop-filter: blur(10px);

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(135deg,
            rgba(255, 255, 255, 0.1) 0%,
            rgba(255, 255, 255, 0.05) 100%);
        opacity: 0;
        transition: opacity 0.3s ease;
      }

      &:hover {
        transform: translateY(-3px);
        box-shadow:
          0 8px 25px rgba(0, 0, 0, 0.12),
          0 0 0 1px rgba(255, 255, 255, 0.2) inset;

        &::before {
          opacity: 1;
        }

        .stat-icon {
          transform: scale(1.1);
        }
      }

      .stat-icon {
        width: 36px;
        height: 36px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;
        transition: all 0.3s ease;
        position: relative;
        z-index: 1;

        &.users {
          background: linear-gradient(135deg, #67C23A, #529B2E);

          .el-icon {
            color: var(--el-color-white);
            font-size: 16px;
            filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.2));
          }
        }

        &.visits {
          background: linear-gradient(135deg, #409EFF, #337ECC);

          .el-icon {
            color: var(--el-color-white);
            font-size: 16px;
            filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.2));
          }
        }

        &.today {
          background: linear-gradient(135deg, #E6A23C, #CF9236);

          .el-icon {
            color: var(--el-color-white);
            font-size: 16px;
            filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.2));
          }
        }
      }

      .stat-info {
        position: relative;
        z-index: 1;

        .stat-number {
          font-size: 18px;
          font-weight: 700;
          color: var(--el-text-color-primary);
          line-height: 1.2;
          margin-bottom: 2px;
          letter-spacing: -0.02em;
        }

        .stat-label {
          font-size: 12px;
          color: var(--el-text-color-regular);
          font-weight: 500;
          letter-spacing: 0.01em;
        }
      }
    }
  }
}

// 关键指标卡片
.metric-card {
  border-radius: 16px;
  padding: 24px 20px;
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.3);
  cursor: pointer;
  backdrop-filter: blur(16px);
  background: rgba(255, 255, 255, 0.95);

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg,
        rgba(255, 255, 255, 0.15) 0%,
        rgba(255, 255, 255, 0.05) 100%);
    z-index: 0;
    opacity: 0;
    transition: opacity 0.3s ease;
  }

  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg,
        rgba(255, 255, 255, 0.08) 0%,
        rgba(255, 255, 255, 0.02) 100%);
    z-index: 0;
    opacity: 1;
    transition: opacity 0.3s ease;
  }

  &:hover {
    transform: translateY(-6px);
    box-shadow:
      0 20px 40px rgba(0, 0, 0, 0.15),
      0 0 0 1px rgba(255, 255, 255, 0.4) inset;
    border-color: rgba(255, 255, 255, 0.5);

    &::before {
      opacity: 1;
    }

    .metric-icon {
      transform: scale(1.05);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
    }

    .metric-growth {
      transform: translateY(-2px);
    }
  }

  &.primary {
    background: linear-gradient(135deg,
        rgba(64, 158, 255, 0.08) 0%,
        rgba(51, 126, 204, 0.03) 100%);
    border-color: rgba(64, 158, 255, 0.2);
    box-shadow:
      0 4px 16px rgba(64, 158, 255, 0.1),
      inset 0 1px 0 rgba(255, 255, 255, 0.2);

    .metric-icon {
      background: linear-gradient(135deg, #409EFF, #337ECC);
    }
  }

  &.success {
    background: linear-gradient(135deg,
        rgba(103, 194, 58, 0.08) 0%,
        rgba(82, 155, 46, 0.03) 100%);
    border-color: rgba(103, 194, 58, 0.2);
    box-shadow:
      0 4px 16px rgba(103, 194, 58, 0.1),
      inset 0 1px 0 rgba(255, 255, 255, 0.2);

    .metric-icon {
      background: linear-gradient(135deg, #67C23A, #529B2E);
    }
  }

  &.warning {
    background: linear-gradient(135deg,
        rgba(230, 162, 60, 0.08) 0%,
        rgba(207, 146, 54, 0.03) 100%);
    border-color: rgba(230, 162, 60, 0.2);
    box-shadow:
      0 4px 16px rgba(230, 162, 60, 0.1),
      inset 0 1px 0 rgba(255, 255, 255, 0.2);

    .metric-icon {
      background: linear-gradient(135deg, #E6A23C, #CF9236);
    }
  }

  &.info {
    background: linear-gradient(135deg,
        rgba(144, 147, 153, 0.08) 0%,
        rgba(120, 123, 129, 0.03) 100%);
    border-color: rgba(144, 147, 153, 0.2);
    box-shadow:
      0 4px 16px rgba(144, 147, 153, 0.1),
      inset 0 1px 0 rgba(255, 255, 255, 0.2);

    .metric-icon {
      background: linear-gradient(135deg, #909399, #787B7F);
    }
  }

  .metric-header {
    position: relative;
    z-index: 2;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16px;

    .metric-icon {
      width: 40px;
      height: 40px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      backdrop-filter: blur(10px);
      box-shadow:
        0 4px 12px rgba(0, 0, 0, 0.1),
        inset 0 1px 0 rgba(255, 255, 255, 0.2);

      .el-icon {
        font-size: 18px;
        color: var(--el-color-white);
        filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.2));
      }
    }

    .metric-trend {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 14px;
      font-weight: 600;
      padding: 4px 8px;
      border-radius: 20px;
      background: rgba(255, 255, 255, 0.9);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.5);

      &.up {
        color: #67C23A;

        .el-icon {
          color: #67C23A;
        }
      }

      &.down {
        color: #F56C6C;

        .el-icon {
          color: #F56C6C;
        }
      }

      .el-icon {
        font-size: 10px;
      }
    }
  }

  .metric-content {
    position: relative;
    z-index: 2;
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    gap: 16px;
  }

  .metric-main {
    flex: 1;

    .metric-title {
      font-size: 14px;
      font-weight: 600;
      color: var(--el-text-color-secondary);
      margin: 0 0 6px 0;
      letter-spacing: 0.01em;
    }

    .metric-subtitle {
      font-size: 11px;
      color: var(--el-text-color-placeholder);
      margin: 0 0 8px 0;
      font-weight: 500;
    }

    .metric-value {
      font-size: 26px;
      font-weight: 700;
      color: var(--el-text-color-primary);
      margin: 0;
      line-height: 1;
      letter-spacing: -0.02em;
    }
  }

  .metric-side {
    text-align: right;

    .metric-growth {
      display: flex;
      align-items: center;
      justify-content: flex-end;
      gap: 4px;
      font-size: 12px;
      font-weight: 700;
      margin-bottom: 6px;
      padding: 6px 10px;
      border-radius: 20px;
      background: rgba(255, 255, 255, 0.9);
      backdrop-filter: blur(10px);
      transition: all 0.3s ease;
      border: 1px solid rgba(255, 255, 255, 0.5);

      &.up {
        color: #67C23A;

        .el-icon {
          color: #67C23A;
        }
      }

      &.down {
        color: #F56C6C;

        .el-icon {
          color: #F56C6C;
        }
      }

      .el-icon {
        font-size: 12px;
      }
    }

    .metric-description {
      font-size: 11px;
      color: var(--el-text-color-placeholder);
      margin: 0;
      font-weight: 500;
    }
  }

  .metric-chart {
    position: absolute;
    bottom: 0;
    right: 0;
    width: 60px;
    height: 60px;
    opacity: 0.08;
    z-index: 0;
    border-radius: 0 16px 0 60px;
    overflow: hidden;
  }
}

// 图表容器
.chart-container {
  background: var(--el-color-white);
  border-radius: 12px;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.06);
  padding: 16px;
  height: 100%;

  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16px;

    .chart-title {
      h3 {
        font-size: 16px;
        font-weight: 600;
        color: var(--el-text-color-primary);
        margin: 0 0 3px 0;
      }

      .chart-subtitle {
        font-size: 13px;
        color: var(--el-text-color-secondary);
        margin: 0;
      }
    }

    .chart-controls {
      .period-selector {
        .el-button {
          border-radius: 6px;
        }
      }
    }
  }

  .chart-content {
    position: relative;
    min-height: 240px;

    .chart {
      width: 100%;
      height: 400px;
    }

    .chart-loading {
      padding: 16px;
    }

    .chart-error {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 160px;
    }
  }
}

// 新增图表卡片样式
.additional-charts-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;

  .chart-card {
    background: var(--el-color-white);
    border-radius: 12px;
    box-shadow: 0 1px 8px rgba(0, 0, 0, 0.06);
    padding: 16px;
    height: 100%;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    border: 1px solid rgba(255, 255, 255, 0.3);
    backdrop-filter: blur(10px);

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(135deg,
          rgba(255, 255, 255, 0.1) 0%,
          rgba(255, 255, 255, 0.05) 100%);
      opacity: 0;
      transition: opacity 0.3s ease;
      border-radius: 12px;
    }

    &:hover {
      transform: translateY(-2px);
      box-shadow:
        0 4px 16px rgba(0, 0, 0, 0.1),
        0 0 0 1px rgba(255, 255, 255, 0.4) inset;

      &::before {
        opacity: 1;
      }
    }

    .chart-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 16px;

      .chart-title {
        h4 {
          font-size: 14px;
          font-weight: 600;
          color: var(--el-text-color-primary);
          margin: 0 0 3px 0;
        }

        .chart-subtitle {
          font-size: 12px;
          color: var(--el-text-color-secondary);
          margin: 0;
        }
      }

      .chart-controls {
        .el-button {
          border-radius: 6px;
        }
      }
    }

    .chart-content {
      position: relative;
      min-height: 200px;

      .chart {
        width: 100%;
        height: 200px;
      }

      .chart-loading {
        padding: 16px;
      }

      .chart-error {
        display: flex;
        align-items: center;
        justify-content: center;
        min-height: 120px;
      }
    }
  }
}

// 活动面板
.activities-panel {
  background: var(--el-color-white);
  border-radius: 12px;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.06);
  padding: 16px;
  height: 100%;
  display: flex;
  flex-direction: column;

  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    h3 {
      font-size: 16px;
      font-weight: 600;
      color: var(--el-text-color-primary);
      margin: 0;
    }

    .activity-badge {
      :deep(.el-badge__content) {
        background-color: var(--el-color-danger);
      }
    }
  }

  .activities-list {
    flex: 1;
    overflow-y: auto;
    max-height: 500px; // 限制最大高度

    // 美化活动列表的滚动条
    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-track {
      background: var(--el-fill-color-lighter);
      border-radius: 3px;
    }

    &::-webkit-scrollbar-thumb {
      background: var(--el-text-color-placeholder);
      border-radius: 3px;

      &:hover {
        background: var(--el-text-color-secondary);
      }
    }

    .activity-item {
      display: flex;
      align-items: flex-start;
      padding: 16px;
      border-radius: 12px;
      margin-bottom: 8px;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      position: relative;
      cursor: pointer;
      background: rgba(255, 255, 255, 0.8);
      border: 1px solid rgba(255, 255, 255, 0.4);
      backdrop-filter: blur(8px);
      overflow: hidden;

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(135deg,
            rgba(255, 255, 255, 0.1) 0%,
            rgba(255, 255, 255, 0.05) 100%);
        opacity: 0;
        transition: opacity 0.3s ease;
      }

      &:hover {
        background: rgba(255, 255, 255, 0.9);
        transform: translateX(6px);
        box-shadow:
          0 4px 16px rgba(0, 0, 0, 0.08),
          inset 0 1px 0 rgba(255, 255, 255, 0.3);
        border-color: rgba(255, 255, 255, 0.6);

        &::before {
          opacity: 1;
        }

        .activity-icon {
          transform: scale(1.1) rotate(5deg);
        }
      }

      &.read {
        opacity: 0.7;
        background: rgba(255, 255, 255, 0.5);
        border-color: rgba(255, 255, 255, 0.3);

        .activity-title {
          color: var(--el-text-color-regular);
        }

        .activity-indicator {
          opacity: 0.3;
          animation: none;
        }
      }

      .activity-icon {
        width: 32px;
        height: 32px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;
        flex-shrink: 0;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        position: relative;
        z-index: 1;
        box-shadow:
          0 2px 8px rgba(0, 0, 0, 0.1),
          inset 0 1px 0 rgba(255, 255, 255, 0.2);

        .el-icon {
          font-size: 14px;
          color: var(--el-color-white);
          filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.2));
        }

        &.success {
          background: linear-gradient(135deg, #67C23A, #529B2E);
        }

        &.warning {
          background: linear-gradient(135deg, #E6A23C, #CF9236);
        }

        &.info {
          background: linear-gradient(135deg, #909399, #787B7F);
        }

        &.primary {
          background: linear-gradient(135deg, #409EFF, #337ECC);
        }
      }

      .activity-content {
        flex: 1;
        position: relative;
        z-index: 1;

        .activity-title {
          font-size: 14px;
          color: var(--el-text-color-primary);
          margin-bottom: 4px;
          line-height: 1.5;
          font-weight: 500;
          letter-spacing: 0.01em;
        }

        .activity-description {
          font-size: 12px;
          color: var(--el-text-color-regular);
          margin-bottom: 6px;
          line-height: 1.4;
        }

        .activity-time {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: 11px;
          color: var(--el-text-color-secondary);
          font-weight: 500;

          .el-icon {
            font-size: 11px;
          }
        }
      }

      .activity-indicator {
        width: 8px;
        height: 8px;
        background: linear-gradient(135deg, #409EFF, #337ECC);
        border-radius: 50%;
        margin-left: 8px;
        margin-top: 6px;
        animation: pulse 2s infinite;
        box-shadow:
          0 0 8px rgba(64, 158, 255, 0.4),
          inset 0 1px 0 rgba(255, 255, 255, 0.3);
      }
    }
  }

  .panel-footer {
    padding-top: 16px;
    border-top: 1px solid var(--el-border-color-lighter);
    text-align: center;
  }
}

// 动画
@keyframes float {

  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }

  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 1;
  }

  50% {
    transform: scale(1.1);
    opacity: 0.7;
  }

  100% {
    transform: scale(1);
    opacity: 1;
  }
}

// 滚动处理 - 重要：确保页面可以正常滚动
.home-container {
  overflow-y: auto;
  scroll-behavior: smooth;

  // 滚动条样式
  &::-webkit-scrollbar {
    width: 8px;
    height: 8px;
  }

  &::-webkit-scrollbar-track {
    background: var(--el-fill-color-lighter);
    border-radius: 4px;
  }

  &::-webkit-scrollbar-thumb {
    background: var(--el-text-color-placeholder);
    border-radius: 4px;

    &:hover {
      background: var(--el-text-color-secondary);
    }
  }
}

// 响应式设计优化
@media (max-width: 1400px) {
  .metric-card {
    .metric-content {
      gap: 12px;

      .metric-icon {
        width: 40px;
        height: 40px;

        .el-icon {
          font-size: 18px;
        }
      }

      .metric-number {
        font-size: 26px;
      }
    }
  }
}

@media (max-width: 1200px) {
  .welcome-section {
    .welcome-content {
      flex-direction: column;
      align-items: flex-start;

      .quick-stats {
        width: 100%;
        justify-content: space-around;
        margin-top: 20px;
        flex-wrap: wrap;

        .stat-card-mini {
          flex: 1;
          min-width: 180px;
        }
      }
    }
  }

  .metric-grid {
    gap: 16px;

    .metric-card {
      .metric-content {
        .metric-number {
          font-size: 24px;
        }

        .metric-label {
          font-size: 13px;
        }
      }
    }
  }

  // 新增图表卡片响应式适配
  .additional-charts-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;

    .chart-card {
      .chart-content .chart {
        height: 180px;
      }
    }
  }
}

@media (max-width: 992px) {
  .quick-stats {
    gap: 12px;

    .stat-card-mini {
      padding: 14px 18px;

      .stat-icon {
        width: 32px;
        height: 32px;

        .el-icon {
          font-size: 14px;
        }
      }

      .stat-info {
        .stat-number {
          font-size: 16px;
        }

        .stat-label {
          font-size: 11px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .home-container {
    padding: 15px;
  }

  .welcome-section {
    padding: 20px;
    border-radius: 12px;

    .welcome-info {
      flex-direction: column;
      text-align: center;

      .user-avatar {
        margin-right: 0;
        margin-bottom: 16px;
      }
    }

    .quick-stats {
      flex-direction: column;
      gap: 12px;

      .stat-card-mini {
        width: 100%;
        justify-content: center;
        padding: 16px 20px;
      }
    }
  }

  .chart-container {
    padding: 16px;
    border-radius: 12px;

    .chart-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;
    }

    .chart-content .chart {
      height: 250px;
    }
  }

  .activities-panel {
    padding: 16px;
    border-radius: 12px;

    .activity-item {
      padding: 14px;
      border-radius: 10px;

      .activity-icon {
        width: 28px;
        height: 28px;
        border-radius: 8px;
      }

      .activity-content {
        .activity-title {
          font-size: 13px;
        }

        .activity-description {
          font-size: 11px;
        }

        .activity-time {
          font-size: 10px;
        }
      }
    }
  }

  .metric-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;

    .metric-card {
      padding: 16px;
      border-radius: 12px;

      .metric-content {
        .metric-icon {
          width: 36px;
          height: 36px;
          border-radius: 10px;

          .el-icon {
            font-size: 16px;
          }
        }

        .metric-number {
          font-size: 22px;
        }

        .metric-label {
          font-size: 12px;
        }
      }
    }
  }

  // 新增图表卡片响应式适配
  .additional-charts-grid {
    grid-template-columns: 1fr;
    gap: 12px;

    .chart-card {
      padding: 14px;
      border-radius: 10px;

      .chart-header {
        margin-bottom: 12px;

        .chart-title h4 {
          font-size: 13px;
        }

        .chart-subtitle {
          font-size: 11px;
        }
      }

      .chart-content .chart {
        height: 160px;
      }
    }
  }
}

@media (max-width: 576px) {
  .home-container {
    padding: 12px;
  }

  .welcome-section {
    padding: 16px;
    border-radius: 10px;

    .quick-stats {
      gap: 10px;

      .stat-card-mini {
        padding: 12px 16px;

        .stat-icon {
          width: 28px;
          height: 28px;
          border-radius: 8px;

          .el-icon {
            font-size: 12px;
          }
        }

        .stat-info {
          .stat-number {
            font-size: 14px;
          }

          .stat-label {
            font-size: 10px;
          }
        }
      }
    }
  }

  .metric-grid {
    grid-template-columns: 1fr;
    gap: 10px;

    .metric-card {
      padding: 14px;

      .metric-content {
        .metric-number {
          font-size: 20px;
        }
      }
    }
  }

  .chart-container {
    padding: 12px;

    .chart-header {
      h3 {
        font-size: 14px;
      }
    }

    .chart-content .chart {
      height: 220px;
    }
  }

  .activities-panel {
    padding: 12px;
  }

  // 新增图表卡片响应式适配
  .additional-charts-grid {
    gap: 10px;

    .chart-card {
      padding: 12px;
      border-radius: 8px;

      .chart-header {
        margin-bottom: 10px;

        .chart-title h4 {
          font-size: 12px;
        }

        .chart-subtitle {
          font-size: 10px;
        }
      }

      .chart-content {
        min-height: 140px;

        .chart {
          height: 140px;
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .quick-stats {
    .stat-card-mini {
      .stat-info {
        .stat-number {
          font-size: 13px;
        }

        .stat-label {
          font-size: 9px;
        }
      }
    }
  }

  .metric-card {
    padding: 12px;

    .metric-header {
      margin-bottom: 10px;
    }

    .metric-content .metric-value {
      font-size: 20px;
    }
  }
}
</style>