package com.tmtp.web.TMTP.dto.exceptions;

public class UnauthorisedAccess extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Unauthorised Access.";

    public UnauthorisedAccess() {
        super(DEFAULT_MESSAGE);
    }

    public UnauthorisedAccess(String message) {
        super(message);
    }
}