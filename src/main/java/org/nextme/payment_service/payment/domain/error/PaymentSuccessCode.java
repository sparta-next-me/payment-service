package org.nextme.payment_service.payment.domain.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.nextme.infrastructure.success.SuccessReasonDTO;
import org.springframework.http.HttpStatus;
import org.nextme.infrastructure.success.BaseSuccessCode;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PaymentSuccessCode implements BaseSuccessCode {


    // 1. 결제 초기화 성공 (DB에 REQUESTED 상태로 레코드 생성 완료)
    PAYMENT_INIT_SUCCESS(HttpStatus.OK, "200", "결제 요청 정보가 성공적으로 초기화되었습니다."),
    PAYMENT_FAILURE_HANDLED(HttpStatus.OK, "S003", "결제 실패 정보 서버 기록 완료"),


    // ----------------------------------------------------------------------------------
    // 1. Payment 엔티티 관련 성공 (결제 승인)
    // ----------------------------------------------------------------------------------
    PAYMENT_CONFIRM_SUCCESS(
            HttpStatus.OK,
            "200",
            "결제가 성공적으로 승인되었습니다." // 토스페이먼츠 승인 API 호출 성공 시
    ),
    PAYMENT_RETRIEVE_SUCCESS(
            HttpStatus.OK,
            "200",
            "결제 정보가 성공적으로 조회되었습니다." // 결제 조회 API 성공 시
    ),

    // ----------------------------------------------------------------------------------
    // 2. RefundOrCancel 엔티티 관련 성공 (환불 처리)
    // ----------------------------------------------------------------------------------
    REFUND_PROCESS_SUCCESS(
            HttpStatus.OK,
            "200",
            "환불 요청이 성공적으로 처리되었습니다." // 환불 API 호출 및 DB 기록 성공 시
    ),

    // ----------------------------------------------------------------------------------
    // 3. SagaCompensation 엔티티 관련 성공 (보상 완료)
    // ----------------------------------------------------------------------------------
    SAGA_COMPENSATION_COMPLETED(
            HttpStatus.OK,
            "200",
            "Saga 보상 프로세스가 성공적으로 완료되었습니다." // Saga 상태가 COMPLETED로 전환 시
    ),

    // ----------------------------------------------------------------------------------
    // 4. 일반적인 비즈니스 오퍼레이션 성공
    // ----------------------------------------------------------------------------------
    REQUEST_PROCESSED_OK(
            HttpStatus.OK,
            "200",
            "요청이 성공적으로 처리되었습니다." // 특정 결과 없이 일반적인 성공 응답이 필요할 때
    );





    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public SuccessReasonDTO getReasonHttpStatus() {
        return SuccessReasonDTO.builder()
                .httpStatus(this.httpStatus)
                .isSuccess(true)
                .code(this.code)
                .message(this.message)
                .build();
    }
}
