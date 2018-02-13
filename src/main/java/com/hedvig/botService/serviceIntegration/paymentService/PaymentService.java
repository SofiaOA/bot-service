package com.hedvig.botService.serviceIntegration.paymentService;

import com.hedvig.botService.serviceIntegration.paymentService.dto.SelectAccountDTO;
import com.hedvig.botService.serviceIntegration.paymentService.dto.UrlResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class PaymentService {

    private PaymentServiceClient client;

    public PaymentService(PaymentServiceClient client) {
        this.client = client;
    }


    public UrlResponse startPayment(String firstName, String lastName, String ssn, String email) {

        SelectAccountDTO dto = new SelectAccountDTO();
        dto.firstName = firstName;
        dto.lastName = lastName;
        dto.ssn = ssn;
        dto.email = email;


        final ResponseEntity<UrlResponse> urlResponseResponseEntity = this.client.startPayment(dto);

        return urlResponseResponseEntity.getBody();
    }

}
