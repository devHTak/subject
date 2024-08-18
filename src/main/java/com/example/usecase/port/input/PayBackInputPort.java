package com.example.usecase.port.input;

import com.example.usecase.dto.PayBackDto;

public interface PayBackInputPort {

    PayBackDto savePayBack(PayBackDto payBackDto);

    PayBackDto cancelPayBack(PayBackDto payBackDto);
}
