package com.hedvig.botService.serviceIntegration.memberService.dto;

import com.hedvig.botService.serviceIntegration.memberService.dto.Address;
import lombok.Data;

@Data
public class FinalizeOnBoardingRequest {

    private String memberId;

    private String ssn;
    private String firstName;
    private String lastName;
    private String email;

    private Address address;

}
