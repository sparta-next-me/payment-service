package org.nextme.payment_service.payment.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.nextme.payment_service.payment.domain.error.PaymentErrorCode;
import org.nextme.payment_service.payment.domain.error.PaymentException;
import org.nextme.payment_service.payment.domain.service.PaymentGatewayService;
import org.nextme.payment_service.payment.domain.valueobject.PaymentConfirmationResponse;
import org.nextme.payment_service.payment.domain.valueobject.RefundConfirmationResponse;
import org.nextme.payment_service.payment.infrastructure.toss.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Map;

@Service
@Slf4j
public class TossPaymentGatewayServiceImpl implements PaymentGatewayService {

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
            log.info("토스 Confirm 데이터: {}", response);

            /**
             * 1. 결제 진행전 프론트앤드에서 선점 여부 체크 API
             * 2. 결제 완료시 예약 확정 전 선점 여부 확인, 선점 O -> 결제 취소,  선점  O -> 예약 처리
             */

            return new PaymentConfirmationResponse(
                    response.getPaymentKey(),
                    response.getOrderId(),
                    response.getTotalAmount(),
                    response.getMetadata()
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
    public TossInitialResponse requestInitialPayment(TossInitialRequest request) {
        log.info("PG사 초기 결제 요청 시작. Order ID: {}", request.getOrderId());

        try {
            // 1. WebClient를 사용하여 PG사에 초기 결제 요청 전송
            TossInitialResponse response = webClient.post()
                    .uri("/payments") // 결제 위젯 초기화 엔드포인트
                    .headers(headers -> headers.setBasicAuth(secretKey, "")) // 인증 헤더 설정
                    .body(BodyInserters.fromValue(request)) // TossInitialRequest DTO를 JSON 본문으로 사용
                    .retrieve()

                    // 2. 4xx 클라이언트 오류 처리 (잘못된 주문 정보 등)
                    .onStatus(httpStatus -> httpStatus.is4xxClientError(), clientResponse ->
                            clientResponse.bodyToMono(TossErrorResponse.class).flatMap(error -> {
                                log.error("PG 4xx 초기 요청 오류. Code: {}, Msg: {}", error.getCode(), error.getMessage());
                                return Mono.error(new PaymentException(
                                        PaymentErrorCode.PG_INITIAL_FAILED,
                                        "PG 초기 요청 실패: " + error.getMessage()
                                ));
                            })
                    )
                    // 3. 5xx 서버 오류 처리 (PG사 시스템 문제)
                    .onStatus(httpStatus -> httpStatus.is5xxServerError(), clientResponse -> {
                        log.error("PG 5xx 서버 오류 발생 (초기 요청)");
                        return Mono.error(new PaymentException(PaymentErrorCode.PG_COMMUNICATION_ERROR, "PG사 서버 오류 발생 (초기 요청)"));
                    })

                    .bodyToMono(TossInitialResponse.class) // PG사 응답을 DTO로 매핑
                    .block(); // 동기적으로 처리

            log.info("PG사 초기 결제 요청 성공. Order ID: {}", response.getOrderId());
            return response;

        } catch (PaymentException e) {
            // onStatus에서 발생한 PG 오류 재전파
            throw e;
        } catch (Exception e) {
            // 네트워크 오류 등 예기치 않은 시스템 오류 처리
            log.error("PG 초기 요청 중 통신 오류 발생. Order ID: {}", request.getOrderId(), e);
            throw new PaymentException(PaymentErrorCode.PG_COMMUNICATION_ERROR, "결제 초기 요청 중 알 수 없는 시스템 오류 발생");
        }
    }

    @Override
    public RefundConfirmationResponse requestCancel(String paymentKey, Long cancelAmount, String cancelReason) {
        Map<String, Object> requestBody = Map.of(
                "cancelAmount", (long) cancelAmount,
                "cancelReason", cancelReason
        );

        try {
            TossCancelResponse response = webClient.post()
                    // 💡 수정된 URI 경로: /payments를 명시합니다.
                    .uri("/payments/{paymentKey}/cancel", paymentKey)
                    // 💡 인증 헤더 설정은 기존 코드처럼 Base64 인코딩된 문자열 사용
                    .headers(headers -> headers.setBasicAuth(getBasicAuthHeader()))
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()

                    // 🚨 4xx 클라이언트 오류 처리 로직 추가 (필수)
                    .onStatus(httpStatus -> httpStatus.is4xxClientError(), clientResponse ->
                            clientResponse.bodyToMono(String.class).flatMap(body -> {
                                System.err.println("PG사 4xx 취소 에러 응답 : " + body);
                                // TossErrorResponse DTO를 파싱하는 것이 더 좋습니다. (여기서는 String으로 처리)
                                return Mono.error(new PaymentException(PaymentErrorCode.PG_REFUND_FAILED, "PG 취소 실패: " + body));
                            })
                    )
                    // 🚨 5xx 서버 오류 처리 로직 추가 (필수)
                    .onStatus(httpStatus -> httpStatus.is5xxServerError(), clientResponse -> {
                        System.err.println("PG사 5xx 서버 오류 발생 (취소)");
                        return Mono.error(new PaymentException(PaymentErrorCode.PG_COMMUNICATION_ERROR, "PG사 서버 오류 발생 (취소)"));
                    })

                    .bodyToMono(TossCancelResponse.class)
                    .block();

            log.info("response 확인:{}", response);
            TossCancelResponse.Cancel cancel = response.getCancels().getFirst();
            return new RefundConfirmationResponse(
                    paymentKey,
                    cancel == null ? null : cancel.getCancelId(),
                    cancel == null ? 0L : cancel.getCancelledAmount()
            );

        } catch (PaymentException e) {
            // onStatus에서 발생한 PaymentException을 다시 던짐
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            // 네트워크 오류 등 알 수 없는 오류 처리
            throw new PaymentException(PaymentErrorCode.PG_COMMUNICATION_ERROR, "결제 취소 중 알 수 없는 시스템 오류 발생: " + e.getMessage());
        }
    }
}
