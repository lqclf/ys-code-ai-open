<template>
	<div class="ys-table-container">
		<!-- 页面头部区域（通过插槽自定义） -->
		<div v-if="$slots['page-header']" class="ys-table-header">
			<slot name="page-header"></slot>
		</div>

		<!-- 工具栏区域（通过插槽自定义） -->
		<div v-if="$slots['toolbar']" class="ys-table-toolbar">
			<slot name="toolbar" :refresh="refresh" :reset="reset" :search="search"></slot>
		</div>

		<!-- 表格主体区域 -->
		<div class="ys-table-content">
			<vxe-grid ref="tableRef" v-bind="mergedOptions" v-on="mergedEvents">
				<template #top>
					<!-- 选中数据提示条 -->
					<el-alert v-if="shouldShowSelectionAlert" type="primary" show-icon closable @close="clearSelection">
						<template #title>
							<div class="selection-alert-content">
								<span>{{ selectedCount > 0 ? `已选中 ${selectedCount} 项` : '未选中任何数据！' }}</span>
								<el-button v-if="selectedCount > 0" type="primary" link @click="clearSelection"
									style="margin-left: 12px"> 清空 </el-button>
							</div>
						</template>
					</el-alert>
				</template>
				<!-- 传递所有插槽 -->
				<template v-for="(_, slot) in $slots" #[slot]="scope">
					<slot :name="slot" v-bind="scope"></slot>
				</template>
			</vxe-grid>
		</div>

		<!-- 分页区域（如果启用分页） -->
		<div v-if="showPagination" class="ys-table-footer">
			<el-pagination :current-page="pageVo.currentPage" background :page-size="pageVo.pageSize"
				:page-sizes="pageSizes" :total="pageVo.total" layout="prev, pager, next, sizes, jumper, total"
				@size-change="handleSizeChange" @current-change="handleCurrentChange" />
		</div>
	</div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted, nextTick, watch, onUnmounted } from 'vue';
import { VxeGridProps, VxeGridInstance, VxeGridListeners } from 'vxe-table';
import { useResetForm } from '@/utils/resetForm';

// 定义组件属性
interface Props {
	// 表格配置
	options?: VxeGridProps;
	// 表格事件
	events?: VxeGridListeners;
	// 数据请求函数
	requestFn?: (params: any) => Promise<any>;
	// 查询参数
	queryParams?: Record<string, any>;
	// 直接传入的数据
	data?: any[];
	// 是否显示分页
	showPagination?: boolean;
	// 分页大小选项
	pageSizes?: number[];
	// 初始分页参数
	pagination?: {
		currentPage: number;
		pageSize: number;
		total: number;
	};
	// 是否自动加载数据
	autoLoad?: boolean;
	// 是否启用自动高度计算
	autoHeight?: boolean;
	// 是否显示选中数据提示条
	showSelectionAlert?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
	options: () => ({}),
	events: () => ({}),
	queryParams: () => ({}),
	showPagination: true,
	pageSizes: () => [10, 20, 50, 100],
	pagination: () => ({
		currentPage: 1,
		pageSize: 10,
		total: 0,
	}),
	autoLoad: true,
	autoHeight: true,
	showSelectionAlert: false,
});

// 定义事件  通过@size-change  来监听数据加载成功
const emit = defineEmits([
	'size-change', // 分页大小改变
	'current-change', // 分页当前页改变
	'request-success', // 数据加载成功
	'request-error', // 数据加载失败
	'refresh', // 刷新
	'reset', // 重置
	'search', // 搜索
]);

// 表格实例
const tableRef = ref<VxeGridInstance>();

// 选中的数据（当前页）
const selectedData = ref<any[]>([]);

// 所有选中的数据（跨分页）
const allSelectedData = ref<any[]>([]);

// 用于存储每页选中数据的 Map，key 为页码，value 为该页选中的数据
const selectedDataMap = ref<Map<number, any[]>>(new Map());

// 选中数据条数计算属性
const selectedCount = computed(() => allSelectedData.value.length);

// 是否显示选中提示条（仅在配置显示且表格有选择功能时显示）
const shouldShowSelectionAlert = computed(() => {
	if (!props.showSelectionAlert) return false;

	// 检查表格配置是否包含选择功能
	const hasCheckbox = computed(() => {
		const columns = props.options?.columns || [];
		return columns.some((column: any) => column.type === 'checkbox' || column.type === 'radio');
	});
	if (hasCheckbox.value) {
		const topWrapper = tableRef.value?.getEl().querySelector('.vxe-grid--top-wrapper');
		if (topWrapper && topWrapper instanceof HTMLElement) {
			topWrapper.style.padding = '0 0 8px 0';
		}
	}

	return !!hasCheckbox.value;
});

// 分页参数
const pageVo = reactive({
	currentPage: props.pagination.currentPage,
	pageSize: props.pagination.pageSize,
	total: props.pagination.total,
});

// 查询参数
const searchParams = reactive<Record<string, any>>({
	...props.queryParams,
});

// 合并后的配置
const mergedOptions = computed<VxeGridProps>(() => {
	const baseOptions: VxeGridProps = {
		border: true,
		stripe: true,
		columnConfig: {
			resizable: true,
		},
		resizeConfig: {
			refreshDelay: 0,
		},
		cellConfig: {
			height: 50,
		},
		rowConfig: {
			keyField: 'id',
			isHover: true,
		},
		pagerConfig: props.showPagination
			? {
				currentPage: pageVo.currentPage,
				pageSize: pageVo.pageSize,
				total: pageVo.total,
			}
			: undefined,
		layouts: props.showPagination ? ['Form', 'Toolbar', 'Top', 'Table', 'Bottom'] : ['Form', 'Toolbar', 'Top', 'Table'],
		formConfig: {
			data: searchParams,
		},
	};

	// 如果有数据请求函数，配置代理
	if (props.requestFn) {
		baseOptions.proxyConfig = {
			ajax: {
				query: async ({ page, sorts, filters, form }) => {
					try {
						const params = {
							pageSize: page.pageSize,
							pageNo: page.currentPage,
							...searchParams,
							...form,
							// 处理排序
							...(sorts &&
								sorts.length > 0 && {
								orderBy: sorts.map((item) => `${item.field}|${item.order}`).join(','),
							}),
							// 处理筛选
							...(filters &&
								Object.keys(filters).length > 0 && {
								filters,
							}),
						};
						const res = await props.requestFn!(params);
						// 更新分页信息

						//DOTO  这里需要根据后端返回的字段来配置
						pageVo.currentPage = res.data?.current || page.currentPage;
						pageVo.pageSize = res.data?.size || page.pageSize;
						pageVo.total = res.data?.total || 0;

						emit('request-success', res);

						return {
							result: res.data?.records || res.data?.list || res.data || [],
							page: {
								currentPage: res.data?.current || page.currentPage,
								pageSize: res.data?.size || page.pageSize,
								total: res.data?.total || 0,
							},
							message: res.msg || '',
						};
					} catch (error) {
						emit('request-error', error);
						return {
							result: [],
							page: {
								currentPage: page.currentPage,
								pageSize: page.pageSize,
								total: 0,
							},
							message: '请求失败',
						};
					} finally {
						if (props.autoHeight) {
							await nextTick(); // 等待 VXE Table 根据 new data 完成 DOM 更新
							calculateMaxHeight(); // 然后再去计算最新的高度
						}
					}
				},
				/* querySuccess: () => {
					if (props.autoHeight) {
						calculateMaxHeight();
					}
				}, */
			},
		};
	} else if (props.data) {
		// 如果直接传入了数据
		baseOptions.data = props.data;
	}

	return {
		...baseOptions,
		...props.options,
	};
});

// 合并后的事件
const mergedEvents = computed<VxeGridListeners>(() => {
	return {
		...props.events,
		// 监听表格选中事件
		checkboxChange: (e) => {
			// 获取当前页选中的数据
			const currentPageSelected = e.$table.getCheckboxRecords(true);
			selectedData.value = currentPageSelected;

			// 将当前页选中的数据保存到 Map 中
			selectedDataMap.value.set(pageVo.currentPage, currentPageSelected);

			// 合并所有页面的选中数据
			updateAllSelectedData();

			// 调用用户传入的事件处理函数
			if (props.events && props.events['checkboxChange']) {
				props.events['checkboxChange'](e);
			}
		},
		checkboxAll: (e) => {
			// 获取当前页选中的数据
			const currentPageSelected = e.$table.getCheckboxRecords(true);
			selectedData.value = currentPageSelected;

			// 将当前页选中的数据保存到 Map 中
			selectedDataMap.value.set(pageVo.currentPage, currentPageSelected);

			// 合并所有页面的选中数据
			updateAllSelectedData();

			// 调用用户传入的事件处理函数
			if (props.events && props.events['checkboxAll']) {
				props.events['checkboxAll'](e);
			}
		},
		radioChange: (e) => {
			const radioRecord = e.$table.getRadioRecord();
			selectedData.value = radioRecord ? [radioRecord] : [];
			allSelectedData.value = [...selectedData.value];

			// 调用用户传入的事件处理函数
			if (props.events && props.events['radioChange']) {
				props.events['radioChange'](e);
			}
		},
	};
});

// 更新所有选中数据的函数
const updateAllSelectedData = () => {
	const allData: any[] = [];
	selectedDataMap.value.forEach((data) => {
		allData.push(...data);
	});
	allSelectedData.value = allData;
};

// 自动计算表格高度
const calculateMaxHeight = async () => {
	await nextTick();
	const container = document.querySelector('.ys-table-container')?.clientHeight || 0;
	const header = document.querySelector('.ys-table-header')?.clientHeight || 0;
	const toolbar = document.querySelector('.ys-table-toolbar')?.clientHeight || 0;
	const footer = document.querySelector('.ys-table-footer')?.clientHeight || 0;

	const newMaxHeight = container - header - toolbar - footer - 60;

	if (tableRef.value) {
		const currentMaxHeight = props.options.maxHeight;
		if (currentMaxHeight !== newMaxHeight) {
			props.options.maxHeight = newMaxHeight;
			await tableRef.value.recalculate(true);
		}
	}
};

// 监听窗口大小变化
if (typeof window !== 'undefined' && props.autoHeight) {
	window.addEventListener('resize', () => {
		calculateMaxHeight();
	});
}

// 处理页码变化
const handleCurrentChange = (currentPage: number) => {
	pageVo.currentPage = currentPage;
	emit('current-change', currentPage);

	if (tableRef.value) {
		tableRef.value.setCurrentPage(currentPage);
		tableRef.value.setPageSize(pageVo.pageSize);
		tableRef.value.commitProxy('query').then(async () => {
			// 等待数据加载和 DOM 更新后再重绘
			await nextTick();
			tableRef.value?.recalculate(true);
		});
	}
};

// 处理每页条数变化
const handleSizeChange = (pageSize: number) => {
	pageVo.pageSize = pageSize;
	pageVo.currentPage = 1;
	emit('size-change', pageSize);

	if (tableRef.value) {
		tableRef.value?.setCurrentPage(1);
		tableRef.value?.setPageSize(pageSize);
		tableRef.value.commitProxy('query').then(async () => {
			await nextTick();
			tableRef.value?.recalculate(true);
		});
	}
};

// 刷新表格数据
const refresh = () => {
	if (tableRef.value) {
		tableRef.value.commitProxy('query').then(async () => {
			await nextTick();
			tableRef.value?.recalculate(true);
		});

		emit('refresh');
	}
};

// 重置查询条件
const { resetForm } = useResetForm(searchParams);
const reset = () => {
	// 重置分页
	pageVo.currentPage = props.pagination.currentPage;
	pageVo.pageSize = props.pagination.pageSize;
	resetForm();
	// 重置查询参数
	Object.keys(props.queryParams).forEach((key) => {
		if (Array.isArray(props.queryParams[key])) {
			searchParams[key] = [];
		} else if (typeof props.queryParams[key] === 'boolean') {
			searchParams[key] = false;
		} else if (typeof props.queryParams[key] === 'number') {
			searchParams[key] = 0;
		} else {
			searchParams[key] = '';
		}
	});
	// 重置表单
	if (tableRef.value) {
		tableRef.value.resetForm();
		tableRef.value.commitProxy('query').then(async () => {
			await nextTick();
			tableRef.value?.recalculate(true);
		});
		emit('reset');
	}
};

// 查询数据
const search = (params?: Record<string, any>) => {
	if (params) {
		Object.assign(searchParams, params);
	}
	pageVo.currentPage = 1;

	if (tableRef.value) {
		tableRef.value.commitProxy('query').then(async () => {
			await nextTick();
			tableRef.value?.recalculate(true);
		});
		emit('search', searchParams);
	}
};

// 清空选中项
const clearSelection = () => {
	if (tableRef.value) {
		tableRef.value.clearCheckboxRow();
		tableRef.value.clearCheckboxReserve();
		tableRef.value.clearRadioRow();
		tableRef.value.clearRadioReserve();
		selectedData.value = [];
		allSelectedData.value = [];
		selectedDataMap.value.clear();
		// 触发 checkboxChange 事件
		if (props.events && props.events['checkboxChange']) {
			props.events['checkboxChange']({
				$table: tableRef.value,
				records: [],
			} as any);
		}
	}
};

// 更新查询参数
const updateQueryParams = (params: Record<string, any>) => {
	Object.assign(searchParams, params);
};

// 获取当前查询参数
const getQueryParams = () => {
	return {
		...searchParams,
		pageNo: pageVo.currentPage,
		pageSize: pageVo.pageSize,
	};
};

// 暴露表格实例和方法
defineExpose({
	tableRef,
	refresh,
	reset,
	search,
	updateQueryParams,
	getQueryParams,
	resetForm: () => tableRef.value?.resetForm(), // 暴露 VXE Table 原生的重置方法
	setCurrentPage: (page: number) => {
		pageVo.currentPage = page;
		if (tableRef.value) {
			tableRef.value.commitProxy('query').then(() => {
				nextTick(() => {
					tableRef.value?.recalculate(true);
				});
			});
		}
	},
	setPageSize: (size: number) => {
		pageVo.pageSize = size;
		if (tableRef.value) {
			tableRef.value.commitProxy('query').then(() => {
				nextTick(() => {
					tableRef.value?.recalculate(true);
				});
			});
		}
	},
	// 暴露清空选中项方法
	clearSelection,
	// 暴露获取所有选中数据的方法
	getAllSelectedData: () => allSelectedData.value,
});

let resizeObserver: ResizeObserver | null = null;

onMounted(() => {
	if (props.autoHeight) {
		calculateMaxHeight();

		// 使用 ResizeObserver 替代 window.addEventListener('resize')
		// ResizeObserver 更精确，只监听容器本身尺寸变化
		resizeObserver = new ResizeObserver(() => {
			calculateMaxHeight();
		});
		const container = document.querySelector('.ys-table-container');
		if (container) {
			resizeObserver.observe(container);
		}
	}
});

onUnmounted(() => {
	// 组件卸载时，务必取消监听
	if (resizeObserver) {
		resizeObserver.disconnect();
		resizeObserver = null;
	}
});

// 监听数据变化
watch(
	() => props.data,
	(newData) => {
		if (newData && !props.requestFn) {
			mergedOptions.value.data = newData;
		}
	},
	{ deep: true }
);

// 监听查询参数变化
watch(
	() => props.queryParams,
	(newParams) => {
		Object.assign(searchParams, newParams);
	},
	{ deep: true }
);
</script>

<style scoped lang="scss">
.ys-table-container {
	padding: 10px;
	height: 100%;
	display: flex;
	flex-direction: column;

	.ys-table-header {
		margin-bottom: 10px;
	}

	.ys-table-toolbar {
		margin-bottom: 15px;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.ys-table-content {
		flex: 1;
		overflow: hidden;

		.selection-alert-content {
			display: flex;
			align-items: center;
			justify-content: space-between;
		}

		:deep(.vxe-grid .el-alert) {
			padding: 6px 16px;

			:deep(.el-alert .el-alert__close-btn) {
				top: 8px;
				right: 10px;
			}
		}
	}
	.ys-table-footer {
		margin-top: 15px;
		display: flex;
		justify-content: flex-end;
	}
}
</style>
