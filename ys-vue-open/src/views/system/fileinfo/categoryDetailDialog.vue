<template>
	<YsDialog
		ref="detailDialogRef"
		:title="`${categoryName} - 文件列表`"
		v-model="isShowDialog"
		width="80%"
		@close="onClose"
		:close-on-click-modal="false"
	>
		<div class="detail-dialog-container">
			<YsTable
				ref="tableRef"
				:request-fn="loadFileList"
				:options="tableOptions"
				:query-params="queryParams"
				:show-pagination="true"
				v-loading="loading"
			>
				<template #fileType="{ row }">
					<el-tag :type="getFileTypeTagType(row.fileType)" size="small">{{ row.fileType }}</el-tag>
				</template>
				<template #fileSize="{ row }">
					<span class="size-text">{{ formatFileSize(row.fileSize) }}</span>
				</template>
				<template #uploadTime="{ row }">
					<span class="time-text">{{ formatTime(row.uploadTime) }}</span>
				</template>
			</YsTable>
		</div>
	</YsDialog>
</template>

<script setup lang="ts" name="FileCategoryDetailDialog">
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { useFileinfoApi } from '@/api/system/fileinfo';

const isShowDialog = ref(false);
const loading = ref(false);
const categoryName = ref('');
const category = ref('');

const queryParams = reactive<any>({
	pageNum: 1,
	pageSize: 10,
	category: '',
});

const tableOptions = reactive<any>({
	columns: [
		{ type: 'seq', width: 60, align: 'center' },
		{ title: '文件名称', field: 'originalFilename', minWidth: 200 },
		{ title: '文件类型', field: 'fileType', width: 120, slots: { default: 'fileType' }, align: 'center' },
		{ title: '文件大小', field: 'fileSize', width: 140, slots: { default: 'fileSize' }, align: 'center' },
		{ title: '上传者', field: 'createName', width: 120, align: 'center' },
		{ title: '上传时间', field: 'createTime', width: 180, align: 'center' },
	],
});

const formatFileSize = (size: number): string => {
	const units = ['B', 'KB', 'MB', 'GB', 'TB'];
	let unitIndex = 0;
	let fileSize = size;

	while (fileSize >= 1024 && unitIndex < units.length - 1) {
		fileSize /= 1024;
		unitIndex++;
	}
	
	return fileSize.toFixed(2) + ' ' + units[unitIndex];
};

const formatTime = (time: string): string => {
	if (!time) return '-';
	const date = new Date(time);
	return date.toLocaleString('zh-CN');
};

const getFileTypeTagType = (fileType: string): string => {
	const typeMap: any = {
		'图片': 'success',
		'文档': 'primary',
		'视频': 'warning',
		'音频': 'info',
		'其他': 'default',
	};
	return typeMap[fileType] || 'default';
};

const loadFileList = async (params: any) => {
	loading.value = true;
	try {
		const res = await useFileinfoApi().list({
			...params,
			category: category.value,
		});
		if (res.code === 200) {
			return {
				code: 200,
				data: res.data.records || [],
				total: res.data.total || 0,
			};
		} else {
			ElMessage.error('获取文件列表失败');
			return {
				code: 500,
				data: [],
				total: 0,
			};
		}
	} catch (error) {
		console.error('加载文件列表失败:', error);
		ElMessage.error('加载文件列表失败,请稍后重试');
		return {
			code: 500,
			data: [],
			total: 0,
		};
	} finally {
		loading.value = false;
	}
};

const openDialog = (row: any) => {
	console.log(row);
	categoryName.value = row.categoryName;
	category.value = row.category;
	isShowDialog.value = true;
};

const onClose = () => {
	isShowDialog.value = false;
};

defineExpose({
	openDialog,
});
</script>

<style scoped lang="scss">
.detail-dialog-container {
	padding: 16px 0;

	.size-text {
		font-weight: 600;
		color: #409eff;
	}

	.time-text {
		color: var(--next-text-placeholder);
		font-size: 13px;
	}
}
</style>
