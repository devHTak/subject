package com.example.usecase.port.output;

import com.example.usecase.dto.PaymentDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentOutputPort {

    Optional<PaymentDto> findByIdWithMember(Long paymentId);

    List<PaymentDto> findPaymentsByMemberIdAndDate(Long memberId, LocalDateTime dateTime);

    Optional<PaymentDto> findByIdWithMemberAndPayBacks(Long paymentId);

    PaymentDto save(PaymentDto paymentDto);

    PaymentDto cancel(PaymentDto payment);
}
