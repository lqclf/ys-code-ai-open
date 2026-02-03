
let highlightStyleElement: HTMLLinkElement | null = null;
// 加载 highlight.js 主题
export function loadHighlightTheme(theme: 'light' | 'dark') {
  // 移除现有的 highlight 样式
  if (highlightStyleElement) {
    highlightStyleElement.remove();
  }
  
  // 创建新的样式链接
  highlightStyleElement = document.createElement('link');
  highlightStyleElement.rel = 'stylesheet';
  
  // 根据主题选择样式文件
  if (theme === 'dark') {
    highlightStyleElement.href = '/node_modules/highlight.js/styles/github-dark.css';
  } else {
    highlightStyleElement.href = '/node_modules/highlight.js/styles/default.css';
  }
  
  // 添加到页面头部
  document.head.appendChild(highlightStyleElement);
}

// 默认加载浅色主题
loadHighlightTheme('light');