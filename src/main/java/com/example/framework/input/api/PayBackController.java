package com.example.framework.input.api;

import com.example.framework.input.dto.ApiResponse;
import com.example.framework.input.dto.PayBackRequest;
import com.example.framework.input.dto.PayBackResponse;
import com.example.usecase.dto.PayBackDto;
import com.example.usecase.dto.constant.PayBackType;
import com.example.usecase.port.input.PayBackInputPort;
import com.example.usecase.usecase.annotation.CacheLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class PayBackController {

    private final Logger log = LoggerFactory.getLogger(PayBackController.class);
    private final PayBackInputPort payBackInputPort;

    @Autowired
    public PayBackController(PayBackInputPort payBackInputPort) {
        this.payBackInputPort = payBackInputPort;
    }

    @PostMapping("/payments/{paymentId}/pay-backs")
    @CacheLock(key = "#request.memberId()")
    public ApiResponse<PayBackResponse> savePayBack(@PathVariable Long paymentId, @Validated @RequestBody PayBackRequest request) {
        log.info("savePayBack: paymentId({})", paymentId);
        PayBackDto payBack = new PayBackDto();
        payBack.setPaymentId(paymentId);
        payBack.setPayBackType(PayBackType.PAY_BACK);
        payBack.setPayBackRequestType(request.payBackRequestType());

        PayBackDto responseDto = payBackInputPort.savePayBack(payBack);

        PayBackResponse response = this.convertToResposne(responseDto);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), response);
    }

    @DeleteMapping("/payments/{paymentId}/pay-backs")
    @CacheLock(key = "#request.memberId()")
    public ApiResponse<PayBackResponse> cancelPayBack(@PathVariable Long paymentId, @Validated @RequestBody PayBackRequest request) {
        log.info("cancelPayBack: paymentId({})", paymentId);
        PayBackDto payBack = new PayBackDto();
        payBack.setPaymentId(paymentId);
        payBack.setPayBackType(PayBackType.PAY_BACK_CANCEL);

        PayBackDto responseDto = payBackInputPort.cancelPayBack(payBack);

        PayBackResponse response = this.convertToResposne(responseDto);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), response);
    }

    private PayBackResponse convertToResposne(PayBackDto responseDto) {
        return new PayBackResponse(
                responseDto.getPayBackId(),
                responseDto.getPayBackType().name(),
                responseDto.getPayBackAmount()
        );
    }

}
