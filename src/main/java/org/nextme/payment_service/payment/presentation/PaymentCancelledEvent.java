package org.nextme.payment_service.payment.presentation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelledEvent {
    private String orderId;
    private Long cancelAmount;
    private String reason;
}
