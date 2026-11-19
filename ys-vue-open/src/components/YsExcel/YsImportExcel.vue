<!--
 * YsImportExcel 数据导入组件
 * 简化版：上传文件 → 弹确认框 → 用户确认后立即执行导入
 * 不再支持预览阶段，校验由后端在导入过程中完成
 * @author: liuQ
 * @date: 2025-07-23
 -->
<template>
	<div class="ys-import-excel">
		<!-- 导入按钮（可通过hideButton隐藏，用于工具栏调用场景） -->
		<el-button
			v-if="!hideButton"
			:type="buttonType"
			:size="size"
			:loading="loading"
			:disabled="disabled"
			@click="handleImportClick"
		>
			<i v-if="!loading" class="ri-upload-line"></i>
			{{ loading ? '处理中...' : buttonText }}
		</el-button>

		<!-- 导入对话框 -->
		<YsDialog
			:title="dialogTitle"
			v-model="dialogVisible"
			:width="dialogWidth"
			:close-on-click-modal="false"
			:close-on-press-escape="false"
			append-to-body
			@close="handleDialogClose"
		>
			<!-- 步骤1: 参数配置（如果有插槽） -->
			<div v-if="hasParamsSlot && currentStep === 'params'" class="step-content">
				<slot
					name="params"
					:params="importParams"
					:validate="validateParamsResult"
					:update-params="handleParamsUpdate"
				></slot>
			</div>

			<!-- 步骤2（或步骤1无插槽时）: 文件上传 -->
			<div v-if="currentStep === 'upload'" class="step-content">
				<!-- 加载遮罩层 -->
				<div v-if="isProcessing" class="processing-overlay">
					<i class="ri-loader-4-line processing-icon"></i>
					<p class="processing-text">{{ processingText }}</p>
				</div>
				<el-upload
					ref="uploadRef"
					class="upload-area"
					drag
					action="#"
					:auto-upload="false"
					:show-file-list="false"
					:accept="acceptTypes"
					:on-change="handleFileChange"
					:disabled="isProcessing"
				>
					<i class="ri-upload-cloud-2-line el-icon--upload"></i>
					<div class="el-upload__text">
						将文件拖到此处，或<em>点击上传</em>
					</div>
					<template #tip>
						<div class="el-upload__tip">
							<el-alert
								type="info"
								:closable="false"
								show-icon
							>
								<template #title>
									<div class="upload-tips">
										<p>支持格式：{{ acceptTypes }}</p>
										<p>文件大小限制：{{ formatFileSize(maxFileSize) }}</p>
										<p v-if="templateUrl">
											<el-link type="primary" @click="downloadTemplate">
												<i class="ri-download-line"></i>
												下载导入模板
											</el-link>
										</p>
										<p class="upload-hint">上传文件后将自动校验数据，校验通过后立即执行导入</p>
									</div>
								</template>
							</el-alert>
						</div>
					</template>
				</el-upload>
			</div>

			<!-- 步骤3: 导入结果 -->
			<div v-if="currentStep === 'result'" class="step-content">
				<div v-if="isImporting" class="import-progress">
					<el-progress
						:percentage="importProgress"
						:stroke-width="20"
						text-inside
					/>
					<p class="progress-text">正在校验并导入数据，请稍候...</p>
				</div>

				<el-result
					v-else-if="importResult"
					:icon="importResultIcon"
					:title="importResultTitle"
					:sub-title="importResultSubTitle"
				>
					<template #extra>
						<el-descriptions :column="3" border>
							<el-descriptions-item label="成功数量">{{ importResult.successCount }}</el-descriptions-item>
							<el-descriptions-item label="失败数量">
								<span :style="{ color: importResult.failCount > 0 ? '#F56C6C' : '' }">
									{{ importResult.failCount }}
								</span>
							</el-descriptions-item>
							<el-descriptions-item label="总耗时">{{ importResult.costTime || 0 }} ms</el-descriptions-item>
						</el-descriptions>
						<div class="result-actions">
							<el-button
								v-if="importResult.failCount > 0"
								type="warning"
								@click="downloadErrorData"
								:loading="downloading"
							>
								<i class="ri-download-line"></i>
								下载错误数据
							</el-button>
						</div>
					</template>
				</el-result>
			</div>

			<!-- 底部按钮 -->
			<template #footer>
				<!-- 参数配置步骤 -->
				<template v-if="hasParamsSlot && currentStep === 'params'">
					<el-button @click="dialogVisible = false">取消</el-button>
					<el-button type="primary" @click="goToUploadStep" :disabled="!isParamsValid">
						下一步
					</el-button>
				</template>

				<!-- 上传步骤 -->
				<template v-if="currentStep === 'upload'">
					<el-button v-if="hasParamsSlot" @click="currentStep = 'params'">上一步</el-button>
					<el-button v-else @click="dialogVisible = false" :disabled="isProcessing">取消</el-button>
				</template>

				<!-- 导入结果 -->
				<template v-if="currentStep === 'result'">
					<el-button v-if="!isImporting" @click="handleClose">关闭</el-button>
					<el-button v-if="!isImporting" type="primary" @click="handleReimport">重新导入</el-button>
				</template>
			</template>
		</YsDialog>
	</div>
</template>

<script setup lang="ts" name="YsImportExcel">
import { ref, reactive, computed, useSlots } from 'vue';
import { UploadFile, ElMessage, ElMessageBox } from 'element-plus';
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
 * 导入结果接口
 */
interface ImportResult {
	successCount: number;
	failCount: number;
	failDetails?: any[];
	costTime?: number;
}

/**
 * 组件Props接口
 */
interface Props {
	/** 导入类型标识（统一导入模式，如 "systemUser"，设置后将忽略 importData） */
	importType?: string;
	/** 额外参数（统一导入模式，如 { departId: 'xxx' }） */
	extraParams?: Record<string, any>;
	/** 导入接口函数（自定义模式，importType 为空时使用），接收文件与参数 */
	importDataApi?: (file: File, params?: Record<string, any>) => Promise<any>;
	/** 错误数据下载接口函数（可选） */
	downloadErrorApi?: (failDetails: any[]) => Promise<any>;
	/** 表格列定义（保留兼容，简化版不再展示预览表格） */
	columns?: ColumnConfig[];
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
	/** 最大文件大小（字节） */
	maxFileSize?: number;
	/** 接受的文件类型 */
	acceptTypes?: string;
	/** 模板下载URL */
	templateUrl?: string;
	/** 是否禁用 */
	disabled?: boolean;
	/** 是否隐藏按钮（通过工具栏调用时使用） */
	hideButton?: boolean;
	/** 确认弹窗的提示信息，支持 {fileName} 占位符 */
	confirmMessage?: string;
}

const props = withDefaults(defineProps<Props>(), {
	buttonText: '导入',
	buttonType: 'primary',
	size: 'default',
	dialogTitle: '数据导入',
	dialogWidth: '600px',
	maxFileSize: 10 * 1024 * 1024, // 10MB
	acceptTypes: '.xlsx,.xls',
	disabled: false,
	hideButton: false,
	confirmMessage: '确认将文件「{fileName}」中的数据导入吗？导入过程将自动校验数据，校验通过的数据将立即写入。',
	columns: () => [],
});

/**
 * 组件事件
 */
const emit = defineEmits<{
	(e: 'success', result: ImportResult): void;
	(e: 'error', error: any): void;
}>();

// 获取插槽
const slots = useSlots();

// 判断是否有参数插槽
const hasParamsSlot = computed(() => !!slots.params);

// 是否使用统一导入模式（设置了 importType）
const isUnifiedMode = computed(() => !!props.importType);

// 统一导入模式：调用 /api/excel/import/execute
const unifiedImportApi = (file: File, params?: Record<string, any>) => {
	const formData = new FormData();
	formData.append('file', file);
	formData.append('importType', props.importType!);
	const extraParams = { ...props.extraParams, ...params };
	Object.entries(extraParams).forEach(([key, value]) => {
		if (value !== undefined && value !== null && value !== '') {
			formData.append(key, value as string);
		}
	});
	return request.post('/api/excel/import/execute', formData, {
		headers: { 'Content-Type': 'multipart/form-data' },
	});
};

// 导入参数（由父组件通过插槽控制）
const importParams = reactive<Record<string, any>>({});

// 参数校验结果（由父组件通过插槽设置）
const validateParamsResult = ref<{ valid: boolean; message?: string }>({ valid: true });

// 参数是否有效
const isParamsValid = computed(() => validateParamsResult.value.valid);

// 状态管理
const dialogVisible = ref(false);
const loading = ref(false);
const currentStep = ref<'params' | 'upload' | 'result'>('upload');

// 上传相关
const uploadRef = ref();
const currentFile = ref<File | null>(null);

// 导入进度与结果
const isImporting = ref(false);
const isProcessing = ref(false);
const processingText = ref('正在解析文件...');
const importProgress = ref(0);
const importResult = ref<ImportResult | null>(null);
const downloading = ref(false);

// 导入结果图标
const importResultIcon = computed(() => {
	if (!importResult.value) return 'info';
	if (importResult.value.failCount === 0) return 'success';
	if (importResult.value.successCount === 0) return 'error';
	return 'warning';
});

// 导入结果标题
const importResultTitle = computed(() => {
	if (!importResult.value) return '';
	if (importResult.value.failCount === 0) return '导入成功';
	if (importResult.value.successCount === 0) return '导入失败';
	return '部分导入成功';
});

// 导入结果副标题
const importResultSubTitle = computed(() => {
	if (!importResult.value) return '';
	return `成功 ${importResult.value.successCount} 条，失败 ${importResult.value.failCount} 条`;
});

/**
 * 格式化文件大小
 */
const formatFileSize = (size: number): string => {
	if (size < 1024) return size + ' B';
	if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB';
	return (size / (1024 * 1024)).toFixed(2) + ' MB';
};

/**
 * 点击导入按钮
 */
const handleImportClick = () => {
	dialogVisible.value = true;
	currentStep.value = hasParamsSlot.value ? 'params' : 'upload';
	resetData();
};

/**
 * 重置数据
 */
const resetData = () => {
	currentFile.value = null;
	importResult.value = null;
	importProgress.value = 0;
	isImporting.value = false;
	downloading.value = false;
};

/**
 * 重置所有数据（包括参数）
 */
const resetAllData = () => {
	resetData();
	// 重置参数
	Object.keys(importParams).forEach(key => {
		delete importParams[key];
	});
	validateParamsResult.value = { valid: true };
};

/**
 * 处理参数更新
 */
const handleParamsUpdate = (newParams: Record<string, any>) => {
	Object.assign(importParams, newParams);
};

/**
 * 进入上传步骤
 */
const goToUploadStep = () => {
	if (!isParamsValid.value) {
		ElMessage.warning(validateParamsResult.value.message || '请完成参数配置');
		return;
	}
	currentStep.value = 'upload';
};

/**
 * 文件变更处理：上传后弹确认框，用户确认后立即执行导入
 */
const handleFileChange = async (uploadFile: UploadFile) => {
	const file = uploadFile.raw;
	if (!file) return;

	// 文件类型校验
	const fileName = file.name.toLowerCase();
	if (!fileName.endsWith('.xlsx') && !fileName.endsWith('.xls')) {
		ElMessage.error('请上传 Excel 文件（.xlsx 或 .xls 格式）');
		return;
	}

	// 文件大小校验
	if (file.size > props.maxFileSize) {
		ElMessage.error(`文件大小不能超过 ${formatFileSize(props.maxFileSize)}`);
		return;
	}

	currentFile.value = file;

	// 弹确认框
	try {
		await ElMessageBox.confirm(
			props.confirmMessage.replace('{fileName}', file.name),
			'导入确认',
			{
				confirmButtonText: '确认导入',
				cancelButtonText: '取消',
				type: 'warning',
			}
		);
	} catch {
		// 用户取消
		currentFile.value = null;
		return;
	}

	// 确认后直接执行导入
	await executeImport(file);
};

/**
 * 执行导入
 */
const executeImport = async (file: File) => {
	currentStep.value = 'result';
	isImporting.value = true;
	importProgress.value = 0;
	importResult.value = null;

	// 模拟进度（实际导入过程不暴露具体进度）
	const progressTimer = setInterval(() => {
		if (importProgress.value < 90) {
			importProgress.value = Math.floor(importProgress.value + Math.random() * 10);
		}
	}, 500);

	try {
		const api = isUnifiedMode.value ? unifiedImportApi : props.importDataApi!;
		const res = await api(file, { ...importParams });
		clearInterval(progressTimer);
		importProgress.value = 100;

		if (res.code === 200 && res.data) {
			importResult.value = {
				successCount: res.data.successCount || 0,
				failCount: res.data.failCount || 0,
				failDetails: res.data.failDetails || [],
				costTime: res.data.costTime,
			};

			if (importResult.value.failCount === 0) {
				ElMessage.success(`导入成功，共导入 ${importResult.value.successCount} 条数据`);
			} else if (importResult.value.successCount === 0) {
				ElMessage.error(`导入失败，${importResult.value.failCount} 条数据均未通过校验`);
			} else {
				ElMessage.warning(`部分导入成功：成功 ${importResult.value.successCount} 条，失败 ${importResult.value.failCount} 条`);
			}
			emit('success', importResult.value);
		} else {
			importResult.value = {
				successCount: 0,
				failCount: 0,
				failDetails: [],
			};
			ElMessage.error(res.msg || '导入失败');
			emit('error', res);
		}
	} catch (error: any) {
		clearInterval(progressTimer);
		importProgress.value = 100;
		importResult.value = {
			successCount: 0,
			failCount: 0,
			failDetails: [],
		};
		ElMessage.error(error?.message || '导入失败');
		emit('error', error);
	} finally {
		isImporting.value = false;
	}
};

/**
 * 下载错误数据
 * 错误数据通过 ImportResult.failDetails 直接传给后端
 */
const downloadErrorData = async () => {
	const failDetails = importResult.value?.failDetails || [];
	if (failDetails.length === 0) {
		ElMessage.warning('没有错误数据可下载');
		return;
	}

	downloading.value = true;
	try {
		if (isUnifiedMode.value) {
			const res = await request.post('/api/excel/import/error/download', failDetails, {
				params: { importType: props.importType },
				responseType: 'blob',
			});
			const blob = new Blob([res.data], {
				type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
			});
			const url = URL.createObjectURL(blob);
			const link = document.createElement('a');
			link.href = url;
			link.download = `导入错误数据_${Date.now()}.xlsx`;
			document.body.appendChild(link);
			link.click();
			document.body.removeChild(link);
			URL.revokeObjectURL(url);
			ElMessage.success('错误数据下载成功');
		} else if (props.downloadErrorApi) {
			await props.downloadErrorApi(failDetails);
		} else {
			// 自定义模式无下载接口时，回退为 JSON 下载
			const blob = new Blob([JSON.stringify(failDetails, null, 2)], {
				type: 'application/json',
			});
			const url = URL.createObjectURL(blob);
			const link = document.createElement('a');
			link.href = url;
			link.download = `导入错误数据_${Date.now()}.json`;
			document.body.appendChild(link);
			link.click();
			document.body.removeChild(link);
			URL.revokeObjectURL(url);
			ElMessage.success('错误数据下载成功');
		}
	} catch (error: any) {
		ElMessage.error(error?.message || '下载失败');
	} finally {
		downloading.value = false;
	}
};

/**
 * 下载模板
 */
const downloadTemplate = () => {
	if (props.templateUrl) {
		window.open(props.templateUrl, '_blank');
	}
};

/**
 * 关闭对话框
 */
const handleClose = () => {
	dialogVisible.value = false;
};

/**
 * 重新导入
 */
const handleReimport = () => {
	resetData();
	currentStep.value = hasParamsSlot.value ? 'params' : 'upload';
};

/**
 * 对话框关闭处理
 */
const handleDialogClose = () => {
	if (isImporting.value) {
		// 导入中不允许关闭
		return;
	}
	resetAllData();
};

// 暴露方法
defineExpose({
	open: handleImportClick,
	close: handleClose,
	/** 获取当前参数 */
	getParams: () => ({ ...importParams }),
	/** 设置参数 */
	setParams: (params: Record<string, any>) => {
		Object.assign(importParams, params);
	},
	/** 设置参数校验结果 */
	setValidateResult: (result: { valid: boolean; message?: string }) => {
		validateParamsResult.value = result;
	},
});
</script>

<style scoped lang="scss">
.ys-import-excel {
	display: inline-flex;
}

.step-content {
	padding: 20px 0;
	min-height: 300px;
	position: relative;
}

.upload-area {
	:deep(.el-upload) {
		width: 100%;
	}

	:deep(.el-upload-dragger) {
		width: 100%;
		padding: 40px 20px;
	}
}

.upload-tips {
	p {
		margin: 4px 0;
		font-size: 13px;
	}
	.upload-hint {
		color: #E6A23C;
		margin-top: 8px;
	}
}

.import-progress {
	padding: 60px 20px;
	text-align: center;

	.progress-text {
		margin-top: 20px;
		font-size: 14px;
		color: #606266;
	}
}

.result-actions {
	margin-top: 16px;
	display: flex;
	justify-content: center;
	gap: 8px;
}

.processing-overlay {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(255, 255, 255, 0.9);
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	z-index: 10;
	padding: 40px;

	.processing-icon {
		font-size: 48px;
		color: #409eff;
		animation: rotating 2s linear infinite;
	}

	.processing-text {
		margin-top: 16px;
		font-size: 14px;
		color: #606266;
	}

	@keyframes rotating {
		from {
			transform: rotate(0deg);
		}
		to {
			transform: rotate(360deg);
		}
	}
}
</style>
