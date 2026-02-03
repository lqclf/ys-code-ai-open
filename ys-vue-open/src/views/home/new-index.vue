<template>
  <div class="home-container">
    <!-- 顶部欢迎区域 -->
    <el-row :gutter="15" class="mb15">
      <el-col :span="24">
        <div class="welcome-card">
          <div class="welcome-content">
            <div class="avatar-container">
              <el-avatar :size="80" :src="userAvatar" />
            </div>
            <div class="welcome-text">
              <h2>{{ greeting }}, {{ userName }}!</h2>
              <p class="welcome-subtitle">{{ currentDate }} · {{ currentTime }}</p>
              <p class="weather-info" v-if="weather">
                <i class="el-icon-sunny"></i> {{ weather.temperature }}°C {{ weather.description }}
              </p>
            </div>
          </div>
          <div class="quick-stats">
            <div class="stat-item online-users">
              <div class="stat-value">{{ onlineUsers }}</div>
              <div class="stat-label">在线用户</div>
            </div>
            <div class="stat-item total-visits">
              <div class="stat-value">{{ totalVisits }}</div>
              <div class="stat-label">总访问量</div>
            </div>
            <div class="stat-item today-visits">
              <div class="stat-value">{{ todayVisits }}</div>
              <div class="stat-label">今日访问</div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 统计卡片区域 -->
    <el-row :gutter="15" class="mb15">
      <el-col :xs="24" :sm="12" :md="6" v-for="(item, index) in statCards" :key="index">
        <div class="stat-card" :class="item.color">
          <div class="stat-card-icon">
            <i :class="item.icon"></i>
          </div>
          <div class="stat-card-content">
            <div class="stat-card-title">{{ item.title }}</div>
            <div class="stat-card-value">{{ item.value }}</div>
            <div class="stat-card-trend" :class="{ 'positive': item.trend > 0, 'negative': item.trend < 0 }">
              <i :class="item.trend > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
              {{ Math.abs(item.trend) }}% 
              <span v-if="item.trend > 0">增长</span>
              <span v-else-if="item.trend < 0">下降</span>
              <span v-else>持平</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="15" class="mb15">
      <!-- 折线图 -->
      <el-col :xs="24" :sm="24" :md="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>访问趋势</h3>
            <el-dropdown @command="handleChartPeriodChange">
              <span class="el-dropdown-link">
                {{ chartPeriodLabel }}<i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="week">近一周</el-dropdown-item>
                  <el-dropdown-item command="month">近一月</el-dropdown-item>
                  <el-dropdown-item command="quarter">近一季度</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <v-chart class="chart" :option="lineChartOption" autoresize />
        </div>
      </el-col>

      <!-- 饼图 -->
      <el-col :xs="24" :sm="24" :md="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>用户来源分布</h3>
          </div>
          <v-chart class="chart" :option="pieChartOption" autoresize />
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="15" class="mb15">
      <!-- 柱状图 -->
      <el-col :xs="24" :sm="24" :md="16">
        <div class="chart-card">
          <div class="chart-header">
            <h3>各模块访问量</h3>
          </div>
          <v-chart class="chart" :option="barChartOption" autoresize />
        </div>
      </el-col>

      <!-- 最新活动 -->
      <el-col :xs="24" :sm="24" :md="8">
        <div class="activities-card">
          <div class="card-header">
            <h3>最新动态</h3>
          </div>
          <div class="activities-list">
            <div class="activity-item" v-for="(activity, index) in activities" :key="index">
              <div class="activity-icon" :class="activity.type">
                <i :class="activity.icon"></i>
              </div>
              <div class="activity-content">
                <div class="activity-title">{{ activity.title }}</div>
                <div class="activity-time">{{ activity.time }}</div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue';
import { useThemeConfig } from '@/stores/themeConfig';
import { formatDate } from '@/utils/formatTime';
import * as echarts from 'echarts';

// 存储
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storesThemeConfig;

// 定义变量内容
const userAvatar = ref('https://img2.baidu.com/it/u=1978192862,2048448374&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500');
const userName = ref('管理员');
const onlineUsers = ref(128);
const totalVisits = ref(12845);
const todayVisits = ref(124);
const weather = ref({
  temperature: 22,
  description: '晴'
});

// 时间相关
const currentTime = ref(formatDate(new Date(), 'HH:mm:ss'));
const currentDate = ref(formatDate(new Date(), 'YYYY年MM月DD日 dddd'));
let timer: NodeJS.Timeout | null = null;

// 统计卡片数据
const statCards = ref([
  {
    title: '总销售额',
    value: '¥128,000',
    icon: 'el-icon-shopping-cart-full',
    color: 'blue',
    trend: 12.5
  },
  {
    title: '订单数量',
    value: '1,280',
    icon: 'el-icon-document',
    color: 'green',
    trend: -3.2
  },
  {
    title: '用户数',
    value: '8,654',
    icon: 'el-icon-user',
    color: 'orange',
    trend: 8.7
  },
  {
    title: '转化率',
    value: '24.5%',
    icon: 'el-icon-data-analysis',
    color: 'purple',
    trend: 1.2
  }
]);

// 图表周期
const chartPeriod = ref('week');
const chartPeriodLabel = computed(() => {
  const labels: Record<string, string> = {
    week: '近一周',
    month: '近一月',
    quarter: '近一季度'
  };
  return labels[chartPeriod.value] || '近一周';
});

// 活动数据
const activities = ref([
  {
    type: 'success',
    icon: 'el-icon-check',
    title: '系统升级完成',
    time: '5分钟前'
  },
  {
    type: 'warning',
    icon: 'el-icon-warning-outline',
    title: '服务器维护通知',
    time: '1小时前'
  },
  {
    type: 'info',
    icon: 'el-icon-info',
    title: '新功能上线',
    time: '3小时前'
  },
  {
    type: 'primary',
    icon: 'el-icon-star-off',
    title: '用户反馈收集',
    time: '1天前'
  }
]);

// 图表配置
const lineChartOption = ref({});
const pieChartOption = ref({});
const barChartOption = ref({});

// 根据时间段获取问候语
const greeting = computed(() => {
  const hour = new Date().getHours();
  if (hour < 6) return '凌晨好';
  if (hour < 9) return '早上好';
  if (hour < 12) return '上午好';
  if (hour < 14) return '中午好';
  if (hour < 17) return '下午好';
  if (hour < 19) return '傍晚好';
  if (hour < 22) return '晚上好';
  return '夜深了';
});

// 初始化折线图
const initLineChart = () => {
  lineChartOption.value = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['访问量', '用户数']
    },
    xAxis: {
      type: 'category',
      data: chartPeriod.value === 'week' 
        ? ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] 
        : chartPeriod.value === 'month' 
          ? ['1周', '2周', '3周', '4周'] 
          : ['1月', '2月', '3月']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '访问量',
        type: 'line',
        smooth: true,
        data: chartPeriod.value === 'week' 
          ? [120, 132, 101, 134, 90, 230, 210] 
          : chartPeriod.value === 'month' 
            ? [420, 532, 601, 734] 
            : [1420, 1532, 1601]
      },
      {
        name: '用户数',
        type: 'line',
        smooth: true,
        data: chartPeriod.value === 'week' 
          ? [82, 93, 90, 120, 80, 150, 130] 
          : chartPeriod.value === 'month' 
            ? [382, 493, 490, 520] 
            : [1382, 1493, 1490]
      }
    ]
  };
};

// 初始化饼图
const initPieChart = () => {
  pieChartOption.value = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      top: '5%',
      left: 'center'
    },
    series: [
      {
        name: '用户来源',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
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
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 1048, name: '搜索引擎' },
          { value: 735, name: '直接访问' },
          { value: 580, name: '邮件营销' },
          { value: 484, name: '联盟广告' },
          { value: 300, name: '视频广告' }
        ]
      }
    ]
  };
};

// 初始化柱状图
const initBarChart = () => {
  barChartOption.value = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['访问量']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        data: ['首页', '用户管理', '系统设置', '数据分析', '帮助中心', '关于我们'],
        axisTick: {
          alignWithLabel: true
        }
      }
    ],
    yAxis: [
      {
        type: 'value'
      }
    ],
    series: [
      {
        name: '访问量',
        type: 'bar',
        barWidth: '60%',
        data: [10, 52, 200, 334, 390, 330, 220]
      }
    ]
  };
};

// 处理图表周期变化
const handleChartPeriodChange = (period: string) => {
  chartPeriod.value = period;
  initLineChart();
};

// 更新时间
const updateTime = () => {
  currentTime.value = formatDate(new Date(), 'HH:mm:ss');
};

// 页面加载时
onMounted(() => {
  initLineChart();
  initPieChart();
  initBarChart();
  
  // 启动定时器更新时间
  timer = setInterval(updateTime, 1000);
  
  // 模拟实时更新在线用户数
  setInterval(() => {
    onlineUsers.value = Math.floor(Math.random() * 100) + 50;
    todayVisits.value = Math.floor(Math.random() * 200) + 80;
  }, 30000);
});

// 组件卸载前清除定时器
onBeforeUnmount(() => {
  if (timer) {
    clearInterval(timer);
  }
});
</script>

<style scoped lang="scss">

.home-container {
  overflow: auto;
  height: calc(100vh - 85px);
  padding: 15px;
  background-color: var(--el-bg-color-page);
  
  .mb15 {
    margin-bottom: 15px;
  }
  
  // 欢迎卡片
  .welcome-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30px;
    border-radius: 8px;
    background: linear-gradient(120deg, var(--el-color-primary-light-9) 0%, var(--el-color-primary-light-7) 100%);
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    color: var(--el-text-color-primary);
    
    flex-wrap: wrap;
    
    .welcome-content {
      display: flex;
      align-items: center;
      
      .avatar-container {
        margin-right: 20px;
        
        :deep(.el-avatar) {
          border: 3px solid rgba(255, 255, 255, 0.3);
          box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
      }
      
      .welcome-text {
        h2 {
          font-size: 24px;
          font-weight: 600;
          margin-bottom: 10px;
        }
        
        .welcome-subtitle {
          font-size: 14px;
          color: var(--el-text-color-secondary);
          margin-bottom: 5px;
        }
        
        .weather-info {
          font-size: 14px;
          color: var(--el-text-color-regular);
          
          i {
            margin-right: 5px;
            color: var(--el-color-warning);
          }
        }
      }
    }
    
    .quick-stats {
      display: flex;
      gap: 30px;
      
      .stat-item {
        text-align: center;
        
        .stat-value {
          font-size: 28px;
          font-weight: 600;
          margin-bottom: 5px;
        }
        
        .stat-label {
          font-size: 14px;
          color: var(--el-text-color-secondary);
        }
      }
      
      .online-users .stat-value {
        color: var(--el-color-success);
      }
      
      .total-visits .stat-value {
        color: var(--el-color-primary);
      }
      
      .today-visits .stat-value {
        color: var(--el-color-warning);
      }
    }
  }
  
  // 统计卡片
  .stat-card {
    display: flex;
    padding: 20px;
    border-radius: 8px;
    background: var(--el-color-white);
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
    cursor: pointer;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 6px 16px 0 rgba(0, 0, 0, 0.15);
    }
    
    &.blue {
      border-left: 4px solid var(--el-color-primary);
    }
    
    &.green {
      border-left: 4px solid var(--el-color-success);
    }
    
    &.orange {
      border-left: 4px solid var(--el-color-warning);
    }
    
    &.purple {
      border-left: 4px solid var(--el-color-purple);
    }
    
    .stat-card-icon {
      width: 60px;
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 8px;
      margin-right: 15px;
      flex-shrink: 0;
      
      i {
        font-size: 28px;
        color: var(--el-color-white);
      }
    }
    
    .blue .stat-card-icon {
      background: linear-gradient(135deg, var(--el-color-primary-light-3), var(--el-color-primary));
    }
    
    .green .stat-card-icon {
      background: linear-gradient(135deg, var(--el-color-success-light-3), var(--el-color-success));
    }
    
    .orange .stat-card-icon {
      background: linear-gradient(135deg, var(--el-color-warning-light-3), var(--el-color-warning));
    }
    
    .purple .stat-card-icon {
      background: linear-gradient(135deg, var(--el-color-purple-light-3), var(--el-color-purple));
    }
    
    .stat-card-content {
      flex: 1;
      
      .stat-card-title {
        font-size: 14px;
        color: var(--el-text-color-secondary);
        margin-bottom: 5px;
      }
      
      .stat-card-value {
        font-size: 24px;
        font-weight: 600;
        margin-bottom: 5px;
        color: var(--el-text-color-primary);
      }
      
      .stat-card-trend {
        font-size: 12px;
        display: flex;
        align-items: center;
        
        i {
          margin-right: 3px;
        }
        
        &.positive {
          color: var(--el-color-success);
        }
        
        &.negative {
          color: var(--el-color-danger);
        }
      }
    }
  }
  
  // 图表卡片
  .chart-card {
    padding: 20px;
    border-radius: 8px;
    background: var(--el-color-white);
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    
    .chart-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      
      h3 {
        margin: 0;
        font-size: 18px;
        font-weight: 600;
      }
      
      .el-dropdown-link {
        cursor: pointer;
        color: var(--el-color-primary);
      }
    }
    
    .chart {
      width: 100%;
      height: 300px;
    }
  }
  
  // 活动卡片
  .activities-card {
    padding: 20px;
    border-radius: 8px;
    background: var(--el-color-white);
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    height: 100%;
    
    .card-header {
      margin-bottom: 20px;
      
      h3 {
        margin: 0;
        font-size: 18px;
        font-weight: 600;
      }
    }
    
    .activities-list {
      .activity-item {
        display: flex;
        padding: 15px 0;
        border-bottom: 1px solid var(--el-border-color-lighter);
        
        &:last-child {
          border-bottom: none;
        }
        
        .activity-icon {
          width: 32px;
          height: 32px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 15px;
          flex-shrink: 0;
          
          i {
            font-size: 16px;
            color: var(--el-color-white);
          }
          
          &.success {
            background: var(--el-color-success);
          }
          
          &.warning {
            background: var(--el-color-warning);
          }
          
          &.info {
            background: var(--el-color-info);
          }
          
          &.primary {
            background: var(--el-color-primary);
          }
        }
        
        .activity-content {
          flex: 1;
          
          .activity-title {
            font-size: 14px;
            margin-bottom: 5px;
            color: var(--el-text-color-primary);
          }
          
          .activity-time {
            font-size: 12px;
            color: var(--el-text-color-secondary);
          }
        }
      }
    }
  }
  
  // 响应式设计
  @media (max-width: 768px) {
    padding: 10px;
    
    .welcome-card {
      flex-direction: column;
      text-align: center;
      
      .welcome-content {
        flex-direction: column;
        margin-bottom: 20px;
        
        .avatar-container {
          margin-right: 0;
          margin-bottom: 15px;
        }
      }
      
      .quick-stats {
        width: 100%;
        justify-content: space-around;
        
        .stat-item {
          .stat-value {
            font-size: 20px;
          }
        }
      }
    }
    
    .stat-card {
      flex-direction: column;
      text-align: center;
      
      .stat-card-icon {
        margin-right: 0;
        margin-bottom: 15px;
      }
    }
    
    .chart-card {
      .chart {
        height: 250px;
      }
    }
  }
}
</style>