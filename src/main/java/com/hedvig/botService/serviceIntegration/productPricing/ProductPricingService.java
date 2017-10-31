package com.hedvig.botService.serviceIntegration.productPricing;

import com.hedvig.botService.enteties.userContextHelpers.UserData;
import com.hedvig.botService.serviceIntegration.productPricing.dto.Address;
import com.hedvig.botService.serviceIntegration.productPricing.dto.CalculateQuoteRequest;
import com.hedvig.botService.serviceIntegration.productPricing.dto.Created;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ProductPricingService {

    private final ProductPricingClient client;

    @Autowired
    ProductPricingService(ProductPricingClient client) {

        this.client = client;
    }

    public String createProduct(String memberId, UserData data) {

        CalculateQuoteRequest request = new CalculateQuoteRequest();

        request.setMemberId(memberId);
        request.setBirthDate(data.getBirthDate());
        request.setFirstName(data.getFirstName());
        request.setLastName(data.getFamilyName());
        request.setHouseType(data.getHouseType());
        request.setLivingSpace(data.getLivingSpace());
        request.setPersonsInHouseHold(data.getPersonsInHouseHold());
        request.setGoodToHaveItems(data.getSecurityItems());

        request.setCurrentInsurer(data.getCurrentInsurer());

        Address address = new Address();
        address.setStreet(data.getAddressStreet());
        address.setCity(data.getAddressCity());
        address.setZipCode(data.getAddressZipCode());
        request.setAddress(address);

        Created result = this.client.createQuote(request).getBody();
        return result.id;
    }

    public void quoteAccepted(String hid) {
        this.client.quoteAccepted(hid);
    }

    public void setInsuranceStatus(String hid, String status) {
        this.client.setInsuranceStatus(hid, status);
    }
}
