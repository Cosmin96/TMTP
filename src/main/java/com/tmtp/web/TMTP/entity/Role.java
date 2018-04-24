package com.tmtp.web.TMTP.entity;

import org.springframework.data.annotation.Id;

import java.util.Set;

public class Role {
    @Id
    private Long id;
    private String name;
    private Set<User> users;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

}
