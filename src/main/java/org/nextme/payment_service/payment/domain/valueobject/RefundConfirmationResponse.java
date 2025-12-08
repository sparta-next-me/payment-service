package org.nextme.payment_service.payment.domain.valueobject;

import lombok.Value;

/**
 * PG사 환불 요청 후, 도메인 레이어에 전달할 핵심 정보 (값 객체)
 */
@Value
public class RefundConfirmationResponse {

    // 원본 결제 키
    String paymentKey;

    // PG사에서 발급된 환불/취소 고유 ID
    String pgCancelId;

    // PG사에서 실제로 취소 처리된 금액
    double cancelledAmount;
}
