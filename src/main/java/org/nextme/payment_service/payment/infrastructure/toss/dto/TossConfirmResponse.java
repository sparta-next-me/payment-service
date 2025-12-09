package org.nextme.payment_service.payment.infrastructure.toss.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TossConfirmResponse {
    private String version;
    private String paymentKey;
    private String orderId;
    private String orderName;
    private String status;
    private long totalAmount;
    private String method;

}
