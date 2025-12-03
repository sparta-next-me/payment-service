package org.nextme.payment_service.payment.domain;

public enum RefundReason {
    SAGA_COMPENSATION("자동 보상 환불"),

    USER_SIMPLE_CANCELLATION("단순 변심 취소"),

    USER_POLICY_CANCELLATION("정책에 따른 취소"),

    EXPERT_CANCELLATION("전문가 사정으로 인한 취소"),

    PARTIAL_REFUND("부분 환불"),

    ETC("기타 사유");

    private final String description;

    RefundReason(String description) {this.description = description;}

    public String getDescription() {
        return description;
    }
}
