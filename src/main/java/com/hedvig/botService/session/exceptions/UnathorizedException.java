package com.hedvig.botService.session.exceptions;

public class UnathorizedException extends RuntimeException {

    public UnathorizedException(final String message) {
        super(message);
    }
}
