package org.nextme.payment_service.payment.infrastructure.toss.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TossErrorResponse {

    private String code;    // PG사에서 정의한 고유 오류 코드 (예: ALREADY_CANCELED_PAYMENT)
    private String message; // 사용자 친화적인 오류 메시지

    // 추가로 필요한 필드(예: orderId 등)가 있다면 추가할 수 있습니다.
}
