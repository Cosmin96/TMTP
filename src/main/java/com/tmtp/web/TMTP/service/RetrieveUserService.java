package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.entity.User;

import java.util.List;

public interface RetrieveUserService {
    User retrieveUser(String username);

    List<User> retrieveAllUsers();

    void deleteUser(User user);
}
