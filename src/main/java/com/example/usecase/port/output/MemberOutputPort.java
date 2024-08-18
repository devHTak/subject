package com.example.usecase.port.output;

import com.example.usecase.dto.MemberDto;

import java.util.Optional;

public interface MemberOutputPort {

    Optional<MemberDto> findMemberWithLimits(Long memberId);

    MemberDto updateMemberAmount(MemberDto memberDto);
}
