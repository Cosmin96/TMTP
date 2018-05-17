package com.tmtp.web.TMTP.dto;

public class NewCommentDto {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "NewCommentDto{" + "text='" + text + '\'' + '}';
    }
}
