package com.example.usecase.dto.constant;

import com.example.usecase.dto.MemberPaymentLimitDto;

import java.util.List;

public enum PaymentLimitType {
    ONE_TIME,
    ONE_DAY,
    THIRTY_DAY;

    public Integer findLimitAmount(List<MemberPaymentLimitDto> paymentLimits) {
        return paymentLimits.stream()
                .filter(paymentLimit -> this.equals(paymentLimit.getPaymentLimitType()))
                .map(paymentLimit -> paymentLimit.getLimitAmount())
                .findFirst().orElse(0);
    }
}
