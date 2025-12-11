package org.nextme.payment_service.payment.infrastructure.toss.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TossInitialRequest {

    @NotBlank
    private String orderId;       // 우리 시스템의 주문 ID (Unique ID)

    @NotBlank
    private String orderName;     // 주문 상품명 (예: 사과 외 2건)

    @NotNull
    private Long amount;          // 결제 금액 (원 단위, Long 사용으로 정밀도 보장)

    // --- 결제 위젯 설정 정보 ---

    @NotBlank
    private String customerEmail; // 구매자 이메일

    @NotBlank
    private String successUrl;    // 결제 성공 시 리다이렉트 URL

    @NotBlank
    private String failUrl;       // 결제 실패 시 리다이렉트 URL

    // --- 추가 정보 (선택적) ---

    // PG사 응답을 웹훅으로 받을 때 사용할 우리 서버 주소
    private String flowExecutionUrl;

    // 구매자가 결제창에 입력해야 하는 정보 (예: 고객 ID 등)
    private String customerName;
}
