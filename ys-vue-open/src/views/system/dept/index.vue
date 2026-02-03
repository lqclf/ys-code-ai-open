<template>
	<div class="page-container dept-management">
		<div class="dept-layout">
			<!-- 左侧部门树区域 -->
			<div class="left-panel">
				<el-card class="tree-card">
					<!-- 操作按钮区域 -->
					<div class="tree-toolbar">
						<el-button type="success" @click="handleAddRootDept" v-auth="'system:dept:add'">
							<i class="ri-add-line"></i>
							新增
						</el-button>
						<el-button type="primary" @click="handleAddDept" v-auth="'system:dept:add'" :disabled="!selectedDept">
							<i class="ri-add-line"></i>
							添加下级
						</el-button>
						<el-button type="danger" @click="handleBatchDelete" v-auth="'system:dept:delete'" :disabled="checkedKeys.length === 0">
							<i class="ri-delete-bin-6-line"></i>
							批量删除
						</el-button>
					</div>
					<!-- 搜索区域 -->
					<div class="search-area">
						<div class="search-wrapper">
							<el-input v-model="searchKeyword" placeholder="输入部门名称进行搜索" clearable @input="filterDeptTree">
								<template #append>
									<el-button type="primary" />
									<i class="ri-search-line"></i>
								</template>
							</el-input>
							<el-link
								type="primary"
								underline="never"
								class="pr6 pl6 mr"
								:title="isTreeExpanded ? '收缩' : '展开'"
								@click="toggleExpandCollapse"
								:disabled="expandLoading"
							>
								<el-icon v-if="expandLoading" class="is-loading">
									<Loading />
								</el-icon>
								<i v-else :class="isTreeExpanded ? 'ri-collapse-vertical-line' : 'ri-expand-height-line'" style="font-size: 20px"> </i>
							</el-link>
						</div>
					</div>

					<!-- 部门树 -->
					<div class="tree-container">
						<el-tree
							ref="deptTreeRef"
							:data="deptTreeData"
							node-key="id"
							:props="treeProps"
							show-checkbox
							:default-expanded-keys="expandedKeys"
							:highlight-current="true"
							:default-expand-all="false"
							:expand-on-click-node="false"
							:filter-node-method="filterNode"
							@node-click="handleNodeClick"
							@check="handleCheckChange"
							class="dept-tree"
						>
							<template #default="{ node, data }">
								<div class="tree-node">
									<el-icon class="tree-icon">
										<OfficeBuilding v-if="data.levelType === 1" />
										<School v-else-if="data.levelType === 2" />
										<Shop v-else />
									</el-icon>
									<span class="tree-label">{{ node.label }}</span>
									<el-tag size="small" class="level-tag" :type="getLevelTagType(data.levelType)">
										{{ getLevelText(data.levelType) }}
									</el-tag>
								</div>
							</template>
						</el-tree>
					</div>
				</el-card>
			</div>

			<!-- 右侧内容区域 -->
			<div class="right-panel">
				<el-card class="content-card">
					<div v-if="!selectedDept" class="empty-state">
						<el-empty description="请选择左侧部门查看详情" />
					</div>

					<div v-else class="dept-content">
						<!-- 部门信息头部 -->
						<div class="dept-header">
							<div class="dept-title">
								<i class="ri-building-line title-icon"></i>
								<h3>{{ selectedDept.name }}</h3>
								<el-tag :type="getLevelTagType(selectedDept.levelType)">{{ getLevelText(selectedDept.levelType) }}</el-tag>
							</div>
						</div>

						<!-- 选项卡内容 -->
						<el-tabs v-model="activeTab" class="dept-tabs">
							<!-- 部门详情选项卡 -->
							<el-tab-pane label="部门详情" name="detail">
								<DeptDetail :dept-data="selectedDept" @update="handleDeptUpdate" @refresh="loadDeptTree" />
							</el-tab-pane>

							<!-- 部门用户选项卡 -->
							<el-tab-pane label="部门用户" name="users">
								<DeptUsers :depart-id="selectedDept.id" :depart-name="selectedDept.name" />
							</el-tab-pane>
						</el-tabs>
					</div>
				</el-card>
			</div>
		</div>

		<!-- 引入组件 -->
		<DeptDialog ref="deptDialogRef" @refresh="loadDeptTree" />
	</div>
</template>
<script setup lang="ts" name="systemDept">
import { defineAsyncComponent, ref, onMounted, reactive, nextTick } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import { Shop, OfficeBuilding, School, Loading } from '@element-plus/icons-vue';
import { useDeptApi } from '@/api/system/dept';
import { commonApi } from '@/api/common';

// 引入组件
const DeptDialog = defineAsyncComponent(() => import('@/views/system/dept/dialog.vue'));
const DeptDetail = defineAsyncComponent(() => import('@/views/system/dept/components/DeptDetail.vue'));
const DeptUsers = defineAsyncComponent(() => import('@/views/system/dept/components/DeptUsers.vue'));

// 定义变量内容
const deptDialogRef = ref();
const deptTreeRef = ref();

// 树是否展开状态
const isTreeExpanded = ref(false);
// 展开/收缩加载状态
const expandLoading = ref(false);

// 状态管理
const state = reactive({
	loading: false,
	selectTypeList: [] as any[],
});

// 搜索关键词
const searchKeyword = ref('');

// 部门树数据
const deptTreeData = ref<any[]>([]);

// 树配置
const treeProps = {
	children: 'children',
	label: 'name',
	value: 'id',
};

// 选中的部门
const selectedDept = ref<any>(null);
// 选中的节点keys
const checkedKeys = ref<string[]>([]);
// 展开的节点keys
const expandedKeys = ref<string[]>([]);

// 当前活跃的tab
const activeTab = ref('detail');

// 加载部门树数据
const loadDeptTree = async () => {
	state.loading = true;
	try {
		const response = await useDeptApi().getDeptList({});
		if (response && response.data) {
			// 转换为树形结构
			deptTreeData.value = buildTreeData(response.data);
			nextTick(() => {
				expandedKeys.value = [deptTreeData.value[0].id]; //设置默认展开第一个节点
				deptTreeRef.value.setCurrentKey(deptTreeData.value[0].id, true); //  当节点被点击时，更新currentNodeKey的值
				selectedDept.value = deptTreeData.value[0]; //  选中第一个节点
			});
		}
	} catch (error) {
		console.error('加载部门树失败:', error);
		ElMessage.error('加载部门数据失败');
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

// 过滤树节点
const filterNode = (value: string, data: any) => {
	if (!value) return true;
	return data.name.includes(value);
};

// 过滤部门树
const filterDeptTree = () => {
	deptTreeRef.value?.filter(searchKeyword.value);
};

// 处理节点点击
const handleNodeClick = (data: any) => {
	selectedDept.value = data;
};

// 处理复选框变化
const handleCheckChange = () => {
	checkedKeys.value = deptTreeRef.value?.getCheckedKeys() || [];
};

// 获取层级标签类型
const getLevelTagType = (levelType: number) => {
	const typeMap: { [key: number]: string } = {
		1: 'primary',
		2: 'success',
		3: 'warning',
		4: 'info',
	};
	return typeMap[levelType] || 'info';
};

// 获取层级文本
const getLevelText = (levelType: number) => {
	const textMap: { [key: number]: string } = {
		1: '总部',
		2: '分公司',
		3: '部门',
		4: '科室',
	};
	return textMap[levelType] || '未知';
};

// 新增根部门
const handleAddRootDept = () => {
	deptDialogRef.value?.openDialog('add', { pid: '' });
};

// 添加下一级部门
const handleAddDept = () => {
	if (!selectedDept.value) {
		ElMessage.warning('请先选择父级部门');
		return;
	}
	deptDialogRef.value?.openDialog('add', { pid: selectedDept.value.id });
};

// 批量删除
const handleBatchDelete = () => {
	if (checkedKeys.value.length === 0) {
		ElMessage.warning('请选择要删除的部门');
		return;
	}

	ElMessageBox.confirm(`确定要删除选中的 ${checkedKeys.value.length} 个部门吗？此操作不可撤销！`, '批量删除确认', {
		confirmButtonText: '确定删除',
		cancelButtonText: '取消',
		type: 'warning',
		dangerouslyUseHTMLString: true,
	})
		.then(async () => {
			try {
				// 这里需要调用批量删除的API
				for (const id of checkedKeys.value) {
					await useDeptApi().deleteDept(id);
				}
				ElMessage.success('批量删除成功');
				checkedKeys.value = [];
				selectedDept.value = null;
				loadDeptTree();
			} catch (error) {
				console.error('批量删除失败:', error);
				ElMessage.error('批量删除失败');
			}
		})
		.catch(() => {
			// 用户取消删除
		});
};

// 展开全部
const expandAll = () => {
	// 添加一个小延迟，确保用户能看到加载状态
	setTimeout(() => {
		const nodes = deptTreeRef.value?.store.nodesMap;
		if (nodes) {
			Object.values(nodes).forEach((node: any) => {
				node.expanded = true;
			});
		}
		isTreeExpanded.value = true;
	}, 10);
};

// 收起全部
const collapseAll = () => {
	// 添加一个小延迟，确保用户能看到加载状态
	setTimeout(() => {
		const nodes = deptTreeRef.value?.store.nodesMap;
		if (nodes) {
			Object.values(nodes).forEach((node: any) => {
				node.expanded = false;
			});
		}
		isTreeExpanded.value = false;
	}, 10);
};

// 处理部门更新
const handleDeptUpdate = (updatedDept: any) => {
	selectedDept.value = updatedDept;
	loadDeptTree();
};

// 切换展开/收缩
const toggleExpandCollapse = () => {
	expandLoading.value = true;
	if (isTreeExpanded.value) {
		collapseAll();
		isTreeExpanded.value = false;
	} else {
		expandAll();
		isTreeExpanded.value = true;
	}
	// 确保加载状态至少显示500ms，让用户能够看到
	setTimeout(() => {
		expandLoading.value = false;
	}, 500);
};

// 组件挂载时加载数据
onMounted(() => {
	loadDeptTree();

	// 获取枚举值
	commonApi()
		.getEnumValue('sys_depttype')
		.then((res: any) => {
			state.selectTypeList = res.data;
		})
		.catch(() => {
			// 忽略错误
		});
});
</script>

<style scoped lang="scss">
.dept-management {
	padding: 20px;
	height: calc(100vh - 80px);
}

.dept-layout {
	display: flex;
	gap: 20px;
	height: 100%;
}

.left-panel {
	width: 40%;
	flex-shrink: 0;
}

.right-panel {
	flex: 1;
	min-width: 0;
}

.tree-card,
.content-card {
	height: 100%;
	display: flex;
	flex-direction: column;

	:deep(.el-card__body) {
		flex: 1;
		display: flex;
		flex-direction: column;
		overflow: hidden;
		padding: 20px;
	}
}
:deep(.el-button) {
	height: 30px;
}
.search-area {
	margin-bottom: 20px;

	.search-wrapper {
		position: relative;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.search-input {
		:deep(.el-input__wrapper) {
			border-radius: 20px;
			border: 2px solid var(--el-border-color-light);
			background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
			transition: all 0.3s ease;
			box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

			&:hover {
				border-color: var(--el-color-primary-light-3);
				box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
				transform: translateY(-1px);
			}

			&.is-focus {
				border-color: var(--el-color-primary);
				box-shadow:
					0 0 0 3px var(--el-color-primary-light-8),
					0 4px 12px rgba(64, 158, 255, 0.25);
				transform: translateY(-2px);
			}
		}

		:deep(.el-input__inner) {
			padding-left: 45px;
			padding-right: 40px;
			height: 42px;
			line-height: 42px;
			font-size: 14px;
			color: var(--el-text-color-primary);
			border: none;
			background: transparent;

			&::placeholder {
				color: var(--el-text-color-placeholder);
				font-size: 13px;
			}
		}

		:deep(.el-input__prefix) {
			left: 15px;
			top: 50%;
			transform: translateY(-50%);
		}

		:deep(.el-input__suffix) {
			right: 15px;
			top: 50%;
			transform: translateY(-50%);
		}
	}

	.search-icon {
		color: var(--el-color-primary);
		font-size: 16px;
		transition: color 0.3s ease;
	}
}

.tree-toolbar {
	margin-bottom: 15px;
	padding-bottom: 15px;
	border-bottom: 1px solid var(--el-border-color-light);

	.el-button {
		flex: 1;
	}
}

.tree-container {
	flex: 1;
	overflow: auto;
}

.dept-tree {
	:deep(.el-tree-node__content) {
		height: 36px;
		align-items: center;

		&:hover {
			background-color: var(--el-fill-color-light);
		}
	}

	:deep(.el-tree-node__expand-icon) {
		margin-right: 8px;
	}
}

.tree-node {
	display: flex;
	align-items: center;
	gap: 8px;
	flex: 1;
	padding-right: 10px;
}

.tree-icon {
	font-size: 16px;
	color: var(--el-color-primary);
}

.tree-label {
	flex: 1;
	font-size: 14px;
}

.level-tag {
	margin-left: auto;
}

.empty-state {
	display: flex;
	align-items: center;
	justify-content: center;
	height: 100%;
}

.dept-content {
	height: 100%;
	display: flex;
	flex-direction: column;
}

.dept-header {
	padding-bottom: 15px;
	border-bottom: 1px solid var(--el-border-color-light);
}

.dept-title {
	display: flex;
	align-items: center;
	gap: 12px;

	h3 {
		margin: 0;
		font-size: 18px;
		font-weight: 600;
		color: var(--el-text-color-primary);
	}
}

.title-icon {
	font-size: 24px;
	color: var(--el-color-primary);
}

.dept-tabs {
	flex: 1;
	display: flex;
	flex-direction: column;
	overflow: hidden;

	:deep(.el-tabs__content) {
		flex: 1;
		overflow: hidden;
	}

	:deep(.el-tab-pane) {
		height: 100%;
	}
	:deep(.el-tabs__header) {
		padding-top: 10px;
	}
}

// 响应式设计
@media (max-width: 1200px) {
	.left-panel {
		width: 300px;
	}
}

@media (max-width: 768px) {
	.dept-layout {
		flex-direction: column;
		height: auto;
	}

	.left-panel {
		width: 100%;
		height: 400px;
	}

	.right-panel {
		height: 600px;
	}
}
</style>
