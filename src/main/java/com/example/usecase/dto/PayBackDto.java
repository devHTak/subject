package com.example.usecase.dto;

import com.example.usecase.dto.constant.PayBackRequestType;
import com.example.usecase.dto.constant.PayBackType;

import java.time.LocalDateTime;

public class PayBackDto {
    private Long paymentId;
    private Long payBackId;
    private PayBackRequestType payBackRequestType;
    private PayBackType payBackType;
    private int payBackAmount;
    private LocalDateTime payBackAt;

    public PayBackDto() {
    }

    public PayBackDto(Long payBackId, PayBackType payBackType, int payBackAmount) {
        this.payBackId = payBackId;
        this.payBackType = payBackType;
        this.payBackAmount = payBackAmount;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getPayBackId() {
        return payBackId;
    }

    public void setPayBackId(Long payBackId) {
        this.payBackId = payBackId;
    }

    public PayBackRequestType getPayBackRequestType() {
        return payBackRequestType;
    }

    public void setPayBackRequestType(PayBackRequestType payBackRequestType) {
        this.payBackRequestType = payBackRequestType;
    }

    public PayBackType getPayBackType() {
        return payBackType;
    }

    public void setPayBackType(PayBackType payBackType) {
        this.payBackType = payBackType;
    }

    public int getPayBackAmount() {
        return payBackAmount;
    }

    public void setPayBackAmount(int payBackAmount) {
        this.payBackAmount = payBackAmount;
    }

    public LocalDateTime getPayBackAt() {
        return payBackAt;
    }

    public void setPayBackAt(LocalDateTime payBackAt) {
        this.payBackAt = payBackAt;
    }
}
