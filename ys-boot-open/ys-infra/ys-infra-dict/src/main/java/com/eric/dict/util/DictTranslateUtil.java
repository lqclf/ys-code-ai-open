package com.eric.dict.util;

import com.eric.dict.aspect.DictAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 字典翻译工具类
 * 直接复用 DictAspect 的共享内存缓存，提供字典值与文本的双向翻译
 * 适用于导出处理器、导入处理器等非Controller层场景
 * 当 DictAspect 缓存未初始化时，自动触发缓存加载
 * @ClassName:   DictTranslateUtil
 * @author:      Eric
 * @date:        2026-05-13
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
@Component
public class DictTranslateUtil {

    /**
     * 确保字典缓存已初始化
     * 当缓存未初始化时（如导出场景未经过Controller AOP），主动触发加载
     */
    private void ensureCacheReady() {
        if (!DictAspect.isCacheInitialized()) {
            DictAspect.ensureCacheInitialized();
        }
    }

    /**
     * 通过字典编码和字典项编码获取翻译文本（name）
     * 已做空指针保护，任何参数异常均返回空字符串
     * 当 DictAspect 缓存未初始化时自动触发加载
     * @param dictCode 字典编码，如 "industry"、"customer_source"、"customer_level" 等
     * @param code 字典项编码，如 "IT"、"A"、1 等
     * @return 翻译后的名称，如未找到或参数异常则返回空字符串
     */
    public String getDictName(String dictCode, Object code) {
        try {
            if (dictCode == null || code == null) {
                return "";
            }
            String codeStr = String.valueOf(code);
            if (codeStr.isEmpty() || "null".equals(codeStr)) {
                return "";
            }
            ensureCacheReady();
            return DictAspect.getDictName(dictCode, codeStr);
        } catch (Exception e) {
            log.warn("字典翻译异常，dictCode={}, code={}", dictCode, code, e);
            return "";
        }
    }

    /**
     * 通过字典编码和翻译文本（name）获取字典项编码（code）
     * 已做空指针保护，任何参数异常均返回空字符串
     * 当 DictAspect 缓存未初始化时自动触发加载
     * @param dictCode 字典编码，如 "industry"、"customer_source"、"customer_level" 等
     * @param name 翻译后的名称，如 "信息技术"、"A级客户" 等
     * @return 字典项编码，如未找到或参数异常则返回空字符串
     */
    public String getDictCode(String dictCode, String name) {
        try {
            if (dictCode == null || name == null || name.isEmpty()) {
                return "";
            }
            ensureCacheReady();
            Map<String, String> codeMap = DictAspect.getDictMap(dictCode);
            if (codeMap.isEmpty()) {
                return "";
            }
            for (Map.Entry<String, String> entry : codeMap.entrySet()) {
                if (name.equals(entry.getValue())) {
                    return entry.getKey();
                }
            }
            return "";
        } catch (Exception e) {
            log.warn("字典反向查询异常，dictCode={}, name={}", dictCode, name, e);
            return "";
        }
    }
}