package com.xr.ruistyle.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xr.ruistyle.dto.LoginRequest;
import com.xr.ruistyle.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 管理员登录
     * @param loginRequest 包含用户名和密码的请求对象
     * @return 登录成功后返回的 JWT Token 字符串
     */
    String login(LoginRequest loginRequest);
}