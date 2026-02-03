<template>
	<YsDialog
		:title="'选择用户添加到角色[' + (props.roleName || '') + ']'"
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
			:auto-load="false"
			:events="tableEvents"
			:show-selection-alert="true"
		>
			<!-- 页面头部区域 -->
			<template #page-header>
				<div class="search-area">
					<el-form :model="searchForm" :inline="true" label-width="80px">
						<el-input v-model="searchForm.keyword" placeholder="请输入用户名称或登录名" clearable class="w-240 mr20" @keyup.enter="searchTable" />
						<el-input v-model="searchForm.phone" placeholder="请输入手机号码" clearable class="w-180 mr20" @keyup.enter="searchTable" />
						<el-button type="primary" @click="searchTable">
							<i class="ri-search-line"></i>
							查询
						</el-button>
						<el-button @click="resetTable">
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
			<el-button type="primary" @click="handleConfirm" size="default" :disabled="getTableSelectedCount() === 0" :loading="saving">
				确认添加 ({{ getTableSelectedCount() }})
			</el-button>
		</template>
	</YsDialog>
</template>

<script setup lang="ts" name="RoleUserSelectDialog">
import { defineAsyncComponent, reactive, ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { useUserApi } from '@/api/system/user';

// 引入组件
const YsDialog = defineAsyncComponent(() => import('@/components/YsDialog/index.vue'));

// Props 定义
const props = defineProps<{
	roleId: string | number;
	roleName?: string;
}>();

// Emits 定义
const emit = defineEmits<{
	confirm: [users: any[]];
	close: [];
}>();

// 响应式数据
const tableRef = ref();
const saving = ref(false);

// 对话框状态
const state = reactive({
	dialog: {
		isShowDialog: false,
	},
});

// 搜索表单
const searchForm = reactive({
	keyword: '',
	phone: '',
	excludeRoleId: '', // 排除的角色ID
});

// 表格配置
const tableOptions = reactive<any>({
	columns: [
		{ type: 'checkbox', width: 55, align: 'center' },
		{ title: '用户名称', field: 'userName', minWidth: 120 },
		{ title: '登录名', field: 'loginName', minWidth: 120 },
		{ title: '真实姓名', field: 'realName', minWidth: 120 },
		{ title: '手机号码', field: 'phone', minWidth: 140 },
		{ title: '邮箱', field: 'email', minWidth: 180, showOverflowTooltip: true },
		{ title: '部门', field: 'deptName', minWidth: 140 },
		{ title: '状态', field: 'status', width: 80, align: 'center', slots: { default: 'status' } },
	],
	checkboxConfig: {
		reserve: true,
		checkMethod: ({ row }: any) => {
			// 排除已选择的用户（具有当前角色的用户）
			return !row.roleIds?.includes(props.roleId);
		},
	},
});

// 表格事件
const tableEvents = {};

// 打开对话框
const openDialog = () => {
	state.dialog.isShowDialog = true;
	// 设置排除的角色ID
	console.log('props.roleId', props.roleId);
	searchForm.excludeRoleId = props.roleId as string;
	// 延迟加载数据，确保表格已渲染
	setTimeout(() => {
		searchTable();
	}, 100);
};

// 关闭对话框
const closeDialog = () => {
	state.dialog.isShowDialog = false;
	// 清空表格选中状态
	tableRef.value?.clearSelection();
	emit('close');
};

// 搜索方法
const searchTable = (resetPage = true) => {
	tableRef.value?.search(resetPage, {
		keyword: searchForm.keyword,
		phone: searchForm.phone,
		excludeRoleId: searchForm.excludeRoleId,
	});
};

// 重置方法
const resetTable = () => {
	// 重置表单
	searchForm.keyword = '';
	searchForm.phone = '';
	// 重新设置排除的角色ID
	searchForm.excludeRoleId = props.roleId as string;
	// 重置表格
	tableRef.value?.reset();
};

// 确认添加用户
const handleConfirm = async () => {
	const selectedData = tableRef.value?.getAllSelectedData() || [];
	if (selectedData.length === 0) {
		ElMessage.warning('请选择要添加的用户');
		return;
	}

	saving.value = true;
	try {
		// 获取用户ID列表
		const userIds = selectedData.map((user: any) => user.id);
		// 调用添加用户到角色的API
		await useUserApi().addRoleUsers({
			roleId: props.roleId,
			userIds: userIds,
		});
		ElMessage.success(`成功添加 ${selectedData.length} 名用户到角色`);
		emit('confirm', selectedData);
		closeDialog();
	} catch (error) {
		console.error('添加用户失败:', error);
		ElMessage.error('添加用户失败');
	} finally {
		saving.value = false;
	}
};

// 监听角色ID变化
watch(
	() => props.roleId,
	(newRoleId) => {
		if (newRoleId) {
			searchForm.excludeRoleId = newRoleId as string;
			// 刷新表格配置
			tableOptions.checkboxConfig.checkMethod = ({ row }: any) => {
				return !row.roleIds?.includes(newRoleId);
			};
		}
	},
	{ immediate: true }
);

// 暴露方法给父组件
defineExpose({
	openDialog,
});

// 获取表格选中数量
const getTableSelectedCount = () => {
	return tableRef.value?.getAllSelectedData()?.length || 0;
};
</script>

<style scoped lang="scss">
.search-area {
	margin-bottom: 15px;
	padding: 20px;
	background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
	border-radius: 12px;
	border: 1px solid var(--el-border-color-lighter);
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

	.el-form {
		margin-bottom: 0;
	}

	.w-240 {
		width: 240px;
	}

	.w-180 {
		width: 180px;
	}

	.mr20 {
		margin-right: 20px;
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

	:deep(.el-button) {
		border-radius: 8px;
		transition: all 0.3s ease;

		&:hover {
			transform: translateY(-1px);
			box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
		}
	}
}

// 响应式设计
@media (max-width: 768px) {
	.search-area {
		:deep(.el-form-item) {
			margin-bottom: 15px;
		}
	}
}
</style>
