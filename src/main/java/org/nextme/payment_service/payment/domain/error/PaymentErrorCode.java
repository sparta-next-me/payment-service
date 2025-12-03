package org.nextme.payment_service.payment.domain.error;

import lombok.Getter;

import org.nextme.payment_service.payment.domain.Payment;
import org.springframework.http.HttpStatus;

@Getter
public enum PaymentErrorCode{

    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "HUB_NOT_FOUND", "해당 허브를 찾을 수 없습니다."),
    DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "DELIVERY_NOT_FOUND", "해당 배송을 찾을 수 없습니다."),
    INVALID_HUB_ID(HttpStatus.BAD_REQUEST, "INVALID_HUB_ID", "유효하지 않은 허브 ID 입니다."),
    ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROUTE_NOT_FOUND", "해당 시작/도착 허브로 갈 수 있는 경로가 없습니다."),
    INVALID_DELIVERY_STATUS(HttpStatus.BAD_REQUEST, "INVALID_DELIVERY_STATUS", "유효하지 않은 배송 상태입니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "해당 주문을 찾을 수 없습니다."),
    RECEIVER_ADDRESS_NOT_FOUND(HttpStatus.BAD_REQUEST, "RECEIVER_ADDRESS_NOT_FOUND", "수령 업체의 주소가 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String defaultMessage;

    PaymentErrorCode(HttpStatus status, String code, String message) {
        this.httpStatus = status;
        this.code = code;
        this.defaultMessage = message;
    }
}