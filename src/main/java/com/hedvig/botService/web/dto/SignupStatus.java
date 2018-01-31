package com.hedvig.botService.web.dto;

public class SignupStatus {

	public static enum states {WAITLIST, NOT_FOUND, ACCESS, USED}
    public Integer position = -1;
    public String status;

    public SignupStatus(){}

    public SignupStatus(Integer position, String status) {

    	this.position = position;
        this.status = status;
    }
}
