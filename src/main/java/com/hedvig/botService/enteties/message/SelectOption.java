package com.hedvig.botService.enteties.message;

public class SelectOption extends SelectItem  {

	public SelectOption(String text, String value, boolean selected) {
		super(selected, text, value);
    }

	public SelectOption(String text, String value) {
		super(false, text, value);
	}
	public SelectOption(){} // NOTE! All objects need to have a default constructor in order for Jackson to marshall.


}
