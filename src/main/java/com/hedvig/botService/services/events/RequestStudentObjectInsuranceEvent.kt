package com.hedvig.botService.services.events

data class RequestStudentObjectInsuranceEvent (
    val memberId: String,
    val productType: String
)
