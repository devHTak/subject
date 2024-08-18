package com.example.usecase.dto.constant;

import com.example.usecase.dto.PaymentDto;

public enum PayBackRequestType {
    RATIO(0.1) {
        @Override
        public int calculatePayBackAmount(PaymentDto paymentDto) {
            return (int)(paymentDto.getAmount() * this.getValue());
        }
    },
    FIXED_AMOUNT(100) {
        @Override
        public int calculatePayBackAmount(PaymentDto paymentDto) {
            return (int)this.getValue();
        }
    };

    public abstract int calculatePayBackAmount(PaymentDto paymentDto);

    private double value;

    PayBackRequestType(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
