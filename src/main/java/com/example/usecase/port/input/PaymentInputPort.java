package com.example.usecase.port.input;

import com.example.usecase.dto.PaymentDto;

public interface PaymentInputPort {
    PaymentDto savePayment(PaymentDto paymentDto);

    PaymentDto deletePayment(PaymentDto paymentDto);
}
