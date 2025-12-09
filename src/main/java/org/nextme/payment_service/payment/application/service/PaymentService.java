package org.nextme.payment_service.payment.application.service;

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

public interface PaymentService {

    // 1. 결제 초기화
    PaymentInitResponse initializePayment(UUID userId, String productName, long amount);

    // 2. 결제 승인
    void confirmPayment(UUID paymentId, String paymentKey, long amount);

    // 3. 결제 실패 처리
    void handlePaymentFailure(UUID orderId, String errorCode, String errorMessage);

    // 💡 4. 단일 주문 조회 (새로 추가)
    PaymentDetailResponse getPaymentDetailByOrderId(String orderId);

    // 💡 5. 목록 조회 (새로 추가, Pageable과 필터링 조건은 생략)
    Page<PaymentListResponse> getPaymentList(Pageable pageable, Long userId, String status);
}

/*@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayService paymentGatewayService;

    @Value("${toss.client-key}")
    private String clientKey;

    public PaymentService(PaymentRepository paymentRepository, PaymentGatewayService paymentGatewayService) {
        this.paymentRepository = paymentRepository;
        this.paymentGatewayService = paymentGatewayService;
    }


    *//**
     * 토스 페이먼츠 결제 최종 승인 처리
     *//*
    @Transactional
    public void confirmPayment(UUID paymentId, String paymentKey, long amount){
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

    public PaymentInitResponse initializePayment(UUID userId, String productName, double amount) {
        UUID sagaId = UUID.randomUUID();

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
    }
}*/
