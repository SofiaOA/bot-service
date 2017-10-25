package com.hedvig.botService.enteties.userContextHelpers;

import lombok.Value;

@Value
public class BankAccount {
    private String name;
    private String clearingNo;
    private String accountNo;
    private Long amonut;
}
