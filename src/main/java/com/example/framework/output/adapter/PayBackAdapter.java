package com.example.framework.output.adapter;

import com.example.framework.output.entity.pay.PayBack;
import com.example.framework.output.entity.pay.Payment;
import com.example.framework.output.repository.pay.PayBackRepository;
import com.example.framework.output.repository.pay.PaymentRepository;
import com.example.usecase.dto.PayBackDto;
import com.example.usecase.exception.ExceptionCode;
import com.example.usecase.exception.PaymentBizException;
import com.example.usecase.port.output.PayBackOutputPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayBackAdapter implements PayBackOutputPort {

    private final PayBackRepository payBackRepository;
    private final PaymentRepository paymentRepository;

    @Autowired
    public PayBackAdapter(PayBackRepository payBackRepository, PaymentRepository paymentRepository) {
        this.payBackRepository = payBackRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PayBackDto save(PayBackDto payBackDto) {
        Payment payment = paymentRepository.findById(payBackDto.getPaymentId())
                .orElseThrow(() -> new PaymentBizException(ExceptionCode.NO_PAYMENT));

        PayBack payBack = new PayBack(
                payment,
                payBackDto.getPayBackType(),
                payBackDto.getPayBackAmount(),
                payBackDto.getPayBackAt());

        return payBackRepository.save(payBack).convertToDto();
    }
}
