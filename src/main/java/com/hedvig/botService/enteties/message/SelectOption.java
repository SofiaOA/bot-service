package com.hedvig.botService.enteties.message;

public class SelectOption extends SelectItem  {
	static final long serialVersionUID = 1L;

	public boolean clearable;

	public SelectOption(String text, String value, boolean selected, boolean clearable) {
		super(selected, text, value);
		this.clearable = clearable;
	}

	public SelectOption(String text, String value, boolean selected) {
		super(selected, text, value);
		clearable = false;
    }

	public SelectOption(String text, String value) {
		super(false, text, value);
		clearable = false;
	}
	public SelectOption(){} // NOTE! All objects need to have a default constructor in order for Jackson to marshall.


}
