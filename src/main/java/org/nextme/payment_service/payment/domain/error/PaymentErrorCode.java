package org.nextme.payment_service.payment.domain.error;

import lombok.Getter;

import org.nextme.payment_service.payment.domain.Payment;
import org.springframework.http.HttpStatus;

@Getter
public enum PaymentErrorCode {

    // ----------------------------------------------------------------------------------
    // 1. Payment 엔티티 관련 오류 (결제 승인, 상태 변경 유효성)
    // ----------------------------------------------------------------------------------
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_001", "해당 결제 정보를 찾을 수 없습니다."),
    INVALID_ORDER_ID(HttpStatus.BAD_REQUEST, "PAYMENT_002", "유효하지 않은 주문 ID 형식입니다."), // UUID 형식 검사 등에 사용
    AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "PAYMENT_003", "요청 금액이 주문 또는 저장된 금액과 일치하지 않습니다."),
    INVALID_PAYMENT_STATUS(HttpStatus.BAD_REQUEST, "PAYMENT_004", "현재 결제 상태에서 요청된 작업을 수행할 수 없습니다."),
    ALREADY_CONFIRMED(HttpStatus.CONFLICT, "PAYMENT_005", "이미 승인 완료된 결제입니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "PAYMENT_006", "유효하지 않거나 잘못된 형식의 입력 값입니다."),


    // ----------------------------------------------------------------------------------
    // 2. PG (Payment Gateway) 통신 및 승인/취소 오류 (토스페이먼츠 연동)
    // ----------------------------------------------------------------------------------
    PG_INITIAL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PG_101", "PG사에 초기 결제 요청을 실패했습니다."), // 💡 새로 추가
    PG_CONFIRM_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PG_102", "PG사에 최종 승인 요청을 실패했습니다."),
    PG_REFUND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PG_103", "PG사에 환불 요청을 실패했습니다."), // 💡 3번 섹션에서 이동
    PG_COMMUNICATION_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "PG_104", "PG사 서버와의 통신에 실패했습니다."),
    PG_INVALID_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "PG_105", "PG사로부터 유효하지 않은 응답을 받았습니다."),


    // ----------------------------------------------------------------------------------
    // 3. Cancel/Refund 비즈니스 유효성 오류
    // ----------------------------------------------------------------------------------
    CANCEL_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "CANCEL_201", "해당 취소/환불 기록을 찾을 수 없습니다."), // REFUND_NOT_FOUND 이름 변경
    REFUNDABLE_AMOUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "CANCEL_202", "요청 환불 금액이 잔여 환불 가능 금액을 초과합니다."),
    CANCEL_STATUS_INVALID(HttpStatus.BAD_REQUEST, "CANCEL_203", "현재 취소 상태에서 요청된 작업을 수행할 수 없습니다."), // REFUND_STATUS_INVALID 이름 변경


    // ----------------------------------------------------------------------------------
    // 4. SagaCompensation 엔티티 관련 오류 (보상 흐름 관리)
    // ----------------------------------------------------------------------------------
    SAGA_NOT_FOUND(HttpStatus.NOT_FOUND, "SAGA_301", "해당 Saga 보상 프로세스를 찾을 수 없습니다."),
    INVALID_SAGA_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "SAGA_302", "현재 Saga 상태에서 다음 단계로 전환할 수 없습니다."),
    COMPENSATION_ALREADY_COMPLETED(HttpStatus.CONFLICT, "SAGA_303", "이미 완료된 보상 프로세스입니다."),
    COMPENSATION_FAILED_CRITICAL(HttpStatus.INTERNAL_SERVER_ERROR, "SAGA_304", "복구 불가능한 Saga 보상 실패가 발생했습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String defaultMessage;

    PaymentErrorCode(HttpStatus status, String code, String message) {
        this.httpStatus = status;
        this.code = code;
        this.defaultMessage = message;
    }
}