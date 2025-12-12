package org.nextme.payment_service.payment.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nextme.common.event.PaymentCancelledEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCancellationProducer {

    private static final String TOPIC_PAYMENT_CANCELLED = "payment.cancelled.v1";

    // KafkaTemplate의 Value 타입은 Object로 지정 (Spring Kafka가 JSON으로 자동 변환합니다.)
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 결제 취소 이벤트를 Kafka로 발행합니다.
     * * @param reservationId 취소할 예약의 고유 ID (Kafka Key로 사용되어 순서 보장)
     * @param userId 이벤트를 발생시킨 사용자의 ID
     * @param paymentId 결제 서비스의 고유 결제 ID
     * @param cancelledAmount 취소 금액
     * @param reason 취소 사유
     */
    public void sendPaymentCancelledEvent(
            String paymentKey,
            UUID userId,
            String paymentId,
            Long cancelledAmount,
            String reason) {

        // 1. 이벤트 객체 생성 (현재 시각을 cancelledAt으로 설정)
        PaymentCancelledEvent event = PaymentCancelledEvent.builder()
                .paymentKey(paymentKey)
                .paymentId(paymentId)
                .userId(userId)
                .cancelledAmount(cancelledAmount)
                .cancelledAt(Instant.now())
                .reason(reason)
                .build();

        // 2. Kafka 메시지 발행
        // Key: reservationId를 사용해 동일 예약 관련 메시지가 같은 파티션에 들어가 순서를 보장합니다.
        kafkaTemplate.send(TOPIC_PAYMENT_CANCELLED, paymentKey.toString(), event);

        log.info("결제 취소 이벤트 발행 성공. Topic: {}, payment ID: {}, User ID: {}",
                TOPIC_PAYMENT_CANCELLED, paymentKey, userId);
    }
}
