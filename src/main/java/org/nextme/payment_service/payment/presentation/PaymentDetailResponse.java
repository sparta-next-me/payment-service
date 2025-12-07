package org.nextme.payment_service.payment.presentation;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentDetailResponse {
    private final String orderId;
    private final Long amount;
    private final String paymentKey;
    private final String paymentStatus;
    private final LocalDateTime requestedAt;
    private final LocalDateTime approvedAt;
    private final String method;
    private final String orderName;
}
