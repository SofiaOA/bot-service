package com.hedvig.botService.serviceIntegration.paymentService;

import com.hedvig.botService.serviceIntegration.paymentService.dto.DirectDebitRequest;
import com.hedvig.botService.serviceIntegration.paymentService.dto.DirectDebitResponse;
import com.hedvig.botService.serviceIntegration.paymentService.dto.OrderInformation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentService {

  private PaymentServiceClient client;

  public PaymentService(PaymentServiceClient client) {
    this.client = client;
  }

  public DirectDebitResponse registerTrustlyDirectDebit(
      String firstName, String lastName, String ssn, String memberId, UUID triggerId) {

    DirectDebitRequest dto =
        new DirectDebitRequest(firstName, lastName, ssn, memberId, triggerId.toString());

    final ResponseEntity<DirectDebitResponse> urlResponseResponseEntity =
        this.client.registerDirectDebit(dto);

    return urlResponseResponseEntity.getBody();
  }

  public OrderInformation getTrustlyOrderInformation(String orderId) {

    final ResponseEntity<OrderInformation> orderInformationResponseEntity =
        this.client.orderInformation(orderId);
    return orderInformationResponseEntity.getBody();
  }
}
