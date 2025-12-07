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

    /**
     * 팩토리 메서드 : 보상 프로세스 시작
     */
    public static SagaCompensation start(UUID sagaId, UUID userId
            , SagaBusinessType type, String initialReason) {
        return SagaCompensation.builder()
                .sagaId(sagaId)
                .userId(userId)
                .businessType(type)
                .currentStep(SagaCurrentStep.INITIATED)
                .status(SagaStatus.COMPENSATING)
                .failureReason(initialReason)
                .startedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 다음 단계로 진행 및 기록
     */
    public void moveToNextStep(SagaCurrentStep nextStep){
        if(this.status != SagaStatus.COMPENSATING) {
            throw new IllegalStateException("보상 실행 중 상태에서만 단계를 변경활 수 있습니다.");
        }
        this.currentStep = nextStep;
    }

    /**
     * 보상 프로세스 성공적으로 완료
     */
    public void complete() {
        if (this.status != SagaStatus.COMPENSATING) {
            throw new IllegalStateException("보상 실행 중 상태에서만 완료 처리할 수 있습니다.");
        }
        this.status = SagaStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * 보상 프로세스 최종 실패
     */
    public void fail(String newFailureReason) {
        this.status = SagaStatus.FAILED;
        this.failureReason = newFailureReason;
        this.completedAt = LocalDateTime.now();
    }
}
