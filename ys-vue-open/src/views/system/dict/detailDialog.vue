<template>
	<YsDialog
		:title="state.dialog.title"
		v-model="state.dialog.isShowDialog"
		@close="closeDialog"
		width="50%"
		:close-on-click-modal="false"
		:close-on-press-escape="false"
		:append-to-body="true"
	>
		<el-form ref="detailFormRef" :model="state.form" :rules="rules" label-width="90px" :inline="false" label-position="right">
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="字典编码" prop="dictCode">
						<el-input v-model="state.form.dictCode" placeholder="" size="default" clearable readonly />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="名称" prop="name">
						<el-input v-model="state.form.name" placeholder="请输入字典明细名称" size="default" clearable :readonly="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="编码" prop="code">
						<el-input v-model="state.form.code" placeholder="请输入字典明细编码" :readonly="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="序号" prop="seq">
						<el-input-number v-model="state.form.seq" placeholder="请输入序号" :min="0" clearable :readonly="state.dialog.type == 'view'" />
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

<script setup lang="ts" name="dictDetailDialog">
import { reactive, ref, nextTick } from 'vue';
import { useDictApi } from '@/api/system/dict';
import { ElMessage } from 'element-plus';
import { objectCopyForm } from '@/utils/objectCopy';

const detailFormRef = ref();

// 定义子组件向父组件传值/事件
const emit = defineEmits(['refresh']);

// 表单初始数据
const initialForm = {
	id: '',
	dictId: '',
	dictCode: '',
	name: '',
	code: '',
	seq: 0,
};

// 弹窗配置
const dialogConfig = {
	add: {
		title: '新增字典明细',
		submitTxt: '新增',
	},
	edit: {
		title: '编辑字典明细',
		submitTxt: '更新',
	},
	view: {
		title: '字典明细详情',
	},
};

const state = reactive({
	form: { ...initialForm }, // 表单数据
	dialog: {
		isShowDialog: false, // 弹窗是否显示
		type: '', // 弹窗类型 add、edit、view
		title: '', // 弹窗标题
		submitTxt: '', // 提交按钮文字
	},
	loading: false,
	isInitializing: false, // 是否正在初始化数据
});

// 表单验证规则
const rules = reactive({
	name: [{ required: true, message: '请输入字典明细名称', trigger: 'blur' }],
	code: [{ required: true, message: '请输入字典明细编码', trigger: 'blur' }],
	seq: [{ required: true, message: '请输入序号', trigger: 'blur' }],
});

// 打开弹窗
const openDialog = async (type: string, row?: any) => {
	// 重置表单数据
	state.form = { ...initialForm };

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
	state.form.dictCode = row.dictCode;
	if (type === 'add') {
		state.form.dictId = row.dictId;
	}
	// 编辑/预览模式时加载数据
	if ((type === 'edit' || type === 'view') && row) {
		state.isInitializing = true;
		state.form = objectCopyForm(row, initialForm) as typeof initialForm;
		await nextTick();
		state.isInitializing = false;
	}
};

// 提交表单
const onSubmit = async () => {
	// 表单验证
	const valid = await detailFormRef.value?.validate();
	if (!valid) return;

	// 防止重复提交
	if (state.loading) return;
	state.loading = true;
	// 根据类型调用不同的API
	const apiMethod = state.dialog.type === 'add' ? useDictApi().addDetail : useDictApi().updateDetail;

	try {
		const res = await apiMethod(state.form);
		if (res.code === 200) {
			ElMessage.success(state.dialog.type === 'add' ? '添加成功' : '更新成功');
			closeDialog();
			emit('refresh');
		} else {
			ElMessage.error(res.msg || `${state.dialog.type === 'add' ? '添加' : '更新'}失败`);
		}
	} finally {
		// 请求完成后重置loading状态
		state.loading = false;
	}
};

// 关闭弹窗
const closeDialog = () => {
	state.dialog.isShowDialog = false;
};

// 暴露变量
defineExpose({
	openDialog,
});
</script>
