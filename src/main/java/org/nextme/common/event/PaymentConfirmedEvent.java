package org.nextme.common.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class PaymentConfirmedEvent {
    private String userId;
    private String paymentId;
    private String paymentKey;
    private UUID productId;
    private UUID advisorId;
    private LocalDateTime dateTime;
    private LocalDateTime endTime;

    @Builder
    public PaymentConfirmedEvent(String userId, String paymentId, String paymentKey, LocalDateTime dateTime,  LocalDateTime endTime, UUID productId, UUID advisorId) {
        this.userId = userId;
        this.paymentId = paymentId;
        this.paymentKey = paymentKey;
        this.dateTime = dateTime;
        this.productId = productId;
        this.advisorId = advisorId;
        this.endTime = endTime;
    }
}
