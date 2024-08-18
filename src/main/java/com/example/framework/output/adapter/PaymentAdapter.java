package com.example.framework.output.adapter;

import com.example.framework.output.entity.member.Member;
import com.example.framework.output.entity.pay.Payment;
import com.example.framework.output.repository.pay.PaymentRepository;
import com.example.usecase.dto.PaymentDto;
import com.example.usecase.exception.ExceptionCode;
import com.example.usecase.exception.PaymentBizException;
import com.example.usecase.port.output.PaymentOutputPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentAdapter implements PaymentOutputPort {
    private final PaymentRepository paymentRepository;

    public PaymentAdapter(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Optional<PaymentDto> findByIdWithMember(Long paymentId) {
        return paymentRepository.findByIdWithMember(paymentId)
                .map(payment -> payment.convertToDtoWithMember());
    }

    @Override
    public PaymentDto save(PaymentDto paymentDto) {
        Payment payment = this.convertToEntity(paymentDto);
        Payment result = paymentRepository.save(payment);

        return result.convertToDto();
    }

    @Override
    public PaymentDto cancel(PaymentDto paymentDto) {
        Payment payment = paymentRepository.findById(paymentDto.getPaymentId())
                .orElseThrow(() -> new PaymentBizException(ExceptionCode.NO_PAYMENT));

        payment.cancel();
        return payment.convertToDto();
    }

    private Payment convertToEntity(PaymentDto paymentDto) {
        Member member = new Member(paymentDto.getMemberId());

        return new Payment(
                  member
                , paymentDto.getPaymentType()
                , paymentDto.getAmount()
                , LocalDateTime.now());
    }

    @Override
    public List<PaymentDto> findPaymentsByMemberIdAndDate(Long memberId, LocalDateTime dateTime) {
        return paymentRepository.findPaymentsByMemberIdAndDate(memberId, dateTime).stream()
                .map(payment -> payment.convertToDto())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PaymentDto> findByIdWithMemberAndPayBacks(Long paymentId) {
        return paymentRepository.findByIdWithMemberAndPayBacks(paymentId)
                .map(payment -> payment.convertToDtoWithMemberAndPayBacks());
    }
}
