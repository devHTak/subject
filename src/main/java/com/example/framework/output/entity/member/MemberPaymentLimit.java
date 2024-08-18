package com.example.framework.output.entity.member;

import com.example.usecase.dto.MemberPaymentLimitDto;
import com.example.usecase.dto.constant.PaymentLimitType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class MemberPaymentLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Enumerated(value = EnumType.STRING)
    private PaymentLimitType paymentLimitType;

    private Integer limitAmount;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public MemberPaymentLimit() {
    }

    public MemberPaymentLimit(PaymentLimitType paymentLimitType, Integer limitAmount) {
        this.paymentLimitType = paymentLimitType;
        this.limitAmount = limitAmount;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public PaymentLimitType getPaymentLimitType() {
        return paymentLimitType;
    }

    public Integer getLimitAmount() {
        return limitAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void saveMember(Member member) {
        this.member = member;
    }

    public MemberPaymentLimitDto convertToDto() {
        MemberPaymentLimitDto memberPaymentLimitDto = new MemberPaymentLimitDto();
        memberPaymentLimitDto.setMemberPaymentLimitId(this.id);
        memberPaymentLimitDto.setPaymentLimitType(this.paymentLimitType);
        memberPaymentLimitDto.setLimitAmount(this.limitAmount);
        memberPaymentLimitDto.setMemberId(this.member.getId());
        return memberPaymentLimitDto;
    }
}
