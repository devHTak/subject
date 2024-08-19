package com.example.framework.input.api;

import com.example.framework.input.dto.PaymentRequest;
import com.example.framework.output.entity.member.Member;
import com.example.framework.output.entity.member.MemberPaymentLimit;
import com.example.framework.output.entity.pay.PayBack;
import com.example.framework.output.entity.pay.Payment;
import com.example.framework.output.repository.member.MemberPaymentLimitRepository;
import com.example.framework.output.repository.member.MemberRepository;
import com.example.framework.output.repository.pay.PayBackRepository;
import com.example.framework.output.repository.pay.PaymentRepository;
import com.example.usecase.dto.constant.PayBackType;
import com.example.usecase.dto.constant.PaymentLimitType;
import com.example.usecase.dto.constant.PaymentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberPaymentLimitRepository memberPaymentLimitRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PayBackRepository payBackRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member;
    private MemberPaymentLimit oneTimeLimit;
    private MemberPaymentLimit oneDayLimit;
    private MemberPaymentLimit thirtyDayLimit;

    @BeforeEach
    void beforeEach() {
        member = new Member("TEST", 6000, "Y");

        oneTimeLimit = new MemberPaymentLimit(PaymentLimitType.ONE_TIME, 500);
        member.addPaymentLimit(oneTimeLimit);
        oneDayLimit = new MemberPaymentLimit(PaymentLimitType.ONE_DAY, 1000);
        member.addPaymentLimit(oneDayLimit);
        thirtyDayLimit = new MemberPaymentLimit(PaymentLimitType.THIRTY_DAY, 2000);
        member.addPaymentLimit(thirtyDayLimit);

        memberRepository.save(member);
        memberPaymentLimitRepository.saveAll(List.of(oneTimeLimit, oneDayLimit, thirtyDayLimit));
    }

    @Test
    @DisplayName("존재하지 않는 회원 정보로 결제 요청")
    void no_member_payment_test() throws Exception {
        PaymentRequest request = new PaymentRequest(member.getId() + 1, 1000);
        mockMvc.perform(post("/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("회원가입이 안되어 있거나 없는 회원입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 보유 한도 이상 결제 요청")
    void owned_amount_exceed_payment_test() throws Exception{
        PaymentRequest request = new PaymentRequest(member.getId(), member.getOwnedAmount() + 1);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("저장 금액 한도 초과되었습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("1회 한도 이상 결제 요청")
    void one_time_exceed_payment_test() throws Exception {
        PaymentRequest request = new PaymentRequest(member.getId(), oneTimeLimit.getLimitAmount() + 1);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("1회 한도 제한 초과되었습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("1일 한도 이상 결제 요청")
    void one_day_exceed_payment_test() throws Exception {
        List<Payment> payments = List.of(new Payment(member, PaymentType.PAYMENT, 400, LocalDateTime.now())
                , new Payment(member, PaymentType.PAYMENT, 400, LocalDateTime.now())
                , new Payment(member, PaymentType.PAYMENT_CANCEL, 0, LocalDateTime.now()));
        paymentRepository.saveAll(payments);

        PaymentRequest request = new PaymentRequest(member.getId(), 201);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("1일 한도 제한 초과되었습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("30일 한도 이상 결제 요청")
    void thirty_day_exceed_payment_test() throws Exception {
        List<Payment> payments = List.of(new Payment(member, PaymentType.PAYMENT, 500, LocalDateTime.now())
                , new Payment(member, PaymentType.PAYMENT, 500, LocalDateTime.now().minusDays(10))
                , new Payment(member, PaymentType.PAYMENT_CANCEL, 0, LocalDateTime.now().minusDays(10))
                , new Payment(member, PaymentType.PAYMENT, 500, LocalDateTime.now().minusDays(20))
                , new Payment(member, PaymentType.PAYMENT, 500, LocalDateTime.now().minusDays(30))
                , new Payment(member, PaymentType.PAYMENT, 500, LocalDateTime.now().minusDays(31)));
        paymentRepository.saveAll(payments);

        PaymentRequest request = new PaymentRequest(member.getId(), 1);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("30일 한도 제한 초과되었습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("결제 정상 처리")
    void payment_test() throws Exception {
        PaymentRequest request = new PaymentRequest(member.getId(), 500);

        MvcResult mvcResult = mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andDo(print())
                .andReturn();
        String contents = mvcResult.getResponse().getContentAsString();
        Integer paymentId = JsonPath.parse(contents).read("$.data.paymentId");

        Payment payment = paymentRepository.findById(paymentId.longValue()).get();
        assertEquals(500, payment.getPaymentAmount());
        assertEquals(PaymentType.PAYMENT, payment.getPaymentType());
    }

    @Test
    @DisplayName("존재하지 않는 결제 정보에 대한 결제 취소 요청")
    void no_payment_info_for_cancel_test() throws Exception {
        PaymentRequest request = new PaymentRequest(member.getId(), 100);

        mockMvc.perform(delete("/payments/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("결재 내역이 이미 취소되었거나 조회되지 않습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("이미 취소된 결제 정보에 대한 결제 취소 요청")
    void already_cancel_payment_test() throws Exception {
        Payment payment = new Payment(member, PaymentType.PAYMENT_CANCEL, 0, LocalDateTime.now());
        Payment returnPayment = paymentRepository.save(payment);

        PaymentRequest request = new PaymentRequest(member.getId(), 100);

        mockMvc.perform(delete("/payments/" + returnPayment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("결재 내역이 이미 취소되었거나 조회되지 않습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("페이백 존재하는 상태에서 결제 취소 요청")
    void payment_cancel_with_pay_back_test() throws Exception {
        Payment payment = new Payment(member, PaymentType.PAYMENT, 100, LocalDateTime.now());
        Payment returnPayment = paymentRepository.save(payment);

        PayBack payBack1 = new PayBack(payment, PayBackType.PAY_BACK, 10, LocalDateTime.now());
        payment.addPayback(payBack1);
        PayBack payBack2 = new PayBack(payment, PayBackType.PAY_BACK_CANCEL, 10, LocalDateTime.now());
        payment.addPayback(payBack2);
        PayBack payBack3 = new PayBack(payment, PayBackType.PAY_BACK, 10, LocalDateTime.now());
        payment.addPayback(payBack3);

        List<PayBack> payBacks = List.of(payBack1, payBack2, payBack3);
        payBackRepository.saveAll(payBacks);

        PaymentRequest request = new PaymentRequest(member.getId(), null);

        mockMvc.perform(delete("/payments/" + returnPayment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.paymentType").value(PaymentType.PAYMENT_CANCEL.name()))
                .andExpect(jsonPath("$.data.paymentAmount").value(0))
                .andDo(print());
    }

    @Test
    @DisplayName("페이백 없는 상태에서 결제 취소 요청")
    void payment_cancel_with_no_pay_back_test() throws Exception {
        Payment payment = new Payment(member, PaymentType.PAYMENT, 100, LocalDateTime.now());
        Payment returnPayment = paymentRepository.save(payment);

        PayBack payBack1 = new PayBack(payment, PayBackType.PAY_BACK, 10, LocalDateTime.now());
        payment.addPayback(payBack1);
        PayBack payBack2 = new PayBack(payment, PayBackType.PAY_BACK_CANCEL, 10, LocalDateTime.now());
        payment.addPayback(payBack2);

        List<PayBack> payBacks = List.of(payBack1, payBack2);
        payBackRepository.saveAll(payBacks);
        payBackRepository.saveAll(payBacks);

        PaymentRequest request = new PaymentRequest(member.getId(), null);

        mockMvc.perform(delete("/payments/" + returnPayment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.paymentType").value(PaymentType.PAYMENT_CANCEL.name()))
                .andExpect(jsonPath("$.data.paymentAmount").value(0))
                .andDo(print());
    }

}