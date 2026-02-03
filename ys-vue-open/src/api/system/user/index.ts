import request from '@/utils/request';

/**
 * 用户管理api接口集合
 * @method getUserInfo 获取当前登录用户信息
 * @method list 获取用户列表
 * @method add 创建用户
 * @method update 更新用户
 * @method delete 删除用户
 * @method getById 根据ID获取用户详情
 * @method updatePassword 修改用户密码
 * @method resetPassword 重置用户密码
 * @method freeze 冻结用户
 * @method unfreeze 解冻用户
 * @method getRoleList 获取角色列表
 * @method getDeptList 获取部门列表
 * @method exportUser 导出用户
 * @method importUser 导入用户
 * @method assignUsersToDept 将指定用户添加到指定部门
 * @method removeUsersFromDept 从部门批量移除用户
 * @method updateUserDetail 更新用户详情信息
 * @method getUserDetail 获取用户详情信息
 * @method toggleUserStatus 切换用户状态（冻结/解冻）
 */
export function useUserApi() {
  return {
    // 获取当前登录用户信息
    getUserInfo: () => {
      return request({
        url: '/api/auth/getLoginUser',
        method: 'get',
      });
    },
    
    // 获取用户列表
    list: (params?: object) => {
      return request({
        url: '/api/system/user/list',
        method: 'get',
        params,
      });
    },
    
    // 创建用户
    add: (params?: object) => {
      return request({
        url: '/api/system/user/add',
        method: 'post',
        params,
      });
    },
    
    // 更新用户
    update: (params?: object) => {
      return request({
        url: '/api/system/user/update',
        method: 'put',
        params,
      });
    },
    
    // 删除用户
    delete: (ids: string) => {
      return request({
        url: `/api/system/user/delete/${ids}`,
        method: 'delete',
      });
    },
    
    // 根据ID获取用户详情
    getById: (id: string) => {
      return request({
        url: `/api/system/user/${id}`,
        method: 'get',
      });
    },
    
    // 修改用户密码
    updatePassword: (params?: object) => {
      return request({
        url: '/api/system/user/updatePassword',
        method: 'put',
        params,
      });
    },
    
    // 重置用户密码
    resetPassword: (params?: object) => {
      return request({
        url: '/api/system/user/resetPassword',
        method: 'put',
        params,
      });
    },
    // 更新用户详情信息
    updateUserDetail: (params?: object) => {
      return request({
        url: '/api/system/user/updateDetail',
        method: 'put',
        params,
      });
    },
    
    // 获取用户详情信息
    getUserDetail: (userId: string) => {
      return request({
        url: `/api/system/user/detail/${userId}`,
        method: 'get',
      });
    },
    //冻结用户
    freezeUser: (id: string) => {
      return request({
        url: `/api/system/user/freeze/${id}`,
        method: 'put',
      });
    },
    //解冻用户
    unfreezeUser: (id: string) => {
      return request({
        url: `/api/system/user/unfreeze/${id}`,
        method: 'put',
      });
    },
    
    // 导出用户
    exportUser: (params?: object) => {
      return request({
        url: '/api/system/user/export',
        method: 'get',
        params,
        responseType: 'blob',
      });
    },
    
    // 导入用户
    importUser: (formData: FormData) => {
      return request({
        url: '/api/system/user/import',
        method: 'post',
        data: formData,
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
    },
    
    // 将指定用户添加到指定部门
    assignUsersToDept: (params?: object) => {
      return request({
        url: '/api/system/user/assignUsersToDept',
        method: 'post',
        data: params,
      });
    },
    
    // 从部门批量移除用户
    removeUsersFromDept: (params?: object) => {
      return request({
        url: '/api/system/user/removeUsersFromDept',
        method: 'post',
        data: params,
      });
    },
    // 获取用户列表（用于指定接收者）
    getUsersByIds: (userIds: string) => {
      return request({
        url: '/api/system/user/getUsersByIds',
        method: 'get',
        params: { userIds: userIds },
      });
    },
    
    // 将指定用户添加到指定角色
    addRoleUsers: (params?: object) => {
      return request({
        url: '/api/system/user/addRoleUsers',
        method: 'post',
        data: params,
      });
    },
    
    // 从角色批量移除用户
    removeRoleUsers: (params?: object) => {
      return request({
        url: '/api/system/user/removeRoleUsers',
        method: 'post',
        data: params,
      });
    },
  };
}