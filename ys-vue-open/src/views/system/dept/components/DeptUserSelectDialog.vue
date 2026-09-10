<!--
 * 部门人员选择弹窗组件 - 使用YsTable
 * @author: Eric
 * @date: 2025-03-13
-->
<template>
	<YsDialog
		:title="state.title"
		v-model="state.dialog.isShowDialog"
		@close="closeDialog"
		width="900px"
		:close-on-click-modal="false"
		:close-on-press-escape="false"
		:append-to-body="true"
	>
		<!-- 使用YsTable组件 -->
		<YsTable
			ref="tableRef"
			:request-fn="useUserApi().list"
			:options="tableOptions"
			:query-params="searchForm"
			:show-pagination="true"
			@request-success="handleSuccess"
		>
			<!-- 页面头部区域 搜索表单 -->
			<template #page-header>
				<div class="search-area">
					<el-form :model="searchForm" :inline="true" label-width="80px">
						<el-input
							v-model="searchForm.userName"
							placeholder="请输入用户名称"
							clearable
							class="w-180 mr20"
							@keyup.enter="searchTable"
						/>
						<el-input
							v-model="searchForm.realName"
							placeholder="请输入真实姓名"
							clearable
							class="w-180 mr20"
							@keyup.enter="searchTable"
						/>
						<el-button type="primary" @click="searchTable">
							<i class="ri-search-line"></i>
							查询
						</el-button>
						<el-button type="default" @click="resetTable">
							<i class="ri-reset-left-line"></i>
							重置
						</el-button>
					</el-form>
				</div>
			</template>

			<!-- 单选列 -->
			<template #radio="scope">
				<el-radio
					:label="scope.row.id"
					v-model="selectedUserId"
					@change="() => handleSelectUser(scope.row)"
				>
					{{ '' }}
				</el-radio>
			</template>

			<!-- 状态列 -->
			<template #status="scope">
				<el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
					{{ scope.row.status === 1 ? '启用' : '禁用' }}
				</el-tag>
			</template>
		</YsTable>

		<template #footer>
			<el-button @click="closeDialog" size="default">取 消</el-button>
			<el-button
				type="primary"
				@click="handleConfirm"
				size="default"
				:disabled="!selectedUserId"
				:loading="saving"
			>
				确 认
			</el-button>
		</template>
	</YsDialog>
</template>

<script setup lang="ts" name="DeptUserSelectDialog">
import { reactive, ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { useUserApi } from '@/api/system/user';

// Props 定义
const props = defineProps<{
	deptId?: string;
	deptName?: string;
}>();

// Emits 定义
const emit = defineEmits<{
	confirm: [user: { id: string; name: string }];
	close: [];
}>();

// 响应式数据
const tableRef = ref();
const saving = ref(false);
const selectedUserId = ref<string>('');
const selectedUser = ref<any>(null);

// 对话框状态
const state = reactive({
	dialog: {
		isShowDialog: false,
	},
	title: '选择人员',
});

// 搜索表单
const searchForm = reactive({
	userName: '',
	realName: '',
	departId: '',
});

// 表格配置
const tableOptions = reactive<any>({
	columns: [
		{
			title: '选择',
			field: 'radio',
			width: 60,
			align: 'center',
			fixed: 'left',
			slots: { default: 'radio' },
		},
		{
			type: 'seq',
			width: 60,
			align: 'center',
			fixed: 'left',
		},
		{
			title: '用户名称',
			field: 'userName',
			minWidth: 120,
		},
		{
			title: '真实姓名',
			field: 'realName',
			minWidth: 120,
		},
		{
			title: '手机号',
			field: 'phone',
			minWidth: 130,
		},
		{
			title: '岗位',
			field: 'positionName',
			minWidth: 120,
		},
		{
			title: '状态',
			field: 'status',
			width: 80,
			align: 'center',
			slots: { default: 'status' },
		},
	],
	data: [],
	stripe: true,
	border: true,
	highlightHoverRow: true,
	loading: false
});

/**
 * 打开对话框
 */
const openDialog = () => {
	// 重置状态
	selectedUserId.value = '';
	selectedUser.value = null;
	searchForm.userName = '';
	searchForm.realName = '';
	// 如果有部门ID，则设置部门筛选
	if (props.deptId) {
		searchForm.departId = props.deptId;
	} else {
		searchForm.departId = '';
	}

	// 设置标题
	state.title = props.deptName ? `选择人员 - ${props.deptName}` : '选择人员';
	state.dialog.isShowDialog = true;

	// 加载数据
	setTimeout(() => {
		searchTable();
	}, 0);
};

/**
 * 关闭对话框
 */
const closeDialog = () => {
	state.dialog.isShowDialog = false;
	selectedUserId.value = '';
	selectedUser.value = null;
	emit('close');
};

/**
 * 搜索方法
 */
const searchTable = (resetPage = true) => {
	tableRef.value?.search(resetPage);
};

/**
 * 重置方法
 */
const resetTable = () => {
	searchForm.userName = '';
	searchForm.realName = '';
	if (props.deptId) {
		searchForm.departId = props.deptId;
	} else {
		searchForm.departId = '';
	}
	tableRef.value?.reset();
};

/**
 * 数据加载成功回调
 */
const handleSuccess = (res: any) => {
	// 可以在这里处理数据加载成功后的逻辑
};

/**
 * 选择用户
 * @param user 用户信息
 */
const handleSelectUser = (user: any) => {
	selectedUserId.value = user.id;
	selectedUser.value = user;
};

/**
 * 确认选择
 */
const handleConfirm = async () => {
	if (!selectedUser.value) {
		ElMessage.warning('请选择人员');
		return;
	}

	saving.value = true;
	try {
		// 返回选中的人员信息
		emit('confirm', {
			id: selectedUser.value.id,
			name: selectedUser.value.realName || selectedUser.value.userName,
		});
		closeDialog();
	} catch (error) {
		console.error('确认选择失败:', error);
		ElMessage.error('确认选择失败');
	} finally {
		saving.value = false;
	}
};

// 监听部门ID变化
watch(
	() => props.deptId,
	(newDeptId) => {
		if (state.dialog.isShowDialog) {
			if (newDeptId) {
				searchForm.departId = newDeptId;
			} else {
				searchForm.departId = '';
			}
			searchTable();
		}
	}
);

// 暴露方法给父组件
defineExpose({
	openDialog,
});
</script>

<style scoped lang="scss">
:deep(.el-radio__label) {
	display: none;
}
</style>
