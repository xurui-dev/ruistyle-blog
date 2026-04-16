package com.xr.ruistyle.common;
import lombok.Data;

/**
 * 全局统一返回结果类
 */
@Data
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    // 私有化构造方法，强制通过静态方法创建
    private Result() {}

    // 成功（无返回数据）
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMessage(ResultCodeEnum.SUCCESS.getMessage());
        return result;
    }

    // 成功（有返回数据）
    public static <T> Result<T> success(T data) {
        Result<T> result = success();
        result.setData(data);
        return result;
    }

    // 失败（使用内置枚举）
    public static <T> Result<T> fail(ResultCodeEnum resultCodeEnum) {
        Result<T> result = new Result<>();
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    // 失败（自定义错误信息，常用于业务异常）
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}