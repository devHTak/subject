package com.example.framework.input.dto;

public record PaymentResponse(
        Long paymentId,
        Long memberId,
        String paymentType,
        Integer paymentAmount
) {
}
