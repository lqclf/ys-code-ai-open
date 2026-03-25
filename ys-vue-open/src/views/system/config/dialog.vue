<template>
	<el-dialog
		:title="state.dialog.title"
		v-model="state.dialog.isShowDialog"
		@close="state.dialog.isShowDialog = false"
		width="50%"
		:close-on-click-modal="false"
		:close-on-press-escape="false"
		:append-to-body="true"
		draggable
	>
		<el-form ref="configDialogFormRef" :model="state.ruleForm" :rules="rules" label-width="90px" :inline="false" label-position="right">
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="配置名称" prop="name">
						<el-input v-model="state.ruleForm.name" placeholder="请输入配置名称" size="default" clearable />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="配置键" prop="key">
						<el-input v-model="state.ruleForm.key" placeholder="请输入配置键" clearable />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="配置值" prop="value">
						<el-input v-model="state.ruleForm.value" placeholder="请输入配置值" type="textarea" :rows="3" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="配置类型" prop="type">
						<el-radio-group v-model="state.ruleForm.type">
							<el-radio-button :value="1"> 系统配置</el-radio-button>
							<el-radio-button :value="2"> 业务配置</el-radio-button>
						</el-radio-group>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="排序" prop="seq">
						<el-input-number v-model="state.ruleForm.seq" placeholder="请输入排序" :min="0" clearable />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="备注" prop="remark">
						<el-input v-model="state.ruleForm.remark" placeholder="请输入备注" type="textarea" :rows="2" />
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
		<template #footer>
			<el-button @click="closeDialog" size="default">取 消</el-button>
			<el-button type="primary" @click="onSubmit" size="default" :loading="state.loading">{{ state.dialog.submitTxt }}</el-button>
		</template>
	</el-dialog>
</template>
<script setup lang="ts" name="systemConfigDialog">
import { reactive, ref, nextTick } from 'vue';
import { useConfigApi } from '@/api/system/config';
import { ElMessage } from 'element-plus';
import { objectCopyForm } from '@/utils/objectCopy';

const configDialogFormRef = ref();

// 表单初始数据
const initialForm = {
	id: '',
	name: '', // 配置名称
	key: '', // 配置键
	value: '', // 配置值
	type: 1, // 配置类型
	seq: 0, // 排序
	remark: '', // 备注
};

// 弹窗配置
const dialogConfig = {
	add: {
		title: '新增配置',
		submitTxt: '新增',
	},
	edit: {
		title: '编辑配置',
		submitTxt: '更新',
	},
};

const state = reactive({
	ruleForm: { ...initialForm }, // 表单数据
	dialog: {
		isShowDialog: false, // 弹窗是否显示
		type: '', // 弹窗类型 add、edit
		title: '', // 弹窗标题
		submitTxt: '', // 提交按钮文字
	},
	loading: false,
	isInitializing: false, // 是否正在初始化数据
});

// 表单验证规则
const rules = reactive({
	name: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
	key: [{ required: true, message: '请输入配置键', trigger: 'blur' }],
	value: [{ required: true, message: '请输入配置值', trigger: 'blur' }],
	type: [{ required: true, message: '请选择配置类型', trigger: 'change' }],
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
	state.dialog.submitTxt = config.submitTxt;
	state.dialog.isShowDialog = true;

	// 编辑模式时加载数据
	if (type === 'edit' && row) {
		state.isInitializing = true;
		state.ruleForm = objectCopyForm(row, initialForm) as typeof initialForm;
		await nextTick();
		state.isInitializing = false;
	}
};

// 提交表单
const onSubmit = async () => {
	// 表单验证
	const valid = await configDialogFormRef.value?.validate();
	if (!valid) return;

	// 防止重复提交
	if (state.loading) return;
	state.loading = true;

	// 根据类型调用不同的API
	const apiMethod = state.dialog.type === 'add' ? useConfigApi().add : useConfigApi().update;

	try {
		const res = await apiMethod(state.ruleForm);
		if (res.code === 200) {
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
