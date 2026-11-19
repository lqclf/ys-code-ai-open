<!--
 * YsExportExcel 数据导出组件
 * 功能：支持多格式导出（xlsx/xls/csv/json），可选导出字段，自定义参数
 * @author: liuQ
 * @date: 2025-07-23
 -->
<template>
	<div class="ys-export-excel">
		<!-- 导出按钮（可通过hideButton隐藏，用于工具栏调用场景） -->
		<el-button
			v-if="!hideButton"
			:type="buttonType"
			:size="size"
			:loading="loading"
			:disabled="disabled"
			@click="handleExportClick"
		>
			<i v-if="!loading" class="ri-download-line"></i>
			{{ loading ? '导出中...' : buttonText }}
		</el-button>

		<!-- 导出确认对话框 -->
		<YsDialog
			:title="dialogTitle"
			v-model="dialogVisible"
			:width="dialogWidth"
			append-to-body
			@close="handleCancel"
		>
			<div class="export-content">
				<!-- 查询条件摘要 -->
				<el-alert
					type="info"
					:closable="false"
					show-icon
					class="query-summary"
				>
					<template #title>
						<div class="summary-content">
							<p class="summary-title">当前查询条件：</p>
							<p v-if="hasQueryParams" class="summary-detail">{{ querySummary }}</p>
							<p v-else class="summary-empty">无查询条件（导出全部数据）</p>
						</div>
					</template>
				</el-alert>

				<!-- 导出配置表单 -->
			<el-form :model="exportForm" label-width="100px" class="export-form">
				<!-- 导出格式选择 -->
				<el-form-item label="导出格式">
					<el-radio-group v-model="exportForm.fileType" class="export-format-group">
						<el-radio value="xlsx" border>
							<i class="ri-file-excel-2-line"></i>
							Excel (.xlsx)
						</el-radio>
						<el-radio value="xls" border>
							<i class="ri-file-excel-2-line"></i>
							Excel 97-2003 (.xls)
						</el-radio>
						<el-radio value="csv" border>
							<i class="ri-file-text-line"></i>
							CSV (.csv)
						</el-radio>
						<el-radio value="json" border>
							<i class="ri-braces-line"></i>
							JSON (.json)
						</el-radio>
					</el-radio-group>
					<div class="format-tips">
						<i class="ri-information-line"></i>
						提示：不同格式适用于不同场景，Excel适合数据分析，CSV适合数据迁移，JSON适合开发调试
					</div>
				</el-form-item>

				<!-- 文件名称 -->
				<el-form-item label="文件名称">
					<el-input
						v-model="exportForm.fileName"
						placeholder="请输入文件名"
						maxlength="100"
						show-word-limit
					/>
				</el-form-item>

				<!-- 导出字段 - 上下排列 -->
				<el-form-item label="导出字段" class="fields-form-item">
						<div class="fields-container-vertical">
							<!-- 全选按钮 - 上方 -->
							<div class="fields-header-vertical">
								<el-checkbox
									v-model="isAllFieldsSelected"
									@change="handleSelectAllFields"
									class="select-all-checkbox"
								>
									{{ isAllFieldsSelected ? '取消全选' : '全选' }}
									<span class="fields-count">（已选择 {{ exportForm.selectedFields.length }} / {{ allFields.length }} 个字段）</span>
								</el-checkbox>
							</div>
							<!-- 复选框组 - 下方 -->
							<el-checkbox-group v-model="exportForm.selectedFields" class="fields-checkbox-group-vertical">
								<el-checkbox
									v-for="col in allFields"
									:key="col.field"
									:label="col.field"
									class="field-checkbox"
								>
									{{ col.title }}
								</el-checkbox>
							</el-checkbox-group>
						</div>
					</el-form-item>

					<!-- 自定义参数表单插槽 -->
					<slot name="customParams" :form="exportForm" :updateForm="updateCustomParams"></slot>
				</el-form>

				<!-- 导出提示 -->
				<el-alert
					type="warning"
					:closable="false"
					show-icon
					class="export-tips"
				>
					<template #title>
						<div class="tips-content">
							<p>导出说明：</p>
							<ul>
								<li>数据量较大时，导出可能需要一些时间，请耐心等待</li>
								<li>导出文件格式为 {{ fileTypeLabel }}</li>
								<li>导出完成后文件将自动下载到本地</li>
							</ul>
						</div>
					</template>
				</el-alert>
			</div>

			<template #footer>
				<el-button @click="handleCancel">取消</el-button>
				<el-button type="primary" @click="handleConfirmExport" :loading="loading">
					确认导出
				</el-button>
			</template>
		</YsDialog>
	</div>
</template>

<script setup lang="ts" name="YsExportExcel">
import { ref, reactive, computed, watch } from 'vue';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';

/**
 * 表格列定义接口
 */
interface ColumnConfig {
	field: string;
	title: string;
	width?: number | string;
	formatter?: (params: any) => string;
}

/**
 * 查询参数接口
 */
interface QueryParams {
	[key: string]: any;
}

/**
 * 导出表单接口
 */
interface ExportForm {
	fileName: string;
	fileType: 'xlsx' | 'xls' | 'csv' | 'json';
	selectedFields: string[];
	customParams: Record<string, any>;
}

/**
 * 组件Props接口
 */
interface Props {
	/** 导出类型标识（统一模式，如 "user"、"customer"）。设置此属性后组件自动调用统一导出接口 */
	exportType?: string;
	/** 额外参数（统一模式，附加到所有导出请求中） */
	extraParams?: Record<string, any>;
	/** 导出接口函数（自定义模式，exportType 为空时使用） */
	exportApi?: (params: any) => Promise<any>;
	/** 查询参数 */
	queryParams?: QueryParams;
	/** 表格列定义 */
	columns: ColumnConfig[];
	/** 默认文件名 */
	fileName?: string;
	/** 按钮文字 */
	buttonText?: string;
	/** 按钮类型 */
	buttonType?: 'primary' | 'success' | 'warning' | 'danger' | 'info';
	/** 按钮尺寸 */
	size?: 'large' | 'default' | 'small';
	/** 对话框标题 */
	dialogTitle?: string;
	/** 对话框宽度 */
	dialogWidth?: string;
	/** 是否禁用 */
	disabled?: boolean;
	/** 是否隐藏按钮（通过工具栏调用时使用） */
	hideButton?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
	exportType: '',
	extraParams: () => ({}),
	buttonText: '导出',
	buttonType: 'success',
	size: 'default',
	dialogTitle: '数据导出',
	dialogWidth: '800px',
	disabled: false,
	hideButton: false,
});

/**
 * 组件事件
 */
const emit = defineEmits<{
	(e: 'success'): void;
	(e: 'error', error: any): void;
}>();

// 状态管理
const dialogVisible = ref(false);
const loading = ref(false);
const isAllFieldsSelected = ref(true);

// 导出表单
const exportForm = reactive<ExportForm>({
	fileName: '',
	fileType: 'xlsx',
	selectedFields: [],
	customParams: {},
});

/**
 * 所有字段列表（包含序号列）
 */
const allFields = computed<ColumnConfig[]>(() => {
	const seqColumn: ColumnConfig = { field: 'selectedFields', title: '序号' };
	return [seqColumn, ...props.columns];
});

/**
 * 文件类型标签
 */
const fileTypeLabel = computed(() => {
	const labels: Record<string, string> = {
		xlsx: 'Excel (.xlsx)',
		xls: 'Excel 97-2003 (.xls)',
		csv: 'CSV (.csv)',
		json: 'JSON (.json)',
	};
	return labels[exportForm.fileType] || 'Excel';
});

/**
 * 更新自定义参数
 */
const updateCustomParams = (params: Record<string, any>) => {
	exportForm.customParams = { ...exportForm.customParams, ...params };
};

// 是否有查询参数
const hasQueryParams = computed(() => {
	if (!props.queryParams) return false;
	return Object.values(props.queryParams).some(
		value => value !== undefined && value !== null && value !== ''
	);
});

// 查询条件摘要
const querySummary = computed(() => {
	if (!props.queryParams) return '';
	const params: string[] = [];
	for (const [key, value] of Object.entries(props.queryParams)) {
		if (value !== undefined && value !== null && value !== '') {
			params.push(`${key}: ${value}`);
		}
	}
	return params.join('，') || '无查询条件';
});

/**
 * 处理全选/取消全选字段
 */
const handleSelectAllFields = (val: boolean) => {
	if (val) {
		exportForm.selectedFields = allFields.value.map(col => col.field);
	} else {
		exportForm.selectedFields = [];
	}
};

/**
 * 监听字段选择变化，更新全选状态
 */
watch(
	() => exportForm.selectedFields,
	(newVal) => {
		isAllFieldsSelected.value = newVal.length === allFields.value.length && newVal.length > 0;
	},
	{ immediate: true }
);

// 初始化默认文件名
const initDefaultFileName = () => {
	const timestamp = new Date().toISOString().slice(0, 19).replace(/:/g, '-');
	exportForm.fileName = props.fileName || `数据导出_${timestamp}`;
};

// 初始化选中字段
const initSelectedFields = () => {
	exportForm.selectedFields = allFields.value.map(col => col.field);
	isAllFieldsSelected.value = true;
};

// 初始化自定义参数
const initCustomParams = () => {
	exportForm.customParams = {};
};

/**
 * 点击导出按钮
 */
const handleExportClick = () => {
	initDefaultFileName();
	initSelectedFields();
	initCustomParams();
	dialogVisible.value = true;
};

/**
 * 确认导出
 */
const handleConfirmExport = async () => {
	// 校验文件名
	if (!exportForm.fileName.trim()) {
		ElMessage.warning('请输入文件名');
		return;
	}

	// 校验选中字段
	if (exportForm.selectedFields.length === 0) {
		ElMessage.warning('请至少选择一个导出字段');
		return;
	}

	loading.value = true;
	try {
		let response: any;

		if (props.exportType) {
			// === 统一导出模式 ===
			const queryParams: Record<string, any> = {
					exportType: props.exportType,
					fileType: exportForm.fileType,
					selectedFields: exportForm.selectedFields.join(','),
					...props.extraParams,
					...props.queryParams,
					...exportForm.customParams,
				};
			response = await request({
					url: '/api/excel/export/execute',
					method: 'get',
					params: queryParams,
					responseType: 'blob',
				});
		} else {
			// === 自定义导出模式（兼容旧用法） ===
			if (!props.exportApi) {
				throw new Error('未配置导出接口，请设置 exportType 或 exportApi');
			}
			const exportParams: any = {
					fileName: exportForm.fileName,
					fileType: exportForm.fileType,
					selectedFields: exportForm.selectedFields,
					...props.queryParams,
					...exportForm.customParams,
				};
			response = await props.exportApi(exportParams);
		}

		// 处理文件下载
		const fileExtension = exportForm.fileType;
		let blobData: Blob;
		let downloadFileName = exportForm.fileName + '.' + fileExtension;

		// 统一处理Blob响应（所有格式均使用responseType:'blob'）
		if (response instanceof Blob) {
			blobData = response;
		} else if (response.data instanceof Blob) {
			blobData = response.data;
			// 尝试从响应头获取文件名
			const contentDisposition = response.headers?.['content-disposition'];
			if (contentDisposition) {
				const filenameMatch = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
				if (filenameMatch && filenameMatch[1]) {
					downloadFileName = decodeURIComponent(filenameMatch[1].replace(/['"]/g, ''));
				}
			}
		} else if (fileExtension === 'json') {
			// 非Blob响应的JSON格式：将数据序列化为JSON Blob
			const jsonContent = JSON.stringify(response.data || response, null, 2);
			blobData = new Blob([jsonContent], { type: 'application/json' });
		} else {
			throw new Error('导出数据格式错误');
		}

		// 创建下载链接
		const url = window.URL.createObjectURL(blobData);
		const link = document.createElement('a');
		link.href = url;
		link.setAttribute('download', downloadFileName);
		document.body.appendChild(link);
		link.click();
		document.body.removeChild(link);
		window.URL.revokeObjectURL(url);

		emit('success');
		dialogVisible.value = false;
	} catch (error: any) {
		ElMessage.error(error.message || '导出失败');
		emit('error', error);
	} finally {
		loading.value = false;
	}
};

/**
 * 取消导出
 */
const handleCancel = () => {
	dialogVisible.value = false;
};

// 监听对话框显示状态
watch(dialogVisible, (visible) => {
	if (visible) {
		initDefaultFileName();
		initSelectedFields();
		initCustomParams();
	}
});

// 暴露方法
defineExpose({
	open: handleExportClick,
	close: handleCancel,
});
</script>

<style scoped lang="scss">
.ys-export-excel {
	display: inline-flex;
}

.export-content {
	padding: 10px 0;
}

.query-summary {
	margin-bottom: 20px;

	.summary-content {
		.summary-title {
			font-weight: bold;
			margin-bottom: 8px;
		}

		.summary-detail {
			color: #606266;
			font-size: 13px;
			word-break: break-all;
		}

		.summary-empty {
			color: #909399;
			font-size: 13px;
		}
	}
}

.export-form {
	.export-format-group {
		display: flex;
		flex-wrap: wrap;
		gap: 12px;

		.el-radio {
			margin-right: 0;
			padding: 8px 16px;

			i {
				margin-right: 4px;
				font-size: 16px;
				vertical-align: middle;
			}
		}
	}

	.format-tips {
		font-size: 12px;
		color: #909399;
		margin-top: 8px;
		line-height: 1.6;

		i {
			margin-right: 4px;
		}
	}

	.file-name-input {
		.file-type-select {
			width: 90px;
		}
	}

	.fields-form-item {
		margin-bottom: 20px;

		.fields-container-vertical {
			display: flex;
			flex-direction: column;
			width: 100%;

			.fields-header-vertical {
				padding: 2px 3px;
				.select-all-checkbox {
					font-weight: 500;

					.fields-count {
						font-size: 12px;
						color: #909399;
						font-weight: normal;
						margin-left: 8px;
					}
				}
			}

			.fields-checkbox-group-vertical {
				display: grid;
				grid-template-columns: repeat(4, 1fr);
				gap: 10px;
				padding: 15px;
				background-color: #fafbfc;
				border-radius: 4px;
				border: 1px solid #e4e7ed;

				.field-checkbox {
					margin-right: 0;
					margin-bottom: 5px;

					:deep(.el-checkbox__label) {
						font-size: 13px;
					}
				}
			}
		}
	}
}

.export-tips {
	margin-top: 20px;

	.tips-content {
		p {
			font-weight: bold;
			margin-bottom: 5px;
		}

		ul {
			margin: 0;
			padding-left: 20px;

			li {
				font-size: 13px;
				line-height: 1.8;
			}
		}
	}
}
</style>
