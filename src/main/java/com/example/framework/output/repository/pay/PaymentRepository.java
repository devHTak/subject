package com.example.framework.output.repository.pay;

import com.example.framework.output.entity.pay.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("""
           SELECT p
           FROM   Payment p
           WHERE  p.member.id = :memberId
           AND    p.paymentAt >= :dateTime 
           """)
    List<Payment> findPaymentsByMemberIdAndDate(Long memberId, LocalDateTime dateTime);

    @Query("""
           SELECT p
           FROM   Payment p
           JOIN FETCH p.member
           WHERE  p.id = :paymentId
           """)
    Optional<Payment> findByIdWithMember(Long paymentId);

    @Query("""
           SELECT p
           FROM   Payment p
           JOIN FETCH       p.member
           LEFT JOIN FETCH  p.payBacks
           WHERE  p.id = :paymentId
           """)
    Optional<Payment> findByIdWithMemberAndPayBacks(Long paymentId);
}
