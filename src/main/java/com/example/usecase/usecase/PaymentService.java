package com.example.usecase.usecase;

import com.example.usecase.dto.PayBackDto;
import com.example.usecase.dto.constant.PayBackType;
import com.example.usecase.dto.constant.PaymentType;
import com.example.usecase.dto.MemberDto;
import com.example.usecase.dto.PaymentDto;
import com.example.usecase.exception.ExceptionCode;
import com.example.usecase.exception.PaymentBizException;
import com.example.usecase.port.input.PaymentInputPort;
import com.example.usecase.port.output.MemberOutputPort;
import com.example.usecase.port.output.PayBackOutputPort;
import com.example.usecase.port.output.PaymentOutputPort;
import com.example.usecase.usecase.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService implements PaymentInputPort {

    private final MemberOutputPort memberOutputPort;
    private final PaymentOutputPort paymentOutputPort;
    private final PayBackOutputPort payBackOutputPort;
    private final PaymentValidator paymentValidator;

    @Autowired
    public PaymentService(MemberOutputPort memberOutputPort
                          , PaymentOutputPort paymentOutputPort
                          , PayBackOutputPort payBackOutputPort
                          , PaymentValidator paymentValidator) {
        this.memberOutputPort = memberOutputPort;
        this.paymentOutputPort = paymentOutputPort;
        this.payBackOutputPort = payBackOutputPort;
        this.paymentValidator = paymentValidator;
    }

    @Override
    @Transactional
    public PaymentDto savePayment(PaymentDto paymentDto) {
        MemberDto memberDto = memberOutputPort.findMemberWithLimits(paymentDto.getMemberId())
                .orElseThrow(() -> new PaymentBizException(ExceptionCode.NOT_JOIN_MEMBER));

        List<PaymentDto> payments = paymentOutputPort.findPaymentsByMemberIdAndDate(paymentDto.getMemberId(), DateUtil.getStartOfMinusDay(30));
        paymentValidator.validateExceedLimit(paymentDto.getAmount(), payments, memberDto);

        memberDto.setOwnedAmount(memberDto.getOwnedAmount() - paymentDto.getAmount());
        memberOutputPort.updateMemberAmount(memberDto);

        paymentDto.setPaymentType(PaymentType.PAYMENT);
        PaymentDto resultDto = paymentOutputPort.save(paymentDto);
        return resultDto;
    }


    @Override
    @Transactional
    public PaymentDto deletePayment(PaymentDto paymentDto) {
        PaymentDto payment = paymentOutputPort.findByIdWithMemberAndPayBacks(paymentDto.getPaymentId())
                .orElseThrow(() -> new PaymentBizException(ExceptionCode.NO_PAYMENT));

        if(PaymentType.PAYMENT_CANCEL.equals(payment.getPaymentType())) {
            throw new PaymentBizException(ExceptionCode.NO_PAYMENT);
        }

        // 이미 페이백을 수령한 경우 페이백 취소 처리
        if(payment.getPayBacks() != null) {
            int alreadyPayBackAmount = paymentValidator.validateAndCalculateForPayBack(payment);
            if(alreadyPayBackAmount > 0) {
                payment.getMember().setOwnedAmount(payment.getMember().getOwnedAmount() - alreadyPayBackAmount);

                PayBackDto payBackDto = new PayBackDto();
                payBackDto.setPaymentId(payment.getPaymentId());
                payBackDto.setPayBackAmount(alreadyPayBackAmount);
                payBackDto.setPayBackAt(LocalDateTime.now());
                payBackDto.setPayBackType(PayBackType.PAY_BACK_CANCEL);
                payBackOutputPort.save(payBackDto);
            }
        }

        payment.getMember().setOwnedAmount(payment.getMember().getOwnedAmount() + payment.getAmount());
        memberOutputPort.updateMemberAmount(payment.getMember());

        return paymentOutputPort.cancel(payment);
    }
}
