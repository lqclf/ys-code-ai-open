<template>
	<div class="ys-upload" :class="uploadClasses">
		<!-- 上传区域 - 仅在上传模式下显示 -->
		<div
			v-if="props.mode === 'upload'"
			ref="uploadAreaRef"
			class="upload-area"
			:class="uploadAreaClasses"
			@click="handleClick"
			@dragenter="handleDragEnter"
			@dragover="handleDragOver"
			@dragleave="handleDragLeave"
			@drop="handleDrop"
		>
			<!-- 拖拽上传 -->
			<div v-if="mergedConfig.displayConfig.drag" class="upload-dragger">
				<div class="dragger-content">
					<el-icon class="upload-icon">
						<UploadFilled />
					</el-icon>
					<div class="upload-text">
						<div class="primary-text">
							{{ dragState.isDragOver ? '释放文件开始上传' : mergedConfig.displayConfig.dragText || '将文件拖拽到此处，或' }}
						</div>
						<div class="secondary-text">
							<el-button type="primary" :disabled="disabled">点击上传</el-button>
						</div>
					</div>
				</div>
			</div>

			<!-- 普通上传按钮 -->
			<div v-else class="upload-button-wrapper">
				<div class="upload-button">
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
				<!-- 开始上传按钮 - 仅在非自动上传模式且有准备上传的文件时显示 -->
				<div v-if="!mergedConfig.uploadConfig.autoUpload && hasReadyFiles" class="start-upload-inline">
					<el-button type="primary" @click="startAllUploads" :loading="hasUploadingFiles">
						<el-icon>
							<Upload />
						</el-icon>
						{{ uploadButtonText }}
					</el-button>
				</div>
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
					:show-preview="props.fileOperations?.preview?.enabled !== false"
					:show-download="props.fileOperations?.download?.enabled !== false"
					:show-remove="props.fileOperations?.remove?.enabled !== false && !disabled && props.mode === 'upload'"
					:disabled="false"
					:file-operations="props.fileOperations"
					:start-time="uploadStartTimes.get(file.id)"
					@preview="handlePreview"
					@download="handleDownload"
					@remove="handleRemove"
					@pause="handlePause"
					@resume="handleResume"
					@cancel="handleCancel"
					@retry="handleRetry"
				/>
			</transition-group>
		</div>

		<!-- 批量操作 - 仅在上传模式下显示 -->
		<div v-if="props.mode === 'upload' && showBatchActions && fileList.length > 1" class="batch-actions">
			<el-button v-if="hasUploadingFiles" size="small" @click="pauseAll" :disabled="isInBatchCooldown()" title="暂停所有上传中的文件">
				<el-icon>
					<VideoPause />
				</el-icon>
				暂停全部
			</el-button>
			<el-button v-if="hasPausedFiles" size="small" @click="resumeAll" :disabled="isInBatchCooldown()" title="继续所有已暂停的文件">
				<el-icon>
					<VideoPlay />
				</el-icon>
				继续全部
			</el-button>
			<el-button v-if="hasFailedFiles" size="small" @click="retryAll" :disabled="isInBatchCooldown()" title="重试所有上传失败的文件">
				<el-icon>
					<RefreshRight />
				</el-icon>
				重试失败
			</el-button>
			<el-button size="small" @click="clearAll" title="清空文件列表">
				<el-icon>
					<Delete />
				</el-icon>
				清空列表
			</el-button>
		</div>
		<!-- 预览对话框 -->
		<FilePreviewDialog ref="previewDialogRef" @download="(file: UploadFile) => emit('download', file)" />
	</div>
</template>

<script setup lang="ts">
import { ref, computed, watch, defineAsyncComponent } from 'vue';
import { ElButton, ElIcon, ElMessage, ElMessageBox } from 'element-plus';
import {
	Upload,
	UploadFilled,
	InfoFilled,
	VideoPause,
	VideoPlay,
	RefreshRight,
	Delete,
	Document,
	Picture,
	VideoCamera,
	Headset,
} from '@element-plus/icons-vue';
import YsUploadItem from './YsUploadItem.vue';
import { useFileUpload, validateApiConfig } from './api';
import { UploadUtils, useFileAttachmentManager } from './utils';
import type { UploadFile, UploadProps, DragState } from '@/components/YsUpload/types/upload';

const FilePreviewDialog = defineAsyncComponent(() => import('./previewDialog.vue'));

// Props 定义
const props = withDefaults(defineProps<UploadProps>(), {
	// 文件列表
	modelValue: () => [],
	// 文件ID集合（用于回显）
	fileIds: () => [],
	// 文件上传配置 - 文件选择、文件限制等基础配置
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
	// 分片上传配置 - 分片上传相关的所有配置
	chunkConfig: () => ({
		enableChunk: true,
		chunkSize: 2 * 1024 * 1024,
		chunkThreshold: 10 * 1024 * 1024,
		enableResume: true,
	}),
	// 显示配置 - 显示相关的配置
	displayConfig: () => ({
		showFileList: true,
		listType: 'text',
		drag: false,
		tip: '',
		placeholder: '',
		dragText: '将文件拖拽到此处，或点击上传',
		width: undefined,
		height: undefined,
	}),
	// 业务配置 - 业务相关的配置
	businessConfig: () => ({
		businessId: '',
		category: '',
		storageType: '',
		enableDeduplication: true,
	}),
	// 文件操作配置 - 预览、下载、删除等操作配置
	fileOperations: () => ({
		preview: { enabled: true, useDefault: true },
		download: { enabled: true, useDefault: true },
		remove: { enabled: true, useDefault: true, confirmDelete: true },
	}),
	// 接口配置 - 所有上传相关的接口地址
	apiConfig: undefined,
	// 响应码配置 - 响应码相关的配置
	responseConfig: () => ({
		successCode: 200,
		errorCode: [400, 401, 403, 404, 500, 502, 503, 504, 505],
		successCodePath: 'code',
		errorCodePath: 'code',
		messagePath: 'message',
		dataPath: 'data',
	}),
	// 组件配置 - 组件基础配置
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
	preview: [file: UploadFile];
	download: [file: UploadFile];
	exceed: [files: File[], fileList: UploadFile[]];
	'before-upload': [file: File];
	'before-remove': [file: UploadFile, fileList: UploadFile[]];
}>();

// 响应式数据
const fileInputRef = ref<HTMLInputElement>();
const fileList = ref<UploadFile[]>(props.modelValue || []);
const dragState = ref<DragState>({
	isDragOver: false,
	isDragging: false,
	dragCounter: 0,
});
const uploadStartTimes = ref<Map<string, number>>(new Map());
const uploadControllers = ref<Map<string, AbortController>>(new Map());
const previewDialogRef = ref();

// 点击冷却时间记录（用于防止快速点击）
const lastClickTime = ref<Map<string, number>>(new Map());
const lastBatchClickTime = ref<number>(0);
const CLICK_COOLDOWN = 500; // 点击冷却时间（毫秒）
const BATCH_CLICK_COOLDOWN = 1000; // 批量操作冷却时间（毫秒）

// 检查是否在冷却时间内
const isInCooldown = (fileId: string): boolean => {
	const lastTime = lastClickTime.value.get(fileId);
	if (!lastTime) return false;

	const now = Date.now();
	return now - lastTime < CLICK_COOLDOWN;
};

// 检查批量操作是否在冷却时间内
const isInBatchCooldown = (): boolean => {
	const now = Date.now();
	return now - lastBatchClickTime.value < BATCH_CLICK_COOLDOWN;
};

// 记录点击时间
const recordClickTime = (fileId: string) => {
	lastClickTime.value.set(fileId, Date.now());
};

// 记录批量操作点击时间
const recordBatchClickTime = () => {
	lastBatchClickTime.value = Date.now();
};

// 验证接口配置
const validateConfig = () => {
	const validation = validateApiConfig(props.apiConfig, mergedConfig.value.chunkConfig.enableChunk);
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

// 合并配置 - 支持新旧两种配置方式
const mergedConfig = computed(() => {
	// 文件上传配置
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

	// 分片上传配置
	const chunkConfig = {
		enableChunk: props.chunkConfig?.enableChunk ?? true,
		chunkSize: props.chunkConfig?.chunkSize ?? 2 * 1024 * 1024,
		chunkThreshold: props.chunkConfig?.chunkThreshold ?? 10 * 1024 * 1024,
		enableResume: props.chunkConfig?.enableResume ?? true,
	};

	// 显示配置
	const displayConfig = {
		showFileList: props.displayConfig?.showFileList ?? true,
		listType: props.displayConfig?.listType ?? 'text',
		drag: props.displayConfig?.drag ?? false,
		tip: props.displayConfig?.tip ?? '',
		placeholder: props.displayConfig?.placeholder ?? '',
		dragText: props.displayConfig?.dragText ?? '将文件拖拽到此处，或点击上传',
		width: props.displayConfig?.width ?? undefined,
		height: props.displayConfig?.height ?? undefined,
	};

	// 业务配置
	const businessConfig = {
		businessId: props.businessConfig?.businessId ?? '',
		category: props.businessConfig?.category ?? '',
		storageType: props.businessConfig?.storageType ?? '',
		enableDeduplication: props.businessConfig?.enableDeduplication ?? true,
	};

	return {
		uploadConfig,
		chunkConfig,
		displayConfig,
		businessConfig,
	};
});

// 响应码判断工具函数
const responseUtils = {
	// 获取嵌套对象的值
	getNestedValue: (obj: any, path: string): any => {
		if (!path) return obj;
		const keys = path.split('.');
		return keys.reduce((current, key) => current?.[key], obj);
	},

	// 判断是否成功
	isSuccess: (response: any): boolean => {
		const config = props.responseConfig;

		// 优先使用自定义判断函数
		if (config?.isSuccess) {
			return config.isSuccess(response);
		}

		// 根据状态码判断
		const code = responseUtils.getCode(response);
		const successCodes = config?.successCode;
		const errorCodes = config?.errorCode;

		// 检查是否在成功码列表中
		if (successCodes !== undefined) {
			if (Array.isArray(successCodes)) {
				return successCodes.includes(code);
			}
			return code === successCodes;
		}

		// 检查是否不在失败码列表中
		if (errorCodes !== undefined) {
			if (Array.isArray(errorCodes)) {
				return !errorCodes.includes(code);
			}
			return code !== errorCodes;
		}

		// 默认判断：code 为 200 或 success 字段为 true
		return code === 200 || responseUtils.getNestedValue(response, 'success') === true;
	},

	// 判断是否失败
	isError: (response: any): boolean => {
		const config = props.responseConfig;

		// 优先使用自定义判断函数
		if (config?.isError) {
			return config.isError(response);
		}

		return !responseUtils.isSuccess(response);
	},

	// 获取状态码
	getCode: (response: any): any => {
		const config = props.responseConfig;
		const codePath = config?.successCodePath || config?.errorCodePath || 'code';
		return responseUtils.getNestedValue(response, codePath);
	},

	// 获取消息
	getMessage: (response: any): string => {
		const config = props.responseConfig;

		// 优先使用自定义提取函数
		if (config?.getMessage) {
			return config.getMessage(response);
		}

		const messagePath = config?.messagePath || 'message';
		return responseUtils.getNestedValue(response, messagePath) || '未知错误';
	},

	// 获取数据
	getData: (response: any): any => {
		const config = props.responseConfig;

		// 优先使用自定义提取函数
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
			'ys-upload-drag': mergedConfig.value.displayConfig.drag,
		},
	];
});
// 上传区域类名
const uploadAreaClasses = computed(() => {
	return [
		{
			'upload-area-drag': mergedConfig.value.displayConfig.drag,
			'upload-area-dragover': dragState.value.isDragOver,
			'upload-area-disabled': props.disabled,
		},
	];
});
// 上传中文件
const hasUploadingFiles = computed(() => {
	return fileList.value.some((file) => file.status === 'uploading');
});
// 暂停文件
const hasPausedFiles = computed(() => {
	return fileList.value.some((file) => file.status === 'paused');
});
// 失败文件
const hasFailedFiles = computed(() => {
	return fileList.value.some((file) => file.status === 'error');
});
// 显示批量操作按钮
const showBatchActions = computed(() => {
	return fileList.value.length > 1 && (hasUploadingFiles.value || hasPausedFiles.value || hasFailedFiles.value);
});
// 准备上传的文件（包括ready、paused、error状态的文件）
const hasReadyFiles = computed(() => {
	return fileList.value.some((file) => file.status === 'ready' || file.status === 'paused' || file.status === 'error');
});

// 上传按钮文本
const uploadButtonText = computed(() => {
	if (hasUploadingFiles.value) {
		return '上传中...';
	} else if (hasPausedFiles.value) {
		return '继续上传';
	} else if (hasFailedFiles.value) {
		return '重试上传';
	}
	return '开始上传';
});

// 批量操作按钮禁用状态（用于显示冷却状态）
const batchActionDisabled = computed(() => isInBatchCooldown());

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
	target.value = ''; // 清空input，允许重复选择同一文件
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
// 开始上传
const startUpload = async (uploadFile: UploadFile) => {
	// 验证接口配置
	if (!validateConfig()) {
		return;
	}

	// 验证文件对象是否存在
	if (!uploadFile.file) {
		console.error('文件对象不存在，无法上传', uploadFile);
		return;
	}

	const isResume = uploadFile.status === 'paused';

	uploadFile.status = 'uploading';

	// 如果不是恢复上传，才重置进度
	if (!isResume) {
		uploadFile.progress = 0;
		uploadStartTimes.value.set(uploadFile.id, Date.now());
		// 初始化分片信息
		if (mergedConfig.value.chunkConfig.enableChunk && uploadFile.size > mergedConfig.value.chunkConfig.chunkThreshold) {
			uploadFile.chunks = UploadUtils.generateChunks(uploadFile.file, mergedConfig.value.chunkConfig.chunkSize);
		}
		// 生成fileKey用于断点续传（使用MD5哈希确保固定32位长度）
		if (!uploadFile.fileKey) {
			uploadFile.fileKey = UploadUtils.generateFileKey(uploadFile.file);
		}
	}

	// 创建 AbortController 用于取消上传
	const controller = new AbortController();
	uploadControllers.value.set(uploadFile.id, controller);

	try {
		const uploadDTO = {
			businessId: mergedConfig.value.businessConfig.businessId,
			category: mergedConfig.value.businessConfig.category,
			storageType: mergedConfig.value.businessConfig.storageType,
			enableDeduplication: mergedConfig.value.businessConfig.enableDeduplication,
		};

		// 判断是否使用分片上传
		const useChunk = mergedConfig.value.chunkConfig.enableChunk && uploadFile.size > mergedConfig.value.chunkConfig.chunkThreshold;

		let result;
		if (useChunk) {
			// 计算已上传的分片索引
			let resumeFromChunk = 0;
			const uploadedChunksStatus = uploadFile.chunks?.map((chunk) => chunk.uploaded) || [];

			// 找到第一个未上传的分片索引
			if (isResume && uploadFile.chunks) {
				// 如果有后端返回的chunkUploadResult，使用其中的uploadedChunks
				if (uploadFile.chunkUploadResult && uploadFile.chunkUploadResult.uploadedChunks !== undefined) {
					resumeFromChunk = uploadFile.chunkUploadResult.uploadedChunks;
				} else {
					// 否则根据chunks状态查找
					for (let i = 0; i < uploadFile.chunks.length; i++) {
						if (!uploadFile.chunks[i].uploaded) {
							resumeFromChunk = i;
							break;
						}
					}
				}
			}

			// 分片上传（支持断点续传）
			result = await uploadApi.uploadWithChunks(uploadFile.file, {
				chunkSize: mergedConfig.value.chunkConfig.chunkSize,
				uploadDTO,
				signal: controller.signal,
				maxRetries: 3,
				resumeFromChunk,
				uploadedChunks: uploadedChunksStatus,
				fileKey: uploadFile.fileKey, // 传入fileKey确保断点续传时使用相同的标识
				onProgress: (progress) => {
					updateFileProgress(uploadFile.id, progress);
					emit('progress', uploadFile, progress);
				},
				onChunkProgress: (chunkIndex: number, chunkProgress: number, chunkResult?: any) => {
					// 更新分片上传状态
					if (uploadFile.chunks && uploadFile.chunks[chunkIndex]) {
						uploadFile.chunks[chunkIndex].uploaded = chunkProgress >= 100;
					}

					// 保存后端返回的chunkUploadResult信息，用于断点续传
					if (chunkResult) {
						uploadFile.chunkUploadResult = chunkResult;
					}
				},
			});
		} else {
			// 普通上传
			result = await uploadApi.uploadSingleFile(uploadFile.file, {
				uploadDTO,
				signal: controller.signal,
				onProgress: (progress) => {
					updateFileProgress(uploadFile.id, progress);
					emit('progress', uploadFile, progress);
				},
			});
		}

		// 判断上传是否成功
		if (responseUtils.isSuccess(result)) {
			// 上传成功
			uploadFile.status = 'success';
			uploadFile.progress = 100;
			uploadFile.response = result;

			// 从响应中提取数据
			const data = responseUtils.getData(result);
			uploadFile.fileId = data?.fileId || data?.id;
			// 不设置 url 字段，避免影响预览功能（预览时通过 fileId 获取文件流）
			// uploadFile.url = data?.fileUrl;

			// 等待一小段时间确保服务器处理完成
			if (uploadFile.fileId) {
				setTimeout(() => {
					forceUpdateFileList();
				}, 100);
			}

			// 强制更新文件列表，确保状态变更能被检测到
			const index = fileList.value.findIndex((f) => f.id === uploadFile.id);
			if (index > -1) {
				fileList.value.splice(index, 1, { ...uploadFile });
			}

			emit('success', uploadFile, result);
			updateModelValue();
			emit('change', fileList.value);
		} else {
			// 上传失败（根据响应码判断）
			uploadFile.status = 'error';
			uploadFile.error = responseUtils.getMessage(result);

			// 强制更新文件列表，确保状态变更能被检测到
			const index = fileList.value.findIndex((f) => f.id === uploadFile.id);
			if (index > -1) {
				fileList.value.splice(index, 1, { ...uploadFile });
			}

			emit('error', uploadFile, uploadFile.error);
			updateModelValue();
			emit('change', fileList.value);
		}
	} catch (error: any) {
		// 检查是否是取消操作导致的错误
		if (error.name === 'AbortError' || error.message?.includes('aborted') || error.message?.includes('UploadCancelled')) {
			// 如果是取消操作，保持paused状态，不触发错误事件
			uploadFile.status = 'paused';
			console.log('上传已暂停:', uploadFile.name);
		} else if (error.message?.includes('分片已存在') || error.message?.includes('请勿重复上传')) {
			// 幂等性冲突，分片已存在，视为成功
			console.log('分片已存在（幂等性）:', uploadFile.name, error.message);
			// 不改变状态，继续上传下一个分片
		} else {
			// 网络错误或其他异常
			uploadFile.status = 'error';
			uploadFile.error = error.message;

			// 强制更新文件列表，确保状态变更能被检测到
			const index = fileList.value.findIndex((f) => f.id === uploadFile.id);
			if (index > -1) {
				fileList.value.splice(index, 1, { ...uploadFile });
			}

			emit('error', uploadFile, uploadFile.error || '上传失败');
			updateModelValue();
			emit('change', fileList.value);
		}
	} finally {
		// 不删除开始时间，以便恢复时能够计算正确的速度
		// uploadStartTimes.value.delete(uploadFile.id)
		uploadControllers.value.delete(uploadFile.id);
	}
};

// 拖拽相关事件
const handleDragEnter = (e: DragEvent) => {
	if (!mergedConfig.value.displayConfig.drag || props.disabled) return;
	e.preventDefault();
	dragState.value.dragCounter++;
	dragState.value.isDragOver = true;
};
// 拖拽文件到上传区域
const handleDragOver = (e: DragEvent) => {
	if (!mergedConfig.value.displayConfig.drag || props.disabled) return;
	e.preventDefault();
};
// 拖拽文件离开上传区域
const handleDragLeave = (e: DragEvent) => {
	if (!mergedConfig.value.displayConfig.drag || props.disabled) return;
	e.preventDefault();
	dragState.value.dragCounter--;
	if (dragState.value.dragCounter === 0) {
		dragState.value.isDragOver = false;
	}
};
// 拖拽文件到上传区域
const handleDrop = (e: DragEvent) => {
	if (!mergedConfig.value.displayConfig.drag || props.disabled) return;
	e.preventDefault();
	dragState.value.isDragOver = false;
	dragState.value.dragCounter = 0;

	const files = Array.from(e.dataTransfer?.files || []);
	handleFiles(files);
};

// 文件操作事件
const handlePreview = async (file: UploadFile) => {
	const previewConfig = props.fileOperations?.preview;

	// 如果有自定义预览函数，优先使用
	if (previewConfig?.customHandler) {
		try {
			await previewConfig.customHandler(file);
			return;
		} catch (error) {
			console.error('自定义预览函数执行失败:', error);
			ElMessage.error('预览失败');
			return;
		}
	}

	// 使用默认预览逻辑
	if (previewConfig?.useDefault !== false) {
		await fallbackToDialogPreview(file);
	}

	emit('preview', file);
};

const fallbackToDialogPreview = async (file: UploadFile) => {
	// 确保文件有有效的ID才能预览
	const fileId = getFileId(file);
	if (!fileId) {
		ElMessage.warning('文件ID不存在，无法预览');
		return;
	}

	// 为预览准备完整的文件信息
	const previewFile = {
		...file,
		fileId: fileId,
	};

	// 判断是否为图片文件
	const isImage = (file.type || '').toLowerCase().startsWith('image/');

	previewDialogRef.value?.openDialog(previewFile, {
		apiConfig: props.apiConfig,
		onDownload: handleDownload,
		enableCustomPreview: true,
		autoPreview: isImage, // 图片自动预览，非图片显示选项
		title: `文件预览 - ${previewFile.name}`,
	});
};

// 统一的文件ID获取方法
const getFileId = (file: UploadFile): string | null => {
	return file.fileId || file.response?.data?.fileId || file.response?.data?.id || file.response?.id || null;
};

// 下载文件
const handleDownload = async (file: UploadFile) => {
	const downloadConfig = props.fileOperations?.download;
	// 如果有自定义下载函数，优先使用
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

	// 使用默认下载逻辑
	if (downloadConfig?.useDefault !== false) {
		try {
			const fileId = getFileId(file);
			if (!fileId) {
				ElMessage.warning('无法获取文件下载地址');
				return;
			}

			// 优先使用原始文件名，如果不存在则使用当前文件名
			const fileName = file.originalName || file.name;

			// 通过API下载
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

	// 是否需要确认删除
	if (removeConfig?.confirmDelete !== false) {
		try {
			const message = removeConfig?.confirmMessage || `确定要删除文件 "${file.name}" 吗？`;
			await ElMessageBox.confirm(message, '确认删除', {
				type: 'warning',
				confirmButtonText: '确定',
				cancelButtonText: '取消',
			});
		} catch {
			return; // 用户取消删除
		}
	}

	// 如果文件正在上传，先取消上传
	if (file.status === 'uploading' || file.status === 'paused') {
		const controller = uploadControllers.value.get(file.id);
		if (controller) {
			controller.abort();
			uploadControllers.value.delete(file.id);
		}
		file.status = 'cancelled';
	}

	// 如果有自定义删除函数，优先使用
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
		// 使用默认删除逻辑（调用后端API删除文件）
		const fileId = getFileId(file);
		if (fileId) {
			try {
				await uploadApi.deleteFile(fileId);
				ElMessage.success('文件删除成功');
			} catch (error) {
				console.error('删除文件失败:', error);
				ElMessage.error('删除文件失败');
				// 即使后端删除失败，也从列表中移除（可配置）
				// return
			}
		}
	}

	// beforeRemove 钩子
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
		uploadStartTimes.value.delete(file.id);
		uploadControllers.value.delete(file.id);
	}

	updateModelValue();
	emit('remove', file, fileList.value);
	emit('change', fileList.value);
};
// 暂停上传
const handlePause = async (file: UploadFile) => {
	// 检查冷却时间
	if (isInCooldown(file.id)) {
		console.warn('[YsUpload] 点击过于频繁，请稍后再试');
		return;
	}

	// 防止重复暂停
	if (file.status !== 'uploading') {
		return;
	}

	// 记录点击时间
	recordClickTime(file.id);

	file.status = 'paused';

	// 取消上传请求
	const controller = uploadControllers.value.get(file.id);
	if (controller) {
		controller.abort();
		uploadControllers.value.delete(file.id);
	}

	// 不删除开始时间，以便恢复时能够计算正确的速度
	// uploadStartTimes.value.delete(file.id)
};

// 恢复上传
const handleResume = async (file: UploadFile) => {
	// 检查冷却时间
	if (isInCooldown(file.id)) {
		console.warn('[YsUpload] 点击过于频繁，请稍后再试');
		return;
	}

	// 防止重复恢复
	if (file.status !== 'paused') {
		return;
	}

	// 记录点击时间
	recordClickTime(file.id);

	// 确保之前的请求已取消
	const controller = uploadControllers.value.get(file.id);
	if (controller) {
		controller.abort();
		uploadControllers.value.delete(file.id);
	}

	// 不重置进度，保持当前进度继续上传
	// 如果有后端返回的chunkUploadResult，使用其中的uploadedChunks和progress
	if (file.chunkUploadResult) {
		// 更新进度为后端返回的值
		file.progress = Math.min(Math.round(file.chunkUploadResult.progress || 0), 100);

		// 更新分片状态
		if (file.chunks && file.chunkUploadResult.uploadedChunks !== undefined) {
			// 标记已上传的分片
			for (let i = 0; i < file.chunkUploadResult.uploadedChunks && i < file.chunks.length; i++) {
				file.chunks[i].uploaded = true;
			}
		}
	}

	// 延迟一小段时间，确保之前的请求完全取消
	await new Promise((resolve) => setTimeout(resolve, 100));

	// 再次检查状态，防止在延迟期间状态发生变化
	if (file.status === 'paused') {
		startUpload(file);
	}
};
// 取消上传
const handleCancel = (file: UploadFile) => {
	file.status = 'cancelled';
	const controller = uploadControllers.value.get(file.id);
	if (controller) {
		controller.abort();
		uploadControllers.value.delete(file.id);
	}
	uploadStartTimes.value.delete(file.id);
};
// 重试上传
const handleRetry = (file: UploadFile) => {
	if (file.status === 'error') {
		file.error = undefined;
		file.progress = 0;
		startUpload(file);
	}
};

// 批量操作
const pauseAll = () => {
	// 检查批量操作冷却时间
	if (isInBatchCooldown()) {
		ElMessage.warning('操作过于频繁，请稍后再试');
		return;
	}

	recordBatchClickTime();

	fileList.value.filter((file) => file.status === 'uploading').forEach((file) => handlePause(file));
};
// 开始所有上传
const startAllUploads = () => {
	// 检查批量操作冷却时间
	if (isInBatchCooldown()) {
		ElMessage.warning('操作过于频繁，请稍后再试');
		return;
	}

	recordBatchClickTime();

	fileList.value.forEach((file) => {
		if (file.status === 'ready') {
			startUpload(file);
		} else if (file.status === 'paused') {
			handleResume(file);
		} else if (file.status === 'error') {
			handleRetry(file);
		}
	});
};
// 恢复所有上传
const resumeAll = () => {
	// 检查批量操作冷却时间
	if (isInBatchCooldown()) {
		ElMessage.warning('操作过于频繁，请稍后再试');
		return;
	}

	recordBatchClickTime();

	fileList.value.filter((file) => file.status === 'paused').forEach((file) => handleResume(file));
};
// 重试所有上传
const retryAll = () => {
	// 检查批量操作冷却时间
	if (isInBatchCooldown()) {
		ElMessage.warning('操作过于频繁，请稍后再试');
		return;
	}

	recordBatchClickTime();

	fileList.value.filter((file) => file.status === 'error').forEach((file) => handleRetry(file));
};
// 清除所有上传
const clearAll = () => {
	// 取消所有正在上传的文件
	fileList.value
		.filter((file) => file.status === 'uploading' || file.status === 'paused')
		.forEach((file) => {
			const controller = uploadControllers.value.get(file.id);
			if (controller) {
				controller.abort();
				uploadControllers.value.delete(file.id);
			}
		});

	fileList.value = [];
	uploadStartTimes.value.clear();
	uploadControllers.value.clear();
	updateModelValue();
	emit('change', fileList.value);
};

// 工具方法
const updateModelValue = () => {
	emit('update:modelValue', fileList.value);
};

// 更新文件进度（确保响应式更新）
const updateFileProgress = (fileId: string, progress: number) => {
	const index = fileList.value.findIndex((f) => f.id === fileId);
	if (index > -1) {
		fileList.value[index].progress = progress;
		// 强制触发响应式更新
		fileList.value = [...fileList.value];
	}
};

// 强制更新文件列表的方法
const forceUpdateFileList = () => {
	// 触发响应式更新
	fileList.value = [...fileList.value];
};

// 监听 modelValue 变化
watch(
	() => props.modelValue,
	(newValue) => {
		if (newValue && newValue !== fileList.value) {
			// 为回显的文件创建虚拟File对象
			fileList.value = newValue.map((file) => {
				if (!file.file && file.name) {
					// 创建虚拟File对象用于显示
					const virtualFile = new File([], file.name, {
						type: file.type || 'application/octet-stream',
					});
					return {
						...file,
						file: virtualFile,
					};
				}
				return file;
			});
		} else if (!newValue) {
			fileList.value = [];
		}
	},
	{ immediate: true, deep: true }
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
	{ immediate: true }
);

// 监听文件列表状态变化，重置冷却时间
watch(
	() => fileList.value.map((f) => ({ id: f.id, status: f.status })),
	(newStatuses, oldStatuses) => {
		if (!oldStatuses) return;

		// 遍历每个文件的状态变化
		newStatuses.forEach((newStatus, index) => {
			const oldStatus = oldStatuses[index];

			// 当状态从 uploading 变为 paused，或从 paused 变为 uploading 时
			// 重置冷却时间，确保按钮可以立即点击
			if (
				(oldStatus?.status === 'uploading' && newStatus.status === 'paused') ||
				(oldStatus?.status === 'paused' && newStatus.status === 'uploading') ||
				(oldStatus?.status === 'error' && newStatus.status === 'uploading')
			) {
				lastClickTime.value.delete(newStatus.id);
			}
		});
	},
	{ deep: true }
);
// 获取文件图标
const getFileIcon = (file: File) => {
	if (UploadUtils.isImageFile(file)) {
		return Picture;
	} else if (UploadUtils.isVideoFile(file)) {
		return VideoCamera;
	} else if (UploadUtils.isAudioFile(file)) {
		return Headset;
	} else {
		return Document;
	}
};

// 获取所有成功上传的文件ID
const getSuccessFileIds = () => {
	return fileList.value
		.filter((file) => file.status === 'success' && (file.fileId || file.response?.data?.id))
		.map((file) => file.fileId || file.response?.data?.id)
		.filter(Boolean);
};

// 根据文件ID集合加载文件列表
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

// 获取文件ID集合的JSON字符串（用于保存到业务表）
const getFileIdsJson = () => {
	const fileIds = getSuccessFileIds();
	return fileAttachmentManager.fileIdsToJson(fileIds);
};

// 获取完整的附件JSON数据（包含文件详细信息）
const getAttachmentJson = () => {
	return fileAttachmentManager.createAttachmentJson(fileList.value);
};

// 暴露方法
defineExpose({
	selectFiles: handleClick,
	clearFiles: clearAll,
	pauseAll,
	resumeAll,
	retryAll,
	startUpload,
	startAllUploads,
	getSuccessFileIds,
	loadFilesByIds,
	getFileIdsJson,
	getAttachmentJson,
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

		&.upload-area-drag {
			border: 2px dashed var(--el-border-color);
			border-radius: 6px;
			cursor: pointer;
			position: relative;
			overflow: hidden;
			transition: border-color 0.3s;

			&:hover {
				border-color: var(--el-color-primary);
			}

			&.upload-area-dragover {
				border-color: var(--el-color-primary);
				background-color: var(--el-color-primary-light-9);
			}

			&.upload-area-disabled {
				cursor: not-allowed;
				opacity: 0.6;
			}
		}

		.upload-dragger {
			padding: 40px 20px;
			text-align: center;

			.dragger-content {
				.upload-icon {
					font-size: 64px;
					color: var(--el-color-primary-light-3);
					margin-bottom: 16px;
				}

				.upload-text {
					.primary-text {
						font-size: 16px;
						color: var(--el-text-color-primary);
						margin-bottom: 8px;
					}

					.secondary-text {
						font-size: 14px;
						color: var(--el-text-color-secondary);
					}
				}
			}
		}

		.upload-button-wrapper {
			display: flex;
			align-items: center;
			justify-content: space-between;
			gap: 12px;

			.upload-button {
				flex: 0 0 auto;
			}

			.start-upload-inline {
				flex: 0 0 auto;
				margin-left: auto;

				.el-button {
					transition: all 0.3s ease;

					&:hover {
						transform: translateY(-2px);
						box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
					}

					.el-icon {
						margin-right: 8px;
						font-size: 16px;
					}
				}
			}
		}

		.upload-button {
			text-align: left;
		}
	}

	.upload-tip {
		display: flex;
		align-items: flex-start;
		gap: 8px;
		margin-top: 12px;
		padding: 12px 16px;
		background: linear-gradient(135deg, #f6f9fc 0%, #f1f6fe 100%);
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
			display: flex;
			flex-direction: column;
			gap: 6px;

			span {
				display: block;
			}

			:deep(.upload-tip) {
				display: flex;
				flex-direction: column;
				gap: 6px;
				padding: 0;
				background: none;
				border: none;

				.tip-item {
					display: flex;
					align-items: center;
					gap: 6px;
					font-size: 13px;
					color: var(--el-text-color-secondary);

					i {
						color: #67c23a;
						font-size: 14px;
						flex-shrink: 0;
					}

					span {
						flex: 1;
					}
				}
			}
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

	.batch-actions {
		margin-top: 16px;
		display: flex;
		gap: 8px;
		justify-content: flex-end;

		.el-button {
			transition: all 0.3s;

			&:disabled {
				opacity: 0.5;
				cursor: not-allowed;
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