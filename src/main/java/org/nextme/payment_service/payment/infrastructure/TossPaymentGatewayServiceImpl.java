package org.nextme.payment_service.payment.infrastructure;

import org.nextme.payment_service.payment.domain.error.PaymentErrorCode;
import org.nextme.payment_service.payment.domain.error.PaymentException;
import org.nextme.payment_service.payment.domain.service.PaymentGatewayService;
import org.nextme.payment_service.payment.domain.service.RefundGatewayService;
import org.nextme.payment_service.payment.domain.valueobject.PaymentConfirmationResponse;
import org.nextme.payment_service.payment.domain.valueobject.RefundConfirmationResponse;
import org.nextme.payment_service.payment.infrastructure.toss.dto.TossCancelResponse;
import org.nextme.payment_service.payment.infrastructure.toss.dto.TossConfirmResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class TossPaymentGatewayServiceImpl implements PaymentGatewayService, RefundGatewayService {

    @Value("${toss.secret-key}")
    private String secretKey;
    private final WebClient webClient;
    private static final String BASE_URL = "https://api.tosspayments.com/v1";

    public TossPaymentGatewayServiceImpl(WebClient.Builder webClientBuilder) {
        if (this.secretKey != null) {
            this.secretKey = this.secretKey.trim();
        }
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    private String getBasicAuthHeader() {
        return Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
    }


    @Override
    public PaymentConfirmationResponse confirmPayment(String paymentKey, String orderId, long amount) {

        Map<String, Object> requestBody = Map.of(
                "paymentKey", paymentKey,
                "orderId", orderId,
                "amount", amount
        );

        try {
            TossConfirmResponse response = webClient.post()
                    .uri("/payments/confirm")
                    //.headers(headers -> headers.setBasicAuth(getBasicAuthHeader()))
                    .headers(headers -> headers.setBasicAuth(secretKey, ""))
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()

                    .onStatus(httpStatus -> httpStatus.is4xxClientError(), clientResponse ->
                            clientResponse.bodyToMono(String.class).flatMap(body -> {
                                System.err.println("PG사 4xx 에러 응답 : " + body);
                                return Mono.error(new PaymentException(PaymentErrorCode.PG_CONFIRM_FAILED, "PG사 요청 실패 : " + body));
                            })
                    )
                    .onStatus(httpStatus -> httpStatus.is5xxServerError(), clientResponse -> {
                        System.err.println("PG사 5xx 서버 오류 발생"); // 로깅
                        return Mono.error(new PaymentException(PaymentErrorCode.PG_COMMUNICATION_ERROR, "PG사 서버 오류 발생"));
                    })
                    .bodyToMono(TossConfirmResponse.class)
                    .block();

            return new PaymentConfirmationResponse(
                    response.getPaymentKey(),
                    response.getOrderId(),
                    response.getTotalAmount()
            );

        } catch (PaymentException e) {
            // PG사 오류(4xx, 5xx)가 발생하여 onStatus에서 던져진 예외를 여기서 재처리 또는 로깅합니다.
            System.err.println("결제 승인 중 오류 발생 (PG사 응답): " + e.getMessage());
            throw e; // 호출한 곳으로 던짐 (트랜잭션 롤백 유도)

        } catch (Exception e) {
            // WebClient 통신 중 발생할 수 있는 네트워크 오류, 파싱 오류 등 예기치 않은 예외를 처리합니다.
            System.err.println("결제 승인 중 예기치 않은 시스템 오류: " + e.getMessage());
            throw new PaymentException(PaymentErrorCode.PG_COMMUNICATION_ERROR, "결제 승인 중 알 수 없는 시스템 오류 발생");
        }
    }

    @Override
    public RefundConfirmationResponse requestCancel(String paymentKey, double cancelAmount, String cancelReason) {
        Map<String, Object> requestBody = Map.of(
                "cancelAmount", (long) cancelAmount, // PG사는 금액을 long으로 요구
                "reason", cancelReason
        );

        try {
            TossCancelResponse response = webClient.post()
                    .uri("/{paymentKey}/cancel", paymentKey)
                    .headers(headers -> headers.setBasicAuth(getBasicAuthHeader()))
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    // 취소 API도 승인과 동일하게 4xx, 5xx 오류 처리 로직 추가 필요
                    .bodyToMono(TossCancelResponse.class)
                    .block();

            return new RefundConfirmationResponse(
                    paymentKey,
                    response.getCancelId(),
                    response.getCancelledAmount()
            );

        } catch (Exception e) {
            throw new PaymentException(PaymentErrorCode.PG_REFUND_FAILED, e.getMessage());
        }
    }
}
