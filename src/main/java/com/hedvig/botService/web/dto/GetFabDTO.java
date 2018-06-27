package com.hedvig.botService.web.dto;

import lombok.Value;

import java.util.List;

@Value
public class GetFabDTO {
    List<String> choices;
}
