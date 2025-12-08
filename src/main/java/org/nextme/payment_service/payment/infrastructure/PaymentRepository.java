package org.nextme.payment_service.payment.infrastructure;

import org.nextme.payment_service.payment.domain.Payment;
import org.nextme.payment_service.payment.infrastructure.toss.dto.PaymentListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByPaymentId(UUID paymentId);

    Page<PaymentListResponse> findFilteredPayments(Pageable pageable, Long userId, String status);
}
