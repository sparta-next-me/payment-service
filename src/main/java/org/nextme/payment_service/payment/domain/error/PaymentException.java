package org.nextme.payment_service.payment.domain.error;

import org.nextme.infrastructure.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class PaymentException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;

    // PaymentErrorCode로부터 상태 코드를 받아 RuntimeException의 메시지를 설정하는 생성자를 추가합니다.
    public PaymentException(PaymentErrorCode errorCode) {
        super(errorCode.getDefaultMessage()); // RuntimeException의 생성자 호출
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
    }

    public PaymentException(PaymentErrorCode errorCode, String message) {
        super(message); // RuntimeException의 생성자 호출
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
    }

    // 💡 필요하다면 Getter 추가 (ApplicationException에서 가져오던 필드들)
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }
}
