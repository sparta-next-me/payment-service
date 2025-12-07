package org.nextme.payment_service.payment.presentation;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PaymentInitResponse {
    private UUID orderId;
    private final long amount;
    private final String clientKey;

    public PaymentInitResponse(UUID orderId, long amount, String clientKey) {
        this.orderId = orderId;
        this.amount = amount;
        this.clientKey = clientKey;
    }
}
