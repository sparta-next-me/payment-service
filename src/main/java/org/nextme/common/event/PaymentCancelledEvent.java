package org.nextme.common.event;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value // 불변 객체 (Immutable)로 정의하고 Getter를 자동 생성합니다.
@Builder
@Jacksonized // JSON 역직렬화 시 Builder 패턴을 사용합니다.
public class PaymentCancelledEvent {

    /**
     * 취소 대상 예약을 식별하는 고유 ID (Kafka Key로 사용)
     */
    UUID reservationId;

    /**
     * 이벤트를 발생시킨 사용자의 고유 ID
     */
    UUID userId;

    /**
     * 결제 서비스의 고유 거래 ID
     */
    String paymentKey;

    String paymentId;

    /**
     * 취소된 금액
     */
    Long cancelledAmount;

    /**
     * 결제 취소가 발생한 시점 (UTC)
     */
    Instant cancelledAt;

    /**
     * 취소 사유 코드 또는 설명
     */
    String reason;
}
