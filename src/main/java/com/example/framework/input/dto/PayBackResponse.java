package com.example.framework.input.dto;

import java.time.LocalDateTime;

public record PayBackResponse(
        Long payBackId,
        String payBackType,
        Integer payBackAmount
) {
}
