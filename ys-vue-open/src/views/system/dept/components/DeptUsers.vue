<template>
	<div class="user-container">
		<YsTable
			ref="tableRef"
			:request-fn="useUserApi().list"
			:options="tableOptions"
			:events="tableEvents"
			:query-params="searchForm"
			:show-pagination="true"
			:auto-load="false"
			:show-selection-alert="true"
			@success="handleSuccess"
		>
			<!-- 页面头部插槽 -->
			<template #page-header>
				<!-- 搜索区域 -->
				<div class="search-container">
					<el-form :model="searchForm" label-width="90px">
						<el-input v-model="searchForm.userName" class="w-180 mr20" placeholder="请输入用户名称" clearable />
						<el-input v-model="searchForm.phone" class="w-180 mr20" placeholder="请输入手机号码" clearable />
						<el-button type="primary" class="ml10" @click="searchTable(true)">
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

			<!-- 状态列插槽 -->
			<template #status="{ row }">
				<el-tag :type="row.status === 1 ? 'success' : 'danger'">
					{{ row.status === 1 ? '启用' : '禁用' }}
				</el-tag>
			</template>

			<!-- 操作列插槽 -->
			<template #action="{ row }">
				<el-link type="primary" underline="never" class="pr6 pl6" @click="handleEditUser(row)" v-auth="'system:dept:edit'">编辑</el-link>
				<el-link type="danger" underline="never" class="pr6 pl6" @click="handleRemoveUser(row.id)" v-auth="'system:dept:delete'"> 移除 </el-link>
			</template>
		</YsTable>

		<!-- 用户操作弹窗 -->
		<UserDialog ref="userDialogRef" @refresh="refreshTable" />

		<!-- 用户选择弹窗 -->
		<UserSelectDialog
			ref="userSelectDialogRef"
			:depart-id="departId"
			:depart-name="departName"
			@confirm="handleUserSelectConfirm"
			@close="handleUserSelectClose"
		/>
	</div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, defineAsyncComponent, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserApi } from '@/api/system/user';

// 引入组件
const UserDialog = defineAsyncComponent(() => import('@/views/system/user/dialog.vue'));
const UserSelectDialog = defineAsyncComponent(() => import('./UserSelectDialog.vue'));

// Props 定义
const props = defineProps<{
	departId: string;
	departName: string;
}>();

// 响应式数据
const tableRef = ref();
const userDialogRef = ref();
const userSelectDialogRef = ref();
const selectedUsers = ref<any[]>([]);

// 搜索表单
const searchForm = reactive({
	userName: '',
	phone: '',
	status: undefined as number | undefined,
	departId: props.departId,
});

// 表格配置
const tableOptions = reactive<any>({
	columns: [
		{ type: 'checkbox', width: 60, align: 'center', fixed: 'left' },
		{ type: 'seq', width: 80, align: 'center', title: '序号' },
		{ field: 'userName', title: '用户名称', minWidth: 120 },
		{ field: 'realName', title: '真实姓名', minWidth: 120 },
		{ field: 'phone', title: '手机号码', minWidth: 140 },
		{ field: 'email', title: '邮箱', minWidth: 180, showOverflow: true },
		{ field: 'position', title: '职位', minWidth: 120 },
		{ field: 'status', title: '状态', width: 80, align: 'center', slots: { default: 'status' } },
		{ field: 'createTime', title: '创建时间', width: 160 },
		{ field: 'action', title: '操作', width: 100, align: 'center', slots: { default: 'action' }, fixed: 'right' },
	],
	// 工具栏配置
	toolbarConfig: {
		size: 'small',
		refresh: true,
		zoom: true,
		custom: true,
		buttons: [
			{ name: '新增用户', code: 'addUser', status: 'primary', icon: 'ri-add-line' },
			{ name: '添加已有用户', code: 'addExistingUser', status: 'success', icon: 'ri-add-line' },
			{ name: '批量移除', code: 'removeUsersFromDept', status: 'danger', icon: 'ri-delete-bin-6-line' },
		],
	},
});

// 表格事件
const tableEvents = {
	toolbarButtonClick(params: any) {
		switch (params.code) {
			case 'addUser':
				handleAddUser();
				break;
			case 'addExistingUser':
				handleAddExistingUser();
				break;
			case 'removeUsersFromDept':
				const userIds = selectedUsers.value.map((user) => user.id);
				if (selectedUsers.value.length === 0) {
					ElMessage.warning('请选择要移除的用户');
					break;
				}
				handleRemoveUser(userIds);
				break;
		}
	},
	checkboxChange(params: any) {
		selectedUsers.value = params.records || [];
	},
};

// 搜索方法
const searchTable = (resetPage = true) => {
	tableRef.value?.search(resetPage);
};

// 重置方法
const resetTable = () => {
	tableRef.value?.reset();
};

// 刷新表格
const refreshTable = () => {
	tableRef.value?.refresh();
};

// 数据加载成功回调
const handleSuccess = () => {
	// 可以在这里处理数据加载成功的逻辑
	selectedUsers.value = [];
};

// 新增用户
const handleAddUser = () => {
	userDialogRef.value?.openDialog('add', { deptId: props.departId });
};

// 添加已有用户
const handleAddExistingUser = () => {
	userSelectDialogRef.value?.openDialog();
};

// 用户选择确认回调
const handleUserSelectConfirm = () => {
	refreshTable();
};

// 用户选择关闭回调
const handleUserSelectClose = () => {
	// 可以在这里处理关闭后的逻辑
};

// 编辑用户
const handleEditUser = (row: any) => {
	userDialogRef.value?.openDialog('edit', row);
};

// 批量移除
const handleRemoveUser = (userIds: any) => {
	ElMessageBox.confirm(`确定要移除指定用户吗？此操作不可撤销！`, '移除确认', {
		confirmButtonText: '确定移除',
		cancelButtonText: '取消',
		type: 'warning',
		dangerouslyUseHTMLString: true,
	})
		.then(async () => {
			try {
				const deptId = props.departId;
				const params = {
					userIds: userIds,
					deptId: deptId,
				};
				// 批量移除API调用
				useUserApi().removeUsersFromDept(params);
				ElMessage.success('批量移除成功');
				refreshTable();
			} catch (error) {
				console.error('批量移除失败:', error);
				ElMessage.error('批量移除失败');
			}
		})
		.catch(() => {
			// 用户取消移除
		});
};

// 监听部门ID变化
watch(
	() => props.departId,
	(newVal) => {
		if (newVal) {
			searchForm.departId = newVal;
			nextTick(() => {
				refreshTable();
			});
		}
	},
	{ immediate: true }
);

// 监听查询参数变化
watch(
	() => searchForm,
	(newForm) => {
		if (tableRef.value) {
			tableRef.value.updateQueryParams(newForm);
		}
	},
	{ deep: true }
);
</script>

<style scoped lang="scss">
.user-container {
	height: 100%;
}
.dept-users {
	padding: 20px;

	.users-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20px;
		padding: 15px 20px;
		background-color: #f5f7fa;
		border-radius: 4px;

		.header-info {
			h4 {
				margin: 0;
				font-size: 18px;
				font-weight: 600;
			}

			.user-count {
				margin-left: 10px;
				color: #606266;
				font-size: 14px;
			}
		}

		.header-actions {
			display: flex;
			gap: 10px;
		}
	}

	.search-area {
		margin-bottom: 20px;
		padding: 20px;
		background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
		border-radius: 12px;
		border: 1px solid var(--el-border-color-lighter);
		box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

		.el-form {
			margin-bottom: 0;
		}

		.w-180 {
			width: 180px;
		}

		:deep(.el-form-item) {
			margin-bottom: 15px;
			margin-right: 20px;
		}

		:deep(.el-input__wrapper) {
			border-radius: 8px;
			border: 1px solid var(--el-border-color-light);
			transition: all 0.3s ease;

			&:hover {
				border-color: var(--el-color-primary-light-3);
				box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
			}

			&.is-focus {
				border-color: var(--el-color-primary);
				box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
			}
		}

		:deep(.el-select) {
			.el-input__wrapper {
				border-radius: 8px;
			}
		}

		:deep(.el-button) {
			border-radius: 8px;
			transition: all 0.3s ease;

			&:hover {
				transform: translateY(-1px);
				box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
			}
		}
	}

	.table-container {
		margin-bottom: 20px;
	}

	.pagination-container {
		display: flex;
		justify-content: flex-end;
	}
}

// 响应式设计
@media (max-width: 768px) {
	.dept-users {
		.users-header {
			flex-direction: column;
			align-items: flex-start;
			gap: 15px;

			.header-actions {
				width: 100%;
				justify-content: flex-end;
			}
		}

		.search-area {
			:deep(.el-form-item) {
				margin-bottom: 15px;
			}
		}
	}
}
</style>
