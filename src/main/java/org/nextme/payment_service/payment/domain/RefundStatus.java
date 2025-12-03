package org.nextme.payment_service.payment.domain;

public enum RefundStatus {
    REQUESTED("환불 접수"),

    PENDING_PG("PG사 연동 중"),

    SUCCESS("환불 완료"),

    FAILED("환불 실패"),

    CANCELED("환불 취소");

    private final String description;

    RefundStatus(String description) {this.description = description;}

    public String getDescription() {
        return description;
    }
}
