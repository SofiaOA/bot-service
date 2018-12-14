package com.hedvig.botService.serviceIntegration.memberService.dto

data class LookupResponse(
    val firstName: String,
    val lastName: String,
    val address:Address?
)
