package com.example.usecase.exception;

public class PaymentBizException extends RuntimeException {

    private ExceptionCode exceptionCode;

    public PaymentBizException(String message) {
        super(message);
    }

    public PaymentBizException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
