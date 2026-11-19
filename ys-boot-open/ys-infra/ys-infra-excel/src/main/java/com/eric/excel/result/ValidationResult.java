package com.eric.excel.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据校验结果
 * 统一封装校验结果，替代返回null或抛出异常的方式
 *
 * @author Eric
 * @date 2025-02-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {

    /**
     * 是否校验通过
     */
    @Builder.Default
    private boolean valid = true;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 错误字段
     */
    private String errorField;

    /**
     * 原始值
     */
    private Object originalValue;

    /**
     * 创建成功结果
     */
    public static ValidationResult success() {
        return ValidationResult.builder().valid(true).build();
    }

    /**
     * 创建失败结果
     * @param errorMsg 错误信息
     * @return 校验失败的结果
     */
    public static ValidationResult fail(String errorMsg) {
        return ValidationResult.builder()
                .valid(false)
                .errorMsg(errorMsg)
                .build();
    }

    /**
     * 创建失败结果（带字段名）
     */
    public static ValidationResult fail(String errorMsg, String errorField) {
        return ValidationResult.builder()
                .valid(false)
                .errorMsg(errorMsg)
                .errorField(errorField)
                .build();
    }

    /**
     * 创建失败结果（带字段名和原始值）
     * @param errorMsg      错误信息
     * @param errorField    错误字段名
     * @param originalValue 原始值
     * @return 校验失败的结果
     */
    public static ValidationResult fail(String errorMsg, String errorField, Object originalValue) {
        return ValidationResult.builder()
                .valid(false)
                .errorMsg(errorMsg)
                .errorField(errorField)
                .originalValue(originalValue)
                .build();
    }
}
