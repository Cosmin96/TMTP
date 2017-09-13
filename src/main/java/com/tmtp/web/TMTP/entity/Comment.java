package com.tmtp.web.TMTP.entity;

import java.time.LocalDateTime;

public class Comment {

    private String comment;
    private User user;
    private String date;
    private LocalDateTime timestamp;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "comment='" + comment + '\'' +
                ", user=" + user +
                ", date='" + date + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
