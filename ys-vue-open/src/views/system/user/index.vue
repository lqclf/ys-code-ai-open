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
					<el-tag v-else-if="scope.row.userType === 1">普通用户</el-tag>
					<el-tag v-else-if="scope.row.userType === 2" type="warning">会员</el-tag>
					<el-tag v-else-if="scope.row.userType === 3" type="danger">管理员</el-tag>
					<el-tag v-else type="info">未知</el-tag>
				</template>

				<!-- 角色列 -->
				<template #roleList="scope">
					<el-tag v-for="item in scope.row.roleList" :key="item.id" class="mr4 mb4">
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
			</YsTable>
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
import YsTable from '@/components/YsTable/index.vue';

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
	roleList: [] as any[],
	deptData: [] as any[], // 上级部门数据
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
		{ title: '角色', field: 'roleList', slots: { default: 'roleList' } },
		{ title: '状态', field: 'isLock', width: 100, slots: { default: 'isLock' } },
		{ title: '操作', field: 'action', width: 220, slots: { default: 'action' }, align: 'center' },
	],
	cellConfig: {
		height: 'auto',
	},
	// 工具栏配置
	toolbarConfig: {
		size: 'small', // 工具栏大小
		refresh: true, // 开启刷新
		zoom: true, // 开启缩放
		custom: true, // 开启自定义按钮
		buttons: [
			{ name: '新增', code: 'userAdd', status: 'success', icon: 'ri-add-line' },
			{ name: '导入', code: 'userImport', status: 'primary', icon: 'ri-download-2-line' },
			{ name: '导出', code: 'userExport', status: 'warning', icon: 'ri-upload-2-line' },
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
				handleImport();
				break;
			case 'userExport':
				handleExport();
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
			// 冻结用户
			res = await useUserApi().freezeUser(row.id);
		} else {
			// 解冻用户
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
});
</script>
