<template>
	<div class="page-container">
		<YsTable
			ref="tableRef"
			:request-fn="requestFn"
			:options="tableOptions"
			:query-params="searchForm"
			:show-pagination="true"
			:events="tableEvents"
			@request-success="handleSuccess"
		>
			<template #page-header>
				<div class="card-header page-header">
					<div class="mb10">
						<el-form ref="searchFormRef" label-width="90px">
							<el-select placeholder="请选择日志类型" class="w-180 mr20" v-model="searchForm.logType" clearable>
								<el-option v-for="item in state.logTypeData" :key="item.value" :label="item.label" :value="item.value" />
							</el-select>
							<el-select placeholder="请选择日志级别" class="w-180 mr20" v-model="searchForm.logLevel" clearable>
								<el-option v-for="item in state.logLevelData" :key="item.value" :label="item.label" :value="item.value" />
							</el-select>
							<el-input placeholder="请输入操作人" class="w-180 mr20" v-model="searchForm.userName" clearable @keyup.enter="searchTable" />
							<el-button type="primary" class="ml10" @click="searchTable">
								<i class="ri-search-line"></i>
								查询
							</el-button>
							<el-button type="default" class="ml10" @click="resetTable">
								<i class="ri-reset-left-line"></i>
								重置
							</el-button>
						</el-form>
					</div>
				</div>
			</template>

			<template #slots_logtype="{ row }">
				<el-tag :type="getLogTypeTag(row.logType)">
					{{ getLogTypeLabel(row.logType) }}
				</el-tag>
			</template>
			<template #slots_loglevel="{ row }">
				<el-tag :type="getLogLevelTag(row.logLevel)">
					{{ getLogLevelLabel(row.logLevel) }}
				</el-tag>
			</template>
			<template #slots_userName="{ row }"> {{ row.userName }}（{{ row.loginName }}） </template>
			<template #slots_action="scope">
				<div class="operation-btn-group">
					<el-link type="danger" underline="never" class="pr6 pl6" @click="deleteRow(scope.row)" v-auth="'system:log:delete'">删除</el-link>
					<el-link type="success" underline="never" class="pr6 pl6" @click="openDialogCRU(scope.row)">详情</el-link>
				</div>
			</template>
		</YsTable>
	</div>
</template>
<script setup lang="ts" name="LoginLogTab">
import { onMounted, reactive, ref } from 'vue';
import { useLoggerApi } from '@/api/system/logger/index';

type LogType = 0 | 1 | 2 | 3;

const tableRef = ref();

const state = reactive({
	logTypeData: [
		{ value: 0, label: '登录' },
		{ value: 1, label: '登出' },
	],
	logLevelData: [
		{ value: 0, label: 'DEBUG' },
		{ value: 1, label: 'INFO' },
		{ value: 2, label: 'WARN' },
		{ value: 3, label: 'ERROR' },
		{ value: 4, label: 'FATAL' },
	],
});

const searchForm = reactive({
	logType: '',
	logLevel: '',
	userName: '',
});

const tableOptions = reactive<any>({
	columns: [
		{ type: 'seq', width: 80, align: 'center' },
		{ title: '类型', field: 'logType', width: 120, slots: { default: 'slots_logtype' } },
		{ title: '日志级别', field: 'logLevel', width: 120, slots: { default: 'slots_loglevel' } },
		{ title: '操作人', field: 'userName', slots: { default: 'slots_userName' } },
		{ title: '登录IP', field: 'ip' },
		{ title: '登录地址', field: 'address' },
		{ title: '操作时间', field: 'logTime' },
		{ title: '操作', field: 'action', width: 'auto', slots: { default: 'slots_action' }, align: 'center' },
	],
	toolbarConfig: {
		enabled: false,
	},
});

const requestFn = (params: any) => {
	const searchParams = {
		...params,
		...searchForm,
		logTypes: [0, 1],
	};
	return useLoggerApi().list(searchParams);
};

const tableEvents = {};

const getLogTypeTag = (logType: LogType): string => {
	const tagMap: Record<LogType, string> = {
		0: 'success',
		1: 'info',
		2: 'primary',
		3: 'danger',
	};
	return tagMap[logType] || '';
};

const getLogTypeLabel = (logType: LogType): string => {
	const labelMap: Record<LogType, string> = {
		0: '登录',
		1: '登出',
		2: '业务操作',
		3: '异常',
	};
	return labelMap[logType] || '';
};

const getLogLevelTag = (logLevel: number): string => {
	const tagMap: Record<number, string> = {
		0: 'info',
		1: 'success',
		2: 'warning',
		3: 'danger',
		4: 'danger',
	};
	return tagMap[logLevel] || '';
};

const getLogLevelLabel = (logLevel: number): string => {
	const labelMap: Record<number, string> = {
		0: 'DEBUG',
		1: 'INFO',
		2: 'WARN',
		3: 'ERROR',
		4: 'FATAL',
	};
	return labelMap[logLevel] || '';
};

const handleSearch = (): void => {
	searchTable();
};

const searchTable = (resetPage = true) => {
	tableRef.value?.search(resetPage);
};

const resetTable = () => {
	tableRef.value?.reset();
};

const deleteRow = async (row: any) => {
	try {
		await useLoggerApi().delete(row.id);
		tableRef.value?.refresh();
	} catch (error) {
		console.error('删除登录日志失败:', error);
	}
};

const handleSuccess = (res: any) => {
};

const openDialogCRU = (row?: any): void => {
	emit('openDialog', row);
};

const emit = defineEmits(['refresh', 'openDialog']);

onMounted(() => {
	searchTable();
});
</script>
<style scoped lang="scss">
.page-container {
	height: calc(100vh - 185px);
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	padding: 0;
}
</style>
