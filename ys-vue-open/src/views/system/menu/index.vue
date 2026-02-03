<template>
	<div class="page-container">
		<el-card>
			<YsTable
				ref="tableRef"
				:options="tableOptions"
				:query-params="searchForm"
				:show-pagination="false"
				:events="tableEvents"
				@request-success="handleSuccess"
			>
				<!-- 页面头部区域 -->
				<template #page-header>
					<div class="page-header">
						<el-form :model="searchForm" ref="searchFormRef" label-width="90px">
							<el-input placeholder="请输入菜单名称" class="w-180 mr20" v-model="searchForm.title" clearable />
							<el-select class="m-2 w-180" v-model="searchForm.type" placeholder="请选择类型" clearable>
								<el-option label="目录" :value="1" />
								<el-option label="菜单" :value="2" />
								<el-option label="按钮" :value="3" />
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
				</template>

				<!-- 菜单名称列 -->
				<template #slots_title="scope">
					<SvgIcon :name="scope.row.icon" v-if="scope.row.type == 1 || scope.row.type == 2" />
					<SvgIcon name="cn cn-tag" v-if="scope.row.type == 3" />
					<span class="ml10">{{ $t(scope.row.title) }}</span>
				</template>

				<!-- 类型列 -->
				<template #slots_type="scope">
					<el-tag type="primary" v-if="scope.row.type == 1">目录</el-tag>
					<el-tag type="success" v-if="scope.row.type == 2">菜单</el-tag>
					<el-tag type="warning" v-if="scope.row.type == 3">按钮</el-tag>
				</template>

				<!-- 操作列 -->
				<template #slots_action="scope">
					<el-link type="primary" underline="never" class="pr6 pl6" @click="onOpenMenu('edit', scope.row)" v-auth="'system:menu:edit'">编辑</el-link>
					<el-link type="danger" underline="never" class="pr6 pl6" @click="onTabelRowDel(scope.row)" v-auth="'system:menu:delete'">删除</el-link>
					<el-link
						type="success"
						underline="never"
						class="pr6 pl6"
						@click="onOpenEditAuth('add', scope.row)"
						v-if="scope.row.type == 2 && scope.row.pid != null && scope.row.pid != ''"
						>权限设置</el-link
					>
				</template>
			</YsTable>
		</el-card>

		<!-- 引入组件 -->
		<MenuDialog ref="menuDialogRef" @refresh="searchTable()" />
		<!--权限设置弹窗-->
		<AuthDialog ref="authDialogRef" @refresh="searchTable()" />
	</div>
</template>
<script setup lang="ts" name="systemMenu">
import { defineAsyncComponent, ref, reactive } from 'vue';
import { ElMessageBox, ElMessage, FormInstance } from 'element-plus';
import { auth } from '@/utils/authFunction';
import { useMenuApi } from '@/api/system/menu';
import { useResetForm } from '@/utils/resetForm';

// 引入组件
const MenuDialog = defineAsyncComponent(() => import('@/views/system/menu/dialog.vue'));
const AuthDialog = defineAsyncComponent(() => import('@/views/system/menu/auth.vue'));
const searchFormRef = ref<FormInstance>();
// 定义变量内容
const menuDialogRef = ref();
const authDialogRef = ref();

const state = reactive({
	table: {
		loading: false,
	},
});

const searchForm = reactive({
	title: '',
	type: '',
});

// 表格配置
const tableOptions = reactive<any>({
	columnConfig: {
		resizable: true,
	},
	rowConfig: {
		keyField: 'id',
	},
	loading: state.table.loading,
	columns: [
		{ type: 'seq', width: 80, align: 'center' },
		{ title: '菜单名称', field: 'title', slots: { default: 'slots_title' }, treeNode: true },
		{ title: '路由名称', field: 'name' },
		{ title: '组件路径', field: 'component', showOverflow: 'tooltip' },
		{ title: '权限标识', field: 'permission' },
		{ title: '排序', field: 'seq', width: 70 },
		{ title: '类型', field: 'type', width: 80, slots: { default: 'slots_type' } },
		{ title: '操作', field: 'action', width: 180, align: 'center', slots: { default: 'slots_action' } },
	],
	toolbarConfig: {
		size: 'small', // 工具栏大小
		refresh: true, // 开启刷新
		zoom: true, // 开启缩放
		custom: true, // 开启自定义按钮
		buttons: [
			{ name: '新增', code: 'menuAdd', status: 'success', icon: 'ri-add-line', visible: auth('system:menu:add') },
			{ name: '展开全部', code: 'expandAll', status: 'primary', icon: 'ri-expand-height-line' },
			{ name: '折叠全部', code: 'clearExpand', status: 'error', icon: 'ri-collapse-vertical-line' },
		],
	},
	treeConfig: {
		// 开启树状表格
		transform: true, // 开启树状数据转换
		rowField: 'id', // 行数据的唯一标识字段
		parentField: 'pid', // 父节点的唯一标识字段
		reserve: true, // 开启保留节点
	},
	proxyConfig: {
		response: {
			list: 'data',
			message: 'msg',
		},
		ajax: {
			query: async () => {
				state.table.loading = true;
				return await useMenuApi().getMenuList(searchForm);
			},
			querySuccess: () => {
				tableRef.value.calculateMaxHeight();
				state.table.loading = false;
			},
		},
	},
});

const tableRef = ref();

const tableEvents = {
	toolbarButtonClick(params: any) {
		switch (params.code) {
			case 'menuAdd':
				onOpenMenu('add');
				break;
			case 'expandAll':
				tableRef.value.tableRef.setAllTreeExpand(true);
				break;
			case 'clearExpand':
				tableRef.value.tableRef.clearTreeExpand();
				break;
		}
	},
};

// 查询
const searchTable = (resetPage = true) => {
	tableRef.value?.search(resetPage);
};

// 重置表单
const { resetForm } = useResetForm(searchForm);
const handleReset = () => {
	resetForm();
	searchFormRef.value?.resetFields(); // 重置表单验证状态
	searchTable();
};

// 请求成功回调
function handleSuccess(data: any) {
	// 如果需要对数据进行处理，可以在这里实现
}
// 打开菜单操作弹窗（新增/编辑）
const onOpenMenu = (type: string, row?: any) => {
	if (type === 'edit' && row.type === 3) {
		onOpenEditAuth('edit', row);
	} else {
		menuDialogRef.value.openDialog(type, row);
	}
};

// 权限设置
function onOpenEditAuth(type: string, row: any) {
	authDialogRef.value.openDialog(type, row);
}

// 删除
function onTabelRowDel(row: any) {
	ElMessageBox.confirm('此操作将永久删除该菜单及其子菜单, 是否继续?', '提示', {
		confirmButtonText: '删除',
		cancelButtonText: '取消',
		type: 'warning',
	})
		.then(() => {
			useMenuApi()
				.deleteMenu(row.id)
				.then(() => {
					ElMessage.success('删除成功');
					searchTable();
				});
		})
		.catch(() => {});
}

</script>
