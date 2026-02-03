<template>
	<YsDialog
		:title="state.title"
		v-model="state.dialog.isShowDialog"
		@close="closeDialog"
		width="80%"
		:close-on-click-modal="false"
		:close-on-press-escape="false"
		:append-to-body="true"
	>
		<YsTable
			ref="tableRef"
			:request-fn="useUserApi().list"
			:options="tableOptions"
			:query-params="searchForm"
			:show-pagination="true"
			:events="tableEvents"
			:show-selection-alert="true"
			:auto-load="false"
		>
			<!-- 页面头部区域 -->
			<template #page-header>
				<div class="search-area">
					<el-form :model="searchForm" :inline="true" label-width="80px">
						<el-input v-model="searchForm.userName" placeholder="请输入用户名称" clearable class="w-180 mr20" @keyup.enter="searchTable" />
						<el-input v-model="searchForm.realName" placeholder="请输入真实姓名" clearable class="w-180 mr20" @keyup.enter="searchTable" />
						<el-input v-model="searchForm.phone" placeholder="请输入手机号码" clearable class="w-180 mr20" @keyup.enter="searchTable" />
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
			</template>

			<!-- 状态列 -->
			<template #status="scope">
				<el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
					{{ scope.row.status === 1 ? '启用' : '禁用' }}
				</el-tag>
			</template>
		</YsTable>

		<template #footer>
			<el-button @click="closeDialog" size="default">取 消</el-button>
			<el-button type="primary" @click="handleConfirm" size="default" :disabled="selectedUsers.length === 0" :loading="saving">
				确认添加 ({{ selectedUsers.length }})
			</el-button>
		</template>
	</YsDialog>
</template>

<script setup lang="ts" name="UserSelectDialog">
import { reactive, ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { useUserApi } from '@/api/system/user';

// Props 定义
const props = defineProps<{
	departId: string;
	departName?: string;
}>();

// Emits 定义
const emit = defineEmits<{
	confirm: [users: any[]];
	close: [];
}>();

// 响应式数据
const tableRef = ref();
const saving = ref(false);
const selectedUsers = ref<any[]>([]);

// 对话框状态
const state = reactive({
	dialog: {
		isShowDialog: false,
	},
	title: '选择用户添加到 <span style="font-weight: bold;">' + props.departName + '</span>',
});

// 搜索表单
const searchForm = reactive({
	userName: '',
	realName: '',
	phone: '',
	excludeDeptId: '', // 排除当前部门的用户
});

// 表格配置
const tableOptions = reactive<any>({
	columns: [
		{ type: 'checkbox', width: 55, align: 'center' },
		{ title: '用户名称', field: 'userName', minWidth: 120 },
		{ title: '工号', field: 'code', minWidth: 120 },
		{ title: '手机号码', field: 'phone', minWidth: 140 },
		{ title: '邮箱', field: 'email', minWidth: 180, showOverflowTooltip: true },
		{ title: '职位', field: 'position', minWidth: 120 },
		{ title: '当前部门', field: 'departName', minWidth: 140 },
		{ title: '状态', field: 'status', width: 80, align: 'center', slots: { default: 'status' } },
	],
	checkboxConfig: {
		reserve: true,
	},
});

// 表格事件
const tableEvents = {
	// 处理表格选择变化
	checkboxChange: ({ records }: any) => {
		selectedUsers.value = tableRef.value?.getAllSelectedData();
	},
	checkboxAll: ({ records }: any) => {
		selectedUsers.value = tableRef.value?.getAllSelectedData();
	},
};

// 打开对话框
const openDialog = () => {
	state.dialog.isShowDialog = true;
	// 设置排除部门ID
	searchForm.excludeDeptId = props.departId;
	// 延迟加载数据，确保表格已渲染
	setTimeout(() => {
		searchTable();
	}, 100);
};

// 关闭对话框
const closeDialog = () => {
	state.dialog.isShowDialog = false;
	selectedUsers.value = [];
	emit('close');
};

// 搜索方法
const searchTable = (resetPage = true) => {
	// 更新排除部门ID
	searchForm.excludeDeptId = props.departId;
	tableRef.value?.search(resetPage ? {} : undefined);
};

// 重置方法
const resetTable = () => {
	// 重置表格
	tableRef.value?.reset();
	// 重新设置排除部门ID
	searchForm.excludeDeptId = props.departId;
};

// 确认添加用户
const handleConfirm = async () => {
	if (selectedUsers.value.length === 0) {
		ElMessage.warning('请选择要添加的用户');
		return;
	}

	saving.value = true;
	try {
		// 调用添加用户到部门的API
		const userIds = selectedUsers.value.map((user) => user.id);
		const res = await useUserApi().assignUsersToDept({
			deptId: props.departId,
			userIds: userIds,
		});
		if (res.code === 200) {
			ElMessage.success(`成功添加 ${selectedUsers.value.length} 名用户到部门`);
			emit('confirm', selectedUsers.value);
			closeDialog();
		} else {
			ElMessage.error(res.msg);
		}
	} catch (error) {
		console.error('添加用户失败:', error);
		ElMessage.error('添加用户失败');
	} finally {
		saving.value = false;
	}
};

// 监听部门ID变化
watch(
	() => props.departId,
	(newDeptId) => {
		if (newDeptId) {
			searchForm.excludeDeptId = newDeptId;
		}
	}
);

// 暴露方法给父组件
defineExpose({
	openDialog,
});
</script>

<style scoped lang="scss">
.user-select-dialog {
	:deep(.el-dialog__body) {
		padding: 15px 20px 0 20px !important;
	}

	.search-form {
		margin-bottom: 15px;
		padding: 15px 0;
		background-color: var(--el-fill-color-light);
		border-radius: 4px;

		.el-form-item {
			margin-bottom: 0;
		}
	}

	.table-container {
		.status-enable {
			color: var(--el-color-success);
		}

		.status-disable {
			color: var(--el-color-danger);
		}
	}

	// 适配 YsTable 样式
	:deep(.ys-table-container) {
		.page-header {
			margin-bottom: 15px;
			padding: 15px 0;
			background-color: var(--el-fill-color-light);
			border-radius: 4px;

			.el-form-item {
				margin-bottom: 0;
			}
		}
	}
}

// 响应式设计
@media (max-width: 768px) {
	.user-select-dialog {
		:deep(.ys-table-container) {
			.page-header {
				padding: 10px 0;
			}
		}
	}
}
</style>
