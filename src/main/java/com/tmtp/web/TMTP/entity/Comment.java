package com.tmtp.web.TMTP.entity;

import java.time.LocalDateTime;

public class Comment {

    private String comment;
    private User user;
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
                ", timestamp=" + timestamp +
                '}';
    }
}
