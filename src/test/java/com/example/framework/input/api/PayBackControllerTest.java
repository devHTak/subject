package com.example.framework.input.api;

import com.example.framework.input.dto.PayBackRequest;
import com.example.framework.output.entity.member.Member;
import com.example.framework.output.entity.pay.PayBack;
import com.example.framework.output.entity.pay.Payment;
import com.example.framework.output.repository.member.MemberRepository;
import com.example.framework.output.repository.pay.PayBackRepository;
import com.example.framework.output.repository.pay.PaymentRepository;
import com.example.usecase.dto.constant.PayBackRequestType;
import com.example.usecase.dto.constant.PayBackType;
import com.example.usecase.dto.constant.PaymentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class PayBackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PayBackRepository payBackRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member;
    private Payment payment;
    private Payment cancelPayment;

    @BeforeEach
    void beforeEach() {
        member = new Member("TEST", 4000, "Y");
        memberRepository.save(member);

        cancelPayment = new Payment(member, PaymentType.PAYMENT_CANCEL, 0, LocalDateTime.now());
        paymentRepository.save(cancelPayment);

        payment = new Payment(member, PaymentType.PAYMENT, 2000, LocalDateTime.now());
        paymentRepository.save(payment);


    }

    @Test
    @DisplayName("존재하지 않는 결제 정보에 대한 페이백 요청")
    void no_payment_info_pay_back_test() throws Exception {
        PayBackRequest request = new PayBackRequest(member.getId(), PayBackRequestType.RATIO);

        mockMvc.perform(post("/payments/" + (payment.getId() + 1) + "/pay-backs")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("결재 내역이 이미 취소되었거나 조회되지 않습니다."));
    }

    @Test
    @DisplayName("취소된 결제 정보에 대한 페이백 요청")
    void cancel_payment_info_pay_back_test() throws Exception {
        PayBackRequest request = new PayBackRequest(member.getId(), PayBackRequestType.RATIO);

        mockMvc.perform(post("/payments/" + cancelPayment.getId() + "/pay-backs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("결재 내역이 이미 취소되었거나 조회되지 않습니다."));
    }

    @Test
    @DisplayName("페이백 금액이 존재하는 결제 정보에 대한 페이백 요청")
    void already_pay_back_amount_test() throws Exception {
        PayBack payBack = new PayBack(payment, PayBackType.PAY_BACK, 10, LocalDateTime.now());
        payment.addPayback(payBack);
        payBackRepository.save(payBack);

        PayBackRequest request = new PayBackRequest(member.getId(), PayBackRequestType.RATIO);

        mockMvc.perform(post("/payments/" + payment.getId() + "/pay-backs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("이미 처리된 페이백이 있습니다."));
    }

    @Test
    @DisplayName("고정 비율 페이백 처리")
    void ratio_pay_back_test() throws Exception {
        PayBackRequest request = new PayBackRequest(member.getId(), PayBackRequestType.RATIO);

        mockMvc.perform(post("/payments/" + payment.getId() + "/pay-backs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.payBackType").value(PayBackType.PAY_BACK.name()))
                .andExpect(jsonPath("$.data.payBackAmount").value(200));

        Payment result = paymentRepository.findByIdWithMemberAndPayBacks(payment.getId()).get();
        assertEquals(PaymentType.PAYMENT, result.getPaymentType());
        assertEquals(2000, result.getPaymentAmount());

        assertEquals(4000 + 200, result.getMember().getOwnedAmount());
    }

    @Test
    @DisplayName("고정 금액 페이백 처리")
    void fixed_amount_pay_back_test() throws Exception {
        PayBackRequest request = new PayBackRequest(member.getId(), PayBackRequestType.FIXED_AMOUNT);

        mockMvc.perform(post("/payments/" + payment.getId() + "/pay-backs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.payBackType").value(PayBackType.PAY_BACK.name()))
                .andExpect(jsonPath("$.data.payBackAmount").value(100));

        Payment result = paymentRepository.findByIdWithMemberAndPayBacks(payment.getId()).get();
        assertEquals(PaymentType.PAYMENT, result.getPaymentType());
        assertEquals(2000, result.getPaymentAmount());

        assertEquals(4000 + 100, result.getMember().getOwnedAmount());
    }

    @Test
    @DisplayName("존재하지 않는 정보에 대한 페이백 취소 요청")
    void no_payment_info_pay_back_cacnel_test() throws Exception {
        PayBackRequest request = new PayBackRequest(member.getId(), PayBackRequestType.RATIO);

        mockMvc.perform(delete("/payments/" + (payment.getId() + 1) + "/pay-backs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("결재 내역이 이미 취소되었거나 조회되지 않습니다."));
    }

    @Test
    @DisplayName("페이백 금액이 존재하지 않는 결제 정보에 대한 페이백 취소 요청")
    void already_pay_back_cancel_amount_test() throws Exception {
        PayBack payBack1 = new PayBack(payment, PayBackType.PAY_BACK, 10, LocalDateTime.now());
        payment.addPayback(payBack1);
        payBackRepository.save(payBack1);

        PayBack payBack2 = new PayBack(payment, PayBackType.PAY_BACK_CANCEL, 10, LocalDateTime.now());
        payment.addPayback(payBack2);
        payBackRepository.save(payBack2);

        PayBackRequest request = new PayBackRequest(member.getId(), PayBackRequestType.RATIO);

        mockMvc.perform(delete("/payments/" + payment.getId() + "/pay-backs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("처리된 페이백이 없습니다."));
    }

    @Test
    @DisplayName("페이백 취소 처리")
    void pay_back_cancel_test() throws Exception {
        PayBack payBack1 = new PayBack(payment, PayBackType.PAY_BACK, 10, LocalDateTime.now());
        payment.addPayback(payBack1);
        payBackRepository.save(payBack1);

        PayBackRequest request = new PayBackRequest(member.getId(), PayBackRequestType.RATIO);

        mockMvc.perform(delete("/payments/" + payment.getId() + "/pay-backs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.payBackType").value(PayBackType.PAY_BACK_CANCEL.name()))
                .andExpect(jsonPath("$.data.payBackAmount").value(10));

        Payment result = paymentRepository.findByIdWithMemberAndPayBacks(payment.getId()).get();
        assertEquals(PaymentType.PAYMENT, result.getPaymentType());
        assertEquals(2000, result.getPaymentAmount());

        assertEquals(4000 - 10, result.getMember().getOwnedAmount());
    }

}