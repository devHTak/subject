package com.example.usecase.exception;

public enum ExceptionCode {
    NOT_JOIN_MEMBER(400, "회원가입이 안되어 있거나 없는 회원입니다."),
    NO_PAYMENT(400, "결재 내역이 이미 취소되었거나 조회되지 않습니다."),
    ONE_TIME_LIMIT(400, "1회 한도 제한 초과되었습니다."),
    ONE_DAY_LIMIT(400, "1일 한도 제한 초과되었습니다."),
    THIRTY_DAY_LIMIT(400, "30일 한도 제한 초과되었습니다."),
    EXCEEDED_STORAGE_LMIIT(400, "저장 금액 한도 초과되었습니다."),
    NOT_EXPIRE_TIME_REQUEST(400, "결제 및 페이백에 대한 요청은 5초 이후에 가능합니다."),
    NO_LOCK_KEY_IN_REQUEST(400, "요청에 락에 사용되는 키값이 없습니다."),
    HAS_PAY_BACK(400, "이미 처리된 페이백이 있습니다."),
    NO_HAS_PAY_BACK(400, "처리된 페이백이 없습니다.")
    ;

    private int statusCode;
    private String message;

    ExceptionCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
