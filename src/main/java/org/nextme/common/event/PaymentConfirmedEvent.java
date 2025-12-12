package org.nextme.common.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentConfirmedEvent {
    private String userId;
    private String paymentId;
    private String paymentKey;

    @Builder
    public PaymentConfirmedEvent(String userId, String paymentId, String paymentKey) {
        this.userId = userId;
        this.paymentId = paymentId;
        this.paymentKey = paymentKey;
    }
}
