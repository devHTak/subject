package com.example.usecase.usecase;

import com.example.usecase.dto.PayBackDto;
import com.example.usecase.dto.PaymentDto;
import com.example.usecase.dto.constant.PayBackType;
import com.example.usecase.exception.ExceptionCode;
import com.example.usecase.exception.PaymentBizException;
import com.example.usecase.port.input.PayBackInputPort;
import com.example.usecase.port.output.MemberOutputPort;
import com.example.usecase.port.output.PayBackOutputPort;
import com.example.usecase.port.output.PaymentOutputPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PayBackService implements PayBackInputPort {

    private final PaymentOutputPort paymentOutputPort;
    private final MemberOutputPort memberOutputPort;
    private final PayBackOutputPort payBackOutputPort;
    private final PaymentValidator paymentValidator;

    @Autowired
    public PayBackService(PaymentOutputPort paymentOutputPort
                          , MemberOutputPort memberOutputPort
                          , PayBackOutputPort payBackOutputPort
                          , PaymentValidator paymentValidator) {
        this.paymentOutputPort = paymentOutputPort;
        this.memberOutputPort = memberOutputPort;
        this.payBackOutputPort = payBackOutputPort;
        this.paymentValidator = paymentValidator;
    }

    @Override
    public PayBackDto savePayBack(PayBackDto payBackDto) {
        PaymentDto paymentDto =  paymentOutputPort.findByIdWithMemberAndPayBacks(payBackDto.getPaymentId())
                .orElseThrow(() -> new PaymentBizException(ExceptionCode.NO_PAYMENT));

        int alreadyPayBackAmount = paymentValidator.validateAndCalculateForPayBack(paymentDto);
        if(alreadyPayBackAmount > 0) {
            throw new PaymentBizException(ExceptionCode.HAS_PAY_BACK);
        }

        int payBackAmount = payBackDto.getPayBackRequestType().calculatePayBackAmount(paymentDto);

        paymentDto.getMember().setOwnedAmount(paymentDto.getMember().getOwnedAmount() + payBackAmount);
        memberOutputPort.updateMemberAmount(paymentDto.getMember());

        payBackDto.setPayBackAmount(payBackAmount);
        payBackDto.setPayBackAt(LocalDateTime.now());
        payBackDto.setPaymentId(paymentDto.getPaymentId());
        return payBackOutputPort.save(payBackDto);
    }

    @Override
    public PayBackDto cancelPayBack(PayBackDto payBackDto) {
        PaymentDto paymentDto =  paymentOutputPort.findByIdWithMemberAndPayBacks(payBackDto.getPaymentId())
                .orElseThrow(() -> new PaymentBizException(ExceptionCode.NO_PAYMENT));

        int alreadyPayBackAmount = paymentValidator.validateAndCalculateForPayBack(paymentDto);
        if(alreadyPayBackAmount == 0) {
            throw new PaymentBizException(ExceptionCode.NO_HAS_PAY_BACK);
        }

        paymentDto.getMember().setOwnedAmount(paymentDto.getMember().getOwnedAmount() - alreadyPayBackAmount);
        memberOutputPort.updateMemberAmount(paymentDto.getMember());

        payBackDto.setPayBackAmount(alreadyPayBackAmount);
        payBackDto.setPayBackAt(LocalDateTime.now());
        payBackDto.setPaymentId(paymentDto.getPaymentId());
        return payBackOutputPort.save(payBackDto);
    }

}
