package com.example.usecase.usecase;

import com.example.usecase.dto.MemberDto;
import com.example.usecase.dto.PayBackDto;
import com.example.usecase.dto.PaymentDto;
import com.example.usecase.dto.constant.PayBackType;
import com.example.usecase.dto.constant.PaymentLimitType;
import com.example.usecase.dto.constant.PaymentType;
import com.example.usecase.exception.ExceptionCode;
import com.example.usecase.exception.PaymentBizException;
import com.example.usecase.usecase.util.DateUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentValidator {

    /**
     * 결제 금액에 대한 validation check
     * - 소유 금액에 대한 validation
     * - 1회 한도에 대한 validation
     * - 1일 한도에 대한 validation
     * - 30일 한도에 대한 validaiton
     */
    public void validateExceedLimit(Integer amount, List<PaymentDto> payments, MemberDto member) {
        if(isExceedOwnedAmount(member.getOwnedAmount(), amount)) {
            throw new PaymentBizException(ExceptionCode.EXCEEDED_STORAGE_LMIIT);
        }

        if(isExceedOneTimeAmount(amount, member)) {
            throw new PaymentBizException(ExceptionCode.ONE_TIME_LIMIT);
        }

        if(isExceedOneDayAmount(amount, payments, member)) {
            throw new PaymentBizException(ExceptionCode.ONE_DAY_LIMIT);
        }

        if(isExceedThirtyDayAmount(amount, payments, member)) {
            throw new PaymentBizException(ExceptionCode.THIRTY_DAY_LIMIT);
        }
    }

    private boolean isExceedOwnedAmount(Integer ownedAmount, Integer amount) {
        return ownedAmount < amount;
    }

    private boolean isExceedOneTimeAmount(Integer amount, MemberDto member) {
        Integer limitAmount = PaymentLimitType.ONE_TIME.findLimitAmount(member.getPaymentLimits());

        // 저장된 제한금액이 없거나 0인 경우 항상 false 가 되도록 함
        return limitAmount != 0 && limitAmount < amount;
    }

    private boolean isExceedOneDayAmount(Integer amount, List<PaymentDto> payments, MemberDto member) {
        Integer limitAmount = PaymentLimitType.ONE_DAY.findLimitAmount(member.getPaymentLimits());

        // 저장된 금액이 없거나 0인 경우 항상 false 리턴
        if(limitAmount == 0) {
            return false;
        }

        Integer oneDayAmounts = payments.stream()
                .filter(payment -> DateUtil.isToday(payment.getPaymentAt())
                        && PaymentType.PAYMENT.equals(payment.getPaymentType()))
                .map(payment -> payment.getAmount())
                .reduce(0, (before, after) -> before += after);
        return limitAmount < oneDayAmounts + amount;
    }

    private boolean isExceedThirtyDayAmount(Integer amount, List<PaymentDto> payments, MemberDto member) {
        Integer limitAmount = PaymentLimitType.THIRTY_DAY.findLimitAmount(member.getPaymentLimits());

        // 저장된 금액이 없거나 0인 경우 항상 false 리턴
        if(limitAmount == 0) {
            return false;
        }

        Integer thirtyDayAmounts = payments.stream()
                .map(payment -> PaymentType.PAYMENT.equals(payment.getPaymentType()) ? payment.getAmount() : 0)
                .reduce(0, (before, after) -> before += after);
        return limitAmount < thirtyDayAmounts + amount;
    }

    public int validateAndCalculateForPayBack(PaymentDto paymentDto) {
        if(PaymentType.PAYMENT_CANCEL.equals(paymentDto.getPaymentType())) {
            throw new PaymentBizException(ExceptionCode.NO_PAYMENT);
        }

        return this.calculateAlreadyPayBackAmount(paymentDto.getPayBacks());
    }

    private int calculateAlreadyPayBackAmount(List<PayBackDto> payBacks) {
        return payBacks.stream()
                .map(payBack -> PayBackType.PAY_BACK.equals(payBack.getPayBackType()) ? payBack.getPayBackAmount() : -1 * payBack.getPayBackAmount())
                .reduce(0, (before, after) -> before += after);
    }
}
