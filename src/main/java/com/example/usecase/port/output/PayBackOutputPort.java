package com.example.usecase.port.output;

import com.example.usecase.dto.PayBackDto;

public interface PayBackOutputPort {

    PayBackDto save(PayBackDto payBackDto);

}
