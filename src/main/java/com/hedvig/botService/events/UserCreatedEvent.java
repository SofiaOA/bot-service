package com.hedvig.botService.events;

import java.time.LocalDate;

import lombok.Value;

@Value
public class UserCreatedEvent {

	private String id;
    private String name;
    private LocalDate birthDate;

}
