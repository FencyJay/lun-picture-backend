package com.danta.lunpicrurebackend.exception;

import com.danta.lunpicrurebackend.common.BaseResponse;
import com.danta.lunpicrurebackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e){
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> businessExceptionHandler(RuntimeException e){
        log.error("BusinessException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"系统错误");
    }

}
