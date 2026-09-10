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
							<el-input placeholder="请输入功能模块" class="w-180 mr20" v-model="searchForm.modul" clearable @keyup.enter="searchTable" />
							<el-input placeholder="请输入操作人" class="w-180 mr20" v-model="searchForm.userName" clearable />
							<el-select placeholder="请选择操作类型" class="w-180 mr20" v-model="searchForm.type" clearable>
								<el-option v-for="item in state.logOperTypeData" :key="item.value" :label="item.label" :value="item.value" />
							</el-select>
							<el-select placeholder="请选择日志级别" class="w-180 mr20" v-model="searchForm.logLevel" clearable>
								<el-option v-for="item in state.logLevelData" :key="item.value" :label="item.label" :value="item.value" />
							</el-select>
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

			<template #slots_userName="{ row }"> {{ row.userName }}（{{ row.loginName }}） </template>
			<template #slots_opertype="{ row }">
				<el-tag :type="getOperTypeTag(row.type)">
					{{ row.type }}
				</el-tag>
			</template>
			<template #slots_loglevel="{ row }">
				<el-tag :type="getLogLevelTag(row.logLevel)">
					{{ getLogLevelLabel(row.logLevel) }}
				</el-tag>
			</template>
			<template #slots_action="scope">
				<div class="operation-btn-group">
					<el-link type="danger" underline="never" class="pr6 pl6" @click="deleteRow(scope.row)" v-auth="'system:log:delete'">删除</el-link>
					<el-link type="success" underline="never" class="pr6 pl6" @click="openDialogCRU(scope.row)">详情</el-link>
				</div>
			</template>
		</YsTable>
	</div>
</template>
<script setup lang="ts" name="OperationLogTab">
import { onMounted, reactive, ref } from 'vue';
import { useLoggerApi } from '@/api/system/logger/index';
import { commonApi } from '@/api/common';

const tableRef = ref();

const state = reactive({
	logOperTypeData: [] as SelectOptionType[],
	logLevelData: [
		{ value: 0, label: 'DEBUG' },
		{ value: 1, label: 'INFO' },
		{ value: 2, label: 'WARN' },
		{ value: 3, label: 'ERROR' },
		{ value: 4, label: 'FATAL' },
	],
});

const searchForm = reactive({
	modul: '',
	userName: '',
	type: '',
	logLevel: '',
});

const tableOptions = reactive<any>({
	columns: [
		{ type: 'seq', width: 80, align: 'center' },
		{ title: '功能模块', field: 'modul' },
		{ title: '操作人', field: 'userName', slots: { default: 'slots_userName' } },
		{ title: '操作类型', field: 'type', width: 90, slots: { default: 'slots_opertype' } },
		{ title: '日志级别', field: 'logLevel', width: 120, slots: { default: 'slots_loglevel' } },
		{ title: '操作描述', field: 'remark' },
		{ title: '操作IP', field: 'ip' },
		{ title: '操作地址', field: 'address' },
		{ title: '操作时间', field: 'logTime', width: 160 },
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
		logTypes: [2],
	};
	return useLoggerApi().list(searchParams);
};

const tableEvents = {};

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
	}
};

const getOperTypeTag = (type: string): string => {
	const tagMap: Record<string, string> = {
		新增: 'primary',
		更新: 'success',
		其他: 'warning',
		删除: 'danger',
		查询: 'info',
		导入: 'warning',
		导出: 'warning',
	};
	return tagMap[type] || 'primary';
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

const handleSuccess = (res: any) => {
};

const openDialogCRU = (row?: any): void => {
	emit('openDialog', row);
};

const emit = defineEmits(['refresh', 'openDialog']);

const initData = async () => {
	const res = await commonApi().getEnumValue('sys_logopertype');
	state.logOperTypeData = res.data;
};

onMounted(() => {
	searchTable();
	initData();
});

defineExpose({
	tableRef,
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
