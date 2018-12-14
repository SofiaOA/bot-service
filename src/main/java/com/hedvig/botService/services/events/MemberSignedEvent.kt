package com.hedvig.botService.services.events

data class MemberSignedEvent(
    val memberId: String,
    val productType: String
)
