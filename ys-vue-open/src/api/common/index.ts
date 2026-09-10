import request from '@/utils/request';
/**
 * 公共api集合
 */
export function commonApi() {
    return {
		getEnumValue: (code: String) => {
			return request({
				url: '/api/enum/getEnumValue/' + code,
				method: 'get',
			});
		},
	};


}