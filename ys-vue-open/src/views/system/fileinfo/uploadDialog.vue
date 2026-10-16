<!--
 * 文件上传弹窗（简化版）
 * 仅保留基础上传能力，移除分片/拖拽/批量/Token/统计
 * @author: Eric
 -->
<template>
	<div class="upload-dialog-container">
		<YsDialog
			ref="ysDialogRef"
			title="文件上传"
			v-model="isShowDialog"
			:width="720"
			:height="520"
			@close="onCancel"
			:show-footer="false"
		>
			<div class="upload-content">
				<!-- 上传组件 -->
				<YsUpload
					ref="ysUploadRef"
					:upload-config="uploadConfig"
					:display-config="displayConfig"
					:business-config="businessConfig"
					:file-operations="fileOperations"
					@success="handleUploadSuccess"
					@remove="handleRemove"
				/>

				<!-- 底部操作栏 -->
				<div class="upload-footer">
					<el-button @click="onCancel">关闭</el-button>
				</div>
			</div>
		</YsDialog>
	</div>
</template>

<script setup lang="ts" name="UploadDialog">
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import YsUpload from '@/components/YsUpload/index.vue';
import type { UploadFile } from '@/components/YsUpload/types/upload';

const emit = defineEmits(['refresh']);

// 弹窗状态
const isShowDialog = ref(false);
const ysUploadRef = ref();

// 上传配置（仅基础能力，无分片/拖拽/Token）
const uploadConfig = reactive({
	accept: '',
	multiple: true,
	maxSize: 100 * 1024 * 1024, // 100MB
	maxCount: 0, // 0 表示无限制
	autoUpload: true,
});

// 展示配置
const displayConfig = reactive({
	showFileList: true,
	listType: 'text',
	tip: '支持单文件或批量上传，单个文件最大 100MB',
});

// 业务配置
const businessConfig = reactive({
	category: 'user',
	businessId: '',
	storageType: 'local',
});

// 文件操作配置
const fileOperations = reactive({
	download: { enabled: true, useDefault: true },
	remove: { enabled: true, useDefault: true, confirmDelete: true },
});

// 打开弹窗
const openDialog = () => {
	isShowDialog.value = true;
};

// 取消
const onCancel = () => {
	isShowDialog.value = false;
};

// 上传成功
const handleUploadSuccess = (file: UploadFile) => {
	ElMessage.success(`文件 ${file.name} 上传成功`);
	emit('refresh');
};

// 文件被移除
const handleRemove = () => {
	emit('refresh');
};

defineExpose({
	openDialog,
	closeDialog: onCancel,
});
</script>

<style scoped lang="scss">
.upload-dialog-container {
	.upload-content {
		display: flex;
		flex-direction: column;
		height: 100%;
	}

	.upload-footer {
		display: flex;
		justify-content: flex-end;
		gap: 12px;
		margin-top: 16px;
		padding-top: 16px;
		border-top: 1px solid var(--el-border-color-lighter);
	}
}
</style>
