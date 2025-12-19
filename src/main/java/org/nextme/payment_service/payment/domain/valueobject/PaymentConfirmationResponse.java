package org.nextme.payment_service.payment.domain.valueobject;

import lombok.Getter;
import lombok.Value;

import java.util.Map;

public record PaymentConfirmationResponse(

    // PG사에서 최종적으로 확인된 결제 고유 키
    String pgTransactionId,

    // PG사에서 사용된 주문 ID (우리가 보낸 PaymentId)
    String orderId,

    // PG사에서 최종적으로 승인된 금액
    long confirmedAmount,

    Map<String, String> metadata
){}
