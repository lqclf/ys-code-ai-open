import type { App } from 'vue';
import { authDirective } from '@/directive/authDirective';
import { wavesDirective, dragDirective } from '@/directive/customDirective';
import { safeHtmlDirective } from '@/directive/safeHtmlDirective';

/**
 * 导出指令方法：v-xxx
 * @methods authDirective 用户权限指令，用法：v-auth
 * @methods wavesDirective 按钮波浪指令，用法：v-waves
 * @methods dragDirective 自定义拖动指令，用法：v-drag
 * @methods safeHtmlDirective 安全HTML渲染指令，用法：v-safe-html
 */
export function directive(app: App) {
	// 用户权限指令
	authDirective(app);
	// 按钮波浪指令
	wavesDirective(app);
	// 自定义拖动指令
	dragDirective(app);
	// 安全HTML渲染指令（YsDialog 标题、日志详情等使用）
	app.directive('safe-html', safeHtmlDirective);
}
