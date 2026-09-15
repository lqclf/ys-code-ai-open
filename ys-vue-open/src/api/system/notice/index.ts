import request from '@/utils/request';

/**
 * 通知公告api接口集合
 * @method list 获取通知公告列表
 * @method add 创建通知公告
 * @method update 更新通知公告
 * @method delete 删除通知公告
 * @method getById 根据ID获取通知公告详情
 * @method publish 发布通知公告
 * @method cancel 撤销通知公告
 * @method read 标记通知公告为已读
 * @method getNoticeReadList 获取通知公告阅读列表
 * @method batchDelete 批量删除通知公告
 * @method exportNotice 导出通知公告
 * @method getReceiverList 获取接收者列表（指定用户）
 * @method updateReceiver 更新接收者列表
 */
export function useNoticeApi() {
  return {
    // 获取通知公告列表
    list: (params?: object) => {
      return request({
        url: '/api/system/notice/list',
        method: 'get',
        params,
      });
    },
    
    // 创建通知公告
    add: (params?: object) => {
      return request({
        url: '/api/system/notice/add',
        method: 'post',
        data: params,
      });
    },
    
    // 更新通知公告
    update: (params?: object) => {
      return request({
        url: '/api/system/notice/update',
        method: 'put',
        data: params,
      });
    },
    
    // 删除通知公告
    delete: (ids: string) => {
      return request({
        url: `/api/system/notice/delete/${ids}`,
        method: 'delete',
      });
    },
    
    // 批量删除通知公告
    batchDelete: (ids: string[]) => {
      return request({
        url: `/api/system/notice/delete/${ids}`,
        method: 'delete',
      });
    },
    
    // 根据ID获取通知公告详情
    getById: (id: string) => {
      return request({
        url: `/api/system/notice/${id}`,
        method: 'get',
      });
    },
    
    // 发布通知公告
    publish: (id: string) => {
      return request({
        url: `/api/system/notice/publish/${id}`,
        method: 'put',
      });
    },
    
    // 撤销通知公告
    cancel: (id: string) => {
      return request({
        url: `/api/system/notice/cancel/${id}`,
        method: 'put',
      });
    },
    
    // 标记通知公告为已读
    read: (id: string) => {
      return request({
        url: `/api/system/notice/read/${id}`,
        method: 'put',
      });
    },
    
    // 获取通知公告阅读列表
    getNoticeReadList: (noticeId: string, params?: object) => {
      return request({
        url: `/api/system/notice/readList/${noticeId}`,
        method: 'get',
        params,
      });
    },
    
    // 获取接收者列表（指定用户）
    getReceiverList: (id: string) => {
      return request({
        url: `/api/system/notice/receivers/${id}`,
        method: 'get',
      });
    },
    
    // 更新接收者列表
    updateReceiver: (params?: object) => {
      return request({
        url: '/api/system/notice/updateReceivers',
        method: 'put',
        params,
      });
    },
    
    // 获取用户的通知公告列表（前台使用）
    getUserNoticeList: (params?: object) => {
      return request({
        url: '/api/system/notice/userList',
        method: 'get',
        params,
      });
    },
    
    // 获取未读通知数量
    getUnreadCount: () => {
      return request({
        url: '/api/system/notice/unreadCount',
        method: 'get',
      });
    },
  };
}