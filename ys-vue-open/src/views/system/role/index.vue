<template>
	<div class="page-container">
		<el-card>
			<YsTable
				ref="tableRef"
				:request-fn="useRoleApi().list"
				:options="tableOptions"
				:query-params="searchForm"
				:show-pagination="true"
				:events="tableEvents"
				@request-success="handleSuccess"
			>
				<!-- 页面头部区域 -->
				<template #page-header>
					<div class="page-header">
						<el-form :model="searchForm" ref="searchFormRef" label-width="90px">
							<el-input placeholder="请输入角色名称" class="w-180 mr20" v-model="searchForm.name" clearable @keyup.enter="searchTable" />
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

				<!-- 操作列 -->
				<template #action="scope">
					<div class="operation-btn-group">
						<el-link type="primary" underline="never" class="pr6 pl6" @click="openDialogCRU('edit', scope.row)" v-auth="'system:role:edit'">
							编辑
						</el-link>
						<el-link type="danger" underline="never" class="pr6 pl6" @click="onDelete(scope.row)" v-auth="'system:role:delete'"> 删除 </el-link>
						<el-link type="success" underline="never" class="pr6 pl6" @click="openDialogCRU('view', scope.row)"> 详情 </el-link>
						<el-dropdown :hide-on-click="false" v-auths="['system:role:updateAuth', 'system:role:userConfig', 'system:role:dataPermission']">
							<span class="el-dropdown-link">
								<el-link type="primary" underline="never" class="pr6 pl6">
									<i class="ri-arrow-right-double-line"></i>
								</el-link>
							</span>
							<template #dropdown>
								<el-dropdown-menu>
									<div v-auth="'system:role:updateAuth'">
										<el-dropdown-item>
											<el-link type="primary" underline="never" class="pr6 pl6" @click="openDialogAuth(scope.row)">
												权限配置
											</el-link>
										</el-dropdown-item>
									</div>
									<div v-auth="'system:role:userConfig'">
										<el-dropdown-item>
											<el-link type="primary" underline="never" class="pr6 pl6" @click="openDialogUser(scope.row)">
												人员配置
											</el-link>
										</el-dropdown-item>
									</div>
									<div v-auth="'system:role:dataPermission'">
										<el-dropdown-item>
											<el-link type="primary" underline="never" class="pr6 pl6" @click="openDialogDataPermission(scope.row)">
												数据权限
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
		<RoleDialog ref="roleDialogRef" />
		<RoleAuthDialog ref="roleAuthDialogRef" />
		<RoleUsersDialog ref="roleUsersDialogRef" />
		<RoleDataPermission ref="roleDataPermissionRef" />
		
		<!-- 导入导出组件（hide-button隐藏自带按钮，通过工具栏调用） -->
		<YsImportExcel
			ref="importRef"
			import-type="systemRole"
			:columns="importColumns"
			button-text="导入"
			button-type="warning"
			dialog-title="角色数据导入"
			hide-button
			@success="handleImportSuccess"
			@error="handleImportError"
		/>
		<YsExportExcel
			ref="exportRef"
			export-type="systemRole"
			:query-params="searchForm"
			:columns="exportColumns"
			file-name="角色数据"
			button-text="导出"
			button-type="success"
			dialog-title="角色数据导出"
			hide-button
			@success="handleExportSuccess"
			@error="handleExportError"
		/>
	</div>
</template>

<script lang="ts" setup name="systemRole">
import { defineAsyncComponent, reactive, ref, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useRoleApi } from '@/api/system/role';
import { useUserInfo } from '@/stores/userInfo';
import { storeToRefs } from 'pinia';

// 引入组件
const RoleDialog = defineAsyncComponent(() => import('@/views/system/role/dialog.vue'));
const RoleAuthDialog = defineAsyncComponent(() => import('@/views/system/role/menuAuth.vue'));
const RoleUsersDialog = defineAsyncComponent(() => import('@/views/system/role/users.vue'));
const RoleDataPermission = defineAsyncComponent(() => import('@/views/system/role/dataAuth.vue'));

// 定义变量内容
const roleDialogRef = ref();
const roleAuthDialogRef = ref();
const roleUsersDialogRef = ref();
const roleDataPermissionRef = ref();
const tableRef = ref();
const importRef = ref();
const exportRef = ref();

// 权限检查
const stores = useUserInfo();
const { userInfos } = storeToRefs(stores);

/**
 * 检查权限
 * @param auth 权限标识
 * @returns 是否拥有该权限
 */
const hasAuth = (auth: string) => {
	return userInfos.value.authBtnList.some((v: string) => v === auth);
};

// API 实例
const roleApi = useRoleApi();

// 导入导出列定义
const importColumns = [
	{ field: 'code', title: '角色编码', width: 150 },
	{ field: 'name', title: '角色名称', width: 150 },
	{ field: 'seq', title: '显示顺序', width: 100 },
	{ field: 'statusStr', title: '状态', width: 100 },
	{ field: 'remark', title: '备注', width: 200 },
];

const exportColumns = [
	{ field: 'code', title: '角色编码' },
	{ field: 'name', title: '角色名称' },
	{ field: 'seq', title: '显示顺序' },
	{ field: 'statusStr', title: '状态' },
	{ field: 'remark', title: '备注' },
];

// 搜索表单
const searchForm = reactive({
	name: '',
});

const tableOptions = reactive<any>({
	columns: [
		{ type: 'seq', width: 80, align: 'center' },
		{ title: '角色名称', field: 'name' },
		{ title: '角色编码', field: 'code' },
		{ title: '排序', field: 'seq', width: 80 },
		{ title: '备注', field: 'remark' },
		{ title: '操作', field: 'action', width: 180, slots: { default: 'action' }, align: 'center' },
	],
	// 工具栏配置
	toolbarConfig: {
		size: 'small',
		refresh: true,
		zoom: true,
		custom: true,
		buttons: computed(() => {
			const allButtons = [
				{ name: '新增', code: 'menuAdd', status: 'success', icon: 'ri-add-line', auth: 'system:role:add' },
				{ name: '导入', code: 'roleImport', status: 'warning', icon: 'ri-upload-line', auth: 'system:role:import' },
				{ name: '导出', code: 'roleExport', status: 'primary', icon: 'ri-download-line', auth: 'system:role:export' },
			];
			return allButtons.filter(btn => !btn.auth || hasAuth(btn.auth)).map(({ auth, ...rest }) => rest);
		}),
	},
});

const tableEvents = {
	toolbarButtonClick(params: any) {
		switch (params.code) {
			case 'menuAdd':
				openDialogCRU('add');
				break;
			case 'roleImport':
				openImportDialog();
				break;
			case 'roleExport':
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

// 数据加载成功回调
const handleSuccess = (res: any) => {
	//console.log('数据加载成功', res);
};

// 打开菜单操作弹窗（新增/编辑）
const openDialogCRU = (type: string, row?: any) => {
	roleDialogRef.value.openDialog(type, row);
};

// 删除当前行
const onDelete = (row: any) => {
	deleteRow(row);
};

// 删除方法
const deleteRow = async (row: any, message?: string) => {
	const confirmMessage = message || `此操作将永久删除该数据, 是否继续?`;
	try {
		await ElMessageBox.confirm(confirmMessage, '提示', {
			confirmButtonText: '删除',
			cancelButtonText: '取消',
			type: 'warning',
		});

		const res = await useRoleApi().delete(row.id);
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

// 打开权限配置弹窗
const openDialogAuth = (row: any) => {
	roleAuthDialogRef.value.openDialog(row);
};

// 打开人员配置弹窗
const openDialogUser = (row: any) => {
	roleUsersDialogRef.value.openDialog({ ...row, roleName: row.name });
};

// 打开数据权限配置弹窗
const openDialogDataPermission = (row: any) => {
	roleDataPermissionRef.value.openDialog(row);
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
</script>
