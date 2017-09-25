package com.hedvig.generic.bot.events;

import java.time.LocalDate;

import lombok.Value;

@Value
public class UserCreatedEvent {

	private String id;
    private String name;
    private LocalDate birthDate;

}
