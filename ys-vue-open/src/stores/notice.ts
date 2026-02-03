import { defineStore } from 'pinia';
import { useNoticeApi } from '@/api/system/notice';

export interface NoticeItem {
	id: string;
	title: string;
	content: string;
	type: number;
	priority: number;
	status: number;
	createTime: string;
	createBy: string;
	readFlag: boolean;
}

export interface NoticeState {
	noticeList: NoticeItem[];
	unreadCount: number;
	loading: boolean;
	activeType: string;
}

export const useNoticeStore = defineStore('notice', {
	state: (): NoticeState => ({
		noticeList: [],
		unreadCount: 0,
		loading: false,
		activeType: 'all',
	}),
	getters: {
		getNoticeList: (state) => state.noticeList,
		getUnreadCount: (state) => state.unreadCount,
		getLoading: (state) => state.loading,
		getActiveType: (state) => state.activeType,
		getFilteredList: (state) => {
			if (state.activeType === 'all') {
				return state.noticeList;
			} else if (state.activeType === 'unread') {
				return state.noticeList.filter((item) => !item.readFlag);
			} else if (state.activeType === 'read') {
				return state.noticeList.filter((item) => item.readFlag);
			}
			return state.noticeList;
		},
	},
	actions: {
		async loadNotices() {
			this.loading = true;
			try {
				const api = useNoticeApi();
				const res = await api.getUserNoticeList({ pageNo: 1, pageSize: 10 });
				console.log('getUserNoticeList API返回数据:', res);
				if (res.code === 200 || res.success) {
					let list = [];
					if (Array.isArray(res.data)) {
						list = res.data;
					} else if (res.data?.list && Array.isArray(res.data.list)) {
						list = res.data.list;
					} else if (res.data?.rows && Array.isArray(res.data.rows)) {
						list = res.data.rows;
					} else if (res.data?.records && Array.isArray(res.data.records)) {
						list = res.data.records;
					}
					console.log('解析后的通知列表:', list);
					this.noticeList = list.map((item: any) => ({
						...item,
						readFlag: item.readFlag === true || item.readFlag === 1,
					}));
					console.log('处理后的noticeList:', this.noticeList);
					await this.loadUnreadCount();
				}
			} catch (error) {
				console.error('加载通知列表失败:', error);
			} finally {
				this.loading = false;
			}
		},
		async loadUnreadCount() {
			try {
				const api = useNoticeApi();
				const res = await api.getUnreadCount();
				if (res.code === 200 || res.success) {
					this.unreadCount = res.data || 0;
				}
			} catch (error) {
				console.error('获取未读通知数量失败:', error);
			}
		},
		async markAsRead(id: string) {
			try {
				const api = useNoticeApi();
				const res = await api.read(id);
				if (res.code === 200 || res.success) {
					const notice = this.noticeList.find((item) => item.id === id);
					if (notice && !notice.readFlag) {
						notice.readFlag = true;
						this.unreadCount = Math.max(0, this.unreadCount - 1);
					}
				}
			} catch (error) {
				console.error('标记已读失败:', error);
			}
		},
		async markAllAsRead() {
			try {
				const api = useNoticeApi();
				const unreadNotices = this.noticeList.filter((item) => !item.readFlag);
				if (unreadNotices.length === 0) return;
				
				const ids = unreadNotices.map((item) => item.id);
				const res = await api.batchDelete(ids);
				if (res.code === 200 || res.success) {
					this.noticeList.forEach((item) => {
						item.readFlag = true;
					});
					this.unreadCount = 0;
				}
			} catch (error) {
				console.error('批量标记已读失败:', error);
			}
		},
		setActiveType(type: string) {
			this.activeType = type;
		},
	},
});
