<template>
  <div class="home-container" v-loading="loading" element-loading-text="加载中...">
    <WelcomeHeader
      :username="userInfo.userName || '管理员'"
      subtitle="欢迎回来，祝您工作愉快！"
      @refresh="handleRefresh"
      @settings="handleSettings"
      @help="handleHelp"
    />

    <StatCards :data="statsData" />

    <ChartPanel
      :sales-trend-data="salesTrendData"
      :order-distribution-data="orderDistributionData"
      :user-source-data="userSourceData"
      :real-time-data="realTimeData"
    />

    <div class="bottom-section">
      <div class="left-column">
        <QuickActions :data="quickActions" />
        <SystemOverview :data="systemStatus" />
      </div>
      <div class="right-column">
        <NotificationList
          :data="notifications"
          @read-all="handleReadAll"
          @click="handleNotificationClick"
        />
        <OperationLog :data="operationLogs" @view-all="handleViewAllLogs" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="HomeIndex">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useUserInfo } from '@/stores/userInfo';
import { useMockData } from './hooks/useMockData';
import WelcomeHeader from './components/WelcomeHeader.vue';
import StatCards from './components/StatCards.vue';
import ChartPanel from './components/ChartPanel.vue';
import QuickActions from './components/QuickActions.vue';
import NotificationList from './components/NotificationList.vue';
import SystemOverview from './components/SystemOverview.vue';
import OperationLog from './components/OperationLog.vue';

const router = useRouter();
const userInfoStore = useUserInfo();
const userInfo = userInfoStore.userInfos;

const {
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
} = useMockData();

const handleRefresh = async () => {
  ElMessage.info('正在刷新数据...');
  await refreshData();
  ElMessage.success('数据刷新成功');
};

const handleSettings = () => {
  router.push('/system/config');
};

const handleHelp = () => {
  ElMessage.info('帮助中心功能开发中...');
};

const handleReadAll = () => {
  notifications.forEach((item) => {
    item.read = true;
  });
  ElMessage.success('已全部标记为已读');
};

const handleNotificationClick = (item: any) => {
  item.read = true;
  ElMessage.info(`查看通知: ${item.title}`);
};

const handleViewAllLogs = () => {
  router.push('/system/log');
};

onMounted(async () => {
  await refreshData();
});
</script>

<style scoped lang="scss">
.home-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100%;
  overflow-y: auto;
  height: calc(100vh - 110px);

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background: #c0c4cc;
    border-radius: 3px;
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }
}

.bottom-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;

  .left-column,
  .right-column {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
}

@media screen and (max-width: 1200px) {
  .bottom-section {
    grid-template-columns: 1fr;
  }
}

@media screen and (max-width: 768px) {
  .home-container {
    padding: 12px;
  }
}
</style>
