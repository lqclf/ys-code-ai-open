<template>
	<YsDialog
		title="修改密码"
		v-model="state.dialog.isShowDialog"
		@close="state.dialog.isShowDialog = false"
		width="50%"
		:close-on-click-modal="false"
		:close-on-press-escape="false"
		:append-to-body="true"
	>
		<el-form ref="passwordDialogFormRef" :model="state.ruleForm" :rules="rules" label-width="120px" :inline="false" label-position="right">
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="用户信息">
						<el-input v-model="state.userInfo" placeholder="" size="default" readonly />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="新密码" prop="newPassword">
						<el-input v-model="state.ruleForm.newPassword" type="password" placeholder="请输入新密码" size="default" clearable show-password />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="确认新密码" prop="confirmPassword">
						<el-input
							v-model="state.ruleForm.confirmPassword"
							type="password"
							placeholder="请再次输入新密码"
							size="default"
							clearable
							show-password
						/>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
		<template #footer>
			<el-button @click="closeDialog" size="default">取 消</el-button>
			<el-button type="primary" @click="onSubmit" size="default" :loading="state.loading">确 定</el-button>
		</template>
	</YsDialog>
</template>

<script setup lang="ts" name="systemUserPasswordDialog">
import { reactive, ref } from 'vue';
import { useUserApi } from '@/api/system/user';
import { ElMessage } from 'element-plus';

const passwordDialogFormRef = ref();

// 表单初始数据
const initialForm = {
	userId: '',
	newPassword: '',
	confirmPassword: '',
};

const state = reactive({
	ruleForm: { ...initialForm }, // 表单数据
	userInfo: '', // 用户信息显示
	dialog: {
		isShowDialog: false, // 弹窗是否显示
	},
	loading: false,
});

// 自定义验证规则
const validateConfirmPassword = (rule: any, value: any, callback: any) => {
	if (value === '') {
		callback(new Error('请再次输入新密码'));
	} else if (value !== state.ruleForm.newPassword) {
		callback(new Error('两次输入的密码不一致'));
	} else {
		callback();
	}
};

// 表单验证规则
const rules = reactive({
	newPassword: [
		{ required: true, message: '请输入新密码', trigger: 'blur' },
		{ min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
		{ max: 20, message: '密码长度不能超过20位', trigger: 'blur' },
		{ pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[^]*$/, message: '密码必须包含大小写字母和数字', trigger: 'blur' },
	],
	confirmPassword: [
		{ required: true, message: '请确认新密码', trigger: 'blur' },
		{ validator: validateConfirmPassword, trigger: 'blur' },
	],
});

// 打开弹窗
const openDialog = async (row: any) => {
	// 重置表单数据
	state.ruleForm = { ...initialForm };
	state.ruleForm.userId = row.id;

	// 设置用户信息显示
	state.userInfo = `${row.userName || row.loginName} (${row.phone || row.email || '暂无联系方式'})`;

	// 显示弹窗
	state.dialog.isShowDialog = true;
};

// 提交表单
const onSubmit = async () => {
	// 表单验证
	const valid = await passwordDialogFormRef.value?.validate();
	if (!valid) return;

	state.loading = true;

	try {
		const res = await useUserApi().resetPassword({
			userId: state.ruleForm.userId,
			newPassword: state.ruleForm.newPassword,
		});

		if (res.code === 200 || res.success) {
			ElMessage.success('密码修改成功');
			closeDialog();
			emit('refresh');
		} else {
			ElMessage.error(res.msg || '密码修改失败');
		}
	} catch (error) {
		ElMessage.error('请求失败');
	} finally {
		state.loading = false;
	}
};

// 关闭弹窗
const closeDialog = () => {
	state.dialog.isShowDialog = false;
	// 重置表单
	passwordDialogFormRef.value?.resetFields();
};

// 定义子组件向父组件传值/事件
const emit = defineEmits(['refresh']);

// 暴露方法给父组件
defineExpose({
	openDialog,
});
</script>
