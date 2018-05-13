package com.tmtp.web.TMTP.dto.enums;

public enum TokenType {

    ACCESS_TOKEN("accessToken");

    private final String name;

    private TokenType(String name) {
        this.name = name;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
