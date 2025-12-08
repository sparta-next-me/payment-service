package org.nextme.payment_service.payment.presentation;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PaymentInitRequest {
    private UUID userId;
    private String productName;
    private long amount;
}
