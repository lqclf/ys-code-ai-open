/**
 * 菜单辅助工具函数
 * 用于根据组件路径自动生成路由相关信息
 * @author Eric
 * @date 2025-04-29
 */

/**
 * 根据组件路径生成路由路径
 * @param component 组件路径
 * @returns 路由路径
 * @example
 * generatePath('system/menu/index') // 返回 '/system/menu'
 * generatePath('ai/provider/index') // 返回 '/ai/provider'
 * generatePath('layout/routerView/parent') // 返回 ''
 */
export function generatePath(component: string): string {
    if (!component || component === 'layout/routerView/parent') {
        return '';
    }

    // 去掉 /index.vue 或 index 后缀
    let path = component.replace(/\/index(\.vue)?$/, '');
    path = path.replace(/\/index$/, '');

    // 去掉开头的斜杠（如果有）
    path = path.replace(/^\//, '');

    // 确保以 / 开头
    if (!path.startsWith('/')) {
        path = '/' + path;
    }

    return path;
}

/**
 * 根据组件路径生成路由名称（驼峰命名）
 * @param component 组件路径
 * @returns 路由名称
 * @example
 * generateName('system/menu/index') // 返回 'systemMenu'
 * generateName('ai/provider/index') // 返回 'aiProvider'
 * generateName('codegen/datasource/index') // 返回 'codegenDatasource'
 * generateName('layout/routerView/parent') // 返回 ''
 */
export function generateName(component: string): string {
    if (!component) {
        return '';
    }

    if (component === 'layout/routerView/parent') {
        return '';
    }

    // 去掉 /index.vue 或 index 后缀
    let name = component.replace(/\/index(\.vue)?$/, '');
    name = name.replace(/\/index$/, '');

    // 去掉开头的斜杠（如果有）
    name = name.replace(/^\//, '');

    // 转换为驼峰命名
    const parts = name.split('/').filter((p) => p);
    if (parts.length === 0) {
        return '';
    }

    // 第一段保持小写，后续段首字母大写
    return parts
        .map((part, index) => {
            if (index === 0) {
                return part.toLowerCase();
            }
            return part.charAt(0).toUpperCase() + part.slice(1).toLowerCase();
        })
        .join('');
}

/**
 * 根据组件路径生成权限标识
 * @param component 组件路径
 * @returns 权限标识
 * @example
 * generatePermission('system/menu/index') // 返回 'system:menu:list'
 * generatePermission('ai/provider/index') // 返回 'ai:provider:list'
 * generatePermission('codegen/datasource/index') // 返回 'codegen:datasource:list'
 * generatePermission('layout/routerView/parent') // 返回 ''
 */
export function generatePermission(component: string): string {
    if (!component || component === 'layout/routerView/parent') {
        return '';
    }

    // 去掉 /index.vue 或 index 后缀
    let permission = component.replace(/\/index(\.vue)?$/, '');
    permission = permission.replace(/\/index$/, '');

    // 去掉开头的斜杠（如果有）
    permission = permission.replace(/^\//, '');

    // 将路径分隔符替换为冒号
    permission = permission.replace(/\//g, ':');

    // 在末尾添加 :list
    return permission + ':list';
}

/**
 * 根据组件路径批量生成路由相关信息
 * @param component 组件路径
 * @returns 包含路由路径、路由名称、权限标识的对象
 * @example
 * generateRouteInfo('system/menu/index')
 * // 返回 { path: '/system/menu', name: 'systemMenu', permission: 'system:menu:list' }
 */
export function generateRouteInfo(component: string): {
    path: string;
    name: string;
    permission: string;
} {
    return {
        path: generatePath(component),
        name: generateName(component),
        permission: generatePermission(component),
    };
}
