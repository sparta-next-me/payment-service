package org.nextme.payment_service.payment.domain.service;

import org.nextme.payment_service.payment.domain.valueobject.RefundConfirmationResponse;

// 환불 요청이라는 비즈니스 행위를 정의하는 도메인 인터페이스
public interface RefundGatewayService {

    /**
     * PG사에 환불을 요청합니다.
     * @param paymentKey 원본 결제 키 (토스 paymentKey)
     * @param cancelAmount 환불 요청 금액
     * @param cancelReason 환불 사유
     * @return PG사로부터 받은 환불 응답 정보
     */
    RefundConfirmationResponse requestCancel(String paymentKey, double cancelAmount, String cancelReason);
}
