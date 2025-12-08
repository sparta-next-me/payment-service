package org.nextme.payment_service.common.success;

import brave.internal.Nullable;
import lombok.Getter;
import org.nextme.infrastructure.success.BaseSuccessCode;
import org.nextme.infrastructure.success.SuccessReasonDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class SuccessResponse {

    private final boolean isSuccess;
    private final String code;
    private final String message;
    private final Object data;

    // 1. data가 없는 성공 응답 (결제 승인, 환불 요청 등)
    public SuccessResponse(BaseSuccessCode code) {
        SuccessReasonDTO reason = code.getReasonHttpStatus();
        this.isSuccess = reason.getIsSuccess();
        this.code = reason.getCode();
        this.message = reason.getMessage();
        this.data = null;
    }

    // 2. data가 있는 성공 응답 (조회 등)
    public SuccessResponse(BaseSuccessCode code, @Nullable Object data) {
        SuccessReasonDTO reason = code.getReasonHttpStatus();
        this.isSuccess = reason.getIsSuccess();
        this.code = reason.getCode();
        this.message = reason.getMessage();
        this.data = data;
    }

    // ResponseEntity로 변환하는 헬퍼 메서드
    public ResponseEntity<SuccessResponse> toResponseEntity() {
        // BaseSuccessCode의 HttpStatus를 사용하여 응답 상태 코드 결정
        HttpStatus httpStatus = this.isSuccess ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;

        // 이 로직은 PaymentSuccessCode 내의 getReasonHttpStatus에서 HttpStatus를 가져오는 방식으로 대체될 수 있습니다.
        // PaymentSuccessCode의 getReasonHttpStatus()를 사용하여 HttpStatus를 가져온다고 가정합니다.

        return ResponseEntity
                .status(HttpStatus.OK) // 모든 성공은 200 OK로 반환한다고 가정
                .body(this);
    }
}
