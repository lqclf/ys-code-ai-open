/**
 * 安全HTML渲染工具
 * 使用DOMPurify净化HTML内容，防止XSS攻击
 * @module utils/sanitize
 * @author Eric
 * @date 2025-04-15
 */

import DOMPurify from 'dompurify';

/**
 * DOMPurify配置选项
 * 允许的标签和属性白名单
 */
const ALLOWED_TAGS = [
    // 文本格式化
    'p', 'br', 'span', 'strong', 'b', 'em', 'i', 'u', 's', 'strike', 'sub', 'sup',
    // 标题
    'h1', 'h2', 'h3', 'h4', 'h5', 'h6',
    // 列表
    'ul', 'ol', 'li', 'dl', 'dt', 'dd',
    // 链接和图片
    'a', 'img',
    // 表格
    'table', 'thead', 'tbody', 'tfoot', 'tr', 'th', 'td', 'caption',
    // 布局
    'div', 'blockquote', 'pre', 'code', 'hr',
    // 其他
    'figure', 'figcaption', 'article', 'section', 'aside', 'header', 'footer', 'nav'
];

/**
 * 允许的属性白名单
 */
const ALLOWED_ATTR = [
    // 通用属性
    'class', 'id', 'title', 'style',
    // 链接属性
    'href', 'target', 'rel',
    // 图片属性
    'src', 'alt', 'width', 'height',
    // 表格属性
    'colspan', 'rowspan', 'border',
    // 数据属性
    'data-*'
];

/**
 * 净化HTML内容，防止XSS攻击
 * @param html 原始HTML字符串
 * @param options 可选的DOMPurify配置
 * @returns 净化后的安全HTML字符串
 */
export function sanitizeHtml(html: string, options?: DOMPurify.Config): string {
    if (!html || typeof html !== 'string') {
        return '';
    }
    
    const defaultConfig: DOMPurify.Config = {
        ALLOWED_TAGS,
        ALLOWED_ATTR,
        ALLOW_DATA_ATTR: true,
        FORBID_TAGS: ['script', 'iframe', 'object', 'embed', 'form', 'input', 'button'],
        FORBID_ATTR: ['onerror', 'onload', 'onclick', 'onmouseover', 'onfocus', 'onblur', 'onchange'],
    };
    
    const config = options ? { ...defaultConfig, ...options } : defaultConfig;
    
    return DOMPurify.sanitize(html, config);
}

/**
 * 净化并保留更多富文本编辑器内容
 * 用于WangEditor等富文本编辑器生成的内容
 * @param html 富文本HTML字符串
 * @returns 净化后的安全HTML字符串
 */
export function sanitizeRichText(html: string): string {
    if (!html || typeof html !== 'string') {
        return '';
    }
    
    return DOMPurify.sanitize(html, {
        ALLOWED_TAGS: [
            ...ALLOWED_TAGS,
            // 富文本编辑器额外允许的标签
            'font', 'center', 'mark', 'small', 'del', 'ins',
            'video', 'audio', 'source', 'track'
        ],
        ALLOWED_ATTR: [
            ...ALLOWED_ATTR,
            // 富文本编辑器额外允许的属性
            'color', 'face', 'size', 'controls', 'autoplay', 'loop', 'muted', 'poster',
            'preload', 'type'
        ],
        ALLOW_DATA_ATTR: true,
        ADD_TAGS: ['video', 'audio', 'source'],
        ADD_ATTR: ['controls', 'autoplay', 'loop', 'muted', 'poster', 'preload'],
        FORBID_TAGS: ['script', 'iframe', 'object', 'embed', 'form'],
        FORBID_ATTR: ['onerror', 'onload', 'onclick', 'onmouseover', 'onfocus', 'onblur', 'onchange'],
    });
}

/**
 * 净化Markdown渲染内容
 * 用于Markdown编辑器生成的内容
 * @param html Markdown渲染后的HTML字符串
 * @returns 净化后的安全HTML字符串
 */
export function sanitizeMarkdown(html: string): string {
    if (!html || typeof html !== 'string') {
        return '';
    }
    
    return DOMPurify.sanitize(html, {
        ALLOWED_TAGS: [
            ...ALLOWED_TAGS,
            // Markdown额外允许的标签
            'input', 'details', 'summary'
        ],
        ALLOWED_ATTR: [
            ...ALLOWED_ATTR,
            // Markdown额外允许的属性
            'type', 'checked', 'disabled'
        ],
        ALLOW_DATA_ATTR: true,
        FORBID_TAGS: ['script', 'iframe', 'object', 'embed', 'form'],
        FORBID_ATTR: ['onerror', 'onload', 'onclick', 'onmouseover', 'onfocus', 'onblur', 'onchange'],
    });
}

/**
 * 净化纯文本内容（仅保留基本格式）
 * @param html HTML字符串
 * @returns 仅包含基本格式标签的HTML字符串
 */
export function sanitizePlainText(html: string): string {
    if (!html || typeof html !== 'string') {
        return '';
    }
    
    return DOMPurify.sanitize(html, {
        ALLOWED_TAGS: ['p', 'br', 'strong', 'b', 'em', 'i', 'u', 's'],
        ALLOWED_ATTR: [],
    });
}

/**
 * 移除所有HTML标签，返回纯文本
 * @param html HTML字符串
 * @returns 纯文本字符串
 */
export function stripHtml(html: string): string {
    if (!html || typeof html !== 'string') {
        return '';
    }
    
    return DOMPurify.sanitize(html, { ALLOWED_TAGS: [] });
}

/**
 * 创建安全的HTML渲染函数（用于Vue组件）
 * @param html 原始HTML字符串
 * @param type 内容类型：'rich' | 'markdown' | 'plain' | 'default'
 * @returns 净化后的安全HTML字符串
 */
export function safeHtml(html: string, type: 'rich' | 'markdown' | 'plain' | 'default' = 'default'): string {
    switch (type) {
        case 'rich':
            return sanitizeRichText(html);
        case 'markdown':
            return sanitizeMarkdown(html);
        case 'plain':
            return sanitizePlainText(html);
        default:
            return sanitizeHtml(html);
    }
}

export default {
    sanitizeHtml,
    sanitizeRichText,
    sanitizeMarkdown,
    sanitizePlainText,
    stripHtml,
    safeHtml,
};
