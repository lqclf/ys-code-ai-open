<template>
	<div class="page-container">
		<el-card>
			<el-tabs v-model="activeTab" @tab-change="handleTabChange">
				<el-tab-pane label="登录日志" name="login">
					<LoginLogTab v-if="activeTab === 'login'" @refresh="handleRefresh" @openDialog="openDialog" />
				</el-tab-pane>
				<el-tab-pane label="操作日志" name="operation">
					<OperationLogTab v-if="activeTab === 'operation'" ref="operationLogTabRef" @refresh="handleRefresh" @openDialog="openDialog" />
				</el-tab-pane>
				<el-tab-pane label="异常日志" name="exception">
					<ExceptionLogTab v-if="activeTab === 'exception'" @refresh="handleRefresh" @openDialog="openDialog" />
				</el-tab-pane>
			</el-tabs>
			<LoggerDialog ref="loggerDialogRef" />
		</el-card>
	</div>
</template>
<script setup lang="ts" name="systemLogger">
import { defineAsyncComponent, ref, nextTick } from 'vue';

const LoggerDialog = defineAsyncComponent(() => import('@/views/system/logger/dialog.vue'));
const LoginLogTab = defineAsyncComponent(() => import('@/views/system/logger/components/LoginLogTab.vue'));
const OperationLogTab = defineAsyncComponent(() => import('@/views/system/logger/components/OperationLogTab.vue'));
const ExceptionLogTab = defineAsyncComponent(() => import('@/views/system/logger/components/ExceptionLogTab.vue'));

const loggerDialogRef = ref();
const operationLogTabRef = ref();
const activeTab = ref<string>('login');

const handleSearch = (): void => {
	if (activeTab.value === 'login') {
		operationLogTabRef.value?.handleSearch();
	}
};

const handleRefresh = (): void => {
	handleSearch();
};

const openDialog = (row?: any): void => {
	loggerDialogRef.value.openDialog(row);
};

const handleTabChange = (tab: string): void => {
	activeTab.value = tab;
	if (tab === 'operation') {
		nextTick(() => {
			handleSearch();
		});
	}
};
</script>
<style scoped lang="scss">
.page-container {
	:deep(.el-tabs__nav-wrap) {
		padding-left: 10px;
	}
}
</style>
