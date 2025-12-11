package org.nextme.payment_service.payment.application.service;

import lombok.RequiredArgsConstructor;
import org.nextme.common.event.PaymentConfirmedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_PAYMENT_CONFIRMED = "payment.confirmed.v1";

    public void sendPaymentConfirmedEvent(UUID userId, String paymentKey) {
        PaymentConfirmedEvent event = new PaymentConfirmedEvent(userId.toString(), paymentKey);

        kafkaTemplate.send(TOPIC_PAYMENT_CONFIRMED, paymentKey, event);
    }
}
