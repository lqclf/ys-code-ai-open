/**
 * 对象拷贝，只拷贝源对象有，默认对象没有的属性
 * @param source 源对象
 * @param defaults 默认对象
 * @returns 
 */
export const objectCopyForm = (source: any, defaults: any) => {

  const result = {};
  Object.keys(defaults).forEach(key => {
    (result as Record<string, any>)[key] = source[key] !== undefined ? source[key] : defaults[key];
  });
  return result;
};

