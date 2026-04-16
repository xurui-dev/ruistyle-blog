package com.xr.ruistyle.interceptor;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.xr.ruistyle.common.BusinessException;
import com.xr.ruistyle.common.ResultCodeEnum;
import com.xr.ruistyle.utils.JwtUtils;

/**
 * JWT 登录拦截器
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 放行 OPTIONS 请求 (处理前后端分离的跨域预检请求)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 从请求头中获取 token (通常前端会放在 Authorization 头，或者自定义的 token 头中)
        String token = request.getHeader("token");
        // 兼容一下标准的 Bearer Token 格式
        if (StrUtil.isBlank(token)) {
            token = request.getHeader("Authorization");
            if (StrUtil.isNotBlank(token) && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
        }

        // 3. 校验 Token 是否存在
        if (StrUtil.isBlank(token)) {
            log.warn("拦截到未携带Token的请求: {}", request.getRequestURI());
            throw new BusinessException(401, "请先登录系统");
        }

        // 4. 校验 Token 的合法性
        if (!JwtUtils.verifyToken(token)) {
            log.warn("拦截到无效或过期的Token请求: {}", request.getRequestURI());
            throw new BusinessException(401, "登录已过期，请重新登录");
        }

        // 5. 将解析出的 userId 放入 request 中，方便后续 Controller 直接获取
        Long userId = JwtUtils.getUserId(token);
        request.setAttribute("currentUserId", userId);

        // 6. 验证通过，放行请求！
        return true;
    }
}