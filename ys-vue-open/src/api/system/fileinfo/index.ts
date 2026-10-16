import request from '@/utils/request';

/**
 * 文件管理 API（简化版）
 * <p>仅提供文件信息的基础CRUD及按业务ID、文件ID集合查询功能</p>
 *
 * @author: Eric
 */
export function useFileinfoApi() {
  return {
    // 分页查询文件信息
    list: (params?: object) => {
      return request({
        url: '/api/system/fileinfo/list',
        method: 'get',
        params,
      });
    },

    // 新增文件记录
    add: (params?: object) => {
      return request({
        url: '/api/system/fileinfo/add',
        method: 'post',
        params,
      });
    },

    // 更新文件信息
    update: (params?: object) => {
      return request({
        url: '/api/system/fileinfo/update',
        method: 'put',
        params,
      });
    },

    // 逻辑删除
    delete: (ids: string) => {
      return request({
        url: `/api/system/fileinfo/delete/${ids}`,
        method: 'delete',
      });
    },

    // 通过ID查询文件详情
    getById: (id: string) => {
      return request({
        url: `/api/system/fileinfo/${id}`,
        method: 'get',
      });
    },

    // 批量物理删除
    deleteFilesPhysically: (fileIds: string[]) => {
      return request({
        url: '/api/system/fileinfo/deletePhysically',
        method: 'delete',
        data: fileIds,
      });
    },

    // 根据业务ID获取文件列表
    getFilesByBusinessId: (businessId: string) => {
      return request({
        url: `/api/system/fileinfo/business/${businessId}`,
        method: 'get',
      });
    },

    // 根据文件ID集合获取文件信息
    getFilesByIds: (fileIds: string[]) => {
      return request({
        url: '/api/system/fileinfo/getByIds',
        method: 'post',
        data: fileIds,
      });
    },
  };
}
