package com.example.framework.input.exception;

import com.example.framework.input.dto.ApiResponse;
import com.example.usecase.exception.PaymentBizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PaymentExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(PaymentExceptionHandler.class);

    @ExceptionHandler(PaymentBizException.class)
    public ApiResponse<String> paymentBizExceptionHandler(PaymentBizException e) {
        log.error("PaymentBizException: {}", e.getMessage());
        return new ApiResponse<>(e.getExceptionCode().getStatusCode(), e.getExceptionCode().getMessage(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<String> validationExceptionHandler(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();

        StringBuilder errorMessage = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessage.append("[");
            errorMessage.append(fieldError.getField());
            errorMessage.append("]");
            errorMessage.append(fieldError.getDefaultMessage());
        }

        return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage.toString(), errorMessage.toString());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<String> exceptionHandler(Exception e) {
        log.error("Exception: {}", e.getMessage());
        return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), e.getMessage());
    }
}
