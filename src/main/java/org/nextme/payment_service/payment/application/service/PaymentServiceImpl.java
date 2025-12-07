package org.nextme.payment_service.payment.application.service;

import lombok.RequiredArgsConstructor;
import org.nextme.payment_service.payment.domain.Payment;
import org.nextme.payment_service.payment.domain.PaymentStatus;
import org.nextme.payment_service.payment.domain.error.PaymentErrorCode;
import org.nextme.payment_service.payment.domain.error.PaymentException;
import org.nextme.payment_service.payment.domain.service.PaymentGatewayService;
import org.nextme.payment_service.payment.domain.valueobject.PaymentConfirmationResponse;
import org.nextme.payment_service.payment.infrastructure.PaymentRepository;
import org.nextme.payment_service.payment.infrastructure.toss.dto.PaymentListResponse;
import org.nextme.payment_service.payment.presentation.PaymentDetailResponse;
import org.nextme.payment_service.payment.presentation.PaymentInitResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.UUID;

@Service // 💡 Spring Bean으로 등록
@RequiredArgsConstructor
// 💡 PaymentService 인터페이스 구현
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentGatewayService paymentGatewayService;
    // ... 다른 의존성 ...

    @Value("${toss.client-key}")
    private String clientKey;

    // ==========================================================
    // 기존 PaymentService 클래스에 있던 모든 메서드 구현 (init, confirm, fail)
    // ==========================================================

    @Transactional
    public void confirmPayment(UUID paymentId, String paymentKey, long amount) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getLocalStatus() != PaymentStatus.REQUESTED) {
            throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_STATUS, "이미 처리되었거나 유효하지 않은 결제 상태입니다.");
        }

        if (payment.getAmount() != amount) {
            throw new PaymentException(PaymentErrorCode.AMOUNT_MISMATCH, String.format("요청 금액(%d)과 주문 금액(%.2f)", amount, payment.getAmount()));
        }

        PaymentConfirmationResponse pgResponse;

        try {
            pgResponse = paymentGatewayService.confirmPayment(paymentKey, paymentId.toString(), amount);
        } catch (PaymentException e) {
            paymentRepository.save(payment);
            throw e;
        }


        payment.confirmSuccess(pgResponse.getPgTransactionId());

        paymentRepository.save(payment);

        // 6. [SAGA 처리]: 결제 성공 이벤트 발행 (다른 도메인 서비스에 알림)
        // eventPublisher.publish(new PaymentConfirmedEvent(payment.getSagaId(), ...));
    }

    public PaymentInitResponse initializePayment(UUID userId, String productName, long amount) {
        UUID sagaId = UUID.randomUUID();
        // 주문번호 - 주문 검증 -> 결제 테이블 기록 -> 결제
        Payment payment = Payment.builder()
                .sagaId(sagaId)
                .userId(userId)
                .productName(productName)
                .amount(amount)
                .refundableAmount(amount)
                .localStatus(PaymentStatus.REQUESTED)
                .isCompensated(false)
                .build();

        payment = paymentRepository.save(payment);

        return new PaymentInitResponse(
                payment.getPaymentId(),
                (long) amount, // amount가 double이라면 long으로 캐스팅 (토스는 정수 사용)
                clientKey
        );
    }

    @Transactional
    public void handlePaymentFailure(UUID orderId, String errorCode, String errorMessage) {
        // 1. DB에서 주문/결제 정보 조회
        Payment payment = paymentRepository.findByPaymentId(orderId)
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        // 2. 상태를 FAILED로 변경하고 실패 정보 기록
        payment.markFailure(errorCode, errorMessage);

        paymentRepository.save(payment);

        // 3. [SAGA 처리]: 다른 서비스에 결제 실패 이벤트 발행 (예: 재고 롤백)
        // eventPublisher.publish(new PaymentFailedEvent(payment.getSagaId(), ...));

        // ==========================================================
        // 💡 4. 단일 주문 조회 구현 (새로 추가된 로직)
        // ==========================================================

    }

    @Override
    public PaymentDetailResponse getPaymentDetailByOrderId(String orderId) {
        // 1. 주문 ID를 DB의 Payment 엔티티 ID 타입(UUID)으로 변환
        // 토스 결제창에서는 orderId를 UUID 형태로 사용했으므로 UUID로 변환합니다.
        UUID paymentId;
        try {
            paymentId = UUID.fromString(orderId);
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 형식의 orderId가 들어왔을 때 처리
            throw new PaymentException(PaymentErrorCode.INVALID_INPUT, "유효하지 않은 주문 ID 형식입니다: " + orderId);
        }

        // 2. DB에서 Payment 엔티티 조회
        // PaymentEntity 대신 Payment 도메인 객체(엔티티)를 사용한다고 가정합니다.
        Payment entity = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND, "해당 주문 ID에 대한 결제 정보를 찾을 수 없습니다."));

        // 3. 엔티티를 응답 DTO (PaymentDetailResponse)로 변환하여 반환
        return PaymentDetailResponse.builder()
                .orderId(entity.getPaymentId().toString())
                .amount(entity.getAmount()) // double/BigDecimal이라면 longValue()로 변환
                .paymentKey(entity.getPaymentKey())
                // 상태는 Enum을 String으로 변환
                .paymentStatus(entity.getLocalStatus().name())
                .orderName(entity.getProductName())
                .requestedAt(entity.getRequestedAt())
                .approvedAt(entity.getApprovedAt())
                .method(entity.getMethod()) // Payment 엔티티에 method 필드가 있어야 함
                .build();
    }

    @Override
    public Page<PaymentListResponse> getPaymentList(Pageable pageable, Long userId, String status) {
        // 1. Repository에서 Page 객체로 데이터 조회 (필터링 로직 포함)
        // 이 부분은 실제 Repository의 커스텀 메서드 또는 Querydsl로 구현해야 합니다.
        Page<PaymentListResponse> entityPage = paymentRepository.findFilteredPayments(pageable, userId, status);

        // 2. 조회된 Entity Page를 Response DTO Page로 변환 (Map)
        return entityPage.map(entity -> PaymentListResponse.builder()
                .orderId(entity.getOrderId())
                .orderName(entity.getOrderName())
                .amount(entity.getAmount())
                .paymentStatus(entity.getPaymentStatus())
                .requestedAt(entity.getRequestedAt())
                .build());
    }
}
