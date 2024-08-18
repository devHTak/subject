package com.example.framework.output.repository.member;

import com.example.framework.output.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("""
           SELECT m
           FROM   Member m
           LEFT JOIN FETCH m.memberPaymentLimits
           WHERE  m.id = :memberId
           AND    m.joinYn   = 'Y' 
           """)
    Optional<Member> findMemberWithLimits(Long memberId);
}
