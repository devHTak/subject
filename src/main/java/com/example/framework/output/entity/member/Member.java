package com.example.framework.output.entity.member;

import com.example.usecase.dto.MemberDto;
import com.example.usecase.dto.MemberPaymentLimitDto;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", length = 20)
    private String name;

    private Integer ownedAmount;

    @Column(name = "JOIN_YN", length = 1)
    private String joinYn;

    @OneToMany(mappedBy = "member")
    private List<MemberPaymentLimit> memberPaymentLimits = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public Member() {}

    public Member(Long id) {
        this.id = id;
    }

    public Member(String name, Integer ownedAmount, String joinYn) {
        this.name = name;
        this.ownedAmount = ownedAmount;
        this.joinYn = joinYn;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getOwnedAmount() {
        return ownedAmount;
    }

    public String getJoinYn() {
        return joinYn;
    }

    public List<MemberPaymentLimit> getMemberPaymentLimits() {
        return memberPaymentLimits;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void updateAmount(Integer ownedAmount) {
        this.ownedAmount = ownedAmount;
    }

    public void addPaymentLimit(MemberPaymentLimit paymentLimit) {
        if(this.memberPaymentLimits == null) {
            this.memberPaymentLimits = new ArrayList<>();
        }
        this.memberPaymentLimits.add(paymentLimit);
        paymentLimit.saveMember(this);
    }

    public void removePaymentLimit(MemberPaymentLimit paymentLimit) {
        if(this.memberPaymentLimits.contains(paymentLimit)) {
            this.memberPaymentLimits.remove(paymentLimit);
        }
        paymentLimit.saveMember(null);
    }

    public MemberDto convertToDtoWithLimits() {
        MemberDto memberDto = new MemberDto();
        memberDto.setMemberId(this.id);
        memberDto.setJoinYn(this.joinYn);
        memberDto.setName(this.name);
        memberDto.setOwnedAmount(this.ownedAmount);

        List<MemberPaymentLimitDto> paymentLimitDtoList = this.memberPaymentLimits.stream()
                        .map(memberPaymentLimit -> memberPaymentLimit.convertToDto())
                        .collect(Collectors.toList());

        memberDto.setPaymentLimits(paymentLimitDtoList);
        return memberDto;
    }

    public MemberDto convertToDto() {
        MemberDto memberDto = new MemberDto();
        memberDto.setMemberId(this.id);
        memberDto.setJoinYn(this.joinYn);
        memberDto.setName(this.name);
        memberDto.setOwnedAmount(this.ownedAmount);
        return memberDto;
    }
}
