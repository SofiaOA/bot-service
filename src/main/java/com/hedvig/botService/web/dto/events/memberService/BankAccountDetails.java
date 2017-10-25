package com.hedvig.botService.web.dto.events.memberService;

import lombok.Value;

@Value
public class BankAccountDetails {

    private Long amount;
    private String name;
    private String clearingNumber;
    private String number;

}
