package com.hedvig.botService.web.dto;

public class EventDTO {

    public String type;
    public String value;

    public EventDTO(){}

    public EventDTO(String type, String value) {

        this.type = type;
        this.value = value;
    }
}
