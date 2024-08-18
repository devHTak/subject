package com.example.usecase.usecase;

import com.example.usecase.dto.MemberDto;
import com.example.usecase.dto.PayBackDto;
import com.example.usecase.dto.PaymentDto;
import com.example.usecase.dto.constant.PayBackRequestType;
import com.example.usecase.dto.constant.PayBackType;
import com.example.usecase.dto.constant.PaymentType;
import com.example.usecase.exception.ExceptionCode;
import com.example.usecase.exception.PaymentBizException;
import com.example.usecase.port.output.MemberOutputPort;
import com.example.usecase.port.output.PayBackOutputPort;
import com.example.usecase.port.output.PaymentOutputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class PayBackServiceTest {

    @InjectMocks
    private PayBackService payBackService;

    @Mock
    private PaymentOutputPort paymentOutputPort;

    @Mock
    private MemberOutputPort memberOutputPort;

    @Mock
    private PayBackOutputPort payBackOutputPort;

    @Mock
    private PaymentValidator paymentValidator;

    @Test
    @DisplayName("페이백 요청 시 결제 정보 미조회 예외 테스트")
    void no_payment_exception_pay_back_test() {
        // given
        PayBackDto payBackDto = new PayBackDto();
        payBackDto.setPaymentId(1L);
        when(paymentOutputPort.findByIdWithMemberAndPayBacks(payBackDto.getPaymentId()))
                .thenReturn(Optional.empty());

        // when
        PaymentBizException e = assertThrows(PaymentBizException.class
                , () -> payBackService.savePayBack(payBackDto));

        // then
        assertEquals(ExceptionCode.NO_PAYMENT, e.getExceptionCode());
    }

    @Test
    @DisplayName("페이백 요청 시 페이백 금액 존재 예외 테스트")
    void already_pay_back_exception_test() {
        // given
        PayBackDto payBackDto = new PayBackDto();
        payBackDto.setPaymentId(1L);

        PaymentDto paymentDto = new PaymentDto();
        when(paymentOutputPort.findByIdWithMemberAndPayBacks(payBackDto.getPaymentId()))
                .thenReturn(Optional.of(paymentDto));
        when(paymentValidator.validateAndCalculateForPayBack(paymentDto))
                .thenReturn(1);

        // when
        PaymentBizException e = assertThrows(PaymentBizException.class
                , () -> payBackService.savePayBack(payBackDto));

        // then
        assertEquals(ExceptionCode.HAS_PAY_BACK, e.getExceptionCode());
    }

    @Test
    @DisplayName("페이백 성공 테스트")
    void pay_back_test() {
        // given
        PayBackDto payBackDto = new PayBackDto();
        payBackDto.setPaymentId(1L);
        payBackDto.setPayBackRequestType(PayBackRequestType.RATIO);

        MemberDto memberDto = new MemberDto();
        memberDto.setMemberId(1L);
        memberDto.setOwnedAmount(1000);

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentId(1L);
        paymentDto.setPaymentType(PaymentType.PAYMENT);
        paymentDto.setAmount(1000);
        paymentDto.setMember(memberDto);
        when(paymentOutputPort.findByIdWithMemberAndPayBacks(payBackDto.getPaymentId()))
                .thenReturn(Optional.of(paymentDto));
        when(paymentValidator.validateAndCalculateForPayBack(paymentDto))
                .thenReturn(0);
        when(payBackOutputPort.save(payBackDto))
                .thenReturn(new PayBackDto(1L, PayBackType.PAY_BACK, 10));

        // when
        PayBackDto result = payBackService.savePayBack(payBackDto);

        // then
        assertEquals(PayBackType.PAY_BACK, result.getPayBackType());
        assertEquals(10, result.getPayBackAmount());
    }

    @Test
    @DisplayName("페이백 취소 시 결재 정보 미조회 예외 테스트")
    void no_payment_exception_pay_back_cancel_test() {
        // given
        PayBackDto payBackDto = new PayBackDto();
        payBackDto.setPaymentId(1L);
        when(paymentOutputPort.findByIdWithMemberAndPayBacks(payBackDto.getPaymentId()))
                .thenReturn(Optional.empty());

        // when
        PaymentBizException e = assertThrows(PaymentBizException.class
                , () -> payBackService.cancelPayBack(payBackDto));

        // then
        assertEquals(ExceptionCode.NO_PAYMENT, e.getExceptionCode());
    }

    @Test
    @DisplayName("페이백 취소 시 페이백 금액이 미조회 예외 테스트")
    void no_pay_back_exception_pay_back_cancel_test() {
        // given
        PayBackDto payBackDto = new PayBackDto();
        payBackDto.setPaymentId(1L);

        PaymentDto paymentDto = new PaymentDto();
        when(paymentOutputPort.findByIdWithMemberAndPayBacks(payBackDto.getPaymentId()))
                .thenReturn(Optional.of(paymentDto));
        when(paymentValidator.validateAndCalculateForPayBack(paymentDto))
                .thenReturn(1);

        // when
        PaymentBizException e = assertThrows(PaymentBizException.class
                , () -> payBackService.savePayBack(payBackDto));

        // then
        assertEquals(ExceptionCode.HAS_PAY_BACK, e.getExceptionCode());
    }

    @Test
    @DisplayName("페이백 취소 테스트")
    void pay_back_cancel_test() {
        // given
        PayBackDto payBackDto = new PayBackDto();
        payBackDto.setPaymentId(1L);

        MemberDto memberDto = new MemberDto();
        memberDto.setMemberId(1L);
        memberDto.setOwnedAmount(1000);

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentId(1L);
        paymentDto.setPaymentType(PaymentType.PAYMENT);
        paymentDto.setAmount(1000);
        paymentDto.setMember(memberDto);
        when(paymentOutputPort.findByIdWithMemberAndPayBacks(payBackDto.getPaymentId()))
                .thenReturn(Optional.of(paymentDto));
        when(paymentValidator.validateAndCalculateForPayBack(paymentDto))
                .thenReturn(1);
        when(payBackOutputPort.save(payBackDto))
                .thenReturn(new PayBackDto(1L, PayBackType.PAY_BACK_CANCEL, 10));

        // when
        PayBackDto result = payBackService.cancelPayBack(payBackDto);

        // then
        assertEquals(PayBackType.PAY_BACK_CANCEL, result.getPayBackType());
        assertEquals(10, result.getPayBackAmount());
    }

}