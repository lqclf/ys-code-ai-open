<template>
	<div class="page-container">
		<el-card>
			<el-row style="height: 100%">
				<el-col :span="12">
					<YsTable
						ref="mainTableRef"
						:request-fn="useDictApi().list"
						:options="mainTableOptions"
						:query-params="mainSearchForm"
						:show-pagination="true"
						:events="mainTableEvents"
					>
						<!-- 页面头部区域 -->
						<template #page-header>
							<div class="page-header">
								<el-form :model="mainSearchForm" ref="mainSearchFormRef" label-width="90px">
									<el-input v-model="mainSearchForm.name" class="w-180 mr20" placeholder="请输入名称" clearable />
									<el-button type="primary" @click="searchMainTable(true)"><i class="ri-search-line"></i>查询</el-button>
									<el-button @click="resetMainTable"><i class="ri-reset-left-line"></i>重置</el-button>
								</el-form>
							</div>
						</template>

						<!-- 操作列 -->
						<template #action="scope">
							<div class="operation-btn-group">
								<el-link type="primary" underline="never" class="pr6 pl6" @click="onOpenEditMain('edit', scope.row)" v-auth="'system:dict:edit'"> 编辑 </el-link>
								<el-link type="danger" underline="never" class="pr6 pl6" @click="onRowDelMain(scope.row)" v-auth="'system:dict:delete'"> 删除 </el-link>
							</div>
						</template>
						<template #status="{ row }">
							<el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
						</template>
					</YsTable>
				</el-col>
				<el-col :span="12">
					<YsTable
						ref="detailTableRef"
						:request-fn="useDictApi().getDetailList"
						:options="detailTableOptions"
						:query-params="detailSearchForm"
						:show-pagination="true"
						:events="detailTableEvents"
					>
						<!-- 页面头部区域 -->
						<template #page-header>
							<div class="page-header">
								<el-form :model="detailSearchForm" ref="detailSearchFormRef">
									<el-input v-model="detailSearchForm.name" class="w-180 mr20" placeholder="请输入名称" clearable />
									<el-button type="primary" @click="searchDetailTable(true)"><i class="ri-search-line"></i>查询</el-button>
									<el-button @click="resetDetailTable"><i class="ri-reset-left-line"></i>重置</el-button>
								</el-form>
							</div>
						</template>

						<!-- 操作列 -->
						<template #action="{ row }">
							<div class="operation-btn-group">
								<el-link type="primary" underline="never" class="pr6 pl6" @click="onOpenEditDetail('edit', row)" v-auth="'system:dict:edit'"> 编辑 </el-link>
								<el-link type="danger" underline="never" class="pr6 pl6" @click="onRowDelDetail(row)" v-auth="'system:dict:delete'"> 删除 </el-link>
							</div>
						</template>
					</YsTable>
				</el-col>
			</el-row>
		</el-card>
		<!-- 引入组件 -->
		<MainDialog ref="mainDialogRef" @refresh="refreshMainTable()" />
		<DetailDialog ref="detailDialogRef" @refresh="refreshDetailTable()" />
	</div>
</template>

<script setup lang="ts" name="systemDict">
import { defineAsyncComponent, reactive, ref, computed } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import { useDictApi } from '@/api/system/dict';
import { useUserInfo } from '@/stores/userInfo';
import { storeToRefs } from 'pinia';

// 引入弹窗组件
const MainDialog = defineAsyncComponent(() => import('@/views/system/dict/mainDialog.vue'));
const DetailDialog = defineAsyncComponent(() => import('@/views/system/dict/detailDialog.vue'));

const mainDialogRef = ref();
const detailDialogRef = ref();
const mainTableRef = ref();
const detailTableRef = ref();
const mainSearchFormRef = ref();
const detailSearchFormRef = ref();
const isDetailAddVisible = ref(false);

// 权限检查
const stores = useUserInfo();
const { userInfos } = storeToRefs(stores);

/**
 * 检查权限
 * @param auth 权限标识
 * @returns 是否拥有该权限
 */
const hasAuth = (auth: string) => {
	return userInfos.value.authBtnList.some((v: string) => v === auth);
};
// 状态管理
const state = reactive({
	currentMainRow: null as any,
});

// 主表搜索表单
const mainSearchForm = reactive({
	name: '',
});

// 明细表搜索表单
const detailSearchForm = reactive({
	name: '',
	dictId: '',
});

const mainTableOptions = reactive<any>({
	columns: [
		{ type: 'seq', width: 80, align: 'center' },
		{ title: '名称', field: 'name' },
		{ title: '编码', field: 'code' },
		{ title: '状态', field: 'status', width: 80, slots: { default: 'status' } },
		{ title: '操作', field: 'action', width: 120, slots: { default: 'action' }, align: 'center' },
	],
	rowConfig: {
		isCurrent: true, // 开启当前行选中
		isHover: true, // 开启鼠标悬停选中
	},
	// 工具栏配置
	toolbarConfig: {
		size: 'small', // 工具栏大小
		refresh: true, // 开启刷新
		buttons: computed(() => {
			const allButtons = [
				{ name: '新增', code: 'add', status: 'success', icon: 'ri-add-line', auth: 'system:dict:add' },
			];
			return allButtons.filter(btn => !btn.auth || hasAuth(btn.auth)).map(({ auth, ...rest }) => rest);
		}),
	},
});

const detailTableOptions = reactive<any>({
	columns: [
		{ type: 'seq', width: 80, align: 'center' },
		{ title: '名称', field: 'name' },
		{ title: '编码', field: 'code' },
		{ title: '序号', field: 'seq', width: 80 },
		{ title: '操作', field: 'action', width: 120, slots: { default: 'action' }, align: 'center' },
	],
	// 工具栏配置
	toolbarConfig: {
		size: 'small', // 工具栏大小
		refresh: true, // 开启刷新
		buttons: computed(() => {
			const allButtons = [
				{ name: '新增', code: 'add', status: 'success', icon: 'ri-add-line', visible: isDetailAddVisible, auth: 'system:dict:add' },
			];
			return allButtons.filter(btn => !btn.auth || hasAuth(btn.auth)).map(({ auth, ...rest }) => rest);
		}),
	},
});

const mainTableEvents = {
	toolbarButtonClick(params: any) {
		switch (params.code) {
			case 'add':
				onOpenAddMain('add');
				break;
		}
	},
	// 主表行点击事件
	currentRowChange(rowIndex: any) {
		state.currentMainRow = rowIndex.row;
		detailSearchForm.dictId = rowIndex.row?.id || '';
		isDetailAddVisible.value = !!state.currentMainRow;
		// 刷新明细表
		if (state.currentMainRow) {
			detailTableRef.value?.search(detailSearchForm);
		}
	},
};
// 明细表事件
const detailTableEvents = {
	toolbarButtonClick(params: any) {
		switch (params.code) {
			case 'add':
				onOpenAddDetail('add');
				break;
		}
	},
};

// 主表搜索方法
const searchMainTable = (resetPage = true) => {
	mainTableRef.value?.search(resetPage);
	detailTableRef.value?.reset();
};

// 主表重置方法
const resetMainTable = () => {
	// 重置表格
	mainTableRef.value?.reset();
	detailTableRef.value?.reset();
};

// 明细表搜索方法
const searchDetailTable = (resetPage = true) => {
	if (state.currentMainRow) {
		detailTableRef.value?.search(resetPage);
	}
};

// 明细表重置方法
const resetDetailTable = () => {
	// 重置表格
	mainTableRef.value?.tableRef.clearCurrentRow();
	detailTableRef.value?.reset();
};

// 刷新主表
const refreshMainTable = () => {
	mainTableRef.value?.refresh();
};
// 刷新明细表
const refreshDetailTable = () => {
	detailTableRef.value?.refresh();
};

// 主表新增
const onOpenAddMain = (type: string) => {
	mainDialogRef.value.openDialog(type);
};

// 主表编辑
const onOpenEditMain = (type: string, row: any) => {
	mainDialogRef.value.openDialog(type, row);
};

// 主表删除
const onRowDelMain = (row: any) => {
	ElMessageBox.confirm(`此操作将永久删除字典：${row.name}, 是否继续?`, '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	})
		.then(() => {
			useDictApi()
				.delete(row.id)
				.then(() => {
					ElMessage.success('删除成功');
					mainTableRef.value?.refresh();
				});
		})
		.catch(() => {});
};

// 明细表新增
const onOpenAddDetail = (type: string) => {
	if (state.currentMainRow) {
		detailDialogRef.value.openDialog(type, { dictId: state.currentMainRow.id, dictCode: state.currentMainRow.code });
	}
};

// 明细表编辑
const onOpenEditDetail = (type: string, row: any) => {
	detailDialogRef.value.openDialog(type, row);
};

// 明细表删除
const onRowDelDetail = (row: any) => {
	ElMessageBox.confirm(`此操作将永久删除字典明细：${row.name}, 是否继续?`, '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	})
		.then(() => {
			useDictApi()
				.delDetail(row.id)
				.then(() => {
					ElMessage.success('删除成功');
					detailTableRef.value?.refresh();
				});
		})
		.catch(() => {});
};
</script>
