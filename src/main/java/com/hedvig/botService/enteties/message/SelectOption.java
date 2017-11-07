package com.hedvig.botService.enteties.message;

public class SelectOption extends SelectItem  {

	public SelectOption(String string, String value, boolean selected) {
		super(selected, string, value);
    }

	public SelectOption(String string, String value) {
		super(false, string, value);
	}
	public SelectOption(){} // NOTE! All objects need to have a default constructor in order for Jackson to marshall.


}
