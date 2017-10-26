package com.hedvig.botService.serviceIntegration.productPricing.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CalculateQuoteRequest {

    private String memberId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    private Address address;

    private Float livingSpace;
    private String houseType;
    private String currentInsurer;
    private int personsInHouseHold;

    private List<String> goodToHaveItems;
}