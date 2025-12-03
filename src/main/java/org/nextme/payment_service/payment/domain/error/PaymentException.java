package org.nextme.payment_service.payment.domain.error;

import org.nextme.infrastructure.exception.ApplicationException;

public class PaymentException extends ApplicationException {
    public PaymentException(PaymentErrorCode errorCode) {
        super(errorCode.getHttpStatus(), errorCode.getCode(), errorCode.getDefaultMessage());
    }

    public PaymentException(PaymentErrorCode errorCode, String message) {
        super(errorCode.getHttpStatus(), errorCode.getCode(), message);
    }
}
