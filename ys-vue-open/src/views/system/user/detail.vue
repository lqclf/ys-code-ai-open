<template>
	<YsDialog
		:title="state.dialog.title"
		v-model="state.dialog.isShowDialog"
		@close="closeDialog"
		width="60%"
		:close-on-click-modal="false"
		:close-on-press-escape="false"
		:append-to-body="true"
	>
		<div class="user-detail-container" v-loading="state.loading">
			<div class="detail-header">
				<div class="user-avatar">
					<el-avatar :size="64" :src="state.userData.avatar || ''">
						<el-icon :size="32"><User /></el-icon>
					</el-avatar>
				</div>
				<div class="user-basic-info">
					<div class="user-info-row">
						<h2 class="user-name">{{ state.userData.userName || '-' }}</h2>
						<div class="user-tags">
							<el-tag v-if="state.userData.userType === 0" type="info" size="small">游客</el-tag>
							<el-tag v-else-if="state.userData.userType === 1" size="small">普通用户</el-tag>
							<el-tag v-else-if="state.userData.userType === 2" type="warning" size="small">会员</el-tag>
							<el-tag v-else-if="state.userData.userType === 3" type="danger" size="small">管理员</el-tag>
							<el-tag :type="state.userData.status === 1 ? 'success' : 'danger'" size="small">
								{{ state.userData.status === 1 ? '正常' : '禁用' }}
							</el-tag>
						</div>
					</div>
					<div class="user-code">
						<span class="label">登录名：</span>
						<span class="value">{{ state.userData.loginName || '-' }}</span>
					</div>
				</div>
			</div>

			<div class="detail-content">
				<div class="info-section">
					<div class="section-title">
						<el-icon><InfoFilled /></el-icon>
						<span>基本信息</span>
					</div>
					<el-row :gutter="20" class="info-row">
						<el-col :span="12">
							<div class="info-item">
								<span class="info-label">登录名</span>
								<span class="info-value">{{ state.userData.loginName || '-' }}</span>
							</div>
						</el-col>
						<el-col :span="12">
							<div class="info-item">
								<span class="info-label">真实姓名</span>
								<span class="info-value">{{ state.userData.userName || '-' }}</span>
							</div>
						</el-col>
					</el-row>
					<el-row :gutter="20" class="info-row">
						<el-col :span="12">
							<div class="info-item">
								<span class="info-label">用户编号</span>
								<span class="info-value">{{ state.userData.code || '-' }}</span>
							</div>
						</el-col>
						<el-col :span="12">
							<div class="info-item">
								<span class="info-label">用户类型</span>
								<span class="info-value">
									<el-tag v-if="state.userData.userType === 0" type="info" size="small">游客</el-tag>
									<el-tag v-else-if="state.userData.userType === 1" size="small">普通用户</el-tag>
									<el-tag v-else-if="state.userData.userType === 2" type="warning" size="small">会员</el-tag>
									<el-tag v-else-if="state.userData.userType === 3" type="danger" size="small">管理员</el-tag>
									<el-tag v-else type="info" size="small">未知</el-tag>
								</span>
							</div>
						</el-col>
					</el-row>
					<el-row :gutter="20" class="info-row">
						<el-col :span="12">
							<div class="info-item">
								<span class="info-label">手机号码</span>
								<span class="info-value">{{ state.userData.phone || '-' }}</span>
							</div>
						</el-col>
						<el-col :span="12">
							<div class="info-item">
								<span class="info-label">邮箱</span>
								<span class="info-value">{{ state.userData.email || '-' }}</span>
							</div>
						</el-col>
					</el-row>
					<el-row :gutter="20" class="info-row">
						<el-col :span="24">
							<div class="info-item">
								<span class="info-label">所属部门</span>
								<span class="info-value">{{ state.userData.departName || '-' }}</span>
							</div>
						</el-col>
					</el-row>
				</div>

				<div class="info-section mt-24">
					<div class="section-title">
						<el-icon><UserFilled /></el-icon>
						<span>角色权限</span>
					</div>
					<div class="role-list">
						<el-empty v-if="!state.userData.roleList || state.userData.roleList.length === 0" description="暂无角色" :image-size="80" />
						<div v-else class="role-cards">
							<el-tag type="primary" v-for="role in state.userData.roleList" :key="role.id" style="margin-right: 6px">{{ role.name }}</el-tag>
						</div>
					</div>
				</div>

				<div class="info-section mt-24">
					<div class="section-title">
						<el-icon><Setting /></el-icon>
						<span>账户状态</span>
					</div>
					<el-row :gutter="20" class="info-row">
						<el-col :span="12">
							<div class="status-item">
								<span class="status-label">账户状态</span>
								<el-tag :type="state.userData.status === 1 ? 'success' : 'danger'" size="large">
									{{ state.userData.status === 1 ? '正常' : '禁用' }}
								</el-tag>
							</div>
						</el-col>
						<el-col :span="12">
							<div class="status-item">
								<span class="status-label">锁定状态</span>
								<el-tag :type="state.userData.isLock === 1 ? 'success' : 'danger'" size="large">
									{{ state.userData.isLock === 1 ? '正常' : '已锁定' }}
								</el-tag>
							</div>
						</el-col>
					</el-row>
					<div class="section-title mt-20">
						<el-icon><Document /></el-icon>
						<span>备注信息</span>
					</div>
					<div class="remark-box">
						<p>{{ state.userData.remark || '暂无备注' }}</p>
					</div>
				</div>
			</div>
		</div>

		<template #footer>
			<el-button @click="closeDialog" size="default">关 闭</el-button>
			<el-button type="primary" @click="handleEdit" v-if="state.userData.id">
				<el-icon class="mr-4"><Edit /></el-icon>
				编辑用户
			</el-button>
		</template>
	</YsDialog>
</template>

<script setup lang="ts" name="systemUserDetail">
import { reactive } from 'vue';
import { User, InfoFilled, UserFilled, Setting, Document, Edit } from '@element-plus/icons-vue';

const state = reactive({
	dialog: {
		isShowDialog: false,
		title: '用户详情',
	},
	loading: false,
	userData: {} as any,
});

const emit = defineEmits(['edit']);

const openDialog = async (row: any) => {
	state.loading = true;
	state.dialog.isShowDialog = true;

	try {
		state.userData = { ...row };
	} catch (error) {
		console.error('加载用户详情失败:', error);
	} finally {
		state.loading = false;
	}
};

const closeDialog = () => {
	state.dialog.isShowDialog = false;
	state.userData = {};
};

const handleEdit = () => {
	closeDialog();
	emit('edit', state.userData);
};

defineExpose({
	openDialog,
});
</script>

<style scoped lang="scss">
.user-detail-container {
	.detail-header {
		display: flex;
		align-items: center;
		padding: 16px 20px;
		background: #f5f7fa;
		border-radius: 8px;
		margin-bottom: 20px;

		.user-avatar {
			margin-right: 16px;
			flex-shrink: 0;

			:deep(.el-avatar) {
				background: #e0e0e0;
				color: #909399;
			}
		}

		.user-basic-info {
			flex: 1;
			display: flex;
			flex-direction: column;
			gap: 8px;

			.user-info-row {
				display: flex;
				align-items: center;
				gap: 12px;
				flex-wrap: wrap;
			}

			.user-name {
				margin: 0;
				font-size: 18px;
				font-weight: 600;
				color: #303133;
				line-height: 1.2;
			}

			.user-tags {
				display: flex;
				align-items: center;
				gap: 6px;
			}

			.user-code {
				font-size: 13px;
				color: #606266;
				line-height: 1.4;

				.label {
					margin-right: 6px;
					color: #909399;
				}

				.value {
					font-weight: 500;
					color: #303133;
				}
			}
		}
	}

	.detail-content {
		.info-section {
			.section-title {
				display: flex;
				align-items: center;
				font-size: 15px;
				font-weight: 600;
				color: #303133;
				margin-bottom: 16px;
				padding-bottom: 10px;
				border-bottom: 2px solid #e4e7ed;

				.el-icon {
					margin-right: 8px;
					color: #409eff;
				}
			}

			.info-row {
				margin-bottom: 12px;

				.info-item {
					display: flex;
					align-items: center;
					padding: 12px 16px;
					background: #fafafa;
					border-radius: 6px;
					transition: all 0.3s;
					border: 1px solid #ebeef5;

					&:hover {
						background: #f0f7ff;
						border-color: #c6e2ff;
					}

					.info-label {
						min-width: 80px;
						color: #606266;
						font-weight: 500;
						font-size: 14px;
					}

					.info-value {
						flex: 1;
						color: #303133;
						font-weight: 600;
						font-size: 14px;
					}
				}
			}

			.status-section {
				.status-item {
					display: flex;
					align-items: center;
					justify-content: space-between;
					padding: 16px;
					background: #fafafa;
					border-radius: 6px;
					border: 1px solid #ebeef5;

					.status-label {
						font-size: 14px;
						color: #606266;
						font-weight: 500;
					}
				}

				.remark-box {
					padding: 16px;
					background: #fafafa;
					border-radius: 6px;
					min-height: 80px;
					border: 1px solid #ebeef5;

					p {
						margin: 0;
						color: #606266;
						line-height: 1.6;
						font-size: 14px;
					}
				}
			}

			.mt-20 {
				margin-top: 20px;
			}
		}

		.mt-24 {
			margin-top: 24px;
		}
	}
}

.mr-4 {
	margin-right: 4px;
}
</style>
