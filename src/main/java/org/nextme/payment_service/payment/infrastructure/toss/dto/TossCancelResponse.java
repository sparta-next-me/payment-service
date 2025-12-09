package org.nextme.payment_service.payment.infrastructure.toss.dto;

import lombok.Getter;

@Getter
public class TossCancelResponse {
    // 원본 결제 키 (paymentKey)
    private String paymentKey;

    // PG사에서 발급된 환불/취소 고유 ID (저희가 RefundOrCancel 엔티티에 저장할 pg_refund_id)
    private String cancelId;

    // 실제로 취소된 금액
    private double cancelledAmount;

    // 취소 상태 (DONE, PARTIAL_CANCELED 등)
    private String status;

    // 취소와 관련된 상세 정보 (취소 사유, 환불 계좌 정보 등)
    private Object cancels;
}
