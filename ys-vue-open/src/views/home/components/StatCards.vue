<template>
  <div class="stat-cards">
    <div
      v-for="(item, index) in data"
      :key="item.id"
      class="stat-card"
      :style="{ animationDelay: `${index * 0.1}s` }"
    >
      <div class="card-icon" :style="{ background: `${item.color}15`, color: item.color }">
        <el-icon :size="28">
          <component :is="getIcon(item.icon)" />
        </el-icon>
      </div>
      <div class="card-content">
        <div class="card-title">{{ item.title }}</div>
        <div class="card-value">
          <span class="value-number">{{ formatValue(item.value) }}</span>
          <span class="value-suffix">{{ item.suffix }}</span>
        </div>
        <div class="card-trend" :class="item.trend">
          <el-icon :size="14">
            <component :is="item.trend === 'up' ? 'Top' : 'Bottom'" />
          </el-icon>
          <span>{{ item.percentage }}%</span>
          <span class="trend-desc">{{ item.description }}</span>
        </div>
      </div>
      <div class="card-decoration">
        <div class="decoration-circle" :style="{ background: item.color }"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="StatCards">
import { markRaw } from 'vue';
import {
  User,
  ShoppingCart,
  Money,
  View,
  TrendCharts,
  UserFilled,
  Top,
  Bottom,
} from '@element-plus/icons-vue';
import type { StatItem } from '../hooks/useMockData';

interface Props {
  data: StatItem[];
}

defineProps<Props>();

const iconMap: Record<string, any> = {
  User: markRaw(User),
  ShoppingCart: markRaw(ShoppingCart),
  Money: markRaw(Money),
  View: markRaw(View),
  TrendCharts: markRaw(TrendCharts),
  UserFilled: markRaw(UserFilled),
  Top: markRaw(Top),
  Bottom: markRaw(Bottom),
};

const getIcon = (iconName: string) => {
  return iconMap[iconName] || User;
};

const formatValue = (value: number): string => {
  if (value >= 10000) {
    return (value / 10000).toFixed(1) + 'w';
  }
  return value.toLocaleString();
};
</script>

<style scoped lang="scss">
.stat-cards {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  animation: fadeInUp 0.5s ease-out forwards;
  opacity: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);

    .card-icon {
      transform: scale(1.1);
    }

    .card-decoration .decoration-circle {
      transform: scale(1.2);
    }
  }

  .card-icon {
    width: 56px;
    height: 56px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 16px;
    transition: transform 0.3s ease;
  }

  .card-content {
    position: relative;
    z-index: 1;

    .card-title {
      font-size: 14px;
      color: #909399;
      margin-bottom: 8px;
    }

    .card-value {
      margin-bottom: 8px;

      .value-number {
        font-size: 28px;
        font-weight: 600;
        color: #303133;
      }

      .value-suffix {
        font-size: 14px;
        color: #606266;
        margin-left: 4px;
      }
    }

    .card-trend {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;

      &.up {
        color: #67c23a;
      }

      &.down {
        color: #f56c6c;
      }

      .trend-desc {
        color: #909399;
        margin-left: 4px;
      }
    }
  }

  .card-decoration {
    position: absolute;
    right: -10px;
    bottom: -10px;

    .decoration-circle {
      width: 80px;
      height: 80px;
      border-radius: 50%;
      opacity: 0.1;
      transition: transform 0.3s ease;
    }
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media screen and (max-width: 1400px) {
  .stat-cards {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media screen and (max-width: 768px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media screen and (max-width: 480px) {
  .stat-cards {
    grid-template-columns: 1fr;
  }
}
</style>
