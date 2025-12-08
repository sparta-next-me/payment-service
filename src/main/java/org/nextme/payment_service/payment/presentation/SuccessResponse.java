package org.nextme.payment_service.payment.presentation;

import lombok.Getter;
import org.nextme.payment_service.payment.domain.error.PaymentSuccessCode;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
public class SuccessResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String code;
    private final String message;

    public SuccessResponse(PaymentSuccessCode successCode) {
        this.code = successCode.getCode();
        this.message = successCode.getMessage();
    }

    // ResponseEntity로 변환하는 헬퍼 메서드
    public ResponseEntity<SuccessResponse> toResponseEntity() {
        return ResponseEntity
                .status(PaymentSuccessCode.PAYMENT_FAILURE_HANDLED.getHttpStatus())
                .body(this);
    }
}
