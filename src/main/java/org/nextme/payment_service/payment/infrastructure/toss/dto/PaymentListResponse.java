package org.nextme.payment_service.payment.infrastructure.toss.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentListResponse {
    private final String orderId;
    private final String orderName;
    private final Long amount;
    private final String paymentStatus;       // 결제 상태 (COMPLETED, FAILED, CANCELLED 등)
    private final LocalDateTime requestedAt; // 요청 시간
}
