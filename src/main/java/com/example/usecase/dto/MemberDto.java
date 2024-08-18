package com.example.usecase.dto;

import java.util.List;

public class MemberDto {
    private Long memberId;
    private String name;
    private String joinYn;
    private Integer ownedAmount;
    private List<MemberPaymentLimitDto> paymentLimits;

    public MemberDto() {}

    public MemberDto(Long memberId, String name, String joinYn, List<MemberPaymentLimitDto> paymentLimits) {
        this.memberId = memberId;
        this.name = name;
        this.joinYn = joinYn;
        this.paymentLimits = paymentLimits;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJoinYn() {
        return joinYn;
    }

    public void setJoinYn(String joinYn) {
        this.joinYn = joinYn;
    }

    public List<MemberPaymentLimitDto> getPaymentLimits() {
        return paymentLimits;
    }

    public void setPaymentLimits(List<MemberPaymentLimitDto> paymentLimits) {
        this.paymentLimits = paymentLimits;
    }

    public Integer getOwnedAmount() {
        return ownedAmount;
    }

    public void setOwnedAmount(Integer ownedAmount) {
        this.ownedAmount = ownedAmount;
    }
}
