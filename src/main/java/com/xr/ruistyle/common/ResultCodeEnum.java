package com.xr.ruistyle.common;

import lombok.Getter;

/**
 * 统一状态码枚举
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(200, "操作成功"),
    ERROR(500, "系统异常，请稍后重试"),
    PARAM_ERROR(400, "参数校验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限");

    private final Integer code;
    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}