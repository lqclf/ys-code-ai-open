<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-tabs v-model="activeTab" class="demo-tabs">
				<el-tab-pane label="登录日志" name="login">
					<LoginLogTab ref="loginRef" @openDialog="openDialogCRU" />
				</el-tab-pane>
				<el-tab-pane label="操作日志" name="operation">
					<OperationLogTab ref="operationRef" @openDialog="openDialogCRU" />
				</el-tab-pane>
				<el-tab-pane label="异常日志" name="exception">
					<ExceptionLogTab ref="exceptionRef" @openDialog="openDialogCRU" />
				</el-tab-pane>
			</el-tabs>
		</div>

		<Dialog ref="dialogRef" />
	</div>
</template>
<script setup lang="ts" name="systemLogger">
import { ref } from 'vue';
import LoginLogTab from './components/LoginLogTab.vue';
import OperationLogTab from './components/OperationLogTab.vue';
import ExceptionLogTab from './components/ExceptionLogTab.vue';
import Dialog from './dialog.vue';

const activeTab = ref('login');
const loginRef = ref();
const operationRef = ref();
const exceptionRef = ref();
const dialogRef = ref();

// 打开详情弹窗
const openDialogCRU = (row?: any) => {
	dialogRef.value?.openDialog(row);
};
</script>
<style scoped lang="scss">
.layout-padding {
	display: flex;
	flex-direction: column;
}
</style>
