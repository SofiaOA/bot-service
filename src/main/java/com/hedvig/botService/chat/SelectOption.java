package com.hedvig.botService.chat;

public class SelectOption {

	public SelectOption(int id, String value, boolean selected) {
		this.id = id;
		this.value = value;
		this.selected = selected;
	}
	public SelectOption(){} // NOTE! All objects need to have a default constructor in order for Jackson to marshall.
	public int id;
	public String value;
	public boolean selected;
	
}
