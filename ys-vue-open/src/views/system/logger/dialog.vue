<template>
	<YsDialog
		:title="state.dialog.title"
		v-model="state.dialog.isShowDialog"
		@close="state.dialog.isShowDialog = false"
		width="60%"
		:close-on-click-modal="false"
		:close-on-press-escape="false"
		:append-to-body="true"
	>
		<el-form ref="roleDialogFormRef" :model="state.ruleForm" label-width="100px" :inline="false" label-position="right" class="ys-log-dialog-form">
			<!-- 基础信息（所有日志类型都显示） -->
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="日志类型">
						<el-tag :type="getLogTypeTagType(state.ruleForm.logType)">
							{{ state.ruleForm.logType_Text }}
						</el-tag>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="日志级别">
						<el-tag :type="getLogLevelTagType(state.ruleForm.logLevel)">
							{{ state.ruleForm.logLevel_Text }}
						</el-tag>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row class="mb15">
				<el-col :span="12">
					<el-form-item label="操作人">
						<el-input v-model="state.ruleForm.userName" readonly />
					</el-form-item>
				</el-col>
			</el-row>

			<!-- 登录/登出日志 (0, 1) -->
			<template v-if="state.ruleForm.logType == 0 || state.ruleForm.logType == 1">
				<el-row class="mb15">
					<el-col :span="12">
						<el-form-item label="操作时间">
							<el-input v-model="state.ruleForm.logTime" readonly />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="登录名">
							<el-input v-model="state.ruleForm.loginName" readonly />
						</el-form-item>
					</el-col>
				</el-row>
				<el-row class="mb15">
					<el-col :span="12">
						<el-form-item label="请求IP">
							<el-input v-model="state.ruleForm.ip" readonly />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="IP地址">
							<el-input v-model="state.ruleForm.address" readonly />
						</el-form-item>
					</el-col>
				</el-row>
				<el-row class="mb15">
					<el-col :span="12">
						<el-form-item label="操作系统">
							<el-input v-model="state.ruleForm.win" readonly />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="版本号">
							<el-input v-model="state.ruleForm.version" readonly />
						</el-form-item>
					</el-col>
				</el-row>
				<el-row class="mb15">
					<el-col :span="24">
						<el-form-item label="浏览器">
							<el-input v-model="state.ruleForm.userAgent" readonly />
						</el-form-item>
					</el-col>
				</el-row>
				<el-row class="mb15">
					<el-col :span="24">
						<el-form-item label="操作描述">
							<el-input v-model="state.ruleForm.remark" readonly />
						</el-form-item>
					</el-col>
				</el-row>
			</template>

			<!-- 业务操作日志 (2) -->
			<template v-else-if="state.ruleForm.logType == 2">
				<el-row class="mb15">
					<el-col :span="12">
						<el-form-item label="操作时间">
							<el-input v-model="state.ruleForm.logTime" readonly />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="功能模块">
							<el-input v-model="state.ruleForm.modul" readonly />
						</el-form-item>
					</el-col>
				</el-row>
				<el-row class="mb15">
					<el-col :span="12">
						<el-form-item label="操作类型">
							<el-input v-model="state.ruleForm.type" readonly />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="操作描述">
							<el-input v-model="state.ruleForm.remark" readonly />
						</el-form-item>
					</el-col>
				</el-row>
				<el-row class="mb15">
					<el-col :span="24">
						<el-form-item label="请求URL">
							<el-input v-model="state.ruleForm.url" readonly />
						</el-form-item>
					</el-col>
				</el-row>
				<el-row class="mb15">
					<el-col :span="12">
						<el-form-item label="请求IP">
							<el-input v-model="state.ruleForm.ip" readonly />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="IP地址">
							<el-input v-model="state.ruleForm.address" readonly />
						</el-form-item>
					</el-col>
				</el-row>
				<el-row class="mb15">
					<el-col :span="24">
						<el-form-item label="操作方法">
							<el-input v-model="state.ruleForm.method" readonly />
						</el-form-item>
					</el-col>
				</el-row>
				<el-row class="mb15">
					<el-col :span="24">
						<el-form-item label="请求参数">
							<pre class="hljs-pre"><code v-safe-html="highlightCode(formattedReqParam, 'json')"></code></pre>
						</el-form-item>
					</el-col>
				</el-row>
				<el-row class="mb15">
					<el-col :span="24">
						<el-form-item label="返回参数">
							<pre class="hljs-pre"><code v-safe-html="highlightCode(formattedRespParam, 'json')"></code></pre>
						</el-form-item>
					</el-col>
				</el-row>
			</template>

			<!-- 异常日志 (3) -->
			<template v-else-if="state.ruleForm.logType == 3">
				<el-row class="mb15">
					<el-col :span="12">
						<el-form-item label="操作时间">
							<el-input v-model="state.ruleForm.logTime" readonly />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="功能模块">
							<el-input v-model="state.ruleForm.modul" readonly />
						</el-form-item>
					</el-col>
				</el-row>
				<el-row class="mb15">
					<el-col :span="24">
						<el-form-item label="请求URL">
							<el-input v-model="state.ruleForm.url" readonly />
						</el-form-item>
					</el-col>
				</el-row>
				<el-row class="mb15">
					<el-col :span="12">
						<el-form-item label="请求IP">
							<el-input v-model="state.ruleForm.ip" readonly />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="IP地址">
							<el-input v-model="state.ruleForm.address" readonly />
						</el-form-item>
					</el-col>
				</el-row>
				<el-row class="mb15">
					<el-col :span="24">
						<el-form-item label="操作方法">
							<el-input v-model="state.ruleForm.method" readonly />
						</el-form-item>
					</el-col>
				</el-row>
				<el-row class="mb15">
					<el-col :span="24">
						<el-form-item label="请求参数">
							<pre class="hljs-pre"><code v-safe-html="highlightCode(formattedReqParam, 'json')"></code></pre>
						</el-form-item>
					</el-col>
				</el-row>
				<el-row class="mb15">
					<el-col :span="24">
						<el-form-item label="返回参数">
							<pre class="hljs-pre"><code v-safe-html="highlightCode(formattedRespParam, 'json')"></code></pre>
						</el-form-item>
					</el-col>
				</el-row>
				<el-row class="mb15">
					<el-col :span="24">
						<el-form-item label="异常名称">
							<pre class="hljs-pre"><code v-safe-html="highlightCode(state.ruleForm.excName, 'java')"></code></pre>
						</el-form-item>
					</el-col>
				</el-row>
				<el-row class="mb15">
					<el-col :span="24">
						<el-form-item label="异常信息">
							<pre class="hljs-pre"><code v-safe-html="highlightCode(state.ruleForm.excMsg, 'java')"></code></pre>
						</el-form-item>
					</el-col>
				</el-row>
			</template>
		</el-form>
		<template #footer>
			<div class="dialog-footer">
				<el-button @click="closeDialog">关 闭</el-button>
			</div>
		</template>
	</YsDialog>
</template>

<script setup lang="ts">
import { reactive, ref, nextTick, computed } from 'vue';
import { objectCopyForm } from '@/utils/objectCopy';
import hljs from 'highlight.js/lib/core';
import jsonLang from 'highlight.js/lib/languages/json';
import javaLang from 'highlight.js/lib/languages/java';
hljs.registerLanguage('json', jsonLang);
hljs.registerLanguage('java', javaLang);

/**
 * 高亮代码
 * @param code 代码字符串
 * @param lang 语言
 * @returns 高亮后的HTML
 */
function highlightCode(code: string, lang: string): string {
	if (!code) return '';
	try {
		return hljs.highlight(code, { language: lang }).value;
	} catch {
		return code.replace(/</g, '&lt;').replace(/>/g, '&gt;');
	}
}

const roleDialogFormRef = ref();

// 表单初始数据
const initialForm = {
	logType: 0, //日志类型 0登录 1登出 2业务操作 3异常
	logType_Text: '', //日志类型文本
	logLevel: 0, //日志级别 0DEBUG 1INFO 2WARN 3ERROR 4FATAL
	logLevel_Text: '', //日志级别文本
	modul: '', // 模块
	type: '', // 日志类型
	remark: '', // 备注
	reqParam: '', // 请求参数
	respParam: '', // 响应参数
	loginName: '', // 登录名
	userName: '', // 用户名
	method: '',
	url: '',
	ip: '',
	address: '',
	win: '',
	userAgent: '',
	version: '',
	logTime: '',
	excName: '',
	excMsg: '',
};
const state = reactive({
	dialog: {
		title: '日志详情',
		isShowDialog: false,
	},
	ruleForm: {
		...initialForm,
	},
	loading: false,
	isInitializing: false, // 是否正在初始化数据
});

// 格式化JSON响应参数
const formattedRespParam = computed(() => {
	try {
		if (!state.ruleForm.respParam) return '';

		// 如果已经是格式化的JSON字符串，直接返回
		if (typeof state.ruleForm.respParam === 'string') {
			// 尝试解析JSON
			const parsed = JSON.parse(state.ruleForm.respParam);
			// 格式化JSON字符串
			return JSON.stringify(parsed, null, 2);
		} else if (typeof state.ruleForm.respParam === 'object') {
			// 如果是对象，直接格式化
			return JSON.stringify(state.ruleForm.respParam, null, 2);
		}
		return state.ruleForm.respParam;
	} catch (error) {
		// 如果不是有效的JSON，返回原始值
		return state.ruleForm.respParam;
	}
});

// 格式化JSON请求参数
const formattedReqParam = computed(() => {
	try {
		if (!state.ruleForm.reqParam) return '';

		// 如果已经是格式化的JSON字符串，直接返回
		if (typeof state.ruleForm.reqParam === 'string') {
			// 尝试解析JSON
			const parsed = JSON.parse(state.ruleForm.reqParam);
			// 格式化JSON字符串
			return JSON.stringify(parsed, null, 2);
		} else if (typeof state.ruleForm.reqParam === 'object') {
			// 如果是对象，直接格式化
			return JSON.stringify(state.ruleForm.reqParam, null, 2);
		}
		return state.ruleForm.reqParam;
	} catch (error) {
		// 如果不是有效的JSON，返回原始值
		return state.ruleForm.reqParam;
	}
});

// 获取日志类型标签样式
const getLogTypeTagType = (logType: number) => {
	switch (logType) {
		case 0: // 登录
			return 'success';
		case 1: // 登出
			return 'warning';
		case 2: // 业务操作
			return 'primary';
		case 3: // 异常
			return 'danger';
		default:
			return 'info';
	}
};

// 获取日志类型文本
const getLogTypeText = (logType: number) => {
	switch (logType) {
		case 0:
			return '登录';
		case 1:
			return '登出';
		case 2:
			return '业务操作';
		case 3:
			return '异常';
		default:
			return '未知';
	}
};

// 获取日志级别标签样式
const getLogLevelTagType = (logLevel: number) => {
	switch (logLevel) {
		case 0: // DEBUG
			return 'info';
		case 1: // INFO
			return 'success';
		case 2: // WARN
			return 'warning';
		case 3: // ERROR
			return 'danger';
		case 4: // FATAL
			return 'danger';
		default:
			return 'info';
	}
};

// 获取日志级别文本
const getLogLevelText = (logLevel: number) => {
	switch (logLevel) {
		case 0:
			return 'DEBUG';
		case 1:
			return 'INFO';
		case 2:
			return 'WARN';
		case 3:
			return 'ERROR';
		case 4:
			return 'FATAL';
		default:
			return '未知';
	}
};

// 打开弹窗
const openDialog = async (row?: any) => {
	// 重置表单数据
	state.ruleForm = { ...initialForm };

	state.dialog.isShowDialog = true;

	// 编辑/预览模式时加载数据
	if (row) {
		state.isInitializing = true;
		state.ruleForm = objectCopyForm(row, initialForm) as typeof initialForm;

		// 处理用户名显示
		if (row.userName && row.loginName) {
			state.ruleForm.userName = row.userName + '（' + row.loginName + '）';
		} else if (row.userName) {
			state.ruleForm.userName = row.userName;
		} else if (row.loginName) {
			state.ruleForm.userName = row.loginName;
		}

		// 设置日志类型文本
		state.ruleForm.logType_Text = getLogTypeText(row.logType);

		// 设置日志级别文本
		state.ruleForm.logLevel_Text = getLogLevelText(row.logLevel);

		await nextTick();
		state.isInitializing = false;
	}
};

const closeDialog = () => {
	state.dialog.isShowDialog = false;
};
// 暴露方法给父组件
defineExpose({
	openDialog,
});
</script>
<style scoped lang="scss">
pre {
	width: 100%;
	border: 1px solid var(--el-border-color-lighter);
	border-radius: 4px;
	background: var(--next-bg-card) !important;
	color: var(--next-text-body) !important;
}

:deep(code) {
	background: var(--next-bg-card) !important;
	color: var(--next-text-body) !important;
}

:deep(.hljs) {
	background: var(--next-bg-card) !important;
	color: var(--next-text-body) !important;
}
</style>
