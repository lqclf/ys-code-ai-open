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
		<el-form ref="menuDialogFormRef" :model="state.ruleForm" :rules="rules" label-width="90px" :inline="false" :label-position="labelPosition">
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="上级菜单" prop="pid">
						<el-tree-select
							v-model="state.ruleForm.pid"
							:data="state.menuData"
							check-strictly
							:render-after-expand="false"
							:default-expanded-keys="expandedKeys"
							accordion
							clearable
						>
							<template #default="{ node, data }">
								<div class="tree-node">
									<el-icon class="tree-icon">
										<Notebook v-if="data.data.type === 1" />
										<Memo v-else />
									</el-icon>
									<span class="tree-label">{{ node.label }}</span>
								</div>
							</template>
						</el-tree-select>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="菜单名称" prop="title">
						<el-input v-model="state.ruleForm.title" placeholder="请输入菜单名称" size="default" clearable />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="组件路径" prop="component">
						<el-input v-model="state.ruleForm.component" placeholder="请输入组件路径" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="路由名称" prop="name" class="form-item-tip">
						<el-input v-model="state.ruleForm.name" placeholder="请输入路由名称" clearable style="width: calc(100% - 20px)" />
						<el-tooltip class="box-item" effect="dark" content="名称与前端路由名称保持一致" placement="top-start">
							<el-icon><ele-InfoFilled /></el-icon>
						</el-tooltip>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="路由路径" prop="path">
						<el-input v-model="state.ruleForm.path" placeholder="请输入路由路径" clearable />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="菜单图标" prop="icon">
						<IconPicker placeholder="请输入菜单图标" v-model="state.ruleForm.icon" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="排序" prop="seq">
						<el-input-number v-model="state.ruleForm.seq" placeholder="请输入排序" :min="0" clearable />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="菜单类型" prop="type">
						<el-radio-group v-model="state.ruleForm.type">
							<el-radio-button :value="1"> 目录</el-radio-button>
							<el-radio-button :value="2"> 菜单</el-radio-button>
						</el-radio-group>
					</el-form-item>
				</el-col>
			</el-row>

			<div v-if="state.ruleForm.type == 2">
				<el-divider />
				<el-row class="mb15">
					<el-col :span="24">
						<el-form-item label="权限标识" prop="permission">
							<el-input v-model="state.ruleForm.permission" placeholder="请输入权限标识" />
						</el-form-item>
					</el-col>
				</el-row>
				<el-row class="mb15">
					<el-col :span="6">
						<el-form-item label="是否固定" prop="isAffix">
							<el-switch
								v-model="state.ruleForm.isAffix"
								width="50px"
								inline-prompt
								:active-value="1"
								:inactive-value="0"
								active-text="是"
								inactive-text="否"
							/>
						</el-form-item>
					</el-col>
					<el-col :span="6">
						<el-form-item label="是否缓存" prop="isCache">
							<el-switch
								v-model="state.ruleForm.isCache"
								width="50px"
								inline-prompt
								:active-value="1"
								:inactive-value="0"
								active-text="是"
								inactive-text="否"
							/>
						</el-form-item>
					</el-col>
					<el-col :span="6">
						<el-form-item label="是否隐藏" prop="isHide">
							<el-switch
								v-model="state.ruleForm.isHide"
								width="50px"
								inline-prompt
								:active-value="1"
								:inactive-value="0"
								active-text="是"
								inactive-text="否"
							/>
						</el-form-item>
					</el-col>
					<el-col :span="6">
						<el-form-item label="是否外链" prop="isIframe">
							<el-switch
								v-model="state.ruleForm.isIframe"
								width="50px"
								inline-prompt
								:active-value="1"
								:inactive-value="0"
								active-text="是"
								inactive-text="否"
							/>
						</el-form-item>
					</el-col>
				</el-row>
				<el-row class="mb15" v-if="state.ruleForm.isIframe == 1">
					<el-col :span="24">
						<el-form-item label="外链地址" prop="link">
							<el-input v-model="state.ruleForm.link" placeholder="请输入外链地址" />
						</el-form-item>
					</el-col>
				</el-row>
			</div>
		</el-form>
		<template #footer>
			<el-button @click="closeDialog" size="default">取 消</el-button>
			<el-button type="primary" @click="onSubmit" size="default" :loading="state.loading">{{ state.dialog.submitTxt }}</el-button>
		</template>
	</el-dialog>
</template>
<script setup lang="ts" name="systemMenuDialog">
import { defineAsyncComponent, reactive, ref, watch, nextTick } from 'vue';
import { Memo, Notebook } from '@element-plus/icons-vue';
import { useMenuApi } from '@/api/system/menu';
import { ElMessage } from 'element-plus';
import { objectCopyForm } from '@/utils/objectCopy';

// 引入组件
const IconPicker = defineAsyncComponent(() => import('@/components/YsIconPicker/index.vue'));

const menuDialogFormRef = ref();
const labelPosition = ref('right');

// 表单初始数据
const initialForm = {
	id: '',
	title: '', // 菜单名称
	component: 'layout/routerView/parent', // 组件路径
	path: '', // 路由路径
	seq: 0, // 排序
	pid: '', // 上级菜单
	name: '', // 路由名称
	redirect: '', // 路由重定向
	permission: '', // 权限标识
	icon: '', // 菜单图标
	link: '', // 外链地址
	type: 1, // 菜单类型
	isLink: 0, // 是否外链
	isIframe: 0, // 是否内嵌
	isHide: 0, // 是否隐藏
	isCache: 0, // 是否缓存
	isAffix: 0, // 是否固定
};

// 弹窗配置
const dialogConfig = {
	add: {
		title: '新增菜单',
		submitTxt: '新增',
	},
	edit: {
		title: '编辑菜单',
		submitTxt: '更新',
	},
};

const state = reactive({
	ruleForm: { ...initialForm }, // 表单数据
	menuData: [] as RouteItems, // 上级菜单数据
	dialog: {
		isShowDialog: false, // 弹窗是否显示
		type: '', // 弹窗类型 add、edit、view
		title: '', // 弹窗标题
		submitTxt: '', // 提交按钮文字
	},
	loading: false,
	isInitializing: false, // 是否正在初始化数据
});

// 展开的节点keys
const expandedKeys = ref<string[]>([]);

// 表单验证规则
const rules = reactive({
	type: [{ required: true, message: '请选择菜单类型', trigger: 'blur' }],
	name: [{ required: true, message: '请输入路由名称', trigger: 'blur' }],
	component: [{ required: true, message: '请输入组件路径', trigger: 'blur' }],
	title: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
	icon: [{ required: true, message: '请输入菜单图标', trigger: 'blur' }],
	permission: [{ required: true, message: '请输入权限标识', trigger: 'blur' }],
	link: [{ required: true, message: '请输入外链地址', trigger: 'blur' }],
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

	// 加载上级菜单数据
	const res = await useMenuApi().selectMenuTree();
	state.menuData = res.data;
};

// 监听菜单类型变化
watch(
	() => state.ruleForm.type,
	(val) => {
		// 如果正在初始化，则跳过重置逻辑
		if (state.isInitializing) return;

		// 根据类型设置默认组件路径
		state.ruleForm.component = val === 1 ? 'layout/routerView/parent' : '';
	}
);

// 提交表单
const onSubmit = async () => {
	// 表单验证
	const valid = await menuDialogFormRef.value?.validate();
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
<style scoped lang="scss">
.form-item-tip :deep(.el-form-item__content) {
	justify-content: space-between;
}
</style>
