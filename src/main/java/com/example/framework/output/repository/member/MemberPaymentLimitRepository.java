package com.example.framework.output.repository.member;

import com.example.framework.output.entity.member.MemberPaymentLimit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPaymentLimitRepository extends JpaRepository<MemberPaymentLimit, Long> {
}
