package com.hedvig.botService.serviceIntegration.paymentService;

import com.hedvig.botService.serviceIntegration.paymentService.dto.DirectDebitRequest;
import com.hedvig.botService.serviceIntegration.paymentService.dto.DirectDebitResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class PaymentService {

    private PaymentServiceClient client;

    public PaymentService(PaymentServiceClient client) {
        this.client = client;
    }


    public DirectDebitResponse registerTrustlyDirectDebit(String firstName, String lastName, String ssn, String email, String memberId) {

        DirectDebitRequest dto = new DirectDebitRequest(firstName, lastName, ssn, email, memberId);

        final ResponseEntity<DirectDebitResponse> urlResponseResponseEntity = this.client.registerDirectDebit(dto);

        return urlResponseResponseEntity.getBody();
    }

}
