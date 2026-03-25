<template>
  <div class="chart-panel">
    <div class="chart-row">
      <div class="chart-item">
        <div class="chart-card">
          <v-chart
            class="chart"
            :option="lineChartOption"
            autoresize
          />
        </div>
      </div>
      <div class="chart-item">
        <div class="chart-card">
          <v-chart
            class="chart"
            :option="barChartOption"
            autoresize
          />
        </div>
      </div>
    </div>
    <div class="chart-row">
      <div class="chart-item">
        <div class="chart-card">
          <v-chart
            class="chart"
            :option="pieChartOption"
            autoresize
          />
        </div>
      </div>
      <div class="chart-item">
        <div class="chart-card">
          <v-chart
            class="chart"
            :option="areaChartOption"
            autoresize
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="ChartPanel">
import { computed } from 'vue';
import VChart from 'vue-echarts';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { LineChart, BarChart, PieChart } from 'echarts/charts';
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
} from 'echarts/components';
import { useCharts } from '../hooks/useCharts';

use([
  CanvasRenderer,
  LineChart,
  BarChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
]);

interface Props {
  salesTrendData: any;
  orderDistributionData: any;
  userSourceData: any;
  realTimeData: any;
}

const props = defineProps<Props>();

const { getLineChartOption, getBarChartOption, getPieChartOption, getAreaChartOption } = useCharts();

const lineChartOption = computed(() => getLineChartOption(props.salesTrendData, '销售趋势分析'));
const barChartOption = computed(() => getBarChartOption(props.orderDistributionData, '订单分布统计'));
const pieChartOption = computed(() => getPieChartOption(props.userSourceData, '用户来源分布'));
const areaChartOption = computed(() => getAreaChartOption(props.realTimeData, '实时数据监控'));
</script>

<style scoped lang="scss">
.chart-panel {
  margin-bottom: 20px;
}

.chart-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 16px;

  &:last-child {
    margin-bottom: 0;
  }
}

.chart-item {
  .chart-card {
    background: #ffffff;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    }
  }
}

.chart {
  width: 100%;
  height: 320px;
}

@media screen and (max-width: 1200px) {
  .chart-row {
    grid-template-columns: 1fr;
  }
}

@media screen and (max-width: 768px) {
  .chart {
    height: 280px;
  }
}
</style>
