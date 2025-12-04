package org.nextme.payment_service.payment.domain;

public enum PaymentStatus {

    REQUESTED("결제 요청 됨"),

    SUCCESS("결제 성공"),

    FAILED("결제 실패"),
    PARTIAL_CANCELLED("부분 취소"),

    CANCELLED("결제 취소");

    private final String description;

    PaymentStatus(String description) {this.description = description;}

    public String getDescription() {
        return description;
    }
}
