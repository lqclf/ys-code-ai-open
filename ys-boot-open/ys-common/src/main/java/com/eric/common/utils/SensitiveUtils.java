package com.eric.common.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


/**
 * 敏感词过滤工具类（基于 Trie 树算法）
 * 功能说明：
 * 1. 支持从资源文件加载敏感词并构建 Trie 树
 * 2. 提供敏感词过滤功能，替换敏感词为指定符号
 * 3. 支持跳过 HTML 图片标签内容
 * 4. 支持动态设置替换符号
 * 5. 提供敏感词检测功能
 * @ClassName:  SensitiveUtils
 * @author:     Eric
 * @date:       2025-07-22 16:25:00
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
@Component
public class SensitiveUtils {

    /**
     * 敏感词文件路径（需放在 resources 目录下）
     */
    private static final String SENSITIVE_WORD = "sensitive-words.txt";

    /**
     * 敏感词替换符号（默认 3 个星号）
     */
    private static volatile String replacement = "***";

    /**
     * Trie 树根节点
     */
    private static final TrieNode ROOT_NODE = new TrieNode();

    /**
     * 设置自定义替换符号
     *
     * @param replacement 新的替换符号
     */
    public static void setReplacement(String replacement) {
        SensitiveUtils.replacement = replacement;
    }

    /**
     * 加载敏感词文件并构建 Trie 树
     * 初始化方法在 Spring 容器启动后自动执行
     * 读取敏感词文件，逐行添加到 Trie 树中
     * 如果加载失败，仅记录警告日志，不影响后续流程
     */
    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream(SENSITIVE_WORD);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is != null ? is : new InputStream() {
                    @Override
                    public int read() throws IOException {
                        return -1;
                    }
                }))
        ) {
            if (is == null) {
                log.warn("敏感词文件 [{}] 未找到", SENSITIVE_WORD);
                return;
            }

            String keyword;
            while ((keyword = reader.readLine()) != null) {
                String trimKeyword = keyword.trim();
                if (!trimKeyword.isEmpty()) {
                    addKeyword(trimKeyword);
                }
            }
        } catch (Exception e) {
            log.warn("加载敏感词文件失败: {}", e.getMessage());
        }
    }

    /**
     * 将敏感词添加到 Trie 树中
     *
     * @param keyword 需要添加的敏感词
     * 实现逻辑：
     * 1. 从根节点开始遍历每个字符
     * 2. 若子节点不存在，则创建新节点
     * 3. 最后一个字符标记为敏感词结尾
     */
    private void addKeyword(String keyword) {
        TrieNode tempNode = ROOT_NODE;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if (subNode == null) {
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            tempNode = subNode;

            // 标记最后一个字符为敏感词结尾
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     *
     * @param text 待过滤的文本
     * @return 替换敏感词后的文本
     * 实现逻辑：
     * 1. 跳过包含 <img> 标签的文本（避免误过滤图片标签）
     * 2. 使用 Trie 树逐字符匹配敏感词
     * 3. 替换匹配到的敏感词为指定符号
     */
    public static String filter(String text) {
        // 跳过包含 <img> 标签的文本
        if (text != null && text.contains("<img")) {
            return text;
        }

        if (StringUtils.isBlank(text)) {
            return text;
        }

        // 将文本转为 char 数组，提高访问效率
        char[] chars = text.toCharArray();
        StringBuilder result = new StringBuilder();
        int length = chars.length;
        int begin = 0;
        int end = 0;

        while (begin < length) {
            // 处理非字母数字字符（符号）
            if (isSymbol(chars[begin])) {
                result.append(chars[begin]);
                begin++;
                continue;
            }

            // 查找敏感词的结束位置
            end = begin;
            TrieNode currentNode = ROOT_NODE;

            while (end < length) {
                char c = chars[end];

                // 如果遇到符号，结束当前匹配
                if (isSymbol(c)) {
                    break;
                }

                currentNode = currentNode.getSubNode(c);

                // 如果当前字符不是敏感词的一部分，结束匹配
                if (currentNode == null) {
                    break;
                }

                // 如果匹配到完整敏感词
                if (currentNode.isKeywordEnd()) {
                    result.append(replacement);
                    begin = ++end;
                    break;
                }

                end++;
            }

            // 如果没有匹配到敏感词
            if (end == begin) {
                result.append(chars[begin]);
                begin++;
            } else {
                // 继续处理下一个字符
                begin = end;
            }
        }

        return result.toString();
    }

    /**
     * 检查文本是否包含敏感词
     *
     * @param text 待检查的文本
     * @return 是否包含敏感词
     */
    public static boolean containsSensitiveWord(String text) {
        return !filter(text).equals(text);
    }

    /**
     * 判断字符是否为符号（非 ASCII 字母数字）
     *
     * @param c 待判断的字符
     * @return 是否为符号
     * 实现逻辑：
     * 1. ASCII 字母数字（0x20~0x7E）不属于符号
     * 2. 东亚文字范围（0x2E80~0x9FFF）不属于符号
     * 3. 其他字符视为符号
     */
    private static boolean isSymbol(char c) {
        return !isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    /**
     * 判断字符是否为 ASCII 字母或数字
     *
     * @param c 待判断的字符
     * @return 是否为 ASCII 字母或数字
     */
    private static boolean isAsciiAlphanumeric(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
    }

    /**
     * Trie 树节点
     * 每个节点存储子节点（Character -> TrieNode）和敏感词结束标志
     */
    private static class TrieNode {
        // 关键词结束标志
        private boolean isKeywordEnd = false;

        // 子节点集合（Character -> TrieNode）
        private final Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public TrieNode getSubNode(char c) {
            return subNodes.get(c);
        }

        public void addSubNode(char c, TrieNode node) {
            subNodes.put(c, node);
        }
    }
}