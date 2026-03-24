<template>
	<el-dialog
		v-model="state.dialog.isShowDialog"
		@close="onCancel"
		width="75%"
		:close-on-click-modal="false"
		:close-on-press-escape="false"
		:append-to-body="true"
		align-center
		draggable
	>
		<template #header>
			<div class="dialog-header">
				<span class="dialog-title">{{ state.dialog.title }}：</span>
				<el-tag type="primary"><i class="ri-shield-user-line"></i> {{ state.row.name }}</el-tag>
			</div>
		</template>
		<div class="text item page-content" style="flex: 1; overflow: auto; display: flex; flex-direction: column">
			<vxe-grid ref="tableRef" v-bind="tableOption" v-on="tableEvents">
				<template #toolbarButtons>
					<div class="link-group">
						<el-link type="primary" @click="expandAll" title="展开所有" underline="never">
							<i class="ri-expand-height-line"></i>
						</el-link>
						<el-link type="primary" @click="clearExpand" title="收起所有" underline="never">
							<i class="ri-collapse-vertical-line"></i>
						</el-link>
						<el-link type="primary" @click="checkBoxAll" title="全选" underline="never">
							<i class="ri-checkbox-multiple-line"></i>
						</el-link>
						<el-link type="primary" @click="checkBoxClearAll" title="全不选" underline="never">
							<i class="ri-checkbox-multiple-blank-line"></i>
						</el-link>
					</div>
				</template>
				<template #slots_btn="{ row }">
					<el-checkbox-group v-model="state.roleIds" @change="(val: string[]) => handleBtnGroupChange(val, row)">
						<el-checkbox v-for="value in row.btnList" :key="value.id" :label="value.id">
							{{ value.title }}
						</el-checkbox>
					</el-checkbox-group>
				</template>
				<template #checkbox_title="{ row }">
					<el-checkbox :model-value="state.roleIds.includes(row.id)" @change="(val: boolean) => handleParentCheckboxChange(val, row)">
						{{ row.title }}
					</el-checkbox>
				</template>
			</vxe-grid>
		</div>
		<template #footer>
			<el-button @click="onCancel">取消</el-button>
			<el-button type="primary" @click="handleSubmit" :loading="state.loading">确定</el-button>
		</template>
	</el-dialog>
</template>
<script setup lang="ts" name="systemRoleAuth">
import { reactive, ref } from 'vue';
import type { VxeGridProps, VxeGridInstance, VxeGridListeners } from 'vxe-table';
import { ElMessage } from 'element-plus';

import { useMenuApi } from '@/api/system/menu';
import { useRoleApi } from '@/api/system/role';

/**
 * 菜单
 */
interface Menu {
	id: string;
	title: string;
	pid: string;
	seq: number;
	btnList: any[];
}

const state = reactive({
	dialog: {
		isShowDialog: false,
		title: '权限配置',
	},
	table: {
		loading: false,
	},
	row: {} as any,
	roleIds: [] as string[],
	allMenuData: [] as Menu[], // 存储所有菜单数据
	loading: false,
});
const openDialog = (row: any) => {
	state.dialog.isShowDialog = true;
	state.dialog.title = '权限配置';
	state.row = row;
	// 初始化角色已有的权限
	useRoleApi()
		.getRoleMenus(row.id)
		.then((res) => {
			// 过滤掉null、undefined或空字符串值
			state.roleIds = (res.data || []).filter((id: string) => id !== null && id !== undefined && id !== '');
		});
};

const tableRef = ref<VxeGridInstance<any>>();
const tableOption = reactive<VxeGridProps<any>>({
	border: 'inner',
	loading: state.table.loading,
	showHeader: false,
	cellConfig: {
		height: 48,
	},
	toolbarConfig: {
		size: 'small',
		slots: {
			buttons: 'toolbarButtons',
		},
	},
	treeConfig: {
		// 开启树状表格
		transform: true, // 开启树状数据转换
		rowField: 'id', // 行数据的唯一标识字段
		parentField: 'pid', // 父节点的唯一标识字段
		expandAll: true,
	},
	proxyConfig: {
		response: {
			list: 'data',
			message: 'msg',
		},
		ajax: {
			query: async () => {
				state.table.loading = true;
				const result = await useMenuApi().getMenuRoleList();
				// 保存所有菜单数据用于后续处理
				state.allMenuData = result.data || [];
				return result;
			},
			querySuccess: (res) => {
				setTimeout(() => {
					state.table.loading = false;
				}, 500);
			},
		},
	},
	checkboxConfig: {
		labelField: 'title', // 复选框的标签字段
		checkMethod: ({ row }) => {
			// 禁用自动选中，使用自定义逻辑
			return false;
		},
	},
	cellClassName({ row, column }) {
		if (column.field === 'checkbox') {
			if (row.type === 2) {
				//菜单
				return 'title-column-border';
			}
		}
		return null;
	},
	columns: [
		{ field: 'checkbox', title: '菜单名称', width: 220, treeNode: true, slots: { default: 'checkbox_title' } },
		{ title: '按钮集合', field: 'name', slots: { default: 'slots_btn' } },
	],
});
const tableEvents: VxeGridListeners = {
	toolbarButtonClick(params) {
		switch (params.code) {
			case 'expandAll':
				expandAll();
				break;
			case 'clearExpand':
				clearExpand();
				break;
		}
	},
};

// 递归查找所有子节点
const findChildren = (parentId: string, allData: Menu[]): Menu[] => {
	const children: Menu[] = [];
	for (const item of allData) {
		// 确保item和item.pid有效
		if (item && item.pid && item.pid === parentId) {
			children.push(item);
			// 递归查找子节点的子节点
			const grandChildren = findChildren(item.id, allData);
			children.push(...grandChildren);
		}
	}
	return children;
};

// 查找父节点
// 查找父节点
const findParent = (childId: string, allData: Menu[]): Menu | null => {
	for (const item of allData) {
		// 确保item和item.id有效
		if (item && item.id && item.id === childId) {
			// 找到当前节点，继续查找其父节点
			if (item.pid) {
				for (const parent of allData) {
					// 确保parent和parent.id有效
					if (parent && parent.id && parent.id === item.pid) {
						return parent;
					}
				}
			}
			return null;
		}
	}
	return null;
};

// 递归选中所有父节点
const checkAndSelectParents = (childId: string, allData: Menu[]) => {
	// 确保childId有效
	if (!childId) return;

	const parent = findParent(childId, allData);
	if (parent && parent.id) {
		// 如果父节点未被选中，则选中它
		if (!state.roleIds.includes(parent.id)) {
			state.roleIds.push(parent.id);
		}
		// 递归处理更高层级的父节点
		checkAndSelectParents(parent.id, allData);
	}
};

// 检查是否有子节点或按钮被选中
const hasSelectedChildren = (parentId: string, allData: Menu[]): boolean => {
	const children = findChildren(parentId, allData);

	// 检查直接子节点是否被选中
	for (const child of children) {
		// 确保child和child.id有效
		if (child && child.id && state.roleIds.includes(child.id)) {
			return true;
		}
		// 检查子节点的按钮是否被选中
		if (child && child.btnList && child.btnList.length > 0) {
			for (const btn of child.btnList) {
				// 确保btn和btn.id有效
				if (btn && btn.id && state.roleIds.includes(btn.id)) {
					return true;
				}
				// 如果btn无效但包含id属性，也检查id属性
				if (btn && !btn.id && btn['id'] && state.roleIds.includes(btn['id'])) {
					return true;
				}
			}
		}
	}
	return false;
};

// 处理父节点复选框变化
const handleParentCheckboxChange = (checked: boolean, row: Menu) => {
	// 确保row有效
	if (!row) return;

	if (checked) {
		// 选中当前节点
		if (row.id && !state.roleIds.includes(row.id)) {
			state.roleIds.push(row.id);
		}

		// 选中当前节点的所有按钮
		if (row.btnList && row.btnList.length > 0) {
			row.btnList.forEach((btn: any) => {
				if (btn && btn.id && !state.roleIds.includes(btn.id)) {
					state.roleIds.push(btn.id);
				}
			});
		}

		// 递归选中所有子节点及其按钮
		const selectAllChildren = (parentId: string) => {
			const children = findChildren(parentId, state.allMenuData);
			children.forEach((child: Menu) => {
				// 确保child有效
				if (!child) return;

				// 选中子节点
				if (child.id && !state.roleIds.includes(child.id)) {
					state.roleIds.push(child.id);
				}

				// 选中子节点的所有按钮
				if (child.btnList && child.btnList.length > 0) {
					child.btnList.forEach((btn: any) => {
						if (btn && btn.id && !state.roleIds.includes(btn.id)) {
							state.roleIds.push(btn.id);
						}
					});
				}

				// 递归处理子节点的子节点
				selectAllChildren(child.id);
			});
		};
		// 选中所有子节点及其按钮
		selectAllChildren(row.id);

		// 选中节点时，自动选中所有父节点
		checkAndSelectParents(row.id, state.allMenuData);
	} else {
		// 取消选中当前节点
		if (row.id) {
			state.roleIds = state.roleIds.filter((id) => id !== row.id);
		}

		// 取消选中当前节点的所有按钮
		if (row.btnList && row.btnList.length > 0) {
			row.btnList.forEach((btn: any) => {
				if (btn && btn.id) {
					state.roleIds = state.roleIds.filter((id) => id !== btn.id);
				}
			});
		}

		// 递归取消选中所有子节点及其按钮
		const deselectAllChildren = (parentId: string) => {
			const children = findChildren(parentId, state.allMenuData);
			children.forEach((child: Menu) => {
				// 确保child有效
				if (!child) return;

				// 取消选中子节点
				if (child.id) {
					state.roleIds = state.roleIds.filter((id) => id !== child.id);
				}

				// 取消选中子节点的所有按钮
				if (child.btnList && child.btnList.length > 0) {
					child.btnList.forEach((btn: any) => {
						if (btn && btn.id) {
							state.roleIds = state.roleIds.filter((id) => id !== btn.id);
						}
					});
				}

				// 递归处理子节点的子节点
				deselectAllChildren(child.id);
			});
		};

		// 取消选中所有子节点及其按钮
		deselectAllChildren(row.id);
	}
};

// 处理按钮组变化
const handleBtnGroupChange = (selectedBtns: string[], row: Menu) => {
	// 确保row有效
	if (!row) return;

	// 检查是否有按钮被选中
	const hasSelectedBtn = selectedBtns.some((btnId) => {
		return row.btnList && row.btnList.some((btn: any) => btn && btn.id && btn.id === btnId);
	});

	if (hasSelectedBtn) {
		// 如果有按钮被选中，确保左侧菜单也被选中
		if (row.id && !state.roleIds.includes(row.id)) {
			state.roleIds.push(row.id);
		}

		// 选中按钮时，自动选中所有父节点
		checkAndSelectParents(row.id, state.allMenuData);
	} else {
		// 如果所有按钮都被取消选中，检查是否还有其他子节点被选中
		const hasOtherSelectedChildren = hasSelectedChildren(row.id, state.allMenuData);

		if (!hasOtherSelectedChildren) {
			// 如果没有其他子节点被选中，可以考虑取消当前节点的选中
			// 这里保持原逻辑，不自动取消节点选中
		}
	}
};

//展开所有
const expandAll = () => {
	const $table = tableRef.value;
	if ($table) {
		$table.setAllTreeExpand(true);
	}
};
//折叠所有
const clearExpand = () => {
	const $table = tableRef.value;
	if ($table) {
		$table.clearTreeExpand();
	}
};

// 全选
const checkBoxAll = () => {
	state.roleIds = [];

	// 递归函数收集所有菜单ID和按钮ID
	const collectAllIds = (menus: Menu[]) => {
		menus.forEach((menu) => {
			// 添加菜单ID（确保ID有效）
			if (menu.id && !state.roleIds.includes(menu.id)) {
				state.roleIds.push(menu.id);
			}

			// 添加按钮ID（确保按钮和按钮ID有效）
			if (menu.btnList && menu.btnList.length > 0) {
				menu.btnList.forEach((btn) => {
					if (btn && btn.id && !state.roleIds.includes(btn.id)) {
						state.roleIds.push(btn.id);
					}
				});
			}
		});
	};

	// 收集所有菜单和按钮的ID
	collectAllIds(state.allMenuData);

	// 确保没有null或undefined值
	state.roleIds = state.roleIds.filter((id: string) => id !== null && id !== undefined && id !== '');
};

// 全不选
const checkBoxClearAll = () => {
	state.roleIds = [];
};

// 提交前确保roleIds数组中没有null值
const cleanRoleIds = () => {
	state.roleIds = state.roleIds.filter((id: string) => id !== null && id !== undefined && id !== '');
};

/**提交 */
const handleSubmit = () => {
	if (state.roleIds.length === 0) {
		ElMessage.error('请选择角色');
		return;
	}
	// 防止重复提交
	if (state.loading) return;
	state.loading = true;
	// 在提交前清理roleIds数组
	cleanRoleIds();

	// 构造正确的参数格式
	const params = {
		id: state.row.id,
		roleIds: state.roleIds,
	};
	useRoleApi()
		.updateRoleMenus(params)
		.then((res) => {
			if (res.code === 200) {
				ElMessage.success('授权成功');
				state.dialog.isShowDialog = false;
				state.roleIds = [];
			} else {
				console.error('授权失败:', res);
				ElMessage.error(res.msg || '授权失败');
			}
		})
		.catch((err) => {
			console.error('授权错误:', err);
			ElMessage.error(err.msg || '授权失败');
		})
		.finally(() => {
			state.loading = false;
		});
};
/**取消 */
const onCancel = () => {
	state.dialog.isShowDialog = false;
	state.roleIds = [];
};

defineExpose({
	openDialog,
});
</script>
<style scoped>
:deep(.el-checkbox-group) {
	white-space: pre;
	overflow-x: auto;
	/* 隐藏滚动条但保持滚动功能 */
	scrollbar-width: none;
	/* Firefox */
	-ms-overflow-style: none;
	/* IE and Edge */
}

/* 隐藏 WebKit 浏览器的滚动条 */
:deep(.el-checkbox-group)::-webkit-scrollbar {
	display: none;
}

/* 鼠标移入时显示滚动条（可选） */
:deep(.el-checkbox-group):hover {
	scrollbar-width: thin;
	/* Firefox */
	-ms-overflow-style: auto;
	/* IE and Edge */
}

:deep(.el-checkbox-group):hover::-webkit-scrollbar {
	display: block;
	width: 6px;
	height: 6px;
}

:deep(.el-checkbox-group):hover::-webkit-scrollbar-thumb {
	background-color: var(--el-border-color-darker);
	border-radius: 3px;
}

:deep(.title-column-border) {
	border-right: 1px solid var(--el-border-color);
}
</style>
