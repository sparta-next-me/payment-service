package org.nextme.payment_service.payment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.nextme.common.jpa.JpaAudit;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_refund_or_cancel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefundOrCancel extends JpaAudit implements Serializable {

    @Id
    @Column(name = "refund_id", nullable = false)
    private UUID refundId; // 환불 거래의 고유 ID (PK)

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId; // 원본 결제 ID (FK: p_payment 참조)

    @Column(name = "saga_id", nullable = false)
    private UUID sagaId; // 이 환불을 유발한 Saga ID (FK: p_saga_compensation 참조)

    @Column(name = "refund_amount", nullable = false)
    private double refundAmount; // 실제 환불된 금액

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_reason", nullable = false)
    private RefundReason reason; // 환불 사유: SAGA_COMPENSATION, USER_REQUEST 등

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RefundStatus status; // 환불 처리 상태: SUCCESS, PENDING, FAILED

    @Column(name = "pg_refund_id", nullable = false)
    private String pgRefundId; // PG사(토스페이먼츠)에서 받은 환불 고유 ID

    @Column(name = "refunded_at", nullable = false)
    private LocalDateTime refundedAt; // 환불이 완료된 시각

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 레코드 생성 시각

    @Builder
    public RefundOrCancel(UUID refundId, UUID paymentId, UUID sagaId, double refundAmount,
                          RefundReason reason, RefundStatus status, String pgRefundId,
                          LocalDateTime refundedAt, LocalDateTime createdAt) {

        this.refundId = refundId;
        this.paymentId = paymentId;
        this.sagaId = sagaId;
        this.refundAmount = refundAmount;
        this.reason = reason;
        this.status = status;
        this.pgRefundId = pgRefundId;
        this.refundedAt = refundedAt;
        this.createdAt = createdAt;
    }

    /**
     * 팩토리 메서드 : 환불 요청 엔티티 생성
     */
    public static RefundOrCancel requestRefund(UUID paymentId, UUID sagaId, double refundAmount, RefundReason reason) {
        return RefundOrCancel.builder()
                .refundId(UUID.randomUUID())
                .paymentId(paymentId)
                .sagaId(sagaId)
                .refundAmount(refundAmount)
                .reason(reason)
                .status(RefundStatus.REQUESTED)
                .build();
    }

    /**
     * 환불 성공 처리 및 PG ID 기록
     */
    public void markAsSuccess() {
        if (this.status != RefundStatus.PENDING_PG) {
            throw new IllegalStateException("환불은 PENDING_PG 상태에서만 완료될 수 있습니다.");
        }
        this.status = RefundStatus.SUCCESS;
        this.pgRefundId = pgRefundId;
        this.refundedAt = LocalDateTime.now();
    }

    /**
     * 환불 실패 처리
     */
    public void markAsFailed() {
        this.status = RefundStatus.FAILED;
    }

    /**
     * PG 연동 직전 상태로 변경
     */
    public void markAsPendingPg() {
        this.status = RefundStatus.PENDING_PG;
    }
}
