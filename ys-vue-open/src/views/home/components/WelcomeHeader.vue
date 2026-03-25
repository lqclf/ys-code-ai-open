<template>
  <div class="welcome-header">
    <div class="welcome-content">
      <div class="welcome-left">
        <div class="welcome-text">
          <span class="greeting">{{ greeting }}</span>
          <span class="username">，{{ username }}</span>
        </div>
        <div class="welcome-subtitle">{{ subtitle }}</div>
      </div>
      <div class="welcome-right">
        <div class="datetime-info">
          <div class="current-time">{{ currentTime }}</div>
          <div class="current-date">{{ currentDate }}</div>
        </div>
        <div class="quick-actions">
          <el-tooltip content="刷新数据" placement="bottom">
            <el-button circle class="action-btn" @click="handleRefresh">
              <el-icon><Refresh /></el-icon>
            </el-button>
          </el-tooltip>
          <el-tooltip content="系统设置" placement="bottom">
            <el-button circle class="action-btn" @click="handleSettings">
              <el-icon><Setting /></el-icon>
            </el-button>
          </el-tooltip>
          <el-tooltip content="帮助中心" placement="bottom">
            <el-button circle class="action-btn" @click="handleHelp">
              <el-icon><QuestionFilled /></el-icon>
            </el-button>
          </el-tooltip>
        </div>
      </div>
    </div>
    <div class="welcome-decoration">
      <div class="decoration-line"></div>
      <div class="decoration-dot"></div>
    </div>
  </div>
</template>

<script setup lang="ts" name="WelcomeHeader">
import { computed } from 'vue';
import { Refresh, Setting, QuestionFilled } from '@element-plus/icons-vue';
import { useGreeting, useCurrentTime } from '../hooks/useCharts';

interface Props {
  username?: string;
  subtitle?: string;
}

const props = withDefaults(defineProps<Props>(), {
  username: '管理员',
  subtitle: '欢迎回来，祝您工作愉快！',
});

const emit = defineEmits<{
  refresh: [];
  settings: [];
  help: [];
}>();

const { greeting } = useGreeting();
const { currentTime, currentDate } = useCurrentTime();

const handleRefresh = () => {
  emit('refresh');
};

const handleSettings = () => {
  emit('settings');
};

const handleHelp = () => {
  emit('help');
};
</script>

<style scoped lang="scss">
.welcome-header {
  background: linear-gradient(135deg, #409eff 0%, #667eea 100%);
  border-radius: 12px;
  padding: 24px 28px;
  position: relative;
  overflow: hidden;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(64, 158, 255, 0.25);

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -10%;
    width: 300px;
    height: 300px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -30%;
    left: 10%;
    width: 200px;
    height: 200px;
    background: rgba(255, 255, 255, 0.08);
    border-radius: 50%;
  }

  .welcome-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
    z-index: 1;
  }

  .welcome-left {
    .welcome-text {
      color: #ffffff;
      font-size: 24px;
      font-weight: 600;
      margin-bottom: 8px;

      .greeting {
        margin-right: 4px;
      }

      .username {
        font-weight: 500;
      }
    }

    .welcome-subtitle {
      color: rgba(255, 255, 255, 0.85);
      font-size: 14px;
    }
  }

  .welcome-right {
    display: flex;
    align-items: center;
    gap: 24px;

    .datetime-info {
      text-align: right;
      color: #ffffff;

      .current-time {
        font-size: 32px;
        font-weight: 600;
        font-family: 'Courier New', monospace;
        letter-spacing: 2px;
      }

      .current-date {
        font-size: 14px;
        opacity: 0.9;
        margin-top: 4px;
      }
    }

    .quick-actions {
      display: flex;
      gap: 12px;

      .action-btn {
        background: rgba(255, 255, 255, 0.2);
        border: 1px solid rgba(255, 255, 255, 0.3);
        color: #ffffff;
        transition: all 0.3s ease;

        &:hover {
          background: rgba(255, 255, 255, 0.3);
          transform: translateY(-2px);
        }

        :deep(.el-icon) {
          font-size: 16px;
        }
      }
    }
  }

  .welcome-decoration {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: rgba(255, 255, 255, 0.3);

    .decoration-line {
      position: absolute;
      left: 0;
      bottom: 0;
      height: 100%;
      width: 30%;
      background: rgba(255, 255, 255, 0.8);
      animation: slideRight 3s ease-in-out infinite;
    }

    .decoration-dot {
      position: absolute;
      right: 20px;
      bottom: -4px;
      width: 10px;
      height: 10px;
      background: #ffffff;
      border-radius: 50%;
      animation: pulse 2s ease-in-out infinite;
    }
  }
}

@keyframes slideRight {
  0%,
  100% {
    left: 0;
    width: 30%;
  }
  50% {
    left: 70%;
    width: 30%;
  }
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.5);
    opacity: 0.5;
  }
}

@media screen and (max-width: 768px) {
  .welcome-header {
    padding: 16px 20px;

    .welcome-content {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
    }

    .welcome-left {
      .welcome-text {
        font-size: 20px;
      }
    }

    .welcome-right {
      width: 100%;
      justify-content: space-between;

      .datetime-info {
        text-align: left;

        .current-time {
          font-size: 24px;
        }
      }
    }
  }
}
</style>
