package com.allure.rap.roze.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtil {
    // Token有效期：Access Token 15分钟，Refresh Token 7天
    private static final long ACCESS_TOKEN_EXPIRE = 15 * 60 * 1000;
    private static final long REFRESH_TOKEN_EXPIRE = 7 * 24 * 60 * 60 * 1000;

    // 非对称密钥对(实际项目中应从安全存储中加载)
    private static final KeyPair keyPair = SecureUtil.generateKeyPair("RSA");
    private static final PrivateKey privateKey = keyPair.getPrivate();
    private static final PublicKey publicKey = keyPair.getPublic();
    private static final JWTSigner signer = JWTSignerUtil.rs256(privateKey);

    /**
     * 生成Access Token (短期访问令牌)
     */
    public static String generateAccessToken(String userId, String username) {
        Map<String, Object> payload = new HashMap<>();
        // 仅包含必要信息，避免敏感数据
        payload.put("sub", userId);
        payload.put("username", username);
        payload.put("type", "access");
        payload.put("tim", new Date());
        payload.put("exp", DateUtil.offsetMillisecond(new Date(), (int) ACCESS_TOKEN_EXPIRE)); // 过期时间

        return JWTUtil.createToken(payload, signer);
    }

    /**
     * 生成Refresh Token (长期刷新令牌)
     */
    public static String generateRefreshToken(String userId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("sub", userId);
        payload.put("type", "refresh");
        payload.put("iat", new Date());
        payload.put("exp", DateUtil.offsetMillisecond(new Date(), (int) REFRESH_TOKEN_EXPIRE));

        return JWTUtil.createToken(payload, signer);
    }

    /**
     * 验证Token有效性
     */
    public static boolean verifyToken(String token) {
        try {
            JWT jwt = JWTUtil.parseToken(token).setSigner(signer);
            return jwt.verify(); // 验证签名和过期时间
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从Token中获取用户ID
     */
    public static Long getUserId(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        return Long.valueOf(jwt.getPayload("sub").toString());
    }

    /**
     * 从Token中获取用户名
     */
    public static String getUsername(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        return jwt.getPayload("username").toString();
    }

    /**
     * 检查是否为Refresh Token
     */
    public static boolean isRefreshToken(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        return "refresh".equals(jwt.getPayload("type"));
    }

}
