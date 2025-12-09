package org.nextme.payment_service.payment.presentation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelRequest {
    @NotBlank(message = "주문 ID는 필수입니다.")
    private String orderId;

    @NotBlank(message = "취소 사유는 필수입니다.")
    private String reason;

    // 부분 취소 시 사용, 전체 취소 시 null 허용
    private Long cancelAmount;
}
