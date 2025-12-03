package org.nextme.payment_service.payment.domain;

public enum SagaStatus {
    COMPLETED("보상 성공"),

    FAILED("보상 실패"),

    COMPENSATING("보상 실행 중");

    private final String description;

    SagaStatus(String description) {this.description = description;}

    public String getDescription() {
        return description;
    }
}
