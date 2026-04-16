package com.xr.ruistyle.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RuiStyle API 接口文档")
                        .version("1.0")
                        .description("基于 Spring Boot 3 + Vue 3 的全栈博客系统"))
                // 配置全局安全校验
                .addSecurityItem(new SecurityRequirement().addList("token"))
                .components(new Components()
                        // 定义名为 "token" 的请求头参数
                        .addSecuritySchemes("token", new SecurityScheme()
                                .name("token")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)));
    }
}