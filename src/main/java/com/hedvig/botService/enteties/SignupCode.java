package com.hedvig.botService.enteties;

import java.time.LocalDate;
import java.util.Random;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class SignupCode {

	private static Logger log = LoggerFactory.getLogger(SignupCode.class);
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	public String email;
	public String code;
	public LocalDate date;

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
		this.date = LocalDate.now();
	}
	
	public SignupCode(String email) {
		this();
		this.email=email;
	}

	public static void main(String args[]){
		SignupCode sc = new SignupCode();
		
	}
}
