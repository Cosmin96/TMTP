package com.tmtp.web.TMTP.dto.exceptions;

public class BadFormatException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Bad request format.";

    public BadFormatException() {
        super(DEFAULT_MESSAGE);
    }

    public BadFormatException(String message) {
        super(message);
    }
}
