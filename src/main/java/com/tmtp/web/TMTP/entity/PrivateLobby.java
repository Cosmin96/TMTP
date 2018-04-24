package com.tmtp.web.TMTP.entity;

import org.springframework.data.annotation.Id;

import java.util.List;

public class PrivateLobby {

    @Id
    private String id;
    private String creator;
    private List<String> joinedUsers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public List<String> getJoinedUsers() {
        return joinedUsers;
    }

    public void setJoinedUsers(List<String> joinedUsers) {
        this.joinedUsers = joinedUsers;
    }
}
