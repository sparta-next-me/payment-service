package org.nextme.payment_service.payment.application.service;

import lombok.RequiredArgsConstructor;
import org.nextme.payment_service.payment.presentation.PaymentCancelledEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_PAYMENT_CANCELLED = "payment.cancelled.v1";

    public void sendPaymentCancelledEvent(String orderId, Long amount, String reason) {
        PaymentCancelledEvent event = new PaymentCancelledEvent(orderId, amount, reason);

        kafkaTemplate.send(TOPIC_PAYMENT_CANCELLED, orderId, event);
    }
}
