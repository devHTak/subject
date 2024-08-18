package com.example.framework.output.entity.pay;

import com.example.framework.output.entity.member.Member;
import com.example.usecase.dto.MemberDto;
import com.example.usecase.dto.PayBackDto;
import com.example.usecase.dto.PaymentDto;
import com.example.usecase.dto.constant.PaymentType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToMany(mappedBy = "payment")
    private List<PayBack> payBacks = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    private PaymentType paymentType;

    private Integer paymentAmount;

    private LocalDateTime paymentAt;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public Payment() {
    }

    public Payment(Long id) {
        this.id = id;
    }

    public Payment(Member member, PaymentType paymentType, Integer paymentAmount, LocalDateTime paymentAt) {
        this.member = member;
        this.paymentType = paymentType;
        this.paymentAmount = paymentAmount;
        this.paymentAt = paymentAt;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public List<PayBack> getPayBacks() {
        return payBacks;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public Integer getPaymentAmount() {
        return paymentAmount;
    }

    public LocalDateTime getPaymentAt() {
        return paymentAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void addPayback(PayBack payBack) {
        if(this.payBacks == null) {
            payBacks = new ArrayList<>();
        }
        this.payBacks.add(payBack);
        payBack.updatePayment(this);
    }

    public void removePayBack(PayBack payBack) {
        if(this.payBacks.contains(payBack)) {
            this.payBacks.remove(payBack);
            payBack.updatePayment(null);
        }
    }

    public PaymentDto convertToDto() {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentId(this.id);
        paymentDto.setMemberId(this.member.getId()); // N+1 이슈 확인 필요
        paymentDto.setPaymentAt(this.paymentAt);
        paymentDto.setAmount(this.paymentAmount);
        paymentDto.setPaymentType(this.paymentType);
        return paymentDto;
    }

    public PaymentDto convertToDtoWithMember() {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentId(this.id);
        paymentDto.setMemberId(this.member.getId()); // N+1 이슈 확인 필요
        paymentDto.setPaymentAt(this.paymentAt);
        paymentDto.setAmount(this.paymentAmount);
        paymentDto.setPaymentType(this.paymentType);

        MemberDto memberDto = member.convertToDto();
        paymentDto.setMember(memberDto);
        return paymentDto;
    }

    public PaymentDto convertToDtoWithMemberAndPayBacks() {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentId(this.id);
        paymentDto.setMemberId(this.member.getId()); // N+1 이슈 확인 필요
        paymentDto.setPaymentAt(this.paymentAt);
        paymentDto.setAmount(this.paymentAmount);
        paymentDto.setPaymentType(this.paymentType);

        MemberDto memberDto = member.convertToDto();
        paymentDto.setMember(memberDto);

        List<PayBackDto> payBacks = this.payBacks.stream()
                .map(payBack -> payBack.convertToDto())
                .collect(Collectors.toList());
        paymentDto.setPayBacks(payBacks);
        return paymentDto;
    }

    public void cancel() {
        this.paymentAmount = 0;
        this.paymentType = PaymentType.PAYMENT_CANCEL;
        this.modifiedAt = LocalDateTime.now();
    }
}
