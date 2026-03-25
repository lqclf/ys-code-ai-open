import { computed, ref, onMounted, onUnmounted } from 'vue';

/**
 * 图表主题配色 - 科技风格
 */
export const chartTheme = {
  color: ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#667eea', '#909399'],
  backgroundColor: 'transparent',
  textStyle: {
    color: '#606266',
    fontSize: 12,
  },
  title: {
    textStyle: {
      color: '#303133',
      fontSize: 16,
      fontWeight: 600,
    },
    subtextStyle: {
      color: '#909399',
      fontSize: 12,
    },
  },
  line: {
    smooth: true,
    symbol: 'circle',
    symbolSize: 6,
    lineStyle: {
      width: 2,
    },
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true,
  },
  tooltip: {
    backgroundColor: 'rgba(255, 255, 255, 0.95)',
    borderColor: '#e4e7ed',
    borderWidth: 1,
    textStyle: {
      color: '#303133',
    },
  },
  legend: {
    textStyle: {
      color: '#606266',
    },
  },
};

/**
 * 图表配置 Hook
 * 提供各种图表的配置选项
 */
export function useCharts() {
  const chartRefs = ref<Record<string, any>>({});

  const baseOption = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e4e7ed',
      borderWidth: 1,
      textStyle: {
        color: '#303133',
      },
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true,
    },
  };

  const getLineChartOption = (data: any, title: string) => ({
    ...baseOption,
    title: {
      text: title,
      left: 'left',
      textStyle: {
        color: '#303133',
        fontSize: 16,
        fontWeight: 600,
      },
    },
    legend: {
      data: data.series.map((s: any) => s.name),
      top: 30,
      textStyle: {
        color: '#606266',
      },
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.xAxis,
      axisLine: {
        lineStyle: {
          color: '#e4e7ed',
        },
      },
      axisLabel: {
        color: '#606266',
      },
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
      splitLine: {
        lineStyle: {
          color: '#f0f0f0',
          type: 'dashed',
        },
      },
      axisLabel: {
        color: '#909399',
      },
    },
    series: data.series.map((item: any, index: number) => ({
      name: item.name,
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      data: item.data,
      lineStyle: {
        width: 2,
        color: chartTheme.color[index],
      },
      itemStyle: {
        color: chartTheme.color[index],
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: `${chartTheme.color[index]}40` },
            { offset: 1, color: `${chartTheme.color[index]}05` },
          ],
        },
      },
    })),
  });

  const getBarChartOption = (data: any, title: string) => ({
    ...baseOption,
    title: {
      text: title,
      left: 'left',
      textStyle: {
        color: '#303133',
        fontSize: 16,
        fontWeight: 600,
      },
    },
    legend: {
      data: data.series.map((s: any) => s.name),
      top: 30,
      textStyle: {
        color: '#606266',
      },
    },
    xAxis: {
      type: 'category',
      data: data.xAxis,
      axisLine: {
        lineStyle: {
          color: '#e4e7ed',
        },
      },
      axisLabel: {
        color: '#606266',
      },
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
      splitLine: {
        lineStyle: {
          color: '#f0f0f0',
          type: 'dashed',
        },
      },
      axisLabel: {
        color: '#909399',
      },
    },
    series: data.series.map((item: any, index: number) => ({
      name: item.name,
      type: 'bar',
      barWidth: '35%',
      data: item.data,
      itemStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: chartTheme.color[index] },
            { offset: 1, color: `${chartTheme.color[index]}80` },
          ],
        },
        borderRadius: [4, 4, 0, 0],
      },
    })),
  });

  const getPieChartOption = (data: any, title: string) => ({
    title: {
      text: title,
      left: 'left',
      textStyle: {
        color: '#303133',
        fontSize: 16,
        fontWeight: 600,
      },
    },
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e4e7ed',
      borderWidth: 1,
      textStyle: {
        color: '#303133',
      },
      formatter: '{b}: {c} ({d}%)',
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: {
        color: '#606266',
      },
    },
    series: [
      {
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2,
        },
        label: {
          show: false,
          position: 'center',
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 18,
            fontWeight: 'bold',
            color: '#303133',
          },
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.2)',
          },
        },
        labelLine: {
          show: false,
        },
        data: data.map((item: any) => ({
          value: item.value,
          name: item.name,
          itemStyle: {
            color: item.color,
          },
        })),
      },
    ],
  });

  const getAreaChartOption = (data: any, title: string) => ({
    ...baseOption,
    title: {
      text: title,
      left: 'left',
      textStyle: {
        color: '#303133',
        fontSize: 16,
        fontWeight: 600,
      },
    },
    legend: {
      data: data.series.map((s: any) => s.name),
      top: 30,
      textStyle: {
        color: '#606266',
      },
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.xAxis,
      axisLine: {
        lineStyle: {
          color: '#e4e7ed',
        },
      },
      axisLabel: {
        color: '#606266',
      },
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
      splitLine: {
        lineStyle: {
          color: '#f0f0f0',
          type: 'dashed',
        },
      },
      axisLabel: {
        color: '#909399',
      },
    },
    series: data.series.map((item: any, index: number) => ({
      name: item.name,
      type: 'line',
      smooth: true,
      symbol: 'none',
      data: item.data,
      lineStyle: {
        width: 2,
        color: chartTheme.color[index],
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: `${chartTheme.color[index]}60` },
            { offset: 1, color: `${chartTheme.color[index]}05` },
          ],
        },
      },
    })),
  });

  return {
    chartRefs,
    chartTheme,
    getLineChartOption,
    getBarChartOption,
    getPieChartOption,
    getAreaChartOption,
  };
}

/**
 * 时间问候语 Hook
 */
export function useGreeting() {
  const greeting = computed(() => {
    const hour = new Date().getHours();
    if (hour >= 6 && hour < 9) return '早上好';
    if (hour >= 9 && hour < 12) return '上午好';
    if (hour >= 12 && hour < 14) return '中午好';
    if (hour >= 14 && hour < 18) return '下午好';
    if (hour >= 18 && hour < 22) return '晚上好';
    return '夜深了';
  });

  return { greeting };
}

/**
 * 实时时间 Hook
 */
export function useCurrentTime() {
  const currentTime = ref('');
  const currentDate = ref('');

  const updateTime = () => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');
    const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
    const weekDay = weekDays[now.getDay()];

    currentTime.value = `${hours}:${minutes}:${seconds}`;
    currentDate.value = `${year}年${month}月${day}日 ${weekDay}`;
  };

  let timer: ReturnType<typeof setInterval>;

  onMounted(() => {
    updateTime();
    timer = setInterval(updateTime, 1000);
  });

  onUnmounted(() => {
    clearInterval(timer);
  });

  return { currentTime, currentDate };
}
