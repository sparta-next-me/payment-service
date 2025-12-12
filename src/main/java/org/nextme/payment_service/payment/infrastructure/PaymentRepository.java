package org.nextme.payment_service.payment.infrastructure;

import org.nextme.payment_service.payment.domain.Payment;
import org.nextme.payment_service.payment.infrastructure.toss.dto.PaymentListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByPaymentId(UUID paymentId);

    @Query(value = "SELECT p FROM Payment p WHERE p.userId = :userId AND p.localStatus = :status")
    Page<Payment> findFilteredPayments(
            Pageable pageable,
            @Param("userId") Long userId,
            @Param("status") String status // Payment 엔티티의 'paymentStatus' 필드와 매칭될 것으로 추정
    );
}
