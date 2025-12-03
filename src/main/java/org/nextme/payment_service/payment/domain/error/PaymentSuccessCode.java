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

    HUB_ASSIGNMENT_LIST_OK(
            HttpStatus.OK,
            "200",
            "해당 허브 배송 목록이 조회되었습니다."
    ),

    DELIVERY_DETAIL_OK(
            HttpStatus.OK,
            "200",
            "배송 상세 정보가 조회되었습니다."
    ),

    // 허브 이동 시작
    HUB_TRANSIT_STARTED(
            HttpStatus.OK,
            "200",
            "허브 이동이 시작되었습니다."
    ),

    // 허브 구간 도착
    HUB_TRANSIT_ARRIVED(
            HttpStatus.OK,
            "200",
            "허브 도착이 처리되었습니다."
    ),

    // 업체 배송 시작
    OUT_FOR_DELIVERY_STARTED(
            HttpStatus.OK,
            "200",
            "업체 배송이 시작되었습니다."
    ),

    // 최종 배송 완료
    DELIVERY_COMPLETED(
            HttpStatus.OK,
            "200",
            "배송이 완료되었습니다."
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
