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
									<el-dropdown-item>
										<el-link type="primary" underline="never" class="pr6 pl6" @click="openDialogAuth(scope.row)" v-auth="'system:role:updateAuth'">
											权限配置
										</el-link>
									</el-dropdown-item>
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
	</div>
</template>

<script lang="ts" setup name="systemRole">
import { defineAsyncComponent, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useRoleApi } from '@/api/system/role';

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
		size: 'small', // 工具栏大小
		refresh: true, // 开启刷新
		zoom: true, // 开启缩放
		custom: true, // 开启自定义按钮
		buttons: [{ name: '新增', code: 'menuAdd', status: 'success', icon: 'ri-add-line' }],
	},
});

const tableEvents = {
	toolbarButtonClick(params: any) {
		switch (params.code) {
			case 'menuAdd':
				openDialogCRU('add');
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
	// 重置表格
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
</script>
