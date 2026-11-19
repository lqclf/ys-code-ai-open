/**
 * 导出任务管理 API 接口集合
 * @author: ERIC
 * @date: 2025-02-27
 */
import request from '@/utils/request';

/**
 * 导出任务管理 API
 */
export function useExportApi() {
    return {
        /**
         * 获取导出任务列表
         * @param params 查询参数
         */
        list: (params?: object) => {
            return request({
                url: '/api/excel/export/list',
                method: 'get',
                params,
            });
        },

        /**
         * 执行导出
         * @param moduleCode 模块编码
         * @param data 导出参数
         */
        execute: (moduleCode: string, data: any) => {
            return request({
                url: `/api/excel/export/${moduleCode}/execute`,
                method: 'post',
                data,
                responseType: 'blob',
            });
        },

        /**
         * 异步导出
         * @param moduleCode 模块编码
         * @param data 导出参数
         */
        asyncExport: (moduleCode: string, data: any) => {
            return request({
                url: `/api/excel/export/${moduleCode}/async`,
                method: 'post',
                data,
            });
        },

        /**
         * 下载导出文件
         * @param taskId 任务ID
         */
        download: (taskId: string) => {
            return request({
                url: `/api/excel/export/download/${taskId}`,
                method: 'get',
                responseType: 'blob',
            });
        },
    };
}
