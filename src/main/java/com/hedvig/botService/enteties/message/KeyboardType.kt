package com.hedvig.botService.enteties.message

import com.fasterxml.jackson.annotation.JsonProperty

enum class KeyboardType {
    @JsonProperty("default")
    DEFAULT,
    @JsonProperty("number-pad")
    NUMBER_PAD,
    @JsonProperty("decimal-pad")
    DECIMAL_PAD,
    @JsonProperty("numeric")
    NUMERIC,
    @JsonProperty("email-address")
    EMAIL_ADDRESS,
    @JsonProperty("phone-pad")
    PHONE_PAD

}
