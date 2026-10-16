<template>
	<div class="page-container">
		<el-card>
			<YsTable
				ref="tableRef"
				:request-fn="useFileinfoApi().list"
				:options="tableOptions"
				:query-params="searchForm"
				:show-pagination="true"
				:show-selection-alert="true"
				:events="tableEvents"
				@request-success="handleSuccess"
			>
				<!-- 页面头部区域 -->
				<template #page-header>
					<div class="page-header">
						<el-form :model="searchForm" ref="searchFormRef" label-width="90px" class="search-form">
							<el-input placeholder="请输入文件名" class="w-180 mr20" v-model="searchForm.fileName" clearable @keyup.enter="searchTable" />
							<el-input
								placeholder="请输入原始文件名"
								class="w-180 mr20"
								v-model="searchForm.originalFilename"
								clearable
								@keyup.enter="searchTable"
							/>
							<el-select class="w-180 mr20" v-model="searchForm.fileType" placeholder="请选择文件类型" clearable>
								<el-option label="图片" value="image" />
								<el-option label="文档" value="document" />
								<el-option label="视频" value="video" />
								<el-option label="音频" value="audio" />
								<el-option label="其他" value="other" />
							</el-select>
							<el-select class="w-180 mr20" v-model="searchForm.storageType" placeholder="请选择存储类型" clearable>
								<el-option label="本地存储" value="0" />
								<el-option label="MinIO" value="1" />
							</el-select>
							<el-select class="w-180 mr20" v-model="searchForm.uploadStatus" placeholder="请选择上传状态" clearable>
								<el-option label="上传中" :value="0" />
								<el-option label="上传完成" :value="1" />
								<el-option label="上传失败" :value="2" />
							</el-select>
							<el-button type="primary" class="ml10" @click="searchTable">
								<i class="ri-search-line"></i>
								查询
							</el-button>
							<el-button type="default" class="ml10" @click="resetTable">
								<i class="ri-reset-left-line"></i>
								重置
							</el-button>
						</el-form>
					</div>
				</template>

				<!-- 文件类型列 -->
				<template #fileType="scope">
					<el-tag
						v-if="getFileTypeOption(scope.row)"
						:key="`fileType-${scope.row.fileType}`"
						:type="tagType(state.fileTypeOptions.findIndex(item => item.value === scope.row.fileType) + 1)"
						>{{ scope.row.fileType_Text }}</el-tag
					>
				</template>

				<!-- 存储类型列 -->
				<template #storageType="scope">
					<el-tag
						v-if="getStorageTypeOption(scope.row)"
						:key="`storageType-${scope.row.storageType}`"
						:type="tagType(state.storageTypeOptions.findIndex(item => item.value === scope.row.storageType) + 1)"
						>{{ scope.row.storageType_Text }}</el-tag
					>
				</template>

				<!-- 上传状态列 -->
				<template #uploadStatus="scope">
					<el-tag
						v-if="getUploadStatusOption(scope.row)"
						:key="`uploadStatus-${scope.row.uploadStatus}`"
						:type="tagType(state.uploadStatusOptions.findIndex(item => item.value === scope.row.uploadStatus) + 1)"
						>{{ scope.row.uploadStatus_Text }}</el-tag
					>
				</template>

				<!-- 操作列 -->
				<template #action="scope">
					<div class="operation-btn-group">
						<el-link type="primary" underline="never" class="pr6 pl6" @click="openDialogCRU('view', scope.row)" v-auth="'system:fileinfo:query'"> 详情 </el-link>
						<el-link type="success" underline="never" class="pr6 pl6" @click="downloadFile(scope.row)" v-auth="'system:fileinfo:download'"> 下载 </el-link>
						<el-dropdown :hide-on-click="false" v-auths="['system:fileinfo:delete']">
							<span class="el-dropdown-link">
								<el-link type="primary" underline="never" class="pr6 pl6">
									<i class="ri-arrow-right-double-line"></i>
								</el-link>
							</span>
							<template #dropdown>
								<el-dropdown-menu>
									<el-dropdown-item>
										<el-link type="danger" underline="never" class="pr6 pl6" @click="onDelete(scope.row)" v-auth="'system:fileinfo:delete'"> 逻辑删除 </el-link>
									</el-dropdown-item>
									<el-dropdown-item>
										<el-link type="danger" underline="never" class="pr6 pl6" @click="onPhysicalDelete(scope.row)" v-auth="'system:fileinfo:delete'"> 物理删除 </el-link>
									</el-dropdown-item>
								</el-dropdown-menu>
							</template>
						</el-dropdown>
					</div>
				</template>
			</YsTable>
		</el-card>
		<!-- 引入组件 -->
		<UploadDialog ref="uploadDialogRef" @refresh="searchTable()" />
		<DetailDialog ref="detailDialogRef" />
	</div>
</template>

<script setup lang="ts" name="systemFileinfo">
import { defineAsyncComponent, reactive, onMounted, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useFileinfoApi } from '@/api/system/fileinfo';
import { commonApi } from '@/api/common';
import { useFileUploadApi } from '@/api/upload';

// 引入组件
const UploadDialog = defineAsyncComponent(() => import('@/views/system/fileinfo/uploadDialog.vue'));
const DetailDialog = defineAsyncComponent(() => import('@/views/system/fileinfo/detailDialog.vue'));

// 定义变量内容
const uploadDialogRef = ref<any>(null);
const detailDialogRef = ref<any>(null);
const tableRef = ref<any>(null);

// 搜索表单
const searchForm = reactive({
	fileName: '',
	originalFilename: '',
	fileType: '',
	storageType: '',
	uploadStatus: '',
	category: '',
});

const state = reactive({
	storageTypeOptions: [] as EnumItemType[],
	uploadStatusOptions: [] as EnumItemType[],
	fileTypeOptions: [] as EnumItemType[],
});

const getFileTypeOption = (row: any) => {
	return state.fileTypeOptions.find(item => item.value === row.fileType);
};

const getStorageTypeOption = (row: any) => {
	return state.storageTypeOptions.find(item => item.value === row.storageType);
};

const getUploadStatusOption = (row: any) => {
	return state.uploadStatusOptions.find(item => item.value === row.uploadStatus);
};

const tagType = (type: number) => {
	switch (type) {
		case 1:
			return 'success';
		case 2:
			return 'primary';
		case 3:
			return 'warning';
		case 4:
			return 'info';
		default:
			return 'primary';
	}
};

const tableOptions = reactive<any>({
	columns: [
		{ type: 'checkbox', width: 55, align: 'center' },
		{ type: 'seq', width: 80, align: 'center' },
		{ title: '文件名', field: 'originalFilename' },
		{ title: '文件类型', field: 'fileType', width: 100, slots: { default: 'fileType' } },
		{ title: '文件大小', field: 'fileSizeStr', width: 120 },
		{ title: '存储类型', field: 'storageType', width: 120, slots: { default: 'storageType' } },
		{ title: '上传状态', field: 'uploadStatus', width: 120, slots: { default: 'uploadStatus' } },
		{ title: '上传时间', field: 'createTime', width: 160 },
		{ title: '上传人', field: 'createName', width: 120 },
		{ title: '操作', field: 'action', width: 180, slots: { default: 'action' }, align: 'center' },
	],
	toolbarConfig: {
		size: 'small',
		refresh: true,
		zoom: true,
		custom: true,
		buttons: [
				{ name: '上传文件', code: 'fileUpload', status: 'success', icon: 'cn cn-upload', auth: 'system:fileinfo:upload' },
				{ name: '批量删除', code: 'batchDelete', status: 'danger', icon: 'cn cn-shanchu_line', auth: 'system:fileinfo:delete' },
			],
	},
});

const tableEvents = {
	toolbarButtonClick(params: any) {
		switch (params.code) {
			case 'fileUpload':
				openDialogCRU('upload');
				break;
			case 'batchDelete':
				handleBatchDelete();
				break;
		}
	},
};

const searchTable = (resetPage = true) => {
	tableRef.value?.search(resetPage);
};

const resetTable = () => {
	tableRef.value?.reset();
};

const handleSuccess = (res: any) => {};

const openDialogCRU = (type: string, row?: any) => {
	if (type === 'upload') {
		uploadDialogRef.value.openDialog();
	} else if (type === 'view') {
		detailDialogRef.value.openDialog(row);
	}
};

const downloadFile = async (row: any) => {
	try {
		const res = await useFileUploadApi().downloadFile(row.id);
		if (res) {
			const url = window.URL.createObjectURL(new Blob([res.data || res]));
			const link = document.createElement('a');
			link.href = url;
			link.setAttribute('download', row.originalFilename || row.fileName);
			document.body.appendChild(link);
			link.click();
			document.body.removeChild(link);
			window.URL.revokeObjectURL(url);
			ElMessage.success('下载成功');
		}
	} catch (error) {
		ElMessage.error('下载失败');
	}
};

// 逻辑删除
const onDelete = (row: any) => {
	deleteRow(row, `此操作将删除文件记录，但不会删除物理文件，是否继续？`);
};

// 物理删除
const onPhysicalDelete = (row: any) => {
	physicalDeleteRow(row, `此操作将永久删除该文件及其物理存储，是否继续？`);
};

const deleteRow = async (row: any, message?: string) => {
	const confirmMessage = message || `此操作将删除该文件记录，是否继续?`;
	try {
		await ElMessageBox.confirm(confirmMessage, '提示', {
			confirmButtonText: '删除',
			cancelButtonText: '取消',
			type: 'warning',
		});

		const res = await useFileinfoApi().delete(row.id);
		if (res.code === 200 || res.success) {
			ElMessage.success('删除成功');
			tableRef.value?.refresh();
		} else {
			ElMessage.error(res.msg || '删除失败');
		}
	} catch (error) {}
};

const physicalDeleteRow = async (row: any, message?: string) => {
	const confirmMessage = message || `此操作将永久删除该文件，是否继续?`;
	try {
		await ElMessageBox.confirm(confirmMessage, '提示', {
			confirmButtonText: '删除',
			cancelButtonText: '取消',
			type: 'warning',
		});

		const res = await useFileinfoApi().deleteFilesPhysically([row.id]);
		if (res.code === 200 || res.success) {
			ElMessage.success('删除成功');
			tableRef.value?.refresh();
		} else {
			ElMessage.error(res.msg || '删除失败');
		}
	} catch (error) {}
};

const handleBatchDelete = async () => {
	const selection = tableRef.value?.getAllSelectedData();
	if (!selection || selection.length === 0) {
		ElMessage.warning('请选择要删除的文件');
		return;
	}

	try {
		await ElMessageBox.confirm(`此操作将永久删除选中的 ${selection.length} 个文件，是否继续？`, '提示', {
			confirmButtonText: '删除',
			cancelButtonText: '取消',
			type: 'warning',
		});

		const fileIds = selection.map((item: any) => item.id);
		const res = await useFileinfoApi().deleteFilesPhysically(fileIds);
		if (res.code === 200 || res.success) {
			ElMessage.success(`批量删除成功，共删除 ${selection.length} 个文件`);
			tableRef.value?.refresh();
		} else {
			ElMessage.error(res.msg || '批量删除失败');
		}
	} catch (error) {}
};

const initData = async (): Promise<void> => {
	const fileStoragetypeRes = await commonApi().getEnumValue('file_storagetype');
	state.storageTypeOptions = fileStoragetypeRes.data;
	const fileUploadstatusRes = await commonApi().getEnumValue('file_uploadstatus');
	state.uploadStatusOptions = fileUploadstatusRes.data;
	const fileTypeRes = await commonApi().getEnumValue('file_type');
	state.fileTypeOptions = fileTypeRes.data;
};

onMounted(() => {
	initData();
});
</script>

<style scoped>
.page-header {
	display: flex;
	flex-direction: column;
	gap: 16px;

	.search-form {
		display: flex;
		flex-wrap: wrap;
		align-items: center;
		gap: 12px;
	}
}

.operation-btn-group {
	display: flex;
	align-items: center;
	flex-wrap: wrap;
}

.el-dropdown-link {
	cursor: pointer;
}
</style>
