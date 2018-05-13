package com.tmtp.web.TMTP.dto.exceptions;

public class UserBannedException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "User is banned.";

    public UserBannedException() {
        super(DEFAULT_MESSAGE);
    }

    public UserBannedException(String message) {
        super(message);
    }
}
