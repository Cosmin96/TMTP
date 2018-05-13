package com.tmtp.web.TMTP.dto.exceptions;

public class NoUserFound extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "User Not Found.";

    public NoUserFound() {
        super(DEFAULT_MESSAGE);
    }

    public NoUserFound(String message) {
        super(message);
    }
}