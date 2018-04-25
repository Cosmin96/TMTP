package com.tmtp.web.TMTP.entity.exceptions;

public class NoUserFound extends RuntimeException {

    public NoUserFound(String message) {
        super(message);
    }
}