package com.example.framework.input.api;

import com.example.framework.input.dto.ApiResponse;
import com.example.framework.input.dto.PaymentRequest;
import com.example.framework.input.dto.PaymentResponse;
import com.example.usecase.dto.PaymentDto;
import com.example.usecase.port.input.PaymentInputPort;
import com.example.usecase.usecase.annotation.CacheLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {

    private final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentInputPort paymentInputPort;

    @Autowired
    public PaymentController(PaymentInputPort paymentInputPort) {
        this.paymentInputPort = paymentInputPort;
    }

    @PostMapping("/payments")
    @CacheLock(key = "#paymentRequest.memberId()")
    public ApiResponse<PaymentResponse> savePayment(@Validated @RequestBody PaymentRequest paymentRequest) {
        log.info("Save Payment: MemberId({}), Amount({})", paymentRequest.memberId(), paymentRequest.paymentAmount());
        PaymentDto requestDto = new PaymentDto();
        requestDto.setMemberId(paymentRequest.memberId());
        requestDto.setAmount(paymentRequest.paymentAmount());

        PaymentDto responseDto = paymentInputPort.savePayment(requestDto);

        PaymentResponse response = convertToResponse(responseDto);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), response);
    }

    @DeleteMapping("/payments/{paymentId}")
    @CacheLock(key = "#paymentRequest.memberId()")
    public ApiResponse<PaymentResponse> deletePayment(@PathVariable Long paymentId, @Validated @RequestBody PaymentRequest paymentRequest) {
        log.info("Delete Payment: PaymentId({})", paymentId);

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentId(paymentId);
        paymentDto.setMemberId(paymentRequest.memberId());
        PaymentDto responseDto = paymentInputPort.deletePayment(paymentDto);

        PaymentResponse response = this.convertToResponse(responseDto);

        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), response);
    }

    private PaymentResponse convertToResponse(PaymentDto responseDto) {
        return new PaymentResponse(responseDto.getPaymentId()
                , responseDto.getMemberId()
                , responseDto.getPaymentType().name()
                , responseDto.getAmount());
    }

}
