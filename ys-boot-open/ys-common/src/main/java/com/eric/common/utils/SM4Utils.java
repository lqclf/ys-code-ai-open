package com.eric.common.utils;

import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * 国密加密 SM4 工具类（CBC 模式）
 * 加密时自动生成随机 IV，并将 IV 拼接到密文前，最终输出 Base64 编码
 * @author Eric
 * @date 2025-06-16
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
public class SM4Utils {

    private static final int BLOCK_SIZE = 16; // SM4 块大小（16 字节）
    private static final int IV_LENGTH = 16;  // IV 长度（16 字节）

    /**
     * 随机生成 SM4 密钥（16 字节，32 位十六进制字符串）
     * @return 十六进制密钥字符串
     */
    public static String createKey() {
        byte[] key = new byte[BLOCK_SIZE];
        new SecureRandom().nextBytes(key);
        return Hex.toHexString(key);
    }

    /**
     * 校验密钥是否合法（16 字节）
     * @param hexKey 十六进制密钥字符串
     * @throws IllegalArgumentException 密钥长度不合法
     */
    private static void validateKey(String hexKey) {
        if (hexKey == null || hexKey.length() != 2 * BLOCK_SIZE) {
            throw new IllegalArgumentException("密钥必须为 " + (2 * BLOCK_SIZE) + " 位十六进制字符串");
        }
    }

    /**
     * CBC 模式加密（自动生成随机 IV）
     * 加密流程：生成随机 IV → CBC 加密 → 将 IV 拼接到密文前 → Base64 编码
     * @param plainText 明文字符串
     * @param hexKey 16 字节 SM4 密钥（32 位十六进制）
     * @return Base64 编码的密文（IV + 密文）
     * @throws IllegalArgumentException 密钥或输入格式不合法
     */
    public static String encrypt(String plainText, String hexKey) {
        try {
            validateKey(hexKey);
            byte[] keyBytes = Hex.decode(hexKey);
            // 生成随机 IV
            byte[] ivBytes = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(ivBytes);
            byte[] input = plainText.getBytes(StandardCharsets.UTF_8);

            // 创建 CBC 模式加密器（自带 PKCS7 填充）
            PaddedBufferedBlockCipher cbc = new PaddedBufferedBlockCipher(
                    new CBCBlockCipher(new SM4Engine()), new PKCS7Padding());
            cbc.init(true, new ParametersWithIV(new KeyParameter(keyBytes), ivBytes));

            // 执行加密
            byte[] encrypted = new byte[cbc.getOutputSize(input.length)];
            int len = cbc.processBytes(input, 0, input.length, encrypted, 0);
            len += cbc.doFinal(encrypted, len);

            // 将 IV 拼接到密文前
            byte[] result = new byte[IV_LENGTH + len];
            System.arraycopy(ivBytes, 0, result, 0, IV_LENGTH);
            System.arraycopy(encrypted, 0, result, IV_LENGTH, len);

            return Base64.toBase64String(result);
        } catch (Exception e) {
            throw new IllegalArgumentException("加密失败: " + e.getMessage(), e);
        }
    }

    /**
     * CBC 模式解密
     * 解密流程：Base64 解码 → 取出前 16 字节 IV → CBC 解密剩余部分
     * @param cipherText Base64 编码的密文（IV + 密文）
     * @param hexKey 16 字节 SM4 密钥（32 位十六进制）
     * @return 解密后的明文
     * @throws IllegalArgumentException 密钥或输入格式不合法
     */
    public static String decrypt(String cipherText, String hexKey) {
        try {
            validateKey(hexKey);
            byte[] data = Base64.decode(cipherText);
            if (data.length < IV_LENGTH + BLOCK_SIZE) {
                throw new IllegalArgumentException("密文长度不合法");
            }

            byte[] keyBytes = Hex.decode(hexKey);
            // 提取 IV（前 16 字节）
            byte[] ivBytes = new byte[IV_LENGTH];
            byte[] encrypted = new byte[data.length - IV_LENGTH];
            System.arraycopy(data, 0, ivBytes, 0, IV_LENGTH);
            System.arraycopy(data, IV_LENGTH, encrypted, 0, encrypted.length);

            // 创建 CBC 模式解密器（自带 PKCS7 去填充）
            PaddedBufferedBlockCipher cbc = new PaddedBufferedBlockCipher(
                    new CBCBlockCipher(new SM4Engine()), new PKCS7Padding());
            cbc.init(false, new ParametersWithIV(new KeyParameter(keyBytes), ivBytes));

            // 执行解密
            byte[] decrypted = new byte[cbc.getOutputSize(encrypted.length)];
            int len = cbc.processBytes(encrypted, 0, encrypted.length, decrypted, 0);
            len += cbc.doFinal(decrypted, len);

            return new String(decrypted, 0, len, StandardCharsets.UTF_8).trim();
        } catch (Exception e) {
            throw new IllegalArgumentException("解密失败: " + e.getMessage(), e);
        }
    }

    /**
     * CBC 模式加密（支持指定 IV）
     * @param plainText 明文字符串
     * @param hexKey 16 字节 SM4 密钥（32 位十六进制）
     * @param hexIV 16 字节 IV（32 位十六进制）
     * @return 十六进制密文
     * @throws IllegalArgumentException 参数不合法
     */
    public static String encryptCBC(String plainText, String hexKey, String hexIV) {
        try {
            validateKey(hexKey);
            if (hexIV == null || hexIV.length() != 2 * BLOCK_SIZE) {
                throw new IllegalArgumentException("IV 必须为 " + (2 * BLOCK_SIZE) + " 位十六进制字符串");
            }

            byte[] keyBytes = Hex.decode(hexKey);
            byte[] ivBytes = Hex.decode(hexIV);
            byte[] input = plainText.getBytes(StandardCharsets.UTF_8);

            PaddedBufferedBlockCipher cbc = new PaddedBufferedBlockCipher(
                    new CBCBlockCipher(new SM4Engine()), new PKCS7Padding());
            cbc.init(true, new ParametersWithIV(new KeyParameter(keyBytes), ivBytes));

            byte[] encrypted = new byte[cbc.getOutputSize(input.length)];
            int len = cbc.processBytes(input, 0, input.length, encrypted, 0);
            len += cbc.doFinal(encrypted, len);

            byte[] result = new byte[len];
            System.arraycopy(encrypted, 0, result, 0, len);
            return Hex.toHexString(result);
        } catch (Exception e) {
            throw new IllegalArgumentException("CBC 加密失败: " + e.getMessage(), e);
        }
    }

    /**
     * CBC 模式解密（支持指定 IV）
     * @param cipherText 十六进制密文
     * @param hexKey 16 字节 SM4 密钥（32 位十六进制）
     * @param hexIV 16 字节 IV（32 位十六进制）
     * @return 解密后的明文
     * @throws IllegalArgumentException 参数不合法
     */
    public static String decryptCBC(String cipherText, String hexKey, String hexIV) {
        try {
            validateKey(hexKey);
            if (hexIV == null || hexIV.length() != 2 * BLOCK_SIZE) {
                throw new IllegalArgumentException("IV 必须为 " + (2 * BLOCK_SIZE) + " 位十六进制字符串");
            }

            byte[] keyBytes = Hex.decode(hexKey);
            byte[] ivBytes = Hex.decode(hexIV);
            byte[] encrypted = Hex.decode(cipherText);

            PaddedBufferedBlockCipher cbc = new PaddedBufferedBlockCipher(
                    new CBCBlockCipher(new SM4Engine()), new PKCS7Padding());
            cbc.init(false, new ParametersWithIV(new KeyParameter(keyBytes), ivBytes));

            byte[] decrypted = new byte[cbc.getOutputSize(encrypted.length)];
            int len = cbc.processBytes(encrypted, 0, encrypted.length, decrypted, 0);
            len += cbc.doFinal(decrypted, len);

            return new String(decrypted, 0, len, StandardCharsets.UTF_8).trim();
        } catch (Exception e) {
            throw new IllegalArgumentException("CBC 解密失败: " + e.getMessage(), e);
        }
    }

    /**
     * CBC 模式加密，返回 Base64 编码结果（等价于 encrypt）
     * @param plainText 明文字符串
     * @param hexKey 16 字节 SM4 密钥（32 位十六进制）
     * @return Base64 编码的密文（IV + 密文）
     */
    public static String encryptBase64(String plainText, String hexKey) {
        return encrypt(plainText, hexKey);
    }

    /**
     * CBC 模式解密，支持 Base64 编码输入（等价于 decrypt）
     * @param base64CipherText Base64 编码的密文（IV + 密文）
     * @param hexKey 16 字节 SM4 密钥（32 位十六进制）
     * @return 解密后的明文
     */
    public static String decryptBase64(String base64CipherText, String hexKey) {
        return decrypt(base64CipherText, hexKey);
    }
}
