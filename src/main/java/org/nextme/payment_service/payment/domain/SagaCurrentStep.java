package org.nextme.payment_service.payment.domain;

public enum SagaCurrentStep {
    PAYMENT_STARTED("결제 시작"),

    PAYMENT_COMPLETED("결제 완료"),

    SCHEDULE_LOCKED("예약 완료"),

    CHATROOM_CRAETED("채팅방 생성 성공"),

    SAGA_COMPLETED("최종 확정");

    private final String description;

    SagaCurrentStep(String description) {this.description = description;}

    public String getDescription() {
        return description;
    }
}
