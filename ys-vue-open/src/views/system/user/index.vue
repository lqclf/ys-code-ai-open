<template>
	<div class="page-container">
		<el-card>
			<!-- 页面头部区域 -->
			<div class="page-header mb15">
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

			<!-- 表格工具栏 -->
			<div class="table-toolbar mb15">
				<el-button type="success" @click="openDialogCRU('add')" v-auth="'system:user:add'">
					<i class="ri-add-line"></i>
					新增
				</el-button>
				<el-button type="primary" @click="handleImport" v-auth="'system:user:import'">
					<i class="ri-download-2-line"></i>
					导入
				</el-button>
				<el-button type="warning" @click="handleExport" v-auth="'system:user:export'">
					<i class="ri-upload-2-line"></i>
					导出
				</el-button>
			</div>

			<!-- 表格区域 -->
			<el-table
				ref="tableRef"
				:data="tableData"
				v-loading="state.loading"
				@selection-change="handleSelectionChange"
				border
				stripe
				highlight-current-row
			>
				<el-table-column type="selection" width="55" align="center" />
				<el-table-column type="index" label="序号" width="80" align="center" />
				<el-table-column prop="loginName" label="登录名" width="140" />
				<el-table-column prop="userName" label="真实姓名" width="140" />
				<el-table-column label="用户类型" width="100">
					<template #default="scope">
						<el-tag v-if="scope.row.userType === 0" type="info">游客</el-tag>
						<el-tag v-else-if="scope.row.userType === 1">普通用户</el-tag>
						<el-tag v-else-if="scope.row.userType === 2" type="warning">会员</el-tag>
						<el-tag v-else-if="scope.row.userType === 3" type="danger">管理员</el-tag>
						<el-tag v-else type="info">未知</el-tag>
					</template>
				</el-table-column>
				<el-table-column prop="phone" label="手机号码" />
				<el-table-column prop="email" label="邮箱" />
				<el-table-column prop="departName" label="部门" />
				<el-table-column label="角色">
					<template #default="scope">
						<el-tag v-for="item in scope.row.roleList" :key="item.id" class="mr4 mb4">
							{{ item.name }}
						</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="状态" width="100">
					<template #default="scope">
						<el-tag type="danger" v-if="scope.row.isLock === 0">已锁定</el-tag>
						<el-tag type="success" v-else>正常</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="220" align="center" fixed="right">
					<template #default="scope">
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
										<el-dropdown-item>
											<el-link type="primary" underline="never" class="pr6 pl6" @click="openPasswordDialog(scope.row)" v-auth="'system:user:resetPwd'">
												密码修改
											</el-link>
										</el-dropdown-item>
										<el-dropdown-item>
											<el-link
												:type="scope.row.isLock === 1 ? 'warning' : 'success'"
												underline="never"
												class="pr6 pl6"
												@click="toggleUserStatus(scope.row)"
												v-auth="'system:user:freeze'"
												:disabled="scope.row.loginName === 'admin'"
											>
												{{ scope.row.isLock === 1 ? '冻结' : '解冻' }}
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
		<UserDialog ref="userDialogRef" @refresh="searchTable()" />
		<PasswordDialog ref="passwordDialogRef" @refresh="searchTable()" />
		<UserDetail ref="userDetailRef" @edit="handleEditFromDetail" />
	</div>
</template>

<script setup lang="ts" name="systemUser">
import { defineAsyncComponent, reactive, onMounted, ref, nextTick } from 'vue';
import { FormInstance, ElMessage, ElMessageBox } from 'element-plus';
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
const searchFormRef = ref<FormInstance>();
const tableRef = ref();

// 展开的节点keys
const expandedKeys = ref<string[]>([]);

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

// 状态数据
const state = reactive({
	loading: false,
	page: 1,
	size: 10,
	total: 0,
	roleList: [] as any[],
	deptData: [] as any[], // 上级部门数据
});

// 表格数据
const tableData = ref<any[]>([]);
// 选中的数据
const selectedRows = ref<any[]>([]);

// 获取表格数据
const getTableData = async () => {
	state.loading = true;
	try {
		const params = {
			...searchForm,
			page: state.page,
			size: state.size,
		};
		const res = await useUserApi().list(params);
		if (res.code === 200 || res.success) {
			tableData.value = res.data?.records || res.data || [];
			state.total = res.data?.total || 0;
		}
	} catch (error) {
		console.error('获取用户列表失败:', error);
		ElMessage.error('获取用户列表失败');
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
	searchForm.loginName = '';
	searchForm.userName = '';
	searchForm.phone = '';
	searchForm.roleId = '';
	searchForm.departId = '';
	searchForm.status = '';
	searchForm.userType = '';
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

// 选中行变化
const handleSelectionChange = (rows: any[]) => {
	selectedRows.value = rows;
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
			getTableData();
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
			// 冻结用户
			res = await useUserApi().freezeUser(row.id);
		} else {
			// 解冻用户
			res = await useUserApi().unfreezeUser(row.id);
		}
		if (res.code === 200 || res.success) {
			ElMessage.success(`${action}成功`);
			getTableData();
		} else {
			ElMessage.error(res.msg || `${action}失败`);
		}
	} catch (error) {
		// 用户取消操作
	}
};

// 处理导入
const handleImport = () => {
	ElMessage.info('导入功能开发中');
};

// 处理导出
const handleExport = async () => {
	try {
		const res = await useUserApi().exportUser(searchForm);
		if (res) {
			// 创建下载链接
			const url = window.URL.createObjectURL(new Blob([res.data || res]));
			const link = document.createElement('a');
			link.href = url;
			link.setAttribute('download', `用户列表_${new Date().getTime()}.xlsx`);
			document.body.appendChild(link);
			link.click();
			document.body.removeChild(link);
			window.URL.revokeObjectURL(url);
			ElMessage.success('导出成功');
		}
	} catch (error) {
		ElMessage.error('导出失败');
	}
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
				expandedKeys.value = [state.deptData[0].data.id]; //设置默认展开第一个节点
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
