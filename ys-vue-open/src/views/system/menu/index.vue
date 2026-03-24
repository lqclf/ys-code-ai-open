<template>
	<div class="page-container">
		<el-card>
			<!-- 页面头部区域 -->
			<div class="page-header mb15">
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

			<!-- 表格工具栏 -->
			<div class="table-toolbar mb15">
				<el-button type="success" @click="onOpenMenu('add')" v-auth="'system:menu:add'">
					<i class="ri-add-line"></i>
					新增
				</el-button>
				<el-button type="primary" @click="expandAll">
					<i class="ri-expand-height-line"></i>
					展开全部
				</el-button>
				<el-button type="danger" @click="collapseAll">
					<i class="ri-collapse-vertical-line"></i>
					折叠全部
				</el-button>
			</div>

			<!-- 表格区域 -->
			<el-table
				ref="tableRef"
				:data="tableData"
				v-loading="state.loading"
				row-key="id"
				border
				stripe
				default-expand-all
				highlight-current-row
				:tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
			>
				<el-table-column type="index" label="序号" width="80" align="center" />
				<el-table-column label="菜单名称" min-width="200">
					<template #default="scope">
						<SvgIcon :name="scope.row.icon" v-if="scope.row.type == 1 || scope.row.type == 2" />
						<SvgIcon name="cn cn-tag" v-if="scope.row.type == 3" />
						<span class="ml10">{{ $t(scope.row.title) }}</span>
					</template>
				</el-table-column>
				<el-table-column prop="name" label="路由名称" />
				<el-table-column prop="component" label="组件路径" show-overflow-tooltip />
				<el-table-column prop="permission" label="权限标识" />
				<el-table-column prop="seq" label="排序" width="70" />
				<el-table-column label="类型" width="80">
					<template #default="scope">
						<el-tag type="primary" v-if="scope.row.type == 1">目录</el-tag>
						<el-tag type="success" v-if="scope.row.type == 2">菜单</el-tag>
						<el-tag type="warning" v-if="scope.row.type == 3">按钮</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="180" align="center" fixed="right">
					<template #default="scope">
						<el-link type="primary" underline="never" class="pr6 pl6" @click="onOpenMenu('edit', scope.row)" v-auth="'system:menu:edit'">编辑</el-link>
						<el-link type="danger" underline="never" class="pr6 pl6" @click="onTabelRowDel(scope.row)" v-auth="'system:menu:delete'">删除</el-link>
						<el-link
							type="success"
							underline="never"
							class="pr6 pl6"
							@click="onOpenEditAuth('add', scope.row)"
							v-if="scope.row.type == 2 && scope.row.pid != null && scope.row.pid != ''"
						>权限设置</el-link>
					</template>
				</el-table-column>
			</el-table>
		</el-card>

		<!-- 引入组件 -->
		<MenuDialog ref="menuDialogRef" @refresh="getTableData" />
		<!--权限设置弹窗-->
		<AuthDialog ref="authDialogRef" @refresh="getTableData" />
	</div>
</template>
<script setup lang="ts" name="systemMenu">
import { defineAsyncComponent, ref, reactive, onMounted } from 'vue';
import { ElMessageBox, ElMessage, FormInstance } from 'element-plus';
import { useMenuApi } from '@/api/system/menu';

// 引入组件
const MenuDialog = defineAsyncComponent(() => import('@/views/system/menu/dialog.vue'));
const AuthDialog = defineAsyncComponent(() => import('@/views/system/menu/auth.vue'));

const searchFormRef = ref<FormInstance>();
// 定义变量内容
const menuDialogRef = ref();
const authDialogRef = ref();
const tableRef = ref();

const state = reactive({
	loading: false,
});

const searchForm = reactive({
	title: '',
	type: '',
});

// 表格数据
const tableData = ref<any[]>([]);

// 获取表格数据
const getTableData = async () => {
	state.loading = true;
	try {
		const res = await useMenuApi().getMenuList(searchForm);
		if (res.code === 200 || res.success) {
			tableData.value = buildTreeData(res.data || []);
		}
	} catch (error) {
		console.error('获取菜单列表失败:', error);
		ElMessage.error('获取菜单列表失败');
	} finally {
		state.loading = false;
	}
};

// 构建树形数据
const buildTreeData = (data: any[]) => {
	const result: any[] = [];
	const map: { [key: string]: any } = {};

	// 先创建所有节点的映射
	data.forEach((item) => {
		map[item.id] = {
			...item,
			children: [],
		};
	});

	// 构建树形结构
	data.forEach((item) => {
		if (item.pid && map[item.pid]) {
			map[item.pid].children.push(map[item.id]);
		} else {
			result.push(map[item.id]);
		}
	});

	return result;
};

// 查询
const searchTable = () => {
	getTableData();
};

// 重置表单
const handleReset = () => {
	searchForm.title = '';
	searchForm.type = '';
	searchFormRef.value?.resetFields();
	getTableData();
};

// 展开全部
const expandAll = () => {
	const expandRows = (rows: any[]) => {
		rows.forEach((row) => {
			tableRef.value?.toggleRowExpansion(row, true);
			if (row.children && row.children.length > 0) {
				expandRows(row.children);
			}
		});
	};
	expandRows(tableData.value);
};

// 折叠全部
const collapseAll = () => {
	const collapseRows = (rows: any[]) => {
		rows.forEach((row) => {
			tableRef.value?.toggleRowExpansion(row, false);
			if (row.children && row.children.length > 0) {
				collapseRows(row.children);
			}
		});
	};
	collapseRows(tableData.value);
};

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
