package com.hedvig.botService.dataTypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailAdress extends HedvigDataType {

	private Pattern pattern;
	private Matcher matcher;

	private static final String EMAIL_PATTERN =
		"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public EmailAdress() {
		this.errorMessage = "{INPUT} låter inte som en korrekt email adress... Prova igen tack!";
		pattern = Pattern.compile(EMAIL_PATTERN);
	}
	
	@Override
	public boolean validate(String input) {
		matcher = pattern.matcher(input);
		this.errorMessage = this.errorMessage.replace("{INPUT}", input);
		return matcher.matches();
	}

}