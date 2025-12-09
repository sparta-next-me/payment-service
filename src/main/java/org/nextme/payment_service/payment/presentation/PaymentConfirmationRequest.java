package org.nextme.payment_service.payment.presentation;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PaymentConfirmationRequest {
    private UUID orderId;
    private String paymentKey;
    private long amount;
}
