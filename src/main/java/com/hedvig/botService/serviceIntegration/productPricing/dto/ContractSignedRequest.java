package com.hedvig.botService.serviceIntegration.productPricing.dto;

import lombok.Value;

@Value
public class ContractSignedRequest {
    String memberId;
    String referenceToken;
}
