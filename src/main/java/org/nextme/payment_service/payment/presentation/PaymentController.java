package org.nextme.payment_service.payment.presentation;

import org.nextme.payment_service.common.success.SuccessResponse;
import org.nextme.payment_service.payment.application.service.PaymentService;
import org.nextme.payment_service.payment.domain.error.PaymentSuccessCode;
import org.nextme.payment_service.payment.infrastructure.PaymentRepository;
import org.nextme.payment_service.payment.infrastructure.toss.dto.PaymentListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentService paymentService, PaymentRepository paymentRepository) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/success")
    public String handlePaymentSuccess(
            @RequestParam String paymentKey,
            @RequestParam UUID orderId,
            @RequestParam long amount) {
        paymentService.confirmPayment(orderId, paymentKey, amount);

        return "redirect:/success.html";
    }

    @PostMapping("/confirm")
    public ResponseEntity<SuccessResponse> confirmPaymentFinal(
            @RequestBody PaymentConfirmationRequest request) { // 승인에 필요한 데이터를 DTO로 받음

        // PaymentService는 이미 confirmPayment(orderId, paymentKey, amount) 로직을 가지고 있습니다.
        // DTO에서 데이터를 추출하여 서비스 호출
        paymentService.confirmPayment(request.getOrderId(), request.getPaymentKey(), request.getAmount());

        return new SuccessResponse(PaymentSuccessCode.PAYMENT_CONFIRM_SUCCESS)
                .toResponseEntity();
    }

    @PostMapping("/init")
    public ResponseEntity<PaymentInitResponse> initializePayment(
            @RequestBody PaymentInitRequest request
    ) {

        PaymentInitResponse response = paymentService.initializePayment(request.getUserId(), request.getProductName()
                , request.getAmount() );
        // HTTP 200 OK와 함께 JSON 응답을 전송

        return ResponseEntity.ok(response);
    }

    /**
     * 결제 실패 시 주문 상태를 FAILED로 업데이트하는 엔드포인트
     */
    @PostMapping("/fail")
    public ResponseEntity<SuccessResponse> handlePaymentFailure(
            @RequestBody PaymentFailureRequest request) {

        paymentService.handlePaymentFailure(request.getOrderId(), request.getCode(), request.getMessage());

        return new SuccessResponse(PaymentSuccessCode.PAYMENT_FAILURE_HANDLED)
                .toResponseEntity();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentDetailResponse> getPaymentDetail(
            @PathVariable String orderId) {
        PaymentDetailResponse response = paymentService.getPaymentDetailByOrderId(orderId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public ResponseEntity<Page<PaymentListResponse>> getPaymentList(
            // 💡 페이징 정보를 스프링이 자동으로 처리하도록 Pageable 객체 사용
            Pageable pageable,
            // 💡 필터링 조건을 RequestParam으로 받습니다.
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status) {

        // 1. 서비스 호출 (Pageable 객체와 필터링 조건을 함께 전달)
        Page<PaymentListResponse> responsePage = paymentService.getPaymentList(pageable, userId, status);

        // 2. HTTP 200 OK와 함께 Page 객체 반환
        return ResponseEntity.ok(responsePage);
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelPayment(@RequestBody PaymentCancelRequest request) {
        paymentService.cancelPayment(
                request.getOrderId(),
                request.getReason(),
                request.getCancelAmount()
        );

        return ResponseEntity.ok("결제 취소 요청이 성공적으로 접수되었으며, 환불 처리 중입니다.");
    }
}
