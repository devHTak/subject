package com.example.usecase.usecase;

import com.example.usecase.dto.MemberDto;
import com.example.usecase.dto.PayBackDto;
import com.example.usecase.dto.PaymentDto;
import com.example.usecase.dto.constant.PayBackType;
import com.example.usecase.dto.constant.PaymentType;
import com.example.usecase.exception.ExceptionCode;
import com.example.usecase.exception.PaymentBizException;
import com.example.usecase.port.output.MemberOutputPort;
import com.example.usecase.port.output.PayBackOutputPort;
import com.example.usecase.port.output.PaymentOutputPort;
import com.example.usecase.usecase.util.DateUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private MemberOutputPort memberOutputPort;

    @Mock
    private PaymentOutputPort paymentOutputPort;

    @Mock
    private PayBackOutputPort payBackOutputPort;

    @Mock
    private PaymentValidator paymentValidator;

    @Test
    @DisplayName("회원 정보가 없는 결재 요청 예외")
    void no_member_payment_exception_test() {
        // given
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setMemberId(1L);

        when(memberOutputPort.findMemberWithLimits(paymentDto.getMemberId()))
                .thenReturn(Optional.empty());

        // when
        PaymentBizException e = assertThrows(PaymentBizException.class
                , () -> paymentService.savePayment(paymentDto));

        // then
        assertEquals(ExceptionCode.NOT_JOIN_MEMBER, e.getExceptionCode());
    }

    @Test
    @DisplayName("결재 테스트")
    void payment_test() {
        // given
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setMemberId(1L);
        paymentDto.setAmount(1000);

        MemberDto memberDto = new MemberDto();
        memberDto.setOwnedAmount(1000);
        when(memberOutputPort.findMemberWithLimits(paymentDto.getMemberId()))
                .thenReturn(Optional.of(memberDto));

        List<PaymentDto> payments = List.of();
        when(paymentOutputPort.findPaymentsByMemberIdAndDate(paymentDto.getMemberId()
                , DateUtil.getStartOfMinusDay(30))).thenReturn(payments);
        when(paymentOutputPort.save(paymentDto))
                .thenReturn(new PaymentDto(1L, 1L, PaymentType.PAYMENT, LocalDateTime.now(), paymentDto.getAmount()));

        // when
        PaymentDto resultDto = paymentService.savePayment(paymentDto);

        // then
        assertEquals(PaymentType.PAYMENT, resultDto.getPaymentType());
        assertEquals(paymentDto.getAmount(), resultDto.getAmount());
    }

    @Test
    @DisplayName("회원 정보나 결재정보가 없는 결재 취소 요청 예외")
    void no_member_and_payment_for_cacnel_exception_test() {
        // given
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentId(1L);

        when(paymentOutputPort.findByIdWithMemberAndPayBacks(paymentDto.getPaymentId()))
                .thenReturn(Optional.empty());

        // when
        PaymentBizException e = assertThrows(PaymentBizException.class
                , () -> paymentService.deletePayment(paymentDto));

        // then
        assertEquals(ExceptionCode.NO_PAYMENT, e.getExceptionCode());
    }

    @Test
    @DisplayName("결재 취소 테스트")
    void payment_cancel_test() {
        // given
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentId(1L);

        MemberDto memberDto = new MemberDto();
        memberDto.setMemberId(1L);
        memberDto.setOwnedAmount(0);
        List<PayBackDto> payBacks = List.of(
                new PayBackDto(1L, PayBackType.PAY_BACK, 10),
                new PayBackDto(1L, PayBackType.PAY_BACK_CANCEL, 10),
                new PayBackDto(1L, PayBackType.PAY_BACK, 10)
        );
        PaymentDto cancelPaymentDto = new PaymentDto();
        cancelPaymentDto.setAmount(1000);
        cancelPaymentDto.setPayBacks(payBacks);
        cancelPaymentDto.setMember(memberDto);

        when(paymentOutputPort.findByIdWithMemberAndPayBacks(paymentDto.getPaymentId()))
                .thenReturn(Optional.of(cancelPaymentDto));
        when(paymentValidator.validateAndCalculateForPayBack(cancelPaymentDto))
                .thenReturn(10);
        when(paymentOutputPort.cancel(cancelPaymentDto))
                .thenReturn(cancelPaymentDto);

        // when
        PaymentDto resultDto = paymentService.deletePayment(paymentDto);

        // then
        assertEquals(PaymentType.PAYMENT_CANCEL, resultDto.getPaymentType());
        assertEquals(cancelPaymentDto.getAmount(), resultDto.getAmount());

    }

}