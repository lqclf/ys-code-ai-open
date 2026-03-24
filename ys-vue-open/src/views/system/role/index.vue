<template>
	<div class="page-container">
		<el-card>
			<!-- 页面头部区域 -->
			<div class="page-header mb15">
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

			<!-- 表格工具栏 -->
			<div class="table-toolbar mb15">
				<el-button type="success" @click="openDialogCRU('add')" v-auth="'system:role:add'">
					<i class="ri-add-line"></i>
					新增
				</el-button>
			</div>

			<!-- 表格区域 -->
			<el-table
				ref="tableRef"
				:data="tableData"
				v-loading="state.loading"
				border
				stripe
				highlight-current-row
			>
				<el-table-column type="index" label="序号" width="80" align="center" />
				<el-table-column prop="name" label="角色名称" />
				<el-table-column prop="code" label="角色编码" />
				<el-table-column prop="seq" label="排序" width="80" />
				<el-table-column prop="remark" label="备注" />
				<el-table-column label="操作" width="180" align="center" fixed="right">
					<template #default="scope">
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
				</el-table-column>
			</el-table>

			<!-- 分页区域 -->
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
		</el-card>

		<!-- 引入组件 -->
		<RoleDialog ref="roleDialogRef" @refresh="getTableData" />
		<RoleAuthDialog ref="roleAuthDialogRef" />
	</div>
</template>

<script lang="ts" setup name="systemRole">
import { defineAsyncComponent, reactive, ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useRoleApi } from '@/api/system/role';

// 引入组件
const RoleDialog = defineAsyncComponent(() => import('@/views/system/role/dialog.vue'));
const RoleAuthDialog = defineAsyncComponent(() => import('@/views/system/role/menuAuth.vue'));

// 定义变量内容
const roleDialogRef = ref();
const roleAuthDialogRef = ref();
const searchFormRef = ref();
const tableRef = ref();

// 搜索表单
const searchForm = reactive({
	name: '',
});

// 状态数据
const state = reactive({
	loading: false,
	page: 1,
	size: 10,
	total: 0,
});

// 表格数据
const tableData = ref<any[]>([]);

// 获取表格数据
const getTableData = async () => {
	state.loading = true;
	try {
		const params = {
			...searchForm,
			page: state.page,
			size: state.size,
		};
		const res = await useRoleApi().list(params);
		if (res.code === 200 || res.success) {
			tableData.value = res.data?.records || res.data || [];
			state.total = res.data?.total || 0;
		}
	} catch (error) {
		console.error('获取角色列表失败:', error);
		ElMessage.error('获取角色列表失败');
	} finally {
		state.loading = false;
	}
};

// 搜索方法
const searchTable = (resetPage = true) => {
	if (resetPage) {
		state.page = 1;
	}
	getTableData();
};

// 重置方法
const resetTable = () => {
	searchForm.name = '';
	state.page = 1;
	getTableData();
};

// 分页大小变化
const handleSizeChange = (val: number) => {
	state.size = val;
	getTableData();
};

// 页码变化
const handleCurrentChange = (val: number) => {
	state.page = val;
	getTableData();
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
			getTableData();
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

// 页面加载时
onMounted(() => {
	getTableData();
});
</script>

<style scoped>
.operation-btn-group {
	display: flex;
	align-items: center;
	gap: 8px;
}
.pagination-container {
	display: flex;
	justify-content: flex-end;
}
</style>
