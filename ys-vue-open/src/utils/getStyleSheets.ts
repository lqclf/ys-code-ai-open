import { nextTick } from 'vue';
import * as svg from '@element-plus/icons-vue';

// 初始化获取 css 样式，获取 element plus 自带 svg 图标，增加了 ele- 前缀，使用时：ele-Aim
const getElementPlusIconfont = () => {
	return new Promise((resolve, reject) => {
		nextTick(() => {
			const icons = svg as any;
			const sheetsIconList = [];
			for (const i in icons) {
				sheetsIconList.push(`ele-${icons[i].name}`);
			}
			if (sheetsIconList.length > 0) resolve(sheetsIconList);
			else reject('未获取到值，请刷新重试');
		});
	});
};

// 初始化获取 css 样式，这里使用 fontawesome 的图标 
const getRemixiconIconfont = async () => {
	return new Promise((resolve, reject) => {
		nextTick(async () => {
			 // 直接从本地 remixicon.css 文件加载图标数据
			const response = await fetch('/theme/remixicon/remixicon.css')
			const cssContent = await response.text()
			
			// 使用更准确的正则表达式提取所有图标类名
			const iconRegex = /\.ri-[a-zA-Z0-9-]+:before\s*\{/g
			const iconList = []
			const iconfontDetails = {} as Record<string, string>;
			let match
			
			while ((match = iconRegex.exec(cssContent)) !== null) {
				// 提取类名部分，移除 :before 伪类
				const fullMatch = match[0]
				const className = fullMatch.substring(1, fullMatch.length - 9) // 移除开头的.和结尾的:before
				iconList.push(className)
				iconfontDetails[className] = className;
			}
			
			resolve({ iconList, iconfontDetails });
		});
	});
};

// 获取iconfont图标数据
const getIconfontJson = () => {
	return new Promise(async (resolve, reject) => {
		try {
			// 修复路径问题，使用正确的相对路径
			const response = await fetch('/src/theme/iconfont/iconfont.json');
			const data = await response.json();
			// 处理实际的JSON结构，该结构包含glyphs数组
			if (data.glyphs && Array.isArray(data.glyphs)) {
				const iconfontDetails = {} as Record<string, string>;
				const iconList = data.glyphs.map((glyph: any) => {
					// 保存图标name和class的映射关系
					iconfontDetails[`cn cn-${glyph.font_class}`] = glyph.name;
					return `cn cn-${glyph.font_class}`;
				});
				resolve({ iconList, iconfontDetails });
			} else {
				resolve({ iconList: [], iconfontDetails: {} });
			}
		} catch (error) {
			console.error('Failed to load iconfont.json:', error);
			reject('Failed to load iconfont.json');
		}
	});
};

/**
 * 获取字体图标 `document.styleSheets`
 * @method ali 获取阿里字体图标 `<i class="iconfont 图标类名"></i>`
 * @method ele 获取 element plus 自带图标 `<i class="图标类名"></i>`
 * @method awe 获取 fontawesome 的图标 `<i class="fa 图标类名"></i>`
 * @method iconfont 获取 iconfont 的图标
 */
const initIconfont = {
	// element plus
	ele: () => {
		return getElementPlusIconfont();
	},
	// fontawesome
	remixicon: () => {
		return getRemixiconIconfont();
	},
	// iconfont
	iconfont: () => {
		return getIconfontJson();
	},
};

// 导出方法
export default initIconfont;
