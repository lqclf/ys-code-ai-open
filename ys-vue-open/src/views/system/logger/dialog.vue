<template>
	<el-dialog title="日志详情" v-model="state.isShowDialog" width="760px" @closed="onClose">
		<el-descriptions :column="2" border v-if="state.ruleForm">
			<el-descriptions-item label="日志类型">{{ getLogTypeLabel(state.ruleForm.logType) }}</el-descriptions-item>
			<el-descriptions-item label="日志级别">{{ getLogLevelLabel(state.ruleForm.logLevel) }}</el-descriptions-item>
			<el-descriptions-item label="操作人">{{ state.ruleForm.userName }}（{{ state.ruleForm.loginName }}）</el-descriptions-item>
			<el-descriptions-item label="操作时间">{{ state.ruleForm.logTime }}</el-descriptions-item>
			<el-descriptions-item label="功能模块">{{ state.ruleForm.modul || '-' }}</el-descriptions-item>
			<el-descriptions-item label="操作类型">{{ state.ruleForm.type || '-' }}</el-descriptions-item>
			<el-descriptions-item label="操作描述" :span="2">{{ state.ruleForm.remark || '-' }}</el-descriptions-item>
			<el-descriptions-item label="请求方法" :span="2">{{ state.ruleForm.method || '-' }}</el-descriptions-item>
			<el-descriptions-item label="IP">{{ state.ruleForm.ip || '-' }}</el-descriptions-item>
			<el-descriptions-item label="地址">{{ state.ruleForm.address || '-' }}</el-descriptions-item>
			<el-descriptions-item label="请求参数" :span="2">
				<pre class="log-pre">{{ state.ruleForm.param || '-' }}</pre>
			</el-descriptions-item>
			<el-descriptions-item label="响应结果" :span="2">
				<pre class="log-pre">{{ state.ruleForm.result || '-' }}</pre>
			</el-descriptions-item>
			<el-descriptions-item label="异常详情" :span="2">
				<pre class="log-pre">{{ state.ruleForm.exceptionDetail || '-' }}</pre>
			</el-descriptions-item>
		</el-descriptions>
		<template #footer>
			<el-button @click="state.isShowDialog = false">关闭</el-button>
		</template>
	</el-dialog>
</template>
<script setup lang="ts" name="systemLoggerDialog">
import { reactive } from 'vue';
import { useLoggerApi } from '@/api/system/logger/index';

const state = reactive({
	isShowDialog: false,
	ruleForm: null as any,
});

const getLogTypeLabel = (logType: number): string => {
	const labelMap: Record<number, string> = {
		0: '登录',
		1: '登出',
		2: '业务操作',
		3: '异常',
	};
	return labelMap[logType] || '-';
};

const getLogLevelLabel = (logLevel: number): string => {
	const labelMap: Record<number, string> = {
		0: 'DEBUG',
		1: 'INFO',
		2: 'WARN',
		3: 'ERROR',
		4: 'FATAL',
	};
	return labelMap[logLevel] || '-';
};

const openDialog = async (row?: any) => {
	state.isShowDialog = true;
	const id = row?.id;
	if (id) {
		const res = await useLoggerApi().getById(id);
		state.ruleForm = res.data || res.data?.data || null;
	} else {
		state.ruleForm = null;
	}
};

const onClose = () => {
	state.ruleForm = null;
};

defineExpose({ openDialog });
</script>
<style scoped lang="scss">
.log-pre {
	white-space: pre-wrap;
	word-break: break-all;
	max-height: 180px;
	overflow: auto;
	margin: 0;
	font-size: 12px;
	line-height: 1.6;
}
</style>
