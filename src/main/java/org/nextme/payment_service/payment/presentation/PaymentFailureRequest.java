package org.nextme.payment_service.payment.presentation;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class PaymentFailureRequest {
    private String code;
    private String message;
    private UUID orderId;

}

