import { isRef, isReactive, toRaw } from 'vue';

/**
 * 通用表单重置工具
 * @param form 表单响应式对象 (reactive 或 ref)
 * @returns 重置函数
 */
export function useResetForm<T extends object>(form: T) {
  // 保存初始状态（深拷贝）
  const initialState = deepClone(toRaw(form));

  /**
   * 重置表单到初始状态
   */
  const resetForm = () => {
    if (isRef(form)) {
      // 处理 ref 对象
      form.value = deepClone(initialState);
    } else if (isReactive(form)) {
      // 处理 reactive 对象
      Object.keys(form).forEach(key => {
        delete form[key as keyof T];
      });
      Object.assign(form, deepClone(initialState));
    } else {
      // 处理普通对象
      Object.assign(form, deepClone(initialState));
    }
  };

  return { resetForm };
}

/**
 * 深度克隆对象（处理简单对象、数组、日期等）
 * @param obj 要克隆的对象
 * @returns 克隆后的对象
 */
function deepClone<T>(obj: T): T {
  if (obj === null || typeof obj !== 'object') return obj;
  
  // 处理日期对象
  if (obj instanceof Date) return new Date(obj.getTime()) as T;
  
  // 处理数组
  if (Array.isArray(obj)) {
    return obj.map(item => deepClone(item)) as unknown as T;
  }
  
  // 处理普通对象
  const clonedObj = {} as T;
  for (const key in obj) {
    if (obj.hasOwnProperty(key)) {
      clonedObj[key] = deepClone(obj[key]);
    }
  }
  return clonedObj;
}