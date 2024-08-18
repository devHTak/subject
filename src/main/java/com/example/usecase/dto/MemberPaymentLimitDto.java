package com.example.usecase.dto;

import com.example.usecase.dto.constant.PaymentLimitType;

public class MemberPaymentLimitDto {
    private Long memberId;
    private Long memberPaymentLimitId;
    private PaymentLimitType paymentLimitType;
    private Integer limitAmount;

    public MemberPaymentLimitDto() {
    }

    public MemberPaymentLimitDto(Long memberId, Long memberPaymentLimitId, PaymentLimitType paymentLimitType, Integer limitAmount) {
        this.memberId = memberId;
        this.memberPaymentLimitId = memberPaymentLimitId;
        this.paymentLimitType = paymentLimitType;
        this.limitAmount = limitAmount;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberPaymentLimitId() {
        return memberPaymentLimitId;
    }

    public void setMemberPaymentLimitId(Long memberPaymentLimitId) {
        this.memberPaymentLimitId = memberPaymentLimitId;
    }

    public PaymentLimitType getPaymentLimitType() {
        return paymentLimitType;
    }

    public void setPaymentLimitType(PaymentLimitType paymentLimitType) {
        this.paymentLimitType = paymentLimitType;
    }

    public Integer getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(Integer limitAmount) {
        this.limitAmount = limitAmount;
    }
}
