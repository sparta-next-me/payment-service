package org.nextme.payment_service.payment.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.nextme.common.jpa.JpaAudit;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_payment")
@Getter
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends JpaAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentId;

    @Column(name = "saga_id", nullable = false)
    private UUID sagaId; // 이 결제를 유발한 Saga ID

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Column(name = "refundable_amount", nullable = false)
    private long refundableAmount; // 현재 환불 가능한 잔여 금액 (핵심 필드 추가)

    @Column(name = "pg_transaction_id") // nullable=false 제거 (승인 전에는 값이 없음)
    private String pgTransactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "local_status", nullable = false)
    private PaymentStatus localStatus;

    private String failureCode;

    private String failureMessage;

    private String paymentKey;

    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;

    private String method;

    @Column(name = "paid_at") //nullable=false 제거 (승인 전에는 값이 없음)
    private LocalDateTime paidAt;

    // isCompensated 필드는 Payment가 아닌 SagaCompensation에서 관리하는 것이 책임 분리에 더 좋습니다. (여기서는 기존 필드 유지)
    @Column(name = "is_compensated", nullable = false)
    private boolean isCompensated;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Payment(UUID paymentId, UUID sagaId, UUID userId, String productName
            , long amount, long refundableAmount, String pgTransactionId, PaymentStatus localStatus, LocalDateTime paidAt, boolean isCompensated) {

        if (paymentId != null) this.paymentId = paymentId;
        this.sagaId = sagaId;
        this.userId = userId;
        this.productName = productName;
        this.amount = amount;
        this.refundableAmount = refundableAmount;
        this.pgTransactionId = pgTransactionId;
        this.localStatus = localStatus;
        this.paidAt = paidAt;
        this.isCompensated = isCompensated;
    }

    /**
     * 최종 결제 승인 성공 처리
     */
    public void confirmSuccess(String pgTransactionId) {
        if(this.localStatus != PaymentStatus.REQUESTED) {
            throw new IllegalStateException("결제 상태 오류: 현재 상태는 " + this.localStatus);
        }
        this.localStatus = PaymentStatus.SUCCESS;
        this.pgTransactionId = pgTransactionId;
        this.paidAt = LocalDateTime.now();
    }

    /**
     * 환불 처리 후 상태 및 잔여 금액 업데이트
     */
    public void applyRefund(double refundAmount) {
        if(refundAmount > this.refundableAmount) {
            throw new IllegalArgumentException("요청된 환불 금액이 잔여 환불 가능 금액을 초과합니다.");
        }

        this.refundableAmount -= refundAmount;

        if (this.refundableAmount == 0) {
            this.localStatus = PaymentStatus.CANCELLED; //전액 취소
        } else if (this.refundableAmount > 0 && this.refundableAmount < this.amount) {
            this.localStatus = PaymentStatus.PARTIAL_CANCELLED; //부분 취소
        }
    }

    /**
     * 금액 일치 여부 검증 (오차 방지를 위해 BigDecimal 사용을 권장하지만 double 유지)
     */
    public boolean isAmountValid(double inputAmount) {
        return (Math.abs(this.amount - inputAmount) < 0.001);
    }

    public void markFailure(String errorCode, String errorMessage) {
        // 1. 상태를 FAILED로 변경
        this.localStatus = PaymentStatus.FAILED;

        // 2. 실패 정보 기록
        this.failureCode = errorCode;
        this.failureMessage = errorMessage;
    }
}
