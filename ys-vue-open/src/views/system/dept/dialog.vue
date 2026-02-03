<template>
	<YsDialog
		:title="state.dialog.title"
		v-model="state.dialog.isShowDialog"
		@close="state.dialog.isShowDialog = false"
		width="60%"
		:close-on-click-modal="false"
		:close-on-press-escape="false"
		:append-to-body="true"
	>
		<el-form ref="deptDialogFormRef" :model="state.ruleForm" :rules="rules" label-width="100px" :inline="false" label-position="right">
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="上级部门" prop="pid">
						<el-tree-select
							v-model="state.ruleForm.pid"
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
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="部门名称" prop="name">
						<el-input v-model="state.ruleForm.name" placeholder="请输入部门名称" size="default" clearable :readonly="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="部门简称" prop="shortName">
						<el-input v-model="state.ruleForm.shortName" placeholder="请输入部门简称" :readonly="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="部门编码" prop="code">
						<el-input v-model="state.ruleForm.code" placeholder="请输入部门编码" clearable :readonly="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="显示顺序" prop="seq">
						<el-input-number v-model="state.ruleForm.seq" placeholder="请输入显示顺序" :min="0" clearable :readonly="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="部门分类" prop="category">
						<el-select
							v-model="state.ruleForm.category"
							placeholder="请选择部门分类"
							clearable
							:disabled="state.dialog.type == 'view'"
							style="width: 100%"
						>
							<el-option label="公司" :value="1" />
							<el-option label="部门" :value="2" />
							<el-option label="小组" :value="3" />
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="部门性质" prop="deptType">
						<el-select
							v-model="state.ruleForm.deptType"
							placeholder="请选择部门性质"
							clearable
							:disabled="state.dialog.type == 'view'"
							style="width: 100%"
						>
							<el-option label="行政" :value="1" />
							<el-option label="技术" :value="2" />
							<el-option label="生产" :value="3" />
							<el-option label="销售" :value="4" />
							<el-option label="财务" :value="5" />
						</el-select>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="部门层级" prop="levelType">
						<el-select
							v-model="state.ruleForm.levelType"
							placeholder="请选择部门层级"
							clearable
							:disabled="state.dialog.type == 'view'"
							style="width: 100%"
						>
							<el-option label="总部" :value="1" />
							<el-option label="分公司" :value="2" />
							<el-option label="部门" :value="3" />
							<el-option label="科室" :value="4" />
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="所属地区" prop="area">
						<el-input v-model="state.ruleForm.area" placeholder="请输入所属地区" :readonly="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="联系人" prop="linkMan">
						<el-input v-model="state.ruleForm.linkMan" placeholder="请输入联系人" :readonly="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="联系电话" prop="linkPhone">
						<el-input v-model="state.ruleForm.linkPhone" placeholder="请输入联系电话" :readonly="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="部门邮箱" prop="email">
						<el-input v-model="state.ruleForm.email" placeholder="请输入部门邮箱" :readonly="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="传真号码" prop="fax">
						<el-input v-model="state.ruleForm.fax" placeholder="请输入传真号码" :readonly="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="邮政编码" prop="zipCode">
						<el-input v-model="state.ruleForm.zipCode" placeholder="请输入邮政编码" :readonly="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="部门图标" prop="icon">
						<IconPicker placeholder="请选择部门图标" v-model="state.ruleForm.icon" :disabled="state.dialog.type == 'view'" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="24">
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
			</el-row>
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="详细地址" prop="address">
						<el-input v-model="state.ruleForm.address" placeholder="请输入详细地址" :readonly="state.dialog.type == 'view'" />
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
							placeholder="请输入备注"
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

<script setup lang="ts" name="systemDeptDialog">
import { defineAsyncComponent, reactive, ref, nextTick } from 'vue';
import { useDeptApi } from '@/api/system/dept';
import { Shop, OfficeBuilding, School } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { objectCopyForm } from '@/utils/objectCopy';

// 引入组件
const IconPicker = defineAsyncComponent(() => import('@/components/YsIconPicker/index.vue'));

const deptDialogFormRef = ref();

// 表单初始数据
const initialForm = {
	id: '',
	name: '', // 部门名称
	shortName: '', // 部门简称
	code: '', // 部门编码
	pid: '', // 上级id
	category: undefined as number | undefined, // 部门分类(如:公司、部门、小组等)
	deptType: undefined as number | undefined, // 部门性质(如:行政、技术、生产等)
	levelType: undefined as number | undefined, // 部门层级(如:总部、分公司、部门等)
	area: '', // 所属地区
	email: '', // 部门邮箱
	zipCode: '', // 邮政编码
	address: '', // 详细地址
	fax: '', // 传真号码
	remark: '', // 备注
	linkMan: '', // 联系人
	linkPhone: '', // 联系电话
	seq: 0, // 显示顺序(值越小越靠前)
	icon: '', // 图标
	status: 1, // 状态 0.禁用 1.正常
};

// 弹窗配置
const dialogConfig = {
	add: {
		title: '新增部门',
		submitTxt: '新增',
	},
	edit: {
		title: '编辑部门',
		submitTxt: '更新',
	},
	view: {
		title: '部门详情',
	},
};

// 展开的节点keys
const expandedKeys = ref<string[]>([]);

const state = reactive({
	ruleForm: { ...initialForm }, // 表单数据
	deptData: [] as any[], // 上级部门数据
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
	name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
	code: [{ required: true, message: '请输入部门编码', trigger: 'blur' }],
	seq: [{ required: true, message: '请输入显示顺序', trigger: 'blur' }],
	category: [{ required: true, message: '请选择部门分类', trigger: 'change' }],
	linkPhone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }],
	email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
	zipCode: [{ pattern: /^\d{6}$/, message: '请输入正确的邮政编码', trigger: 'blur' }],
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
	if (row) {
		state.isInitializing = true;
		state.ruleForm = objectCopyForm(row, initialForm) as typeof initialForm;
		await nextTick();
		state.isInitializing = false;
	}

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
};

// 提交表单
const onSubmit = async () => {
	// 表单验证
	const valid = await deptDialogFormRef.value?.validate();
	if (!valid) return;
	// 防止重复提交
	if (state.loading) return;
	state.loading = true;

	// 根据类型调用不同的API
	const apiMethod = state.dialog.type === 'add' ? useDeptApi().addDept : useDeptApi().updateDept;

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
