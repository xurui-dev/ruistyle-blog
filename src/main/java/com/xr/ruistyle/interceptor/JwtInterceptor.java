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

        // 🌟 1. 无条件放行所有 OPTIONS 请求（解决浏览器跨域预检报错问题）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 🌟 2. 无条件放行所有 GET 请求（因为我们博客的 GET 都是公开给读者的查询操作）
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // ==========================================
        // 走到这里的，只剩下 POST、PUT、DELETE 等需要权限的写操作了
        // ==========================================

        // 3. 尝试从不同的 Header 中获取 token (优先取自定义的 token 头)
        String token = request.getHeader("token");

        // 如果 token 头里没有，再去兼容一下标准的 Authorization: Bearer 格式
        if (StrUtil.isBlank(token)) {
            token = request.getHeader("Authorization");
            if (StrUtil.isNotBlank(token) && token.startsWith("Bearer ")) {
                token = token.substring(7); // 截取掉 "Bearer " 前缀
            }
        }

        // 4. 终极校验：判断提取出来的 Token 是否存在
        if (StrUtil.isBlank(token)) {
            log.warn("拦截到未携带Token的危险请求: {}", request.getRequestURI());
            throw new BusinessException(401, "请先登录系统");
        }

        // 5. 校验 Token 的合法性 (是否被篡改或过期)
        if (!JwtUtils.verifyToken(token)) {
            log.warn("拦截到无效或过期的Token请求: {}", request.getRequestURI());
            throw new BusinessException(401, "登录已过期，请重新登录");
        }

        // 6. 将解析出的 userId 放入 request 中，方便后续 Controller 直接获取
        Long userId = JwtUtils.getUserId(token);
        request.setAttribute("currentUserId", userId);

        // 7. 验证通过，放行请求！
        return true;
    }
}