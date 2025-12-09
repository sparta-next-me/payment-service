package org.nextme.payment_service.payment.domain.service;

import org.nextme.payment_service.payment.domain.valueobject.PaymentConfirmationResponse;
import org.nextme.payment_service.payment.domain.valueobject.RefundConfirmationResponse;
import org.nextme.payment_service.payment.infrastructure.toss.dto.TossInitialRequest;
import org.nextme.payment_service.payment.infrastructure.toss.dto.TossInitialResponse;

public interface PaymentGatewayService {


    TossInitialResponse requestInitialPayment(TossInitialRequest request);

    /**
     * PG사에 결제 최종 승인을 요청합니다.
     * @param paymentKey 토스에서 받은 결제 키
     * @param orderId 주문/결제 ID (UUID.toString())
     * @param amount 결제 금액 (long 타입)
     * @return PG사로부터 받은 승인 응답 정보
     */
    PaymentConfirmationResponse confirmPayment(String paymentKey, String orderId, long amount);


    RefundConfirmationResponse requestCancel(String paymentKey, Long cancelAmount, String cancelReason);
}
