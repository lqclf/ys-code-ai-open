import { useNoticeApi } from './index';
import {
    transformNoticeToMessage,
    batchTransformNotices,
    groupMessagesByType,
    groupMessagesByStatus,
    buildTabDataSource,
    sortMessagesByTime,
    BackendNoticeData,
} from '@/api/system/common/notice-common';
import type { MessageItem } from '@/types/notice';

/**
 * 通知公告服务层
 * @description 封装通知公告相关的业务逻辑和API调用
 * @author Eric
 * @date 2025-03-18
 */

/**
 * 通知公告服务类
 */
export class NoticeService {
    private noticeApi = useNoticeApi();

    /**
     * 获取用户通知列表
     * @param params 查询参数
     * @returns 消息列表
     */
    async getUserNoticeList(params?: any): Promise<MessageItem[]> {
        try {
            const response = await this.noticeApi.getUserNoticeList(params);
            if (response.code === 200 && response.data) {
                let list = [];
                if (Array.isArray(response.data)) {
                    list = response.data;
                } else if (response.data?.list && Array.isArray(response.data.list)) {
                    list = response.data.list;
                } else if (response.data?.rows && Array.isArray(response.data.rows)) {
                    list = response.data.rows;
                } else if (response.data?.records && Array.isArray(response.data.records)) {
                    list = response.data.records;
                }
                return batchTransformNotices(list as BackendNoticeData[]);
            }
            return [];
        } catch (error) {
            console.error('获取用户通知列表失败:', error);
            return [];
        }
    }

    /**
     * 获取未读通知数量
     * @returns 未读数量
     */
    async getUnreadCount(): Promise<number> {
        try {
            const response = await this.noticeApi.getUnreadCount();
            if (response.code === 200) {
                return response.data ?? 0;
            }
            return 0;
        } catch (error) {
            console.error('获取未读通知数量失败:', error);
            return 0;
        }
    }

    /**
     * 标记通知为已读
     * @param id 通知ID
     * @returns 是否成功
     */
    async markAsRead(id: string): Promise<boolean> {
        try {
            const response = await this.noticeApi.read(id);
            return response.code === 200;
        } catch (error) {
            console.error('标记已读失败:', error);
            return false;
        }
    }

    /**
     * 批量标记通知为已读
     * @param ids 通知ID列表
     * @returns 是否成功
     */
    async batchMarkAsRead(ids: string[]): Promise<boolean> {
        if (!ids || ids.length === 0) return false;
        
        try {
            const promises = ids.map((id) => this.markAsRead(id));
            const results = await Promise.all(promises);
            return results.every((result) => result);
        } catch (error) {
            console.error('批量标记已读失败:', error);
            return false;
        }
    }

    /**
     * 获取通知详情
     * @param id 通知ID
     * @returns 通知详情
     */
    async getNoticeDetail(id: string): Promise<MessageItem | null> {
        try {
            const response = await this.noticeApi.getById(id);
            if (response.code === 200 && response.data) {
                return transformNoticeToMessage(response.data as BackendNoticeData);
            }
            return null;
        } catch (error) {
            console.error('获取通知详情失败:', error);
            return null;
        }
    }

    /**
     * 按类型分组消息
     * @param messages 消息列表
     * @returns 按类型分组的消息
     */
    groupMessagesByType(messages: MessageItem[]): Record<string, MessageItem[]> {
        return groupMessagesByType(messages);
    }

    /**
     * 按状态分组消息
     * @param messages 消息列表
     * @returns 按状态分组的消息（全部、未读、已读）
     */
    groupMessagesByStatus(messages: MessageItem[]): {
        all: Record<string, MessageItem[]>;
        unread: Record<string, MessageItem[]>;
        read: Record<string, MessageItem[]>;
    } {
        return groupMessagesByStatus(messages);
    }

    /**
     * 构建通知中心数据源
     * @param messages 消息列表
     * @returns 通知中心数据源
     */
    buildTabDataSource(messages: MessageItem[]): {
        all: Record<string, MessageItem[]>;
        unread: Record<string, MessageItem[]>;
        read: Record<string, MessageItem[]>;
    } {
        return buildTabDataSource(messages);
    }

    /**
     * 加载通知数据并构建完整的数据配置
     * @param params 查询参数
     * @returns 包含消息列表和标签数据源的完整数据
     */
    async loadNoticeData(params?: any): Promise<{
        messageList: Record<string, MessageItem[]>;
        tabDataSource: {
            all: Record<string, MessageItem[]>;
            unread: Record<string, MessageItem[]>;
            read: Record<string, MessageItem[]>;
        };
        unreadCount: number;
    }> {
        const [notices, noticeUnreadCount] = await Promise.all([
            this.getUserNoticeList(params),
            this.getUnreadCount(),
        ]);

        const sortedNotices = sortMessagesByTime(notices);

        return {
            messageList: this.groupMessagesByType(sortedNotices),
            tabDataSource: this.buildTabDataSource(sortedNotices),
            unreadCount: noticeUnreadCount,
        };
    }
}

/**
 * 创建通知服务实例
 */
export function createNoticeService(): NoticeService {
    return new NoticeService();
}

/**
 * 默认导出通知服务实例
 */
export default createNoticeService();
