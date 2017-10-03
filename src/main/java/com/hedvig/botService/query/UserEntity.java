package com.hedvig.botService.query;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserEntity {

    @Id
    public String id;

    public String name;

    public LocalDate birthDate;

}
