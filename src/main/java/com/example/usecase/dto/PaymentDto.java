package com.example.usecase.dto;

import com.example.usecase.dto.constant.PaymentType;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentDto {
    private Long paymentId;
    private Long memberId;
    private PaymentType paymentType;
    private LocalDateTime paymentAt;
    private Integer amount;
    private MemberDto member;
    private List<PayBackDto> payBacks;

    public PaymentDto() {
    }

    public PaymentDto(Long paymentId, Long memberId, PaymentType paymentType, LocalDateTime paymentAt, Integer amount) {
        this.paymentId = paymentId;
        this.memberId = memberId;
        this.paymentType = paymentType;
        this.paymentAt = paymentAt;
        this.amount = amount;
    }

    public Long getPaymentId() { return paymentId; }

    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public LocalDateTime getPaymentAt() {
        return paymentAt;
    }

    public void setPaymentAt(LocalDateTime paymentAt) {
        this.paymentAt = paymentAt;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public MemberDto getMember() {
        return member;
    }

    public void setMember(MemberDto member) {
        this.member = member;
    }

    public List<PayBackDto> getPayBacks() {
        return payBacks;
    }

    public void setPayBacks(List<PayBackDto> payBacks) {
        this.payBacks = payBacks;
    }
}
