package org.nextme.payment_service.payment.infrastructure;

import org.nextme.payment_service.payment.domain.SagaCompensation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SagaCompensationRepository extends JpaRepository<SagaCompensation, UUID> {
    Optional<SagaCompensation> findBySagaId(UUID sagaId);
}
