package org.nextme.payment_service.payment.infrastructure.toss.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TossCancelResponse {
    // 원본 결제 키 (paymentKey)
    private String paymentKey;

    private Long balanceAmount; //잔액 ( 취소 후 남은 잔액)

    // 취소 상태 (DONE, PARTIAL_CANCELED 등)
    private String status;

    // 취소와 관련된 상세 정보 (취소 사유, 환불 계좌 정보 등)
    private List<Cancel> cancels;

    @ToString
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cancel {
        private String cancelId;       // PG사 발급 취소 ID
        private long cancelledAmount; // 실제로 취소된 금액
        @JsonAlias("cancelReason")
        private String reason;         // 취소 사유
        private String transactionKey; // 취소 거래 키
        private String taxExemptionAmount; // 면세 금액
    }
}
