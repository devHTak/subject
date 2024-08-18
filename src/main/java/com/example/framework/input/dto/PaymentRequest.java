package com.example.framework.input.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull @Min(0) Long memberId,
        @Min(1) Integer paymentAmount
) {
}
