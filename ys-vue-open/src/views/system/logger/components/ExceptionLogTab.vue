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
			<!-- 页面头部区域 -->
			<template #page-header>
				<div class="card-header page-header">
					<div class="mb10">
						<el-form ref="searchFormRef" label-width="90px">
							<el-input placeholder="请输入功能模块" class="w-180 mr20" v-model="searchForm.modul" clearable @keyup.enter="searchTable" />
							<el-input placeholder="请输入操作人" class="w-180 mr20" v-model="searchForm.userName" clearable />
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

			<!-- 操作列 -->
			<template #slots_userName="{ row }"> {{ row.userName }}（{{ row.loginName }}） </template>
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
<script setup lang="ts" name="ExceptionLogTab">
import { onMounted, reactive, ref } from 'vue';
import { useLoggerApi } from '@/api/system/logger/index';

const tableRef = ref();

const state = reactive({
	logLevelData: [
		{ value: 0, label: 'DEBUG' },
		{ value: 1, label: 'INFO' },
		{ value: 2, label: 'WARN' },
		{ value: 3, label: 'ERROR' },
		{ value: 4, label: 'FATAL' },
	],
});

// 搜索表单
const searchForm = reactive({
	modul: '',
	userName: '',
	logLevel: '',
});

const tableOptions = reactive<any>({
	columns: [
		{ type: 'seq', width: 80, align: 'center' },
		{ title: '操作人', field: 'userName', width: 210, slots: { default: 'slots_userName' } },
		{ title: '日志级别', field: 'logLevel', width: 120, slots: { default: 'slots_loglevel' } },
		{ title: '异常方法', field: 'method', showOverflow: 'tooltip' },
		{ title: '操作IP', field: 'ip', width: 160 },
		{ title: '操作地址', field: 'address', width: 220 },
		{ title: '操作时间', field: 'logTime', width: 160 },
		{ title: '异常名称', field: 'excName', showOverflow: 'tooltip' },
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
		logTypes: [3],
	};
	return useLoggerApi().list(searchParams);
};

const tableEvents = {};

// 搜索方法
const searchTable = (resetPage = true) => {
	tableRef.value?.search(resetPage);
};

// 重置方法
const resetTable = () => {
	// 重置表格
	tableRef.value?.reset();
};

// 删除方法
const deleteRow = async (row: any) => {
	try {
		await useLoggerApi().delete(row.id);
		tableRef.value?.refresh();
	} catch (error) {
		// 错误处理
	}
};

// 数据加载成功回调
const handleSuccess = (res: any) => {
	//console.log('数据加载成功', res);
};

const getLogLevelTag = (logLevel: number): string => {
	const tagMap: Record<number, string> = {
		0: 'info', // DEBUG
		1: 'success', // INFO
		2: 'warning', // WARN
		3: 'danger', // ERROR
		4: 'danger', // FATAL
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

// 打开详情弹窗
const openDialogCRU = (row?: any): void => {
	// 通过事件总线或ref调用父组件的方法
	emit('openDialog', row);
};

// 定义事件
const emit = defineEmits(['refresh', 'openDialog']);

// 页面加载时
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
