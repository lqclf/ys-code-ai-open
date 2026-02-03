<template>
	<YsDialog
		:title="state.dialog.title"
		v-model="state.dialog.isShowDialog"
		@close="state.dialog.isShowDialog = false"
		width="50%"
		:close-on-click-modal="false"
		:close-on-press-escape="false"
		:append-to-body="true"
	>
		<el-form ref="userDialogFormRef" :model="state.ruleForm" :rules="rules" label-width="90px" :inline="false" label-position="right">
			<!-- 用户基本信息 -->
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="登录名" prop="loginName">
						<el-input
							v-model="state.ruleForm.loginName"
							placeholder="请输入登录名"
							size="default"
							clearable
							:readonly="state.dialog.type == 'view'"
						/>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="真实姓名" prop="userName">
						<el-input
							v-model="state.ruleForm.userName"
							placeholder="请输入真实姓名"
							size="default"
							clearable
							:readonly="state.dialog.type == 'view'"
						/>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="用户类型" prop="userType">
						<el-select v-model="state.ruleForm.userType" placeholder="请选择用户类型" clearable class="w100" :disabled="state.dialog.type == 'view'">
							<el-option label="游客" :value="0"></el-option>
							<el-option label="普通用户" :value="1"></el-option>
							<el-option label="会员" :value="2"></el-option>
							<el-option label="管理员" :value="3"></el-option>
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="手机号码" prop="phone">
						<el-input v-model="state.ruleForm.phone" placeholder="请输入手机号码" size="default" clearable :readonly="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="邮箱" prop="email">
						<el-input v-model="state.ruleForm.email" placeholder="请输入邮箱" size="default" clearable :readonly="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="所属部门" prop="departId">
						<el-tree-select
							v-model="state.ruleForm.departId"
							:data="state.deptData"
							check-strictly
							:render-after-expand="false"
							:default-expanded-keys="expandedKeys"
							accordion
							clearable
							placeholder="请选择上级部门"
							:readonly="state.dialog.type == 'view'"
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
				</el-col>
			</el-row>
			<!-- 添加角色选择 -->
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="角色" prop="roleIds">
						<el-select
							v-model="state.ruleForm.roleIds"
							multiple
							value-key="id"
							placeholder="请选择角色"
							clearable
							:readonly="state.dialog.type == 'view'"
							class="w100"
						>
							<el-option v-for="role in state.roleList" :key="role.id" :label="role.name" :value="role.id" />
						</el-select>
					</el-form-item>
				</el-col>
			</el-row>
			<!-- 用户基本信息结束 -->
			<el-row class="mb15" v-if="state.dialog.type === 'add'">
				<el-col :span="24">
					<el-form-item label="初始密码" prop="password">
						<el-input v-model="state.ruleForm.password" type="password" placeholder="请输入初始密码" size="default" clearable show-password />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="状态" prop="status">
						<el-switch
							v-model="state.ruleForm.status"
							width="50px"
							inline-prompt
							:active-value="1"
							:inactive-value="0"
							active-text="正常"
							inactive-text="禁用"
							:disabled="state.dialog.type == 'view'"
						/>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="锁定状态" prop="isLock">
						<el-switch
							v-model="state.ruleForm.isLock"
							width="50px"
							inline-prompt
							:active-value="1"
							:inactive-value="0"
							active-text="正常"
							inactive-text="锁定"
							:disabled="state.dialog.type == 'view'"
						/>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="备注" prop="remark">
						<el-input
							v-model="state.ruleForm.remark"
							type="textarea"
							:rows="3"
							maxlength="200"
							show-word-limit
							:readonly="state.dialog.type == 'view'"
						/>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
		<template #footer>
			<el-button @click="closeDialog" size="default">取 消</el-button>
			<el-button type="primary" @click="onSubmit" size="default" v-if="state.dialog.type !== 'view'" :loading="state.loading">{{
				state.dialog.submitTxt
			}}</el-button>
		</template>
	</YsDialog>
</template>

<script setup lang="ts" name="systemUserDialog">
import { reactive, ref, nextTick } from 'vue';
import { useUserApi } from '@/api/system/user';
import { useRoleApi } from '@/api/system/role';
import { Shop, OfficeBuilding, School } from '@element-plus/icons-vue';
import { useDeptApi } from '@/api/system/dept';
import { ElMessage } from 'element-plus';
import { objectCopyForm } from '@/utils/objectCopy';

const userDialogFormRef = ref();

// 表单初始数据
const initialForm = {
	id: '',
	loginName: '', // 登录名
	userName: '', // 真实姓名
	code: '', // 编号
	departId: '', // 所属部门ID
	departName: '', // 所属部门名称
	userType: 1, // 用户类型
	phone: '', // 手机号码
	email: '', // 邮箱
	status: 1, // 状态
	isLock: 1, // 锁定状态
	remark: '', // 备注
	password: '', // 密码（仅新增时使用）
	roleIds: [], // 角色ID数组
};

// 弹窗配置
const dialogConfig = {
	add: {
		title: '新增用户',
		submitTxt: '新增',
	},
	edit: {
		title: '编辑用户',
		submitTxt: '更新',
	},
	view: {
		title: '用户详情',
	},
};

// 展开的节点keys
const expandedKeys = ref<string[]>([]);

const state = reactive({
	ruleForm: { ...initialForm }, // 表单数据
	dialog: {
		isShowDialog: false, // 弹窗是否显示
		type: '', // 弹窗类型 add、edit、view
		title: '', // 弹窗标题
		submitTxt: '', // 提交按钮文字
	},
	loading: false,
	isInitializing: false, // 是否正在初始化数据
	deptData: [] as any[], // 部门数据
	roleList: [] as any[], // 角色列表数据
});

// 表单验证规则
const rules = reactive({
	loginName: [{ required: true, message: '请输入登录名', trigger: 'blur' }],
	userName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
	userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }],
	email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
	password: [{ required: true, message: '请输入初始密码', trigger: 'blur' }],
});

// 打开弹窗
const openDialog = async (type: string, row?: any) => {
	// 重置表单数据
	state.ruleForm = { ...initialForm };

	// 设置弹窗配置
	const config = dialogConfig[type as keyof typeof dialogConfig];
	if (!config) return;

	// 应用弹窗配置
	state.dialog.type = type;
	state.dialog.title = config.title;
	if ('submitTxt' in config) {
		state.dialog.submitTxt = config.submitTxt;
	}
	state.dialog.isShowDialog = true;

	// 编辑/预览模式时加载数据
	if ((type === 'edit' || type === 'view') && row) {
		state.isInitializing = true;

		// 设置基本信息
		state.ruleForm = objectCopyForm(row, initialForm) as typeof initialForm;

		state.ruleForm.roleIds = row.roleList.map((item: any) => item.id);

		await nextTick();
		state.isInitializing = false;
	}
	if (type === 'add' && row) {
		state.isInitializing = true;
		// 设置基本信息
		state.ruleForm = objectCopyForm(row, initialForm) as typeof initialForm;

		await nextTick();
		state.isInitializing = false;
	}

	// 初始化下拉列表数据
	await initSelectData();
};

// 初始化下拉列表数据
const initSelectData = async () => {
	// 加载上级部门数据
	try {
		const res = await useDeptApi().selectDeptTree();
		state.deptData = res.data;
		nextTick(() => {
			if (state.deptData && state.deptData.length > 0) {
				expandedKeys.value = [state.deptData[0].data.id]; //设置默认展开第一个节点
			}
		});
	} catch (error) {
		console.error('加载部门树失败:', error);
		state.deptData = [];
	}

	// 加载角色列表数据
	try {
		const res = await useRoleApi().getRoleList();
		state.roleList = res.data || [];
	} catch (error) {
		console.error('加载角色列表失败:', error);
		state.roleList = [];
	}
};

// 提交表单
const onSubmit = async () => {
	// 表单验证
	const valid = await userDialogFormRef.value?.validate();
	if (!valid) return;

	// 防止重复提交
	if (state.loading) return;
	state.loading = true;

	try {
		// 根据类型调用不同的API
		const apiMethod = state.dialog.type === 'add' ? useUserApi().add : useUserApi().update;

		const res = await apiMethod(state.ruleForm);
		if (res.code === 200 || res.success) {
			ElMessage.success(state.dialog.type === 'add' ? '添加成功' : '更新成功');
			closeDialog();
			emit('refresh');
		} else {
			ElMessage.error(res.msg || `${state.dialog.type === 'add' ? '添加' : '更新'}失败`);
		}
	} catch (error) {
		ElMessage.error('请求失败');
	} finally {
		// 请求完成后重置loading状态
		state.loading = false;
	}
};

// 关闭弹窗
const closeDialog = () => {
	state.dialog.isShowDialog = false;
};

// 定义子组件向父组件传值/事件
const emit = defineEmits(['refresh']);

// 暴露方法给父组件
defineExpose({
	openDialog,
});
</script>
