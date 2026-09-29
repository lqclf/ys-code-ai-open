package com.eric.common.annotation.sensitive;

import com.eric.common.enums.sensitive.SensitiveEnum;
import com.eric.common.utils.SM4Utils;
import com.eric.common.utils.SensitiveInfoUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

/**
 * 脱敏序列化器
 * 
 * 功能说明：
 * 1. 实现Jackson的JsonSerializer接口
 * 2. 支持ContextualSerializer上下文感知序列化
 * 3. 根据注解配置进行脱敏处理
 * 4. 支持多种敏感信息类型
 * 
 * 使用示例：
 * <pre>
 * {@code
 * public class User {
 *     @Sensitive(type = SensitiveEnum.MOBILE_PHONE)
 *     private String phone;
 * }
 * }
 * </pre>
 * 
 * @ClassName:  SensitiveSerialize
 * @author:     Eric
 * @date:       2026-01-16 12:08:14
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class SensitiveSerialize extends JsonSerializer<String> implements ContextualSerializer {

    /**
     * 敏感信息类型
     * 用于确定使用哪种脱敏策略
     */
    private SensitiveEnum type;

    /**
     * 序列化方法
     * 
     * 功能说明：
     * 1. 根据敏感类型选择不同的脱敏策略
     * 2. ENCODE类型：使用SM4加密
     * 3. 其他类型：使用对应的脱敏方法
     * 4. 加密失败时返回原始数据
     * 
     * @param data 待序列化的数据
     * @param jsonGenerator JSON生成器
     * @param serializerProvider 序列化提供者
     * @throws IOException IO异常
     */
    @Override
    public void serialize(String data, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        switch (type){
            case ENCODE:
                try {
                    jsonGenerator.writeString(SM4Utils.encrypt(data, SM4Utils.createKey()));
                } catch (Exception exception) {
                    jsonGenerator.writeString(data);
                }
                break;
            case CHINESE_NAME:
                jsonGenerator.writeString(SensitiveInfoUtils.chineseName(data));
                break;
            case ID_CARD:
                jsonGenerator.writeString(SensitiveInfoUtils.idCardNum(data));
                break;
            case FIXED_PHONE:
                jsonGenerator.writeString(SensitiveInfoUtils.fixedPhone(data));
                break;
            case MOBILE_PHONE:
                jsonGenerator.writeString(SensitiveInfoUtils.mobilePhone(data));
                break;
            case ADDRESS:
                jsonGenerator.writeString(SensitiveInfoUtils.address(data, 3));
                break;
            case EMAIL:
                jsonGenerator.writeString(SensitiveInfoUtils.email(data));
                break;
            case BANK_CARD:
                jsonGenerator.writeString(SensitiveInfoUtils.bankCard(data));
                break;
            case CNAPS_CODE:
                jsonGenerator.writeString(SensitiveInfoUtils.cnapsCode(data));
                break;
            default:
                jsonGenerator.writeString(data);
        }
    }

    /**
     * 创建上下文序列化器
     * 
     * 功能说明：
     * 1. 检查字段类型是否为String
     * 2. 获取字段上的@Sensitive注解
     * 3. 根据注解配置创建对应的序列化器
     * 4. 如果没有注解，使用默认序列化器
     * 
     * @param serializerProvider 序列化提供者
     * @param beanProperty Bean属性
     * @return 序列化器实例
     * @throws JsonMappingException JSON映射异常
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
                Sensitive sensitive = beanProperty.getAnnotation(Sensitive.class);
                if (sensitive == null) {
                    sensitive = beanProperty.getContextAnnotation(Sensitive.class);
                }
                if (sensitive != null) {
                    return new SensitiveSerialize(sensitive.type());
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(null);
    }
}
