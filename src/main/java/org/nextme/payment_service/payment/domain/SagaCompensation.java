package org.nextme.payment_service.payment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nextme.common.jpa.JpaAudit;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_saga_compensation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SagaCompensation extends JpaAudit implements Serializable {

    @Id
    @Column(name = "saga_id", nullable = false)
    private UUID sagaId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_type", nullable = false)
    private SagaBusinessType businessType;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_step", nullable = false)
    private SagaCurrentStep currentStep;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SagaStatus status;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder
    public SagaCompensation(UUID sagaId, UUID userId, SagaBusinessType businessType
            , SagaCurrentStep  currentStep, SagaStatus status, String failureReason, LocalDateTime startedAt, LocalDateTime completedAt) {
        this.sagaId = sagaId;
        this.userId = userId;
        this.businessType = businessType;
        this.currentStep = currentStep;
        this.status = status;
        this.failureReason = failureReason;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }
}
