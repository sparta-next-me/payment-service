package org.nextme.payment_service.payment.domain;

public enum SagaCurrentStep {
    // 1. 시작 단계 (Compensation 시작)
    INITIATED("보상 프로세스 시작"),

    // 2. 핵심 보상 단계 (결제 시스템과 관련된 단계)
    REFUND_PAYMENT_REQUESTED("결제 환불 요청"),
    REFUND_PAYMENT_COMPLETED("결제 환불 완료"),
    REFUND_PAYMENT_FAILED("결제 환불 실패"),

    // 3. 기타 보상 단계 (다른 도메인 서비스가 있다면 추가)
    // 예: 재고를 복구하는 단계
    INVENTORY_RESTORE_REQUESTED("재고 복구 요청"),
    INVENTORY_RESTORE_COMPLETED("재고 복구 완료"),
    INVENTORY_RESTORE_FAILED("재고 복구 실패"),

    // 4. 최종 단계
    COMPENSATION_FINALIZED("보상 프로세스 최종 종료");

    private final String description;

    SagaCurrentStep(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
