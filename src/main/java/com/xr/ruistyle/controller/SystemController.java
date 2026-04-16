package com.xr.ruistyle.controller;


import com.xr.ruistyle.utils.JwtUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xr.ruistyle.common.BusinessException;
import com.xr.ruistyle.common.Result;
import com.xr.ruistyle.common.ResultCodeEnum;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
@RequestMapping("/sys")
@RequiredArgsConstructor
public class SystemController {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    /**
     * 1. 测试数据库连接
     * 访问地址: http://localhost:8080/sys/db-check
     */
    @GetMapping("/db-check")
    public Result<String> checkDbConnection() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                // 执行一个简单的 SQL 语句进一步确认
                Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                return Result.success("数据库连接成功，测试查询结果：" + result);
            }
        } catch (Exception e) {
            // 如果连接失败，这里会抛出异常，被全局异常处理器捕获
            throw new BusinessException(500, "数据库连接失败: " + e.getMessage());
        }
        return Result.fail(ResultCodeEnum.ERROR);
    }

    /**
     * 2. 测试全局异常处理器是否启用
     * 访问地址: http://localhost:8080/sys/error-check
     */
    @GetMapping("/error-check")
    public Result<Void> checkExceptionHandler() {
        // 主动抛出一个自定义业务异常
        throw new BusinessException(ResultCodeEnum.PARAM_ERROR.getCode(), "看到这条消息说明全局异常处理器已生效！");
    }

    @GetMapping("/jwt-test")
    public Result<String> testJwt() {
        // 1. 模拟登录成功，生成一个带有用户 ID 为 1001，名为 "XuRui" 的 Token
        String token = JwtUtils.generateToken(1001L, "XuRui");
        System.out.println("生成的 Token: " + token);

        // 2. 验证这个 Token 是否合法
        boolean isValid = JwtUtils.verifyToken(token);
        System.out.println("Token 合法性: " + isValid);

        // 3. 从 Token 里把 userId 挖出来
        Long userId = JwtUtils.getUserId(token);
        System.out.println("解析出的 UserId: " + userId);

        return Result.success(token);
    }

    @GetMapping("/get-pwd")
    public Result<String> getPwd() {
        // 使用 Hutool 的 BCrypt 生成 123456 的密文
        String hash = cn.hutool.crypto.digest.BCrypt.hashpw("123456", cn.hutool.crypto.digest.BCrypt.gensalt());
        return Result.success("123456的正确密文是: " + hash);
    }
}
