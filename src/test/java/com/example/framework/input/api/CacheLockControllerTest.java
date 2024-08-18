package com.example.framework.input.api;

import com.example.framework.input.dto.PaymentRequest;
import com.example.framework.output.entity.member.Member;
import com.example.framework.output.repository.member.MemberRepository;
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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class CacheLockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member;

    @BeforeEach
    void beforeEach() {
        member = new Member("TEST", 6000, "Y");
        memberRepository.save(member);
    }

    @Test
    @DisplayName("5초 이내에 같은 회원에 대한 요청")
    void requests_within_5_seconds_test() throws Exception {
        PaymentRequest request = new PaymentRequest(member.getId(), 1000);

        mockMvc.perform(post("/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("결제 및 페이백에 대한 요청은 5초 이후에 가능합니다."));
    }

    @Test
    @DisplayName("5초 이후에 같은 회원에 대한 요청")
    void requests_over_5_seconds_test() throws Exception {
        PaymentRequest request = new PaymentRequest(member.getId(), 1000);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        Thread.sleep(5000L);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.paymentType").value(PaymentType.PAYMENT.name()))
                .andExpect(jsonPath("$.data.paymentAmount").value(request.paymentAmount()));
    }
}
