package org.nextme.common.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PaymentConfirmedEvent {
    private String userId;
    private String paymentId;
    private String paymentKey;
    private LocalDateTime dateTime;

    @Builder
    public PaymentConfirmedEvent(String userId, String paymentId, String paymentKey, LocalDateTime dateTime) {
        this.userId = userId;
        this.paymentId = paymentId;
        this.paymentKey = paymentKey;
        this.dateTime = dateTime;
    }
}
