<template>
	<div class="page-container">
		<el-card>
			<!-- 页面头部区域 -->
			<div class="page-header mb15">
				<el-form :model="searchForm" ref="searchFormRef" label-width="90px">
					<el-input placeholder="请输入配置名称" class="w-180 mr20" v-model="searchForm.name" clearable />
					<el-select class="m-2 w-180" v-model="searchForm.type" placeholder="请选择类型" clearable>
						<el-option label="系统配置" :value="1" />
						<el-option label="业务配置" :value="2" />
					</el-select>
					<el-button type="primary" class="ml10" @click="searchTable">
						<i class="ri-search-line"></i>
						查询
					</el-button>
					<el-button type="default" class="ml10" @click="handleReset">
						<i class="ri-reset-left-line"></i>
						重置
					</el-button>
				</el-form>
			</div>

			<!-- 表格工具栏 -->
			<div class="table-toolbar mb15">
				<el-button type="success" @click="onOpenConfig('add')" v-auth="'system:config:add'">
					<i class="ri-add-line"></i>
					新增
				</el-button>
			</div>

			<!-- 表格区域 -->
			<el-table ref="tableRef" :data="tableData" v-loading="state.loading" border stripe highlight-current-row>
				<el-table-column type="index" label="序号" width="80" align="center" />
				<el-table-column prop="name" label="配置名称" />
				<el-table-column prop="key" label="配置键" />
				<el-table-column prop="value" label="配置值" show-overflow-tooltip />
				<el-table-column prop="seq" label="排序" width="70" />
				<el-table-column label="类型" width="100">
					<template #default="scope">
						<el-tag type="primary" v-if="scope.row.type == 1">系统配置</el-tag>
						<el-tag type="success" v-if="scope.row.type == 2">业务配置</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="180" align="center" fixed="right">
					<template #default="scope">
						<el-link type="primary" underline="never" class="pr6 pl6" @click="onOpenConfig('edit', scope.row)" v-auth="'system:config:edit'">编辑</el-link>
						<el-link type="danger" underline="never" class="pr6 pl6" @click="onTabelRowDel(scope.row)" v-auth="'system:config:delete'">删除</el-link>
					</template>
				</el-table-column>
			</el-table>

			<!-- 分页区域 -->
			<div class="pagination-container mt15">
				<el-pagination
					v-model:current-page="state.page"
					v-model:page-size="state.size"
					:page-sizes="[10, 20, 50, 100]"
					:total="state.total"
					layout="total, sizes, prev, pager, next, jumper"
					@size-change="handleSizeChange"
					@current-change="handleCurrentChange"
				/>
			</div>
		</el-card>

		<!-- 引入组件 -->
		<ConfigDialog ref="configDialogRef" @refresh="getTableData" />
	</div>
</template>
<script setup lang="ts" name="systemConfig">
import { defineAsyncComponent, ref, reactive, onMounted } from 'vue';
import { ElMessageBox, ElMessage, FormInstance } from 'element-plus';
import { useConfigApi } from '@/api/system/config';

// 引入组件
const ConfigDialog = defineAsyncComponent(() => import('@/views/system/config/dialog.vue'));

const searchFormRef = ref<FormInstance>();
// 定义变量内容
const configDialogRef = ref();
const tableRef = ref();

const state = reactive({
	loading: false,
	page: 1,
	size: 10,
	total: 0,
});

const searchForm = reactive({
	name: '',
	type: '',
});

// 表格数据
const tableData = ref<any[]>([]);

// 获取表格数据
const getTableData = async () => {
	state.loading = true;
	try {
		const res = await useConfigApi().list({
			pageNum: state.page,
			pageSize: state.size,
			...searchForm,
		});
		if (res.code === 200 || res.success) {
			tableData.value = res.data.list || [];
			state.total = res.data.total || 0;
		}
	} catch (error) {
		console.error('获取配置列表失败:', error);
		ElMessage.error('获取配置列表失败');
	} finally {
		state.loading = false;
	}
};

// 查询
const searchTable = () => {
	state.page = 1;
	getTableData();
};

// 重置表单
const handleReset = () => {
	searchForm.name = '';
	searchForm.type = '';
	searchFormRef.value?.resetFields();
	state.page = 1;
	getTableData();
};

// 分页大小改变
const handleSizeChange = (val: number) => {
	state.size = val;
	getTableData();
};

// 页码改变
const handleCurrentChange = (val: number) => {
	state.page = val;
	getTableData();
};

// 打开配置操作弹窗（新增/编辑）
const onOpenConfig = (type: string, row?: any) => {
	configDialogRef.value.openDialog(type, row);
};

// 删除
function onTabelRowDel(row: any) {
	ElMessageBox.confirm('此操作将永久删除该配置, 是否继续?', '提示', {
		confirmButtonText: '删除',
		cancelButtonText: '取消',
		type: 'warning',
	})
		.then(() => {
			useConfigApi()
				.delete(row.id)
				.then(() => {
					ElMessage.success('删除成功');
					getTableData();
				});
		})
		.catch(() => {});
}

// 页面加载时
onMounted(() => {
	getTableData();
});
</script>
