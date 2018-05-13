package com.tmtp.web.TMTP.dto.exceptions;

public class NoDataFound extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "No Data found.";

    public NoDataFound() {
        super(DEFAULT_MESSAGE);
    }

    public NoDataFound(String message) {
        super(message);
    }
}