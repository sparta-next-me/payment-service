package org.nextme.payment_service.payment.infrastructure;

import org.nextme.payment_service.payment.domain.RefundOrCancel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefundOrCancelRepository extends JpaRepository<RefundOrCancel, UUID>
{
    Optional<RefundOrCancel> findByRefundId(UUID refundId);
}
