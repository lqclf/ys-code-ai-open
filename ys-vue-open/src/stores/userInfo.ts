import { defineStore } from 'pinia';
import Cookies from 'js-cookie';
import { Session } from '@/utils/storage';
import { useUserApi } from '@/api/system/user/index';

/**
 * 用户信息
 * @methods setUserInfos 设置用户信息
 */
export const useUserInfo = defineStore('userInfo', {
	state: (): UserInfosState => ({
		userInfos: {
			userName: '',
			photo: '',
			time: 0,
			roles: [],
			authBtnList: [],
		},
	}),
	actions: {
		async setUserInfos() {
			// 存储用户信息到浏览器缓存
			if (Session.get('userInfo')) {
				this.userInfos = Session.get('userInfo');
			} else {
				const userInfos = <UserInfos>await this.getApiUserInfo();
				this.userInfos = userInfos;
			}
		},
		// 调用后端接口获取用户信息
		async getApiUserInfo() {
			const userInfoApi = useUserApi();
			try {
				const response = await userInfoApi.getUserInfo();
				// 假设后端返回的数据结构与模拟数据一致
				const userInfos = {
					userName: response.data.userName,
					photo: response.data.photo,
					time: new Date().getTime(),
					roles: response.data.roles || [],
					authBtnList: response.data.permissions || [],
				};
				Session.set('userInfo', userInfos);
				return userInfos;
			} catch (error) {
				// 如果接口调用失败，使用默认的模拟数据
				console.error('获取用户信息失败，使用默认数据:', error);
				const userName = Cookies.get('userName');
				// 模拟数据
				let defaultRoles: Array<string> = [];
				let defaultAuthBtnList: Array<string> = [];
				// admin 页面权限标识，对应路由 meta.roles，用于控制路由的显示/隐藏
				let adminRoles: Array<string> = ['admin'];
				// admin 按钮权限标识
				let adminAuthBtnList: Array<string> = ['btn.add', 'btn.del', 'btn.edit', 'btn.link'];
				// test 页面权限标识，对应路由 meta.roles，用于控制路由的显示/隐藏
				let testRoles: Array<string> = ['common'];
				// test 按钮权限标识
				let testAuthBtnList: Array<string> = ['btn.add', 'btn.link'];
				// 不同用户模拟不同的用户权限
				if (userName === 'admin') {
					defaultRoles = adminRoles;
					defaultAuthBtnList = adminAuthBtnList;
				} else {
					defaultRoles = testRoles;
					defaultAuthBtnList = testAuthBtnList;
				}
				// 用户信息模拟数据
				const userInfos = {
					userName: userName,
					photo:
						userName === 'admin'
							? 'https://img2.baidu.com/it/u=1978192862,2048448374&fm=253&fmt=auto&app=138&f=JPEG?w=504&h=500'
							: 'https://img2.baidu.com/it/u=2370931438,70387529&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500',
					time: new Date().getTime(),
					roles: defaultRoles,
					authBtnList: defaultAuthBtnList,
				};
				Session.set('userInfo', userInfos);
				return userInfos;
			}
		},
	},
});
