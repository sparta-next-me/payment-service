package org.nextme.payment_service.infrastructure;

import org.junit.jupiter.api.Test;
import org.nextme.payment_service.payment.domain.service.PaymentGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PaymentTest {
    @Autowired
    PaymentGatewayService service;

    @Test
    void paymentTest() {
        service.confirmPayment("tgen_20251204170637SOau0", "MC40OTY3MTQwODY4Mjgz", 50000);
    }
}
