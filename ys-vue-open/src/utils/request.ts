import axios, { AxiosInstance } from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Session } from '@/utils/storage';
import qs from 'qs';

// 配置新建一个 axios 实例
const service: AxiosInstance = axios.create({
	baseURL: '',
	timeout: 50000,
	headers: { 'Content-Type': 'application/json' },
	paramsSerializer: {
		serialize(params) {
			return qs.stringify(params, { allowDots: true });
		},
	},
});

// 添加请求拦截器
service.interceptors.request.use(
	(config) => {
		// 在发送请求之前做些什么 token
		if (Session.get('token')) {
			config.headers!['Authorization'] = `${Session.get('token')}`;
		}
		return config;
	},
	(error) => {
		// 对请求错误做些什么
		return Promise.reject(error);
	}
);

/**
 * 处理登录过期，跳转到登录页
 */
const handleLoginExpired = (message: string = '登录已过期，请重新登录') => {
	Session.clear(); // 清除浏览器全部临时缓存
	ElMessage.error(message);
	setTimeout(() => {
		window.location.href = '/'; // 去登录页
	}, 1500);
};

// 添加响应拦截器
service.interceptors.response.use(
	(response) => {
		// 对响应数据做点什么
		const res = response.data;
		if (res.code && res.code != 200) {
			// `token` 过期或者账号已在别处登录
			if (res.code === 401 || res.code === 4001) {
				handleLoginExpired(res.msg || '你已被登出，请重新登录');
			}
			return res;
		} else {
			return res;
		}
	},
	(error) => {
		// 对响应错误做点什么
		if (error.message.indexOf('timeout') != -1) {
			ElMessage.error('网络超时');
		} else if (error.message == 'Network Error') {
			ElMessage.error('网络连接错误');
		} else if (error.response) {
			// 处理 Sa-Token token 无效的情况
			const { status, data } = error.response;
			const errorMsg = data?.msg || data?.message || '';

			// 检查是否是 token 相关错误（401 状态码或包含特定错误信息）
			if (status === 401 ||
				errorMsg.includes('token') ||
				errorMsg.includes('Token') ||
				errorMsg.includes('无效') ||
				errorMsg.includes('未登录') ||
				errorMsg.includes('NotLoginException') ||
				errorMsg.includes('登录') ||
				errorMsg.includes('登出')) {
				handleLoginExpired(errorMsg || '登录已过期，请重新登录');
			} else {
				if (data?.msg || data?.message) {
					ElMessage.error(data.msg || data.message);
				} else {
					ElMessage.error(`请求错误: ${status}`);
				}
			}
		} else {
			ElMessage.error('接口路径找不到');
		}
		return Promise.reject(error);
	}
);

// 导出 axios 实例
export default service;
