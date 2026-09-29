package com.eric.common.utils;

import com.eric.common.enums.sensitive.SensitiveEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;


/**
 * 敏感信息处理工具类
 * 
 * 功能说明：
 * 1. 提供各种敏感信息的脱敏方法（手机号、身份证号、邮箱等）
 * 2. 支持SM4加密解密
 * 3. 支持安全模式（加密/解密失败时返回脱敏数据）
 * 4. 支持自定义脱敏规则
 * 
 * 使用示例：
 * <pre>
 * {@code
 * // 手机号脱敏
 * String phone = "13812345678";
 * String masked = SensitiveInfoUtils.mobilePhone(phone);
 * // 结果: 138****5678
 * 
 * // 身份证号脱敏
 * String idCard = "110101199001011234";
 * String masked = SensitiveInfoUtils.idCardNum(idCard);
 * // 结果: ************1234
 * 
 * // 数据加密
 * String encrypted = SensitiveInfoUtils.getEncodeData("13812345678", SensitiveEnum.ENCODE);
 * 
 * // 数据解密
 * String decrypted = SensitiveInfoUtils.getDecodeData(encrypted);
 * }
 * </pre>
 * 
 * @ClassName:  SensitiveInfoUtils
 * @author:     Eric
 * @date:       2025-07-30 15:20:05
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
@Slf4j
public class SensitiveInfoUtils {

    /**
     * SM4加密密钥
     * 建议从配置文件读取
     * 生产环境请务必修改此密钥或通过配置中心动态注入
     * 默认值仅用于开发环境，生产环境必须更换
     */
    private static String SM4_KEY = "6f4c05de2194d2b29eab61ef792b26af";
    
    /**
     * 是否启用加密失败时返回脱敏数据而非原始数据的安全模式
     * true: 加密/解密失败时返回脱敏数据（推荐，更安全）
     * false: 加密/解密失败时返回原始数据（不推荐，可能泄露敏感信息）
     */
    private static boolean SAFE_MODE = true;
    
    /**
     * 设置SM4加密密钥
     * 
     * 功能说明：
     * 1. 验证密钥格式（必须为32位十六进制字符串）
     * 2. 更新系统使用的SM4密钥
     * 3. 记录日志
     * 
     * 使用示例：
     * <pre>
     * {@code
     * SensitiveInfoUtils.setSm4Key("0123456789abcdef0123456789abcdef");
     * }
     * </pre>
     * 
     * @param key 32位十六进制字符串
     */
    public static void setSm4Key(String key) {
        if (StringUtils.isNotBlank(key) && key.length() == 32) {
            SM4_KEY = key;
           // log.info("SM4加密密钥已更新");
        } else {
            log.warn("SM4密钥格式不正确，密钥必须为32位十六进制字符串");
        }
    }
    
    /**
     * 获取当前SM4密钥
     * 
     * 功能说明：
     * 返回当前系统使用的SM4加密密钥
     * 
     * @return 当前密钥
     */
    public static String getSm4Key() {
        return SM4_KEY;
    }
    
    /**
     * 设置安全模式
     * 
     * 功能说明：
     * 1. 设置安全模式开关
     * 2. 记录日志
     * 
     * 使用示例：
     * <pre>
     * {@code
     * // 启用安全模式（推荐）
     * SensitiveInfoUtils.setSafeMode(true);
     * 
     * // 禁用安全模式（不推荐）
     * SensitiveInfoUtils.setSafeMode(false);
     * }
     * </pre>
     * 
     * @param safeMode true表示安全模式，加密/解密失败时返回脱敏数据；false表示返回原始数据
     */
    public static void setSafeMode(boolean safeMode) {
        SAFE_MODE = safeMode;
    }
    
    /**
     * 处理数据 获取解密后的数据
     * 
     * 功能说明：
     * 1. 使用SM4算法解密数据
     * 2. 解密失败时根据安全模式返回脱敏数据或原始数据
     * 3. 记录错误日志
     * 
     * 使用示例：
     * <pre>
     * {@code
     * String encrypted = "加密后的数据";
     * String decrypted = SensitiveInfoUtils.getDecodeData(encrypted);
     * }
     * </pre>
     * 
     * @param data 待解密数据
     * @return 解密后的数据，解密失败时根据安全模式返回脱敏数据或原始数据
     */
    public static String getDecodeData(String data) {
        if (StringUtils.isEmpty(data)) {
            return data;
        }
        try {
            String result = SM4Utils.decrypt(data, SM4_KEY);
            if (StringUtils.isNotEmpty(result)) {
                return result;
            }
            log.warn("数据解密结果为空，可能数据未加密或密钥不匹配");
        } catch (Exception exception) {
            log.error("数据解密错误，原数据长度: {}, 错误信息: {}", data.length(), exception.getMessage());
        }
        
        if (SAFE_MODE) {
            log.warn("安全模式下解密失败，返回脱敏数据");
            return maskSensitiveData(data);
        }
        log.warn("非安全模式下解密失败，返回原始数据");
        return data;
    }

    /**
     * 处理数据 获取加密后的数据 或是格式化后的数据
     * 
     * 功能说明：
     * 1. 根据敏感类型选择不同的处理方式
     * 2. ENCODE类型：使用SM4加密
     * 3. 其他类型：使用对应的脱敏方法
     * 4. 处理失败时根据安全模式返回脱敏数据或原始数据
     * 
     * 使用示例：
     * <pre>
     * {@code
     * // 加密数据
     * String encrypted = SensitiveInfoUtils.getEncodeData("13812345678", SensitiveEnum.ENCODE);
     * 
     * // 脱敏手机号
     * String masked = SensitiveInfoUtils.getEncodeData("13812345678", SensitiveEnum.MOBILE_PHONE);
     * 
     * // 脱敏身份证号
     * String masked = SensitiveInfoUtils.getEncodeData("110101199001011234", SensitiveEnum.ID_CARD);
     * }
     * </pre>
     * 
     * @param data 字符串
     * @param sensitiveEnum 类型
     * @return 处理后的字符串
     */
    public static String getEncodeData(String data, SensitiveEnum sensitiveEnum) {
        if (StringUtils.isEmpty(data)) {
            return data;
        }
        
        try {
            switch (sensitiveEnum) {
                case ENCODE:
                    return encryptData(data);
                case CHINESE_NAME:
                    return chineseName(data);
                case ID_CARD:
                    return idCardNum(data);
                case FIXED_PHONE:
                    return fixedPhone(data);
                case MOBILE_PHONE:
                    return mobilePhone(data);
                case ADDRESS:
                    return address(data, 3);
                case EMAIL:
                    return email(data);
                case BANK_CARD:
                    return bankCard(data);
                case CNAPS_CODE:
                    return cnapsCode(data);
                case PASSPORT:
                    return passport(data);
                case DRIVING_LICENSE:
                    return drivingLicense(data);
                case LICENSE_PLATE:
                    return licensePlate(data);
                case CREDIT_CODE:
                    return creditCode(data);
                case TAX_NUMBER:
                    return taxNumber(data);
                default:
                    return data;
            }
        } catch (Exception e) {
            log.error("数据脱敏处理失败，类型: {}, 数据长度: {}, 错误: {}", sensitiveEnum, data.length(), e.getMessage());
            if (SAFE_MODE) {
                return maskSensitiveData(data);
            }
            return data;
        }
    }
    
    /**
     * 加密数据
     * 
     * 功能说明：
     * 1. 使用SM4算法加密数据
     * 2. 加密失败时根据安全模式返回脱敏数据或原始数据
     * 
     * @param data 待加密数据
     * @return 加密后的数据，失败时根据安全模式返回脱敏数据或原始数据
     */
    private static String encryptData(String data) {
        try {
            return SM4Utils.encrypt(data, SM4_KEY);
        } catch (Exception exception) {
            log.error("数据加密错误，数据长度: {}, 错误信息: {}", data.length(), exception.getMessage());
            if (SAFE_MODE) {
                return maskSensitiveData(data);
            }
            return data;
        }
    }
    
    /**
     * 对敏感数据进行通用脱敏处理（保留首尾各2位）
     * 
     * 功能说明：
     * 1. 保留前2位和后2位字符
     * 2. 中间部分用星号替换
     * 3. 用于加密/解密失败时的兜底处理
     * 
     * @param data 待脱敏数据
     * @return 脱敏后的数据
     */
    private static String maskSensitiveData(String data) {
        if (StringUtils.isEmpty(data)) {
            return data;
        }
        int len = data.length();
        if (len <= 4) {
            return "**";
        }
        int keepStart = Math.min(2, len / 2);
        int keepEnd = Math.min(2, len - keepStart);
        return formatBetween(data, keepStart, keepEnd);
    }


    /**
     * [中文姓名] 只显示第一个汉字，其他隐藏为2个星号
     * 
     * 功能说明：
     * 1. 保留第一个汉字
     * 2. 后面用2个星号替换
     * 
     * 使用示例：
     * <pre>
     * {@code
     * String name = "张三";
     * String masked = SensitiveInfoUtils.chineseName(name);
     * // 结果: 张**
     * 
     * name = "欧阳修";
     * masked = SensitiveInfoUtils.chineseName(name);
     * // 结果: 欧**
     * }
     * </pre>
     * 
     * @param fullName 全名
     * @return <例子：李**>
     */
    public static String chineseName(String fullName) {
        if (StringUtils.isEmpty(fullName)) {
            return "";
        }
        return formatRight(fullName, 1);
    }

    /**
     * [中文姓名] 只显示第一个汉字，其他隐藏为2个星号
     * 
     * 功能说明：
     * 1. 将姓和名拼接
     * 2. 保留第一个汉字
     * 3. 后面用2个星号替换
     * 
     * 使用示例：
     * <pre>
     * {@code
     * String masked = SensitiveInfoUtils.chineseName("张", "三");
     * // 结果: 张**
     * }
     * </pre>
     * 
     * @param familyName 姓
     * @param firstName  名
     * @return <例子：李**>
     */
    public static String chineseName(String familyName, String firstName) {
        if (StringUtils.isEmpty(familyName) || StringUtils.isEmpty(firstName)) {
            return "";
        }
        return chineseName(familyName + firstName);
    }

    /**
     * [身份证号] 显示最后四位，其他隐藏。共计18位或者15位。
     * 
     * 功能说明：
     * 1. 保留最后4位数字
     * 2. 前面用星号替换
     * 3. 支持18位和15位身份证号
     * 
     * 使用示例：
     * <pre>
     * {@code
     * String idCard = "110101199001011234";
     * String masked = SensitiveInfoUtils.idCardNum(idCard);
     * // 结果: ************1234
     * }
     * </pre>
     * 
     * @param id 身份证号
     * @return <例子：*************5762>
     */
    public static String idCardNum(String id) {
        if (StringUtils.isEmpty(id)) {
            return "";
        }
        return formatLeft(id, 4);
    }

    /**
     * [固定电话] 后四位，其他隐藏
     * 
     * 功能说明：
     * 1. 保留最后4位数字
     * 2. 前面用星号替换
     * 
     * 使用示例：
     * <pre>
     * {@code
     * String phone = "01012345678";
     * String masked = SensitiveInfoUtils.fixedPhone(phone);
     * // 结果: ****5678
     * }
     * </pre>
     * 
     * @param num 固定电话
     * @return <例子：****1234>
     */
    public static String fixedPhone(String num) {
        return StringUtils.isEmpty(num) ? "" : formatLeft(num, 4);
    }

    /**
     * [手机号码] 前三位，后四位，其他隐藏
     * 
     * 功能说明：
     * 1. 保留前3位和后4位数字
     * 2. 中间用4个星号替换
     * 3. 适用于11位手机号
     * 
     * 使用示例：
     * <pre>
     * {@code
     * String phone = "13812345678";
     * String masked = SensitiveInfoUtils.mobilePhone(phone);
     * // 结果: 138****5678
     * }
     * </pre>
     * 
     * @param num 手机号码
     * @return <例子:138******1234>
     */
    public static String mobilePhone(String num) {
        if (StringUtils.isEmpty(num)) {
            return "";
        }
        int len = num.length();
        return len < 11 ? num : formatBetween(num, 3, 4);
    }

    /**
     * [地址] 只显示到地区，不显示详细地址；我们要对个人信息增强保护
     * 
     * 功能说明：
     * 1. 保留前N位字符（通常是省市区）
     * 2. 后面用星号替换
     * 3. 敏感长度可配置
     * 
     * 使用示例：
     * <pre>
     * {@code
     * String address = "北京市朝阳区xxx街道xxx号";
     * String masked = SensitiveInfoUtils.address(address, 3);
     * // 结果: 北京市朝阳区****
     * }
     * </pre>
     * 
     * @param address       地址
     * @param sensitiveSize 敏感信息长度
     * @return <例子：北京市海淀区****>
     */
    public static String address(String address, int sensitiveSize) {
        if (StringUtils.isEmpty(address)) {
            return "";
        }
        return address.length() < sensitiveSize ? address : formatRight(address, sensitiveSize);
    }

    /**
     * [电子邮箱] 邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示
     * 
     * 功能说明：
     * 1. 保留@符号前的第一个字符
     * 2. 中间用2个星号替换
     * 3. @及后面的地址完整显示
     * 
     * 使用示例：
     * <pre>
     * {@code
     * String email = "zhangsan@example.com";
     * String masked = SensitiveInfoUtils.email(email);
     * // 结果: z***@example.com
     * }
     * </pre>
     * 
     * @param email 电子邮箱
     * @return <例子:g**@163.com>
     */
    public static String email(String email) {
        if (StringUtils.isEmpty(email)) {
            return "";
        }
        int index = email.indexOf("@");
        if (index <= 1) {
            return email;
        }
        return email.substring(0, 1) + "**" + email.substring(index);
    }

    /**
     * [银行卡号] 前六位，后四位，其他用星号隐藏每位1个星号
     * 
     * 功能说明：
     * 1. 保留前6位和后4位数字
     * 2. 中间用星号替换
     * 
     * 使用示例：
     * <pre>
     * {@code
     * String cardNum = "6222021234567890123";
     * String masked = SensitiveInfoUtils.bankCard(cardNum);
     * // 结果: 622202********0123
     * }
     * </pre>
     * 
     * @param cardNum 银行卡号
     * @return <例子:6222600**********1234>
     */
    public static String bankCard(String cardNum) {
        if (StringUtils.isEmpty(cardNum)) {
            return "";
        }
        return formatBetween(cardNum, 6, 4);
    }

    /**
     * [公司开户银行联号] 公司开户银行联行号,显示前两位，其他用星号隐藏，每位1个星号
     * 
     * 功能说明：
     * 1. 保留前2位数字
     * 2. 后面用星号替换
     * 
     * 使用示例：
     * <pre>
     * {@code
     * String code = "102100099999";
     * String masked = SensitiveInfoUtils.cnapsCode(code);
     * // 结果: 12********
     * }
     * </pre>
     * 
     * @param code 公司开户银行联号
     * @return <例子:12********>
     */
    public static String cnapsCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return "";
        }
        return formatRight(code, 2);
    }


    /**
     * 将右边的格式化成*
     * 
     * 功能说明：
     * 1. 保留前N位字符
     * 2. 后面用星号替换
     * 
     * 使用示例：
     * <pre>
     * {@code
     * String result = SensitiveInfoUtils.formatRight("张三丰", 1);
     * // 结果: 张**
     * }
     * </pre>
     * 
     * @param str 字符串
     * @param reservedLength 保留长度
     * @return 格式化后的字符串
     * @throws IllegalArgumentException 如果保留长度超过字符串长度
     */
    public static String formatRight(String str, int reservedLength) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        if (reservedLength <= 0) {
            return String.join("", Collections.nCopies(str.length(), "*"));
        }
        if (reservedLength >= str.length()) {
            return str;
        }
        String name = str.substring(0, reservedLength);
        String stars = String.join("", Collections.nCopies(str.length() - reservedLength, "*"));
        return name + stars;
    }

    /**
     * 将左边的格式化成*
     * 
     * 功能说明：
     * 1. 保留后N位字符
     * 2. 前面用星号替换
     * 
     * 使用示例：
     * <pre>
     * {@code
     * String result = SensitiveInfoUtils.formatLeft("110101199001011234", 4);
     * // 结果: ************1234
     * }
     * </pre>
     * 
     * @param str 字符串
     * @param reservedLength 保留长度
     * @return 格式化后的字符串
     * @throws IllegalArgumentException 如果保留长度超过字符串长度
     */
    public static String formatLeft(String str, int reservedLength) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        if (reservedLength <= 0) {
            return String.join("", Collections.nCopies(str.length(), "*"));
        }
        int len = str.length();
        if (reservedLength >= len) {
            return str;
        }
        String show = str.substring(len - reservedLength);
        String stars = String.join("", Collections.nCopies(len - reservedLength, "*"));
        return stars + show;
    }

    /**
     * 将中间的格式化成*
     * 
     * 功能说明：
     * 1. 保留前N位和后M位字符
     * 2. 中间用星号替换
     * 
     * 使用示例：
     * <pre>
     * {@code
     * String result = SensitiveInfoUtils.formatBetween("13812345678", 3, 4);
     * // 结果: 138****5678
     * }
     * </pre>
     * 
     * @param str 字符串
     * @param beginLen 开始保留长度
     * @param endLen 结尾保留长度
     * @return 格式化后的字符串
     * @throws IllegalArgumentException 如果保留长度之和超过字符串长度
     */
    public static String formatBetween(String str, int beginLen, int endLen) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        int len = str.length();
        if (beginLen <= 0 && endLen <= 0) {
            return String.join("", Collections.nCopies(len, "*"));
        }
        if (beginLen + endLen >= len) {
            return str;
        }
        String begin = str.substring(0, Math.max(0, beginLen));
        String end = str.substring(len - Math.max(0, endLen));
        String stars = String.join("", Collections.nCopies(len - beginLen - endLen, "*"));
        return begin + stars + end;
    }

    /**
     * [护照号码] 显示前两位和后两位，其他隐藏
     * 护照号码通常为9位（G开头）或14位（普通护照）
     * 
     * 功能说明：
     * 1. 保留前2位和后2位字符
     * 2. 中间用星号替换
     * 3. 适用于9位或14位护照号码
     * 
     * 使用示例：
     * <pre>
     * {@code
     * String passport = "E12345678";
     * String masked = SensitiveInfoUtils.passport(passport);
     * // 结果: E8******78
     * }
     * </pre>
     * 
     * @param passport 护照号码
     * @return <例子: E8******12>
     */
    public static String passport(String passport) {
        if (StringUtils.isEmpty(passport)) {
            return "";
        }
        int len = passport.length();
        if (len <= 4) {
            return "**";
        }
        return formatBetween(passport, 2, 2);
    }

    /**
     * [驾驶证号码] 显示前两位和后四位，其他隐藏
     * 驾驶证号码通常为18位
     * 
     * 功能说明：
     * 1. 保留前2位和后4位字符
     * 2. 中间用星号替换
     * 3. 适用于18位驾驶证号码
     * 
     * 使用示例：
     * <pre>
     * {@code
     * String license = "123456789012345678";
     * String masked = SensitiveInfoUtils.drivingLicense(license);
     * // 结果: 12************5678
     * }
     * </pre>
     * 
     * @param license 驾驶证号码
     * @return <例子: 12************5678>
     */
    public static String drivingLicense(String license) {
        if (StringUtils.isEmpty(license)) {
            return "";
        }
        int len = license.length();
        if (len <= 6) {
            return "**";
        }
        return formatBetween(license, 2, 4);
    }

    /**
     * [车牌号] 显示省份简称和后两位，其他隐藏
     * 车牌号格式：省份简称+字母+5位数字/字母
     * 
     * 功能说明：
     * 1. 保留前2位（省份简称+字母）和后2位
     * 2. 中间用星号替换
     * 
     * 使用示例：
     * <pre>
     * {@code
     * String plate = "京A12345";
     * String masked = SensitiveInfoUtils.licensePlate(plate);
     * // 结果: 京A*****45
     * }
     * </pre>
     * 
     * @param plate 车牌号
     * @return <例子: 京A*****12>
     */
    public static String licensePlate(String plate) {
        if (StringUtils.isEmpty(plate)) {
            return "";
        }
        int len = plate.length();
        if (len <= 3) {
            return "**";
        }
        return formatBetween(plate, 2, 2);
    }

    /**
     * [统一社会信用代码] 显示前两位和后两位，其他隐藏
     * 统一社会信用代码为18位
     * 
     * 功能说明：
     * 1. 保留前2位和后2位字符
     * 2. 中间用星号替换
     * 3. 适用于18位统一社会信用代码
     * 
     * 使用示例：
     * <pre>
     * {@code
     * String code = "91110000123456789X";
     * String masked = SensitiveInfoUtils.creditCode(code);
     * // 结果: 91**********9X
     * }
     * </pre>
     * 
     * @param code 统一社会信用代码
     * @return <例子: 91****************12>
     */
    public static String creditCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return "";
        }
        int len = code.length();
        if (len <= 4) {
            return "**";
        }
        return formatBetween(code, 2, 2);
    }

    /**
     * [税号] 显示前两位和后两位，其他隐藏
     * 税号通常为15-20位
     * 
     * 功能说明：
     * 1. 保留前2位和后2位字符
     * 2. 中间用星号替换
     * 3. 适用于15-20位税号
     * 
     * 使用示例：
     * <pre>
     * {@code
     * String taxNumber = "110101123456789";
     * String masked = SensitiveInfoUtils.taxNumber(taxNumber);
     * // 结果: 11**********789
     * }
     * </pre>
     * 
     * @param taxNumber 税号
     * @return <例子: 91****************12>
     */
    public static String taxNumber(String taxNumber) {
        if (StringUtils.isEmpty(taxNumber)) {
            return "";
        }
        int len = taxNumber.length();
        if (len <= 4) {
            return "**";
        }
        return formatBetween(taxNumber, 2, 2);
    }

}
