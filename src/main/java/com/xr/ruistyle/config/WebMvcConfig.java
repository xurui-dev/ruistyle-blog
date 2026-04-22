package com.xr.ruistyle.config;

import com.xr.ruistyle.interceptor.JwtInterceptor; // 导入拦截器
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private JwtInterceptor jwtInterceptor; // 注入我们的保安

    // 之前配置图片映射的方法保留不动
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadPath);
    }

    // ====== 修改：配置拦截器规则 ======
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有 /api/ 开头的请求
                .addPathPatterns("/api/**")
                // 现在只需要排除登录接口即可！其他的交给 JwtInterceptor 内部去判断
                .excludePathPatterns("/api/auth/login"
                        ,"/api/ai/chat"
                );

    }
}
//    // ====== 新增：配置拦截器规则 ======
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(jwtInterceptor)
//                // 拦截所有 /api/ 开头的请求
//                .addPathPatterns("/api/**")
//                // 排除不需要登录就能访问的接口（比如：看文章详情、看文章列表、上传图片暂不拦截）
//                .excludePathPatterns(
//                        "/api/auth/login",  // 放行登录接口
//                        "/api/articles",
//                        "/api/articles/*",
//                        "/api/categories",
//                        "/api/tags"
//                );
//    }
