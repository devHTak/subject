package com.example.framework.output.adapter;

import com.example.framework.output.entity.member.Member;
import com.example.framework.output.entity.pay.Payment;
import com.example.framework.output.repository.member.MemberRepository;
import com.example.usecase.dto.MemberDto;
import com.example.usecase.exception.ExceptionCode;
import com.example.usecase.exception.PaymentBizException;
import com.example.usecase.port.output.MemberOutputPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberAdapter implements MemberOutputPort {
    private final MemberRepository memberRepository;

    public MemberAdapter(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public Optional<MemberDto> findMemberWithLimits(Long memberId) {
        return memberRepository.findMemberWithLimits(memberId)
                .map(member -> member.convertToDtoWithLimits());
    }

    @Override
    public MemberDto updateMemberAmount(MemberDto memberDto) {
        // 이미 PersistentContext에서 관리중인 객체인 경우 조회 쿼리가 발생하지 않음
        Member member = memberRepository.findById(memberDto.getMemberId())
                .orElseThrow(() -> new PaymentBizException(ExceptionCode.NOT_JOIN_MEMBER));
        member.updateAmount(memberDto.getOwnedAmount());

        return member.convertToDto();
    }
}
