<template>
	<el-dialog title="权限设置" v-model="state.dialog.isShowDialog" width="50%" :close-on-click-modal="false" :close-on-press-escape="false">
		<el-form :model="state.ruleForm" :rules="rules" ref="formRef" label-width="90px" style="height: 200px">
			<el-row>
				<el-col :span="12" class="mb20">
					<el-form-item label="所属菜单">
						<el-input v-model="state.ruleForm.parentName" readonly />
					</el-form-item>
				</el-col>
				<el-col :span="12" class="mb20">
					<el-form-item label="权限名称">
						<EditableSelect
							v-model="state.ruleForm.title"
							ref="editableSelectRef"
							:options="state.optionTitles"
							placeholder="请选择或输入"
							allow-custom
						/>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="12" class="mb20">
					<el-form-item label="权限标识">
						<el-input v-model="state.ruleForm.permission" />
					</el-form-item>
				</el-col>
				<el-col :span="12" class="mb20">
					<el-form-item label="序号">
						<el-input v-model="state.ruleForm.seq" />
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button @click="onCancel" size="default">取 消</el-button>
				<el-button type="primary" @click="onSubmit" size="default" :loading="state.loading">确定</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script setup lang="ts" name="AuthDialog">
import { ref, reactive, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import EditableSelect from '@/components/EditableSelect/index.vue';
import { useMenuApi } from '@/api/system/menu';

// 表单引用
const formRef = ref();
// 获取 EditableSelect 组件的引用
const editableSelectRef = ref();

// 定义子组件向父组件传值/事件
const emit = defineEmits(['refresh']);

// 初始表单数据（用于重置）
const initialForm = {
	parentName: '', // 所属菜单
	pid: '', // 父菜单ID
	title: '', // 菜单名称
	name: '', // 路由名称
	permission: '', // 权限标识
	seq: 0, // 排序
	type: 3, // 类型 按钮权限
};

const state = reactive({
	ruleForm: { ...initialForm }, // 使用初始数据
	dialog: {
		isShowDialog: false,
		type: '', // 弹窗类型 add、edit
	},
	optionTitles: [] as any,
	loading: false,
});

const rules = reactive({
	title: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
});

// 打开弹窗并重置表单
const openDialog = (type: string, row?: any) => {
	// 重置表单数据
	state.ruleForm = { ...initialForm };

	state.dialog.type = type;
	if (type === 'edit') {
		state.ruleForm = {
			...row,
		};
		// 设置动态初始值
		state.ruleForm.parentName = row.pidName;
		state.ruleForm.pid = row.pid;
		// 立即设置title值
		state.ruleForm.title = row.title;
	} else if (type === 'add') {
		// 设置动态初始值
		state.ruleForm.parentName = row.title;
		state.ruleForm.pid = row.id;
	}

	// 生成选项
	const title = state.ruleForm.parentName + '-';
	state.optionTitles = [
		{ value: title + '新增', label: title + '新增' },
		{ value: title + '编辑', label: title + '编辑' },
		{ value: title + '删除', label: title + '删除' },
		{ value: title + '查看', label: title + '查看' },
		{ value: title + '导入', label: title + '导入' },
		{ value: title + '导出', label: title + '导出' },
	];
	// 显示弹窗
	state.dialog.isShowDialog = true;

	// 重置 EditableSelect 组件内部状态
	nextTick(() => {
		if (editableSelectRef.value) {
			// 先重置组件
			editableSelectRef.value.reset();
			// 如果是编辑模式，重新设置值
			if (type === 'edit') {
				// 强制触发v-model更新
				state.ruleForm.title = '';
				nextTick(() => {
					state.ruleForm.title = row.title;
				});
			}
		}
	});
};

// 关闭弹窗
const closeDialog = () => {
	state.dialog.isShowDialog = false;
};

// 取消按钮
const onCancel = () => {
	closeDialog();
};

// 提交表单
const onSubmit = async () => {
	// 表单验证
	const valid = await formRef.value?.validate();
	if (!valid) return;
	// 防止重复提交
	if (state.loading) return;
	state.loading = true;
	// 根据类型调用不同的API
	const apiMethod = state.dialog.type === 'add' ? useMenuApi().addMenu : useMenuApi().updateMenu;

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

// 暴露方法
defineExpose({
	openDialog,
});
</script>
