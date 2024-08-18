package com.example.framework.input.dto;

import com.example.usecase.dto.constant.PayBackRequestType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PayBackRequest(
        @NotNull @Min(0) Long memberId,
        PayBackRequestType payBackRequestType
) {
}
