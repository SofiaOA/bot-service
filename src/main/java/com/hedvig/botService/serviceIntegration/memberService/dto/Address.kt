package com.hedvig.botService.serviceIntegration.memberService.dto


data class Address(
    val street: String?,
    val city: String?,
    val zipCode: String?,
    val apartmentNo: String?,
    val floor: Int = 0
)
