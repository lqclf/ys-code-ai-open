<template>
	<YsDialog
		:title="'角色[' + (roleNameRef || '') + ']用户管理'"
		v-model="state.dialog.isShowDialog"
		@close="closeDialog"
		width="80%"
		:close-on-click-modal="false"
		:close-on-press-escape="false"
		:append-to-body="true"
	>
		<div class="role-users">
			<YsTable
				ref="tableRef"
				:request-fn="getUserListByRole"
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
					<div class="page-header">
						<el-form :model="searchForm" label-width="90px">
							<el-input v-model="searchForm.userName" class="w-180 mr20" placeholder="请输入用户名称" clearable />
							<el-input v-model="searchForm.phone" class="w-180 mr20" placeholder="请输入手机号码" clearable />
							<el-select v-model="searchForm.status" class="w-180 mr20" placeholder="请选择状态" clearable>
								<el-option label="启用" :value="1" />
								<el-option label="禁用" :value="0" />
							</el-select>
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

				<!-- 状态 -->
				<template #status="{ row }">
					<el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
				</template>

				<!-- 操作列插槽 -->
				<template #action="{ row }">
					<el-link type="primary" underline="never" class="pr6 pl6" @click="handleViewUser(row)">详情</el-link>
					<el-link type="danger" underline="never" class="pr6 pl6" @click="handleRemoveUser([row.id])">移除</el-link>
				</template>
			</YsTable>

			<!-- 用户操作弹窗 -->
			<UserDialog ref="userDialogRef" @refresh="refreshTable" />

			<!-- 用户选择弹窗 -->
			<UserSelectDialog
				ref="userSelectDialogRef"
				:role-id="searchForm.roleId"
				:role-name="roleNameRef"
				@confirm="handleUserSelectConfirm"
				@close="handleUserSelectClose"
			/>
		</div>

		<template #footer>
			<el-button @click="closeDialog" size="default">关 闭</el-button>
		</template>
	</YsDialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, defineAsyncComponent, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserApi } from '@/api/system/user';

// 引入组件
const UserDialog = defineAsyncComponent(() => import('@/views/system/user/dialog.vue'));
const UserSelectDialog = defineAsyncComponent(() => import('./userSelectDialog.vue'));

// Props 定义
const props = withDefaults(
	defineProps<{
		roleId?: string;
		roleName?: string;
	}>(),
	{
		roleId: '',
		roleName: '',
	}
);

// 弹窗状态数据
const state = reactive({
	dialog: {
		isShowDialog: false,
	},
});

// 响应式变量存储roleName
const roleNameRef = ref(props.roleName);

// 响应式数据
const tableRef = ref();
const userDialogRef = ref();
const userSelectDialogRef = ref();
const selectedUsers = ref<any[]>([]);
const isBatchRunVisible = ref(false);

// 搜索表单
const searchForm = reactive({
	userName: '',
	phone: '',
	status: undefined as number | undefined,
	roleId: props.roleId,
});

// 表格配置
const tableOptions = reactive<any>({
	columns: [
		{ type: 'checkbox', width: 60, align: 'center', fixed: 'left' },
		{ type: 'seq', width: 80, align: 'center', title: '序号' },
		{ field: 'userName', title: '用户名称', minWidth: 120 },
		{ field: 'realName', title: '真实姓名', minWidth: 120 },
		{ field: 'phone', title: '手机号码', minWidth: 140 },
		{ field: 'email', title: '邮箱', minWidth: 180, showOverflowTooltip: true },
		{ field: 'position', title: '职位', minWidth: 120 },
		{ field: 'status', title: '状态', width: 80, align: 'center', slots: { default: 'status' } },
		{ field: 'createTime', title: '创建时间', width: 160 },
		{ field: 'action', title: '操作', width: 120, align: 'center', slots: { default: 'action' }, fixed: 'right' },
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
			{ name: '批量移除', code: 'removeUsersFromRole', status: 'danger', icon: 'ri-delete-bin-6-line', visible: isBatchRunVisible },
		],
	},
	checkboxConfig: {
		reserve: true,
	},
});

// 表格事件
const tableEvents = {
	// 处理工具栏按钮点击
	toolbarButtonClick(params: any) {
		switch (params.code) {
			case 'addUser':
				handleAddUser();
				break;
			case 'addExistingUser':
				handleAddExistingUser();
				break;
			case 'removeUsersFromRole':
				const userIds = selectedUsers.value.map((user) => user.id);
				if (selectedUsers.value.length === 0) {
					ElMessage.warning('请选择要移除的用户');
					break;
				}
				handleRemoveUser(userIds);
				break;
		}
	},
	// 处理表格选择变化
	checkboxChange: ({ records }: any) => {
		selectedUsers.value = tableRef.value?.getAllSelectedData();
		isBatchRunVisible.value = selectedUsers.value.length > 0;
	},
	checkboxAll: ({ records }: any) => {
		selectedUsers.value = tableRef.value?.getAllSelectedData();
		isBatchRunVisible.value = selectedUsers.value.length > 0;
	},
};

// 自定义方法：根据角色ID获取用户列表
const getUserListByRole = (params: any) => {
	// 这里需要调用后端接口获取角色下的用户列表
	// 由于API文档中没有提供相关接口，我们暂时使用用户列表接口并添加roleId参数
	return useUserApi().list({ ...params, roleId: searchForm.roleId });
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
	searchTable();
};

// 数据加载成功回调
const handleSuccess = (res: any) => {
	// 可以在这里处理数据加载成功的逻辑
	selectedUsers.value = [];
};

// 新增用户
const handleAddUser = () => {
	userDialogRef.value?.openDialog('add', { roleIds: [searchForm.roleId] });
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

// 打开弹窗方法
const openDialog = (row: any) => {
	// 设置角色ID和角色名称
	// 注意：props是只读的，不能直接修改，我们需要通过其他方式传递数据
	// 这里我们直接使用传入的row数据来设置搜索表单

	// 更新搜索表单中的roleId
	searchForm.roleId = row.id;

	// 更新roleNameRef
	roleNameRef.value = row.roleName || row.name || '';

	// 显示弹窗
	state.dialog.isShowDialog = true;

	// 刷新表格
	nextTick(() => {
		refreshTable();
	});
};

// 关闭弹窗方法
const closeDialog = () => {
	state.dialog.isShowDialog = false;
};

// 暴露方法给父组件
defineExpose({
	openDialog,
	closeDialog,
});

// 查看用户详情
const handleViewUser = (row: any) => {
	userDialogRef.value?.openDialog('view', row);
};

// 批量移除
const handleRemoveUser = (userIds: any) => {
	ElMessageBox.confirm(`确定要从角色[${roleNameRef.value}]中移除指定用户吗？此操作不可撤销！`, '移除确认', {
		confirmButtonText: '确定移除',
		cancelButtonText: '取消',
		type: 'warning',
		dangerouslyUseHTMLString: true,
	})
		.then(async () => {
			try {
				// 调用从角色批量移除用户的API
				const params = {
					userIds: userIds,
					roleId: searchForm.roleId,
				};
				const res = await useUserApi().removeRoleUsers(params);
				if (res.code === 200) {
					ElMessage.success('批量移除成功');
					refreshTable();
				} else {
					ElMessage.error(res.msg || '批量移除失败');
				}
			} catch (error) {
				console.error('批量移除失败:', error);
				ElMessage.error('批量移除失败');
			}
		})
		.catch(() => {
			// 用户取消移除
		});
};

// 监听角色ID变化
watch(
	() => props.roleId,
	(newVal) => {
		if (newVal) {
			searchForm.roleId = newVal;
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

<style scoped lang="scss"></style>
