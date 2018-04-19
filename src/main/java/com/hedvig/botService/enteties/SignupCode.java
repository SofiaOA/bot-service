package com.hedvig.botService.enteties;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@ToString
public class SignupCode {

	private static Logger log = LoggerFactory.getLogger(SignupCode.class);
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	public String email;
	public String code;
	public LocalDateTime date;
	public Boolean used;
	public Boolean active;
	
	public Boolean getUsed() {
		return used;
	}

	public UUID externalToken;
	
	public void setUsed(Boolean used) {
		this.used = used;
	}

	public Boolean getActive() {
		return active;
	}

	public SignupCode(){
		Random r = new Random();
		String code = "";
		for(int i = 0; i<4; i++){
			Character c = (char)(r.nextInt(26) + 'A');
			code+=c.toString();
		}
		for(int i = 0; i<4; i++){
			Integer c = r.nextInt(9);
			code+=c.toString();
		}
		log.debug("SingupCode generated: " + code);
		this.code = code;
		this.used = false;
		this.active = false;
		this.date = LocalDateTime.now();
		this.externalToken = UUID.randomUUID();
	}
	
	public SignupCode(String email) {
		this();
		this.email=email;
	}
}
