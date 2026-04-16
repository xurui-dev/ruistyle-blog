package com.xr.ruistyle.common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义的业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理由 @Valid 或 @Validated 触发的参数校验异常 (pom中已引入validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        // 获取第一个校验失败的错误信息
        String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
        log.warn("参数校验异常: {}", errorMessage);
        return Result.fail(ResultCodeEnum.PARAM_ERROR.getCode(), errorMessage);
    }

    /**
     * 处理所有未知的系统异常 (兜底方案)
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        // 对于未知异常，打印完整堆栈信息以便排查
        log.error("系统内部异常: ", e);
        return Result.fail(ResultCodeEnum.ERROR);
    }
}
