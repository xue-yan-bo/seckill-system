package com.seckill.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * MD5加密工具类
 */
public class MD5Util {
    
    private static final String SALT = "seckill_salt_2024";
    
    /**
     * MD5加密
     */
    public static String md5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() == 1) {
                    result.append("0");
                }
                result.append(hex);
            }
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }
    
    /**
     * 密码加密（带盐值）
     */
    public static String encryptPassword(String password) {
        return md5(password + SALT);
    }
    
    /**
     * 验证密码
     */
    public static boolean verifyPassword(String inputPassword, String encryptedPassword) {
        return encryptPassword(inputPassword).equals(encryptedPassword);
    }
}

