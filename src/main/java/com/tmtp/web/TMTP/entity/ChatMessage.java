package com.tmtp.web.TMTP.entity;

import com.tmtp.web.TMTP.dto.enums.MessageType;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;

public class ChatMessage {

    @Id
    private String id;
    private String name;
    private String username;
    private String text;
    private DateTime dateTime;
    private MessageType messageType = MessageType.TEXT;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", text='" + text + '\'' +
                ", messageType='" + messageType + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}