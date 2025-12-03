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
    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(name = "saga_id", nullable = false)
    private UUID sagaId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "pg_transaction_id", nullable = false)
    private String pgTransactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "local_status", nullable = false)
    private PaymentStatus localStatus;

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;

    @Column(name = "is_compensated", nullable = false)
    private boolean isCompensated;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Payment(UUID paymentId, UUID sagaId, UUID userId, String productName
            , double amount, String pgTransactionId, PaymentStatus localStatus
            , LocalDateTime paidAt, boolean isCompensated) {

        this.paymentId = paymentId;
        this.sagaId = sagaId;
        this.userId = userId;
        this.productName = productName;
        this.amount = amount;
        this.pgTransactionId = pgTransactionId;
        this.localStatus = localStatus;
        this.paidAt = paidAt;
        this.isCompensated = isCompensated;
    }
}
