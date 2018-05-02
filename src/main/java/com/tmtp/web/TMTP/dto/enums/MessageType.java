package com.tmtp.web.TMTP.dto.enums;

import java.util.Arrays;

public enum MessageType {
    TEXT("Text"), AUDIO("Audio"), VIDEO("Video"), IMAGE("Image");

    private String text;

    MessageType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static MessageType fromText(String text) {
        return Arrays.stream(values())
                .filter(messageType -> messageType.text.equalsIgnoreCase(text))
                .findFirst()
                .orElse(null);
    }
}
