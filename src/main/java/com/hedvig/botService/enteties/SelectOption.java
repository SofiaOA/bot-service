package com.hedvig.botService.enteties;

public class SelectOption extends SelectItem  {

	public SelectOption(String string, String value, boolean selected) {
		this.id = string;
		this.value = value;
		this.selected = selected;
	}
	public SelectOption(){} // NOTE! All objects need to have a default constructor in order for Jackson to marshall.
	
	public String type = "selection";
	public String id;
	public String value;
	public boolean selected;
	
}
