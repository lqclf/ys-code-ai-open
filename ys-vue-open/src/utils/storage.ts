import Cookies from 'js-cookie';

/**
 * 动态检测存储是否可用的辅助函数（每次使用时动态检测）
 * @param type 存储类型：'localStorage' | 'sessionStorage'
 * @returns boolean 是否可用
 */
const tryStorage = (type: 'localStorage' | 'sessionStorage'): boolean => {
	try {
		const storage = window[type];
		const testKey = '__storage_test__';
		storage.setItem(testKey, testKey);
		storage.removeItem(testKey);
		return true;
	} catch (e) {
		return false;
	}
};

/**
 * 备用存储方案：当 sessionStorage 不可用时使用 Cookies
 * @param key 存储键名
 * @param val 存储值
 */
const fallbackSet = (key: string, val: any) => {
	try {
		Cookies.set(key, typeof val === 'string' ? val : JSON.stringify(val), { path: '/', expires: 1 });
	} catch (e) {
		console.warn('Storage fallback to Cookies failed:', e);
	}
};

/**
 * 备用获取方案：当 sessionStorage 不可用时从 Cookies 获取
 * @param key 存储键名
 * @returns string | undefined 存储的值
 */
const fallbackGet = (key: string): string | undefined => {
	try {
		return Cookies.get(key);
	} catch (e) {
		console.warn('Storage fallback get from Cookies failed:', e);
		return undefined;
	}
};

/**
 * window.localStorage 浏览器永久缓存
 * @method set 设置永久缓存
 * @method get 获取永久缓存
 * @method remove 移除永久缓存
 * @method clear 移除全部永久缓存
 */
export const Local = {
	// 查看 v2.4.3版本更新日志
	setKey(key: string) {
		// @ts-ignore
		return `${__NEXT_NAME__}:${key}`;
	},
	// 设置永久缓存
	set<T>(key: string, val: T) {
		if (!tryStorage('localStorage')) return;
		try {
			window.localStorage.setItem(Local.setKey(key), JSON.stringify(val));
		} catch (e) {
			console.warn('LocalStorage set failed:', e);
		}
	},
	// 获取永久缓存
	get(key: string) {
		if (!tryStorage('localStorage')) return null;
		try {
			let json = <string>window.localStorage.getItem(Local.setKey(key));
			return json ? JSON.parse(json) : null;
		} catch (e) {
			console.warn('LocalStorage get failed:', e);
			return null;
		}
	},
	// 移除永久缓存
	remove(key: string) {
		if (!tryStorage('localStorage')) return;
		try {
			window.localStorage.removeItem(Local.setKey(key));
		} catch (e) {
			console.warn('LocalStorage remove failed:', e);
		}
	},
	// 移除全部永久缓存
	clear() {
		if (!tryStorage('localStorage')) return;
		try {
			window.localStorage.clear();
		} catch (e) {
			console.warn('LocalStorage clear failed:', e);
		}
	},
};

/**
 * window.sessionStorage 浏览器临时缓存
 * @method set 设置临时缓存
 * @method get 获取临时缓存
 * @method remove 移除临时缓存
 * @method clear 移除全部临时缓存
 */
export const Session = {
	// token 存储键名（用于 localStorage 备用）
	tokenKey: 'token',

	// 设置临时缓存
	set<T>(key: string, val: T) {
		if (key === 'token') {
			// token 同时存储到 localStorage 和 Cookies，确保在 Chrome 中也能正常工作
			// localStorage 不受 SameSite 限制，更可靠
			try {
				window.localStorage.setItem(Session.tokenKey, val as string);
			} catch (e) {
				console.warn('Token localStorage set failed:', e);
			}
			// 同时尝试存储到 Cookies（使用简化配置，避免 sameSite/secure 冲突）
			try {
				Cookies.set(key, val as string, { path: '/', expires: 1 });
			} catch (e) {
				console.warn('Token Cookies set failed:', e);
			}
			return;
		}

		// 优先使用 sessionStorage
		if (tryStorage('sessionStorage')) {
			try {
				window.sessionStorage.setItem(Local.setKey(key), JSON.stringify(val));
			} catch (e) {
				console.warn('SessionStorage set failed, falling back to Cookies:', e);
				fallbackSet(Local.setKey(key), val);
			}
		} else {
			fallbackSet(Local.setKey(key), val);
		}
	},
	// 获取临时缓存
	get(key: string) {
		if (key === 'token') {
			// 优先从 localStorage 获取 token（Chrome 中更可靠）
			try {
				const token = window.localStorage.getItem(Session.tokenKey);
				if (token && token !== 'undefined' && token !== 'null') {
					return token;
				}
			} catch (e) {
				console.warn('Token localStorage get failed:', e);
			}
			// 如果 localStorage 没有，尝试从 Cookies 获取
			try {
				return Cookies.get(key);
			} catch (e) {
				console.warn('Token Cookies get failed:', e);
				return undefined;
			}
		}

		// 优先从 sessionStorage 获取
		if (tryStorage('sessionStorage')) {
			try {
				let json = <string>window.sessionStorage.getItem(Local.setKey(key));
				return json ? JSON.parse(json) : null;
			} catch (e) {
				console.warn('SessionStorage get failed, falling back to Cookies:', e);
				const fallbackValue = fallbackGet(Local.setKey(key));
				return fallbackValue ? JSON.parse(fallbackValue) : null;
			}
		} else {
			const fallbackValue = fallbackGet(Local.setKey(key));
			return fallbackValue ? JSON.parse(fallbackValue) : null;
		}
	},
	// 移除临时缓存
	remove(key: string) {
		if (key === 'token') {
			// 同时清除 localStorage 和 Cookies 中的 token
			try {
				window.localStorage.removeItem(Session.tokenKey);
			} catch (e) {
				console.warn('Token localStorage remove failed:', e);
			}
			try {
				Cookies.remove(key, { path: '/' });
			} catch (e) {
				console.warn('Token Cookies remove failed:', e);
			}
			return;
		}

		if (tryStorage('sessionStorage')) {
			try {
				window.sessionStorage.removeItem(Local.setKey(key));
			} catch (e) {
				console.warn('SessionStorage remove failed:', e);
			}
		}
	},
	// 移除全部临时缓存
	clear() {
		// 清除 token
		try {
			window.localStorage.removeItem(Session.tokenKey);
		} catch (e) {
			console.warn('Token localStorage clear failed:', e);
		}
		try {
			Cookies.remove('token', { path: '/' });
		} catch (e) {
			console.warn('Token Cookies clear failed:', e);
		}

		// 清除其他 sessionStorage 数据
		if (tryStorage('sessionStorage')) {
			try {
				window.sessionStorage.clear();
			} catch (e) {
				console.warn('SessionStorage clear failed:', e);
			}
		}
	},
};
