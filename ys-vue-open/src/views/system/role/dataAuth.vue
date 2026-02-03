<template>
	<YsDialog
		v-model="dialogVisible"
		:title="dialogTitle"
		width="800px"
		:close-on-click-modal="false"
		:close-on-press-escape="false"
		:append-to-body="true"
		@close="handleClose"
	>
		<el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
			<el-card class="permission-card mb15" shadow="never">
				<template #header>
					<div class="card-header">
						<span><i class="ri-shield-check-line"></i> 角色信息</span>
					</div>
				</template>
				<el-row :gutter="20">
					<el-col :span="12">
						<el-form-item label="角色标识">
							<el-tag type="primary" effect="plain" size="large">
								{{ roleCode }}
							</el-tag>
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="角色名称">
							<el-tag type="success" effect="plain" size="large">
								{{ roleName }}
							</el-tag>
						</el-form-item>
					</el-col>
				</el-row>
			</el-card>

			<el-card class="permission-card mb15" shadow="never">
				<template #header>
					<div class="card-header">
						<span><i class="ri-database-2-line"></i> 数据权限配置</span>
					</div>
				</template>
				<el-form-item label="权限类型" prop="dataPermissionType">
					<div class="permission-options-grid">
						<div
							v-for="option in permissionOptions"
							:key="option.value"
							:class="['permission-mini-card', { active: formData.dataPermissionType === option.value }]"
							@click="
								formData.dataPermissionType = option.value;
								handleDataTypeChange();
							"
						>
							<div class="mini-card-icon">
								<i :class="option.icon"></i>
							</div>
							<span class="mini-card-label">{{ option.label }}</span>
							<div class="mini-card-check">
								<i v-if="formData.dataPermissionType === option.value" class="ri-check-fill"></i>
							</div>
						</div>
					</div>
				</el-form-item>

				<el-form-item v-if="formData.dataPermissionType === 2" label="指定部门" prop="departIds">
					<el-tree-select
						v-model="formData.departIds"
						:data="departTree"
						check-strictly
						:render-after-expand="false"
						:default-expanded-keys="expandedKeys"
						multiple
						show-checkbox
						accordion
						clearable
						placeholder="请选择指定部门"
						class="dept-tree-select"
					>
						<template #default="{ node, data }">
							<div class="tree-node">
								<i :class="getDeptIcon(data.data && data.data.levelType)"></i>
								<span class="tree-label">{{ node.label }}</span>
							</div>
						</template>
					</el-tree-select>
				</el-form-item>
			</el-card>
		</el-form>
		<template #footer>
			<el-button @click="handleClose" size="default"> 取消 </el-button>
			<el-button type="primary" @click="handleSubmit" size="default" :loading="submitLoading"> 保存 </el-button>
		</template>
	</YsDialog>
</template>

<script lang="ts" setup name="roleDataPermission">
import { reactive, ref, nextTick } from 'vue';
import { FormInstance, ElMessage } from 'element-plus';
import { useRoleDataApi } from '@/api/system/role/roleData';
import { useDeptApi } from '@/api/system/dept';

interface PermissionOption {
	value: number;
	label: string;
	description: string;
	icon: string;
}

const roleDataApi = useRoleDataApi();
const deptApi = useDeptApi();

const dialogVisible = ref(false);
const dialogTitle = ref('');
const roleCode = ref('');
const roleName = ref('');
const submitLoading = ref(false);
const formRef = ref<FormInstance>();
const expandedKeys = ref<string[]>([]);

const permissionOptions: PermissionOption[] = [
	{
		value: 1,
		label: '全部数据权限',
		description: '可以访问系统中的所有数据',
		icon: 'ri-global-line',
	},
	{
		value: 2,
		label: '指定部门权限',
		description: '仅能访问指定部门的数据',
		icon: 'ri-building-line',
	},
	{
		value: 3,
		label: '本部门数据权限',
		description: '仅能访问当前用户所属部门的数据',
		icon: 'ri-home-4-line',
	},
	{
		value: 4,
		label: '本部门及以下',
		description: '可以访问本部门及所有下级部门的数据',
		icon: 'ri-node-tree',
	},
	{
		value: 5,
		label: '仅本人数据权限',
		description: '仅能访问自己创建的数据',
		icon: 'ri-user-line',
	},
];

const formData = reactive({
	id: '',
	roleId: '',
	dataPermissionType: 5,
	departIds: [] as string[],
	remark: '',
});

const rules = {
	dataPermissionType: [{ required: true, message: '请选择数据权限类型', trigger: 'change' }],
	departIds: [{ required: true, message: '请选择指定部门', trigger: 'change' }],
};

const departTree = ref([] as any[]);

const getDeptIcon = (levelType: number | undefined) => {
	if (levelType === 1) return 'ri-building-line';
	if (levelType === 2) return 'ri-community-line';
	return 'ri-building-2-line';
};

const loadDepartTree = async () => {
	try {
		const res = await deptApi.selectDeptTree();
		if (res.code === 200 || res.success) {
			departTree.value = res.data || [];
			nextTick(() => {
				if (departTree.value && departTree.value.length > 0) {
					expandedKeys.value = [departTree.value[0].data?.id];
				}
			});
		}
	} catch (error) {
		ElMessage.error('加载部门树失败');
	}
};

const loadRoleDataPermission = async (row: any) => {
	try {
		const res = await roleDataApi.getRoleDataPermission(row.id);
		if ((res.code === 200 || res.success) && res.data) {
			formData.id = res.data.id || '';
			formData.roleId = res.data.roleId || row.id;
			formData.dataPermissionType = res.data.dataPermissionType ?? 5;
			formData.departIds = res.data.departIds || [];
			formData.remark = res.data.remark || '';
		} else {
			formData.roleId = row.id;
			formData.dataPermissionType = 5;
			formData.departIds = [];
			formData.remark = '';
		}
	} catch (error) {
		formData.roleId = row.id;
		formData.dataPermissionType = 5;
		formData.departIds = [];
		formData.remark = '';
	}
};

const handleDataTypeChange = () => {
	if (formData.dataPermissionType !== 2) {
		formData.departIds = [];
	}
};

const handleSubmit = async () => {
	if (!formRef.value) return;
	await formRef.value.validate(async (valid) => {
		if (valid) {
			submitLoading.value = true;
			try {
				const params = {
					id: formData.id || undefined,
					roleId: formData.roleId,
					dataPermissionType: formData.dataPermissionType,
					departIds: formData.departIds,
					remark: formData.remark,
				};
				const res = await roleDataApi.saveRoleDataPermission(params);
				if (res.code === 200 || res.success) {
					ElMessage.success('保存成功');
					dialogVisible.value = false;
				} else {
					ElMessage.error(res.msg || '保存失败');
				}
			} catch (error) {
				ElMessage.error('保存失败');
			} finally {
				submitLoading.value = false;
			}
		}
	});
};

const handleClose = () => {
	dialogVisible.value = false;
	if (formRef.value) {
		formRef.value.resetFields();
	}
};

const openDialog = async (row: any) => {
	roleCode.value = row.code || '';
	roleName.value = row.name || '';
	dialogTitle.value = `数据权限配置`;
	dialogVisible.value = true;
	formData.dataPermissionType = 5;
	formData.departIds = [];
	formData.remark = '';
	await loadDepartTree();
	await loadRoleDataPermission(row);
};

defineExpose({
	openDialog,
});
</script>

<style scoped>
.permission-card {
	border: 1px solid #e4e7ed;
	border-radius: 8px;
}

.permission-card :deep(.el-card__header) {
	padding: 12px 20px;
	background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
	border-bottom: 1px solid #e4e7ed;
}

.card-header {
	display: flex;
	align-items: center;
	gap: 8px;
	font-size: 15px;
	font-weight: 600;
	color: #303133;
}

.card-header i {
	font-size: 16px;
	color: #409eff;
}

.permission-options-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 12px;
	width: 100%;
}

.permission-mini-card {
	position: relative;
	display: flex;
	align-items: center;
	gap: 8px;
	padding: 10px 12px;
	border: 1px solid #e4e7ed;
	border-radius: 6px;
	cursor: pointer;
	transition: all 0.2s ease;
	background: #ffffff;
	height: 40px;
}

.permission-mini-card:hover {
	border-color: #409eff;
	background: #f5f7fa;
}

.permission-mini-card.active {
	border-color: #409eff;
	background: linear-gradient(135deg, #ecf5ff 0%, #d9ecff 100%);
}

.mini-card-icon {
	width: 26px;
	height: 26px;
	display: flex;
	align-items: center;
	justify-content: center;
	background: #f0f2f5;
	border-radius: 6px;
	flex-shrink: 0;
}

.mini-card-icon i {
	font-size: 14px;
	color: #909399;
	transition: all 0.2s ease;
}

.permission-mini-card.active .mini-card-icon {
	background: #409eff;
}

.permission-mini-card.active .mini-card-icon i {
	color: #ffffff;
}

.mini-card-label {
	font-size: 13px;
	color: #606266;
	text-align: left;
	font-weight: 500;
	transition: all 0.2s ease;
	flex: 1;
}

.permission-mini-card.active .mini-card-label {
	color: #409eff;
	font-weight: 600;
}

.mini-card-check {
	width: 18px;
	height: 18px;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
}

.mini-card-check i {
	font-size: 16px;
	color: #409eff;
}

.tree-node {
	display: flex;
	align-items: center;
	gap: 6px;
}

.tree-node i {
	font-size: 16px;
	color: #409eff;
}

.tree-label {
	font-size: 14px;
	color: #303133;
}

.dept-tree-select {
	width: 100%;
}

.dept-tree-select :deep(.el-tree-select__wrapper) {
	padding: 8px 12px;
}

.mb15 {
	margin-bottom: 15px;
}
</style>
