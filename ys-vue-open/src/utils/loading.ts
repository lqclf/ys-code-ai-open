import { nextTick } from 'vue';
import '@/theme/loading.scss';

/**
 * 页面全局 Loading
 * @method start 创建 loading
 * @method done 移除 loading
 */
export const NextLoading = {
	// 创建 loading
	start: () => {
		// 检查是否已存在 loading 元素，避免重复创建
		if (document.querySelector('.loading-next')) {
			window.nextLoading = true;
			return;
		}

		const bodys: Element = document.body;
		const div = <HTMLElement>document.createElement('div');
		div.setAttribute('class', 'loading-next');
		const htmls = `
			<div class="loading-next-box">
				<div class="loading-next-box-warp">
					<div class="loading-next-box-item"></div>
					<div class="loading-next-box-item"></div>
					<div class="loading-next-box-item"></div>
					<div class="loading-next-box-item"></div>
					<div class="loading-next-box-item"></div>
					<div class="loading-next-box-item"></div>
					<div class="loading-next-box-item"></div>
					<div class="loading-next-box-item"></div>
					<div class="loading-next-box-item"></div>
				</div>
			</div>
		`;
		div.innerHTML = htmls;

		try {
			// 确保在 body 存在且有子节点时插入
			if (bodys.childNodes.length > 0) {
				bodys.insertBefore(div, bodys.childNodes[0]);
			} else {
				bodys.appendChild(div);
			}
			window.nextLoading = true;
		} catch (e) {
			console.warn('NextLoading start failed:', e);
			window.nextLoading = false;
		}
	},
	// 移除 loading
	done: (time: number = 0) => {
		nextTick(() => {
			setTimeout(() => {
				window.nextLoading = false;
				const el = <HTMLElement>document.querySelector('.loading-next');
				if (el && el.parentNode) {
					try {
						el.parentNode.removeChild(el);
					} catch (e) {
						console.warn('NextLoading done remove failed:', e);
					}
				}
			}, time);
		});
	},
};
