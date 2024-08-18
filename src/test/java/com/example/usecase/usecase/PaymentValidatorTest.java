package com.example.usecase.usecase;

import com.example.usecase.dto.MemberDto;
import com.example.usecase.dto.MemberPaymentLimitDto;
import com.example.usecase.dto.PayBackDto;
import com.example.usecase.dto.PaymentDto;
import com.example.usecase.dto.constant.PayBackType;
import com.example.usecase.dto.constant.PaymentLimitType;
import com.example.usecase.dto.constant.PaymentType;
import com.example.usecase.exception.ExceptionCode;
import com.example.usecase.exception.PaymentBizException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentValidatorTest {

    @InjectMocks
    private PaymentValidator paymentValidator;

    @Test
    @DisplayName("결제 초과 금액 테스트 - 개인 소유 금액 초과 결제 요청 예외")
    void exceed_owned_amount_exception_test() {
        // given
        List<PaymentDto> payments = new ArrayList<>();
        MemberDto memberDto = new MemberDto();
        memberDto.setMemberId(1L);
        memberDto.setOwnedAmount(1000);

        // when
        PaymentBizException e = assertThrows(PaymentBizException.class
                , () -> paymentValidator.validateExceedLimit(1001, payments, memberDto));

        // then
        assertEquals(ExceptionCode.EXCEEDED_STORAGE_LMIIT, e.getExceptionCode());
    }

    @Test
    @DisplayName("결제 초과 금액 테스트 - 1회 한도 초과 결제 요청 예외")
    void exceed_one_time_amount_exception_test() {
        // given
        List<PaymentDto> payments = new ArrayList<>();

        List<MemberPaymentLimitDto> paymentLimits = List.of(
                new MemberPaymentLimitDto(1L, 1L, PaymentLimitType.ONE_TIME, 500));

        MemberDto memberDto = new MemberDto();
        memberDto.setOwnedAmount(1000);
        memberDto.setMemberId(1L);
        memberDto.setPaymentLimits(paymentLimits);

        // when
        PaymentBizException e = assertThrows(PaymentBizException.class
                , () -> paymentValidator.validateExceedLimit(501, payments, memberDto));

        // then
        assertEquals(ExceptionCode.ONE_TIME_LIMIT, e.getExceptionCode());
    }

    @Test
    @DisplayName("결제 초과 금액 테스트 - 1일 한도 초과 결제 요청 예외")
    void exceed_one_day_amount_exception_test() {
        // given
        List<PaymentDto> payments = List.of(
                new PaymentDto(1L, 1L, PaymentType.PAYMENT,
                        LocalDate.now().atStartOfDay(), 100),
                new PaymentDto(1L, 1L, PaymentType.PAYMENT,
                        LocalDate.now().atTime(0, 0, 1), 200),
                new PaymentDto(1L, 1L, PaymentType.PAYMENT,
                        LocalDate.now().atTime(23, 59, 59), 300),
                new PaymentDto(1L, 1L, PaymentType.PAYMENT,
                        LocalDate.now().atTime(23, 59, 58), 400));

        List<MemberPaymentLimitDto> paymentLimits = List.of(
                new MemberPaymentLimitDto(1L, 1L, PaymentLimitType.ONE_DAY, 1100));

        MemberDto memberDto = new MemberDto();
        memberDto.setOwnedAmount(1000);
        memberDto.setMemberId(1L);
        memberDto.setPaymentLimits(paymentLimits);

        // when
        PaymentBizException e = assertThrows(PaymentBizException.class
                , () -> paymentValidator.validateExceedLimit(101, payments, memberDto));

        // then
        assertEquals(ExceptionCode.ONE_DAY_LIMIT, e.getExceptionCode());

    }

    @Test
    @DisplayName("결제 초과 금액 테스트 - 30일 한도 초과 결제 요청 예외")
    void exceed_thirty_day_amount_exception_test() {
        // given
        List<PaymentDto> payments = List.of(
                new PaymentDto(1L, 1L, PaymentType.PAYMENT,
                        LocalDate.now().minusDays(30).atStartOfDay(), 100),
                new PaymentDto(1L, 1L, PaymentType.PAYMENT,
                        LocalDate.now().minusDays(30).atTime(0, 0, 1), 200));

        List<MemberPaymentLimitDto> paymentLimits = List.of(
                new MemberPaymentLimitDto(1L, 1L, PaymentLimitType.THIRTY_DAY, 400));

        MemberDto memberDto = new MemberDto();
        memberDto.setOwnedAmount(1000);
        memberDto.setMemberId(1L);
        memberDto.setPaymentLimits(paymentLimits);

        // when
        PaymentBizException e = assertThrows(PaymentBizException.class
                , () -> paymentValidator.validateExceedLimit(101, payments, memberDto));

        // then
        assertEquals(ExceptionCode.THIRTY_DAY_LIMIT, e.getExceptionCode());

    }

    @Test
    @DisplayName("페이백 실패 예외 - 취소 결제 상태 예외")
    void payment_cancel_status_exception_test() {
        // given
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentType(PaymentType.PAYMENT_CANCEL);

        // when
        PaymentBizException e = assertThrows(PaymentBizException.class
                , () -> paymentValidator.validateAndCalculateForPayBack(paymentDto));

        // then
        assertEquals(ExceptionCode.NO_PAYMENT, e.getExceptionCode());
    }

    @Test
    @DisplayName("페이백 금액 존재 계산")
    void calculate_already_pay_back_amount_test() {
        // given
        List<PayBackDto> payBacks = List.of(
                new PayBackDto(1L, PayBackType.PAY_BACK, 100),
                new PayBackDto(1L, PayBackType.PAY_BACK_CANCEL, 100),
                new PayBackDto(1L, PayBackType.PAY_BACK, 100));

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentType(PaymentType.PAYMENT);
        paymentDto.setPayBacks(payBacks);

        // when
        int actual = paymentValidator.validateAndCalculateForPayBack(paymentDto);

        // then
        assertEquals(100, actual);
    }

    @Test
    @DisplayName("페이백 금액 0원 계산")
    void calculate_pay_back_amount_test() {
        // given
        List<PayBackDto> payBacks = List.of(
                new PayBackDto(1L, PayBackType.PAY_BACK, 100),
                new PayBackDto(1L, PayBackType.PAY_BACK_CANCEL, 100),
                new PayBackDto(1L, PayBackType.PAY_BACK, 100),
                new PayBackDto(1L, PayBackType.PAY_BACK_CANCEL, 100));

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentType(PaymentType.PAYMENT);
        paymentDto.setPayBacks(payBacks);

        // when
        int actual = paymentValidator.validateAndCalculateForPayBack(paymentDto);

        // then
        assertEquals(0, actual);
    }

}