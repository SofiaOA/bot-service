package com.hedvig.generic.bot.commands;

import java.time.LocalDate;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import lombok.Value;

@Value
public class CreateUserCommand {

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

	@TargetAggregateIdentifier
    public String id;
    private String name;
    private LocalDate birthDate;

    public CreateUserCommand(String id, String name, LocalDate birthDate) {
        System.out.println("CreateUserCommand");
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        System.out.println(this.toString());
    }
}
