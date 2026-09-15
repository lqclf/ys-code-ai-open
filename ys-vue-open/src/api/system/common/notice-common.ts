import type { MessageItem, MessageSourceConfig } from '@/types/notice';

/**
 * 公共消息处理模块
 * @description 提供消息通知的通用处理函数
 * @author: Eric
 * @date: 2025-03-18
 */

/**
 * 后端通知类型枚举
 */
export enum BackendNoticeType {
    NOTICE = 0,
    SYSTEM = 1
}

/**
 * 前端消息类型枚举
 */
export enum FrontendMessageType {
    NOTICE = 'notice',
    SYSTEM = 'system',
    TODO = 'todo',
    ALERT = 'alert',
    MESSAGE = 'message'
}

/**
 * 后端通知类型映射到前端消息类型
 * @param backendType 后端通知类型 (0通知公告, 1系统消息)
 * @returns 前端消息类型
 */
export function mapBackendToFrontendType(backendType: number): string {
    const typeMap: Record<number, string> = {
        [BackendNoticeType.NOTICE]: FrontendMessageType.NOTICE,
        [BackendNoticeType.SYSTEM]: FrontendMessageType.SYSTEM,
    };
    return typeMap[backendType] || FrontendMessageType.SYSTEM;
}

/**
 * 前端消息类型映射到后端通知类型
 * @param frontendType 前端消息类型
 * @returns 后端通知类型
 */
export function mapFrontendToBackendType(frontendType: string): number {
    const typeMap: Record<string, number> = {
        [FrontendMessageType.NOTICE]: BackendNoticeType.NOTICE,
        [FrontendMessageType.SYSTEM]: BackendNoticeType.SYSTEM,
    };
    return typeMap[frontendType] ?? BackendNoticeType.SYSTEM;
}

/**
 * 后端通知数据接口
 */
export interface BackendNoticeData {
    id: string;
    noticeId: string;
    type?: number;
    title: string;
    content: string;
    sendUser?: string;
    sendTime?: string;
    createTime?: string;
    status?: number;
    readStatus?: number;
    readTime?: string;
    priority?: number;
    bgTime?: string;
    endTime?: string;
}

/**
 * 后端站内信数据接口
 */
export interface BackendMessageData {
    id: string;
    messageId: string;
    messageType?: number;
    bizType?: number;
    title: string;
    content: string;
    senderName?: string;
    sendTime: string;
    readStatus?: number;
    readTime?: string;
}

/**
 * 转换后端通知数据为前端消息格式
 * @param backendData 后端通知数据
 * @returns 前端消息数据
 */
export function transformNoticeToMessage(backendData: BackendNoticeData): MessageItem {
    const noticeType = backendData.type !== undefined
        ? mapBackendToFrontendType(backendData.type)
        : FrontendMessageType.NOTICE;

    return {
        id: backendData.noticeId || backendData.id,
        type: noticeType,
        title: backendData.title || '',
        content: backendData.content || '',
        sendTime: backendData.sendTime || backendData.createTime || new Date().toISOString(),
        sendUser: backendData.sendUser || '系统',
        readStatus: (backendData.readStatus ?? 0) as 0 | 1,
        originalType: backendData.type,
        status: backendData.status,
        priority: backendData.priority
    };
}

/**
 * 转换后端站内信数据为前端消息格式
 * @param backendData 后端站内信数据
 * @returns 前端消息数据
 */
export function transformMessageToMessage(backendData: BackendMessageData): MessageItem {
    return {
        id: backendData.messageId || backendData.id,
        type: FrontendMessageType.MESSAGE,
        title: backendData.title,
        content: backendData.content,
        sendTime: backendData.sendTime,
        sendUser: backendData.senderName,
        readStatus: (backendData.readStatus ?? 0) as 0 | 1,
        originalType: backendData.messageType,
        messageType: backendData.messageType,
        bizType: backendData.bizType
    };
}

/**
 * 批量转换后端通知数据
 * @param dataList 后端通知数据列表
 * @returns 前端消息数据列表
 */
export function batchTransformNotices(dataList: BackendNoticeData[]): MessageItem[] {
    return dataList.map(transformNoticeToMessage);
}

/**
 * 批量转换后端站内信数据
 * @param dataList 后端站内信数据列表
 * @returns 前端消息数据列表
 */
export function batchTransformMessages(dataList: BackendMessageData[]): MessageItem[] {
    return dataList.map(transformMessageToMessage);
}

/**
 * 更新消息已读状态
 * @param messages 消息列表
 * @param messageId 消息ID
 * @param readStatus 已读状态
 * @returns 更新后的消息列表
 */
export function updateMessageReadStatus(
    messages: MessageItem[],
    messageId: string,
    readStatus: 0 | 1
): MessageItem[] {
    return messages.map((m) => {
        if (String(m.id) === messageId) {
            return {
                ...m,
                readStatus,
                readTime: readStatus === 1 ? new Date().toISOString() : m.readTime,
            };
        }
        return m;
    });
}

/**
 * 批量更新消息已读状态
 * @param messages 消息列表
 * @param messageIds 消息ID列表
 * @param readStatus 已读状态
 * @returns 更新后的消息列表
 */
export function batchUpdateMessageReadStatus(
    messages: MessageItem[],
    messageIds: string[],
    readStatus: 0 | 1
): MessageItem[] {
    const idSet = new Set(messageIds.map(String));
    const readTime = readStatus === 1 ? new Date().toISOString() : undefined;
    
    return messages.map((m) => {
        if (idSet.has(String(m.id))) {
            return {
                ...m,
                readStatus,
                readTime: readTime || m.readTime,
            };
        }
        return m;
    });
}

/**
 * 从列表中移除消息
 * @param messages 消息列表
 * @param messageId 消息ID
 * @returns 更新后的消息列表
 */
export function removeMessageFromList(messages: MessageItem[], messageId: string): MessageItem[] {
    return messages.filter((m) => String(m.id) !== messageId);
}

/**
 * 批量移除消息
 * @param messages 消息列表
 * @param messageIds 消息ID列表
 * @returns 更新后的消息列表
 */
export function batchRemoveMessages(messages: MessageItem[], messageIds: string[]): MessageItem[] {
    const idSet = new Set(messageIds.map(String));
    return messages.filter((m) => !idSet.has(String(m.id)));
}

/**
 * 过滤消息
 * @param messages 消息列表
 * @param filter 过滤条件
 * @returns 过滤后的消息列表
 */
export function filterMessages(
    messages: MessageItem[],
    filter: {
        type?: string;
        readStatus?: 0 | 1;
        keyword?: string;
    }
): MessageItem[] {
    let result = [...messages];

    if (filter.type && filter.type !== 'all') {
        result = result.filter((m) => m.type === filter.type);
    }

    if (filter.readStatus !== undefined) {
        result = result.filter((m) => m.readStatus === filter.readStatus);
    }

    if (filter.keyword) {
        const keyword = filter.keyword.toLowerCase();
        result = result.filter(
            (m) =>
                m.title.toLowerCase().includes(keyword) ||
                m.content.toLowerCase().includes(keyword)
        );
    }

    return result;
}

/**
 * 按类型分组消息
 * @param messages 消息列表
 * @returns 按类型分组的结果
 */
export function groupMessagesByType(messages: MessageItem[]): Record<string, MessageItem[]> {
    const grouped: Record<string, MessageItem[]> = {};

    messages.forEach((message) => {
        const type = message.type || 'system';
        if (!grouped[type]) {
            grouped[type] = [];
        }
        grouped[type].push(message);
    });

    return grouped;
}

/**
 * 按状态分组消息
 * @param messages 消息列表
 * @returns 按状态分组的结果
 */
export function groupMessagesByStatus(messages: MessageItem[]): {
    all: Record<string, MessageItem[]>;
    unread: Record<string, MessageItem[]>;
    read: Record<string, MessageItem[]>;
} {
    const result = {
        all: {} as Record<string, MessageItem[]>,
        unread: {} as Record<string, MessageItem[]>,
        read: {} as Record<string, MessageItem[]>,
    };

    const byType = groupMessagesByType(messages);

    Object.entries(byType).forEach(([type, typeMessages]) => {
        result.all[type] = typeMessages;
        result.unread[type] = typeMessages.filter((m) => m.readStatus === 0);
        result.read[type] = typeMessages.filter((m) => m.readStatus === 1);
    });

    return result;
}

/**
 * 按时间排序消息
 * @param messages 消息列表
 * @param order 排序方式，默认降序
 * @returns 排序后的消息列表
 */
export function sortMessagesByTime(
    messages: MessageItem[],
    order: 'asc' | 'desc' = 'desc'
): MessageItem[] {
    return [...messages].sort((a, b) => {
        const timeA = new Date(a.sendTime).getTime();
        const timeB = new Date(b.sendTime).getTime();
        return order === 'desc' ? timeB - timeA : timeA - timeB;
    });
}

/**
 * 计算未读消息数量
 * @param messages 消息列表
 * @returns 未读消息数量
 */
export function countUnread(messages: MessageItem[]): number {
    return messages.filter((m) => m.readStatus === 0).length;
}

/**
 * 计算各类型未读消息数量
 * @param messages 消息列表
 * @returns 各类型未读消息数量
 */
export function countUnreadByType(messages: MessageItem[]): Record<string, number> {
    const result: Record<string, number> = {};
    const grouped = groupMessagesByType(messages);

    Object.entries(grouped).forEach(([type, typeMessages]) => {
        result[type] = typeMessages.filter((m) => m.readStatus === 0).length;
    });

    return result;
}

/**
 * 构建通知中心数据源
 * @param messages 消息列表
 * @returns 通知中心数据源
 */
export function buildTabDataSource(messages: MessageItem[]): {
    all: Record<string, MessageItem[]>;
    unread: Record<string, MessageItem[]>;
    read: Record<string, MessageItem[]>;
} {
    const grouped = groupMessagesByStatus(messages);

    ['all', 'unread', 'read'].forEach((status) => {
        Object.keys(grouped[status as keyof typeof grouped]).forEach((type) => {
            grouped[status as keyof typeof grouped][type] = sortMessagesByTime(
                grouped[status as keyof typeof grouped][type]
            );
        });
    });

    return grouped;
}

/**
 * 查找消息
 * @param messages 消息列表
 * @param messageId 消息ID
 * @returns 找到的消息或 undefined
 */
export function findMessage(messages: MessageItem[], messageId: string): MessageItem | undefined {
    return messages.find((m) => String(m.id) === String(messageId));
}

/**
 * 检查消息是否存在
 * @param messages 消息列表
 * @param messageId 消息ID
 * @returns 是否存在
 */
export function hasMessage(messages: MessageItem[], messageId: string): boolean {
    return messages.some((m) => String(m.id) === String(messageId));
}

/**
 * 获取消息类型配置
 * @param type 消息类型
 * @returns 类型配置
 */
export function getMessageTypeConfig(type: string): MessageSourceConfig {
    const configs: Record<string, MessageSourceConfig> = {
        notice: {
            type: 'notice',
            label: '通知公告',
            icon: 'ri-notification-3-line',
            color: '#409eff',
        },
        message: {
            type: 'message',
            label: '站内信',
            icon: 'ri-mail-send-line',
            color: '#67c23a',
        },
        system: {
            type: 'system',
            label: '系统消息',
            icon: 'ri-notification-3-line',
            color: '#909399',
        },
        todo: {
            type: 'todo',
            label: '待办提醒',
            icon: 'ri-checkbox-circle-line',
            color: '#e6a23c',
        },
        alert: {
            type: 'alert',
            label: '警告提醒',
            icon: 'ri-alarm-warning-line',
            color: '#f56c6c',
        },
    };

    return configs[type] || configs.system;
}

/**
 * 获取所有消息类型配置
 * @returns 所有消息类型配置
 */
export function getAllMessageTypes(): MessageSourceConfig[] {
    return [
        { type: 'notice', label: '通知公告', icon: 'ri-notification-3-line', color: '#409eff' },
        { type: 'message', label: '站内信', icon: 'ri-mail-send-line', color: '#67c23a' },
        { type: 'system', label: '系统消息', icon: 'ri-notification-3-line', color: '#909399' },
        { type: 'todo', label: '待办提醒', icon: 'ri-checkbox-circle-line', color: '#e6a23c' },
        { type: 'alert', label: '警告提醒', icon: 'ri-alarm-warning-line', color: '#f56c6c' },
    ];
}
