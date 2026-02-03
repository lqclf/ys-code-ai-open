<template>
	<div class="dept-detail">
		<!-- 表单区域 -->
		<el-form ref="formRef" :model="formData" :rules="rules" label-width="100px" label-position="right" class="detail-form">
			<!-- 上级部门选择 -->
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="上级部门" prop="pid">
						<el-tree-select
							v-model="formData.pid"
							:data="parentDeptData"
							check-strictly
							:render-after-expand="false"
							accordion
							clearable
							placeholder="请选择上级部门"
							disabled
						/>
					</el-form-item>
				</el-col>
			</el-row>

			<!-- 基本信息 -->
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="部门名称" prop="name">
						<el-input v-model="formData.name" placeholder="请输入部门名称" size="default" clearable />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="部门简称" prop="shortName">
						<el-input v-model="formData.shortName" placeholder="请输入部门简称" />
					</el-form-item>
				</el-col>
			</el-row>

			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="部门编码" prop="code">
						<el-input v-model="formData.code" placeholder="请输入部门编码" clearable />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="显示顺序" prop="seq">
						<el-input-number v-model="formData.seq" placeholder="请输入显示顺序" :min="0" clearable style="width: 100%" />
					</el-form-item>
				</el-col>
			</el-row>

			<!-- 分类信息 -->
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="部门分类" prop="category">
						<el-select v-model="formData.category" placeholder="请选择部门分类" clearable style="width: 100%">
							<el-option label="公司" :value="1" />
							<el-option label="部门" :value="2" />
							<el-option label="小组" :value="3" />
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="部门性质" prop="deptType">
						<el-select v-model="formData.deptType" placeholder="请选择部门性质" clearable style="width: 100%">
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
						<el-select v-model="formData.levelType" placeholder="请选择部门层级" clearable style="width: 100%">
							<el-option label="总部" :value="1" />
							<el-option label="分公司" :value="2" />
							<el-option label="部门" :value="3" />
							<el-option label="科室" :value="4" />
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="所属地区" prop="area">
						<el-input v-model="formData.area" placeholder="请输入所属地区" />
					</el-form-item>
				</el-col>
			</el-row>

			<!-- 联系信息 -->
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="联系人" prop="linkMan">
						<el-input v-model="formData.linkMan" placeholder="请输入联系人" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="联系电话" prop="linkPhone">
						<el-input v-model="formData.linkPhone" placeholder="请输入联系电话" />
					</el-form-item>
				</el-col>
			</el-row>

			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="部门邮箱" prop="email">
						<el-input v-model="formData.email" placeholder="请输入部门邮箱" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="传真号码" prop="fax">
						<el-input v-model="formData.fax" placeholder="请输入传真号码" />
					</el-form-item>
				</el-col>
			</el-row>

			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="邮政编码" prop="zipCode">
						<el-input v-model="formData.zipCode" placeholder="请输入邮政编码" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="部门图标" prop="icon">
						<IconPicker placeholder="请选择部门图标" v-model="formData.icon" />
					</el-form-item>
				</el-col>
			</el-row>

			<!-- 状态信息 -->
			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="状态" prop="status">
						<el-switch
							v-model="formData.status"
							width="50px"
							inline-prompt
							:active-value="1"
							:inactive-value="0"
							active-text="正常"
							inactive-text="禁用"
						/>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="详细地址" prop="address">
						<el-input v-model="formData.address" placeholder="请输入详细地址" />
					</el-form-item>
				</el-col>
			</el-row>

			<el-row class="mb15">
				<el-col :span="24">
					<el-form-item label="备注" prop="remark">
						<el-input v-model="formData.remark" type="textarea" :rows="3" maxlength="200" show-word-limit placeholder="请输入备注" />
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>

		<!-- 底部按钮 -->
		<div class="form-footer">
			<el-button type="primary" @click="handleUpdate" :loading="saving" size="default">更新</el-button>
		</div>
	</div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted, defineAsyncComponent } from 'vue';
import { ElMessage, FormInstance } from 'element-plus';
import { useDeptApi } from '@/api/system/dept';
import { objectCopyForm } from '@/utils/objectCopy';
// 引入组件
const IconPicker = defineAsyncComponent(() => import('@/components/YsIconPicker/index.vue'));
interface DeptData {
	id: string;
	name: string;
	shortName: string;
	code: string;
	seq: number;
	levelType: number;
	area: string;
	linkMan: string;
	phone: string;
	address: string;
	remark: string;
	[key: string]: any;
}

// Props 定义
const props = defineProps<{
	deptData: DeptData;
}>();

// Emits 定义
const emit = defineEmits<{
	update: [data: DeptData];
	refresh: [];
}>();

// 响应式数据
const formRef = ref<FormInstance>();
const saving = ref(false);
const parentDeptData = ref<DeptData[]>([]);

// 表单数据
const formData = reactive<DeptData>({
	id: '',
	name: '',
	shortName: '',
	code: '',
	seq: 0,
	levelType: 1,
	area: '',
	linkMan: '',
	phone: '',
	address: '',
	remark: '',
});

// 表单验证规则
const rules = {
	name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
	code: [{ required: true, message: '请输入部门编码', trigger: 'blur' }],
	levelType: [{ required: true, message: '请选择部门层级', trigger: 'change' }],
	seq: [{ required: true, message: '请输入显示顺序', trigger: 'blur' }],
};

// 监听 props 变化，更新表单数据
watch(
	() => props.deptData,
	(newData) => {
		if (newData) {
			const initialForm = {
				id: '',
				name: '',
				shortName: '',
				code: '',
				seq: 0,
				pid: '',
				category: undefined as number | undefined,
				deptType: undefined as number | undefined,
				levelType: undefined as number | undefined,
				area: '',
				linkMan: '',
				linkPhone: '',
				email: '',
				fax: '',
				zipCode: '',
				address: '',
				remark: '',
				status: 1,
				icon: '',
			};
			Object.assign(formData, objectCopyForm(newData, initialForm));
		}
	},
	{ immediate: true, deep: true }
);

// 更新部门信息
const handleUpdate = async () => {
	if (!formRef.value) return;

	try {
		await formRef.value.validate();
		saving.value = true;

		// 调用更新API
		const response = await useDeptApi().updateDept(formData);

		if (response && response.code === 200) {
			ElMessage.success('部门信息更新成功');
			emit('update', formData);
			emit('refresh');
		} else {
			ElMessage.error(response?.msg || '更新失败');
		}
	} catch (error) {
		console.error('更新失败:', error);
		ElMessage.error('更新失败，请检查输入信息');
	} finally {
		saving.value = false;
	}
};

// 加载上级部门数据
const loadParentDeptData = async () => {
	try {
		const res = await useDeptApi().selectDeptTree();
		parentDeptData.value = res.data || [];
	} catch (error) {
		console.error('加载部门数据失败:', error);
	}
};

// 组件挂载时加载数据
onMounted(() => {
	loadParentDeptData();
});
</script>

<style scoped lang="scss">
.dept-detail {
	height: 100%;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.detail-form {
	padding: 10px;
	overflow-y: auto;
}

.form-footer {
	padding-top: 20px;
	border-top: 1px solid var(--el-border-color-light);
	display: flex;
	justify-content: flex-end;
	gap: 15px;
}
</style>
