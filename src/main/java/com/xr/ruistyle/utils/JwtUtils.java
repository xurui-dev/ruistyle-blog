package com.xr.ruistyle.utils;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类 (基于 Hutool 封装)
 */
@Slf4j
@Component
public class JwtUtils {

    // 🌟 核心秘钥（相当于印钞机的钢印，绝对不能泄露给前端！）
    // 实际生产中建议放在 application.yml 里读取，这里为了方便直接写死
    private static final byte[] SECRET_KEY = "RuiStyle_Blog_Secret_Key_2026".getBytes();

    // Token 过期时间（例如：24小时）
    private static final int EXPIRE_HOURS = 24;

    /**
     * 生成 Token
     * @param userId 用户的 ID
     * @param username 用户的名称
     * @return 经过签名的 JWT 字符串
     */
    public static String generateToken(Long userId, String username) {
        // 计算过期时间：当前时间 + 24小时
        DateTime now = DateTime.now();
        DateTime expireTime = now.offsetNew(DateField.HOUR, EXPIRE_HOURS);

        // 构建 Payload (载荷，即你想存放在 Token 里的公开数据)
        Map<String, Object> payload = new HashMap<>();
        // 签发时间
        payload.put(JWTPayload.ISSUED_AT, now);
        // 过期时间
        payload.put(JWTPayload.EXPIRES_AT, expireTime);
        // 生效时间
        payload.put(JWTPayload.NOT_BEFORE, now);

        // 自定义业务数据
        payload.put("userId", userId);
        payload.put("username", username);

        // 生成签名并返回
        return JWTUtil.createToken(payload, SECRET_KEY);
    }

    /**
     * 验证 Token 并获取验证结果
     * @param token 客户端传来的 token
     * @return 是否有效 (未被篡改且未过期)
     */
    public static boolean verifyToken(String token) {
        try {
            // 1. 验证签名是否正确（防篡改）
            boolean isValid = JWTUtil.verify(token, SECRET_KEY);
            if (!isValid) {
                return false;
            }
            // 2. 验证是否过期
            JWTValidator.of(token).validateDate(DateTime.now());
            return true;
        } catch (ValidateException e) {
            // 如果抛出异常，说明 Token 已经过期
            log.warn("Token 已过期: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            // 其他异常（如格式错误）
            log.warn("Token 解析失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从 Token 中提取 userId
     */
    public static Long getUserId(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        return Long.valueOf(jwt.getPayload("userId").toString());
    }
}