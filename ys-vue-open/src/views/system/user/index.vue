<template>
	<div class="page-container">
		<el-card>
			<YsTable
				ref="tableRef"
				:request-fn="useUserApi().list"
				:options="tableOptions"
				:query-params="searchForm"
				:show-pagination="true"
				:show-selection-alert="true"
				:events="tableEvents"
			>
				<!-- 页面头部区域 -->
				<template #page-header>
					<div class="page-header">
						<el-form :model="searchForm" ref="searchFormRef" label-width="90px">
							<el-input placeholder="请输入登录名" class="w-180 mr20" v-model="searchForm.loginName" clearable @keyup.enter="searchTable" />
							<el-input placeholder="请输入真实姓名" class="w-180 mr20" v-model="searchForm.userName" clearable @keyup.enter="searchTable" />
							<el-select class="m-2 w-180 mr20" v-model="searchForm.roleId" placeholder="请选择角色" clearable>
								<el-option v-for="item in state.roleList" :key="item.id" :label="item.name" :value="item.id" />
							</el-select>
							<el-select class="m-2 w-180 mr20" v-model="searchForm.status" placeholder="请选择状态" clearable>
								<el-option label="正常" :value="1" />
								<el-option label="禁用" :value="0" />
							</el-select>
							<el-select class="m-2 w-180 mr20" v-model="searchForm.userType" placeholder="请选择用户类型" clearable>
								<el-option label="游客" :value="0" />
								<el-option label="普通用户" :value="1" />
								<el-option label="会员" :value="2" />
								<el-option label="管理员" :value="3" />
							</el-select>
							<el-tree-select
								class="w-280 mr20"
								v-model="searchForm.departId"
								:data="state.deptData"
								check-strictly
								:render-after-expand="false"
								:default-expanded-keys="expandedKeys"
								accordion
								clearable
								placeholder="请选择部门"
							>
								<template #default="{ node, data }">
									<div class="tree-node">
										<el-icon class="tree-icon">
											<OfficeBuilding v-if="data.data.levelType === 1" />
											<School v-else-if="data.data.levelType === 2" />
											<Shop v-else />
										</el-icon>
										<span class="tree-label">{{ node.label }}</span>
									</div>
								</template>
							</el-tree-select>
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

				<!-- 用户类型列 -->
				<template #userType="scope">
					<el-tag v-if="scope.row.userType === 0" type="info">游客</el-tag>
					<el-tag v-else-if="scope.row.userType === 1" type="primary">普通用户</el-tag>
					<el-tag v-else-if="scope.row.userType === 2" type="warning">会员</el-tag>
					<el-tag v-else-if="scope.row.userType === 3" type="danger">管理员</el-tag>
					<el-tag v-else type="info">未知</el-tag>
				</template>

				<!-- 角色列 -->
				<template #roleList="scope">
					<el-tag v-for="item in scope.row.roleList" :key="item.id" type="info" class="mr4 mb4">
						{{ item.name }}
					</el-tag>
				</template>

				<!-- 锁定状态列 -->
				<template #isLock="scope">
					<el-tag type="danger" v-if="scope.row.isLock === 0">已锁定</el-tag>
					<el-tag type="success" v-else>正常</el-tag>
				</template>

				<!-- 操作列 -->
				<template #action="scope">
					<div class="operation-btn-group">
						<el-link type="primary" underline="never" class="pr6 pl6" @click="openDialogCRU('edit', scope.row)" v-auth="'system:user:edit'">
							编辑
						</el-link>
						<el-link
							type="danger"
							underline="never"
							class="pr6 pl6"
							@click="onDelete(scope.row)"
							v-auth="'system:user:delete'"
							:disabled="scope.row.loginName === 'admin'"
						>
							删除
						</el-link>
						<el-link type="success" underline="never" class="pr6 pl6" @click="openDialogCRU('view', scope.row)"> 详情 </el-link>
						<el-dropdown :hide-on-click="false" v-auths="['system:user:freeze', 'system:user:resetPwd']">
							<span class="el-dropdown-link">
								<el-link type="primary" underline="never" class="pr6 pl6">
									<i class="ri-arrow-right-double-line"></i>
								</el-link>
							</span>
							<template #dropdown>
								<el-dropdown-menu>
									<div v-auth="'system:user:resetPwd'">
										<el-dropdown-item>
											<el-link type="primary" underline="never" class="pr6 pl6" @click="openPasswordDialog(scope.row)">
												密码修改
											</el-link>
										</el-dropdown-item>
									</div>
									<div v-auth="'system:user:freeze'">
										<el-dropdown-item>
											<el-link
												:type="scope.row.isLock === 1 ? 'warning' : 'success'"
												underline="never"
												class="pr6 pl6"
												@click="toggleUserStatus(scope.row)"
												:disabled="scope.row.loginName === 'admin'"
											>
												{{ scope.row.isLock === 1 ? '冻结' : '解冻' }}
											</el-link>
										</el-dropdown-item>
									</div>
								</el-dropdown-menu>
							</template>
						</el-dropdown>
					</div>
				</template>
			</YsTable>
		</el-card>

		<!-- 引入组件 -->
		<UserDialog ref="userDialogRef" @refresh="searchTable()" />
		<PasswordDialog ref="passwordDialogRef" @refresh="searchTable()" />
		<UserDetail ref="userDetailRef" @edit="handleEditFromDetail" />
		
		<!-- 导入组件（使用插槽自定义参数，hide-button隐藏自带按钮，通过工具栏调用） -->
		<YsImportExcel
			ref="importRef"
			import-type="systemUser"
			:columns="importColumns"
			button-text="导入"
			button-type="warning"
			dialog-title="用户数据导入"
			hide-button
			@success="handleImportSuccess"
			@error="handleImportError"
		>
			<!-- 自定义参数配置插槽 -->
			<template #params="{ params, validate, updateParams }">
				<div class="import-params-form">
					<el-form label-width="100px">
						<el-form-item label="选择部门">
							<el-tree-select
								class="w-full"
								:model-value="params.departId"
								@update:model-value="(val: string) => updateParams({ departId: val })"
								:data="state.deptData"
								check-strictly
								:render-after-expand="false"
								accordion
								clearable
								placeholder="请选择导入部门（可选）"
							>
								<template #default="{ node, data }">
									<div class="tree-node">
										<el-icon class="tree-icon">
											<OfficeBuilding v-if="data.data.levelType === 1" />
											<School v-else-if="data.data.levelType === 2" />
											<Shop v-else />
										</el-icon>
										<span class="tree-label">{{ node.label }}</span>
									</div>
								</template>
							</el-tree-select>
						</el-form-item>
					</el-form>
					<el-alert type="info" :closable="false" show-icon class="import-tips">
						<template #title>
							<div class="import-tips-content">
								<p>说明：</p>
								<ul>
									<li>选择部门后，导入的用户将统一分配到该部门</li>
									<li>如果导入数据中的部门名称与选择的部门不一致，将作为错误数据处理</li>
									<li>不选择部门时，将以导入数据中的部门名称匹配部门</li>
								</ul>
							</div>
						</template>
					</el-alert>
				</div>
			</template>
		</YsImportExcel>
		
		<!-- 导出组件（hide-button隐藏自带按钮，通过工具栏调用） -->
		<YsExportExcel
			ref="exportRef"
			export-type="systemUser"
			:query-params="searchForm"
			:columns="exportColumns"
			file-name="用户数据"
			button-text="导出"
			button-type="success"
			dialog-title="用户数据导出"
			hide-button
			@success="handleExportSuccess"
			@error="handleExportError"
		/>
	</div>
</template>

<script setup lang="ts" name="systemUser">
import { defineAsyncComponent, reactive, onMounted, ref, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Shop, OfficeBuilding, School } from '@element-plus/icons-vue';
import { useUserApi } from '@/api/system/user';
import { useRoleApi } from '@/api/system/role';
import { useDeptApi } from '@/api/system/dept';

// 引入组件
const UserDialog = defineAsyncComponent(() => import('@/views/system/user/dialog.vue'));
const PasswordDialog = defineAsyncComponent(() => import('@/views/system/user/passwordDialog.vue'));
const UserDetail = defineAsyncComponent(() => import('@/views/system/user/detail.vue'));

// 定义变量内容
const userDialogRef = ref();
const passwordDialogRef = ref();
const userDetailRef = ref();
const tableRef = ref();
const importRef = ref();
const exportRef = ref();

// 展开的节点keys
const expandedKeys = ref<string[]>([]);

// API 实例
const userApi = useUserApi();

// 搜索表单
const searchForm = reactive({
	loginName: '',
	userName: '',
	phone: '',
	roleId: '',
	departId: '',
	status: '',
	userType: '',
});

// 导入导出列定义
const importColumns = [
	{ field: 'loginName', title: '登录名', width: 150 },
	{ field: 'userName', title: '真实姓名', width: 150 },
	{ field: 'phone', title: '手机号码', width: 150 },
	{ field: 'email', title: '邮箱', width: 200 },
	{ field: 'departName', title: '部门名称', width: 150 },
	{ field: 'positionName', title: '岗位名称', width: 150 },
	{ field: 'userTypeStr', title: '用户类型', width: 100 },
	{ field: 'statusStr', title: '状态', width: 100 },
];
const exportColumns = [
	{ field: 'loginName', title: '登录名' },
	{ field: 'userName', title: '真实姓名' },
	{ field: 'phone', title: '手机号码' },
	{ field: 'email', title: '邮箱' },
	{ field: 'departName', title: '部门' },
	{ field: 'positionName', title: '岗位' },
	{ field: 'userTypeStr', title: '用户类型' },
	{ field: 'statusStr', title: '状态' },
];

// 状态数据
const state = reactive({
	roleList: [] as any[],
	deptData: [] as any[],
});

const tableOptions = reactive<any>({
	columns: [
		{ type: 'checkbox', width: 55, align: 'center' },
		{ type: 'seq', width: 80, align: 'center' },
		{ title: '登录名', field: 'loginName', width: 140 },
		{ title: '真实姓名', field: 'userName', width: 140 },
		{ title: '用户类型', field: 'userType', width: 100, slots: { default: 'userType' } },
		{ title: '手机号码', field: 'phone' },
		{ title: '邮箱', field: 'email' },
		{ title: '部门', field: 'departName' },
		{ title: '岗位', field: 'positionName', width: 120 },
		{ title: '角色', field: 'roleList', slots: { default: 'roleList' } },
		{ title: '状态', field: 'isLock', width: 100, slots: { default: 'isLock' } },
		{ title: '操作', field: 'action', width: 220, slots: { default: 'action' }, align: 'center' },
	],
	cellConfig: {
		height: 'auto',
	},
	// 工具栏配置
	toolbarConfig: {
		size: 'small',
		refresh: true,
		zoom: true,
		custom: true,
		buttons: [
				{ name: '新增', code: 'userAdd', status: 'success', icon: 'ri-add-line', auth: 'system:user:add' },
				{ name: '导入', code: 'userImport', status: 'warning', icon: 'ri-upload-line', auth: 'system:user:import' },
				{ name: '导出', code: 'userExport', status: 'primary', icon: 'ri-download-line', auth: 'system:user:export' },
			],
	},
});

const tableEvents = {
	toolbarButtonClick(params: any) {
		switch (params.code) {
			case 'userAdd':
				openDialogCRU('add');
				break;
			case 'userImport':
				openImportDialog();
				break;
			case 'userExport':
				openExportDialog();
				break;
		}
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

// 打开用户操作弹窗（新增/编辑/查看）
const openDialogCRU = (type: string, row?: any) => {
	if (type === 'view') {
		userDetailRef.value.openDialog(row);
	} else {
		userDialogRef.value.openDialog(type, row);
	}
};

// 从详情页打开编辑
const handleEditFromDetail = (row: any) => {
	userDialogRef.value.openDialog('edit', row);
};

// 打开密码修改弹窗
const openPasswordDialog = (row: any) => {
	passwordDialogRef.value.openDialog(row);
};

// 删除当前行
const onDelete = (row: any) => {
	deleteRow(row);
};

// 删除方法
const deleteRow = async (row: any, message?: string) => {
	const confirmMessage = message || `此操作将永久删除该用户数据, 是否继续?`;
	try {
		await ElMessageBox.confirm(confirmMessage, '提示', {
			confirmButtonText: '删除',
			cancelButtonText: '取消',
			type: 'warning',
		});

		const res = await useUserApi().delete(row.id);
		if (res.code === 200 || res.success) {
			ElMessage.success('删除成功');
			tableRef.value?.refresh();
		} else {
			ElMessage.error(res.msg || '删除失败');
		}
	} catch (error) {
		// 用户取消删除
	}
};

// 切换用户状态（冻结/解冻）
const toggleUserStatus = async (row: any) => {
	const action = row.isLock === 1 ? '冻结' : '解冻';
	try {
		await ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning',
		});
		let res;
		if (row.isLock === 1) {
			res = await useUserApi().freezeUser(row.id);
		} else {
			res = await useUserApi().unfreezeUser(row.id);
		}
		if (res.code === 200 || res.success) {
			ElMessage.success(`${action}成功`);
			tableRef.value?.refresh();
		} else {
			ElMessage.error(res.msg || `${action}失败`);
		}
	} catch (error) {
		// 用户取消操作
	}
};

// ==================== 导入导出相关方法 ====================

/**
 * 打开导入弹窗
 */
const openImportDialog = () => {
	importRef.value?.open();
};

/**
 * 打开导出弹窗
 */
const openExportDialog = () => {
	exportRef.value?.open();
};

// 导入成功回调
const handleImportSuccess = (result: any) => {
	ElMessage.success(`导入完成，成功 ${result.successCount} 条，失败 ${result.failCount} 条`);
	// 刷新表格
	tableRef.value?.refresh();
};

// 导入失败回调
const handleImportError = (error: any) => {
	ElMessage.error(error.message || '导入失败');
};

// 导出成功回调
const handleExportSuccess = () => {
	ElMessage.success('导出成功');
};

// 导出失败回调
const handleExportError = (error: any) => {
	ElMessage.error(error.message || '导出失败');
};

// 初始化数据
const initData = async () => {
	try {
		// 获取角色列表
		const roleRes = await useRoleApi().getRoleList();
		if (roleRes.code === 200 || roleRes.success) {
			state.roleList = roleRes.data || [];
		}

		// 获取部门列表
		const res = await useDeptApi().selectDeptTree();
		state.deptData = res.data;
		nextTick(() => {
			if (state.deptData && state.deptData.length > 0) {
				expandedKeys.value = [state.deptData[0].data.id];
			}
		});
	} catch (error) {
		console.error('初始化数据失败:', error);
		state.deptData = [];
	}
};

// 页面加载时
onMounted(() => {
	initData();
});
</script>

<style scoped lang="scss">
.import-params-form {
	padding: 10px 0;
}

.import-tips {
	margin-top: 20px;

	.import-tips-content {
		p {
			font-weight: bold;
			margin-bottom: 8px;
		}

		ul {
			margin: 0;
			padding-left: 20px;

			li {
				font-size: 13px;
				line-height: 1.8;
			}
		}
	}
}
</style>
