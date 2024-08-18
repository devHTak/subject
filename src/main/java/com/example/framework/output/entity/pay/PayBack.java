package com.example.framework.output.entity.pay;

import com.example.usecase.dto.PayBackDto;
import com.example.usecase.dto.constant.PayBackType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PayBack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Payment payment;

    @Enumerated(value = EnumType.STRING)
    private PayBackType payBackType;

    private Integer payBackAmount;

    private LocalDateTime payBackAt;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public PayBack() {
    }

    public PayBack(Payment payment, PayBackType payBackType, Integer payBackAmount, LocalDateTime payBackAt) {
        this.payment = payment;
        this.payBackType = payBackType;
        this.payBackAmount = payBackAmount;
        this.payBackAt = payBackAt;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Payment getPayment() {
        return payment;
    }

    public PayBackType getPayBackType() {
        return payBackType;
    }

    public Integer getPayBackAmount() {
        return payBackAmount;
    }

    public LocalDateTime getPayBackAt() {
        return payBackAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void updatePayment(Payment payment) {
        this.payment = payment;
    }

    public PayBackDto convertToDto() {
        PayBackDto payBackDto = new PayBackDto();
        payBackDto.setPayBackId(this.id);
        payBackDto.setPayBackType(this.payBackType);
        payBackDto.setPayBackAt(this.payBackAt);
        payBackDto.setPayBackAmount(this.payBackAmount);
        return payBackDto;
    }
}
