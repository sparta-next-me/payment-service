package org.nextme.payment_service.payment.infrastructure.toss.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TossInitialResponse {
    private String orderId;
    private String orderName;
    private Long amount;
    private String customerEmail;

    private String successUrl;
    private String failUrl;

    private String paymentWidgetToken;
}
