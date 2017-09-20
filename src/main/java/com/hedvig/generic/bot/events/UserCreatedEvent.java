package com.hedvig.generic.bot.events;

import java.time.LocalDate;

import lombok.Value;

@Value
public class UserCreatedEvent {

    public UserCreatedEvent(String id2, String name2, LocalDate birthDate2) {
		this.id = id2;
		this.name = name2;
		this.birthDate = birthDate2;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalDate getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}
	private String id;
    private String name;
    private LocalDate birthDate;

}
