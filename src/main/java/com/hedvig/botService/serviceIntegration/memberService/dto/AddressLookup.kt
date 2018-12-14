package com.hedvig.botService.serviceIntegration.memberService.dto

import org.hibernate.validator.constraints.NotEmpty
import javax.validation.constraints.Digits
import javax.validation.constraints.Size

data class SweAddressRequest(
    @NotEmpty
    @Digits(integer = 12, fraction = 0)
    @Size(min = 12, max = 12)
    val ssn: String,
    @NotEmpty
    val memberId: String
)

data class SweAddressResponse(
    val firstName: String,
    val lastName: String,
    val address: Address?
)
