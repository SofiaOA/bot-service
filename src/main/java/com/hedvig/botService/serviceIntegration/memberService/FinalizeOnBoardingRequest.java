package com.hedvig.botService.serviceIntegration.memberService;

import lombok.Data;

@Data
public class FinalizeOnBoardingRequest {

    String memberId;

    String ssn;
    String firstName;
    String lastName;
    String email;

    Address address;

}
