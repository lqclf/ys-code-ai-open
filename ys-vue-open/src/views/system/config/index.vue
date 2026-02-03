<template>
	<div class="page-container">
		<el-card>
			<YsTable
				ref="tableRef"
				:request-fn="useConfigApi().list"
				:options="tableOptions"
				:query-params="searchForm"
				:show-pagination="true"
				:events="tableEvents"
				@request-success="handleSuccess"
			>
				<!-- 页面头部区域 -->
				<template #page-header>
					<div class="page-header">
						<el-form :model="searchForm" ref="searchFormRef" label-width="90px">
							<el-input placeholder="请输入配置名称" class="w-180 mr20" v-model="searchForm.name" clearable @keyup.enter="searchTable" />
							<el-input placeholder="请输入配置编码" class="w-180 mr20" v-model="searchForm.code" clearable @keyup.enter="searchTable" />
							<el-button type="primary" class="ml10" @click="searchTable">
								<i class="ri-search-line"></i>
								查询
							</el-button>
							<el-button type="default" class="ml10" @click="resetTable">
								<i class="ri-reset-left-line"></i>
								重置
							</el-button>
						</el-form>
					</div>
				</template>

				<!-- 操作列 -->
				<template #action="scope">
					<div class="operation-btn-group">
						<el-link type="primary" underline="never" class="pr6 pl6" @click="openDialogCRU('edit', scope.row)"> 编辑 </el-link>
						<el-link type="danger" underline="never" class="pr6 pl6" @click="onDelete(scope.row)"> 删除 </el-link>
						<el-link type="success" underline="never" class="pr6 pl6" @click="openDialogCRU('view', scope.row)"> 详情 </el-link>
					</div>
				</template>
			</YsTable>
		</el-card>

		<!-- 引入组件 -->
		<ConfigDialog ref="configDialogRef" />
	</div>
</template>

<script lang="ts" setup name="systemConfig">
import { defineAsyncComponent, reactive, ref } from 'vue';
import { FormInstance, ElMessage, ElMessageBox } from 'element-plus';
import { useConfigApi } from '@/api/system/config';

// 引入组件
const ConfigDialog = defineAsyncComponent(() => import('@/views/system/config/dialog.vue'));

// 定义变量内容
const configDialogRef = ref();
const searchFormRef = ref<FormInstance>();
const tableRef = ref();

// 搜索表单
const searchForm = reactive({
	name: '',
	code: '',
});

const tableOptions = reactive<any>({
	columns: [
		{ type: 'seq', width: 80, align: 'center' },
		{ title: '名称', field: 'name' },
		{ title: '编码', field: 'code' },
		{ title: '配置值', field: 'value' },
		{ title: '分类', field: 'category', width: 120 },
		{ title: '备注', field: 'remark' },
		{ title: '创建时间', field: 'createTime', width: 160 },
		{ title: '操作', field: 'action', width: 180, slots: { default: 'action' }, align: 'center' },
	],
	// 工具栏配置
	toolbarConfig: {
		size: 'small', // 工具栏大小
		refresh: true, // 开启刷新
		zoom: true, // 开启缩放
		custom: true, // 开启自定义按钮
		buttons: [{ name: '新增', code: 'add', status: 'success', icon: 'ri-add-line' }],
	},
});

const tableEvents = {
	toolbarButtonClick(params: any) {
		switch (params.code) {
			case 'add':
				openDialogCRU('add');
				break;
		}
	},
};
// 搜索方法
const searchTable = (resetPage = true) => {
	tableRef.value?.search(resetPage);
};

// 重置方法
const resetTable = () => {
	// 重置表格
	tableRef.value?.reset();
};

// 数据加载成功回调
const handleSuccess = (res: any) => {
	//console.log('数据加载成功', res);
};

// 打开配置操作弹窗（新增/编辑）
const openDialogCRU = (type: string, row?: any) => {
	configDialogRef.value.openDialog(type, row);
};

// 删除当前行
const onDelete = (row: any) => {
	deleteRow(row);
};

// 删除方法
const deleteRow = async (row: any, message?: string) => {
	const confirmMessage = message || `此操作将永久删除该配置, 是否继续?`;
	try {
		await ElMessageBox.confirm(confirmMessage, '提示', {
			confirmButtonText: '删除',
			cancelButtonText: 'cancel',
			type: 'warning',
		});

		const res = await useConfigApi().delete(row.id);
		if (res.code === 200 || res.success) {
			ElMessage.success('删除成功');
			tableRef.value?.refresh();
		} else {
			ElMessage.error(res.msg || '删除失败');
		}
	} catch (error) {
		// 用户取消删除
	}
};
</script>

<style scoped>
.operation-btn-group {
	display: flex;
	align-items: center;
	gap: 8px;
}
</style>
