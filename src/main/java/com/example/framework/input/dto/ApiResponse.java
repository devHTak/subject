package com.example.framework.input.dto;

public record ApiResponse<T>(
        int status,
        String message,
        T data
) {
}
