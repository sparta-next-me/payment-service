package org.nextme.payment_service.payment.infrastructure.toss.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@ToString
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
    private Map<String, String> metadata;

}
