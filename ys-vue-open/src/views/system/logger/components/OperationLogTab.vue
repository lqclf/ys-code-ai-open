<template>
	<div class="page-container">
		<!-- 查询条件 -->
		<div class="page-header mb15">
			<el-form :model="searchForm" label-width="90px">
				<el-input placeholder="请输入功能模块" class="w-180 mr20" v-model="searchForm.modul" clearable @keyup.enter="searchTable" />
				<el-input placeholder="请输入操作人" class="w-180 mr20" v-model="searchForm.userName" clearable />
				<el-select placeholder="请选择操作类型" class="w-180 mr20" v-model="searchForm.type" clearable>
					<el-option v-for="item in operTypeData" :key="item.value" :label="item.label" :value="item.value" />
				</el-select>
				<el-select placeholder="请选择日志级别" class="w-180 mr20" v-model="searchForm.logLevel" clearable>
					<el-option v-for="item in logLevelData" :key="item.value" :label="item.label" :value="item.value" />
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

		<!-- 操作日志表格 -->
		<el-table :data="tableData" v-loading="state.loading" border stripe highlight-current-row>
			<el-table-column type="index" label="序号" width="80" align="center" />
			<el-table-column prop="modul" label="功能模块" show-overflow-tooltip />
			<el-table-column label="操作人" min-width="160">
				<template #default="scope"> {{ scope.row.userName }}（{{ scope.row.loginName }}） </template>
			</el-table-column>
			<el-table-column label="操作类型" width="100">
				<template #default="scope">
					<el-tag :type="getOperTypeTag(scope.row.type)">{{ scope.row.type }}</el-tag>
				</template>
			</el-table-column>
			<el-table-column label="日志级别" width="100">
				<template #default="scope">
					<el-tag :type="getLogLevelTag(scope.row.logLevel)">{{ getLogLevelLabel(scope.row.logLevel) }}</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="remark" label="操作描述" show-overflow-tooltip />
			<el-table-column prop="ip" label="操作IP" width="150" />
			<el-table-column prop="address" label="操作地址" width="180" show-overflow-tooltip />
			<el-table-column prop="logTime" label="操作时间" width="170" />
			<el-table-column label="操作" width="150" align="center" fixed="right">
				<template #default="scope">
					<el-link type="danger" underline="never" class="pr6 pl6" @click="deleteRow(scope.row)" v-auth="'system:logger:delete'">删除</el-link>
					<el-link type="success" underline="never" class="pr6 pl6" @click="openDialogCRU(scope.row)">详情</el-link>
				</template>
			</el-table-column>
		</el-table>

		<!-- 分页 -->
		<div class="pagination-container mt15">
			<el-pagination
				v-model:current-page="state.page"
				v-model:page-size="state.size"
				:page-sizes="[10, 20, 50, 100]"
				:total="state.total"
				layout="total, sizes, prev, pager, next, jumper"
				@size-change="handleSizeChange"
				@current-change="handleCurrentChange"
			/>
		</div>
	</div>
</template>
<script setup lang="ts" name="OperationLogTab">
import { reactive, ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useLoggerApi } from '@/api/system/logger/index';

// 日志级别下拉数据（与后端 LogLevelEnum 保持一致）
const logLevelData = [
	{ value: 0, label: 'DEBUG' },
	{ value: 1, label: 'INFO' },
	{ value: 2, label: 'WARN' },
	{ value: 3, label: 'ERROR' },
	{ value: 4, label: 'FATAL' },
];

const operTypeData = ref<SelectOptionType[]>([]);

const searchForm = reactive({
	modul: '',
	userName: '',
	type: '',
	logLevel: '',
});

const state = reactive({
	loading: false,
	page: 1,
	size: 10,
	total: 0,
});

const tableData = ref<any[]>([]);

// 获取操作日志列表（固定查询业务操作类型=2）
const getTableData = async () => {
	state.loading = true;
	try {
		const res = await useLoggerApi().list({
			pageNo: state.page,
			pageSize: state.size,
			...searchForm,
			logTypes: [2],
		});
		if (res.code === 200 || res.success) {
			tableData.value = res.data.records || [];
			state.total = res.data.total || 0;
		}
	} catch (error) {
		console.error('获取操作日志失败:', error);
	} finally {
		state.loading = false;
	}
};

// 查询（重置页码）
const searchTable = () => {
	state.page = 1;
	getTableData();
};

// 重置
const resetTable = () => {
	searchForm.modul = '';
	searchForm.userName = '';
	searchForm.type = '';
	searchForm.logLevel = '';
	state.page = 1;
	getTableData();
};

const handleSizeChange = (val: number) => {
	state.size = val;
	getTableData();
};

const handleCurrentChange = (val: number) => {
	state.page = val;
	getTableData();
};

const deleteRow = (row: any) => {
	ElMessageBox.confirm('此操作将永久删除该日志, 是否继续?', '提示', {
		confirmButtonText: '删除',
		cancelButtonText: '取消',
		type: 'warning',
	})
		.then(() => {
			useLoggerApi()
				.delete(row.id)
				.then(() => {
					ElMessage.success('删除成功');
					getTableData();
				});
		})
		.catch(() => {});
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

const openDialogCRU = (row?: any): void => {
	emit('openDialog', row);
};

const emit = defineEmits(['refresh', 'openDialog']);

// 父组件通过 ref 调用
const handleSearch = () => searchTable();
defineExpose({ handleSearch });

// 初始化：加载枚举字典与列表数据
onMounted(async () => {
	try {
		const res = await useLoggerApi().enums();
		if (res.code === 200 || res.success) {
			operTypeData.value = res.data.operType || [];
		}
	} catch (error) {
		console.error('获取日志枚举失败:', error);
	}
	searchTable();
});
</script>
<style scoped lang="scss">
.page-container {
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}
</style>
