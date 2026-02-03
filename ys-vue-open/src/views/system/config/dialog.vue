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
		<el-form ref="configDialogFormRef" :model="state.ruleForm" :rules="rules" label-width="90px" :inline="false" label-position="right">
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="配置名称" prop="name">
						<el-input v-model="state.ruleForm.name" placeholder="请输入配置名称" size="default" clearable :readonly="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="配置编码" prop="code">
						<el-input v-model="state.ruleForm.code" placeholder="请输入配置编码" :readonly="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="配置值" prop="value">
						<el-input v-model="state.ruleForm.value" placeholder="请输入配置值" type="textarea" :rows="3" :readonly="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="分类" prop="category">
						<el-input v-model="state.ruleForm.category" placeholder="请输入分类" :readonly="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="过滤条件" prop="filter">
						<el-input v-model="state.ruleForm.filter" placeholder="请输入过滤条件" :readonly="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="备注说明" prop="remark">
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
			<el-button type="primary" @click="onSubmit" size="default" :loading="state.loading" v-if="state.dialog.type !== 'view'">{{
				state.dialog.submitTxt
			}}</el-button>
		</template>
	</YsDialog>
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
	code: '', // 配置编码
	value: '', // 配置值
	category: '', // 分类
	filter: '', // 过滤条件
	remark: '', // 备注说明
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
	view: {
		title: '配置详情',
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
	code: [{ required: true, message: '请输入配置编码', trigger: 'blur' }],
	value: [{ required: true, message: '请输入配置值', trigger: 'blur' }],
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
