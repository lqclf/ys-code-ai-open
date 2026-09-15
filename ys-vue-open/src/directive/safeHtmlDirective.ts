/**
 * 安全HTML渲染指令
 * 使用 v-safe-html 替代 v-html，自动进行XSS防护
 * @module directives/safeHtml
 * @author Eric
 * @date 2025-04-15
 */

import type { Directive, DirectiveBinding } from 'vue';
import { sanitizeHtml, sanitizeRichText, sanitizeMarkdown, sanitizePlainText } from '@/utils/sanitize';

type SafeHtmlType = 'default' | 'rich' | 'markdown' | 'plain';

interface SafeHtmlValue {
    html: string;
    type?: SafeHtmlType;
}

/**
 * 获取净化函数
 */
function getSanitizeFunction(type: SafeHtmlType): (html: string) => string {
    switch (type) {
        case 'rich':
            return sanitizeRichText;
        case 'markdown':
            return sanitizeMarkdown;
        case 'plain':
            return sanitizePlainText;
        default:
            return sanitizeHtml;
    }
}

/**
 * 处理指令值
 */
function processValue(value: string | SafeHtmlValue): { html: string; type: SafeHtmlType } {
    if (typeof value === 'string') {
        return { html: value, type: 'default' };
    }
    return {
        html: value.html || '',
        type: value.type || 'default'
    };
}

/**
 * 更新元素内容
 */
function updateElement(el: HTMLElement, binding: DirectiveBinding<string | SafeHtmlValue>): void {
    const { html, type } = processValue(binding.value);
    const sanitizeFn = getSanitizeFunction(type);
    el.innerHTML = sanitizeFn(html);
}

/**
 * v-safe-html 指令
 * 
 * 使用方式：
 * 1. 基本用法：v-safe-html="htmlContent"
 * 2. 指定类型：v-safe-html="{ html: htmlContent, type: 'rich' }"
 * 
 * 类型说明：
 * - default: 默认模式，适用于普通HTML内容
 * - rich: 富文本模式，适用于WangEditor等富文本编辑器内容
 * - markdown: Markdown模式，适用于Markdown渲染内容
 * - plain: 纯文本模式，仅保留基本格式标签
 */
export const safeHtmlDirective: Directive<HTMLElement, string | SafeHtmlValue> = {
    mounted(el: HTMLElement, binding: DirectiveBinding<string | SafeHtmlValue>): void {
        updateElement(el, binding);
    },
    updated(el: HTMLElement, binding: DirectiveBinding<string | SafeHtmlValue>): void {
        updateElement(el, binding);
    }
};

/**
 * 注册安全HTML指令
 */
export function setupSafeHtmlDirective(app: any): void {
    app.directive('safe-html', safeHtmlDirective);
}

export default safeHtmlDirective;
