package com.xr.ruistyle.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xr.ruistyle.common.BusinessException;
import com.xr.ruistyle.dto.LoginRequest;
import com.xr.ruistyle.entity.User;
import com.xr.ruistyle.mapper.UserMapper;
import com.xr.ruistyle.service.UserService;
import com.xr.ruistyle.utils.JwtUtils;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public String login(LoginRequest loginRequest) {
        // 1. 根据用户名查询用户信息
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, loginRequest.getUsername()));

        // 2. 判断用户是否存在
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 3. 校验密码 (使用 BCrypt 进行安全匹配)
        // 参数1：前端传来的明文，参数2：数据库里的加密串
        if (!BCrypt.checkpw(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 4. 验证通过，颁发 Token
        return JwtUtils.generateToken(user.getId(), user.getUsername());
    }
}