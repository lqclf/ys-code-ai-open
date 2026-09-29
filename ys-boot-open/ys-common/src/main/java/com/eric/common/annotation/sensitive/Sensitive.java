package com.eric.common.annotation.sensitive;

import com.eric.common.enums.sensitive.SensitiveEnum;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.*;

/**
 * 敏感信息注解
 * 
 * 功能说明：
 * 1. 用于标记需要在JSON序列化时脱敏的字段
 * 2. 配合Jackson框架实现自动脱敏
 * 3. 支持多种敏感信息类型
 * 4. 通过SensitiveSerialize实现脱敏逻辑
 * 
 * 使用示例：
 * <pre>
 * {@code
 * public class User {
 *     
 *     @Sensitive(type = SensitiveEnum.MOBILE_PHONE)
 *     private String phone;
 *     
 *     @Sensitive(type = SensitiveEnum.ID_CARD)
 *     private String idCard;
 *     
 *     @Sensitive(type = SensitiveEnum.EMAIL)
 *     private String email;
 *     
 *     @Sensitive(type = SensitiveEnum.CHINESE_NAME)
 *     private String name;
 *     
 *     @Sensitive(type = SensitiveEnum.BANK_CARD)
 *     private String bankCard;
 *     
 *     // getters and setters
 * }
 * 
 * // 使用示例
 * ObjectMapper mapper = new ObjectMapper();
 * User user = new User();
 * user.setPhone("13812345678");
 * user.setIdCard("110101199001011234");
 * user.setEmail("zhangsan@example.com");
 * user.setName("张三");
 * user.setBankCard("6222021234567890123");
 * 
 * String json = mapper.writeValueAsString(user);
 * // 输出结果：
 * // {"phone":"138****5678","idCard":"************1234","email":"z****@example.com","name":"张*","bankCard":"***********0123"}
 * }
 * </pre>
 * 
 * 注解属性说明：
 * <ul>
 *   <li>type - 指定敏感信息类型，默认为ENCODE（加密类型）</li>
 * </ul>
 * 
 * 支持的敏感信息类型：
 * <ul>
 *   <li>ENCODE - 加密类型，使用SM4算法加密</li>
 *   <li>CHINESE_NAME - 中文名称，保留姓氏，名字脱敏</li>
 *   <li>ID_CARD - 身份证号，保留后4位</li>
 *   <li>FIXED_PHONE - 座机号，保留后4位</li>
 *   <li>MOBILE_PHONE - 手机号，保留前3位和后4位</li>
 *   <li>ADDRESS - 地址，保留前6位和后4位</li>
 *   <li>EMAIL - 电子邮箱，保留首字母和@后域名</li>
 *   <li>BANK_CARD - 银行卡号，保留后4位</li>
 *   <li>CNAPS_CODE - 公司开户银行联号，保留后4位</li>
 *   <li>PASSPORT - 护照号码，保留前2位和后2位</li>
 *   <li>DRIVING_LICENSE - 驾驶证号码，保留前2位和后4位</li>
 *   <li>LICENSE_PLATE - 车牌号，保留省份和后2位</li>
 *   <li>CREDIT_CODE - 统一社会信用代码，保留前6位和后4位</li>
 *   <li>TAX_NUMBER - 税号，保留前6位和后4位</li>
 * </ul>
 * 
 * 使用场景：
 * - 实体类字段标记敏感信息
 * - JSON序列化时自动脱敏
 * - API接口返回数据脱敏
 * - 前端展示敏感数据保护
 * 
 * 注意事项：
 * 1. 此注解仅用于字段级别
 * 2. 需要配合Jackson框架使用
 * 3. 脱敏逻辑由SensitiveSerialize实现
 * 4. 默认类型为ENCODE，需要根据实际情况指定
 * 5. 脱敏后的数据无法还原，请谨慎使用
 * 
 * @ClassName:  Sensitive
 * @author:     Eric
 * @date:       2025-07-30 15:10:54
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveSerialize.class)
public @interface Sensitive {

    /**
     * 敏感信息类型
     * 
     * 说明：
     * - 指定字段的敏感信息类型
     * - 不同类型对应不同的脱敏策略
     * - 默认为ENCODE（加密类型）
     * 
     * 可选值：
     * <ul>
     *   <li>ENCODE - 加密类型，使用SM4算法加密</li>
     *   <li>CHINESE_NAME - 中文名称，保留姓氏，名字脱敏</li>
     *   <li>ID_CARD - 身份证号，保留后4位</li>
     *   <li>FIXED_PHONE - 座机号，保留后4位</li>
     *   <li>MOBILE_PHONE - 手机号，保留前3位和后4位</li>
     *   <li>ADDRESS - 地址，保留前6位和后4位</li>
     *   <li>EMAIL - 电子邮箱，保留首字母和@后域名</li>
     *   <li>BANK_CARD - 银行卡号，保留后4位</li>
     *   <li>CNAPS_CODE - 公司开户银行联号，保留后4位</li>
     *   <li>PASSPORT - 护照号码，保留前2位和后2位</li>
     *   <li>DRIVING_LICENSE - 驾驶证号码，保留前2位和后4位</li>
     *   <li>LICENSE_PLATE - 车牌号，保留省份和后2位</li>
     *   <li>CREDIT_CODE - 统一社会信用代码，保留前6位和后4位</li>
     *   <li>TAX_NUMBER - 税号，保留前6位和后4位</li>
     * </ul>
     * 
     * 使用示例：
     * <pre>
     * {@code
     * @Sensitive(type = SensitiveEnum.MOBILE_PHONE)
     * private String phone;
     * 
     * @Sensitive(type = SensitiveEnum.ID_CARD)
     * private String idCard;
     * 
     * @Sensitive(type = SensitiveEnum.EMAIL)
     * private String email;
     * }
     * </pre>
     * 
     * @return 敏感信息类型，默认为ENCODE
     */
    SensitiveEnum type() default SensitiveEnum.ENCODE;
}
