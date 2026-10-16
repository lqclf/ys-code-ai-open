<!--
 * 文件详情弹窗（简化版）
 * 仅展示文件信息并提供下载，移除预览/Token/复制信息等功能
 * @author: Eric
 -->
<template>
	<div class="detail-dialog-container">
		<YsDialog
			ref="ysDialogRef"
			title="文件详情"
			v-model="isShowDialog"
			:width="640"
			@close="onClose"
		>
			<div class="detail-content" v-if="fileData">
				<el-descriptions :column="2" border>
					<el-descriptions-item label="文件名">
						{{ fileData.originalFilename }}
					</el-descriptions-item>
					<el-descriptions-item label="文件ID">
						{{ fileData.id }}
					</el-descriptions-item>
					<el-descriptions-item label="文件大小">
						{{ fileData.fileSizeStr }}
					</el-descriptions-item>
					<el-descriptions-item label="存储类型">
						{{ getStorageTypeLabel() }}
					</el-descriptions-item>
					<el-descriptions-item label="文件MD5">
						<span class="code-text">{{ fileData.fileMd5 }}</span>
					</el-descriptions-item>
					<el-descriptions-item label="业务ID">
						{{ fileData.businessId || '-' }}
					</el-descriptions-item>
					<el-descriptions-item label="分类">
						{{ fileData.category || '-' }}
					</el-descriptions-item>
					<el-descriptions-item label="创建时间">
						{{ formatDateTime(fileData.createTime) }}
					</el-descriptions-item>
				</el-descriptions>
			</div>

			<!-- 自定义底部按钮 -->
			<template #footer>
				<div class="dialog-footer">
					<el-button @click="onClose">关闭</el-button>
					<el-button type="primary" @click="downloadFile">下载文件</el-button>
				</div>
			</template>
		</YsDialog>
	</div>
</template>

<script setup lang="ts" name="DetailDialog">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { useFileinfoApi } from '@/api/system/fileinfo';
import { useFileUploadApi } from '@/api/upload';

const ysDialogRef = ref();
const isShowDialog = ref(false);
const fileData = ref<any>(null);

// 存储类型映射（仅本地/MinIO）
const getStorageTypeLabel = () => {
	if (fileData.value?.storageType === '0' || fileData.value?.storageType === 'local') return '本地存储';
	if (fileData.value?.storageType === '1' || fileData.value?.storageType === 'minio') return 'MinIO';
	return '未知';
};

// 打开弹窗
const openDialog = async (row: any) => {
	try {
		const res = await useFileinfoApi().getById(row.id);
		if (res.code === 200 || res.success) {
			fileData.value = res.data;
			isShowDialog.value = true;
		} else {
			ElMessage.error(res.msg || '获取文件详情失败');
		}
	} catch (error) {
		console.error('获取文件详情失败:', error);
		ElMessage.error('获取文件详情失败');
	}
};

// 关闭弹窗
const onClose = () => {
	isShowDialog.value = false;
	fileData.value = null;
};

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
	if (!dateTime) return '-';
	return new Date(dateTime).toLocaleString('zh-CN');
};

// 下载文件
const downloadFile = async () => {
	if (!fileData.value) return;
	try {
		const res: any = await useFileUploadApi().downloadFile(fileData.value.id);
		const blob = res?.data || res;
		const url = window.URL.createObjectURL(new Blob([blob]));
		const link = document.createElement('a');
		link.href = url;
		link.setAttribute('download', fileData.value.originalFilename || fileData.value.fileName);
		document.body.appendChild(link);
		link.click();
		document.body.removeChild(link);
		window.URL.revokeObjectURL(url);
		ElMessage.success('下载成功');
	} catch (error) {
		ElMessage.error('下载失败');
	}
};

defineExpose({
	openDialog,
});
</script>

<style scoped lang="scss">
.detail-dialog-container {
	.detail-content {
		padding: 4px 0;

		.code-text {
			font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
			background: var(--el-fill-color-light);
			padding: 2px 6px;
			border-radius: 4px;
			font-size: 12px;
			word-break: break-all;
		}
	}

	.dialog-footer {
		display: flex;
		justify-content: flex-end;
		gap: 12px;
	}
}
</style>
