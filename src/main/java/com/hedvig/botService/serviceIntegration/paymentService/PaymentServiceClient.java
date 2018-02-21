package com.hedvig.botService.serviceIntegration.paymentService;

import com.hedvig.botService.serviceIntegration.paymentService.dto.DirectDebitRequest;
import com.hedvig.botService.serviceIntegration.paymentService.dto.DirectDebitResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@FeignClient(value = "payment-service", url ="http://localhost:4086")
public interface PaymentServiceClient {

    @RequestMapping(value = "_/trustlyOrder/registerDirectDebit", method = POST, produces = "application/json")
    ResponseEntity<DirectDebitResponse> registerDirectDebit(@RequestBody DirectDebitRequest requestBody);

}