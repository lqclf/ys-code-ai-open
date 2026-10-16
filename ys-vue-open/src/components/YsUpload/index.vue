<!--
   YsUpload 文件上传组件（简化版）
   仅支持单文件/批量上传、下载、删除，不支持分片上传、文件预览
   @author: Eric
   @date: 2025-06-16
-->
<template>
	<div class="ys-upload" :class="uploadClasses">
		<!-- 上传区域 - 仅在上传模式下显示 -->
		<div
			v-if="props.mode === 'upload'"
			class="upload-area"
			@click="handleClick"
		>
			<div class="upload-button-wrapper">
				<el-button
					type="primary"
					:disabled="disabled || (mergedConfig.uploadConfig.maxCount > 0 && fileList.length >= mergedConfig.uploadConfig.maxCount)"
				>
					<el-icon>
						<Upload />
					</el-icon>
					选择文件
				</el-button>
			</div>

			<!-- 隐藏的文件输入框 -->
			<input
				ref="fileInputRef"
				type="file"
				:accept="mergedConfig.uploadConfig.accept"
				:multiple="mergedConfig.uploadConfig.multiple"
				:disabled="disabled"
				style="display: none"
				@change="handleFileSelect"
			/>
		</div>

		<!-- 提示信息 - 仅在上传模式下显示，且没有文件时显示 -->
		<div v-if="props.mode === 'upload' && fileList.length === 0" class="upload-tip">
			<el-icon>
				<InfoFilled />
			</el-icon>
			<div class="tip-content">
				<span v-if="mergedConfig.displayConfig.tip">{{ mergedConfig.displayConfig.tip }}</span>
				<slot v-else name="tip"></slot>
			</div>
		</div>

		<!-- 文件列表 -->
		<div
			v-if="mergedConfig.displayConfig.showFileList && fileList.length > 0"
			class="upload-list"
			:class="`upload-list-${mergedConfig.displayConfig.listType}`"
		>
			<transition-group name="upload-list" tag="div">
				<YsUploadItem
					v-for="file in fileList"
					:key="file.id"
					:file="file"
					:mode="props.mode"
					:list-type="mergedConfig.displayConfig.listType"
					:show-download="props.fileOperations?.download?.enabled !== false"
					:show-remove="props.fileOperations?.remove?.enabled !== false && !disabled && props.mode === 'upload'"
					:file-operations="props.fileOperations"
					@download="handleDownload"
					@remove="handleRemove"
				/>
			</transition-group>
		</div>
	</div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { ElButton, ElIcon, ElMessage, ElMessageBox } from 'element-plus';
import { Upload, InfoFilled } from '@element-plus/icons-vue';
import YsUploadItem from './YsUploadItem.vue';
import { useFileUpload, validateApiConfig } from './api';
import { UploadUtils, useFileAttachmentManager } from './utils';
import type { UploadFile, UploadProps } from '@/components/YsUpload/types/upload';

// Props 定义
const props = withDefaults(defineProps<UploadProps>(), {
	// 文件列表
	modelValue: () => [],
	// 文件ID集合（用于回显）
	fileIds: () => [],
	// 文件上传配置
	uploadConfig: () => ({
		action: '/api/upload/file/upload',
		method: 'POST',
		headers: {},
		data: {},
		name: 'file',
		accept: '',
		multiple: true,
		maxSize: 50 * 1024 * 1024,
		maxCount: 0,
		autoUpload: true,
	}),
	// 显示配置
	displayConfig: () => ({
		showFileList: true,
		listType: 'text',
		tip: '',
		placeholder: '',
		width: undefined,
		height: undefined,
	}),
	// 业务配置
	businessConfig: () => ({
		businessId: '',
		category: '',
		storageType: '',
	}),
	// 文件操作配置
	fileOperations: () => ({
		download: { enabled: true, useDefault: true },
		remove: { enabled: true, useDefault: true, confirmDelete: true },
	}),
	// 接口配置
	apiConfig: undefined,
	// 响应码配置
	responseConfig: () => ({
		successCode: 200,
		errorCode: [400, 401, 403, 404, 500, 502, 503, 504, 505],
		successCodePath: 'code',
		errorCodePath: 'code',
		messagePath: 'message',
		dataPath: 'data',
	}),
	// 组件配置
	disabled: false,
	mode: 'upload',
});

// Emits 定义
const emit = defineEmits<{
	'update:modelValue': [files: UploadFile[]];
	change: [fileList: UploadFile[]];
	progress: [file: UploadFile, progress: number];
	success: [file: UploadFile, response: any];
	error: [file: UploadFile, error: string];
	remove: [file: UploadFile, fileList: UploadFile[]];
	download: [file: UploadFile];
	exceed: [files: File[], fileList: UploadFile[]];
	'before-upload': [file: File];
	'before-remove': [file: UploadFile, fileList: UploadFile[]];
}>();

// 响应式数据
const fileInputRef = ref<HTMLInputElement>();
const fileList = ref<UploadFile[]>(props.modelValue || []);

// 验证接口配置
const validateConfig = () => {
	const validation = validateApiConfig(props.apiConfig);
	if (!validation.valid) {
		validation.errors.forEach((error) => {
			console.error('[YsUpload] 配置错误:', error);
			ElMessage.error(error);
		});
		return false;
	}
	return true;
};

// Composables
const uploadApi = useFileUpload(props.apiConfig);
const fileAttachmentManager = useFileAttachmentManager();

// 合并配置
const mergedConfig = computed(() => {
	const uploadConfig = {
		action: props.uploadConfig?.action ?? '/api/upload/file/upload',
		method: props.uploadConfig?.method ?? 'POST',
		headers: props.uploadConfig?.headers ?? {},
		data: props.uploadConfig?.data ?? {},
		name: props.uploadConfig?.name ?? 'file',
		accept: props.uploadConfig?.accept ?? '',
		multiple: props.uploadConfig?.multiple ?? true,
		maxSize: props.uploadConfig?.maxSize ?? 50 * 1024 * 1024,
		maxCount: props.uploadConfig?.maxCount ?? 0,
		autoUpload: props.uploadConfig?.autoUpload ?? true,
	};

	const displayConfig = {
		showFileList: props.displayConfig?.showFileList ?? true,
		listType: props.displayConfig?.listType ?? 'text',
		tip: props.displayConfig?.tip ?? '',
		placeholder: props.displayConfig?.placeholder ?? '',
		width: props.displayConfig?.width ?? undefined,
		height: props.displayConfig?.height ?? undefined,
	};

	const businessConfig = {
		businessId: props.businessConfig?.businessId ?? '',
		category: props.businessConfig?.category ?? '',
		storageType: props.businessConfig?.storageType ?? '',
	};

	return { uploadConfig, displayConfig, businessConfig };
});

// 响应码判断工具函数
const responseUtils = {
	getNestedValue: (obj: any, path: string): any => {
		if (!path) return obj;
		const keys = path.split('.');
		return keys.reduce((current, key) => current?.[key], obj);
	},

	isSuccess: (response: any): boolean => {
		const config = props.responseConfig;
		if (config?.isSuccess) {
			return config.isSuccess(response);
		}
		const code = responseUtils.getCode(response);
		const successCodes = config?.successCode;
		const errorCodes = config?.errorCode;
		if (successCodes !== undefined) {
			if (Array.isArray(successCodes)) {
				return successCodes.includes(code);
			}
			return code === successCodes;
		}
		if (errorCodes !== undefined) {
			if (Array.isArray(errorCodes)) {
				return !errorCodes.includes(code);
			}
			return code !== errorCodes;
		}
		return code === 200 || responseUtils.getNestedValue(response, 'success') === true;
	},

	getCode: (response: any): any => {
		const config = props.responseConfig;
		const codePath = config?.successCodePath || config?.errorCodePath || 'code';
		return responseUtils.getNestedValue(response, codePath);
	},

	getMessage: (response: any): string => {
		const config = props.responseConfig;
		if (config?.getMessage) {
			return config.getMessage(response);
		}
		const messagePath = config?.messagePath || 'message';
		return responseUtils.getNestedValue(response, messagePath) || '未知错误';
	},

	getData: (response: any): any => {
		const config = props.responseConfig;
		if (config?.getData) {
			return config.getData(response);
		}
		const dataPath = config?.dataPath || 'data';
		return responseUtils.getNestedValue(response, dataPath);
	},
};

// 计算属性
const uploadClasses = computed(() => {
	return [
		{
			'ys-upload-disabled': props.disabled,
		},
	];
});

// 方法
const handleClick = () => {
	if (props.disabled) return;
	if (mergedConfig.value.uploadConfig.maxCount > 0 && fileList.value.length >= mergedConfig.value.uploadConfig.maxCount) {
		ElMessage.warning(`最多只能上传 ${mergedConfig.value.uploadConfig.maxCount} 个文件`);
		return;
	}
	fileInputRef.value?.click();
};

// 处理文件选择
const handleFileSelect = (event: Event) => {
	const target = event.target as HTMLInputElement;
	const files = Array.from(target.files || []);
	handleFiles(files);
	target.value = '';
};

// 处理文件
const handleFiles = async (files: File[]) => {
	if (files.length === 0) return;

	// 检查文件数量限制
	if (mergedConfig.value.uploadConfig.maxCount > 0) {
		const remainingSlots = mergedConfig.value.uploadConfig.maxCount - fileList.value.length;
		if (files.length > remainingSlots) {
			files = files.slice(0, remainingSlots);
			emit('exceed', files, fileList.value);
			ElMessage.warning(`最多只能上传 ${mergedConfig.value.uploadConfig.maxCount} 个文件，已自动选择前 ${remainingSlots} 个文件`);
		}
	}

	// 文件验证和处理
	for (const file of files) {
		// 文件大小检查
		if (mergedConfig.value.uploadConfig.maxSize && file.size > mergedConfig.value.uploadConfig.maxSize) {
			ElMessage.error(`文件 ${file.name} 大小超过限制 ${UploadUtils.formatFileSize(mergedConfig.value.uploadConfig.maxSize)}`);
			continue;
		}

		// 文件类型检查
		if (mergedConfig.value.uploadConfig.accept && !UploadUtils.checkFileType(file, mergedConfig.value.uploadConfig.accept)) {
			ElMessage.error(`文件 ${file.name} 类型不支持`);
			continue;
		}

		// beforeUpload 钩子
		if (props.beforeUpload) {
			try {
				const result = await props.beforeUpload(file);
				if (result === false) {
					continue;
				}
			} catch (error) {
				ElMessage.error(`文件 ${file.name} 上传前检查失败`);
				continue;
			}
		}

		// 创建上传文件对象
		const uploadFile = UploadUtils.createUploadFile(file);
		fileList.value.push(uploadFile);

		// 自动上传
		if (mergedConfig.value.uploadConfig.autoUpload) {
			startUpload(uploadFile);
		}
	}

	updateModelValue();
	emit('change', fileList.value);
};

// 开始上传（仅普通上传）
const startUpload = async (uploadFile: UploadFile) => {
	if (!validateConfig()) {
		return;
	}

	if (!uploadFile.file) {
		console.error('文件对象不存在，无法上传', uploadFile);
		return;
	}

	uploadFile.status = 'uploading';
	uploadFile.progress = 0;

	try {
		const uploadDTO = {
			businessId: mergedConfig.value.businessConfig.businessId,
			category: mergedConfig.value.businessConfig.category,
			storageType: mergedConfig.value.businessConfig.storageType,
		};

		const result = await uploadApi.uploadSingleFile(uploadFile.file, {
			uploadDTO,
			onProgress: (progress) => {
				uploadFile.progress = progress;
				emit('progress', uploadFile, progress);
			},
		});

		// 判断上传是否成功
		if (responseUtils.isSuccess(result)) {
			uploadFile.status = 'success';
			uploadFile.progress = 100;
			uploadFile.response = result;

			const data = responseUtils.getData(result);
			uploadFile.fileId = data?.fileId || data?.id;

			emit('success', uploadFile, result);
			updateModelValue();
			emit('change', fileList.value);
		} else {
			uploadFile.status = 'error';
			uploadFile.error = responseUtils.getMessage(result);
			emit('error', uploadFile, uploadFile.error);
			updateModelValue();
			emit('change', fileList.value);
		}
	} catch (error: any) {
		uploadFile.status = 'error';
		uploadFile.error = error.message;
		emit('error', uploadFile, uploadFile.error || '上传失败');
		updateModelValue();
		emit('change', fileList.value);
	}
};

// 下载文件
const handleDownload = async (file: UploadFile) => {
	const downloadConfig = props.fileOperations?.download;
	if (downloadConfig?.customHandler) {
		try {
			await downloadConfig.customHandler(file);
			return;
		} catch (error) {
			console.error('自定义下载函数执行失败:', error);
			ElMessage.error('下载失败');
			return;
		}
	}

	if (downloadConfig?.useDefault !== false) {
		try {
			const fileId = getFileId(file);
			if (!fileId) {
				ElMessage.warning('无法获取文件下载地址');
				return;
			}
			const fileName = file.originalName || file.name;
			const response = await uploadApi.downloadFile(fileId);
			const blob = response as unknown as Blob;
			UploadUtils.downloadBlob(blob, fileName);
		} catch (error) {
			console.error('下载文件失败:', error);
			ElMessage.error('下载失败');
		}
	}

	emit('download', file);
};

// 移除文件
const handleRemove = async (file: UploadFile) => {
	const removeConfig = props.fileOperations?.remove;

	if (removeConfig?.confirmDelete !== false) {
		try {
			const message = removeConfig?.confirmMessage || `确定要删除文件 "${file.name}" 吗？`;
			await ElMessageBox.confirm(message, '确认删除', {
				type: 'warning',
				confirmButtonText: '确定',
				cancelButtonText: '取消',
			});
		} catch {
			return;
		}
	}

	if (removeConfig?.customHandler) {
		try {
			const result = await removeConfig.customHandler(file);
			if (result === false) {
				return;
			}
		} catch (error) {
			console.error('自定义删除函数执行失败:', error);
			ElMessage.error('删除失败');
			return;
		}
	} else if (removeConfig?.useDefault !== false) {
		const fileId = getFileId(file);
		if (fileId) {
			try {
				await uploadApi.deleteFile(fileId);
				ElMessage.success('文件删除成功');
			} catch (error) {
				console.error('删除文件失败:', error);
				ElMessage.error('删除文件失败');
			}
		}
	}

	if (props.onRemove) {
		try {
			const result = props.onRemove(file, fileList.value);
			if (result === false) {
				return;
			}
		} catch (error) {
			return;
		}
	}

	const index = fileList.value.findIndex((f) => f.id === file.id);
	if (index > -1) {
		fileList.value.splice(index, 1);
	}

	updateModelValue();
	emit('remove', file, fileList.value);
	emit('change', fileList.value);
};

// 工具方法
const updateModelValue = () => {
	emit('update:modelValue', fileList.value);
};

const getFileId = (file: UploadFile): string | null => {
	return file.fileId || file.response?.data?.fileId || file.response?.data?.id || file.response?.id || null;
};

// 监听 modelValue 变化
watch(
	() => props.modelValue,
	(newValue) => {
		if (newValue && newValue !== fileList.value) {
			fileList.value = newValue.map((file) => {
				if (!file.file && file.name) {
					const virtualFile = new File([], file.name, {
						type: file.type || 'application/octet-stream',
					});
					return { ...file, file: virtualFile };
				}
				return file;
			});
		} else if (!newValue) {
			fileList.value = [];
		}
	},
	{ immediate: true, deep: true },
);

// 监听文件ID集合变化，用于回显
watch(
	() => props.fileIds,
	async (newFileIds) => {
		if (newFileIds && newFileIds.length > 0 && !props.modelValue?.length) {
			try {
				const files = await fileAttachmentManager.getFilesForDisplay(newFileIds);
				fileList.value = files;
				updateModelValue();
				emit('change', fileList.value);
			} catch (error) {
				console.error('根据文件ID回显失败:', error);
			}
		}
	},
	{ immediate: true },
);

// 暴露方法
const getSuccessFileIds = () => {
	return fileList.value
		.filter((file) => file.status === 'success' && (file.fileId || file.response?.data?.id))
		.map((file) => file.fileId || file.response?.data?.id)
		.filter(Boolean);
};

const loadFilesByIds = async (fileIds: string[]) => {
	if (!fileIds || fileIds.length === 0) {
		fileList.value = [];
		updateModelValue();
		emit('change', fileList.value);
		return;
	}
	try {
		const files = await fileAttachmentManager.getFilesForDisplay(fileIds);
		fileList.value = files;
		updateModelValue();
		emit('change', fileList.value);
	} catch (error) {
		console.error('加载文件失败:', error);
	}
};

const clearAll = () => {
	fileList.value = [];
	updateModelValue();
	emit('change', fileList.value);
};

defineExpose({
	selectFiles: handleClick,
	clearFiles: clearAll,
	startUpload,
	getSuccessFileIds,
	loadFilesByIds,
	getFileIdsJson: () => fileAttachmentManager.fileIdsToJson(getSuccessFileIds()),
	getAttachmentJson: () => fileAttachmentManager.createAttachmentJson(fileList.value),
});
</script>

<style scoped lang="scss">
.ys-upload {
	width: 100%;

	&.ys-upload-disabled {
		opacity: 0.6;
		pointer-events: none;
	}

	.upload-area {
		position: relative;
		overflow: hidden;

		.upload-button-wrapper {
			display: flex;
			align-items: center;
			justify-content: flex-start;
		}
	}

	.upload-tip {
		display: flex;
		align-items: flex-start;
		gap: 8px;
		margin-top: 12px;
		padding: 12px 16px;
		background: var(--next-bg-card-hover);
		border-radius: 8px;
		border: 1px solid var(--el-border-color-lighter);
		font-size: 13px;
		color: var(--el-text-color-secondary);
		line-height: 1.6;

		.el-icon {
			font-size: 16px;
			color: var(--el-color-primary);
			flex-shrink: 0;
			margin-top: 1px;
		}

		.tip-content {
			flex: 1;
			min-width: 0;
		}
	}

	.upload-list {
		margin-top: 16px;

		&.upload-list-picture-card {
			.upload-list-item {
				display: inline-block;
				margin-right: 8px;
				margin-bottom: 8px;
			}
		}
	}
}

// 列表动画
.upload-list-enter-active,
.upload-list-leave-active {
	transition: all 0.3s ease;
}

.upload-list-enter-from,
.upload-list-leave-to {
	opacity: 0;
	transform: translateX(-20px);
}

.upload-list-move {
	transition: transform 0.3s ease;
}
</style>
