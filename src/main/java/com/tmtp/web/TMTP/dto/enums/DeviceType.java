package com.tmtp.web.TMTP.dto.enums;

public enum DeviceType {

    IOS("iOs");

    private final String name;

    DeviceType(String name) {
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
