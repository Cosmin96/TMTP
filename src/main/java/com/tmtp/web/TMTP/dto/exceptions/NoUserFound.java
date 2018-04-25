package com.tmtp.web.TMTP.dto.exceptions;

public class NoUserFound extends RuntimeException {

    public NoUserFound(String message) {
        super(message);
    }
}