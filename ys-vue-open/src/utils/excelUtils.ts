/**
 * Excel工具函数
 * @module utils/excelUtils
 * @author ERIC
 * @since 2026-02-12
 */

/**
 * 读取Excel文件
 * @param file 文件对象
 * @param _options 选项
 */
export const readExcel = (file: File, _options?: { sheetIndex?: number; headerRowIndex?: number }): Promise<any> => {
	return new Promise((resolve, reject) => {
		const reader = new FileReader();
		reader.onload = (e) => {
			try {
				const data = new Uint8Array(e.target?.result as ArrayBuffer);
				resolve(data);
			} catch (error) {
				reject(error);
			}
		};
		reader.onerror = (error) => reject(error);
		reader.readAsArrayBuffer(file);
	});
};

/**
 * 下载文件
 * @param blob Blob对象
 * @param filename 文件名
 */
export const downloadFile = (blob: Blob, filename: string): void => {
	const link = document.createElement('a');
	link.href = URL.createObjectURL(blob);
	link.download = filename;
	document.body.appendChild(link);
	link.click();
	document.body.removeChild(link);
	URL.revokeObjectURL(link.href);
};

/**
 * 格式化文件大小
 * @param size 文件大小（字节）
 */
export const formatFileSize = (size: number): string => {
	if (size < 1024) {
		return size + ' B';
	} else if (size < 1024 * 1024) {
		return (size / 1024).toFixed(2) + ' KB';
	} else if (size < 1024 * 1024 * 1024) {
		return (size / (1024 * 1024)).toFixed(2) + ' MB';
	} else {
		return (size / (1024 * 1024 * 1024)).toFixed(2) + ' GB';
	}
};

/**
 * 获取文件扩展名
 * @param filename 文件名
 */
export const getFileExtension = (filename: string): string => {
	return filename.slice(((filename.lastIndexOf('.') - 1) >>> 0) + 2).toLowerCase();
};

/**
 * 检查文件类型是否允许
 * @param filename 文件名
 * @param allowedTypes 允许的类型列表
 */
export const isAllowedFileType = (filename: string, allowedTypes: string[] = ['xlsx', 'xls', 'csv']): boolean => {
	const ext = getFileExtension(filename);
	return allowedTypes.includes(ext);
};

/**
 * 任务状态映射
 */
export const taskStatusMap: Record<string, { label: string; type: 'success' | 'warning' | 'danger' | 'info' }> = {
	PENDING: { label: '待执行', type: 'info' },
	RUNNING: { label: '执行中', type: 'warning' },
	SUCCESS: { label: '成功', type: 'success' },
	PARTIAL_SUCCESS: { label: '部分成功', type: 'warning' },
	FAIL: { label: '失败', type: 'danger' },
	CANCELED: { label: '已取消', type: 'info' },
	COMPLETED: { label: '已完成', type: 'success' },
	FAILED: { label: '失败', type: 'danger' },
	CANCELLED: { label: '已取消', type: 'info' },
	PROCESSING: { label: '处理中', type: 'warning' }
};

/**
 * 获取任务状态文本
 * @param status 状态码
 */
export const getTaskStatusLabel = (status: string): string => {
	return taskStatusMap[status]?.label || status;
};

/**
 * 获取任务状态类型
 * @param status 状态码
 */
export const getTaskStatusType = (status: string): 'success' | 'warning' | 'danger' | 'info' => {
	return taskStatusMap[status]?.type || 'info';
};

/**
 * 生成任务ID
 * @param prefix 前缀
 */
export const generateTaskId = (prefix: string): string => {
	return prefix + Date.now() + Math.floor(Math.random() * 10000);
};

/**
 * 计算字符串相似度（编辑距离）
 * @param s1 字符串1
 * @param s2 字符串2
 */
export const calculateSimilarity = (s1: string, s2: string): number => {
	const len = Math.max(s1.length, s2.length);
	if (len === 0) return 1;

	const distance = levenshteinDistance(s1, s2);
	return 1 - distance / len;
};

/**
 * 计算编辑距离
 * @param s1 字符串1
 * @param s2 字符串2
 */
const levenshteinDistance = (s1: string, s2: string): number => {
	const matrix: number[][] = [];
	for (let i = 0; i <= s1.length; i++) {
		matrix[i] = [i];
	}
	for (let j = 0; j <= s2.length; j++) {
		matrix[0]![j] = j;
	}

	for (let i = 1; i <= s1.length; i++) {
		for (let j = 1; j <= s2.length; j++) {
			const cost = s1[i - 1] === s2[j - 1] ? 0 : 1;
			const deletion = (matrix[i - 1]?.[j] ?? 0) + 1;
			const insertion = (matrix[i]?.[j - 1] ?? 0) + 1;
			const substitution = (matrix[i - 1]?.[j - 1] ?? 0) + cost;
			matrix[i]![j] = Math.min(deletion, insertion, substitution);
		}
	}

	return matrix[s1.length]?.[s2.length] ?? 0;
};

/**
 * 获取常见别名映射
 */
export const getCommonAliases = (): Record<string, string[]> => {
	return {
		name: ['姓名', '用户名', '名字'],
		username: ['用户名', '账号', '用户'],
		phone: ['手机号', '电话', '手机', '联系方式'],
		email: ['邮箱', '电子邮件', 'e-mail'],
		address: ['地址', '住址'],
		idCard: ['身份证', '身份证号', '身份证号码'],
		deptName: ['部门', '部门名称', 'department'],
		status: ['状态', '启用状态'],
		createTime: ['创建时间', '创建日期'],
		updateTime: ['更新时间', '更新日期'],
		remark: ['备注', '说明', '注释']
	};
};
